package com.plcpipeline.ingestion.hik.service;

import com.plcpipeline.ingestion.hik.dto.PreviewUrlDto;
import com.plcpipeline.ingestion.mtx.client.MediamtxClient;
import com.plcpipeline.ingestion.mtx.config.MediamtxProperties;
import lombok.*;
import org.slf4j.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class HikWebrtcProvisionService {

    private static final Logger log = LoggerFactory.getLogger(HikWebrtcProvisionService.class);

    private final HikPreviewService hikPreviewService;
    private final MediamtxClient mediamtxClient;
    private final MediamtxProperties props;

    private final ConcurrentMap<String, ProvisionEntry> active = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        cleaner.scheduleAtFixedRate(this::cleanupStalePaths, 30, 30, TimeUnit.SECONDS);
    }

    public record ProvisionEntry(String engineCode, String rtsp, Instant createdAt, Instant lastSeen) {}

    public String provisionForEngine(String engineCode) {
        PreviewUrlDto preview = hikPreviewService.getPreviewUrlsForEngine(engineCode);
        //List<String> urls = preview.getUrls();
        // Sample list of public RTSP URLs for testing
        List<String> urls = List.of(
            "rtsp://192.168.100.10:554/sms/HCPEurl/commonvideobiz_uSoWMlrY%2F%2BdlqnIVD%2BZu7QOr6Mb1Jhm9Rsd1ntjHkojD62KZU3mno%2BEP%2F9l4kJ%2FbmhQPmsCHrQq1xzo1yqcENbSiVHdo31wk6neFxgx%2FprAPz1BTwAyYYlrKsr0ZewRKHWNwGwW%2FUDedmlLlmrOs5M9sCdCPa8a6RO8FAE7YhfD0ZAimpje9Geqpgkz6mU8y%2BBQHW0sxEaLU%2BJqQDtatTLcUP%2FGKPQmwXvwogWxtjKE%3D"
        );
        if (urls == null || urls.isEmpty()) {
            throw new IllegalArgumentException("No cameras available for engine " + engineCode);
        }

        String raw = urls.get(0);
        String rtsp = extractRtsp(raw);

        Optional<String> existing = findExistingPathForRtsp(rtsp);
        if (existing.isPresent()) {
            String pathName = existing.get();
            touch(pathName);
            return buildReaderUrl(pathName);
        }

        String pathName = engineCode + "-" + UUID.randomUUID().toString().substring(0,8);

        boolean ok = mediamtxClient.addPath(pathName, raw);
        if (!ok) {
            throw new RuntimeException("Failed to create mediamtx path");
        }

        ProvisionEntry entry = new ProvisionEntry(engineCode, rtsp, Instant.now(), Instant.now());
        active.put(pathName, entry);

        log.info("Provisioned {} -> {} (engine={})", pathName, rtsp, engineCode);
        return buildReaderUrl(pathName);
    }

    public boolean unprovision(String pathName) {
        boolean ok = mediamtxClient.removePath(pathName);
        active.remove(pathName);
        return ok;
    }

    private Optional<String> findExistingPathForRtsp(String rtsp) {
        return active.entrySet().stream()
                .filter(e -> e.getValue().rtsp().equals(rtsp))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private void touch(String pathName) {
        active.computeIfPresent(pathName, (k, v) -> new ProvisionEntry(v.engineCode(), v.rtsp(), v.createdAt(), Instant.now()));
    }

    private String extractRtsp(String raw) {
        if (raw == null) return null;
        int idx = raw.indexOf("rtsp://");
        if (idx >= 0) {
            String sub = raw.substring(idx).trim();
            sub = sub.replaceAll("[\"',]+$", "");
            return sub;
        }
        return raw;
    }

    private String buildReaderUrl(String pathName) {
        return props.getReaderHost() + "/" + pathName + "/index.m3u8";
    }

    private void cleanupStalePaths() {
        try {
            Instant cutoff = Instant.now().minusSeconds(props.getPathTtlSeconds());
            List<String> toDelete = new ArrayList<>();
            for (var entry : active.entrySet()) {
                if (entry.getValue().lastSeen().isBefore(cutoff)) {
                    toDelete.add(entry.getKey());
                }
            }
            for (String p : toDelete) {
                log.info("Cleaning up stale mediamtx path {}", p);
                mediamtxClient.removePath(p);
                active.remove(p);
            }
        } catch (Exception ex) {
            log.error("Error during mediamtx cleanup: {}", ex.getMessage());
        }
    }

    public Map<String, ProvisionEntry> listActive() {
        return Collections.unmodifiableMap(active);
    }
}
