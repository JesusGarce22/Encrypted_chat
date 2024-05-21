// ChatController.java
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
    private SimpMessagingTemplate messagingTemplate; // Inyección del template para enviar mensajes a través de WebSocket

    @MessageMapping("/topic/mensajes")
    public void recibirMensaje(String mensaje) {
        try {
            String mensajeDescifrado = Encriptador.descifrarMensaje(mensaje);
            System.out.println("Mensaje recibido por el servidor: " + mensajeDescifrado);

            // Envía el mensaje descifrado a todos los clientes conectados
            messagingTemplate.convertAndSend("/topic/mensajes", mensajeDescifrado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para enviar un mensaje al servidor
    public void enviarMensaje(String mensaje) {
        // Envía el mensaje al destino "/app/chat" para que el servidor lo reciba y lo procese
        messagingTemplate.convertAndSend("/app/chat", mensaje);
    }
}
