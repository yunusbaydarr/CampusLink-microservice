package com.CampusLink.controller;


import com.CampusLink.entity.RefreshToken;
import com.CampusLink.repository.UserRepository;
import com.CampusLink.security.CustomUserDetailsService;
import com.CampusLink.security.SecurityUser;
import com.CampusLink.service.RefreshTokenService;
import com.campuslink.common.dtos.requests.AuthRequest;
import com.campuslink.common.dtos.requests.TokenRefreshRequest;
import com.campuslink.common.dtos.responses.AuthResponse;

import com.campuslink.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var securityUser = new SecurityUser(user);

        String jwtToken = jwtService.generateToken(securityUser, user.getId(), user.getRole().name());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .build();
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(new SecurityUser(user), user.getId(), user.getRole().name());

                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.getRefreshToken())
                            .email(user.getEmail())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenRefreshRequest request) {
        refreshTokenService.logout(request.getRefreshToken());

        return ResponseEntity.ok("Başarıyla çıkış yapıldı.");
    }
}