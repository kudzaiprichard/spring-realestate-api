package com.intela.realestatebackend.requestResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeatureResponse{
    private int bathrooms;
    private int bedrooms;
    private int lounges;
    private int storeys;
}
