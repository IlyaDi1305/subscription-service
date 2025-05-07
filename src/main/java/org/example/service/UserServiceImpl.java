package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.SubscriptionDto;
import org.example.dto.UserDto;
import org.example.dto.UserRequest;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        log.info("Creating user with email: {}", request.getEmail());
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subscriptions(new HashSet<>())
                .build();

        return toDto(userRepository.save(user));
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public UserDto getUser(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        return toDto(user);
    }

    @CachePut(value = "users", key = "#id")
    @Override
    public UserDto updateUser(Long id, UserRequest request) {
        log.info("Updating user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return toDto(userRepository.save(user));
    }

    @CacheEvict(value = {"users", "subscriptions"}, key = "#id")
    @Override
    public void deleteUser(Long id) {
        log.warn("Deleting user ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .subscriptions(
                        Optional.ofNullable(user.getSubscriptions())
                                .orElse(Collections.emptySet())
                                .stream()
                                .map(s -> SubscriptionDto.builder()
                                        .id(s.getId())
                                        .serviceName(s.getServiceName())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
