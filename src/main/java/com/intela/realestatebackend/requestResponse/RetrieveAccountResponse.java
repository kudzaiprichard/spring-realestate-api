package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.application.CustomerInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class RetrieveAccountResponse extends User {
    @Override
    @JsonIgnore
    public Set<CustomerInformation> getCustomerInformation() {
        return super.getCustomerInformation();
    }
}
