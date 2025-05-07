package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.SubscriptionDto;
import org.example.dto.SubscriptionRequest;
import org.example.entity.Subscription;
import org.example.entity.User;
import org.example.repository.SubscriptionRepository;
import org.example.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @CacheEvict(value = {"subscriptions", "users"}, key = "#userId")
    @Override
    public SubscriptionDto addSubscription(Long userId, SubscriptionRequest request) {
        log.info("Adding subscription '{}' to user ID {}", request.getServiceName(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Subscription subscription = subscriptionRepository
                .findByServiceNameIgnoreCase(request.getServiceName())
                .orElseGet(() -> {
                    log.debug("Creating new subscription: {}", request.getServiceName());
                    return subscriptionRepository.save(Subscription.builder()
                            .serviceName(request.getServiceName())
                            .build());
                });

        boolean alreadySubscribed = user.getSubscriptions().stream()
                .anyMatch(s -> s.getId().equals(subscription.getId()));

        if (alreadySubscribed) {
            throw new IllegalArgumentException("User is already subscribed to this service");
        }

        user.getSubscriptions().add(subscription);
        userRepository.save(user);

        return SubscriptionDto.builder()
                .id(subscription.getId())
                .serviceName(subscription.getServiceName())
                .build();
    }

    @Cacheable(value = "subscriptions", key = "#userId")
    @Override
    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        log.debug("Getting subscriptions for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Set<Subscription> subs = user.getSubscriptions();

        return subs.stream()
                .map(s -> SubscriptionDto.builder()
                        .id(s.getId())
                        .serviceName(s.getServiceName())
                        .build())
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"subscriptions", "users"}, key = "#userId")
    @Override
    public void deleteSubscription(Long userId, Long subId) {
        log.warn("Deleting subscription ID {} for user ID {}", subId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Subscription subscription = subscriptionRepository.findById(subId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found with ID: " + subId));

        boolean removed = user.getSubscriptions().removeIf(s -> s.getId().equals(subscription.getId()));
        if (!removed) {
            throw new IllegalArgumentException("User is not subscribed to this service");
        }

        userRepository.save(user);
    }

    @Override
    public List<String> getTopSubscriptions() {
        log.info("Fetching top 3 popular subscriptions");
        return subscriptionRepository
                .findTopSubscriptions(PageRequest.of(0, 3))
                .stream()
                .map(Subscription::getServiceName)
                .collect(Collectors.toList());
    }
}
