/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salvador.springchat.controller;

import com.salvador.springchat.service.user.AuthRequest;
import com.salvador.springchat.service.user.UserEntity;
import com.salvador.springchat.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Administrador
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password) {
        System.out.println("Authenticate request");
        var request = AuthRequest.builder()
                .email(username)
                .username(username)
                .password(password)
                .build();
        System.out.println("Request is " + request.getEmail() + " " + request.getUsername() + " " + request.getPassword());
        var user = userService.login(request);

        return new ModelAndView("home")
                .addObject("username", user.getUsername())
                .addObject("email", user.getEmail())
                .addObject("avatarUrl", user.getAvatarUrl());
    }

    @PostMapping("/register")
    public ModelAndView register(@RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam String avatarUrl) {
        System.out.println("Register request");
        var request = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .avatarUrl(avatarUrl)
                .build();
        System.out.println("Request is " + request.getEmail() + " " + request.getUsername() + " " + request.getPassword());
        var user = userService.register(request);

        return new ModelAndView("home")
                .addObject("username", user.getUsername())
                .addObject("email", user.getEmail())
                .addObject("avatarUrl", user.getAvatarUrl());
    }

}
