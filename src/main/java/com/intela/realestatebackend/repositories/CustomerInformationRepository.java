package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.CustomerInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInformationRepository extends JpaRepository<CustomerInformation, Integer> {
    List<CustomerInformation> findByUser(User user);

    Optional<CustomerInformation> findByUserId(Integer userId);
}
