package com.intela.realestatebackend.util;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
}
