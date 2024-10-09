package com.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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

    private static Stage primaryStage; // The main application stage
    private static Scene scene; // The current scene of the application

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage; // Assign the primary stage
        primaryStage.setTitle("Minimal Chess"); // Set the title of the application

        // Load the application icon
        Image icon = new Image(getClass().getResourceAsStream("/com/example/crown.png"));
        primaryStage.getIcons().add(icon);

        // Load the custom cursor image
        Image cursorImage = new Image(getClass().getResource("/com/example/pointer.png").toExternalForm());
        ImageCursor customCursor = new ImageCursor(cursorImage); // Create a custom cursor

        // Load the initial scene from the FXML file
        scene = new Scene(loadFXML("secondary"));
        scene.getStylesheets().add(App.class.getResource("/com/example/styles.css").toExternalForm()); // Add styles
        scene.setCursor(customCursor); // Set the custom cursor for the scene
        primaryStage.setScene(scene); // Set the scene to the primary stage

        // Set the application to full screen and hide the exit hint
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show(); // Display the primary stage
    }

    // Method to set the root of the scene with a fade transition
    public static void setRoot(String fxml) throws IOException {
        Parent newRoot = loadFXML(fxml); // Load the new FXML root

        // Ensure the application remains in full screen
        primaryStage.setFullScreen(true);

        if (scene != null) {
            Parent oldRoot = scene.getRoot(); // Get the current root

            // Create a fade-out transition for the old root
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldRoot);
            fadeOut.setFromValue(1.0); // Start fully visible
            fadeOut.setToValue(0.0); // Fade to invisible

            // Create a fade-in transition for the new root
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0); // Start invisible
            fadeIn.setToValue(1.0); // Fade to fully visible

            // Chain the transitions so they occur sequentially
            fadeOut.setOnFinished(event -> {
                scene.setRoot(newRoot); // Set the new root after fade out
                fadeIn.play(); // Play the fade-in transition
            });
            fadeOut.play(); // Start the fade-out transition
        } else {
            // If the scene is not yet initialized, set the root directly
            scene.setRoot(newRoot);
            newRoot.setOpacity(0.0); // Set opacity for fade-in transition

            // Create and play the fade-in transition for the initial scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    // Method to load the FXML file
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml")); // Load the FXML file
        return fxmlLoader.load(); // Return the loaded parent
    }

    // Method to retrieve the local IP address
    private String getLocalIPAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost(); // Get local host address
            return inetAddress.getHostAddress(); // Return the IP address
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Print the stack trace in case of an error
            return "localhost"; // Fallback to localhost if IP address cannot be determined
        }
    }

    // Method to handle application stop event
    @Override
    public void stop() {
        ExecutorServiceManager.shutdown(); // Shut down any executors
        ChessWebSocketServer.getInstance(new InetSocketAddress(getLocalIPAddress(), 8887)).stopServer(); // Stop the
                                                                                                         // WebSocket
                                                                                                         // server
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args); // Start the JavaFX application
    }
}
