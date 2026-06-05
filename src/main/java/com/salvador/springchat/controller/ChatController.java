/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salvador.springchat.controller;

import com.salvador.springchat.service.chat.ChatMessageEntity;
import com.salvador.springchat.service.chat.ChatService;
import com.salvador.springchat.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Administrador
 */
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final UserService userService;
    private final ChatService chatService;
    
    @GetMapping("/chat")
    public ModelAndView chattingRoom() {
        return new ModelAndView("chat")
                .addObject("user", userService.getCurrentSessionUser());
    }
    
    @ResponseBody
    @PostMapping("/sendMessage")
    public ResponseEntity sendMessage(
            ChatMessageEntity msgRequest,
            HttpServletRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(msgRequest));
    }
}
