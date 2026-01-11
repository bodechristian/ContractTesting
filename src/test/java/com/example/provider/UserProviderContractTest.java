package com.example.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import com.example.model.User;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Provider("UserProvider")
@PactFolder("target/pacts")
@ExtendWith(PactVerificationInvocationContextProvider.class)
public class UserProviderContractTest {

    @TestTarget
    public final HttpTestTarget target = new HttpTestTarget("localhost", 8080);

    private static HttpServer server;
    private static final Gson gson = new Gson();
    private static final UserController controller = new UserController();

    @BeforeAll
    public static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/users/123", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                User user = controller.getUserById(123L);
                String response = gson.toJson(user);

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        });

        server.start();
    }

    @TestTemplate
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("user with id 123 exists")
    public void userWithId123Exists() {
        // Clear any existing data
        controller.clearUsers();

        // Set up the specific state required for this test
        User testUser = new User(123L, "John Doe", "john.doe@example.com");
        controller.addUser(testUser);

        System.out.println("Provider state set up: User 123 created");
    }

    @AfterEach
    public void cleanupState() {
        // Clean up after each test to ensure isolation
        controller.clearUsers();
    }

    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }
}
