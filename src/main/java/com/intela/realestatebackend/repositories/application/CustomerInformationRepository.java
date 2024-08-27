package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.application.CustomerInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInformationRepository extends JpaRepository<CustomerInformation, Integer> {
    List<CustomerInformation> findByUser(User user);

    Optional<CustomerInformation> findByUserIdAndPropertyIsNull(Integer userId);
}
