package com.icesi.edu.co.EncryptedChat.encriptacion;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class Encriptador {
    private static SecretKey claveCompartida;
    public static void establecerClaveCompartida(byte[] sharedSecret) {
        claveCompartida = new SecretKeySpec(sharedSecret, 0, 16, "AES");
    }

    public static String cifrarMensaje(String mensaje) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, claveCompartida);
        byte[] encryptedMessage = cipher.doFinal(mensaje.getBytes());
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedMessage);
        System.out.println("Mensaje cifrado: " + encryptedBase64);
        return encryptedBase64;
    }

    public static String descifrarMensaje(String mensajeCifrado) throws Exception {
        System.out.println("Mensaje cifrado recibido: " + mensajeCifrado);
        byte[] encryptedMessage = Base64.getDecoder().decode(mensajeCifrado);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, claveCompartida);
        byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
        String decryptedText = new String(decryptedMessage);
        System.out.println("Mensaje descifrado: " + decryptedText);
        return decryptedText;
    }

    public static void establecerClaveCompartida(KeyPair kp, PublicKey pubKeyServidor) throws NoSuchAlgorithmException, InvalidKeyException {
        // Inicializar el objeto KeyAgreement
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");

        // Inicializar KeyAgreement con la clave privada del par de claves y la clave pública del servidor
        keyAgreement.init(kp.getPrivate());
        keyAgreement.doPhase(pubKeyServidor, true);

        // Generar la clave compartida
        byte[] sharedSecret = keyAgreement.generateSecret();

        // Utilizar los primeros 128 bits de la clave compartida para AES (16 bytes)
        claveCompartida = new SecretKeySpec(sharedSecret, 0, 16, "AES");
    }
}
