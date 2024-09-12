package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intela.realestatebackend.models.property.Plan;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.models.property.PropertyStatus;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequest extends Property {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<PropertyImage> propertyImages;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Plan> plans = new HashSet<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PropertyStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdDate;
    @JsonIgnore
    private Property parentListing;
}
