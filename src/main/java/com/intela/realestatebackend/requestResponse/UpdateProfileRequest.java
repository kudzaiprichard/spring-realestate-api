package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class UpdateProfileRequest extends Profile {
    @Autowired
    private UserRepository userRepository;
    private Integer userId;

    {
        this.setProfileOwner(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
