package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {
    List<Image> findAllByPropertyId(int propertyId);
}
