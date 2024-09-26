package com.intela.realestatebackend.testUsers;


import com.intela.realestatebackend.models.archetypes.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestUserAdmin extends TestUser{
    private final String FIRST_NAME = "Bob";
    private final String LAST_NAME = "The Builder";
    private final String EMAIL = "bob@gmail.com";
    private final String MOBILE_NUMBER = "0400000002";
    private final String PASSWORD = "password2";
    private final Role ROLE = Role.ADMIN;
}
