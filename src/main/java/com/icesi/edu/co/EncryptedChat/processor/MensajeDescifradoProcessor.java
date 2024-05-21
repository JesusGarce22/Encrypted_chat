package com.icesi.edu.co.EncryptedChat.processor;

import org.springframework.stereotype.Component;

// Definición de la interfaz del procesador de mensajes descifrados
public interface MensajeDescifradoProcessor {
    void procesarMensaje(String mensajeDescifrado);
}
