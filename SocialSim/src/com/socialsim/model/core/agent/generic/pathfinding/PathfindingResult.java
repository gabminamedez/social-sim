package com.socialsim.model.core.agent.generic.pathfinding;

public abstract class PathfindingResult {

    private final double distance;

    public PathfindingResult(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

}