package com.eyeglasses.eyeglasses_store.util;

import java.util.UUID;

public class UuidUtil {

    /**
     * Parse UUID from string, supporting both standard UUID format and hex format with 0x prefix
     * @param uuidStr UUID string (e.g., "550e8400-e29b-41d4-a716-446655440000" or "0x550e8400e29b41d4a716446655440000")
     * @return UUID object
     * @throws IllegalArgumentException if the string is not a valid UUID format
     */
    public static UUID parseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID string cannot be null or empty");
        }

        // Handle hex format with 0x prefix (MySQL BINARY(16) format)
        if (uuidStr.startsWith("0x")) {
            String hex = uuidStr.substring(2);
            if (hex.length() == 32) {
                // Convert hex to UUID format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
                String uuidFormatted = hex.substring(0, 8) + "-" +
                        hex.substring(8, 12) + "-" +
                        hex.substring(12, 16) + "-" +
                        hex.substring(16, 20) + "-" +
                        hex.substring(20, 32);
                return UUID.fromString(uuidFormatted);
            } else {
                throw new IllegalArgumentException("Invalid hex UUID format: expected 32 hex characters after 0x");
            }
        } else {
            // Standard UUID format
            return UUID.fromString(uuidStr);
        }
    }
}

