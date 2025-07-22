package org.example.gestionvacaciones.controller;

import org.example.gestionvacaciones.dto.request.FormSubmissionRequest;
import org.example.gestionvacaciones.dto.request.FormTemplateRequest;
import org.example.gestionvacaciones.dto.response.ApiResponse;
import org.example.gestionvacaciones.dto.response.FormResponseDto;
import org.example.gestionvacaciones.dto.response.FormTemplateResponse;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.service.FormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormController {

    private final FormService formService;

    @PostMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FormTemplateResponse>> createFormTemplate(
            @Valid @RequestBody FormTemplateRequest request,
            @AuthenticationPrincipal User admin) {
        FormTemplateResponse response = formService.createFormTemplate(request, admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Plantilla de formulario creada exitosamente", response));
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<FormTemplateResponse>>> getFormTemplates(
            @AuthenticationPrincipal User user) {
        List<FormTemplateResponse> templates = formService.getCompanyFormTemplates(user.getId());
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @PutMapping("/templates/{templateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FormTemplateResponse>> updateFormTemplate(
            @PathVariable Long templateId,
            @Valid @RequestBody FormTemplateRequest request,
            @AuthenticationPrincipal User admin) {
        FormTemplateResponse response = formService.updateFormTemplate(templateId, request, admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Plantilla actualizada exitosamente", response));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<FormResponseDto>> submitForm(
            @Valid @RequestBody FormSubmissionRequest request,
            @AuthenticationPrincipal User user) {
        FormResponseDto response = formService.submitForm(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Formulario enviado exitosamente", response));
    }

    @GetMapping("/templates/{templateId}/responses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FormResponseDto>>> getFormResponses(
            @PathVariable Long templateId,
            @AuthenticationPrincipal User admin,
            Pageable pageable) {
        Page<FormResponseDto> responses = formService.getFormResponses(templateId, admin.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/my-responses")
    public ResponseEntity<ApiResponse<List<FormResponseDto>>> getMyFormResponses(
            @AuthenticationPrincipal User user) {
        List<FormResponseDto> responses = formService.getUserFormResponses(user.getId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}