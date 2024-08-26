package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceManager {
    private static ExecutorService analysisExecutor = Executors.newFixedThreadPool(1); // Singleton instance

    private ExecutorServiceManager() {}

    public static ExecutorService getExecutorService() {
        return analysisExecutor;
    }

    public static void shutdown() {
        analysisExecutor.shutdown();
        try {
            if (!analysisExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                analysisExecutor.shutdownNow();
                if (!analysisExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate in the specified time.");
                }
            }
        } catch (InterruptedException ex) {
            analysisExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

