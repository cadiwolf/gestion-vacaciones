package org.example.gestionvacaciones.service;

import org.example.gestionvacaciones.dto.response.CalendarResponse;
import org.example.gestionvacaciones.exception.ResourceNotFoundException;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.model.VacationRequest;
import org.example.gestionvacaciones.model.VacationStatus;
import org.example.gestionvacaciones.repository.UserRepository;
import org.example.gestionvacaciones.repository.VacationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final UserRepository userRepository;
    private final VacationRequestRepository vacationRequestRepository;

    public List<CalendarResponse> getUserCalendar(Long userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<VacationRequest> vacations = vacationRequestRepository
                .findByUserAndDateRangeBetween(user, startDate, endDate);

        return vacations.stream()
                .filter(request -> request.getStatus() == VacationStatus.APPROVED)
                .map(this::mapToCalendarResponse)
                .collect(Collectors.toList());
    }

    public List<CalendarResponse> getCompanyCalendar(Long adminId, int year, int month) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (admin.getCompany() == null) {
            throw new ResourceNotFoundException("El usuario no pertenece a ninguna empresa");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<VacationRequest> vacations = vacationRequestRepository
                .findByCompanyAndDateRangeBetween(admin.getCompany(), startDate, endDate);

        return vacations.stream()
                .filter(request -> request.getStatus() == VacationStatus.APPROVED)
                .map(this::mapToCalendarResponse)
                .collect(Collectors.toList());
    }

    public List<CalendarResponse> getUserYearCalendar(Long userId, int year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<VacationRequest> vacations = vacationRequestRepository
                .findByUserAndDateRangeBetween(user, startDate, endDate);

        return vacations.stream()
                .filter(request -> request.getStatus() == VacationStatus.APPROVED)
                .map(this::mapToCalendarResponse)
                .collect(Collectors.toList());
    }

    private CalendarResponse mapToCalendarResponse(VacationRequest request) {
        CalendarResponse response = new CalendarResponse();
        response.setId(request.getId());
        response.setTitle(request.getUser().getFirstName() + " " + request.getUser().getLastName() + " - Vacaciones");
        response.setStartDate(request.getStartDate());
        response.setEndDate(request.getEndDate());
        response.setUserName(request.getUser().getFirstName() + " " + request.getUser().getLastName());
        response.setUserEmail(request.getUser().getEmail());
        response.setReason(request.getReason());
        response.setTotalDays(request.getTotalDays());
        response.setStatus(request.getStatus());
        return response;
    }
}