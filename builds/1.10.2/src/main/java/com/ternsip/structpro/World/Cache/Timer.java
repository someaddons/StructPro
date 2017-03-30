package com.ternsip.structpro.World.Cache;

class Timer {

    /* Holding last tick */
    private long lastTime = 0;

    /* Construct time and register current time */
    Timer() {
        this.lastTime = System.currentTimeMillis();
    }

    /* How much time spent in milliseconds */
    long spent() {
        return System.currentTimeMillis() - lastTime;
    }

    /* Drop timer time */
    void drop() {
        this.lastTime = System.currentTimeMillis();
    }

}
