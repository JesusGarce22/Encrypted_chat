package com.icesi.edu.co.EncryptedChat.cliente;

import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Cliente {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Conectado al servidor.");

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            // Generar par de claves DH
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();

            // Enviar clave pública al servidor
            PublicKey publicKey = kp.getPublic();
            salida.writeObject(publicKey.getEncoded());

            // Recibir clave pública del servidor
            byte[] publicKeyBytesServidor = (byte[]) entrada.readObject();
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytesServidor);
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            PublicKey pubKeyServidor = keyFact.generatePublic(x509KeySpec);

            // Establecer clave compartida
            Encriptador.establecerClaveCompartida(kp, pubKeyServidor);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while (true) {
                System.out.print("Ingrese su mensaje: ");
                message = userInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                // Encriptar el mensaje antes de enviarlo
                String encryptedMessage = Encriptador.cifrarMensaje(message);
                salida.writeObject(encryptedMessage);

                // Leer la respuesta del servidor
                String response = (String) entrada.readObject();
                String decryptedResponse = Encriptador.descifrarMensaje(response);
                System.out.println("Respuesta del servidor: " + decryptedResponse);
            }

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
