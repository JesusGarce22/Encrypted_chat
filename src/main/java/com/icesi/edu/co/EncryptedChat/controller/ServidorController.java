// ServidorController.java
package com.icesi.edu.co.EncryptedChat.controller;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;
import com.icesi.edu.co.EncryptedChat.processor.MensajeDescifradoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ServidorController {

    private List<String> clientesConectados = new ArrayList<>(); // Almacena los clientes conectados

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Inyección del template para enviar mensajes a través de WebSocket

    @Autowired
    private MensajeDescifradoProcessor mensajeDescifradoProcessor; // Inyección del procesador de mensajes descifrados


    @PostMapping("/mensaje")
    public void recibirMensaje(@RequestBody String mensajeCifrado) {
        try {
            String mensajeDescifrado = Encriptador.descifrarMensaje(mensajeCifrado);
            System.out.println("Mensaje recibido por el servidor: " + mensajeDescifrado);

            // Procesar el mensaje descifrado utilizando el procesador
            mensajeDescifradoProcessor.procesarMensaje(mensajeDescifrado);

            // Envía el mensaje descifrado a todos los clientes conectados
            messagingTemplate.convertAndSend("/topic/mensajes", mensajeDescifrado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciarChat() {
        // Lógica para iniciar el chat entre los clientes
        System.out.println("Se han conectado al menos dos clientes. Iniciando el chat...");

        // Verificar que haya al menos dos clientes conectados
        if (clientesConectados.size() >= 2) {
            // Envía un mensaje de inicio a cada cliente conectado
            for (String cliente : clientesConectados) {
                // Envía un mensaje de inicio a cada cliente conectado a través de WebSocket
                // Utiliza el template de mensajería para enviar un mensaje al destino "/topic/mensajes"
                messagingTemplate.convertAndSend("/topic/mensajes", "¡El chat ha comenzado!");
            }
        } else {
            // Si no hay suficientes clientes conectados, imprime un mensaje de advertencia
            System.out.println("No hay suficientes clientes conectados para iniciar el chat.");
        }
    }

    // Método para manejar la conexión de un nuevo cliente
    public void agregarCliente(String cliente) {
        clientesConectados.add(cliente); // Agrega el nuevo cliente a la lista de clientes conectados
        System.out.println("Cliente conectado: " + cliente);
        iniciarChat(); // Inicia el chat si hay al menos dos clientes conectados
    }
}
