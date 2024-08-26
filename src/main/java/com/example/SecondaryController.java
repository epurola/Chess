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
    private Button button2;

    private static final Logger logger = LoggerFactory.getLogger(SecondaryController.class);

    @FXML
    private void initialize() {
        // Set up the hover sound effect for both buttons
        addHoverSoundEffect(button);
        addHoverSoundEffect(button1);
        addHoverSoundEffect(button2);
    }

    private void addHoverSoundEffect(Button button) {
        button.setOnMouseEntered(event -> {
            try {
                SoundManager.playHoverSound();
            } catch (Exception e) {
                logger.error("Error playing hover sound for button: " + button.getId(), e);
            }
        });
    }

    @FXML
    private void switchToPrimary() throws IOException {
        
            App.setRoot("primary");
       
    }

    @FXML
    private void switchToOnline() {
        try {
            App.setRoot("online");
        } catch (IOException e) {
            logger.error("Failed to switch to online view", e);
        }
    }

    @FXML
    private void switchToBot() {
        try {
            App.setRoot("replay");
        } catch (IOException e) {
            logger.error("Failed to switch to bot view", e);
        }
    }

    @FXML
    private void handleExit() {
        try {
            // Get the Stage from the current scene
            Stage stage = (Stage) vbox.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            logger.error("Error occurred while closing the stage", e);
        }
    }
}


