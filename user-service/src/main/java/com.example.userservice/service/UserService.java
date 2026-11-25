package com.example.userservice.service;

import java.util.List;

import com.example.userservice.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entities.User;
import com.example.userservice.kafka.UserEventPublisher;
import com.example.userservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository repo;
    private final UserEventPublisher publisher;

    public UserService(UserRepository repo, UserEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public List<UserResponse> getAllUsers() {
        return repo.getALl();
    }

    public UserResponse getUserById(Long userId) {
        return repo.findResponseById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + userId));
    }


    @Transactional
    public void updateUser(
            Long userId,
            String firstName,
            String lastName,
            Integer age,
            String email
    ) {
        User user = findEntityById(userId);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        afterCommit(() -> publisher.publishUpdated(user));
    }


    @Transactional
    public void deleteUser(Long userId){
        User user = findEntityById(userId);

        repo.delete(user);

        afterCommit(() -> publisher.publishDeleted(userId));
    }


    private User findEntityById(Long userId){
        return repo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким ID не найден: " + userId));
    }

    private void afterCommit(Runnable action){
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit(){
                        action.run();
                    }
                }
        );
    }
}