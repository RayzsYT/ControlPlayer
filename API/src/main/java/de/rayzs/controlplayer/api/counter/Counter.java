package de.rayzs.controlplayer.api.counter;

public class Counter {

    private int count, maxTime;
    private long currentTime;

    public Counter(int maxTime) {
        count = 0;
        this.maxTime = maxTime;
        currentTime = System.currentTimeMillis();
    }

    public Counter() {
        count = 0;
        maxTime = 1000;
        currentTime = System.currentTimeMillis();
    }

    public void add() {
        if(count >= Integer.MAX_VALUE) set(0);
        count++;
    }

    public void set(final int value) {
        this.count = value;
    }

    public int getCount() {
        final long calculatedTime = System.currentTimeMillis() - currentTime;
        if(calculatedTime > maxTime) {
            currentTime = System.currentTimeMillis();
            count = 0;
        }
        return count;
    }
}
