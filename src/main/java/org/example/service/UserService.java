package org.example.service;

import org.example.dto.UserDto;
import org.example.dto.UserRequest;

public interface UserService {
    UserDto createUser(UserRequest request);
    UserDto getUser(Long id);
    UserDto updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}

