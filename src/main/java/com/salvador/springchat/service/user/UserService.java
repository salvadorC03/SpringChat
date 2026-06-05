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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static java.lang.System.err;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.context.SecurityContextRepository;

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

    @Autowired
    private final SecurityContextRepository securityContextRepository;

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
    
    public Optional<User> findByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity findByUsername(String username) {
        return buildUserEntity(userRepository.findByUsername(username).orElseThrow());
    }

    public User getCurrentSessionUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            err.println("Invocation to getCurrentSessionUser returned null authentication.");
            return null;
        }

        return (User) authentication.getPrincipal();
    }

    public void clearCurrentSession(HttpServletRequest request, HttpServletResponse response) {
        securityContextRepository.saveContext(SecurityContextHolder.createEmptyContext(), request, response);
    }

    private UserEntity authenticate(User user, String password, HttpServletRequest req, HttpServletResponse res) {
        var token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
        var authentication = authenticationManager.authenticate(token);

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, req, res);

        return buildUserEntity(user);
    }

    public UserEntity login(AuthRequest request, HttpServletRequest req, HttpServletResponse res) {
        var userByEmail = userRepository.findByEmail(request.getEmail());
        var userByName = userRepository.findByUsername(request.getUsername());

        if (userByEmail.isPresent()) {
            return authenticate(userByEmail.get(), request.getPassword(), req, res);
        }

        if (userByName.isPresent()) {
            return authenticate(userByName.get(), request.getPassword(), req, res);
        }

        throw new UsernameNotFoundException("User not found.");
    }

    public UserEntity register(UserEntity entity, HttpServletRequest req, HttpServletResponse res) {
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

        return authenticate(createdUser, entity.getPassword(), req, res);
    }
}
