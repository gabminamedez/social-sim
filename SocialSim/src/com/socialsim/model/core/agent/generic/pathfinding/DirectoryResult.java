package com.socialsim.model.core.agent.generic.pathfinding;

import com.socialsim.model.core.environment.generic.patchobject.Amenity;

public class DirectoryResult {

    private final Amenity goalAmenity;
    private final double distance;

    public DirectoryResult(Amenity goalAmenity, double distance) {
        this.goalAmenity = goalAmenity;
        this.distance = distance;
    }

    public Amenity getGoalAmenity() {
        return goalAmenity;
    }

    public double getDistance() {
        return distance;
    }

}