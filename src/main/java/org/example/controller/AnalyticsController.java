package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.config.ApiVersion;
import org.example.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Аналитика подписок")
@RestController
@RequestMapping(ApiVersion.V1 + "/subscriptions")
@RequiredArgsConstructor
public class AnalyticsController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/top")
    public List<String> getTopSubscriptions() {
        return subscriptionService.getTopSubscriptions();
    }
}
