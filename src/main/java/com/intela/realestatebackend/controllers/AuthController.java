package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/user")
    public ResponseEntity<LoggedUserResponse> getLoggedInUser(HttpServletRequest request) {
        return ResponseEntity.ok(this.authService.fetchLoggedInUserByToken(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.created(URI.create("")).body(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.accepted()
                .body(authService.authenticate(request));
    }

    @PostMapping(value = "/resetPassword")
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @RequestBody PasswordResetRequest request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.accepted()
                .body(authService.resetPassword(servletRequest, request));
    }

    /*
    Manage access and refresh tokens
        In this case, the flow is the following one:

        User logins into the application (including username and password)

        Your backend application returns any required credentials information and:

        2.1 Access JWT token with an expired time usually "low" (15, 30 minutes, etc).

        2.2 Refresh JWT token with an expired time greater than access one.

        From now, your frontend application will use access token in the Authorization header for every request.

        When backend returns 401, the frontend application will try to use refresh token (using an specific endpoint) to get new credentials, without forcing the user to login again.

        Refresh token flow (This is only an example, usually only the refresh token is sent)

        If there is no problem, then the user will be able to continue using the application. If backend returns a new 401 => frontend should redirect to login page.
    */
    @PostMapping("/refreshToken")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response

    ) throws IOException {
        authService.refreshToken(request, response);
    }

    @GetMapping("/userByAccessToken")
    public ResponseEntity<RetrieveAccountResponse> getUserByAccessToken(@RequestParam String accessToken) {
        return ResponseEntity.ok(authService.getUserByAccessToken(accessToken));
    }

    @GetMapping("/userByRefreshToken")
    public ResponseEntity<RetrieveAccountResponse> getUserByRefreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.getUserByRefreshToken(refreshToken));
    }
}
