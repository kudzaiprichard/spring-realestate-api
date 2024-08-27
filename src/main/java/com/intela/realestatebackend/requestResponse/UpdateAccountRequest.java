package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class UpdateAccountRequest extends User {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
}
