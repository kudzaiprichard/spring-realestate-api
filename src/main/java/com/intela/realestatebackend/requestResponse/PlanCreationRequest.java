package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Plan;
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
public class PlanCreationRequest extends Plan {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    private Integer userId;
    private Integer propertyId;
    @PostConstruct
    private void init() {
        this.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        this.setParentListing(propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId)));
    }
}
