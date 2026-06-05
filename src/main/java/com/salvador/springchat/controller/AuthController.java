/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salvador.springchat.controller;

import com.salvador.springchat.service.user.AuthRequest;
import com.salvador.springchat.service.user.UserEntity;
import com.salvador.springchat.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/logout")
    public String logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userService.clearCurrentSession(request, response);
        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        var payload = AuthRequest.builder()
                .email(username)
                .username(username)
                .password(password)
                .build();
        userService.login(payload, req, res);

        return "redirect:/home";
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String avatarUrl,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        var payload = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .avatarUrl(avatarUrl)
                .build();
        userService.register(payload, request, response);

        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView home() {
        return new ModelAndView("home")
                .addObject("user", userService.getCurrentSessionUser());
    }
}
