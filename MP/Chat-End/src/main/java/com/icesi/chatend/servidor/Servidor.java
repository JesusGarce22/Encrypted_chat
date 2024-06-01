package com.icesi.chatend.servidor;

import com.icesi.chatend.encriptacion.Encriptador;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor extends Application {

    private static Servidor instance;

    private static final int PORT_MIN = 12345;
    private static final int PORT_MAX = 12350;
    private static final String SERVER_IP = "localhost";
    private static Map<Integer, List<PrintWriter>> portClientsMap = new HashMap<>();

    private ServerSocket serverSocket;

    @FXML
    private Button startButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextArea printedMessages;

    private Servidor() {
        // Private constructor to prevent instantiation
    }

    public static Servidor getInstance() {
        if (instance == null) {
            instance = new Servidor();
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/icesi/chatend/server.fxml"));
        loader.setController(this);
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Servidor de Chat");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void onStartButtonClick() {
        new Thread(() -> {
            try {
                for (int port = PORT_MIN; port <= PORT_MAX; port++) {
                    serverSocket = new ServerSocket(port);
                    appendMessage("Servidor iniciado en el puerto: " + port);
                    portClientsMap.put(port, new ArrayList<>());

                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        appendMessage("Cliente conectado desde: " + clientSocket.getInetAddress());

                        int pairedPort = (port % 2 == 0) ? port - 1 : port + 1;
                        Thread clientThread = new Thread(new ClientHandler(clientSocket, pairedPort));
                        clientThread.start();
                    }
                }
            } catch (IOException e) {
                appendMessage("Error al iniciar el servidor: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void onExitButtonClick() {
        System.exit(0);
    }

    private void appendMessage(String message) {
        Platform.runLater(() -> printedMessages.appendText(message + "\n"));
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientOutput;
        private int pairedPort;

        public ClientHandler(Socket clientSocket, int pairedPort) {
            this.clientSocket = clientSocket;
            this.pairedPort = pairedPort;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);

                synchronized (portClientsMap) {
                    if (!portClientsMap.containsKey(pairedPort)) {
                        portClientsMap.put(pairedPort, new ArrayList<>());
                    }
                }

                synchronized (portClientsMap) {
                    portClientsMap.get(pairedPort).add(clientOutput);
                }

                String message;
                while ((message = input.readLine()) != null) {
                    String decryptedMessage = Encriptador.descifrarMensaje(message);
                    sendToPairedClient(decryptedMessage);
                }

                synchronized (portClientsMap) {
                    portClientsMap.get(pairedPort).remove(clientOutput);
                }

                input.close();
                clientOutput.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendToPairedClient(String message) {
            synchronized (portClientsMap) {
                for (PrintWriter client : portClientsMap.get(pairedPort)) {
                    client.println(message);
                }
            }
        }
    }
}