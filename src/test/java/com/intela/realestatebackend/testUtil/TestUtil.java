package com.intela.realestatebackend.testUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intela.realestatebackend.requestResponse.AuthenticationRequest;
import com.intela.realestatebackend.requestResponse.AuthenticationResponse;
import com.intela.realestatebackend.requestResponse.PasswordResetRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtil {
    public static AuthenticationResponse testLogin(MockMvc mockMvc, ObjectMapper objectMapper, String email, String password) throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(password);

        String authResponse = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().is(202))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the access token from the response
        return objectMapper.readValue(authResponse, AuthenticationResponse.class);
    }

    public static void testLogout(MockMvc mockMvc, String accessToken) throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());  // Logout should be successful
    }

    public static void testResetPasswordAndLogout(MockMvc mockMvc, ObjectMapper objectMapper, String accessToken, String newPassword) throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setNewPassword(newPassword);

        mockMvc.perform(post("/api/v1/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is(202))
                .andReturn();

        testLogout(mockMvc, accessToken);
    }
}
