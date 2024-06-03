package com.icesi.edu.co.EncryptedChat.servidor;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Servidor que maneja las conexiones de clientes y retransmite los mensajes cifrados.
 *
 * @author [Jesus Garces - Juan Pablo Acevedo]
 * @version 5.0
 */
public class Servidor {
    private static final int SERVER_PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();

    /**
     * Método principal que inicia el servidor y espera conexiones de clientes.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clase interna que maneja la comunicación con un cliente.
     */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectOutputStream salida;
        private ObjectInputStream entrada;
        private SecretKey claveCompartida;
        private String username;

        /**
         * Constructor de ClientHandler.
         *
         * @param clientSocket Socket del cliente.
         */
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Método run que maneja la comunicación con el cliente.
         */
        @Override
        public void run() {
            try {
                salida = new ObjectOutputStream(clientSocket.getOutputStream());
                entrada = new ObjectInputStream(clientSocket.getInputStream());

                // Leer el nombre de usuario del cliente
                username = (String) entrada.readObject();

                // Generar par de claves DH (Diffie-Hellman).
                // KeyPairGenerator se usa para generar pares de claves públicas y privadas para el algoritmo DH.
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");

                // Inicializar el generador de pares de claves con un tamaño de clave de 1024 bits.
                kpg.initialize(1024);

                // Generar el par de claves (pública y privada).
                KeyPair kp = kpg.generateKeyPair();

                // Enviar la clave pública al cliente.
                // Obtenemos la clave pública del par de claves generadas.
                PublicKey publicKey = kp.getPublic();

                // Enviamos la clave pública al cliente en formato de bytes usando ObjectOutputStream.
                salida.writeObject(publicKey.getEncoded());

                // Recibir la clave pública del cliente.
                // Leemos la clave pública del cliente desde ObjectInputStream, recibida como un arreglo de bytes.
                byte[] publicKeyBytesCliente = (byte[]) entrada.readObject();

                // Convertir la clave pública recibida del cliente de un formato de bytes a un objeto PublicKey.
                // X509EncodedKeySpec se usa para representar la clave pública en el formato X.509.
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytesCliente);

                // Obtener una instancia de KeyFactory para el algoritmo "DH".
                // KeyFactory es una clase que convierte claves de un tipo a otro.
                KeyFactory keyFact = KeyFactory.getInstance("DH");

                // Generar el objeto PublicKey del cliente usando la especificación de clave X.509.
                PublicKey pubKeyCliente = keyFact.generatePublic(x509KeySpec);

                // Establecer la clave compartida.
                // Obtener una instancia de KeyAgreement para el algoritmo "DH".
                KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");

                // Inicializar el KeyAgreement con la clave privada del par de claves generadas por el servidor.
                keyAgreement.init(kp.getPrivate());

                // Ejecutar la fase de clave con la clave pública del cliente.
                // Esto genera la clave compartida que ambos, el servidor y el cliente, podrán usar para cifrar y descifrar mensajes.
                keyAgreement.doPhase(pubKeyCliente, true);

                // Generar el secreto compartido (clave compartida).
                byte[] sharedSecret = keyAgreement.generateSecret();

                // Crear una clave secreta de tipo AES utilizando los primeros 128 bits (16 bytes) del secreto compartido.
                // SecretKeySpec es una clase que se utiliza para construir claves de cifrado a partir de bytes.
                claveCompartida = new SecretKeySpec(sharedSecret, 0, 16, "AES");

                String message;
                while ((message = (String) entrada.readObject()) != null) {
                    String decryptedMessage = Encriptador.descifrarMensaje(message, claveCompartida);
                    String formattedMessage = username + ": " + decryptedMessage;
                    broadcastMessage(formattedMessage, this);
                }

            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    clients.remove(this);
                    entrada.close();
                    salida.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Método que retransmite el mensaje a todos los clientes excepto al remitente.
         *
         * @param message El mensaje a retransmitir.
         * @param sender  El cliente remitente.
         */
        private void broadcastMessage(String message, ClientHandler sender) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client != sender) {
                        try {
                            String encryptedMessage = Encriptador.cifrarMensaje(message, client.claveCompartida);
                            client.salida.writeObject(encryptedMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
