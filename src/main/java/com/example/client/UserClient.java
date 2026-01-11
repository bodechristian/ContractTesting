package com.example.client;

import com.example.model.User;
import org.springframework.web.client.RestTemplate;

public class UserClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public UserClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public User getUserById(Long userId) {
        String url = baseUrl + "/users/" + userId;
        return restTemplate.getForObject(url, User.class);
    }
}
