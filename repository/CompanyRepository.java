package org.example.gestionvacaciones.repository;

import org.example.gestionvacaciones.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByInvitationCode(String invitationCode);
}