package com.icesi.edu.co.EncryptedChat.servidor;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import javax.crypto.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private static final int SERVER_PORT = 12345;
    private static List<ObjectOutputStream> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

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
        private ObjectOutputStream salida;
        private ObjectInputStream entrada;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                salida = new ObjectOutputStream(clientSocket.getOutputStream());
                entrada = new ObjectInputStream(clientSocket.getInputStream());

                // Generar par de claves DH
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
                kpg.initialize(1024);
                KeyPair kp = kpg.generateKeyPair();

                // Recibir clave pública del cliente
                byte[] publicKeyBytesCliente = (byte[]) entrada.readObject();
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytesCliente);
                KeyFactory keyFact = KeyFactory.getInstance("DH");
                PublicKey pubKeyCliente = keyFact.generatePublic(x509KeySpec);

                // Enviar clave pública al cliente
                PublicKey publicKey = kp.getPublic();
                salida.writeObject(publicKey.getEncoded());

                // Establecer clave compartida
                KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
                keyAgreement.init(kp.getPrivate());
                keyAgreement.doPhase(pubKeyCliente, true);
                byte[] sharedSecret = keyAgreement.generateSecret();
                Encriptador.establecerClaveCompartida(sharedSecret);

                synchronized (clients) {
                    clients.add(salida);
                }

                String message;
                while ((message = (String) entrada.readObject()) != null) {
                    String decryptedMessage = Encriptador.descifrarMensaje(message);
                    broadcastMessage(decryptedMessage, salida);
                }

                synchronized (clients) {
                    clients.remove(salida);
                }

                entrada.close();
                salida.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void broadcastMessage(String message, ObjectOutputStream excludeClient) {
            synchronized (clients) {
                for (ObjectOutputStream client : clients) {
                    if (client != excludeClient) {
                        try {
                            String encryptedMessage = Encriptador.cifrarMensaje(message);
                            client.writeObject(encryptedMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
