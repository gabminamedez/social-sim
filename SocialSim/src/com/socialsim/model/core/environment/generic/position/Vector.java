package com.socialsim.model.core.environment.generic.position;

import java.util.List;

public class Vector {

    private Coordinates startingPosition;
    private double heading;
    private Coordinates futurePosition;
    private double xDisplacement;
    private double yDisplacement;
    private double magnitude;

    public Vector(Coordinates startingPosition, double heading, Coordinates futurePosition, double magnitude) {
        setVector(startingPosition, heading, futurePosition);
        this.magnitude = magnitude;
    }

    private static double computeMagnitude(Coordinates startingPosition, Coordinates futurePosition) {
        return Coordinates.distance(startingPosition, futurePosition);
    }

    public Vector(Coordinates startingPosition, double heading, Coordinates futurePosition, double xDisplacement, double yDisplacement, double magnitude) {
        this.startingPosition = startingPosition;
        this.heading = heading;
        this.futurePosition = futurePosition;
        this.xDisplacement = xDisplacement;
        this.yDisplacement = yDisplacement;
        this.magnitude = magnitude;
    }

    public void setVector(Coordinates currentPosition, double currentHeading, Coordinates futurePosition) {
        this.startingPosition = currentPosition;
        this.heading = currentHeading;
        this.futurePosition = futurePosition;
        this.xDisplacement = this.futurePosition.getX() - this.startingPosition.getX();
        this.yDisplacement = this.futurePosition.getY() - this.startingPosition.getY();
        this.magnitude = Coordinates.distance(this.startingPosition, this.futurePosition);
    }

    public void adjustHeading(double newHeading) {
        Coordinates futurePosition = Coordinates.computeFuturePosition(this.startingPosition, newHeading, this.magnitude);
        this.setVector(this.startingPosition, newHeading, futurePosition);
    }

    public void adjustMagnitude(double newMagnitude) {
        Coordinates futurePosition = Coordinates.computeFuturePosition(this.startingPosition, this.heading, newMagnitude);
        this.setVector(this.startingPosition, this.heading, futurePosition);
    }

    public Coordinates getStartingPosition() {
        return startingPosition;
    }

    public double getHeading() {
        return heading;
    }

    public Coordinates getFuturePosition() {
        return futurePosition;
    }

    public double getXDisplacement() {
        return xDisplacement;
    }

    public double getYDisplacement() {
        return yDisplacement;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public static Vector computeResultantVector(Coordinates startingPosition, List<Vector> vectors) {
        double sumX = 0.0;
        double sumY = 0.0;

        for (Vector vector : vectors) {
            if (vector != null) {
                sumX += vector.getXDisplacement();
                sumY += vector.getYDisplacement();
            }
        }

        double endX = startingPosition.getX() + sumX;
        double endY = startingPosition.getY() + sumY;
        Coordinates endingPosition = new Coordinates(endX, endY);
        double newHeading = Coordinates.headingTowards(startingPosition, endingPosition);

        if (!Double.isNaN(newHeading)) {
            return new Vector(
                    startingPosition,
                    newHeading,
                    endingPosition,
                    sumX,
                    sumY,
                    Vector.computeMagnitude(startingPosition, endingPosition)
            );
        }
        else {
            return null;
        }
    }

}