package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@AllArgsConstructor
public class UpdateAccountRequest {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
}
