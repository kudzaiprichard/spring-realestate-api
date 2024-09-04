package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.property.PropertyImage;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@Data
@SuperBuilder
public class PropertyImageResponse extends PropertyImage{
    public PropertyImageResponse (PropertyImage propertyImage){
        BeanUtils.copyProperties(propertyImage, this);
    }
    private Integer propertyId;
    @PostConstruct
    private void init() {
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
