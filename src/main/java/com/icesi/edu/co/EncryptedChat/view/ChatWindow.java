package com.icesi.edu.co.EncryptedChat.view;

import com.icesi.edu.co.EncryptedChat.controller.ChatController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class ChatWindow extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    @Autowired
    private ChatController chatController; // Inyectamos el controlador del chat

    public ChatWindow() {
        // Configuración de la ventana de chat
        setTitle("Chat Encrypted");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Componentes de la interfaz de usuario
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        add(messageField, BorderLayout.SOUTH);

        sendButton = new JButton("Send");
        add(sendButton, BorderLayout.EAST);

        // Agregar listener para el botón de enviar mensaje
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Manejar el evento de clic en el botón de enviar mensaje
                String mensaje = messageField.getText();
                if (!mensaje.isEmpty()) {
                    // Enviar el mensaje al servidor a través del controlador del chat
                    chatController.enviarMensaje(mensaje);
                    // Limpiar el campo de texto
                    messageField.setText("");
                }
            }
        });

        setVisible(true);
    }

    // Método para agregar un ActionListener al botón de enviar mensaje
    public void agregarEnviarMensajeListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    // Método para agregar un mensaje al área de chat
    public void agregarMensaje(String mensaje) {
        chatArea.append(mensaje + "\n");
    }

    // Método para obtener el mensaje ingresado por el usuario
    public String obtenerMensaje() {
        return messageField.getText();
    }

    // Método para limpiar el campo de texto del mensaje
    public void limpiarCampoMensaje() {
        messageField.setText("");
    }
}
