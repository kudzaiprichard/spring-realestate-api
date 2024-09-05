package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.PropertyImage;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@Data
@SuperBuilder
public class PropertyImageResponse extends PropertyImage {
    public PropertyImageResponse(PropertyImage propertyImage) {
        BeanUtils.copyProperties(propertyImage, this);
        init();
    }

    private Integer propertyId;

    private void init() {
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
