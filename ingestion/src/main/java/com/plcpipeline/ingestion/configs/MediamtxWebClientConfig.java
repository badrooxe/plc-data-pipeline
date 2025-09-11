package com.plcpipeline.ingestion.configs;

import com.plcpipeline.ingestion.mtx.config.MediamtxProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.*;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.core.publisher.Mono;

@Configuration
public class MediamtxWebClientConfig {

    @Bean
    @Qualifier("mediamtxApi")
    public WebClient mediamtxApi(WebClient.Builder builder, MediamtxProperties props) {
        // Basic auth filter (not required if API is localhost-only; added for future use)
        ExchangeFilterFunction basicAuthFilter = ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (props.getApiUser() != null && !props.getApiUser().isBlank()) {
                ClientRequest authorized = ClientRequest.from(clientRequest)
                        .headers(h -> h.setBasicAuth(props.getApiUser(), props.getApiPass()))
                        .build();
                return Mono.just(authorized);
            }
            return Mono.just(clientRequest);
        });

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        HttpClient httpClient = HttpClient.create().compress(true);

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(props.getHost())
                .filter(basicAuthFilter)
                .exchangeStrategies(strategies)
                .build();
    }
}
