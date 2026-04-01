package com.cryptic.client;

import com.cryptic.model.Address;
import com.cryptic.model.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

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

    public Optional<UserProfile> getUser(Long userId) {
        try {
            UserProfile user = restTemplate.getForObject(
                    userServiceUrl + "/users/{id}", UserProfile.class, Map.of("id", userId));
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Address> getDefaultAddress(Long userId) {
        try {
            Address address = restTemplate.getForObject(
                    userServiceUrl + "/users/{id}/addresses/default",
                    Address.class, Map.of("id", userId));
            return Optional.ofNullable(address);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
