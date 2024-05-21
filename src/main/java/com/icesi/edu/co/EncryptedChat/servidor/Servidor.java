package com.icesi.edu.co.EncryptedChat.servidor;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private static final int SERVER_PORT = 12345;
    private static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                // Manejar la comunicación con el cliente en un hilo separado
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientOutput;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);

                // Agregar el PrintWriter del cliente a la lista de clientes
                synchronized (clients) {
                    clients.add(clientOutput);
                }

                String message;
                while ((message = input.readLine()) != null) {
                    // Descifrar el mensaje recibido del cliente
                    String decryptedMessage = Encriptador.descifrarMensaje(message);
                    // Enviar el mensaje descifrado a todos los clientes, excepto al remitente
                    broadcastMessage(decryptedMessage);
                }

                // Si el cliente se desconecta, eliminar su PrintWriter de la lista de clientes
                synchronized (clients) {
                    clients.remove(clientOutput);
                }

                input.close();
                clientOutput.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error al manejar la conexión con el cliente: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Método para enviar un mensaje en texto plano a todos los clientes, excepto al remitente
        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (PrintWriter client : clients) {
                    if (client != clientOutput) {
                        client.println(message);
                    }
                }
            }
        }
    }
}
