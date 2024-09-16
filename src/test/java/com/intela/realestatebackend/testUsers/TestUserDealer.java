package com.intela.realestatebackend.testUsers;


import com.intela.realestatebackend.models.archetypes.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestUserDealer extends TestUser{
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Citizen";
    private final String EMAIL = "john@gmail.com";
    private final String MOBILE_NUMBER = "0400000001";
    private final String PASSWORD = "password1";
    private final Role ROLE = Role.DEALER;
}
