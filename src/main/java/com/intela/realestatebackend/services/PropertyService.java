package com.intela.realestatebackend.services;

import com.intela.realestatebackend.requestResponse.ImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PropertyService {
    public List<PropertyResponse> fetchAllProperties(Pageable pageRequest) {
        return null;
    }

    public PropertyResponse fetchPropertyById(Integer propertyId) {
        return null;
    }

    public List<ImageResponse> fetchAllImagesByPropertyId(Integer propertyId) {
        return null;
    }
}
