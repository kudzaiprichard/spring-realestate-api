package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.requestResponse.PropertyImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import com.intela.realestatebackend.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private PropertyImageRepository propertyImageRepository;

    public List<PropertyResponse> fetchAllProperties(Pageable pageRequest) {
        Page<Property> properties = propertyRepository.findAll(pageRequest);
        return properties.stream()
                .map(PropertyResponse::new) // Assuming PropertyResponse has a constructor that takes a Property
                .collect(Collectors.toList());
    }

    public PropertyResponse fetchPropertyById(Integer propertyId) {
        return Util.getPropertyById(propertyId, propertyRepository);
    }

    public List<PropertyImageResponse> fetchAllImagesByPropertyId(Integer propertyId) {
        List<PropertyImage> propertyImages = propertyImageRepository.findAllByPropertyId(propertyId);
        return propertyImages.stream()
                .map(Util::convertFromPropertyImageToImageResponse) // Assuming ImageResponse has a constructor that takes a PropertyImage
                .collect(Collectors.toList());
    }

}
