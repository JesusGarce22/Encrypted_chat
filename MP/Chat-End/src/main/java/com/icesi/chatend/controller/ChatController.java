package com.icesi.chatend.controller;

import com.icesi.chatend.encriptacion.Encriptador;
import com.icesi.chatend.processor.MensajeDescifradoProcessor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private SimpMessagingTemplate messagingTemplate; // Inject the template to send messages via WebSocket

    private MensajeDescifradoProcessor mensajeDescifradoProcessor; // Inject the decrypted message processor

    // Method to send a message to the server
    public void enviarMensaje(String mensaje) {
        // Send the message to the "/app/chat" destination for the server to receive and process it
        messagingTemplate.convertAndSend("/app/chat", mensaje);
    }

}
