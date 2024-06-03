package com.icesi.edu.co.EncryptedChat.encriptacion;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Clase Encriptador que maneja el cifrado y descifrado de mensajes utilizando AES.
 *
 * @autor [Jesus Garces - Juan Pablo Acevedo]
 * @version 5.0
 */
public class Encriptador {

    /**
     * Método para cifrar un mensaje utilizando una clave compartida.
     *
     * @param mensaje        El mensaje a cifrar.
     * @param claveCompartida La clave compartida para el cifrado.
     * @return El mensaje cifrado en Base64.
     * @throws Exception Si ocurre un error durante el cifrado.
     */
    public static String cifrarMensaje(String mensaje, SecretKey claveCompartida) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, claveCompartida);
        byte[] encryptedMessage = cipher.doFinal(mensaje.getBytes());
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedMessage);
        return encryptedBase64;
    }

    /**
     * Método para descifrar un mensaje cifrado utilizando una clave compartida.
     *
     * @param mensajeCifrado El mensaje cifrado en Base64.
     * @param claveCompartida La clave compartida para el descifrado.
     * @return El mensaje descifrado.
     * @throws Exception Si ocurre un error durante el descifrado.
     */
    public static String descifrarMensaje(String mensajeCifrado, SecretKey claveCompartida) throws Exception {
        byte[] encryptedMessage = Base64.getDecoder().decode(mensajeCifrado);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, claveCompartida);
        byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
        return new String(decryptedMessage);
    }
}
