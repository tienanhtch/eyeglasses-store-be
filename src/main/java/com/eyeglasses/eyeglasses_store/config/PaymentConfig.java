package com.eyeglasses.eyeglasses_store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private VnPay vnpay = new VnPay();
    private MoMo momo = new MoMo();

    public VnPay getVnpay() {
        return vnpay;
    }

    public void setVnpay(VnPay vnpay) {
        this.vnpay = vnpay;
    }

    public MoMo getMomo() {
        return momo;
    }

    public void setMomo(MoMo momo) {
        this.momo = momo;
    }

    public static class VnPay {
        private String tmnCode;
        private String hashSecret;
        private String apiUrl;
        private String returnUrl;
        private String ipnUrl;

        public String getTmnCode() {
            return tmnCode;
        }

        public void setTmnCode(String tmnCode) {
            this.tmnCode = tmnCode;
        }

        public String getHashSecret() {
            return hashSecret;
        }

        public void setHashSecret(String hashSecret) {
            this.hashSecret = hashSecret;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getIpnUrl() {
            return ipnUrl;
        }

        public void setIpnUrl(String ipnUrl) {
            this.ipnUrl = ipnUrl;
        }
    }

    public static class MoMo {
        private String partnerCode;
        private String accessKey;
        private String secretKey;
        private String apiUrl;
        private String returnUrl;
        private String ipnUrl;

        public String getPartnerCode() {
            return partnerCode;
        }

        public void setPartnerCode(String partnerCode) {
            this.partnerCode = partnerCode;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getIpnUrl() {
            return ipnUrl;
        }

        public void setIpnUrl(String ipnUrl) {
            this.ipnUrl = ipnUrl;
        }
    }
}
