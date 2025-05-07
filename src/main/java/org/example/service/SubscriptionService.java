package org.example.service;

import org.example.dto.SubscriptionDto;
import org.example.dto.SubscriptionRequest;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto addSubscription(Long userId, SubscriptionRequest request);
    List<SubscriptionDto> getUserSubscriptions(Long userId);
    void deleteSubscription(Long userId, Long subId);
    List<String> getTopSubscriptions();
}
