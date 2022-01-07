package com.socialsim.model.core.environment.generic.position;

import com.socialsim.model.core.environment.generic.Patch;

import java.util.Objects;

public class Coordinates extends Location { // Represents a pair of 2D Cartesian coordinates in the simulation

    private double x;
    private double y;

    public Coordinates(Coordinates coordinates) {
        this.x = coordinates.getX();
        this.y = coordinates.getY();
    }

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinates getPatchCenterCoordinates(Patch patch) {
        double column = patch.getMatrixPosition().getColumn();
        double row = patch.getMatrixPosition().getRow();

        double centeredX = column * Patch.PATCH_SIZE_IN_SQUARE_METERS + Patch.PATCH_SIZE_IN_SQUARE_METERS * 0.5;
        double centeredY = row * Patch.PATCH_SIZE_IN_SQUARE_METERS + Patch.PATCH_SIZE_IN_SQUARE_METERS * 0.5;

        return new Coordinates(centeredX, centeredY);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static double distance(Coordinates sourceCoordinates, Coordinates targetCoordinates) {
        double x = targetCoordinates.getX();
        double y = targetCoordinates.getY();

        return Math.sqrt(Math.pow(x - sourceCoordinates.getX(), 2) + Math.pow(y - sourceCoordinates.getY(), 2));
    }

    public static double distance(Patch sourcePatch, Patch targetPatch) {
        return Coordinates.distance(sourcePatch.getPatchCenterCoordinates(), targetPatch.getPatchCenterCoordinates());
    }

    public static double headingTowards(Coordinates sourceCoordinates, Coordinates targetCoordinates) {
        double x = targetCoordinates.getX();
        double y = targetCoordinates.getY();
        double dx = x - sourceCoordinates.getX();
        double dy = y - sourceCoordinates.getY();
        double adjacentLength = dx;
        double hypotenuseLength = distance(sourceCoordinates, targetCoordinates);
        double angle = Math.acos(adjacentLength / hypotenuseLength);

        if (dy > 0) {
            angle = 2.0 * Math.PI - angle;
        }

        return angle;
    }

    public static boolean isWithinFieldOfView(Coordinates sourceCoordinates, Coordinates targetCoordinates, double heading, double maximumHeadingChange) {
        double headingTowardsCoordinate = headingTowards(sourceCoordinates, targetCoordinates);
        double headingDifference = Coordinates.headingDifference(headingTowardsCoordinate, heading);

        return headingDifference <= maximumHeadingChange;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public static double headingDifference(double heading1, double heading2) {
        double headingDifference = Math.abs(heading1 - heading2);

        if (headingDifference > Math.toRadians(180)) {
            headingDifference = Math.toRadians(360) - headingDifference;
        }

        return headingDifference;
    }

    public static double meanHeading(double... headings) {
        double xHeadingSum = 0.0;
        double yHeadingSum = 0.0;

        for (double heading : headings) {
            xHeadingSum += Math.cos(heading);
            yHeadingSum += Math.sin(heading);
        }

        return Math.atan2(yHeadingSum / headings.length, xHeadingSum / headings.length);
    }

    public static Coordinates computeFuturePosition(Coordinates startingPosition, double heading, double magnitude) {
        double newX = startingPosition.getX() + Math.cos(heading) * magnitude;
        double newY = startingPosition.getY() - Math.sin(heading) * magnitude;

        return new Coordinates(newX, newY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;

        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static class PatchPair {
        private final Patch patch1;
        private final Patch patch2;

        public PatchPair(Patch patch1, Patch patch2) {
            this.patch1 = patch1;
            this.patch2 = patch2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PatchPair that = (PatchPair) o;

            return patch1.equals(that.patch1) && patch2.equals(that.patch2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(patch1, patch2);
        }
    }

}