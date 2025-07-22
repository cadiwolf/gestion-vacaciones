package org.example.gestionvacaciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FormSubmissionRequest {
    @NotBlank(message = "El ID del template es obligatorio")
    private String templateId;

    @NotEmpty(message = "Las respuestas son obligatorias")
    private Map<String, Object> responses;
}