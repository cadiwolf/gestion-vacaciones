package org.example.gestionvacaciones.controller;

import org.example.gestionvacaciones.dto.request.VacationRequestDto;
import org.example.gestionvacaciones.dto.response.ApiResponse;
import org.example.gestionvacaciones.dto.response.VacationResponse;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.service.VacationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VacationController {

    private final VacationService vacationService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<VacationResponse>> createVacationRequest(
            @Valid @RequestBody VacationRequestDto request,
            @AuthenticationPrincipal User user) {
        VacationResponse response = vacationService.createVacationRequest(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Solicitud de vacaciones creada exitosamente", response));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<Page<VacationResponse>>> getMyVacationRequests(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<VacationResponse> response = vacationService.getUserVacationRequests(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/company-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<VacationResponse>>> getCompanyVacationRequests(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<VacationResponse> response = vacationService.getCompanyVacationRequests(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VacationResponse>> approveVacationRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comments,
            @AuthenticationPrincipal User user) {
        VacationResponse response = vacationService.approveVacationRequest(requestId, user.getId(), comments);
        return ResponseEntity.ok(ApiResponse.success("Solicitud aprobada exitosamente", response));
    }

    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VacationResponse>> rejectVacationRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comments,
            @AuthenticationPrincipal User user) {
        VacationResponse response = vacationService.rejectVacationRequest(requestId, user.getId(), comments);
        return ResponseEntity.ok(ApiResponse.success("Solicitud rechazada", response));
    }
}