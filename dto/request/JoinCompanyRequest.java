package org.example.gestionvacaciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinCompanyRequest {

    @NotBlank(message = "El código de invitación es obligatorio")
    private String invitationCode;

    // Lombok @Data genera automáticamente los getters y setters
    // Pero si prefieres hacerlo manual:

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}