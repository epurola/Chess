package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecondaryController {

    @FXML
    private VBox vbox; 

    @FXML
    private Button button;
    @FXML
    private Button button1;
    @FXML
    Button button2;
    private static final Logger logger = LoggerFactory.getLogger(SecondaryController.class);

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
        logger.info("Attempting to switch to 'primary' view.");
        try {
            App.setRoot("primary");
            logger.info("Successfully switched to 'primary' view.");
        } catch (IOException e) {
            logger.error("Failed to switch to 'primary' view", e);
            throw e; // Re-throw the exception to allow the caller to handle it if needed
        }
    }
    @FXML
    private void switchToOnline() throws IOException {
        App.setRoot("online");
    }
    @FXML
    private void switchToBot() throws IOException {
        App.setRoot("replay");
    }

    @FXML
    private void handleExit() {
        // Get the Stage from the current scene
        Stage stage = (Stage) vbox.getScene().getWindow();
        stage.close();
    }
}

