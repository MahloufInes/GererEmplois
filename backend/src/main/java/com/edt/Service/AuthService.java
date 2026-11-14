
package com.edt.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edt.dto.LoginRequest;
import com.edt.dto.LoginResponse;
import com.edt.entity.Utilisateur;
import com.edt.repository.UtilisateurRepository;
import com.edt.security.JwtService;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 1. Find user by email
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Email ou mot de passe incorrect."
                        )
                );

        // 2. Verify password
        if (!passwordEncoder.matches(
                request.motDePasse(),
                utilisateur.getMotDePasse())) {

            throw new BadCredentialsException(
                    "Email ou mot de passe incorrect."
            );
        }

        // 3. Generate JWT
        String token = jwtService.generateToken(utilisateur);

        // 4. Return user information and token
        return new LoginResponse(
                token,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole().name()
        );
    }
}
