package com.ternsip.structpro.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* Simple thread pool class */
public class Pool {

    /* General executor service */
    private final ExecutorService executor;

    /* Construct new thread pool */
    public Pool() {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    /* Add remain process */
    public void add(Runnable runnable) {
        executor.submit(runnable);
    }

    /* Wait for finishing all threads limited by time measured in seconds */
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
