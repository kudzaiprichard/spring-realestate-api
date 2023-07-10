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
                    .email("kudzai@gmail.com")
                    .password("1234")
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + authenticationService.register(admin).getToken());

            var customer = RegisterRequest.builder()
                    .firstName("nigel")
                    .lastName("nickel")
                    .email("nigel@gmail.com")
                    .password("1234")
                    .role(CUSTOMER)
                    .build();
            System.out.println("Customer token: " + authenticationService.register(customer).getToken());
        };
    }
}
