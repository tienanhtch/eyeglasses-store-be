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
        return ResponseEntity.ok(authService.register(body.email(), body.password(), body.fullName(), body.phone()));
    }

    @PostMapping(ApiConstants.LOGIN)
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest body) {
        return ResponseEntity.ok(authService.login(body.email(), body.password()));
    }
}
