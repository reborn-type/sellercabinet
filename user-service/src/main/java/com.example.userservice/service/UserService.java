package com.example.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entities.User;
import com.example.userservice.kafka.UserEventPublisher;
import com.example.userservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository repo;
    private final UserEventPublisher publisher;

    public UserService(UserRepository repo, UserEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public List<User> getAllUsers() {
        return repo.getAll();
    }

    public User getUserById(Long userId) {
        return repo.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + userId));
    }

    @Transactional
    public User createUser(String firstName, String lastName, Integer age, String email,
                           String role, String status) {
        User created = repo.create(firstName, lastName, age, email, role, status);
        publisher.publishCreated(created);
        return created;
    }

    @Transactional
    public void updateUser(Long userId, String firstName, String lastName, Integer age, String email) {
        repo.update(userId, firstName, lastName, age, email);
        publisher.publishUpdated(getUserById(userId));
    }

    @Transactional
    public void deleteUser(Long userId) {
        repo.deleteById(userId);
        publisher.publishDeleted(userId);
    }
}