package com.icesi.edu.co.EncryptedChat.cliente;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    private static final String SERVER_IP = "172.30.180.124"; // Cambia esto por la dirección IP del servidor
    private static final int SERVER_PORT = 12345; // Cambia esto por el puerto del servidor

    public static void main(String[] args) {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingrese el puerto al que desea conectar: ");
            int port = Integer.parseInt(userInput.readLine());

            Socket socket = new Socket(SERVER_IP, port);
            System.out.println("Conectado al servidor en el puerto: " + port);

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Lógica para leer mensajes del usuario y enviarlos al servidor
            String message;
            while (true) {
                System.out.print("Ingrese su mensaje: ");
                message = userInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break; // Salir del bucle si el usuario ingresa "exit"
                }
                // Encriptar el mensaje antes de enviarlo
                String encryptedMessage = Encriptador.cifrarMensaje(message);
                output.println(encryptedMessage);

                // Leer la respuesta del servidor
                String response = input.readLine();
                System.out.println("Respuesta del servidor: " + response);
            }

            // Cerramos los recursos
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
