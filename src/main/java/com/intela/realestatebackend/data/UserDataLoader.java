package com.intela.realestatebackend.data;

import com.intela.realestatebackend.models.Token;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.archetypes.TokenType;
import com.intela.realestatebackend.repositories.TokenRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDataLoader {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    @PostConstruct
    public void loadData() {
        // Create a User
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securepassword") // Make sure this is hashed in real applications
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);

        // Create a Token
        Token token = Token.builder()
                .token("sampleaccesstoken")
                .tokenType(TokenType.ACCESS)
                .revoked(false)
                .expired(false)
                .user(user)
                .build();

        tokenRepository.save(token);

        // Add more users and tokens as needed
    }
}
