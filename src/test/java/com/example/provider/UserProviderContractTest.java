package com.example.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Provider("UserProvider")
@PactFolder("target/pacts")
@ExtendWith({SpringExtension.class, PactVerificationInvocationContextProvider.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserProviderContractTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserController userController;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("user with id 123 exists")
    public void userWithId123Exists() {
        userController.clearUsers();
        User testUser = new User(123L, "John Doe", "john.doe@example.com");
        userController.addUser(testUser);
        System.out.println("Provider state set up: User 123 created");
    }

    @AfterEach
    public void cleanupState() {
        userController.clearUsers();
    }
}
