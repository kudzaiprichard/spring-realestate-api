package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intela.realestatebackend.models.property.Plan;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class PlanCreationRequest extends Plan {
    @JsonIgnore
    private List<PropertyImage> propertyImages;
}
