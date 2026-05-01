package com.cryptic.service;

import com.cryptic.dto.AuthResponse;
import com.cryptic.dto.LoginRequest;
import com.cryptic.dto.RegisterRequest;
import com.cryptic.model.Role;
import com.cryptic.model.User;
import com.cryptic.repo.UserRepository;
import com.cryptic.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + req.email());
        }

        Role role = parseRole(req.role());
        User user = new User(
                req.name(), req.email(), req.phone(),
                passwordEncoder.encode(req.password()),
                role
        );
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), role.name());
        return new AuthResponse(token, user.getEmail(), role.name());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    private Role parseRole(String role) {
        try {
            return role == null ? Role.CUSTOMER : Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}

