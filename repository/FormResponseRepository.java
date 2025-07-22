package org.example.gestionvacaciones.repository;

import org.example.gestionvacaciones.model.FormResponse;
import org.example.gestionvacaciones.model.FormTemplate;
import org.example.gestionvacaciones.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormResponseRepository extends JpaRepository<FormResponse, Long> {
    Page<FormResponse> findByTemplateOrderBySubmittedAtDesc(FormTemplate template, Pageable pageable);

    List<FormResponse> findByUserOrderBySubmittedAtDesc(User user);

    List<FormResponse> findByTemplateAndUserOrderBySubmittedAtDesc(FormTemplate template, User user);

    Long countByTemplate(FormTemplate template);

    Long countByUser(User user);
}