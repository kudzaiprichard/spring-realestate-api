package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ApplicationResponse extends Application {
    private Integer userId;
    private Integer propertyId;

    public ApplicationResponse(Application application) {
        BeanUtils.copyProperties(application, this);
        init();
    }

    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
