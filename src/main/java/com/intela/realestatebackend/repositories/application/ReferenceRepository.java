package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.profile.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Integer> {
}
