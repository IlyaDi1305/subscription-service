package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ApiVersion;
import org.example.dto.UserDto;
import org.example.dto.UserRequest;
import org.example.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    @DisplayName("Should create user and return UserDto")
    void createUser_ReturnsUserDto() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");

        UserDto response = new UserDto();
        response.setId(1L);
        response.setName("Test User");
        response.setEmail("test@example.com");

        Mockito.when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post(ApiVersion.V1 + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return 400 when email is invalid")
    void createUser_InvalidEmail_Returns400() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("Test User");
        request.setEmail("invalid-email");

        mockMvc.perform(post(ApiVersion.V1 + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").value("Email format is invalid"));
    }
}
