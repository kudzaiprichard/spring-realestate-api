package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.profile.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDRepository extends JpaRepository<ID, Integer> {
}
