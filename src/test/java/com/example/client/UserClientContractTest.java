package com.example.client;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
public class UserClientContractTest {

    private static final Logger log = LoggerFactory.getLogger(UserClientContractTest.class);

    @Pact(consumer = "UserConsumer", provider = "UserProvider")
    public RequestResponsePact respondWithUserPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder.given("user with id 123 exists")
                .uponReceiving("a request for user 123")
                .path("/users/123")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body("{\"id\": 123, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}")
                .toPact();
    }

    @Pact(consumer = "UserConsumer", provider = "UserProvider")
    public RequestResponsePact missingUserPact(PactDslWithProvider builder) {
        return builder.given("user with id 123 exists")
                .uponReceiving("a request for user 1")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Test
    @PactTestFor(providerName = "UserProvider", pactMethod = "respondWithUserPact")
    public void testGetUserById(MockServer mockServer) {
        UserClient client = new UserClient(mockServer.getUrl());

        User user = client.getUserById(123L);

        assertNotNull(user);
        assertEquals(123L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    @PactTestFor(providerName = "UserProvider", pactMethod = "missingUserPact")
    public void testGetUserByWrongId(MockServer mockServer) {
        UserClient client = new UserClient(mockServer.getUrl());

        HttpClientErrorException.NotFound ex = assertThrows(HttpClientErrorException.NotFound.class,
                                                            () -> client.getUserById(1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
