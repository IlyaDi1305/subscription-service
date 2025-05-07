package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ApiVersion;
import org.example.dto.SubscriptionDto;
import org.example.dto.SubscriptionRequest;
import org.example.service.SubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public SubscriptionService subscriptionService() {
            return Mockito.mock(SubscriptionService.class);
        }
    }

    @Test
    @DisplayName("Should add subscription for user and return DTO")
    void addSubscription_ReturnsDto() throws Exception {
        Long userId = 1L;
        SubscriptionRequest request = new SubscriptionRequest();
        request.setServiceName("Netflix");

        SubscriptionDto response = new SubscriptionDto();
        response.setId(10L);
        response.setServiceName("Netflix");

        Mockito.when(subscriptionService.addSubscription(eq(userId), any()))
                .thenReturn(response);

        mockMvc.perform(post(ApiVersion.V1 + "/users/{userId}/subscriptions", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.serviceName").value("Netflix"));
    }

    @Test
    @DisplayName("Should return user's subscriptions list")
    void getUserSubscriptions_ReturnsList() throws Exception {
        Long userId = 1L;

        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(10L);
        dto.setServiceName("Netflix");

        Mockito.when(subscriptionService.getUserSubscriptions(userId))
                .thenReturn(List.of(dto));

        mockMvc.perform(get(ApiVersion.V1 + "/users/{userId}/subscriptions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].serviceName").value("Netflix"));
    }
}
