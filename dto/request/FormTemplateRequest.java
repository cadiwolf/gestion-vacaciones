package org.example.gestionvacaciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FormTemplateRequest {
    @NotBlank(message = "El nombre del formulario es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotEmpty(message = "Los campos son obligatorios")
    private List<Map<String, Object>> fields;

    private boolean isActive = true;
}