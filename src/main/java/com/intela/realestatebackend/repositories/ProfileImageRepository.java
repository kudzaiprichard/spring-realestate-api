package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Integer> {
    ProfileImage findByUserId(int userId);
}
