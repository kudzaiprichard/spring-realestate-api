package com.intela.realestatebackend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intela.realestatebackend.exceptions.MissingAccessTokenException;
import com.intela.realestatebackend.exceptions.MissingRefreshTokenException;
import com.intela.realestatebackend.models.Token;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.TokenType;
import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.repositories.TokenRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.util.Util;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.intela.realestatebackend.util.Util.getUserByToken;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private void saveUserToken(User user, String jwtToken, TokenType type) {
        var token = Token
                .builder()
                .user(user)
                .token(jwtToken)
                .tokenType(type)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var userEmail = userRepository.findByEmail(request.getEmail());
        if (userEmail.isPresent()) {
            throw new RuntimeException("User email already exists");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .profile(new Profile())
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken, TokenType.ACCESS);
        saveUserToken(savedUser, refreshToken, TokenType.REFRESH);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getUsername(), savedUser.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public LoggedUserResponse fetchLoggedInUserByToken(
            HttpServletRequest request
    ) {
        User user = getUserByToken(request, jwtService, this.userRepository);
        return LoggedUserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User email does not exist"));
        if (!user.isAccountNonLocked())
            throw new RuntimeException("User is banned until " + user.getBannedTill());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), request.getPassword()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            revokeAllUserTokens(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(user, jwtToken, TokenType.ACCESS);
            saveUserToken(user, refreshToken, TokenType.REFRESH);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new RuntimeException("Incorrect password");
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.split(" ")[1].trim();
        username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Please enter valid token"));

            var isTokenValid = tokenRepository.findByTokenAndExpiredFalseAndRevokedFalse(refreshToken)
                    .map(token -> !token.getRevoked() && !token.getExpired() && token.getTokenType().equals(TokenType.REFRESH))
                    .orElse(false);

            if (jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken, TokenType.ACCESS);
                saveUserToken(user, refreshToken, TokenType.REFRESH);

                var authResponse = AuthenticationResponse
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }

            throw new RuntimeException("Please enter valid refresh token");
        }
    }

    public void resetPassword(HttpServletRequest servletRequest, PasswordResetRequest request) {
        User user = Util.getUserByToken(servletRequest, jwtService, userRepository);

        // Encrypt the new password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // Update the user's password
        user.setPassword(encodedPassword);

        // Save the updated user
        userRepository.save(user);
    }

    public RetrieveAccountResponse getUserByAccessToken(String accessToken) {
        if (accessToken == null) {
            throw new MissingAccessTokenException("Missing access token");
        }
        User user = tokenRepository.findUserByAccessToken(accessToken);
        if (user == null) {
            throw new MissingAccessTokenException("Missing access token");
        }
        return Util.mapToRetrieveAccountResponse(user);
    }

    public RetrieveAccountResponse getUserByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new MissingRefreshTokenException("Missing refresh token");
        }
        User user = tokenRepository.findUserByRefreshToken(refreshToken);
        if (user == null) {
            throw new MissingRefreshTokenException("Missing refresh token");
        }
        return Util.mapToRetrieveAccountResponse(user);
    }
}
