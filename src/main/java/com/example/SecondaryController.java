package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SecondaryController {

    @FXML
    private VBox vbox; 

    @FXML
    private Button button;
    @FXML
    private Button button1;
    @FXML
    Button button2;

    @FXML
    private void initialize() {
        // Set up the hover sound effect for both buttons
        addHoverSoundEffect(button);
        addHoverSoundEffect(button1);
        addHoverSoundEffect(button2);
    }

    private void addHoverSoundEffect(Button button) {
        button.setOnMouseEntered(event -> SoundManager.playHoverSound());
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    private void switchToOnline() throws IOException {
        App.setRoot("online");
    }

    @FXML
    private void handleExit() {
        // Get the Stage from the current scene
        Stage stage = (Stage) vbox.getScene().getWindow();
        stage.close();
    }
}

