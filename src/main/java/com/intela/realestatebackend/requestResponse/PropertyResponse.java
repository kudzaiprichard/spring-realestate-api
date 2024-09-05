package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PropertyResponse extends Property {
    private Integer userId;
    private Integer parentId;

    public PropertyResponse(Property property) {
        BeanUtils.copyProperties(property, this);
        init();
    }

    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.parentId = this.getParentListing() != null ? this.getParentListing().getId() : null;
    }
}
