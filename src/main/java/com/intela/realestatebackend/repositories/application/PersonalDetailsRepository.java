package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.application.ContactDetails;
import com.intela.realestatebackend.models.application.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, Integer> {
}
