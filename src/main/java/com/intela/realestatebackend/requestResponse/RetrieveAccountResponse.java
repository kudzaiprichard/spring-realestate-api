package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.CustomerInformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class RetrieveAccountResponse extends User {
    @Override
    @JsonIgnore
    public CustomerInformation getCustomerInformation() {
        return super.getCustomerInformation();
    }
}
