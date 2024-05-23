package com.icesi.edu.co.EncryptedChat.servidor;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
    private static final int PORT_MIN = 12345;
    private static final int PORT_MAX = 12350; // Adjust port range as needed
    private static final String SERVER_IP = "172.30.180.124"; // Specify the server's IP address
    private static Map<Integer, List<PrintWriter>> portClientsMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            for (int port = PORT_MIN; port <= PORT_MAX; port++) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Servidor iniciado en el puerto: " + port);
                portClientsMap.put(port, new ArrayList<>());

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                    // Pair clients on adjacent ports
                    int pairedPort = (port % 2 == 0) ? port - 1 : port + 1;

                    // Manejar la comunicación con el cliente en un hilo separado
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, pairedPort));
                    clientThread.start();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientOutput;
        private int pairedPort;

        public ClientHandler(Socket clientSocket, int pairedPort) {
            this.clientSocket = clientSocket;
            this.pairedPort = pairedPort;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);

                // Ensure that the list for the paired port is initialized
                synchronized (portClientsMap) {
                    if (!portClientsMap.containsKey(pairedPort)) {
                        portClientsMap.put(pairedPort, new ArrayList<>());
                    }
                }

                // Add the PrintWriter of the client to the list of clients
                synchronized (portClientsMap) {
                    portClientsMap.get(pairedPort).add(clientOutput);
                }

                String message;
                while ((message = input.readLine()) != null) {
                    // Decrypt the received message from the client
                    String decryptedMessage = Encriptador.descifrarMensaje(message);
                    // Send the decrypted message to the paired client
                    sendToPairedClient(decryptedMessage);
                }

                // If the client disconnects, remove its PrintWriter from the list of clients
                synchronized (portClientsMap) {
                    portClientsMap.get(pairedPort).remove(clientOutput);
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

        // Method to send a plain text message to the paired client
        private void sendToPairedClient(String message) {
            synchronized (portClientsMap) {
                for (PrintWriter client : portClientsMap.get(pairedPort)) {
                    client.println(message);
                }
            }
        }
    }
}
