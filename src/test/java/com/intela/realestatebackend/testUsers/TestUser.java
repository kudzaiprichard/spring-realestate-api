package com.intela.realestatebackend.testUsers;


import com.intela.realestatebackend.models.archetypes.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestUser {
    private String FIRST_NAME;
    private String LAST_NAME;
    private String EMAIL;
    private String MOBILE_NUMBER;
    private String PASSWORD;
    private Role ROLE;
}
