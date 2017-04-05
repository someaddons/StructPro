package com.ternsip.structpro.Utils;

public class Timer {

    private long timeout = 0;

    /* Holding last tick */
    private long lastTime = 0;

    /* Construct time and register current time */
    public Timer(long timeout) {
        this.timeout = timeout;
        this.lastTime = System.currentTimeMillis();
    }

    /* How much time spent in milliseconds */
    private long spent() {
        return System.currentTimeMillis() - lastTime;
    }

    /* Drop timer time */
    public void drop() {
        this.lastTime = System.currentTimeMillis();
    }

    public boolean isOver() {
        return spent() > timeout;
    }

}
