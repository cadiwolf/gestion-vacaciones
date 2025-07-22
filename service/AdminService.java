package org.example.gestionvacaciones.service;

import org.example.gestionvacaciones.dto.response.UserResponse;
import org.example.gestionvacaciones.exception.BadRequestException;
import org.example.gestionvacaciones.exception.ResourceNotFoundException;
import org.example.gestionvacaciones.exception.UnauthorizedException;
import org.example.gestionvacaciones.model.Role;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.model.VacationRequest;
import org.example.gestionvacaciones.model.VacationStatus;
import org.example.gestionvacaciones.repository.UserRepository;
import org.example.gestionvacaciones.repository.VacationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final VacationRequestRepository vacationRequestRepository;

    public Page<UserResponse> getCompanyEmployees(Long adminId, Pageable pageable) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        Page<User> employees = userRepository.findByCompanyAndRoleName(
                admin.getCompany(), Role.RoleName.USER, pageable);

        return employees.map(this::mapToUserResponse);
    }

    public UserResponse getEmployeeDetails(Long adminId, Long employeeId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        if (!employee.getCompany().equals(admin.getCompany())) {
            throw new UnauthorizedException("Solo puedes ver empleados de tu empresa");
        }

        return mapToUserResponse(employee);
    }

    public Map<String, Object> getCompanyStatistics(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        Map<String, Object> stats = new HashMap<>();

        // Estadísticas de empleados
        Long totalEmployees = userRepository.countByCompanyAndRoleName(
                admin.getCompany(), Role.RoleName.USER);
        stats.put("totalEmployees", totalEmployees);

        // Estadísticas de solicitudes
        Long pendingRequests = vacationRequestRepository.countByCompanyAndStatus(
                admin.getCompany(), VacationStatus.PENDING);
        Long approvedRequests = vacationRequestRepository.countByCompanyAndStatus(
                admin.getCompany(), VacationStatus.APPROVED);
        Long rejectedRequests = vacationRequestRepository.countByCompanyAndStatus(
                admin.getCompany(), VacationStatus.REJECTED);

        stats.put("pendingRequests", pendingRequests);
        stats.put("approvedRequests", approvedRequests);
        stats.put("rejectedRequests", rejectedRequests);
        stats.put("totalRequests", pendingRequests + approvedRequests + rejectedRequests);

        // Estadísticas de días de vacaciones
        Integer totalVacationDaysUsed = userRepository.sumUsedVacationDaysByCompany(admin.getCompany());
        Integer totalVacationDaysAvailable = userRepository.sumAvailableVacationDaysByCompany(admin.getCompany());

        stats.put("totalVacationDaysUsed", totalVacationDaysUsed != null ? totalVacationDaysUsed : 0);
        stats.put("totalVacationDaysAvailable", totalVacationDaysAvailable != null ? totalVacationDaysAvailable : 0);

        return stats;
    }

    public Map<String, Object> getEmployeeStatistics(Long adminId, Long employeeId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validateAdminAccess(admin);

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        if (!employee.getCompany().equals(admin.getCompany())) {
            throw new UnauthorizedException("Solo puedes ver estadísticas de empleados de tu empresa");
        }

        Map<String, Object> stats = new HashMap<>();

        stats.put("employeeInfo", mapToUserResponse(employee));

        // Solicitudes del empleado
        Long pendingRequests = vacationRequestRepository.countByUserAndStatus(employee, VacationStatus.PENDING);
        Long approvedRequests = vacationRequestRepository.countByUserAndStatus(employee, VacationStatus.APPROVED);
        Long rejectedRequests = vacationRequestRepository.countByUserAndStatus(employee, VacationStatus.REJECTED);

        stats.put("requestCounts", Map.of(
                "pending", pendingRequests,
                "approved", approvedRequests,
                "rejected", rejectedRequests,
                "total", pendingRequests + approvedRequests + rejectedRequests
        ));

        // Porcentaje de vacaciones utilizadas
        int totalDays = employee.getCompany().getVacationDaysPerYear();
        int usedDays = employee.getUsedVacationDays();
        double usagePercentage = totalDays > 0 ? (double) usedDays / totalDays * 100 : 0;

        stats.put("vacationUsage", Map.of(
                "totalDays", totalDays,
                "usedDays", usedDays,
                "availableDays", employee.getAvailableVacationDays(),
                "usagePercentage", Math.round(usagePercentage * 100.0) / 100.0
        ));

        return stats;
    }

    private void validateAdminAccess(User admin) {
        if (!admin.getRole().getName().equals(Role.RoleName.ADMIN)) {
            throw new UnauthorizedException("Solo los administradores pueden acceder a esta funcionalidad");
        }

        if (admin.getCompany() == null) {
            throw new BadRequestException("El administrador debe estar asociado a una empresa");
        }
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