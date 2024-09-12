package com.intela.realestatebackend.handler;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.services.AuthService;
import com.intela.realestatebackend.services.JwtService;
import com.intela.realestatebackend.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        User user = Util.getUserByToken(request, jwtService, userRepository);
        if (user != null){
            authService.revokeAllUserTokens(user);
        }
    }
}
