package com.example;

import javafx.animation.AnimationTimer;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

public class CountdownClock extends Parent {
    private final Line secondHand; // Rotating line inside the circle
    private final Rotate rotate;   // Rotate transform for rotating the line
    private AnimationTimer timer;  // Reference to the AnimationTimer
    private long lastUpdate = 0; // Last update time
    private double currentAngle = 0; // Current angle of the second hand
    private double updateInterval = 1.0; // Update interval in seconds

    public CountdownClock() {
        // Create a circle
        Circle circle = new Circle(10);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);

        // Create a line to act as the second hand
        secondHand = new Line(0, 0, 0, -7); // Line extending upward
        secondHand.setStroke(Color.BLUE);
        secondHand.setStrokeWidth(1.5);

        // Create a Rotate transform
        rotate = new Rotate();
        rotate.setPivotX(circle.getCenterX());
        rotate.setPivotY(circle.getCenterY());
        secondHand.getTransforms().add(rotate);

        getChildren().addAll(circle, secondHand);

    }

    public void startCountdown() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return; // Skip the first call to initialize lastUpdate
                }

                double elapsedTime = (now - lastUpdate) / 1_000_000_000.0; // Convert to seconds
                if (elapsedTime >= updateInterval) {
                    // Update the angle
                    currentAngle = (currentAngle + 90) % 360;
                    rotate.setAngle(currentAngle);

                    // Update lastUpdate time
                    lastUpdate = now;
                }
            }
        };

        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
}