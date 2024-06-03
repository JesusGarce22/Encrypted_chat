package com.icesi.chatend;

import com.icesi.chatend.cliente.Cliente;
import com.icesi.chatend.servidor.Servidor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage; // Import Stage for closing window
import javafx.scene.Node; // Import Node to get the current stage
import javafx.event.ActionEvent; // Import ActionEvent

public class HelloController {
    private static Servidor server;
    private static Cliente client;

    @FXML
    private Button nextScreenButton;

    @FXML
    private Button clientButton;

    @FXML
    private Button exitButton;

    @FXML
    protected void onNextScreenButtonClick(ActionEvent event) {
        server = Servidor.getInstance();
        try {
            Stage serverStage = new Stage();
            server.start(serverStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onExitButtonClick() {
        System.exit(0);
    }

    @FXML
    void onOpenClientButtonClick(ActionEvent event) {
        try {
            client = new Cliente();
            Stage serverStage = new Stage();
            client.start(serverStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
