package com.icesi.edu.co.EncryptedChat.processor;

import org.springframework.stereotype.Component;

// Implementación de la interfaz para imprimir el mensaje descifrado
@Component
public class ImprimirMensajeProcessor implements MensajeDescifradoProcessor {
    @Override
    public void procesarMensaje(String mensajeDescifrado) {
        System.out.println("Mensaje descifrado procesado: " + mensajeDescifrado);
        // Aquí se puede agregar cualquier otra lógica que desees realizar con el mensaje descifrado
    }
}
