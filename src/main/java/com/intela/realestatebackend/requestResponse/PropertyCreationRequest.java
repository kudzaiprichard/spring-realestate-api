package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    {
        this.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
