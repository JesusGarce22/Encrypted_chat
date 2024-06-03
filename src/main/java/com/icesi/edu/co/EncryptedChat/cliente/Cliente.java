package com.icesi.edu.co.EncryptedChat.cliente;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Clase Cliente que maneja la conexión con el servidor y el envío/recepción de mensajes cifrados.
 *
 * @author [Jesus Garces - Juan Pablo Acevedo]
 * @version 5.0
 */
public class Cliente {
    private static SecretKey claveCompartida;

    /**
     * Método principal que maneja la conexión con el servidor y la comunicación.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Ingrese el nombre de usuario: ");
            String username = userInput.readLine();

            System.out.print("Ingrese la dirección IP del servidor: ");
            String serverIp = userInput.readLine();

            System.out.print("Ingrese el puerto del servidor: ");
            int serverPort = Integer.parseInt(userInput.readLine());

            Socket socket = new Socket(serverIp, serverPort);
            System.out.println("Conectado al servidor.");

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            // Enviar el nombre de usuario al servidor
            salida.writeObject(username);

            // Generar par de claves DH
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();

            // Enviar clave pública al servidor
            PublicKey publicKey = kp.getPublic();
            salida.writeObject(publicKey.getEncoded());

            // Recibir la clave pública del servidor.
            // El cliente lee la clave pública del servidor desde el ObjectInputStream.
            byte[] publicKeyBytesServidor = (byte[]) entrada.readObject();

            // Convertir la clave pública recibida del servidor de un formato de bytes a un objeto PublicKey.
            // X509EncodedKeySpec se usa para representar la clave pública en el formato X.509.
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytesServidor);

            // Obtener una instancia de KeyFactory para el algoritmo "DH" (Diffie-Hellman).
            // KeyFactory es una clase que convierte claves de un tipo a otro.
            KeyFactory keyFact = KeyFactory.getInstance("DH");

            // Generar el objeto PublicKey del servidor usando la especificación de clave X.509.
            PublicKey pubKeyServidor = keyFact.generatePublic(x509KeySpec);

            // Establecer la clave compartida.
            // Obtener una instancia de KeyAgreement para el algoritmo "DH".
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");

            // Inicializar el KeyAgreement con la clave privada del par de claves generadas por el cliente.
            keyAgreement.init(kp.getPrivate());

            // Ejecutar la fase de clave con la clave pública del servidor.
            // Esto genera la clave compartida que ambos, el servidor y el cliente, podrán usar para cifrar y descifrar mensajes.
            keyAgreement.doPhase(pubKeyServidor, true);

            // Generar el secreto compartido (clave compartida).
            byte[] sharedSecret = keyAgreement.generateSecret();

            // Crear una clave secreta de tipo AES utilizando los primeros 128 bits (16 bytes) del secreto compartido.
            // SecretKeySpec es una clase que se utiliza para construir claves de cifrado a partir de bytes.
            claveCompartida = new SecretKeySpec(sharedSecret, 0, 16, "AES");

            Thread receiverThread = new Thread(() -> {
                try {
                    while (true) {
                        String response = (String) entrada.readObject();
                        String decryptedResponse = Encriptador.descifrarMensaje(response, claveCompartida);
                        System.out.println(decryptedResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            receiverThread.start();

            String message;
            while (true) {
                message = userInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                // Encriptar el mensaje antes de enviarlo
                String encryptedMessage = Encriptador.cifrarMensaje(message, claveCompartida);
                salida.writeObject(encryptedMessage);
            }

            receiverThread.join();
            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
