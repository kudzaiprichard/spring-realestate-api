package com.intela.realestatebackend.services;

import com.intela.realestatebackend.requestResponse.RetrieveProfileResponse;
import com.intela.realestatebackend.requestResponse.UpdateProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    public List<RetrieveProfileResponse> listAllAccounts() {
        return null;
    }

    public void deleteAccount() {
    }

    public UpdateProfileResponse updateAccount() {
        return null;
    }
}
