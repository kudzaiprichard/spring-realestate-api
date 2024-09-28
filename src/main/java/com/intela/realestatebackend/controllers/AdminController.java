package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("ADMIN::TEST");
    }

    @GetMapping("/user-management/profiles")
    public ResponseEntity<List<RetrieveProfileResponse>> listAllProfiles() {
        return ResponseEntity.ok(this.adminService.listAllProfiles());
    }

    @GetMapping("/user-management")
    public ResponseEntity<List<RetrieveAccountResponse>> listAllAccounts() {
        return ResponseEntity.ok(this.adminService.listAllAccounts());
    }

    @DeleteMapping("/user-management/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Integer userId) {
        this.adminService.deleteAccount(userId);
        return ResponseEntity.ok("User " + userId + "deleted");
    }

    @Operation(
            summary = "Updates user profile",
            description = "Uploads a user profile JSON object",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "images", contentType = "image/png, image/jpeg")
                            },
                            mediaType = "multipart/form-data",
                            schemaProperties =
                                    {
                                            @SchemaProperty(
                                                    name = "request",
                                                    schema = @Schema(implementation = UpdateProfileRequest.class)
                                            ),
                                            @SchemaProperty(
                                                    name = "images",
                                                    array = @ArraySchema(
                                                            schema = @Schema(type = "string", format = "binary")
                                                    )
                                            )
                                    }

                    )
            )
    )
    @PostMapping("/user-management/profiles/{userId}")
    public ResponseEntity<String> updateProfile(
            @PathVariable Integer userId,
            @RequestPart("images") MultipartFile[] images,
            @RequestPart("request") UpdateProfileRequest request
    ) {
        try {
            this.adminService.updateProfile(userId, images, request);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/user-management/profiles/{userId}")
    public ResponseEntity<RetrieveProfileResponse> retrieveProfile(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok().body(adminService.retrieveProfile(userId));
    }

    @GetMapping(value = "/user-management/profiles/ids/{userId}")
    public ResponseEntity<List<IDImageResponse>> getIdImagesByUserId(@PathVariable Integer userId, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.adminService.getIdImagesByUserId(userId, servletRequest));
    }

    @PostMapping("/user-management/{userId}")
    public ResponseEntity<UpdateAccountResponse> updateAccount(@PathVariable Integer userId, @RequestBody UpdateAccountRequest request) {
        try {
            return ResponseEntity.ok(this.adminService.updateAccount(userId, request));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/user-management/{userId}")
    public ResponseEntity<RetrieveAccountResponse> retrieveAccount(@PathVariable Integer userId) {
        return ResponseEntity.ok(this.adminService.retrieveAccount(userId));
    }

    @PostMapping("/user-management/ban/{userId}")
    public ResponseEntity<String> banAccount(@PathVariable Integer userId, @RequestBody Timestamp bannedTill) {
        this.adminService.banAccount(userId, bannedTill);
        return ResponseEntity.ok("User " + userId + "banned");
    }

    @PostMapping("/user-management/unban/{userId}")
    public ResponseEntity<String> unbanAccount(@PathVariable Integer userId) {
        this.adminService.unbanAccount(userId);
        return ResponseEntity.ok("User " + userId + "unbanned");
    }
}
