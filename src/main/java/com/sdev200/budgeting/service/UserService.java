package com.sdev200.budgeting.service;

import com.sdev200.budgeting.model.AppUser;
import com.sdev200.budgeting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse create(String name, String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("An account with that email already exists.");
        }
        AppUser user = userRepository.save(new AppUser(name.trim(), normalizedEmail));
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " was not found."));
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public record UserResponse(Long id, String name, String email) {
    }
}
