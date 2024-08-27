package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.application.CustomerInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInformationRepository extends JpaRepository<CustomerInformation, Integer> {
    // Custom queries related to CustomerInformation can go here
}
