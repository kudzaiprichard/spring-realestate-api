package com.intela.realestatebackend.integration;

import com.intela.realestatebackend.BaseTestContainerTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.annotation.DirtiesContext;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CustomerDealerIntegrationTest extends BaseTestContainerTest {
}
