package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String password;
    private Role role;
}
