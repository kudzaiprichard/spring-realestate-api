package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.profile.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDRepository extends JpaRepository<ID, Integer> {
    List<ID> findAllByProfileId(int propertyId);
}
