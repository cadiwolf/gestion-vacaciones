package org.example.gestionvacaciones.repository;

import org.example.gestionvacaciones.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {
    Page<VacationRequest> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<VacationRequest> findByCompanyOrderByCreatedAtDesc(Company company, Pageable pageable);

    Long countByCompanyAndStatus(Company company, VacationStatus status);

    Long countByUserAndStatus(User user, VacationStatus status);

    @Query("SELECT CASE WHEN COUNT(vr) > 0 THEN true ELSE false END FROM VacationRequest vr " +
            "WHERE vr.user.id = :userId " +
            "AND vr.status IN :statuses " +
            "AND ((vr.startDate <= :endDate AND vr.endDate >= :startDate))")
    boolean existsByUserAndDateRangeAndStatus(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("statuses") List<VacationStatus> statuses);

    @Query("SELECT vr FROM VacationRequest vr " +
            "WHERE vr.user = :user " +
            "AND ((vr.startDate <= :endDate AND vr.endDate >= :startDate))")
    List<VacationRequest> findByUserAndDateRangeBetween(@Param("user") User user,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VacationRequest vr " +
            "WHERE vr.company = :company " +
            "AND ((vr.startDate <= :endDate AND vr.endDate >= :startDate))")
    List<VacationRequest> findByCompanyAndDateRangeBetween(@Param("company") Company company,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
}