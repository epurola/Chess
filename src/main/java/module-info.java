module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires java.net.http;
    requires org.java_websocket;
    requires org.json;
    requires java.sql;
    

    opens com.example to javafx.fxml;
    exports com.example;
}
