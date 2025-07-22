package org.example.gestionvacaciones.dto.response;

import org.example.gestionvacaciones.model.VacationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VacationResponse {
    private Long id;
    private String userFullName;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private String reason;
    private VacationStatus status;
    private String adminComments;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String reviewedByName;
}