package com.cryptic.config;

import com.cryptic.model.Address;
import com.cryptic.model.Role;
import com.cryptic.model.User;
import com.cryptic.repo.AddressRepository;
import com.cryptic.repo.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    ApplicationRunner seedUsers(UserRepository userRepo,
                                AddressRepository addressRepo,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.count() > 0) return;

            User alice = userRepo.save(new User(
                    "Alice", "alice@example.com", "9000000001",
                    passwordEncoder.encode("pass123"),
                    Role.CUSTOMER
            ));
            addressRepo.save(new Address(alice, "Home", "12 MG Road",
                    "Bengaluru", "560001", true));
            addressRepo.save(new Address(alice, "Work", "91 Koramangala",
                    "Bengaluru", "560034", false));

            User bob = userRepo.save(new User(
                    "Bob", "bob@example.com", "9000000002",
                    passwordEncoder.encode("pass123"),
                    Role.CUSTOMER
            ));
            addressRepo.save(new Address(bob, "Home", "5 Anna Nagar",
                    "Chennai", "600040", true));

            User admin = userRepo.save(new User(
                    "Admin", "admin@delihvery.com", "9000000099",
                    passwordEncoder.encode("admin123"),
                    Role.ADMIN
            ));

            User driver = userRepo.save(new User(
                    "Driver Dan", "dan@delihvery.com", "9000000003",
                    passwordEncoder.encode("drive123"),
                    Role.DRIVER
            ));
        };
    }
}
