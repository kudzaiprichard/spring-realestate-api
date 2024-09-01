package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.profile.ContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactDetailsRepository extends JpaRepository<ContactDetails, Integer> {
}
