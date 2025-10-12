package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.cart.Cart;
import com.eyeglasses.eyeglasses_store.entity.cart.CartItem;
import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.order.OrderItem;
import com.eyeglasses.eyeglasses_store.entity.order.Payment;
import com.eyeglasses.eyeglasses_store.repository.cart.CartItemRepository;
import com.eyeglasses.eyeglasses_store.repository.cart.CartRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderItemRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.order.PaymentRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AddressRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final StoreRepository storeRepository;

    public OrderService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            AddressRepository addressRepository,
            StoreRepository storeRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.addressRepository = addressRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Map<String, Object> createOrderFromCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty())
            throw new IllegalStateException("Cart is empty");

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderNo(generateOrderNo());
        order.setStatus("PENDING");
        order.setFulfillment("DELIVERY");

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem it : items) {
            BigDecimal unitPrice = it.getVariant().getSalePrice() != null ? it.getVariant().getSalePrice()
                    : it.getVariant().getRetailPrice();
            if (it.getCustomPrice() != null)
                unitPrice = it.getCustomPrice();
            BigDecimal lensPrice = it.getLensPackage() != null ? Optional.ofNullable(it.getLensPackage().getSalePrice())
                    .orElse(it.getLensPackage().getRetailPrice()) : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(it.getQty())).add(lensPrice);
            subtotal = subtotal.add(lineTotal);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setVariant(it.getVariant());
            oi.setNameSnapshot(it.getVariant().getProduct().getName());
            oi.setSkuSnapshot(it.getVariant().getSku());
            oi.setPriceUnit(unitPrice);
            oi.setQty(it.getQty());
            oi.setLensPackage(it.getLensPackage());
            oi.setPrescription(it.getPrescription());
            oi.setLineTotal(lineTotal);
            orderItems.add(oi);
        }

        order.setSubtotal(subtotal);
        order.setGrandTotal(subtotal); // tạm thời chưa tính thuế/ship/discount
        Order saved = orderRepository.save(order);
        for (OrderItem oi : orderItems) {
            orderItemRepository.save(oi);
        }
        // clear cart items
        for (CartItem it : items) {
            cartItemRepository.delete(it);
        }
        return toOrderPayload(saved);
    }

    @Transactional
    public Map<String, Object> initVnpay(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider("VNPAY");
        payment.setAmount(order.getGrandTotal());
        payment.setStatus("INIT");
        payment.setTxRef("VNPAY-" + order.getOrderNo() + "-" + System.currentTimeMillis());
        Payment saved = paymentRepository.save(payment);

        String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?txnRef=" + saved.getTxRef() + "&amount="
                + order.getGrandTotal();
        return Map.of(
                "orderId", order.getId(),
                "orderNo", order.getOrderNo(),
                "amount", order.getGrandTotal(),
                "provider", "VNPAY",
                "txRef", saved.getTxRef(),
                "paymentUrl", paymentUrl);
    }

    @Transactional
    public Map<String, Object> vnpayReturn(String txRef, String status) {
        Payment payment = paymentRepository.findByTxRef(txRef).orElseThrow();
        if (Objects.equals(status, "success")) {
            payment.setStatus("PAID");
            payment.setPaidAt(OffsetDateTime.now());
            payment.getOrder().setStatus("PAID");
        } else {
            payment.setStatus("FAILED");
            payment.getOrder().setStatus("FAILED");
        }
        paymentRepository.save(payment);
        return Map.of(
                "txRef", payment.getTxRef(),
                "status", payment.getStatus(),
                "orderNo", payment.getOrder().getOrderNo());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toOrderPayload).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderDetail(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        return toOrderPayload(order);
    }

    private Map<String, Object> toOrderPayload(Order o) {
        List<OrderItem> items = o.getId() != null ? orderItemRepository.findByOrderId(o.getId()) : List.of();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getId());
        m.put("orderNo", o.getOrderNo());
        m.put("status", o.getStatus());
        m.put("fulfillment", o.getFulfillment());
        m.put("subtotal", o.getSubtotal());
        m.put("discountTotal", o.getDiscountTotal());
        m.put("shippingFee", o.getShippingFee());
        m.put("taxTotal", o.getTaxTotal());
        m.put("grandTotal", o.getGrandTotal());
        m.put("createdAt", o.getCreatedAt());
        m.put("updatedAt", o.getUpdatedAt());
        m.put("items", items.stream().map(oi -> Map.of(
                "id", oi.getId(),
                "nameSnapshot", oi.getNameSnapshot(),
                "skuSnapshot", oi.getSkuSnapshot(),
                "priceUnit", oi.getPriceUnit(),
                "qty", oi.getQty(),
                "lineTotal", oi.getLineTotal())).toList());
        return m;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }

    @Transactional
    public Map<String, Object> createOrderFromCartAdvanced(UUID userId, UUID shippingAddressId, UUID billingAddressId,
            String fulfillment, UUID storeId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty())
            throw new IllegalStateException("Cart is empty");

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderNo(generateOrderNo());
        order.setStatus("PENDING");
        order.setFulfillment(fulfillment != null ? fulfillment : "DELIVERY");

        if (shippingAddressId != null) {
            addressRepository.findById(shippingAddressId).ifPresent(order::setShippingAddress);
        }
        if (billingAddressId != null) {
            addressRepository.findById(billingAddressId).ifPresent(order::setBillingAddress);
        }
        if (storeId != null) {
            storeRepository.findById(storeId).ifPresent(order::setStore);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem it : items) {
            BigDecimal unitPrice = it.getVariant().getSalePrice() != null ? it.getVariant().getSalePrice()
                    : it.getVariant().getRetailPrice();
            if (it.getCustomPrice() != null)
                unitPrice = it.getCustomPrice();
            BigDecimal lensPrice = it.getLensPackage() != null ? Optional.ofNullable(it.getLensPackage().getSalePrice())
                    .orElse(it.getLensPackage().getRetailPrice()) : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(it.getQty())).add(lensPrice);
            subtotal = subtotal.add(lineTotal);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setVariant(it.getVariant());
            oi.setNameSnapshot(it.getVariant().getProduct().getName());
            oi.setSkuSnapshot(it.getVariant().getSku());
            oi.setPriceUnit(unitPrice);
            oi.setQty(it.getQty());
            oi.setLensPackage(it.getLensPackage());
            oi.setPrescription(it.getPrescription());
            oi.setLineTotal(lineTotal);
            orderItems.add(oi);
        }

        // Simple fees for demo
        BigDecimal shippingFee = "PICKUP".equalsIgnoreCase(order.getFulfillment()) ? BigDecimal.ZERO
                : new BigDecimal("30000");
        BigDecimal taxTotal = subtotal.multiply(new BigDecimal("0.08")).setScale(2,
                java.math.RoundingMode.HALF_UP);

        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setTaxTotal(taxTotal);
        order.setGrandTotal(subtotal.add(shippingFee).add(taxTotal));

        Order saved = orderRepository.save(order);
        for (OrderItem oi : orderItems) {
            orderItemRepository.save(oi);
        }
        for (CartItem it : items) {
            cartItemRepository.delete(it);
        }
        return toOrderPayload(saved);
    }

    @Transactional
    public Map<String, Object> initVnpaySigned(UUID orderId, String returnUrl) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider("VNPAY");
        payment.setAmount(order.getGrandTotal());
        payment.setStatus("INIT");
        payment.setTxRef("VNPAY-" + order.getOrderNo() + "-" + System.currentTimeMillis());
        Payment saved = paymentRepository.save(payment);

        String payBase = System.getenv().getOrDefault("VNPAY_PAY_URL",
                "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        String tmnCode = System.getenv().getOrDefault("VNPAY_TMN_CODE", "DEMO");
        String secret = System.getenv().getOrDefault("VNPAY_HASH_SECRET", "SECRET");
        String ret = returnUrl != null ? returnUrl
                : System.getenv().getOrDefault("VNPAY_RETURN_URL", "http://localhost:3000/payment/vnpay-return");

        java.util.SortedMap<String, String> params = new java.util.TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount",
                order.getGrandTotal().multiply(new BigDecimal("100")).setScale(0).toPlainString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", saved.getTxRef());
        params.put("vnp_OrderInfo", "Thanh toan don " + order.getOrderNo());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", ret);
        params.put("vnp_CreateDate",
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now()));

        String query = params.entrySet().stream()
                .map(e -> e.getKey() + "="
                        + java.net.URLEncoder.encode(e.getValue(), java.nio.charset.StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("&"));
        String hashData = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(java.util.stream.Collectors.joining("&"));
        String secureHash = hmacSHA512(secret, hashData);
        String paymentUrl = payBase + "?" + query + "&vnp_SecureHashType=HmacSHA512&vnp_SecureHash=" + secureHash;

        return Map.of(
                "orderId", order.getId(),
                "orderNo", order.getOrderNo(),
                "amount", order.getGrandTotal(),
                "provider", "VNPAY",
                "txRef", saved.getTxRef(),
                "paymentUrl", paymentUrl);
    }

    private String hmacSHA512(String secret, String data) {
        try {
            javax.crypto.Mac hmac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec key = new javax.crypto.spec.SecretKeySpec(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(key);
            byte[] bytes = hmac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
