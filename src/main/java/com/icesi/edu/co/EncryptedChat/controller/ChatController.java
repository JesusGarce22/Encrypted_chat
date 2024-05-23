package com.icesi.edu.co.EncryptedChat.controller;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;
import com.icesi.edu.co.EncryptedChat.processor.MensajeDescifradoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Inject the template to send messages via WebSocket

    @Autowired
    private MensajeDescifradoProcessor mensajeDescifradoProcessor; // Inject the decrypted message processor

    @MessageMapping("/topic/mensajes")
    public void recibirMensaje(String mensaje) {
        try {
            String mensajeDescifrado = Encriptador.descifrarMensaje(mensaje);
            System.out.println("Mensaje recibido por el servidor: " + mensajeDescifrado);

            // Send the decrypted message to all connected clients
            messagingTemplate.convertAndSend("/topic/mensajes", mensajeDescifrado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to the server
    public void enviarMensaje(String mensaje) {
        // Send the message to the "/app/chat" destination for the server to receive and process it
        messagingTemplate.convertAndSend("/app/chat", mensaje);
    }

    // Method to handle receiving messages from clients asynchronously
    public void handleReceivedMessage(String mensaje) {
        try {
            // Decrypt the received message
            String mensajeDescifrado = Encriptador.descifrarMensaje(mensaje);
            System.out.println("Mensaje recibido por el servidor: " + mensajeDescifrado);

            // Process the decrypted message using the injected processor
            mensajeDescifradoProcessor.procesarMensaje(mensajeDescifrado);

            // Send the decrypted message to all connected clients
            messagingTemplate.convertAndSend("/topic/mensajes", mensajeDescifrado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
