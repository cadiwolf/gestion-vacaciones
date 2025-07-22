package org.example.gestionvacaciones.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class FormTemplateResponse {
    private Long id;
    private String name;
    private String description;
    private List<Map<String, Object>> fields;
    private boolean isActive;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}