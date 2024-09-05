package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class ApplicationRequest extends Application {

}
