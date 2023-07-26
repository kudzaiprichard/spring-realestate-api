package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.Property;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    List<Property> findAllByUserId(Integer userId, Pageable pageRequest);
}
