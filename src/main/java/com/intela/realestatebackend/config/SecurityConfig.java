package com.intela.realestatebackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.intela.realestatebackend.models.Permission.*;
import static com.intela.realestatebackend.models.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)->authorize
                        .requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        //ADMIN ENDPOINTS
                        .requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name())
                        .requestMatchers(GET,"/api/v1/admin/**").hasAuthority(ADMIN_READ.name())
                        .requestMatchers(POST,"/api/v1/admin/**").hasAuthority(ADMIN_CREATE.name())
                        .requestMatchers(PUT,"/api/v1/admin/**").hasAuthority(ADMIN_UPDATE.name())
                        .requestMatchers(DELETE,"/api/v1/admin/**").hasAuthority(ADMIN_DELETE.name())

                        //CUSTOMER ENDPOINTS
                        .requestMatchers("/api/v1/customer/**").hasAnyRole(CUSTOMER.name(),ADMIN.name())
                        .requestMatchers(GET,"/api/v1/customer/**").hasAnyAuthority(CUSTOMER_READ.name(), ADMIN_READ.name())
                        .requestMatchers(POST,"/api/v1/customer/**").hasAnyAuthority(CUSTOMER_CREATE.name(), ADMIN_CREATE.name())
                        .requestMatchers(PUT,"/api/v1/customer/**").hasAnyAuthority(CUSTOMER_UPDATE.name(), ADMIN_UPDATE.name())
                        .requestMatchers(DELETE,"/api/v1/customer/**").hasAnyAuthority(CUSTOMER_DELETE.name(), ADMIN_DELETE.name())

                        //DEALER ENDPOINTS
                        .requestMatchers("/api/v1/dealer/**").hasAnyRole(DEALER.name(),ADMIN.name())
                        .requestMatchers(GET,"/api/v1/dealer/**").hasAnyAuthority(DEALER_READ.name(), ADMIN_READ.name())
                        .requestMatchers(POST,"/api/v1/dealer/**").hasAnyAuthority(DEALER_CREATE.name(), ADMIN_CREATE.name())
                        .requestMatchers(PUT,"/api/v1/dealer/**").hasAnyAuthority(DEALER_UPDATE.name(), ADMIN_UPDATE.name())
                        .requestMatchers(DELETE,"/api/v1/dealer/**").hasAnyAuthority(DEALER_DELETE.name(), ADMIN_DELETE.name())

                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((
                                (request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                        ))
                );
        return http.build();
    }
}
