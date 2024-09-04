package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.profile.CustomerInformation;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RetrieveProfileResponse extends CustomerInformation {
    private Integer userId;
    public RetrieveProfileResponse (CustomerInformation customerInformation){
        BeanUtils.copyProperties(customerInformation, this);
    }
    @PostConstruct
    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
    }
}
