package com.ternsip.structpro.WorldCache;

class Timer {

    private long lastTime = 0;

    Timer() {
        this.lastTime = System.currentTimeMillis();
    }

    long spent() {
        return System.currentTimeMillis() - lastTime;
    }

    void drop() {
        this.lastTime = System.currentTimeMillis();
    }

}
