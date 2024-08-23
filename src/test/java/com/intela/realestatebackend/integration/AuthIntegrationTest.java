package com.intela.realestatebackend.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.requestResponse.AuthenticationRequest;
import com.intela.realestatebackend.requestResponse.AuthenticationResponse;
import com.intela.realestatebackend.requestResponse.PasswordResetRequest;
import com.intela.realestatebackend.requestResponse.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    private static final String FIRST_NAME = "Gary";
    private static final String LAST_NAME = "Li";
    private static final String EMAIL = "gary@gmail.com";
    private static final String MOBILE_NUMBER = "0400000000";
    private static final String PASSWORD = "password";
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("realestate")
            .withUsername("root")
            .withPassword("");  // Set the password to empty, as expected in application.yml

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", () -> "");  // Override with an empty password
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUser() throws Exception {
        // Step 2: Prepare RegisterRequest object with all required fields
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(FIRST_NAME);
        registerRequest.setLastName(LAST_NAME);
        registerRequest.setPassword(PASSWORD);
        registerRequest.setEmail(EMAIL);
        registerRequest.setMobileNumber(MOBILE_NUMBER);
        registerRequest.setRole(Role.CUSTOMER);

        // Step 2: Perform POST /register without any specific role
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());  // Assuming successful registration returns HTTP 201
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldVerifyUserRegistrationAsAdmin() throws Exception {
        // Step 3: Perform GET /user-management as an admin and verify all fields
        mockMvc.perform(get("/api/v1/admin/user-management")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email == '%s')].firstName".formatted(EMAIL)).value(FIRST_NAME))
                .andExpect(jsonPath("$[?(@.email == '%s')].lastName".formatted(EMAIL)).value(LAST_NAME))
                .andExpect(jsonPath("$[?(@.email == '%s')].mobileNumber".formatted(EMAIL)).value(MOBILE_NUMBER));
    }

    AuthenticationResponse login(String email, String password) throws Exception{
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(password);

        String authResponse = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the access token from the response
        AuthenticationResponse authenticationResponse = objectMapper.readValue(authResponse, AuthenticationResponse.class);
        return authenticationResponse;
    }

    void logout(String accessToken) throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());  // Logout should be successful
    }
    @Test
    void shouldLoginAndLogoutSuccessfully() throws Exception {
        // Step 1: Authenticate the user and obtain tokens
        // Extract the access token from the response
        AuthenticationResponse authenticationResponse = login(EMAIL, PASSWORD);
        String accessToken = authenticationResponse.getAccessToken();

        // Step 2: Verify that the token allows access to a protected endpoint
        mockMvc.perform(get("/api/v1/auth/user")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());  // Access should be granted

        // Step 3: Log out the user to invalidate the tokens
        logout(accessToken);

        // Step 4: Attempt to access the protected endpoint again with the same token
        mockMvc.perform(get("/api/v1/auth/user")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());  // Access should be denied
    }

    @Test
    void shouldUserLogInAndGetAccessDeniedThenLogOut() throws Exception {
        AuthenticationResponse authenticationResponse = login(EMAIL, PASSWORD);
        String accessToken = authenticationResponse.getAccessToken();

        // Step 3: Attempt to access the admin endpoint with the obtained access token
        mockMvc.perform(get("/api/v1/admin/test")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());  // Expecting HTTP 403 Forbidden

        // Step 4: Log out the user to invalidate the tokens
        logout(accessToken);
    }
    void resetPassword(String accessToken, String newPassword) throws Exception {
        PasswordResetRequest request = new PasswordResetRequest(newPassword);

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
    @Test
    void shouldUserResetPasswordSuccessfully() throws Exception {
        AuthenticationResponse authenticationResponse = login(EMAIL, PASSWORD);
        String accessToken = authenticationResponse.getAccessToken();
        resetPassword(accessToken, PASSWORD+"1");
        assertThrows(RuntimeException.class, () -> {
            login(EMAIL, PASSWORD);
        });
        authenticationResponse = login(EMAIL, PASSWORD+"1");
        accessToken = authenticationResponse.getAccessToken();
        resetPassword(accessToken, PASSWORD);
        authenticationResponse = login(EMAIL, PASSWORD);
        accessToken = authenticationResponse.getAccessToken();
        logout(accessToken);
    }
}
