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

/**
 * Clase que representa el cliente del chat.
 * Permite a los usuarios enviar y recibir mensajes en el chat.
 * @author [Jesus Garces - Juan Pablo Acevedo]
 * @version 3.0
 */
public class Cliente extends Application {
    /**
     * Dirección IP del servidor.
     */
    private static final String SERVER_IP = "192.168.1.8"; // Cambia esto por la dirección IP del servidor

    /**
     * Área de texto donde se muestran los mensajes.
     */
    @FXML
    private TextArea printedMessages;

    /**
     * Campo de texto para ingresar el nombre de usuario.
     */
    @FXML
    private TextField userName;

    /**
     * Campo de texto para ingresar el número de puerto del servidor.
     */
    @FXML
    private TextField portNumber;

    /**
     * Campo de texto para ingresar el mensaje que el usuario desea enviar.
     */
    @FXML
    private TextField clientSendMessage;

    /**
     * Botón para iniciar la conexión con el servidor.
     */
    @FXML
    private Button startButton;

    /**
     * Botón para salir del cliente.
     */
    @FXML
    private Button exitButton;

    /**
     * Botón para enviar un mensaje al servidor.
     */
    @FXML
    private Button sendButton;

    /**
     * Socket para la conexión con el servidor.
     */
    private Socket socket;

    /**
     * Flujo de entrada para recibir mensajes del servidor.
     */
    private BufferedReader input;

    /**
     * Flujo de salida para enviar mensajes al servidor.
     */
    private PrintWriter output;

    /**
     * Hilo para escuchar mensajes del servidor.
     */
    private Thread serverListenerThread;

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * Configura la interfaz gráfica y muestra la ventana del cliente.
     *
     * @param primaryStage El escenario principal de la aplicación.
     * @throws Exception Si ocurre un error al cargar la interfaz gráfica.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/icesi/chatend/client.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        primaryStage.setTitle("Cliente");
        primaryStage.setScene(new Scene(root, 998, 654));
        primaryStage.show();
    }

    /**
     * Método que se ejecuta al hacer clic en el botón de iniciar conexión.
     * Inicia la conexión con el servidor en un hilo separado.
     *
     * @param event El evento de acción que desencadena el método.
     */
    @FXML
    public void onStartButtonClick(ActionEvent event) {
        String userNameText = userName.getText();
        int port = Integer.parseInt(portNumber.getText());

        // Iniciar la conexión en un hilo separado
        new Thread(() -> connectToServer(userNameText, port)).start();
    }

    /**
     * Método que se ejecuta al hacer clic en el botón de salir.
     * Cierra la conexión con el servidor y termina la aplicación.
     *
     * @param event El evento de acción que desencadena el método.
     */
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

    /**
     * Método que se ejecuta al hacer clic en el botón de enviar mensaje.
     * Envía el mensaje ingresado por el usuario al servidor.
     *
     * @param event El evento de acción que desencadena el método.
     * @throws Exception Si ocurre un error al enviar el mensaje.
     */
    @FXML
    public void onSendButtonClick(ActionEvent event) throws Exception {
        String message = clientSendMessage.getText();
        if (message != null && !message.trim().isEmpty()) {
            sendMessage(message);
            clientSendMessage.clear();
        }
    }

    /**
     * Establece la conexión con el servidor.
     * Crea el socket, los flujos de entrada y salida, y comienza el hilo para escuchar mensajes del servidor.
     *
     * @param userName El nombre de usuario del cliente.
     * @param port     El número de puerto del servidor.
     */
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

    /**
     * Envía un mensaje al servidor.
     *
     * @param message El mensaje a enviar.
     * @throws Exception Si ocurre un error al enviar el mensaje.
     */
    private void sendMessage(String message) throws Exception {
        if (socket != null && socket.isConnected() && output != null) {
            String userNameText = userName.getText();
            String encryptedMessage = Encriptador.cifrarMensaje(userNameText + ": " + message);
            output.println(encryptedMessage);
        } else {
            Platform.runLater(() -> printedMessages.appendText("No está conectado al servidor.\n"));
        }
    }

    /**
     * Genera una clave secreta basada en el nombre de usuario.
     * La clave secreta tiene una longitud fija de 16 caracteres.
     *
     * @param userName El nombre de usuario del cliente.
     * @return La clave secreta generada.
     */
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

    /**
     * Método principal de la aplicación.
     * Lanza la aplicación de cliente.
     *
     * @param args Los argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

