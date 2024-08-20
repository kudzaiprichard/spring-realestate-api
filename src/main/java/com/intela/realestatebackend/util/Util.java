package com.intela.realestatebackend.util;

import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.FeatureResponse;
import com.intela.realestatebackend.requestResponse.ImageResponse;
import com.intela.realestatebackend.requestResponse.LoggedUserResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import com.intela.realestatebackend.services.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RequiredArgsConstructor
public class Util {

    public static User getUserByToken(HttpServletRequest request, JwtService jwtService, UserRepository userRepository) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String jwtToken;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Please enter a valid token");
        }

        jwtToken = authHeader.split(" ")[1].trim();
        userEmail = jwtService.extractUsername(jwtToken);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public static byte[] compressImage(byte[] image){
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(image);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(image.length);
        byte[] tmp = new byte[4*1024];

        while(!deflater.finished()){
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }

        try{
            outputStream.close();
        }catch (IOException e){
            throw new RuntimeException("Can't compress image");
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] image){
        Inflater inflater = new Inflater();
        inflater.setInput(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] tmp = new byte[4*1024];

        try{
            while(!inflater.finished()){
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        }catch (Exception e){
            throw new RuntimeException("Can't decompress image");
        }
        return outputStream.toByteArray();
    }

    public static PropertyResponse getPropertyResponse(List<String> imageResponses,
                                                       Property property,
                                                       UserRepository userRepository) {

        //get property dealer
        User user = userRepository.findById(property.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Failed to fetch property"));

        //convert user object to loggedUserResponse object
        LoggedUserResponse dealer = LoggedUserResponse.builder()
                .firstname(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();

        return PropertyResponse.builder()
                .id(property.getId())
                .propertyOwnerName(property.getPropertyOwnerName())
                .description(property.getDescription())
                .location(property.getLocation())
                .numberOfRooms(property.getNumberOfRooms())
                .propertyType(property.getPropertyType())
                .price(property.getPrice())
                .status(property.getStatus())
                .feature(property.getFeature())
                .images(imageResponses)
                .build();
    }

    public static PropertyResponse getPropertyById(Integer propertyId,
                                                   PropertyRepository propertyRepository,
                                                   UserRepository userRepository) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(()-> new EntityNotFoundException("Property not found"));
        List<String> imageResponses = new ArrayList<>();

        property.getPropertyImages().forEach(propertyImage1 -> imageResponses.add(propertyImage1.getName()));

        return getPropertyResponse(imageResponses, property, userRepository);
    }

    public static List<ImageResponse> getImageByPropertyId(int propertyId, PropertyImageRepository propertyImageRepository) {
        List<ImageResponse> imageResponses = new ArrayList<>();
        List<PropertyImage> propertyImages = propertyImageRepository.findAllByPropertyId(propertyId);

        propertyImages.forEach(
                propertyImage -> imageResponses.add(
                        ImageResponse.builder()
                                .id(propertyImage.getId())
                                .type(propertyImage.getType())
                                .name(propertyImage.getName())
                                .image(decompressImage(propertyImage.getImage()))
                                .build()
                )
        );

        return imageResponses;
    }
}
