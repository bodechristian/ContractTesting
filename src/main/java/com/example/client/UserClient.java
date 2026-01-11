package com.example.client;

import com.example.model.User;
import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class UserClient {
    private final String baseUrl;
    private final Gson gson;

    public UserClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
    }

    public User getUserById(Long userId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(baseUrl + "/users/" + userId);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                return gson.fromJson(json, User.class);
            } catch (ParseException e) {
                throw new IOException("Failed to parse response", e);
            }
        }
    }
}
