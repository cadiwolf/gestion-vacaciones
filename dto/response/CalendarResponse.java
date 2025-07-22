package org.example.gestionvacaciones.dto.response;

import org.example.gestionvacaciones.model.VacationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarResponse {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userName;
    private String userEmail;
    private String reason;
    private Integer totalDays;
    private VacationStatus status;
    private String color;
    private boolean allDay = true;

    public void setStatus(VacationStatus status) {
        this.status = status;
        // Asignar colores basados en el estado
        switch (status) {
            case APPROVED:
                this.color = "#28a745"; // Verde
                break;
            case PENDING:
                this.color = "#ffc107"; // Amarillo
                break;
            case REJECTED:
                this.color = "#dc3545"; // Rojo
                break;
            default:
                this.color = "#6c757d"; // Gris
        }
    }
}