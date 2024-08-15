package com.intela.realestatebackend;

import com.intela.realestatebackend.requestResponse.RegisterRequest;
import com.intela.realestatebackend.services.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import static com.intela.realestatebackend.models.Role.ADMIN;
import static com.intela.realestatebackend.models.Role.CUSTOMER;

@SpringBootApplication
public class RealestateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealestateBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthService authenticationService
    ){
        return args -> {
            var admin = RegisterRequest.builder()
                    .firstName("kudzai")
                    .lastName("matizirofa")
                    .mobileNumber("+2630772349201")
                    .email("kudzai@gmail.com")
                    .password("1234")
                    .role(ADMIN)
                    .build();
            var savedAdmin = authenticationService.register(admin);
            System.out.println("Admin token: " + savedAdmin.getAccessToken());
            System.out.println("Admin refresh token: " + savedAdmin.getRefreshToken());

            var customer = RegisterRequest.builder()
                    .firstName("nigel")
                    .lastName("nickel")
                    .mobileNumber("+2630772349201")
                    .email("nigel@gmail.com")
                    .password("1234")
                    .role(CUSTOMER)
                    .build();
            var savedCustomer = authenticationService.register(customer);
            System.out.println("Customer token: " + savedCustomer.getAccessToken());
            System.out.println("Customer refresh token: " + savedCustomer.getRefreshToken());
        };
    }
}
