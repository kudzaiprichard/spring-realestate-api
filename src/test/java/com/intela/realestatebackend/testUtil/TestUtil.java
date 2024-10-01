package com.intela.realestatebackend.testUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.testUsers.TestUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtil {
    public static final String TEST_IMAGE_PATH = "/usr/src/files/images";

    public static byte[] readFileToBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public static void resetTestUserAccountInfo(MockMvc mockMvc, ObjectMapper objectMapper, String accessToken, TestUser testUser) throws Exception {
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(
                testUser.getFIRST_NAME(),
                testUser.getLAST_NAME(),
                testUser.getMOBILE_NUMBER(),
                testUser.getEMAIL()
        );
        mockMvc.perform(post("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }

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

    public static List<TestUser> testRegisterCustomerUsers(MockMvc mockMvc, ObjectMapper objectMapper, List<TestUser> allUsers) throws Exception {
        List<TestUser> testUserList = new ArrayList<>();
        for (TestUser user : allUsers) {
            if (user.getROLE().equals(Role.CUSTOMER)) {
                TestUtil.testRegister(mockMvc, objectMapper, user);
                testUserList.add(user);
            }
        }
        return testUserList;
    }

    public static List<TestUser> testRegisterAdminUsers(MockMvc mockMvc, ObjectMapper objectMapper, List<TestUser> allUsers) throws Exception {
        List<TestUser> testUserList = new ArrayList<>();
        for (TestUser user : allUsers) {
            if (user.getROLE().equals(Role.ADMIN)) {
                TestUtil.testRegister(mockMvc, objectMapper, user);
                testUserList.add(user);
            }
        }
        return testUserList;
    }

    public static void testRegister(MockMvc mockMvc, ObjectMapper objectMapper, TestUser testUser) throws Exception {
        // Step 1: Prepare RegisterRequest object with all required fields
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(testUser.getFIRST_NAME());
        registerRequest.setLastName(testUser.getLAST_NAME());
        registerRequest.setPassword(testUser.getPASSWORD());
        registerRequest.setEmail(testUser.getEMAIL());
        registerRequest.setMobileNumber(testUser.getMOBILE_NUMBER());
        registerRequest.setRole(testUser.getROLE());

        // Step 2: Perform POST /register without any specific role
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerRequest))).andExpect(status().isCreated());  // Assuming successful registration returns HTTP 201
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
