package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.AuthService;
import com.intela.realestatebackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            HttpServletRequest servletRequest,
            @RequestBody UpdateProfileRequest request
    ) {
        try {
            return ResponseEntity.ok().body(userService.updateProfile(servletRequest, request));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<RetrieveProfileResponse> retrieveProfile(
            HttpServletRequest servletRequest,
            @RequestBody RetrieveProfileRequest request
    ) {
        return ResponseEntity.ok().body(userService.retrieveProfile(servletRequest));
    }
    @PostMapping("/")
    public ResponseEntity<UpdateAccountResponse> updateAccount(
            HttpServletRequest servletRequest,
            @RequestBody UpdateAccountRequest request
    ) {
        try {
            return ResponseEntity.ok().body(userService.updateAccount(servletRequest, request));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
