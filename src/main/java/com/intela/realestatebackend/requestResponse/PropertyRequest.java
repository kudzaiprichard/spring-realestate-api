package com.intela.realestatebackend.requestResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequest{
    private  String description;
    private  Integer numberOfRooms;
    private  String Status;
    private  Long price;
    private  String location;
    private  String propertyOwnerName;
    private  String propertyType;
    private  Integer bathrooms;
    private  Integer bedrooms;
    private  Integer lounges;
    private  Integer storeys;
}
