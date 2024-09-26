package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.PropertyImage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PropertyImageResponse extends PropertyImage {
    private Integer propertyId;

    public PropertyImageResponse(PropertyImage propertyImage) {
        BeanUtils.copyProperties(propertyImage, this);
        init();
    }

    private void init() {
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
