package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.Bookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Integer> {
    List<Bookmark> findAllByUserId(Integer userId, Pageable pageRequest);
    Bookmark findByPropertyId(Integer propertyId);
}
