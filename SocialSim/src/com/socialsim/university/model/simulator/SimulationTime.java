package com.socialsim.university.model.simulator;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationTime {

    private static final int DEFAULT_SLEEP_TIME_IN_MILLISECONDS = 1000; // The time a thread has to pause (in milliseconds) in order to make the pace of the simulation conducive to visualization
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

    // Return true unless the given time is ahead of the given ending time
    public boolean isTimeBeforeOrDuring(SimulationTime endingTime) {
        return this.time.compareTo(endingTime.getTime()) <= 0;
    }

    // Increment the time by one second
    public void tick() {
        final long INCREMENT_COUNT = 1L;
        this.setTime(this.time.plusSeconds(INCREMENT_COUNT));
    }

    // Reset the time to the start time
    public void reset() {
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
