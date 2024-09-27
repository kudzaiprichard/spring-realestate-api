package com.intela.realestatebackend.integration;

import com.intela.realestatebackend.BaseTestContainerTest;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.requestResponse.AuthenticationResponse;
import com.intela.realestatebackend.requestResponse.LoggedUserResponse;
import com.intela.realestatebackend.requestResponse.RetrieveAccountResponse;
import com.intela.realestatebackend.testUsers.TestUser;
import com.intela.realestatebackend.testUtil.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthIntegrationTest extends BaseTestContainerTest {

    @Autowired
    private List<TestUser> allUsers;

    private List<TestUser> testUserList;

    @Test
    @Order(1)
    void shouldRegisterUser() throws Exception {
        testUserList = TestUtil.testRegisterCustomerUsers(mockMvc, objectMapper, allUsers);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Order(2)
    void shouldVerifyUserRegistrationAsAdmin() throws Exception {
        // Step 3: Perform GET /user-management as an admin and verify all fields
        mockMvc.perform(get("/api/v1/admin/user-management")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email == '%s')].firstName".formatted(testUserList.get(0).getEMAIL())).value(testUserList.get(0).getFIRST_NAME()))
                .andExpect(jsonPath("$[?(@.email == '%s')].lastName".formatted(testUserList.get(0).getEMAIL())).value(testUserList.get(0).getLAST_NAME()))
                .andExpect(jsonPath("$[?(@.email == '%s')].mobileNumber".formatted(testUserList.get(0).getEMAIL())).value(testUserList.get(0).getMOBILE_NUMBER()));
    }

    @Test
    @Order(3)
    void shouldLoginAndLogoutSuccessfully() throws Exception {
        // Step 1: Authenticate the user and obtain tokens
        // Extract the access token from the response
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();

        // Step 2: Verify that the token allows access to a protected endpoint
        mockMvc.perform(get("/api/v1/user/").header("Authorization", "Bearer " + accessToken)).andExpect(status().isOk());  // Access should be granted

        // Step 3: Log out the user to invalidate the tokens
        TestUtil.testLogout(mockMvc, accessToken);

        // Step 4: Attempt to access the protected endpoint again with the same token
        mockMvc.perform(get("/api/v1/user/").header("Authorization", "Bearer " + accessToken)).andExpect(status().is(403));  // Access should be denied
    }

    @Test
    @Order(4)
    void shouldUserLogInAndGetAccessDeniedThenLogOut() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();

        // Step 3: Attempt to access the admin endpoint with the obtained access token
        mockMvc.perform(get("/api/v1/admin/test")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden

        // Step 4: Log out the user to invalidate the tokens
        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(5)
    void shouldUserResetPasswordSuccessfully() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();
        TestUtil.testResetPasswordAndLogout(mockMvc, objectMapper, accessToken, testUserList.get(0).getPASSWORD() + "1");
        assertThrows(AssertionError.class, () -> {
            TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        });
        authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD() + "1");
        accessToken = authenticationResponse.getAccessToken();
        TestUtil.testResetPasswordAndLogout(mockMvc, objectMapper, accessToken, testUserList.get(0).getPASSWORD());
        authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        accessToken = authenticationResponse.getAccessToken();
        TestUtil.testLogout(mockMvc, accessToken);
    }

    @Test
    @Order(6)
    void shouldCustomerAccessProtectedAuthEndpoints() throws Exception {
        AuthenticationResponse authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String oldAccessToken = authenticationResponse.getAccessToken();
        TestUtil.testLogout(mockMvc, oldAccessToken);
        Thread.sleep(1000); // 1 second
        authenticationResponse = TestUtil.testLogin(mockMvc, objectMapper, testUserList.get(0).getEMAIL(), testUserList.get(0).getPASSWORD());
        String accessToken = authenticationResponse.getAccessToken();
        String refreshToken = authenticationResponse.getRefreshToken();
        assertNotEquals(accessToken, oldAccessToken);
        assertNotEquals(accessToken, refreshToken);

        //access /user with an invalid token
        mockMvc.perform(get("/api/v1/auth/user")
                        .header("Authorization", "Bearer " + accessToken.substring(0, accessToken.length() - 5) + "abcde"))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /user with an old token
        mockMvc.perform(get("/api/v1/auth/user")
                        .header("Authorization", "Bearer " + oldAccessToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /user with a refresh token
        mockMvc.perform(get("/api/v1/auth/user")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /user without a token
        mockMvc.perform(get("/api/v1/auth/user"))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /user with a valid token
        String s = mockMvc.perform(get("/api/v1/auth/user")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoggedUserResponse loggedUserResponse = objectMapper.readValue(s, LoggedUserResponse.class);

        //access /userByAccessToken with an invalid token
        mockMvc.perform(get("/api/v1/auth/userByAccessToken")
                        .param("accessToken", accessToken.substring(0, accessToken.length() - 5) + "abcde"))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /userByAccessToken with an old token
        mockMvc.perform(get("/api/v1/auth/userByAccessToken")
                        .param("accessToken", oldAccessToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /userByAccessToken with a refresh token
        mockMvc.perform(get("/api/v1/auth/userByAccessToken")
                        .param("accessToken", refreshToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /userByAccessToken with a valid token
        s = mockMvc.perform(get("/api/v1/auth/userByAccessToken")
                        .param("accessToken", accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse retrieveAccountResponseFromAccessToken = objectMapper.readValue(s, RetrieveAccountResponse.class);

        //access /userByRefreshToken with an invalid token
        mockMvc.perform(get("/api/v1/auth/userByRefreshToken")
                        .param("refreshToken", accessToken.substring(0, accessToken.length() - 5) + "abcde"))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /userByRefreshToken with an old token
        mockMvc.perform(get("/api/v1/auth/userByRefreshToken")
                        .param("refreshToken", oldAccessToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /userByRefreshToken with an access token
        mockMvc.perform(get("/api/v1/auth/userByRefreshToken")
                        .param("refreshToken", accessToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden
        //access /userByRefreshToken with a valid token
        s = mockMvc.perform(get("/api/v1/auth/userByRefreshToken")
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        RetrieveAccountResponse retrieveAccountResponseFromRefreshToken = objectMapper.readValue(s, RetrieveAccountResponse.class);

        assertEquals(testUserList.get(0).getMOBILE_NUMBER(), loggedUserResponse.getMobileNumber());
        assertEquals(testUserList.get(0).getMOBILE_NUMBER(), retrieveAccountResponseFromRefreshToken.getMobileNumber());
        assertEquals(testUserList.get(0).getMOBILE_NUMBER(), retrieveAccountResponseFromAccessToken.getMobileNumber());

        assertEquals(testUserList.get(0).getFIRST_NAME(), loggedUserResponse.getFirstName());
        assertEquals(testUserList.get(0).getFIRST_NAME(), retrieveAccountResponseFromRefreshToken.getFirstName());
        assertEquals(testUserList.get(0).getFIRST_NAME(), retrieveAccountResponseFromAccessToken.getFirstName());
        TestUtil.testLogout(mockMvc, accessToken);
    }
}
