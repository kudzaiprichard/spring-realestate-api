package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.property.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Integer> {
    List<PropertyImage> findAllByPropertyId(int propertyId);

}
