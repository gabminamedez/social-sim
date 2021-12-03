package com.socialsim.model.core.agent;

import com.socialsim.model.core.environment.university.UniversityPatch;

import java.util.List;

public class PathfindingResult {

    private final List<UniversityPatch> path;
    private final double distance;

    public PathfindingResult(List<UniversityPatch> path, double distance) {
        this.path = path;
        this.distance = distance;
    }

    public List<UniversityPatch> getPath() {
        return path;
    }

    public double getDistance() {
        return distance;
    }

}