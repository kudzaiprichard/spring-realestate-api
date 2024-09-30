package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    Optional<List<User>> findAllByRole(Role role);
}
