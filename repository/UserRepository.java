package org.example.gestionvacaciones.repository;

import org.example.gestionvacaciones.model.Company;
import org.example.gestionvacaciones.model.Role;
import org.example.gestionvacaciones.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByCompanyAndRoleName(Company company, Role.RoleName roleName, Pageable pageable);

    Long countByCompanyAndRoleName(Company company, Role.RoleName roleName);

    @Query("SELECT SUM(u.usedVacationDays) FROM User u WHERE u.company = :company")
    Integer sumUsedVacationDaysByCompany(@Param("company") Company company);

    @Query("SELECT SUM(u.availableVacationDays) FROM User u WHERE u.company = :company")
    Integer sumAvailableVacationDaysByCompany(@Param("company") Company company);
}