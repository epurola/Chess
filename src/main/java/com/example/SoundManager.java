package com.example;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

public class SoundManager {
    private static final String MOVE_SOUND_PATH = "/com/example/move-self.mp3";
    private static final String CAPTURE_SOUND_PATH = "/com/example/capture.mp3";
    private static final String NOTIFY_SOUND_PATH = "/com/example/notify.mp3";
    private static final String BUTTON_SOUND_PATH = "/com/example/button.mp3";
    private static final String WIN_SOUND_PATH = "/com/example/WIN.mp3";

    // Create MediaPlayers dynamically
    private static MediaPlayer createMediaPlayer(String path) {
        URL soundURL = SoundManager.class.getResource(path);
        if (soundURL == null) {
            throw new RuntimeException("Sound file not found: " + path);
        }
        Media media = new Media(soundURL.toString());
        return new MediaPlayer(media);
    }

    // Static methods for common sounds
    public static void playMoveSound() {
        MediaPlayer movePlayer = createMediaPlayer(MOVE_SOUND_PATH);
        playSound(movePlayer);
    }
    public static void playWinSound() {
        MediaPlayer movePlayer = createMediaPlayer(WIN_SOUND_PATH);
        playSound(movePlayer);
    }

    public static void playCaptureSound() {
        MediaPlayer capturePlayer = createMediaPlayer(CAPTURE_SOUND_PATH);
        playSound(capturePlayer);
    }

    public static void playNotifySound() {
        MediaPlayer notifyPlayer = createMediaPlayer(NOTIFY_SOUND_PATH);
        playSound(notifyPlayer);
    }

    public static void playButtonSound() {
        MediaPlayer buttonPlayer = createMediaPlayer(BUTTON_SOUND_PATH);
        playSound(buttonPlayer);
    }
    private static final MediaPlayer HOVER_PLAYER = createMediaPlayer(BUTTON_SOUND_PATH);

    public static void playHoverSound() {
        HOVER_PLAYER.seek(Duration.ZERO); // Rewind to start
        HOVER_PLAYER.play();
    }

    // Utility method to play a sound with a MediaPlayer
    private static void playSound(MediaPlayer player) {
        player.seek(Duration.ZERO); // Rewind to start
        player.play();
    }
}




