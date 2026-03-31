package com.cryptic.service;

import org.springframework.stereotype.Service;

import com.cryptic.model.User;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Map<Long, User> USERS = Map.of(
            1L, new User(1L, "Alice", "alice@example.com", "9000000001"),
            2L, new User(2L, "Bob",   "bob@example.com",   "9000000002"),
            3L, new User(3L, "Carol", "carol@example.com", "9000000003")
    );

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(USERS.get(id));
    }
}
