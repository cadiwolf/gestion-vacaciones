package org.example.gestionvacaciones.service;

import org.example.gestionvacaciones.dto.request.FormSubmissionRequest;
import org.example.gestionvacaciones.dto.request.FormTemplateRequest;
import org.example.gestionvacaciones.dto.response.FormResponseDto;
import org.example.gestionvacaciones.dto.response.FormTemplateResponse;
import org.example.gestionvacaciones.exception.BadRequestException;
import org.example.gestionvacaciones.exception.ResourceNotFoundException;
import org.example.gestionvacaciones.exception.UnauthorizedException;
import org.example.gestionvacaciones.model.*;
import org.example.gestionvacaciones.repository.FormResponseRepository;
import org.example.gestionvacaciones.repository.FormTemplateRepository;
import org.example.gestionvacaciones.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FormService {

    private final FormTemplateRepository formTemplateRepository;
    private final FormResponseRepository formResponseRepository;
    private final UserRepository userRepository;

    public FormTemplateResponse createFormTemplate(FormTemplateRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        FormTemplate template = new FormTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setFields(request.getFields());
        template.setActive(request.isActive());
        template.setCompany(admin.getCompany());

        FormTemplate saved = formTemplateRepository.save(template);
        return mapToFormTemplateResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FormTemplateResponse> getCompanyFormTemplates(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getCompany() == null) {
            throw new BadRequestException("El usuario debe pertenecer a una empresa");
        }

        List<FormTemplate> templates = formTemplateRepository.findByCompanyAndIsActiveTrue(user.getCompany());
        return templates.stream()
                .map(this::mapToFormTemplateResponse)
                .collect(Collectors.toList());
    }

    public FormResponseDto submitForm(FormSubmissionRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        FormTemplate template = formTemplateRepository.findById(Long.parseLong(request.getTemplateId()))
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla de formulario no encontrada"));

        if (!template.isActive()) {
            throw new BadRequestException("La plantilla de formulario no está activa");
        }

        if (!template.getCompany().equals(user.getCompany())) {
            throw new UnauthorizedException("No tienes acceso a esta plantilla de formulario");
        }

        FormResponse response = new FormResponse();
        response.setTemplate(template);
        response.setUser(user);
        response.setResponses(request.getResponses());

        FormResponse saved = formResponseRepository.save(response);
        return mapToFormResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<FormResponseDto> getFormResponses(Long templateId, Long adminId, Pageable pageable) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        FormTemplate template = formTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla de formulario no encontrada"));

        if (!template.getCompany().equals(admin.getCompany())) {
            throw new UnauthorizedException("No tienes acceso a esta plantilla de formulario");
        }

        Page<FormResponse> responses = formResponseRepository.findByTemplateOrderBySubmittedAtDesc(template, pageable);
        return responses.map(this::mapToFormResponseDto);
    }

    @Transactional(readOnly = true)
    public List<FormResponseDto> getUserFormResponses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<FormResponse> responses = formResponseRepository.findByUserOrderBySubmittedAtDesc(user);
        return responses.stream()
                .map(this::mapToFormResponseDto)
                .collect(Collectors.toList());
    }

    public FormTemplateResponse updateFormTemplate(Long templateId, FormTemplateRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        FormTemplate template = formTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla de formulario no encontrada"));

        if (!template.getCompany().equals(admin.getCompany())) {
            throw new UnauthorizedException("No tienes acceso a esta plantilla de formulario");
        }

        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setFields(request.getFields());
        template.setActive(request.isActive());

        FormTemplate saved = formTemplateRepository.save(template);
        return mapToFormTemplateResponse(saved);
    }

    private void validateAdminAccess(User admin) {
        if (!admin.getRole().getName().equals(Role.RoleName.ADMIN)) {
            throw new UnauthorizedException("Solo los administradores pueden acceder a esta funcionalidad");
        }

        if (admin.getCompany() == null) {
            throw new BadRequestException("El administrador debe estar asociado a una empresa");
        }
    }

    private FormTemplateResponse mapToFormTemplateResponse(FormTemplate template) {
        FormTemplateResponse response = new FormTemplateResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setFields(template.getFields());
        response.setActive(template.isActive());
        response.setCompanyName(template.getCompany().getName());
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        return response;
    }

    private FormResponseDto mapToFormResponseDto(FormResponse response) {
        FormResponseDto dto = new FormResponseDto();
        dto.setId(response.getId());
        dto.setTemplateName(response.getTemplate().getName());
        dto.setUserFullName(response.getUser().getFirstName() + " " + response.getUser().getLastName());
        dto.setUserEmail(response.getUser().getEmail());
        dto.setResponses(response.getResponses());
        dto.setSubmittedAt(response.getSubmittedAt());
        return dto;
    }
}