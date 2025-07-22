package org.example.gestionvacaciones.controller;

import org.example.gestionvacaciones.dto.response.ApiResponse;
import org.example.gestionvacaciones.dto.response.UserResponse;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@AuthenticationPrincipal User user) {
        UserResponse profile = userService.getUserProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserDashboard(@AuthenticationPrincipal User user) {
        Map<String, Object> dashboard = userService.getUserDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}