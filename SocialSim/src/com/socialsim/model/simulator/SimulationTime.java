package com.socialsim.model.simulator;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationTime {

    private static final int DEFAULT_SLEEP_TIME_IN_MILLISECONDS = 1000;
    public static final AtomicInteger SLEEP_TIME_MILLISECONDS = new AtomicInteger(DEFAULT_SLEEP_TIME_IN_MILLISECONDS);

    private final LocalTime startTime;
    private LocalTime time;

    public SimulationTime(SimulationTime simulationTime) {
        this.time = LocalTime.of(simulationTime.getTime().getHour(), simulationTime.getTime().getMinute(), simulationTime.getTime().getSecond());
        this.startTime = this.time;
    }

    public SimulationTime(LocalTime time) {
        this.time = time;
        this.startTime = this.time;
    }

    public SimulationTime(int hour, int minute, int second) {
        this.time = LocalTime.of(hour, minute, second);
        this.startTime = this.time;
    }

    public void tick() {
        final long INCREMENT_COUNT = 5L;
        this.setTime(this.time.plusSeconds(INCREMENT_COUNT));
    }

    public void reset() { // Reset the time to the start time
        this.setTime(this.startTime);
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

}