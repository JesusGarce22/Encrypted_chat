// EncryptedChatApplication.java
package com.icesi.edu.co.EncryptedChat;

import com.icesi.edu.co.EncryptedChat.config.WebSocketConfig;
import com.icesi.edu.co.EncryptedChat.controller.ChatController;
import com.icesi.edu.co.EncryptedChat.encriptacion.Encriptador;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WebSocketConfig.class)
public class EncryptedChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(EncryptedChatApplication.class, args);

		// Create instance of ChatController
		ChatController chatController = new ChatController();

		// Simulate receiving encrypted messages and sending them to the server
		try {
			// Simulate receiving an encrypted message
			String encryptedMessage = "U2FsdGVkX1/n6fntLj5vE5MZp3mkPdMNKoQ3nfB/Yj0=";
			// Decrypt the message
			String decryptedMessage = Encriptador.descifrarMensaje(encryptedMessage);
			// Send the decrypted message to the server
			chatController.enviarMensaje(decryptedMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
