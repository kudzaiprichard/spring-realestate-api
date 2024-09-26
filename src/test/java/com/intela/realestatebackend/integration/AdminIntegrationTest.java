package com.intela.realestatebackend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intela.realestatebackend.BaseTestContainerTest;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.testUsers.TestUser;
import com.intela.realestatebackend.testUtil.TestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AdminIntegrationTest extends BaseTestContainerTest {
    @Autowired
    private List<TestUser> testUserList;

    private List<TestUser> adminUsers = new ArrayList<>();
    private List<TestUser> customerUsers = new ArrayList<>();

    @Test
    @Order(1)
    void shouldRegisterUser() throws Exception {
        for (TestUser user : testUserList) {
            if (user.getROLE().equals(Role.ADMIN)){
                TestUtil.testRegister(mockMvc, objectMapper, user);
                adminUsers.add(user);
            }
            if (user.getROLE().equals(Role.CUSTOMER)){
                TestUtil.testRegister(mockMvc, objectMapper, user);
                customerUsers.add(user);
            }
        }
    }

    @Test
    @Order(2)
    public void testAdminTestEndpoint() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();
        mockMvc.perform(get("/api/v1/admin/test")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN::TEST"));
    }

    @Test
    @Order(3)
    public void testAdminUpdateCustomerProfile() throws Exception {
        byte[] image1Bytes = TestUtil.readFileToBytes(Paths.get(TestUtil.TEST_IMAGE_PATH, "image1.jpg").toString());
        byte[] image2Bytes = TestUtil.readFileToBytes(Paths.get(TestUtil.TEST_IMAGE_PATH, "image2.jpg").toString());

        // Step 1: Admin logs in
        AuthenticationResponse adminAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String adminAccessToken = adminAuthResponse.getAccessToken();

        // Step 2: Customer logs in and retrieves their profile
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();

        String s = mockMvc.perform(
                get("/api/v1/user/")
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        User admin = objectMapper.readValue(s, User.class);

        s = mockMvc.perform(
                        get("/api/v1/user/")
                                .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        User customer = objectMapper.readValue(s, User.class);

        // Retrieve customer's profile
        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/{userId}", admin.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveProfileResponse originalProfile = objectMapper.readValue(s, RetrieveProfileResponse.class);
        // Convert JSON to RetrieveProfileResponse

        // Step 3: Admin updates customer's profile
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        BeanUtils.copyProperties(originalProfile, updateProfileRequest);
        updateProfileRequest.getPersonalDetails().setFirstName("UpdatedFirstName");
        updateProfileRequest.getPersonalDetails().setLastName("UpdatedLastName");
        updateProfileRequest.getContactDetails().setContactNumber("1234567890");

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",            // Part name
                "",       // Original filename (can be arbitrary)
                "application/json",    // Content type
                objectMapper.writeValueAsBytes(updateProfileRequest) // JSON content
        );
        MockMultipartFile image1 = new MockMultipartFile(
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

        mockMvc.perform(multipart("/api/v1/admin/user-management/profiles/{userId}", customer.getId())
                        .file(requestPart)
                        .file(image1)
                        .file(image2)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully"));

        // Step 4: Retrieve the customer's profile again to verify the changes
        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/{userId}", customer.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveProfileResponse updatedProfile = objectMapper.readValue(s, RetrieveProfileResponse.class);

        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/images/{userId}", customer.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<IDImageResponse> idImageResponses = objectMapper.readValue(s, new TypeReference<>() {
        });

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

        Assertions.assertEquals("UpdatedFirstName", updatedProfile.getPersonalDetails().getFirstName());
        Assertions.assertEquals("UpdatedLastName", updatedProfile.getPersonalDetails().getLastName());
        Assertions.assertEquals("1234567890", updatedProfile.getContactDetails().getContactNumber());

        // Step 5: List all profiles and verify the updated profile is in the list
        mockMvc.perform(get("/api/v1/admin/user-management/profiles")
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.userId == " + updatedProfile.getUserId() + ")].firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$[?(@.userId == " + updatedProfile.getUserId() + ")].lastName").value("UpdatedLastName"))
                .andExpect(jsonPath("$[?(@.userId == " + updatedProfile.getUserId() + ")].mobileNumber").value("1234567890"));
    }

    @Test
    @Order(4)
    public void testAdminUpdateCustomerAccount() throws Exception {
        // Step 1: Admin logs in
        AuthenticationResponse adminAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String adminAccessToken = adminAuthResponse.getAccessToken();

        // Step 2: Customer logs in and retrieves their profile
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();

        String s = mockMvc.perform(
                        get("/api/v1/user/")
                                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        User admin = objectMapper.readValue(s, User.class);

        s = mockMvc.perform(
                        get("/api/v1/user/")
                                .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        User customer = objectMapper.readValue(s, User.class);

        // Retrieve customer's profile
        s = mockMvc.perform(get("/api/v1/admin/user-management/{userId}", admin.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse originalAccount = objectMapper.readValue(s, RetrieveAccountResponse.class);
        // Convert JSON to RetrieveProfileResponse

        // Step 3: Admin updates customer's profile
        UpdateAccountRequest updateProfileRequest = new UpdateAccountRequest(
                "UpdatedFirstName",
                "UpdatedLastName",
                "1234567890",
                "updatedEmail@gmail.com"
        );
        Assertions.assertNotEquals(originalAccount.getEmail(), updateProfileRequest.getEmail());
        Assertions.assertNotEquals(originalAccount.getMobileNumber(), updateProfileRequest.getMobileNumber());
        Assertions.assertNotEquals(originalAccount.getFirstName(), updateProfileRequest.getFirstName());
        Assertions.assertNotEquals(originalAccount.getLastName(), updateProfileRequest.getLastName());

        mockMvc.perform(post("/api/v1/admin/user-management/{userId}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfileRequest))
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully"));

        // Step 4: Retrieve the customer's profile again to verify the changes
        s = mockMvc.perform(get("/api/v1/admin/user-management/{userId}", customer.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse updatedAccount = objectMapper.readValue(s, RetrieveAccountResponse.class);

        Assertions.assertEquals("UpdatedFirstName", updatedAccount.getFirstName());
        Assertions.assertEquals("UpdatedLastName", updatedAccount.getLastName());
        Assertions.assertEquals("1234567890", updatedAccount.getMobileNumber());
        Assertions.assertEquals("updatedEmail@gmail.com", updatedAccount.getEmail());

        // Step 5: List all profiles and verify the updated profile is in the list
        mockMvc.perform(get("/api/v1/admin/user-management/")
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].lastName").value("UpdatedLastName"))
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].mobileNumber").value("1234567890"))
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].email").value("updatedEmail@gmail.com"));
    }
}
