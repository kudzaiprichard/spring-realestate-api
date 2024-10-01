package com.intela.realestatebackend.testUsers;


import com.intela.realestatebackend.models.archetypes.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestUserCustomer1 extends TestUser {
    private final String FIRST_NAME = "James";
    private final String LAST_NAME = "Li";
    private final String EMAIL = "james@gmail.com";
    private final String MOBILE_NUMBER = "0400000010";
    private final String PASSWORD = "password3";
    private final Role ROLE = Role.CUSTOMER;
}
