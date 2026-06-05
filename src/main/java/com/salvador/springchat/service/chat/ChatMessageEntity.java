/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salvador.springchat.service.chat;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

/**
 *
 * @author Administrador
 */
@Data
@Builder
public final class ChatMessageEntity {
    private String from;
    private String to;
    private String content;
    private Instant date;
}
