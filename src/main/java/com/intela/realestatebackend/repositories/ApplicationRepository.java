package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.application.CustomerInformation;
import com.intela.realestatebackend.models.property.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<CustomerInformation,Integer> {
}
