package com.intela.realestatebackend.requestResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateAccountRequest {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
}
