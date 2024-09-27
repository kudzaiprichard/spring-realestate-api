package com.intela.realestatebackend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intela.realestatebackend.BaseTestContainerTest;
import com.intela.realestatebackend.dto.ContactDetailsDTO;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.testUsers.TestUser;
import com.intela.realestatebackend.testUtil.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserIntegrationTest extends BaseTestContainerTest {
    @Autowired
    private List<TestUser> allUsers;

    private List<TestUser> testUserList;

    @Test
    @Order(1)
    void shouldRegisterUser() throws Exception {
        testUserList = TestUtil.testRegisterCustomerUsers(mockMvc, objectMapper, allUsers);
    }

    @Test
    @Order(2)
    void shouldUserUpdateProfile() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        byte[] image1Bytes = TestUtil.readFileToBytes(Paths.get(TestUtil.TEST_IMAGE_PATH, "image1.jpg").toString());
        byte[] image2Bytes = TestUtil.readFileToBytes(Paths.get(TestUtil.TEST_IMAGE_PATH, "image2.jpg").toString());

        String accessToken = authenticationResponse.getAccessToken();
        String s = mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveProfileResponse retrieveProfileResponse = objectMapper.readValue(s, RetrieveProfileResponse.class);
        assertNull(retrieveProfileResponse.getContactDetails());

        ContactDetailsDTO contactDetails = new ContactDetailsDTO();
        contactDetails.setContactEmail(testUserList.get(0).getEMAIL());
        contactDetails.setContactNumber(testUserList.get(0).getMOBILE_NUMBER());

        retrieveProfileResponse.setContactDetails(contactDetails);
        s = objectMapper.writeValueAsString(retrieveProfileResponse);
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",            // Part name
                "",       // Original filename (can be arbitrary)
                "application/json",    // Content type
                s.getBytes() // JSON content
        );
        MockMultipartFile image1 = new MockMultipartFile(
                "images",                // Part name (matching @RequestPart name)
                "image1.jpg",            // Filename
                "image/jpeg",            // Content type
                image1Bytes // File content
        );
        mockMvc.perform(multipart("/api/v1/user/profile")
                        .file(image1)
                        .file(requestPart)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        s = mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveProfileResponse retrieveProfileResponse1 = objectMapper.readValue(s, RetrieveProfileResponse.class);
        assertEquals(retrieveProfileResponse1.getContactDetails().getContactEmail(), contactDetails.getContactEmail());
        assertEquals(retrieveProfileResponse1.getContactDetails().getContactNumber(), contactDetails.getContactNumber());

        contactDetails = new ContactDetailsDTO();
        contactDetails.setContactEmail(testUserList.get(1).getEMAIL());
        contactDetails.setContactNumber(testUserList.get(1).getMOBILE_NUMBER());

        retrieveProfileResponse.setContactDetails(contactDetails);
        s = objectMapper.writeValueAsString(retrieveProfileResponse);
        requestPart = new MockMultipartFile(
                "request",            // Part name
                "",       // Original filename (can be arbitrary)
                "application/json",    // Content type
                s.getBytes() // JSON content
        );
        image1 = new MockMultipartFile(
                "images",                // Part name (matching @RequestPart name)
                "image1.jpg",            // Filename
                "image/jpeg",            // Content type
                image1Bytes // File content
        );
        MockMultipartFile image2 = new MockMultipartFile(
                "images",                // Part name (matching @RequestPart name)
                "image2.jpg",            // Filename
                "image/jpeg",            // Content type
                image2Bytes // File content
        );
        mockMvc.perform(multipart("/api/v1/user/profile")
                        .file(image1)
                        .file(image2)
                        .file(requestPart)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        s = mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveProfileResponse retrieveProfileResponse2 = objectMapper.readValue(s, RetrieveProfileResponse.class);

        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/images")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<IDImageResponse> idImageResponses = objectMapper.readValue(s, new TypeReference<>() {
        });

        assertEquals(retrieveProfileResponse2.getContactDetails().getContactEmail(), contactDetails.getContactEmail());
        assertEquals(retrieveProfileResponse2.getContactDetails().getContactNumber(), contactDetails.getContactNumber());

        idImageResponses.forEach(id -> {
            if (id.getName().equals("image1.jpg")) {
                assertThat(id.getImage().length).isGreaterThan(0);
                assertThat(id.getImage()).isEqualTo(image1Bytes);
            }
            if (id.getName().equals("image2.jpg")) {
                assertThat(id.getImage().length).isGreaterThan(0);
                assertThat(id.getImage()).isEqualTo(image2Bytes);
            }
        });

        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(3)
    void shouldUserUploadProfileImage() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();
        // Create a MockMultipartFile for testing
        byte[] imageBytes = TestUtil.readFileToBytes(Paths.get(TestUtil.TEST_IMAGE_PATH, "image1.jpg").toString());
        MockMultipartFile file = new MockMultipartFile("image", "testImage.jpg", "image/jpeg", imageBytes);

        // 1. Upload the image
        mockMvc.perform(multipart("/api/v1/user/profile/avatar")
                        .file(file)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        // 2. Retrieve the image
        MvcResult retrieveResult = mockMvc.perform(get("/api/v1/user/profile/avatar")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        ProfileImageResponse profileImageResponse = objectMapper.readValue(
                retrieveResult.getResponse().getContentAsString(),
                ProfileImageResponse.class);
        byte[] retrievedImageBytes = profileImageResponse.getImage();

        // 3. Assert that the uploaded and retrieved images have the same bytes
        assertThat(retrievedImageBytes.length).isGreaterThan(0);
        assertThat(retrievedImageBytes).isEqualTo(imageBytes);
        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(4)
    void shouldUserUpdateAccountInfo() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();

        String s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());


        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(
                retrieveAccountResponse.getFirstName(),
                retrieveAccountResponse.getLastName(),
                retrieveAccountResponse.getMobileNumber(),
                testUserList.get(1).getEMAIL()
        );
        mockMvc.perform(post("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andReturn();

        s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(1).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());

        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(5)
    void shouldUserUpdateAccountInfo1() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();

        String s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());


        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(
                testUserList.get(1).getFIRST_NAME(),
                retrieveAccountResponse.getLastName(),
                retrieveAccountResponse.getMobileNumber(),
                retrieveAccountResponse.getEmail()
        );
        mockMvc.perform(post("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andReturn();

        s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(1).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());

        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(6)
    void shouldUserUpdateAccountInfo2() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();

        String s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());


        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(
                retrieveAccountResponse.getFirstName(),
                testUserList.get(1).getLAST_NAME(),
                retrieveAccountResponse.getMobileNumber(),
                retrieveAccountResponse.getEmail()
        );
        mockMvc.perform(post("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andReturn();

        s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(1).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());

        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(7)
    void shouldUserUpdateAccountInfo3() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();

        String s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(0).getMOBILE_NUMBER());


        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(
                retrieveAccountResponse.getFirstName(),
                retrieveAccountResponse.getLastName(),
                testUserList.get(1).getMOBILE_NUMBER(),
                retrieveAccountResponse.getEmail()
        );
        mockMvc.perform(post("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andReturn();

        s = mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        retrieveAccountResponse = objectMapper.readValue(s, RetrieveAccountResponse.class);
        assertEquals(retrieveAccountResponse.getFirstName(), testUserList.get(0).getFIRST_NAME());
        assertEquals(retrieveAccountResponse.getLastName(), testUserList.get(0).getLAST_NAME());
        assertEquals(retrieveAccountResponse.getEmail(), testUserList.get(0).getEMAIL());
        assertEquals(retrieveAccountResponse.getMobileNumber(), testUserList.get(1).getMOBILE_NUMBER());

        TestUtil.testLogout(mockMvc, accessToken);
    }
}
