package com.intela.realestatebackend.services;

import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookmarkRepository bookmarkRepository;

    //Todo: Get logged in user via token

    //Todo: Fetch all properties
    //should include pagination etc

    //Todo: Fetch property by property id

    //Todo: Fetch all bookmarks by user

    //Todo: Fetch bookmark by bookmark id

    //Todo: Add bookmark

    //Todo: Add a search/filter/sorting functionality on fetching all filters
}
