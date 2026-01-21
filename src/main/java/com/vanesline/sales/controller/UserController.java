package com.vanesline.sales.controller;

import com.vanesline.sales.dto.User.UserResponse;
import com.vanesline.sales.entity.Role;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${application.path.base.private}/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users - requires user:read permission
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAllWithRoles().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Get current logged in user profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsernameWithRolesAndPermissions(username)
                .map(user -> ResponseEntity.ok(toUserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by ID - requires user:read permission
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userRepository.findByIdWithRoles(id)
                .map(user -> ResponseEntity.ok(toUserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user - requires user:delete permission
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private UserResponse toUserResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .isSystem(user.getIsSystem())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
