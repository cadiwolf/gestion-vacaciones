package org.example.gestionvacaciones.service;

import org.example.gestionvacaciones.dto.response.UserResponse;
import org.example.gestionvacaciones.exception.ResourceNotFoundException;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return mapToUserResponse(user);
    }

    public Map<String, Object> getUserDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Map<String, Object> dashboard = new HashMap<>();

        // Información del usuario
        dashboard.put("userInfo", mapToUserResponse(user));

        // Estadísticas de vacaciones
        if (user.getCompany() != null) {
            int totalDays = user.getCompany().getVacationDaysPerYear();
            int usedDays = user.getUsedVacationDays();
            int availableDays = user.getAvailableVacationDays();
            double usagePercentage = totalDays > 0 ? (double) usedDays / totalDays * 100 : 0;

            Map<String, Object> vacationStats = new HashMap<>();
            vacationStats.put("totalDays", totalDays);
            vacationStats.put("usedDays", usedDays);
            vacationStats.put("availableDays", availableDays);
            vacationStats.put("usagePercentage", Math.round(usagePercentage * 100.0) / 100.0);

            dashboard.put("vacationStats", vacationStats);
        }

        // Información de la empresa
        if (user.getCompany() != null) {
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("name", user.getCompany().getName());
            companyInfo.put("code", user.getCompany().getInvitationCode()); // Cambio aquí
            companyInfo.put("vacationDaysPerYear", user.getCompany().getVacationDaysPerYear());

            dashboard.put("companyInfo", companyInfo);
        }

        return dashboard;
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole().getName().name());
        response.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);
        response.setAvailableVacationDays(user.getAvailableVacationDays());
        response.setUsedVacationDays(user.getUsedVacationDays());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}