package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.USERS_BASE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Lưu ý: Tạm thời nhận userId từ query để trả full data (vì chưa gắn JWT)
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<Map<String, Object>>> addresses(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(userService.listAddresses(userId));
    }

    @PostMapping("/addresses")
    public ResponseEntity<Map<String, Object>> createAddress(@RequestParam("userId") UUID userId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(userService.createAddress(userId, body));
    }

    @GetMapping("/saved-filters")
    public ResponseEntity<List<Map<String, Object>>> savedFilters(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(userService.listSavedFilters(userId));
    }

    private record SaveFilterRequest(String name, String paramsJson) {
    }

    @PostMapping("/saved-filters")
    public ResponseEntity<Map<String, Object>> saveFilter(@RequestParam("userId") UUID userId,
            @RequestBody SaveFilterRequest body) {
        return ResponseEntity.ok(userService.saveFilter(userId, body.name(), body.paramsJson()));
    }
}
