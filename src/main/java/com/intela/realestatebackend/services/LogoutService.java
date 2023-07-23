package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.Token;
import com.intela.realestatebackend.models.TokenType;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.TokenRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Please enter valid bearer token");
        }

        jwt = authHeader.split(" ")[1].trim();

        var isTokenValid = tokenRepository.findByToken(jwt)
                .map(token -> !token.getRevoked() && !token.getExpired() && token.getTokenType().equals(TokenType.ACCESS))
                .orElse(false);

        if(isTokenValid){
            var userEmail = jwtService.extractUsername(jwt);
            User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<Token> userValidToken = tokenRepository.findAllValidTokenByUser(user.getId());

            userValidToken.forEach(token -> {token.setExpired(true); token.setRevoked(true);});
            tokenRepository.saveAll(userValidToken);
            return;
        }

        throw new RuntimeException("Please enter valid token");
    }
}
