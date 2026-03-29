package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.dto.CreateUserRequest;

import java.util.List;

public interface UserService {
    User createUser(CreateUserRequest request);
    List<User> listUsers();
    User getUser(Long id);
    User getUserByUsername(String username);
    User updateUser(Long id, CreateUserRequest request);
    void deleteUser(Long id);

    User updateStatus(Long id, String status);

    User updateRole(Long id, String role);
}