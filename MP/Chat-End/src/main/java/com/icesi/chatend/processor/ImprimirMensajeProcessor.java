package com.icesi.chatend.processor;

import org.springframework.stereotype.Component;

// Implementation of the interface to print the decrypted message
@Component
public class ImprimirMensajeProcessor implements MensajeDescifradoProcessor {
    @Override
    public void procesarMensaje(String mensajeDescifrado) {
        // Print the decrypted message
        System.out.println("Mensaje descifrado procesado: " + mensajeDescifrado);
        // Additional logic can be added here if needed
    }
}
