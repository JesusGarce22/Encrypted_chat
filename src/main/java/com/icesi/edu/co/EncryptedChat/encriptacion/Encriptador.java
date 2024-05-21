package com.icesi.edu.co.EncryptedChat.encriptacion;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

public class Encriptador {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
    private static final String CLAVE_SECRETA = "claveSecreta1234"; // Clave secreta para el cifrado AES (16 bytes)

    public static String cifrarMensaje(String mensaje) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec clave = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] iv = cipher.getIV();
        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(mensajeCifrado);
    }

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
