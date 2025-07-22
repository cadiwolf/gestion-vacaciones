package org.example.gestionvacaciones.controller;

import org.example.gestionvacaciones.dto.response.ApiResponse;
import org.example.gestionvacaciones.dto.response.CalendarResponse;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/my-calendar")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getMyCalendar(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month,
            @AuthenticationPrincipal User user) {

        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        List<CalendarResponse> calendar = calendarService.getUserCalendar(user.getId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(calendar));
    }

    @GetMapping("/my-calendar/year")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getMyYearCalendar(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user) {

        if (year == 0) year = LocalDate.now().getYear();

        List<CalendarResponse> calendar = calendarService.getUserYearCalendar(user.getId(), year);
        return ResponseEntity.ok(ApiResponse.success(calendar));
    }

    @GetMapping("/company-calendar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getCompanyCalendar(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month,
            @AuthenticationPrincipal User user) {

        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        List<CalendarResponse> calendar = calendarService.getCompanyCalendar(user.getId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(calendar));
    }
}