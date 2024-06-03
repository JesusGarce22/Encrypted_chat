package com.icesi.chatend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase que representa ela pagina de inicio
 * @author [Jesus Garces - Juan Pablo Acevedo]
 * @version 3.0
 */
public class HelloApplication extends Application {
    /**
     * Método que se ejecuta al iniciar la aplicación.
     * Configura la interfaz gráfica y muestra la ventana del cliente.
     *
     * @param stage El escenario principal de la aplicación.
     * @throws Exception Si ocurre un error al cargar la interfaz gráfica.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 998, 654);
        stage.setTitle("Encripted chat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}