package org.example.gestionvacaciones.service;

import lombok.RequiredArgsConstructor;
import org.example.gestionvacaciones.dto.request.CompanyCreateRequest;
import org.example.gestionvacaciones.dto.request.JoinCompanyRequest;
import org.example.gestionvacaciones.dto.response.CompanyResponse;
import org.example.gestionvacaciones.model.Company;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.exception.ResourceNotFoundException;
import org.example.gestionvacaciones.exception.BadRequestException;
import org.example.gestionvacaciones.repository.CompanyRepository;
import org.example.gestionvacaciones.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request, Long userId) {
        // Verificar que el usuario no tenga ya una empresa
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getCompany() != null) {
            throw new BadRequestException("Ya perteneces a una empresa");
        }

        // Crear la empresa
        Company company = new Company();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setVacationDaysPerYear(request.getVacationDaysPerYear()); // Nuevo campo
        company.setCreatedBy(user);

        Company savedCompany = companyRepository.save(company);

        // Asignar la empresa al usuario
        user.setCompany(savedCompany);
        userRepository.save(user);

        return convertToResponse(savedCompany);
    }

    @Transactional
    public CompanyResponse joinCompany(JoinCompanyRequest request, Long userId) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar que el usuario no tenga ya una empresa
        if (user.getCompany() != null) {
            throw new BadRequestException("Ya perteneces a una empresa");
        }

        // Buscar la empresa por código de invitación
        Company company = companyRepository.findByInvitationCode(request.getInvitationCode())
                .orElseThrow(() -> new ResourceNotFoundException("Código de invitación inválido"));

        // Asignar la empresa al usuario
        user.setCompany(company);
        userRepository.save(user);

        return convertToResponse(company);
    }

    public CompanyResponse getCompanyDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getCompany() == null) {
            throw new ResourceNotFoundException("No perteneces a ninguna empresa");
        }

        return convertToResponse(user.getCompany());
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        return convertToResponse(company);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa no encontrada con ID: " + id);
        }
        companyRepository.deleteById(id);
    }

    private CompanyResponse convertToResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setDescription(company.getDescription());
        response.setAddress(company.getAddress());
        response.setPhone(company.getPhone());
        response.setEmail(company.getEmail());
        response.setInvitationCode(company.getInvitationCode());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());
        return response;
    }
}