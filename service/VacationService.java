package org.example.gestionvacaciones.service;

import org.example.gestionvacaciones.dto.request.VacationRequestDto;
import org.example.gestionvacaciones.dto.response.VacationResponse;
import org.example.gestionvacaciones.exception.BadRequestException;
import org.example.gestionvacaciones.exception.ResourceNotFoundException;
import org.example.gestionvacaciones.exception.UnauthorizedException;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.model.VacationRequest;
import org.example.gestionvacaciones.model.VacationStatus;
import org.example.gestionvacaciones.repository.UserRepository;
import org.example.gestionvacaciones.repository.VacationRequestRepository;
import org.example.gestionvacaciones.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VacationService {

    private final VacationRequestRepository vacationRequestRepository;
    private final UserRepository userRepository;

    public VacationResponse createVacationRequest(VacationRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getCompany() == null) {
            throw new BadRequestException("El usuario debe pertenecer a una empresa para solicitar vacaciones");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        int totalDays = DateUtils.calculateBusinessDays(request.getStartDate(), request.getEndDate());

        if (totalDays > user.getAvailableVacationDays()) {
            throw new BadRequestException("No tienes suficientes días de vacaciones disponibles");
        }

        // Verificar solapamiento con solicitudes existentes
        boolean hasOverlap = vacationRequestRepository.existsByUserAndDateRangeAndStatus(
                user.getId(), request.getStartDate(), request.getEndDate(),
                List.of(VacationStatus.PENDING, VacationStatus.APPROVED)
        );

        if (hasOverlap) {
            throw new BadRequestException("Ya tienes una solicitud en ese rango de fechas");
        }

        VacationRequest vacationRequest = new VacationRequest();
        vacationRequest.setUser(user);
        vacationRequest.setCompany(user.getCompany());
        vacationRequest.setStartDate(request.getStartDate());
        vacationRequest.setEndDate(request.getEndDate());
        vacationRequest.setTotalDays(totalDays);
        vacationRequest.setReason(request.getReason());
        vacationRequest.setStatus(VacationStatus.PENDING);

        VacationRequest saved = vacationRequestRepository.save(vacationRequest);
        return mapToVacationResponse(saved);
    }

    public VacationResponse approveVacationRequest(Long requestId, Long adminId, String comments) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de vacaciones no encontrada"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        validateAdminAccess(admin, vacationRequest);

        if (vacationRequest.getStatus() != VacationStatus.PENDING) {
            throw new BadRequestException("Solo se pueden aprobar solicitudes pendientes");
        }

        vacationRequest.setStatus(VacationStatus.APPROVED);
        vacationRequest.setAdminComments(comments);
        vacationRequest.setReviewedAt(LocalDateTime.now());
        vacationRequest.setReviewedBy(admin);

        // Actualizar días disponibles del usuario
        User user = vacationRequest.getUser();
        user.setAvailableVacationDays(user.getAvailableVacationDays() - vacationRequest.getTotalDays());
        user.setUsedVacationDays(user.getUsedVacationDays() + vacationRequest.getTotalDays());
        userRepository.save(user);

        VacationRequest saved = vacationRequestRepository.save(vacationRequest);
        return mapToVacationResponse(saved);
    }

    public VacationResponse rejectVacationRequest(Long requestId, Long adminId, String comments) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de vacaciones no encontrada"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        validateAdminAccess(admin, vacationRequest);

        if (vacationRequest.getStatus() != VacationStatus.PENDING) {
            throw new BadRequestException("Solo se pueden rechazar solicitudes pendientes");
        }

        vacationRequest.setStatus(VacationStatus.REJECTED);
        vacationRequest.setAdminComments(comments);
        vacationRequest.setReviewedAt(LocalDateTime.now());
        vacationRequest.setReviewedBy(admin);

        VacationRequest saved = vacationRequestRepository.save(vacationRequest);
        return mapToVacationResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<VacationResponse> getUserVacationRequests(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Page<VacationRequest> requests = vacationRequestRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return requests.map(this::mapToVacationResponse);
    }

    @Transactional(readOnly = true)
    public Page<VacationResponse> getCompanyVacationRequests(Long adminId, Pageable pageable) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (admin.getCompany() == null) {
            throw new BadRequestException("El usuario debe pertenecer a una empresa");
        }

        Page<VacationRequest> requests = vacationRequestRepository
                .findByCompanyOrderByCreatedAtDesc(admin.getCompany(), pageable);
        return requests.map(this::mapToVacationResponse);
    }

    private void validateAdminAccess(User admin, VacationRequest vacationRequest) {
        if (!admin.getRole().getName().equals(org.example.gestionvacaciones.model.Role.RoleName.ADMIN)) {
            throw new UnauthorizedException("Solo los administradores pueden revisar solicitudes");
        }

        if (!admin.getCompany().equals(vacationRequest.getCompany())) {
            throw new UnauthorizedException("Solo puedes revisar solicitudes de tu empresa");
        }
    }

    private VacationResponse mapToVacationResponse(VacationRequest request) {
        VacationResponse response = new VacationResponse();
        response.setId(request.getId());
        response.setUserFullName(request.getUser().getFirstName() + " " + request.getUser().getLastName());
        response.setUserEmail(request.getUser().getEmail());
        response.setStartDate(request.getStartDate());
        response.setEndDate(request.getEndDate());
        response.setTotalDays(request.getTotalDays());
        response.setReason(request.getReason());
        response.setStatus(request.getStatus());
        response.setAdminComments(request.getAdminComments());
        response.setCreatedAt(request.getCreatedAt());
        response.setReviewedAt(request.getReviewedAt());
        response.setReviewedByName(request.getReviewedBy() != null ?
                request.getReviewedBy().getFirstName() + " " + request.getReviewedBy().getLastName() : null);
        return response;
    }
}