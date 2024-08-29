package com.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class PythonScriptRunner {

    private String pythonScriptPath;

    // Constructor to set the path to the Python script
    public PythonScriptRunner(String scriptPath) {
        this.pythonScriptPath = scriptPath;
    }

    // Method to run the Python script with the given prompt
    public String runScript(String prompt) throws IOException, InterruptedException {
        // Build the command to execute the Python script with the prompt
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, prompt);
        Process process = processBuilder.start();

        // Read the output from the Python script
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Wait for the process to complete and check exit code
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Python script failed with exit code " + exitCode);
        }

        return output.toString();
    }

    // Optional: Method to handle errors or process output
    public void handleError(Exception e) {
        e.printStackTrace();  // Handle the error according to your application's needs
    }
}
