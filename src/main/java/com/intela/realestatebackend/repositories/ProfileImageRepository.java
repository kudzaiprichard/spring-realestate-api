package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.ProfileImage;
import com.intela.realestatebackend.models.property.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileImageRepository extends JpaRepository<ProfileImage,Integer> {
    ProfileImage findByUserId(int userId);
}
