package org.example.gestionvacaciones.controller;

import org.example.gestionvacaciones.dto.request.CompanyCreateRequest;
import org.example.gestionvacaciones.dto.request.JoinCompanyRequest;
import org.example.gestionvacaciones.dto.response.ApiResponse;
import org.example.gestionvacaciones.dto.response.CompanyResponse;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @Valid @RequestBody CompanyCreateRequest request,
            @AuthenticationPrincipal User user) {
        CompanyResponse response = companyService.createCompany(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Empresa creada exitosamente", response));
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<CompanyResponse>> joinCompany(
            @Valid @RequestBody JoinCompanyRequest request,
            @AuthenticationPrincipal User user) {
        CompanyResponse response = companyService.joinCompany(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Te has unido a la empresa exitosamente", response));
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyDetails(@AuthenticationPrincipal User user) {
        CompanyResponse response = companyService.getCompanyDetails(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}