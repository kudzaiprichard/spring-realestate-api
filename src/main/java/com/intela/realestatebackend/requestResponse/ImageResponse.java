package com.intela.realestatebackend.requestResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private int id;
    private byte[] image;
    private String type;
    private String name;
    private Integer propertyId;
}
