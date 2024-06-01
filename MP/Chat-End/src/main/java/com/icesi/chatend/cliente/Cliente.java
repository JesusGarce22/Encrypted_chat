package com.icesi.chatend.cliente;

import com.icesi.chatend.encriptacion.Encriptador;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Cliente extends Application {
    private static final String SERVER_IP = "localhost"; // Cambia esto por la dirección IP del servidor

    @FXML
    private TextArea printedMessages;

    @FXML
    private TextField userName;

    @FXML
    private TextField portNumber;

    @FXML
    private TextField clientSendMessage;

    @FXML
    private Button startButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button sendButton;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Thread serverListenerThread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/icesi/chatend/client.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        primaryStage.setTitle("Cliente");
        primaryStage.setScene(new Scene(root, 998, 654));
        primaryStage.show();
    }

    @FXML
    public void onStartButtonClick(ActionEvent event) {
        String userNameText = userName.getText();
        int port = Integer.parseInt(portNumber.getText());

        // Iniciar la conexión en un hilo separado
        new Thread(() -> connectToServer(userNameText, port)).start();
    }

    @FXML
    public void onExitButtonClick(ActionEvent event) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @FXML
    public void onSendButtonClick(ActionEvent event) throws Exception {
        String message = clientSendMessage.getText();
        if (message != null && !message.trim().isEmpty()) {
            sendMessage(message);
            clientSendMessage.clear();
        }
    }

    private void connectToServer(String userName, int port) {

        try {
            socket = new Socket(SERVER_IP, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Actualizar la UI desde el hilo de la aplicación JavaFX
            Platform.runLater(() -> printedMessages.appendText(userName + " conectado al servidor en el puerto: " + port + "\n"));

            // Crear y iniciar el hilo para leer mensajes del servidor
            serverListenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {
                        String finalServerMessage = serverMessage;
                        Platform.runLater(() -> printedMessages.appendText(finalServerMessage + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListenerThread.start();

            // Deshabilitar el botón de iniciar
            Platform.runLater(() -> startButton.setDisable(true));
        } catch (IOException e) {
            Platform.runLater(() -> printedMessages.appendText("Error al conectar con el servidor: " + e.getMessage() + "\n"));
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) throws Exception {
        if (socket != null && socket.isConnected() && output != null) {
            String userNameText = userName.getText();
            String encryptedMessage = Encriptador.cifrarMensaje(userNameText + ": " + message);
            output.println(encryptedMessage);
        } else {
            Platform.runLater(() -> printedMessages.appendText("No está conectado al servidor.\n"));
        }
    }

    private String generarClaveSecreta(String userName) {
        // Asegurar que el nombre de usuario tenga como máximo 16 caracteres
        if (userName.length() > 16) {
            userName = userName.substring(0, 16);
        } else if (userName.length() < 16) {
            // Si el nombre de usuario tiene menos de 16 caracteres, completar con caracteres aleatorios
            SecureRandom random = new SecureRandom();
            byte[] randomBytes = new byte[16 - userName.length()];
            random.nextBytes(randomBytes);
            userName += new String(randomBytes, StandardCharsets.UTF_8);
        }

        return userName;
    }
    public static void main(String[] args) {
        launch(args);
    }
}