package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.config.ApiVersion;
import org.example.dto.SubscriptionDto;
import org.example.dto.SubscriptionRequest;
import org.example.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Подписки пользователей")
@RestController
@RequestMapping(ApiVersion.V1 + "/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto addSubscription(
            @PathVariable Long userId,
            @RequestBody @Valid SubscriptionRequest request) {
        return subscriptionService.addSubscription(userId, request);
    }

    @GetMapping
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }

    @DeleteMapping("/{subId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(
            @PathVariable Long userId,
            @PathVariable Long subId) {
        subscriptionService.deleteSubscription(userId, subId);
    }
}
