package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Application;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class ApplicationResponse extends Application {
}
