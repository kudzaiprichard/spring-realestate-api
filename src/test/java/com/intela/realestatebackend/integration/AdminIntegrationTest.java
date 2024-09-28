package com.intela.realestatebackend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intela.realestatebackend.BaseTestContainerTest;
import com.intela.realestatebackend.dto.ContactDetailsDTO;
import com.intela.realestatebackend.dto.PersonalDetailsDTO;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.profile.ContactDetails;
import com.intela.realestatebackend.models.profile.PersonalDetails;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.testUsers.TestUser;
import com.intela.realestatebackend.testUtil.TestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AdminIntegrationTest extends BaseTestContainerTest {
    @Autowired
    private List<TestUser> allUsers;

    private static List<TestUser> adminUsers;
    private static List<TestUser> customerUsers;

    @Test
    @Order(1)
    void shouldRegisterUser() throws Exception {
        customerUsers = TestUtil.testRegisterCustomerUsers(mockMvc, objectMapper, allUsers);
        adminUsers = TestUtil.testRegisterAdminUsers(mockMvc, objectMapper, allUsers);
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
        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/{userId}", customer.getId())
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
        if (updateProfileRequest.getPersonalDetails() == null){
            updateProfileRequest.setPersonalDetails(new PersonalDetailsDTO());
        }
        if (updateProfileRequest.getContactDetails() == null){
            updateProfileRequest.setContactDetails(new ContactDetailsDTO());
        }
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
                .andExpect(status().isOk());

        // Step 4: Retrieve the customer's profile again to verify the changes
        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/{userId}", customer.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveProfileResponse updatedProfile = objectMapper.readValue(s, RetrieveProfileResponse.class);

        s = mockMvc.perform(get("/api/v1/admin/user-management/profiles/ids/{userId}", customer.getId())
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
                .andExpect(jsonPath("$[?(@.userId == " + updatedProfile.getUserId() + ")].personalDetails.firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$[?(@.userId == " + updatedProfile.getUserId() + ")].personalDetails.lastName").value("UpdatedLastName"))
                .andExpect(jsonPath("$[?(@.userId == " + updatedProfile.getUserId() + ")].contactDetails.contactNumber").value("1234567890"));
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
        s = mockMvc.perform(get("/api/v1/admin/user-management/{userId}", customer.getId())
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
                .andExpect(status().isOk());

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
        mockMvc.perform(get("/api/v1/admin/user-management")
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].lastName").value("UpdatedLastName"))
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].mobileNumber").value("1234567890"))
                .andExpect(jsonPath("$[?(@.id == " + updatedAccount.getId() + ")].email").value("updatedEmail@gmail.com"));
    }

    @Test
    @Order(5)
    public void testDeleteAccount_Success() throws Exception {
        // Step 1: Admin logs in
        AuthenticationResponse adminAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String adminAccessToken = adminAuthResponse.getAccessToken();
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        // Step 2: Retrieve a customer account to delete (assume customerUsers.get(0) exists)
        String s = mockMvc.perform(
                        get("/api/v1/user/")
                                .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        User customer = objectMapper.readValue(s, User.class);
        Integer customerUserId = customer.getId();

        // Step 3: Admin deletes the customer account
        mockMvc.perform(delete("/api/v1/admin/user-management/{userId}", customerUserId)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk());

        // Step 4: Attempt to retrieve the deleted customer account (should not exist)
        mockMvc.perform(get("/api/v1/admin/user-management/profiles/{userId}", customerUserId)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound());  // The user profile should no longer exist
    }

    @Test
    @Order(6)
    public void testDeleteAccount_NonExistentUser() throws Exception {
        // Step 1: Admin logs in
        AuthenticationResponse adminAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String adminAccessToken = adminAuthResponse.getAccessToken();

        // Step 2: Attempt to delete a non-existent user (userId 99999 does not exist)
        Integer nonExistentUserId = -2;

        // Step 3: Admin tries to delete the non-existent user
        mockMvc.perform(delete("/api/v1/admin/user-management/{userId}", nonExistentUserId)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound());  // Should return 404 since the user does not exist
    }

    @Test
    @Order(7)
    public void testBanUser_Success() throws Exception {
        // Step 1: Admin logs in
        AuthenticationResponse adminAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String adminAccessToken = adminAuthResponse.getAccessToken();
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        // Step 2: Retrieve a customer account to delete (assume customerUsers.get(0) exists)
        String s = mockMvc.perform(
                        get("/api/v1/user/")
                                .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        User customer = objectMapper.readValue(s, User.class);
        Integer customerUserId = customer.getId();

        // Step 3: Admin bans the customer until a specified date (e.g., 1 year from now)
        Timestamp bannedTill = Timestamp.valueOf("2025-12-31 23:59:59");

        mockMvc.perform(post("/api/v1/admin/user-management/ban/{userId}", customerUserId)
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bannedTill)))
                .andExpect(status().isOk())
                .andExpect(content().string("User " + customerUserId + " banned"));

        // Step 4: Verify the customer is now banned (optional: retrieve the profile and check the banned status)
        // This step depends on how your system handles banned users (e.g., a `bannedTill` field in the profile)
        mockMvc.perform(get("/api/v1/user/")
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isForbidden());
        assertThrows(AssertionError.class, () -> {
            TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        });
    }

    @Test
    @Order(8)
    public void testBanUser_NonExistentUser() throws Exception {
        // Step 1: Admin logs in
        AuthenticationResponse adminAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, adminUsers.get(0).getEMAIL(), adminUsers.get(0).getPASSWORD());
        String adminAccessToken = adminAuthResponse.getAccessToken();

        // Step 2: Attempt to ban a non-existent user (userId 99999 does not exist)
        Integer nonExistentUserId = 99999;

        // Step 3: Admin tries to ban the non-existent user
        Timestamp bannedTill = Timestamp.valueOf("2025-12-31 23:59:59");

        mockMvc.perform(post("/api/v1/admin/user-management/ban/{userId}", nonExistentUserId)
                        .header("Authorization", "Bearer " + adminAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bannedTill)))
                .andExpect(status().isNotFound());  // Should return 404 since the user does not exist
    }


}
