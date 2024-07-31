package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SecondaryController {

    @FXML
    private VBox vbox; // Ensure this matches the FXML id

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void handleExit() {
        // Get the Stage from the current scene
        Stage stage = (Stage) vbox.getScene().getWindow();
        stage.close();
    }
}
