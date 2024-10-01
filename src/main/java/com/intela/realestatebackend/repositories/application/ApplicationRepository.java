package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.property.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByUser(User user);

    Optional<Application> findByUserId(Integer userId);

    Optional<Application> findByPropertyId(Integer propertyId);
}
