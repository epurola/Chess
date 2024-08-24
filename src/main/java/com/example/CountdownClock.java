package com.example;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class CountdownClock extends Parent {
    private final Line secondHand; // Rotating line inside the circle
    private final Rotate rotate;   // Rotate transform for rotating the line
    private long lastUpdate = 0;   // Last update time
    private double currentAngle = 0; // Current angle of the second hand
    private Timeline sharedTimeline;  // Shared timeline between clock and countdown timer
    private Label playerTimerLabel;
    private int playerTimeLeft = 600;
    private boolean isRunning = false; // To keep track of the timer state
    private SinglePlayerController controller;
    private MultiplayerController Mcontroller;
    private BotController bController;

    public CountdownClock(SinglePlayerController controller) {
        // Create a circle
        this.controller = controller;
        Circle circle = new Circle(10);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);
        // Create a line to act as the second hand
        secondHand = new Line(0, 0, 0, -7); // Line extending upward
        secondHand.setStroke(Color.BLUE);
        secondHand.setStrokeWidth(1.5);
        Group clockGroup = new Group(circle, secondHand);
        // Create a Rotate transform
        rotate = new Rotate();
        rotate.setPivotX(circle.getCenterX());
        rotate.setPivotY(circle.getCenterY());
        secondHand.getTransforms().add(rotate);
        playerTimerLabel = new Label();
        playerTimerLabel.setText("10:00");  // Set the initial time to 10:00 minutes
        playerTimerLabel.setStyle("-fx-font-size: 16; -fx-text-fill: black; -fx-background-color: transparent;");

        HBox layout = new HBox(10);  // 10 is the spacing between the clock and the label
        layout.setAlignment(Pos.CENTER);  // Center the contents horizontally
        layout.getChildren().addAll(clockGroup, playerTimerLabel);

        // Initialize the timers and the shared timeline
        initializeTimers();

        // Add the HBox to the parent node
        getChildren().add(layout);
    }
    public CountdownClock(MultiplayerController Mcontroller) {
        // Create a circle
        this.Mcontroller = Mcontroller;
        Circle circle = new Circle(10);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);
        // Create a line to act as the second hand
        secondHand = new Line(0, 0, 0, -7); // Line extending upward
        secondHand.setStroke(Color.BLUE);
        secondHand.setStrokeWidth(1.5);
        Group clockGroup = new Group(circle, secondHand);
        // Create a Rotate transform
        rotate = new Rotate();
        rotate.setPivotX(circle.getCenterX());
        rotate.setPivotY(circle.getCenterY());
        secondHand.getTransforms().add(rotate);
        playerTimerLabel = new Label();
        playerTimerLabel.setText("10:00");  // Set the initial time to 10:00 minutes
        playerTimerLabel.setStyle("-fx-font-size: 16; -fx-text-fill: black; -fx-background-color: transparent;");

        HBox layout = new HBox(10);  // 10 is the spacing between the clock and the label
        layout.setAlignment(Pos.CENTER);  // Center the contents horizontally
        layout.getChildren().addAll(clockGroup, playerTimerLabel);

        // Initialize the timers and the shared timeline
        initializeTimers();

        // Add the HBox to the parent node
        getChildren().add(layout);
    }
    public CountdownClock(BotController Bcontroller) {
        // Create a circle
        this.bController = Bcontroller;
        Circle circle = new Circle(10);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);
        // Create a line to act as the second hand
        secondHand = new Line(0, 0, 0, -7); // Line extending upward
        secondHand.setStroke(Color.BLUE);
        secondHand.setStrokeWidth(1.5);
        Group clockGroup = new Group(circle, secondHand);
        // Create a Rotate transform
        rotate = new Rotate();
        rotate.setPivotX(circle.getCenterX());
        rotate.setPivotY(circle.getCenterY());
        secondHand.getTransforms().add(rotate);
        playerTimerLabel = new Label();
        playerTimerLabel.setText("10:00");  // Set the initial time to 10:00 minutes
        playerTimerLabel.setStyle("-fx-font-size: 16; -fx-text-fill: black; -fx-background-color: transparent;");

        HBox layout = new HBox(10);  // 10 is the spacing between the clock and the label
        layout.setAlignment(Pos.CENTER);  // Center the contents horizontally
        layout.getChildren().addAll(clockGroup, playerTimerLabel);

        // Initialize the timers and the shared timeline
        initializeTimers();

        // Add the HBox to the parent node
        getChildren().add(layout);
    }

    private void initializeTimers() {
        // Timer for both clock (second hand) and player's countdown using sharedTimeline
        sharedTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Handle the player's countdown
            playerTimeLeft--;
            updateTimerLabel(playerTimerLabel, playerTimeLeft);
            // Update the clock's second hand angle
            currentAngle = (currentAngle + 90) % 360; // 90 degrees per second 
            rotate.setAngle(currentAngle);
            if (playerTimeLeft <= 0) {
                sharedTimeline.stop();
                onTimeOut();
                isRunning = false;
            }
        }));

        sharedTimeline.setCycleCount(Timeline.INDEFINITE); // Keep running until stopped
        updateTimerLabel(playerTimerLabel, playerTimeLeft); // Update the label with the initial time
    }

    public void start() {
        if (!isRunning) {
            sharedTimeline.play(); // Start the shared timeline
            lastUpdate = System.nanoTime(); // Initialize the update time
            isRunning = true;
        }
    }

    public void pause() {
        if (isRunning) {
            sharedTimeline.pause(); // Pause the shared timeline
            isRunning = false;
        }
    }

    public void stop() {
        if (isRunning) {
            sharedTimeline.stop(); // Stop the shared timeline
            isRunning = false;
        }
    }
    private void onTimeOut() {
        System.out.print("This WAS TRIGGERED");
         controller.onTimeOut();
    }

    private void updateTimerLabel(Label label, int timeLeft) {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        Platform.runLater(() -> label.setText(String.format("%02d:%02d", minutes, seconds)));
    }
}

