module com.icesi.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires eu.hansolo.fx.countries;
    requires eu.hansolo.fx.heatmap;
    requires eu.hansolo.toolboxfx;
    requires eu.hansolo.toolbox;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires org.bouncycastle.provider;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.websocket;
    requires spring.messaging;
    requires spring.context;

    opens com.icesi.chatend to javafx.fxml;
    opens com.icesi.chatend.servidor to javafx.fxml;
    opens com.icesi.chatend.cliente to javafx.fxml;
    exports com.icesi.chatend.servidor;
    exports com.icesi.chatend.cliente;
    exports com.icesi.chatend;
}
