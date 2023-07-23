package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Integer> {
}
