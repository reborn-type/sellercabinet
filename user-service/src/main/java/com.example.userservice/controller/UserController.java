package com.example.userservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.userservice.entities.User;
import com.example.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAll(){
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> create(@RequestBody CreateUserRequest req){
        User created = userService.createUser(req.firstName(), req.lastName(),
                req.age(), req.email(), req.role(), "ACTIVE");
        return ResponseEntity.created(URI.create("/api/users/" + created.getUserId())).body(created);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.subject")
    public ResponseEntity<Void> update(@PathVariable Long userId,
                                       @RequestBody UpdateUserRequest req) {
        userService.updateUser(userId, req.firstName(), req.lastName(), req.age(), req.email());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}

record CreateUserRequest(String firstName, String lastName, int age, String email, String role) {}
record UpdateUserRequest(String firstName, String lastName, int age, String email) {}
