package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class PropertyCreationRequest extends Property {
    @Autowired
    private UserRepository userRepository;
    private Integer userId;
    @PostConstruct
    private void init() {
        this.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
