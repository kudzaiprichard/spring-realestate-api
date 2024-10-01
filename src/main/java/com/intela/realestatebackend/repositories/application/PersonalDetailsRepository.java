package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.profile.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, Integer> {
}
