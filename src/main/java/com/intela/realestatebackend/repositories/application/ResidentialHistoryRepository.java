package com.intela.realestatebackend.repositories.application;

import com.intela.realestatebackend.models.application.ResidentialHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentialHistoryRepository extends JpaRepository<ResidentialHistory, Integer> {
}
