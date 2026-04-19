package com.cryptic.controllers;

import com.cryptic.model.Address;
import com.cryptic.model.User;
import com.cryptic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<Address>> getAddresses(@PathVariable Long id) {
        List<Address> addresses = userService.getAddresses(id);
        if (addresses.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}/addresses/default")
    public ResponseEntity<Address> getDefaultAddress(@PathVariable Long id) {
        return userService.getDefaultAddress(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
