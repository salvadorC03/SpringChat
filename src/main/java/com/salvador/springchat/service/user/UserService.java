/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salvador.springchat.service.user;

import com.salvador.springchat.model.user.Role;
import com.salvador.springchat.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.salvador.springchat.model.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Administrador
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private UserEntity buildUserEntity(User user) {
        return UserEntity.builder().
                username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public UserEntity findByEmail(String email) {
        return buildUserEntity(userRepository.findByEmail(email).orElseThrow());
    }

    public UserEntity findByUsername(String username) {
        return buildUserEntity(userRepository.findByUsername(username).orElseThrow());
    }

    private UserEntity authenticate(User user, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), password));
        return buildUserEntity(user);
    }

    public UserEntity login(AuthRequest request) {
        var userByEmail = userRepository.findByEmail(request.getEmail());
        var userByName = userRepository.findByUsername(request.getUsername());

        if (userByEmail.isPresent()) {
            return authenticate(userByEmail.get(), request.getPassword());
        }

        if (userByName.isPresent()) {
            return authenticate(userByName.get(), request.getPassword());
        }

        throw new UsernameNotFoundException("User not found.");
    }

    public UserEntity register(UserEntity entity) {
        // Check if email exists
        if (userRepository.findByEmail(entity.getEmail()).isPresent()) {
            throw new AuthException("Email exists.");
        }

        // Check if username exists
        if (userRepository.findByUsername(entity.getUsername()).isPresent()) {
            throw new AuthException("Email exists.");
        }

        if (entity.getPassword().length() < 6) {
            throw new AuthException("Password must contain at least 6 characters.");
        }

        var newUser = User.builder()
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(passwordEncoder.encode(entity.getPassword()))
                .avatarUrl(entity.getAvatarUrl())
                .role(Role.USER)
                .build();

        var createdUser = userRepository.save(newUser);

        return buildUserEntity(createdUser);
    }
}
