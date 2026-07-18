package com.taskflow.auth_service.controller;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.service.AuthService;
import com.taskflow.auth_service.service.TokenBlacklistService;
import com.taskflow.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        long expirationMillis = jwtUtil.extractExpiration(token).getTime() - System.currentTimeMillis();
        tokenBlacklistService.blacklistToken(token, expirationMillis);
        return ResponseEntity.ok("Logged out successfully");
    }
}
