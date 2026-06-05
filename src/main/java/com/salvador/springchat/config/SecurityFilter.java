/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salvador.springchat.config;

import com.salvador.springchat.model.user.User;
import com.salvador.springchat.model.user.UserRepository;
import com.salvador.springchat.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import static java.lang.System.out;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Administrador
 */
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        out.println("Security Filter");
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        var principal = authentication.getPrincipal();
        
        if (principal == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (principal instanceof User user) {
            var userFromRepository = userService.findByEmailOptional(user.getEmail());
            if (userFromRepository.isEmpty()) {
                userService.clearCurrentSession(request, response);
                response.sendRedirect("/login");

                filterChain.doFilter(request, response);
                return;
            }

            var servletPath = request.getServletPath();

            // Prevent user from accessing Login and Register pages while they're alredy signed in.
            switch (servletPath) {
                case "/login":
                case "/register":
                    response.sendRedirect("/home");
                    filterChain.doFilter(request, response);
                    return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
