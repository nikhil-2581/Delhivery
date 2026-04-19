package com.cryptic.service;

import com.cryptic.model.Address;
import com.cryptic.model.User;
import com.cryptic.repo.AddressRepository;
import com.cryptic.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<Address> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Optional<Address> getDefaultAddress(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }

    @Transactional
    public User createUser(String name, String email, String phone) {
        User user = new User(name, email, phone);
        return userRepository.save(user);
    }

    @Transactional
    public Address addAddress(Long userId, String label, String line1,
                              String city, String pincode, boolean isDefault) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (isDefault) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existing -> {
                        throw new IllegalStateException("A default address already exists. Update it first.");
                    });
        }

        return addressRepository.save(
                new Address(user, label, line1, city, pincode, isDefault));
    }
}
