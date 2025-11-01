package com.aitbek.metrics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OperationMetricsTest {

    @Test
    void testInitialState() {
        OperationMetrics metrics = new OperationMetrics();
        assertEquals(0, metrics.getElapsedTime());
        assertEquals(0, metrics.getCounter("nonexistent"));
    }

    @Test
    void testTimer() throws InterruptedException {
        OperationMetrics metrics = new OperationMetrics();
        metrics.startTimer();
        Thread.sleep(10);
        metrics.stopTimer();
        assertTrue(metrics.getElapsedTime() >= 10_000_000);
    }

    @Test
    void testCounterIncrement() {
        OperationMetrics metrics = new OperationMetrics();
        metrics.incrementCounter("test");
        metrics.incrementCounter("test");
        metrics.incrementCounter("test2");
        assertEquals(2, metrics.getCounter("test"));
        assertEquals(1, metrics.getCounter("test2"));
        assertEquals(0, metrics.getCounter("unknown"));
    }

    @Test
    void testSetCounter() {
        OperationMetrics metrics = new OperationMetrics();
        metrics.setCounter("test", 5);
        assertEquals(5, metrics.getCounter("test"));
        metrics.incrementCounter("test");
        assertEquals(6, metrics.getCounter("test"));
    }

    @Test
    void testReset() {
        OperationMetrics metrics = new OperationMetrics();
        metrics.setCounter("test", 5);
        metrics.startTimer();
        metrics.stopTimer();
        assertTrue(metrics.getElapsedTime() > 0);
        assertEquals(5, metrics.getCounter("test"));
        metrics.reset();
        assertEquals(0, metrics.getElapsedTime());
        assertEquals(0, metrics.getCounter("test"));
    }

    @Test
    void testGetAllCounters() {
        OperationMetrics metrics = new OperationMetrics();
        metrics.incrementCounter("a");
        metrics.incrementCounter("b");
        metrics.incrementCounter("a");
        var counters = metrics.getAllCounters();
        assertEquals(2, counters.size());
        assertEquals(2, counters.get("a"));
        assertEquals(1, counters.get("b"));
    }
}