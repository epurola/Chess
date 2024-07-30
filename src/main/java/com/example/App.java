package com.example;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file and set the scene
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/primary.fxml"));
        scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/com/example/styles.css").toExternalForm());

        // Configure the stage
        primaryStage.setTitle("Chess Game");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    // Method to set the root in the current scene
    static void setRoot(String fxml) throws IOException {
        if (scene != null) {
            scene.setRoot(loadFXML(fxml));
        } else {
            throw new IllegalStateException("Scene has not been initialized");
        }
    }

    // Method to load the FXML file
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}

