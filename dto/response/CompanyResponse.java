package org.example.gestionvacaciones.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String invitationCode;
    private Integer vacationDaysPerYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}