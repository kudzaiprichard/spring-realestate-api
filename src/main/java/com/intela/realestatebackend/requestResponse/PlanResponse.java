package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Plan;
import com.intela.realestatebackend.models.property.Property;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PlanResponse extends Plan {
    private Integer userId;
    private Integer propertyId;
    public PlanResponse(Plan plan){
        BeanUtils.copyProperties(plan, this);
    }
    @PostConstruct
    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.propertyId = this.getParentListing() != null ? this.getParentListing().getId() : null;
    }
}
