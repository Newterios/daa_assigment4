package com.aitbek.metrics;

import java.util.HashMap;
import java.util.Map;

public class OperationMetrics {
    private final Map<String, Integer> counters;
    private long startTime;
    private long endTime;

    public OperationMetrics() {
        this.counters = new HashMap<>();
        reset();
    }

    public void reset() {
        counters.clear();
        startTime = 0;
        endTime = 0;
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void incrementCounter(String name) {
        counters.put(name, counters.getOrDefault(name, Integer.valueOf(0)) + 1);
    }

    public void setCounter(String name, int value) {
        counters.put(name, Integer.valueOf(value));
    }

    public int getCounter(String name) {
        return counters.getOrDefault(name, Integer.valueOf(0)).intValue();
    }

    public Map<String, Integer> getAllCounters() {
        return new HashMap<>(counters);
    }

    @Override
    public String toString() {
        return "OperationMetrics{" +
                "counters=" + counters +
                ", timeNs=" + getElapsedTime() +
                '}';
    }
}