package com.eyeglasses.eyeglasses_store.service.payment;

import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.payment.PaymentTransaction;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.payment.PaymentTransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;
    private final VNPayService vnPayService;
    private final MoMoService moMoService;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentTransactionRepository paymentTransactionRepository,
            OrderRepository orderRepository,
            VNPayService vnPayService,
            MoMoService moMoService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.orderRepository = orderRepository;
        this.vnPayService = vnPayService;
        this.moMoService = moMoService;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public Map<String, Object> createPayment(UUID orderId, String paymentMethod, String ipAddress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getGrandTotal() == null) {
            throw new RuntimeException("Order grand total is null");
        }

        long amount = order.getGrandTotal().longValue();
        String orderInfo = "Thanh toan don hang " + orderId;

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setAmount(order.getGrandTotal());
        transaction.setStatus("PENDING");

        Map<String, Object> response = new HashMap<>();

        try {
            if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, orderInfo, ipAddress);
                transaction.setPaymentUrl(paymentUrl);
                paymentTransactionRepository.save(transaction);

                response.put("paymentUrl", paymentUrl);
                response.put("transactionId", transaction.getId());
                response.put("method", "VNPAY");

            } else if ("MOMO".equalsIgnoreCase(paymentMethod)) {
                // MoMo temporarily disabled
                throw new RuntimeException("MoMo payment is currently unavailable. Please use VNPAY or COD.");
                /*
                 * Map<String, Object> momoResponse = moMoService.createPayment(orderId, amount,
                 * orderInfo);
                 * 
                 * String paymentUrl = (String) momoResponse.get("payUrl");
                 * transaction.setPaymentUrl(paymentUrl);
                 * transaction.setTransactionId((String) momoResponse.get("requestId"));
                 * transaction.setResponseData(objectMapper.writeValueAsString(momoResponse));
                 * paymentTransactionRepository.save(transaction);
                 * 
                 * response.put("paymentUrl", paymentUrl);
                 * response.put("transactionId", transaction.getId());
                 * response.put("method", "MOMO");
                 */

            } else if ("COD".equalsIgnoreCase(paymentMethod)) {
                transaction.setStatus("PENDING");
                transaction.setTransactionId("COD_" + UUID.randomUUID());
                paymentTransactionRepository.save(transaction);

                response.put("message", "Đơn hàng sẽ được thanh toán khi nhận hàng");
                response.put("transactionId", transaction.getId());
                response.put("method", "COD");

            } else {
                throw new RuntimeException("Unsupported payment method: " + paymentMethod);
            }

            response.put("success", true);
            return response;

        } catch (Exception e) {
            log.error("Error creating payment", e);
            transaction.setStatus("FAILED");
            transaction.setResponseData(e.getMessage());
            paymentTransactionRepository.save(transaction);
            throw new RuntimeException("Cannot create payment: " + e.getMessage());
        }
    }

    @Transactional
    public void handleVNPayReturn(Map<String, String> params) {
        boolean isValid = vnPayService.verifyPaymentSignature(params);
        if (!isValid) {
            throw new RuntimeException("Invalid VNPay signature");
        }

        String orderIdStr = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");

        UUID orderId = UUID.fromString(orderIdStr);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentTransaction transaction = paymentTransactionRepository
                .findByOrderIdAndStatus(orderId, "PENDING")
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        transaction.setTransactionId(transactionNo);

        try {
            transaction.setResponseData(objectMapper.writeValueAsString(params));
        } catch (Exception e) {
            log.error("Error serializing VNPay response", e);
        }

        if ("00".equals(responseCode)) {
            transaction.setStatus("SUCCESS");
            order.setPaymentStatus("PAID");
            order.setStatus("CONFIRMED");
        } else {
            transaction.setStatus("FAILED");
            order.setPaymentStatus("FAILED");
        }

        paymentTransactionRepository.save(transaction);
        orderRepository.save(order);
    }

    @Transactional
    public void handleMoMoReturn(Map<String, String> params) {
        boolean isValid = moMoService.verifySignature(params);
        if (!isValid) {
            throw new RuntimeException("Invalid MoMo signature");
        }

        String orderIdStr = params.get("orderId");
        String resultCode = params.get("resultCode");
        String transId = params.get("transId");

        UUID orderId = UUID.fromString(orderIdStr);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentTransaction transaction = paymentTransactionRepository
                .findByOrderIdAndStatus(orderId, "PENDING")
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        transaction.setTransactionId(transId);

        try {
            transaction.setResponseData(objectMapper.writeValueAsString(params));
        } catch (Exception e) {
            log.error("Error serializing MoMo response", e);
        }

        if ("0".equals(resultCode)) {
            transaction.setStatus("SUCCESS");
            order.setPaymentStatus("PAID");
            order.setStatus("CONFIRMED");
        } else {
            transaction.setStatus("FAILED");
            order.setPaymentStatus("FAILED");
        }

        paymentTransactionRepository.save(transaction);
        orderRepository.save(order);
    }

    public PaymentTransaction getTransactionByOrderId(UUID orderId) {
        return paymentTransactionRepository.findByOrderIdAndStatus(orderId, "SUCCESS")
                .or(() -> paymentTransactionRepository.findByOrderIdAndStatus(orderId, "PENDING"))
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));
    }
}
