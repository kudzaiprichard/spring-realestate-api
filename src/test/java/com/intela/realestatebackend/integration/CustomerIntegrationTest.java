package com.intela.realestatebackend.integration;

import com.intela.realestatebackend.BaseTestContainerTest;
import com.intela.realestatebackend.requestResponse.AuthenticationResponse;
import com.intela.realestatebackend.requestResponse.RetrieveAccountResponse;
import com.intela.realestatebackend.testUsers.TestUser;
import com.intela.realestatebackend.testUtil.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CustomerIntegrationTest extends BaseTestContainerTest {
    @Autowired
    private List<TestUser> allUsers;

    private static List<TestUser> customerUsers;

    @Test
    @Order(1)
    void shouldRegisterUser() throws Exception {
        customerUsers = TestUtil.testRegisterCustomerUsers(mockMvc, objectMapper, allUsers);
    }

    @Test
    @Order(1)
    public void fetchAllProperties_withDefaultPagination_shouldReturnFirst20Properties() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        mockMvc.perform(get("/api/v1/properties/")
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(20)) // Assuming there are at least 20 properties
                .andExpect(jsonPath("$[0].id").exists()); // Check if at least first property has an ID
        TestUtil.testLogout(mockMvc, customerAccessToken);
    }

    @Test
    @Order(2)
    public void fetchAllProperties_withCustomPagination_shouldReturnPaginatedProperties() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        mockMvc.perform(get("/api/v1/properties/")
                        .header("Authorization", "Bearer " + customerAccessToken)
                        .param("pageNumber", "1")
                        .param("amount", "10")
                        .param("sortBy", "name")) // Sorting by 'name' field
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10)) // Expect 10 properties per page
                .andExpect(jsonPath("$[0].name").exists()); // Assuming 'name' is a valid property field
        TestUtil.testLogout(mockMvc, customerAccessToken);
    }

    @Test
    @Order(3)
    public void fetchAllProperties_withHighPageNumber_shouldReturnEmptyList() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        mockMvc.perform(get("/api/v1/properties/")
                        .header("Authorization", "Bearer " + customerAccessToken)
                        .param("pageNumber", "100")) // A high page number that has no properties
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // Expect an empty list
        TestUtil.testLogout(mockMvc, customerAccessToken);
    }

    // 1b. Test Fetch Property by ID

    @Test
    @Order(4)
    public void fetchPropertyById_withValidId_shouldReturnProperty() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        Integer validPropertyId = 1; // Use a valid property ID from your test data
        mockMvc.perform(get("/api/v1/properties/{propertyId}", validPropertyId)
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validPropertyId)) // Expect the returned property ID to match
                .andExpect(jsonPath("$.name").exists()); // Assuming 'name' is a valid property field
        TestUtil.testLogout(mockMvc, customerAccessToken);
    }

    @Test
    @Order(5)
    public void fetchPropertyById_withInvalidId_shouldReturnNotFound() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        Integer invalidPropertyId = -2; // Use an invalid property ID that doesn't exist
        mockMvc.perform(get("/api/v1/properties/{propertyId}", invalidPropertyId)
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isNotFound());
        TestUtil.testLogout(mockMvc, customerAccessToken);
    }

    @Test
    @Order(6)
    public void addAndRemoveBookmark_forProperty_shouldWorkCorrectly() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();

        String s = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/user/")
                                .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk()
                ).andReturn().getResponse().getContentAsString();
        RetrieveAccountResponse customer = objectMapper.readValue(s, RetrieveAccountResponse.class);
        // Step 1: List all available properties and save the ID of the first result
        String propertyResponse = mockMvc.perform(get("/api/v1/properties/")
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract propertyId from the response (assuming it's a JSON array of properties)
        Integer propertyId = objectMapper.readTree(propertyResponse).get(0).get("id").asInt();

        // Step 2: Add a bookmark for the property
        mockMvc.perform(post("/api/v1/customer/bookmarks/add/{propertyId}", propertyId)
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk());

        // Step 3: Verify that the bookmark was added using GET /bookmarks
        String bookmarkResponse = mockMvc.perform(get("/api/v1/customer/bookmarks")
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].propertyId").value(propertyId))
                .andExpect(jsonPath("$[0].userId").value(customer.getId()))// Verify the bookmark is linked to the correct propertyId
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract bookmarkId from the response (assuming it returns an array of bookmarks)
        Integer bookmarkId = objectMapper.readTree(bookmarkResponse).get(0).get("id").asInt();
        assertThat(bookmarkId).isNotNull(); // Check that the bookmark exists

        // Step 4: Remove the bookmark
        mockMvc.perform(delete("/api/v1/customer/bookmarks/{bookmarkId}", bookmarkId)
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk());

        // Step 5: Verify that the bookmark was removed using GET /bookmarks
        mockMvc.perform(get("/api/v1/customer/bookmarks")
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Ensure no bookmarks exist now
    }

    @Test
    @Order(7)
    public void addBookmark_forNonExistingProperty_shouldReturnNotFound() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        // Step 1: Define a non-existing propertyId
        Integer nonExistingPropertyId = -2; // This ID should not exist in your test database

        // Step 2: Try to add a bookmark for the non-existing property
        mockMvc.perform(post("/api/v1/customer/bookmarks/add/{propertyId}", nonExistingPropertyId)
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isNotFound()); // Assuming a specific error message, adjust if necessary
    }

    @Test
    @Order(8)
    public void removeBookmark_withInvalidBookmarkId_shouldReturnNotFound() throws Exception {
        AuthenticationResponse customerAuthResponse = TestUtil.testLogin(mockMvc, objectMapper, customerUsers.get(0).getEMAIL(), customerUsers.get(0).getPASSWORD());
        String customerAccessToken = customerAuthResponse.getAccessToken();
        // Step 1: Define a non-existing bookmarkId
        Integer nonExistingBookmarkId = -2; // This ID should not exist in your test database

        // Step 2: Try to remove the bookmark with the invalid bookmarkId
        mockMvc.perform(delete("/api/v1/customer/bookmarks/{bookmarkId}", nonExistingBookmarkId)
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isNotFound()); // Assuming a specific error message, adjust if necessary
    }

    //TODO: Create tests for the application feature
}
