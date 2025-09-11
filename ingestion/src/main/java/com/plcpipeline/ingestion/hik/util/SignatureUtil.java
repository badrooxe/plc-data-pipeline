package com.plcpipeline.ingestion.hik.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public final class SignatureUtil {

    private SignatureUtil() {}

    public static String generateSignature(String method, String accept, String contentType, String urlPath, String secret) {
        try {
            String stringToSign = String.join("\n", method, accept, contentType, urlPath);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate HMAC signature", ex);
        }
    }
}
