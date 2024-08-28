package com.example;

import java.io.IOException;

import com.example.WebSocket.ChessWebSocketServer;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static Stage primaryStage;
    private static Scene scene;
    private static SinglePlayerController controller;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Minimal Chess");
        Image icon = new Image(getClass().getResourceAsStream("/com/example/crown.png"));
        primaryStage.getIcons().add(icon);
        Image cursorImage = new Image(getClass().getResource("/com/example/pointer.png").toExternalForm());

        
       
        ImageCursor customCursor = new ImageCursor(cursorImage);
        

        // Load the initial scene
        scene = new Scene(loadFXML("secondary")); // Load the initial FXML
        scene.getStylesheets().add(App.class.getResource("/com/example/styles.css").toExternalForm());
        scene.setCursor(customCursor);
        primaryStage.setScene(scene);
        // Set the initial full screen and hide the full screen exit hint
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }
    // Method to set the root with animation
    public static void setRoot(String fxml) throws IOException {
        Parent newRoot = loadFXML(fxml);
        // Ensure we are in full screen before changing the root
        primaryStage.setFullScreen(true);
        if (scene != null) {
            Parent oldRoot = scene.getRoot();
            // Apply fade-out transition to the old root
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            // Apply fade-in transition to the new root
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            // Chain the transitions
            fadeOut.setOnFinished(event -> {
                scene.setRoot(newRoot);
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            // If the scene is not yet initialized, just set the root directly
            scene.setRoot(newRoot);
            // Apply fade-in transition to the initial scene
            newRoot.setOpacity(0.0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    // Method to load the FXML file
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
     public void stop() {
    ExecutorServiceManager.shutdown();
      }
      
    public static void main(String[] args) {
        launch(args);
    }
}