package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Property;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PropertyResponse extends Property {
    private Integer userId;
    public PropertyResponse(Property property){
        BeanUtils.copyProperties(property, this);
    }
    @PostConstruct
    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
    }
}
