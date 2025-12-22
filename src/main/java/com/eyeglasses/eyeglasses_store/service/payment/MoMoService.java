package com.eyeglasses.eyeglasses_store.service.payment;

import com.eyeglasses.eyeglasses_store.config.PaymentConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MoMoService {

    private static final Logger log = LoggerFactory.getLogger(MoMoService.class);
    private final PaymentConfig paymentConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MoMoService(PaymentConfig paymentConfig) {
        this.paymentConfig = paymentConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> createPayment(UUID orderId, long amount, String orderInfo) {
        try {
            String requestId = UUID.randomUUID().toString();
            String orderIdStr = orderId.toString();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", paymentConfig.getMomo().getPartnerCode());
            requestBody.put("partnerName", "EyeGlasses Store");
            requestBody.put("storeId", "EyeGlassesStore");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderIdStr);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", paymentConfig.getMomo().getReturnUrl());
            requestBody.put("ipnUrl", paymentConfig.getMomo().getIpnUrl());
            requestBody.put("lang", "vi");
            requestBody.put("extraData", "");
            requestBody.put("requestType", "captureWallet");
            requestBody.put("autoCapture", true);

            // Create signature
            String rawSignature = "accessKey=" + paymentConfig.getMomo().getAccessKey() +
                    "&amount=" + amount +
                    "&extraData=" +
                    "&ipnUrl=" + paymentConfig.getMomo().getIpnUrl() +
                    "&orderId=" + orderIdStr +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + paymentConfig.getMomo().getPartnerCode() +
                    "&redirectUrl=" + paymentConfig.getMomo().getReturnUrl() +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";

            String signature = hmacSHA256(paymentConfig.getMomo().getSecretKey(), rawSignature);
            requestBody.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    paymentConfig.getMomo().getApiUrl(),
                    entity,
                    String.class);

            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            log.info("MoMo payment created: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Error creating MoMo payment", e);
            throw new RuntimeException("Cannot create MoMo payment", e);
        }
    }

    public boolean verifySignature(Map<String, String> params) {
        try {
            String receivedSignature = params.get("signature");
            String rawSignature = "accessKey=" + paymentConfig.getMomo().getAccessKey() +
                    "&amount=" + params.get("amount") +
                    "&extraData=" + params.getOrDefault("extraData", "") +
                    "&message=" + params.get("message") +
                    "&orderId=" + params.get("orderId") +
                    "&orderInfo=" + params.get("orderInfo") +
                    "&orderType=" + params.get("orderType") +
                    "&partnerCode=" + params.get("partnerCode") +
                    "&payType=" + params.get("payType") +
                    "&requestId=" + params.get("requestId") +
                    "&responseTime=" + params.get("responseTime") +
                    "&resultCode=" + params.get("resultCode") +
                    "&transId=" + params.get("transId");

            String calculatedSignature = hmacSHA256(paymentConfig.getMomo().getSecretKey(), rawSignature);
            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            log.error("Error verifying MoMo signature", e);
            return false;
        }
    }

    private String hmacSHA256(String key, String data) throws Exception {
        Mac hmac256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac256.init(secretKey);
        byte[] result = hmac256.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder(2 * result.length);
        for (byte b : result) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
