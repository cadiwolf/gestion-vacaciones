package org.example.gestionvacaciones.repository;

import org.example.gestionvacaciones.model.Company;
import org.example.gestionvacaciones.model.FormTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormTemplateRepository extends JpaRepository<FormTemplate, Long> {
    List<FormTemplate> findByCompanyAndIsActiveTrue(Company company);

    List<FormTemplate> findByCompanyOrderByCreatedAtDesc(Company company);

    Optional<FormTemplate> findByIdAndCompany(Long id, Company company);

    Long countByCompanyAndIsActiveTrue(Company company);

    boolean existsByNameAndCompany(String name, Company company);
}