package com.eyeglasses.eyeglasses_store.constant;

/**
 * Constants for API endpoints and configuration
 */
public class ApiConstants {

    // API Base Path (context-path is already set in application.yaml)
    public static final String API_BASE_PATH = "";

    // Authentication endpoints
    public static final String AUTH_BASE = "/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String REFRESH_TOKEN = "/refresh-token";
    public static final String LOGOUT = "/logout";

    // User endpoints
    public static final String USERS_BASE = "/users";
    public static final String USER_PROFILE = "/profile";
    public static final String USER_UPDATE = "/update";

    // Product endpoints
    public static final String PRODUCTS_BASE = "/products";
    public static final String PRODUCT_SEARCH = "/search";
    public static final String PRODUCT_CATEGORIES = "/categories";

    // Order endpoints
    public static final String ORDERS_BASE = "/orders";
    public static final String ORDER_HISTORY = "/history";
    public static final String ORDER_STATUS = "/status";

    // Admin endpoints
    public static final String ADMIN_BASE = "/admin";
    public static final String ADMIN_USERS = "/users";
    public static final String ADMIN_PRODUCTS = "/products";
    public static final String ADMIN_ORDERS = "/orders";

    // Public endpoints
    public static final String PUBLIC_BASE = "/public";
    public static final String PUBLIC_PRODUCTS = "/products";
    public static final String PUBLIC_CATEGORIES = "/categories";

    private ApiConstants() {
        // Utility class
    }
}
