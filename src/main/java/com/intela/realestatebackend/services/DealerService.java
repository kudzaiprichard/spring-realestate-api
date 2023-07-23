package com.intela.realestatebackend.services;

import com.intela.realestatebackend.repositories.FeatureRepository;
import com.intela.realestatebackend.repositories.ImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DealerService {
    private final PropertyRepository propertyRepository;
    private final FeatureRepository featureRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    //Todo: Get logged in user via token

    //Todo: Fetch property by user id

    //Todo: Add a property

    //Todo: Fetch a property by property id

    //Todo: Delete a property by property id

    //Todo: Update a property by property id
}
