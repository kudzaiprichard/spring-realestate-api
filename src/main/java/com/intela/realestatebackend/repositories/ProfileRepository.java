package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    List<Profile> findByProfileOwner(User user);

    Optional<Profile> findByProfileOwnerId(Integer userId);
}
