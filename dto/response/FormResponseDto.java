package org.example.gestionvacaciones.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FormResponseDto {
    private Long id;
    private String templateName;
    private String userFullName;
    private String userEmail;
    private Map<String, Object> responses;
    private LocalDateTime submittedAt;
}