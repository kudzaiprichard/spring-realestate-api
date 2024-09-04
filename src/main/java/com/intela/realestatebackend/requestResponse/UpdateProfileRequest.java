package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.profile.CustomerInformation;
import com.intela.realestatebackend.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class UpdateProfileRequest extends CustomerInformation {
    @Autowired
    private UserRepository userRepository;
    private Integer userId;
    @PostConstruct
    private void init() {
        this.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
