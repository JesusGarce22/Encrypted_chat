package com.icesi.chatend;

import com.icesi.chatend.cliente.Cliente;
import com.icesi.chatend.servidor.Servidor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

/**
 * Clase controladora para la interfaz gráfica principal.
 * Controla los eventos de los botones para iniciar el servidor, abrir el cliente y salir de la aplicación.
 */
public class HelloController {
    /**
     * Instancia del servidor.
     */
    private static Servidor server;

    /**
     * Instancia del cliente.
     */
    private static Cliente client;

    /**
     * Botón para iniciar la interfaz del servidor.
     */
    @FXML
    private Button nextScreenButton;

    /**
     * Botón para abrir la interfaz del cliente.
     */
    @FXML
    private Button clientButton;

    /**
     * Botón para salir de la aplicación.
     */
    @FXML
    private Button exitButton;

    /**
     * Maneja el evento de clic en el botón para iniciar la interfaz del servidor.
     * Crea una instancia del servidor y muestra la interfaz.
     *
     * @param event El evento de acción que desencadena el método.
     */
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

    /**
     * Maneja el evento de clic en el botón para salir de la aplicación.
     */
    @FXML
    protected void onExitButtonClick() {
        System.exit(0);
    }

    /**
     * Maneja el evento de clic en el botón para abrir la interfaz del cliente.
     * Crea una instancia del cliente y muestra la interfaz.
     *
     * @param event El evento de acción que desencadena el método.
     */
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
