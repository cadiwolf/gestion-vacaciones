package org.example.gestionvacaciones.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String companyName;
    private Integer availableVacationDays;
    private Integer usedVacationDays;
    private LocalDateTime createdAt;
}