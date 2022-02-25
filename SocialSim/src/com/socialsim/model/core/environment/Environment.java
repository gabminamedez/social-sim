package com.socialsim.model.core.environment;

import com.socialsim.controller.Main;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.MatrixPosition;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.simulator.SimulationObject;
import java.io.Serializable;
import java.util.*;

public abstract class Environment extends BaseObject implements SimulationObject, Serializable {

    private final int rows;
    private final int columns;
    private final Patch[][] patches;

    public Environment(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.patches = new Patch[rows][columns];
        initializePatches();
    }

    private void initializePatches() {
        MatrixPosition matrixPosition;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrixPosition = new MatrixPosition(row, column);
                patches[row][column] = new Patch(this, matrixPosition);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Patch getPatch(Coordinates coordinates) {
        return getPatch((int) (coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS), (int) (coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS));
    }

    public Patch getPatch(MatrixPosition matrixPosition) {
        return getPatch(matrixPosition.getRow(), matrixPosition.getColumn());
    }

    public Patch getPatch(int row, int column) {
        return patches[row][column];
    }

    public Patch[][] getPatches() {
        return this.patches;
    }

    public static List<Patch> get7x7Field(Environment environment, Patch centerPatch, double heading, boolean includeCenterPatch, double fieldOfViewAngle) {
        int truncatedX = (int) (centerPatch.getPatchCenterCoordinates().getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (centerPatch.getPatchCenterCoordinates().getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);

        Patch chosenPatch = null;
        List<Patch> patchesToExplore = new ArrayList<>();

        for (int rowOffset = -3; rowOffset <= 3; rowOffset++) {
            for (int columnOffset = -3; columnOffset <= 3; columnOffset++) {
                boolean xCondition;
                boolean yCondition;
                boolean isCenterPatch = rowOffset == 0 && columnOffset == 0;

                if (!includeCenterPatch) {
                    if (isCenterPatch) {
                        continue;
                    }
                }

                if (rowOffset < 0) {
                    yCondition = truncatedY + rowOffset > 0;
                }
                else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < 60;
                }
                else {
                    yCondition = true;
                }

                if (columnOffset < 0) {
                    xCondition = truncatedX + columnOffset > 0;
                }
                else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < 120;
                }
                else {
                    xCondition = true;
                }

                if (xCondition && yCondition) {
                    if (environment instanceof University) {
                        chosenPatch = Main.universitySimulator.getUniversity().getPatch(truncatedY + rowOffset, truncatedX + columnOffset);
                    }

                    if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(centerPatch.getPatchCenterCoordinates(), chosenPatch.getPatchCenterCoordinates(), heading, fieldOfViewAngle)) {
                        patchesToExplore.add(chosenPatch);
                    }
                }
            }
        }

        return patchesToExplore;
    }

    public abstract SortedSet<Patch> getAmenityPatchSet();

    public abstract SortedSet<Patch> getAgentPatchSet();

}