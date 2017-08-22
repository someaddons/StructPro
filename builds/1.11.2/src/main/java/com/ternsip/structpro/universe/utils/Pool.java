package com.ternsip.structpro.universe.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simple thread pool class
 * Provide control over asynchronous runnable tasks
 * @author Ternsip
 */
public class Pool {

    /** General executor service */
    private final ExecutorService executor;

    /** Construct new thread pool */
    public Pool() {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    /**
     * Add remain process
     * @param runnable Runnable task
     */
    public void add(Runnable runnable) {
        executor.submit(runnable);
    }

    /**
     * Wait for finishing all threads limited by time measured in seconds
     * @param seconds Seconds limit for waiting
     */
    public void wait(int seconds) {
        executor.shutdown();
        try {
            executor.awaitTermination(seconds, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            new Report()
                    .post("PROCESS TIMEOUT", ie.getMessage())
                    .post("TERMINATING PROCESS", "NOW")
                    .print();
            executor.shutdownNow();
        }
    }
}
