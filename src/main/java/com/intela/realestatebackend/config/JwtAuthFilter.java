package com.intela.realestatebackend.config;

import com.intela.realestatebackend.models.Token;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.TokenType;
import com.intela.realestatebackend.repositories.TokenRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.services.AuthService;
import com.intela.realestatebackend.services.JwtService;
import com.intela.realestatebackend.util.Util;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.split(" ")[1].trim();
        try {
            username = jwtService.extractUsername(jwt);
        } catch (SignatureException e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: Invalid or Expired JWT Token");
            response.getWriter().flush();
            return;
        } catch (IllegalArgumentException | MalformedJwtException e) {
            // Catching any JWT parsing or format issues
            System.err.println("Invalid JWT format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Bad Request: Invalid JWT format");
            response.getWriter().flush();
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean isTokenValid;
            try {
                Token token = tokenRepository.findByTokenAndExpiredFalseAndRevokedFalse(jwt).orElseThrow(() -> new AccessDeniedException("Please enter a valid ACCESS token"));
                isTokenValid = !token.getRevoked() && !token.getExpired() && token.getTokenType().equals(TokenType.ACCESS);

                if (!isTokenValid) {
                    throw new AccessDeniedException("Please enter a valid ACCESS token");
                } else {
                    User user = Util.getUserByToken(token.getToken(), jwtService, userRepository);
                    if (!user.isAccountNonLocked()){
                        this.authService.revokeAllUserTokens(user);
                        throw new AccessDeniedException("User is banned until " + user.getBannedTill());
                    }
                }
            } catch (AccessDeniedException e) {
                System.err.println("JWT validation failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: Invalid or Expired JWT Token");
                response.getWriter().flush();
                return;
            } catch (RuntimeException e) {
                // General runtime exception handling
                System.err.println("Unexpected error: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Server Error");
                response.getWriter().flush();
                return;
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        filterChain.doFilter(request, response);
    }
}
