package com.cryptic.config;

import com.cryptic.model.Address;
import com.cryptic.model.User;
import com.cryptic.repo.AddressRepository;
import com.cryptic.repo.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    ApplicationRunner seedUsers(UserRepository userRepo,
                                AddressRepository addressRepo) {
        return args -> {
            if (userRepo.count() > 0) return;

            User alice = userRepo.save(new User("Alice", "alice@example.com", "9000000001"));
            addressRepo.save(new Address(alice, "Home", "12 MG Road", "Bengaluru", "560001", true));
            addressRepo.save(new Address(alice, "Work", "91 Koramangala", "Bengaluru", "560034", false));

            User bob = userRepo.save(new User("Bob", "bob@example.com", "9000000002"));
            addressRepo.save(new Address(bob, "Home", "5 Anna Nagar", "Chennai", "600040", true));
        };
    }
}
