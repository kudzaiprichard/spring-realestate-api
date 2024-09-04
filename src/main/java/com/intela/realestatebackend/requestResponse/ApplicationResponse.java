package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Application;
import com.intela.realestatebackend.models.property.Bookmark;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ApplicationResponse extends Application {
    private Integer userId;
    private Integer propertyId;
    public ApplicationResponse(Application application){
        BeanUtils.copyProperties(application, this);
    }
    @PostConstruct
    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
