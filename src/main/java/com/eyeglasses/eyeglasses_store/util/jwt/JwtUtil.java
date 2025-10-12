package com.eyeglasses.eyeglasses_store.util.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtUtil {

    public static String generateToken(String subject, List<String> roles, String secret, long expiresMillis) {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        long now = Instant.now().getEpochSecond();
        long exp = Instant.now().plusMillis(expiresMillis).getEpochSecond();
        String rolesArray = roles != null ? ("[\"" + String.join("\",\"", roles) + "\"]") : "[]";
        String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d,\"roles\":%s}",
                escapeJson(subject), now, exp, rolesArray);
        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha256(secret, header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public static Map<String, Object> validate(String token, String secret) {
        String[] parts = token.split("\\.");
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid token");
        String expected = hmacSha256(secret, parts[0] + "." + parts[1]);
        if (!expected.equals(parts[2]))
            throw new IllegalArgumentException("Invalid signature");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> claims = parseSimpleJson(payloadJson);
        long now = Instant.now().getEpochSecond();
        Object exp = claims.get("exp");
        if (exp instanceof Number && now > ((Number) exp).longValue())
            throw new IllegalArgumentException("Token expired");
        return claims;
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hmacSha256(String secret, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(sig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // naive JSON parser for flat claims
    private static Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> map = new HashMap<>();
        String trimmed = json.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
            if (!trimmed.isEmpty()) {
                for (String part : trimmed.split(",")) {
                    String[] kv = part.split(":", 2);
                    if (kv.length == 2) {
                        String key = kv[0].trim().replaceAll("^\"|\"$", "");
                        String val = kv[1].trim();
                        if (val.matches("^-?\\d+$"))
                            map.put(key, Long.parseLong(val));
                        else if (val.equals("true") || val.equals("false"))
                            map.put(key, Boolean.parseBoolean(val));
                        else
                            map.put(key, val.replaceAll("^\"|\"$", ""));
                    }
                }
            }
        }
        return map;
    }
}
