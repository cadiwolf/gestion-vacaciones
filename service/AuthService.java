package org.example.gestionvacaciones.service;

import org.example.gestionvacaciones.dto.request.LoginRequest;
import org.example.gestionvacaciones.dto.request.RegisterRequest;
import org.example.gestionvacaciones.dto.response.AuthResponse;
import org.example.gestionvacaciones.dto.response.UserResponse;
import org.example.gestionvacaciones.exception.BadRequestException;
import org.example.gestionvacaciones.model.Role;
import org.example.gestionvacaciones.model.User;
import org.example.gestionvacaciones.repository.RoleRepository;
import org.example.gestionvacaciones.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new BadRequestException("Rol de usuario no encontrado"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(userRole);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token, mapToUserResponse(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, mapToUserResponse(user));
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