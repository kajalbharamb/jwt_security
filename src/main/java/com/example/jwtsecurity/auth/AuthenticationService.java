package com.example.jwtsecurity.auth;

import com.example.jwtsecurity.config.JwtService;
import com.example.jwtsecurity.config.RefreshTokenService;
import com.example.jwtsecurity.entity.RefreshToken;
import com.example.jwtsecurity.entity.Role;
import com.example.jwtsecurity.entity.User;
import com.example.jwtsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager manager;
    @Autowired
    private final RefreshTokenService tokenService;


    public AuthenticationResponse register(RegisterRequest request) {
        var user= User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        RefreshToken refershToken=tokenService.createRefreshToken(request.getEmail());
        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(String.valueOf(refershToken.getToken()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user=repository.findByEmail(request.getEmail()).orElseThrow(null);
        RefreshToken refershToken=tokenService.createRefreshToken(request.getEmail());
        var jwtToken= jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(String.valueOf(refershToken.getToken()))
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return tokenService.findByToken(refreshTokenRequest.getToken())
                .map(tokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token= jwtService.generateToken(user);
                    return AuthenticationResponse.builder()
                            .token(token)
                            .refreshToken(String.valueOf(refreshTokenRequest.getToken()))
                            .build();
                }).orElseThrow(()-> new RuntimeException("Refresh token is not in database"));
    }
}



