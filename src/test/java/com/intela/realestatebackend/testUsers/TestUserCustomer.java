package com.intela.realestatebackend.testUsers;


import com.intela.realestatebackend.models.archetypes.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestUserCustomer extends TestUser{
    private final String FIRST_NAME = "Gary";
    private final String LAST_NAME = "Li";
    private final String EMAIL = "gary@gmail.com";
    private final String MOBILE_NUMBER = "0400000000";
    private final String PASSWORD = "password";
    private final Role ROLE = Role.CUSTOMER;
}
