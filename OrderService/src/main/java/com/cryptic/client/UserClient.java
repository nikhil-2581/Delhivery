package com.cryptic.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public boolean userExists(Long userId) {
        try {
            restTemplate.getForObject(userServiceUrl + "/users/{id}", Object.class,
                    Map.of("id", userId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
