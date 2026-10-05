package com.example.freelanceledger.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String username, String rawPassword, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("そのユーザー名は既に使われています");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("そのメールアドレスは既に使われています");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), email);
        return userRepository.save(user);
    }
}
