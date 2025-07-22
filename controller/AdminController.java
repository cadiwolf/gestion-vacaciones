package org.example.gestionvacaciones.controller;

import org.example.gestionvacaciones.dto.response.ApiResponse;
import org.example.gestionvacaciones.dto.response.UserResponse;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getCompanyEmployees(
            @AuthenticationPrincipal User admin,
            Pageable pageable) {
        Page<UserResponse> employees = adminService.getCompanyEmployees(admin.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<ApiResponse<UserResponse>> getEmployeeDetails(
            @PathVariable Long employeeId,
            @AuthenticationPrincipal User admin) {
        UserResponse employee = adminService.getEmployeeDetails(admin.getId(), employeeId);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompanyStatistics(
            @AuthenticationPrincipal User admin) {
        Map<String, Object> statistics = adminService.getCompanyStatistics(admin.getId());
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/employees/{employeeId}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployeeStatistics(
            @PathVariable Long employeeId,
            @AuthenticationPrincipal User admin) {
        Map<String, Object> statistics = adminService.getEmployeeStatistics(admin.getId(), employeeId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}