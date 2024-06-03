package com.icesi.chatend.encriptacion;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

/**
 * Clase que proporciona métodos para cifrar y descifrar mensajes utilizando AES.
 * @author [Jesus Garces - Juan Pablo Acevedo]
 * @version 3.0
 */
public class Encriptador {

    /**
     * Bloque estático para agregar el proveedor de seguridad Bouncy Castle.
     * Esto es necesario para usar algoritmos de cifrado avanzados.
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Algoritmo de cifrado utilizado (AES en modo CBC con relleno PKCS5).
     */
    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";

    /**
     * Clave secreta para el cifrado AES (16 bytes).
     */
    private static final String CLAVE_SECRETA = "claveSecreta1234";

    /**
     * Método para cifrar un mensaje utilizando AES.
     *
     * @param mensaje El mensaje a cifrar.
     * @return El mensaje cifrado.
     * @throws Exception Si ocurre un error durante el cifrado.
     */
    public static String cifrarMensaje(String mensaje) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec clave = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] iv = cipher.getIV();
        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(mensajeCifrado);
    }

    /**
     * Método para descifrar un mensaje cifrado utilizando AES.
     *
     * @param mensajeCifrado El mensaje cifrado.
     * @return El mensaje descifrado.
     * @throws Exception Si ocurre un error durante el descifrado.
     */
    public static String descifrarMensaje(String mensajeCifrado) throws Exception {
        String[] partes = mensajeCifrado.split(":");
        byte[] iv = Base64.getDecoder().decode(partes[0]);
        byte[] mensajeCifradoBytes = Base64.getDecoder().decode(partes[1]);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec clave = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, clave, new IvParameterSpec(iv));
        byte[] mensajeDescifradoBytes = cipher.doFinal(mensajeCifradoBytes);
        return new String(mensajeDescifradoBytes);
    }
}
