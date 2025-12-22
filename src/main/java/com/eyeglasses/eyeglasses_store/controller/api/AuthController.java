package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiConstants.AUTH_BASE)
public class AuthController {

    private record RegisterRequest(String email, String password, String fullName, String phone) {
    }

    private record LoginRequest(String email, String password) {
    }

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(ApiConstants.REGISTER)
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest body) {
        try {
            Map<String, Object> payload = authService.register(body.email(), body.password(), body.fullName(), body.phone());
            return ResponseEntity.ok(payload);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()
            ));
        }
    }

    @PostMapping(ApiConstants.LOGIN)
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest body) {
        try {
            Map<String, Object> payload = authService.login(body.email(), body.password());
            return ResponseEntity.ok(payload);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "error", ex.getMessage()
            ));
        }
    }
}
