package com.socialsim.model.core.environment.generic;

import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.MatrixPosition;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Patch extends BaseObject implements Comparable<Patch> {

    public static final double PATCH_SIZE_IN_SQUARE_METERS = 1.0;
    private final MatrixPosition matrixPosition;
    private final Coordinates patchCenterCoordinates;
    private CopyOnWriteArrayList<Agent> agent;
    private Amenity.AmenityBlock amenityBlock; // Denotes the amenity block present on this patch
    private Pair<PatchField, Integer> patchField;
    private Pair<QueueingPatchField, Integer> queueingPatchField;
    private final Environment environment;
    private final List<MatrixPosition> neighborIndices;
    private final List<MatrixPosition> neighbor7x7Indices; // Denotes the positions of the neighbors of this patch within a 7x7 range
    private final List<MatrixPosition> neighbor3x3Indices; // Denotes the positions of the neighbors of this patch within a 7x7 range
    private int amenityBlocksAround; // Denotes the number of amenity blocks around this patch
    private int wallsAround; // Denotes the number of amenity blocks around this patch
    private int team;

    public Patch(Environment environment, MatrixPosition matrixPosition) {
        super();

        this.matrixPosition = matrixPosition;
        this.patchCenterCoordinates = Coordinates.getPatchCenterCoordinates(this);
        this.agent = new CopyOnWriteArrayList<>();
        this.amenityBlock = null;
        this.patchField = null;
        this.queueingPatchField = null;
        this.environment = environment;
        this.neighborIndices = this.computeNeighboringPatches();
        this.neighbor7x7Indices = this.compute7x7Neighbors();
        this.neighbor3x3Indices = this.compute7x7Neighbors();
        this.amenityBlocksAround = 0;
        this.wallsAround = 0;
        this.team = -1;
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public CopyOnWriteArrayList<Agent> getAgents() {
        return agent;
    }

    public void addAgent(Agent newAgent) {
        getAgents().add(newAgent);
    }

    public void removeAgent(Agent newAgent) {
        getAgents().remove(newAgent);
    }

    public Amenity.AmenityBlock getAmenityBlock() {
        return amenityBlock;
    }

    public void setAmenityBlock(Amenity.AmenityBlock amenityBlock) {
        this.amenityBlock = amenityBlock;
    }

    public Pair<PatchField, Integer> getPatchField() {
        return patchField;
    }

    public void setPatchField(Pair<PatchField, Integer> patchField) {
        this.patchField = patchField;
    }

    public Pair<QueueingPatchField, Integer> getQueueingPatchField() {
        return queueingPatchField;
    }

    public void setQueueingPatchField(Pair<QueueingPatchField, Integer> queueingPatchField) {
        this.queueingPatchField = queueingPatchField;
    }

    public int getAmenityBlocksAround() {
        return amenityBlocksAround;
    }

    public int getWallsAround() {
        return wallsAround;
    }

    public void setWallsAround(int wallsAround) {
        this.wallsAround = wallsAround;
    }

    public Environment getEnvironment() {
        return environment;
    }

    private List<MatrixPosition> computeNeighboringPatches() {
        int patchRow = this.matrixPosition.getRow();
        int patchColumn = this.matrixPosition.getColumn();

        List<MatrixPosition> neighboringPatchIndices = new ArrayList<>();

        if (patchRow - 1 >= 0 && patchColumn - 1 >= 0) { // Top-left of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn - 1));
        }

        if (patchRow - 1 >= 0) { // Top of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn));
        }

        if (patchRow - 1 >= 0 && patchColumn + 1 < this.getEnvironment().getColumns()) { // Top-right of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn + 1));
        }

        if (patchColumn - 1 >= 0) { // Left of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow, patchColumn - 1));
        }

        if (patchColumn + 1 < this.getEnvironment().getColumns()) { // Right of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow, patchColumn + 1));
        }

        if (patchRow + 1 < this.getEnvironment().getRows() && patchColumn - 1 >= 0) { // Bottom-left of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn - 1));
        }

        if (patchRow + 1 < this.getEnvironment().getRows()) { // Bottom of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn));
        }

        if (patchRow + 1 < this.getEnvironment().getRows() && patchColumn + 1 < this.getEnvironment().getColumns()) { // Bottom-right of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn + 1));
        }

        return neighboringPatchIndices;
    }

    private List<MatrixPosition> compute7x7Neighbors() { // Field of vision
        int patchRow = this.matrixPosition.getRow();
        int patchColumn = this.matrixPosition.getColumn();

        int truncatedX = (int) (this.getPatchCenterCoordinates().getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (this.getPatchCenterCoordinates().getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);

        List<MatrixPosition> patchIndicesToExplore = new ArrayList<>();

        for (int rowOffset = -3; rowOffset <= 3; rowOffset++) {
            for (int columnOffset = -3; columnOffset <= 3; columnOffset++) {
                boolean xCondition;
                boolean yCondition;

                if (rowOffset < 0) { // Separate upper and lower rows
                    yCondition = truncatedY + rowOffset > 0;
                }
                else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < environment.getRows();
                }
                else {
                    yCondition = true;
                }

                if (columnOffset < 0) { // Separate left and right columns
                    xCondition = truncatedX + columnOffset > 0;
                }
                else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < environment.getColumns();
                }
                else {
                    xCondition = true;
                }

                if (xCondition && yCondition) { // Insert the patch to the list of patches to be explored if the patches are within the bounds of the floor
                    patchIndicesToExplore.add(new MatrixPosition(patchRow + rowOffset, patchColumn + columnOffset));
                }
            }
        }

        return patchIndicesToExplore;
    }

    private List<MatrixPosition> compute3x3Neighbors() { // Field of vision
        int patchRow = this.matrixPosition.getRow();
        int patchColumn = this.matrixPosition.getColumn();

        int truncatedX = (int) (this.getPatchCenterCoordinates().getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (this.getPatchCenterCoordinates().getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);

        List<MatrixPosition> patchIndicesToExplore = new ArrayList<>();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                boolean xCondition;
                boolean yCondition;

                if (rowOffset < 0) { // Separate upper and lower rows
                    yCondition = truncatedY + rowOffset > 0;
                }
                else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < environment.getRows();
                }
                else {
                    yCondition = true;
                }

                if (columnOffset < 0) { // Separate left and right columns
                    xCondition = truncatedX + columnOffset > 0;
                }
                else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < environment.getColumns();
                }
                else {
                    xCondition = true;
                }

                if (xCondition && yCondition) { // Insert the patch to the list of patches to be explored if the patches are within the bounds of the floor
                    patchIndicesToExplore.add(new MatrixPosition(patchRow + rowOffset, patchColumn + columnOffset));
                }
            }
        }

        return patchIndicesToExplore;
    }

    public List<Patch> getNeighbors() {
        List<Patch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighborIndices) {
            Patch patch = this.getEnvironment().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn());

            if (patch != null) {
                neighboringPatches.add(patch);
            }
        }

        return neighboringPatches;
    }

    public List<Patch> get7x7Neighbors(boolean includeCenterPatch) {
        List<Patch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighbor7x7Indices) {
            Patch patch = this.getEnvironment().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn());

            if (patch != null) {
                if (!includeCenterPatch || !patch.equals(this)) {
                    neighboringPatches.add(patch);
                }
            }
        }

        return neighboringPatches;
    }

    public List<Patch> get3x3Neighbors(boolean includeCenterPatch) {
        List<Patch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighbor3x3Indices) {
            Patch patch = this.getEnvironment().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn());

            if (patch != null) {
                if (!includeCenterPatch || !patch.equals(this)) {
                    neighboringPatches.add(patch);
                }
            }
        }

        return neighboringPatches;
    }

    public void signalAddAmenityBlock() { // Signal to this patch and to its neighbors that an amenity block was added here
        this.incrementAmenityBlocksAround();

        for (Patch neighbor : this.getNeighbors()) {
            neighbor.incrementAmenityBlocksAround();
        }
    }

    public void signalAddWall() { // Signal to this patch and to its neighbors that an amenity block was added here
        this.incrementWallsAround();

        for (Patch neighbor : this.getNeighbors()) {
            neighbor.incrementWallsAround();
        }
    }

    public void signalRemoveAmenityBlock() { // Signal to this patch and to its neighbors that an amenity block was removed from here
        this.decrementAmenityBlocksAround();

        for (Patch neighbor : this.getNeighbors()) {
            neighbor.decrementAmenityBlocksAround();
        }
    }

    public boolean isNextToAmenityBlock() {
        return this.amenityBlocksAround > 0;
    }

    private void incrementAmenityBlocksAround() {
        this.amenityBlocksAround++;
    }

    private void incrementWallsAround() {
        this.wallsAround++;
    }

    private void decrementAmenityBlocksAround() {
        this.amenityBlocksAround--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patch patch = (Patch) o;

        return matrixPosition.equals(patch.matrixPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrixPosition);
    }

    @Override
    public int compareTo(Patch patch) {
        int thisRow = this.getMatrixPosition().getRow();
        int patchRow = patch.getMatrixPosition().getRow();

        int thisColumn = this.getMatrixPosition().getColumn();
        int patchColumn = patch.getMatrixPosition().getColumn();

        if (thisRow > patchRow) {
            return 1;
        }
        else if (thisRow == patchRow) {
            return Integer.compare(thisColumn, patchColumn);
        }
        else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "[" + this.getMatrixPosition().getRow() + ", " + this.getMatrixPosition().getColumn() + "]";
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
            PatchPair patchPair = (PatchPair) o;
            return patch1.equals(patchPair.patch1) && patch2.equals(patchPair.patch2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(patch1, patch2);
        }

        @Override
        public String toString() {
            return "(" + patch1 + ", " + patch2 + ")";
        }
    }

    public static class Offset { // Denotes the offset of a specific offset of an object in terms of its matrix position
        private final MatrixPosition offset;

        public Offset(int rowOffset, int columnOffset) {
            this.offset = new MatrixPosition(rowOffset, columnOffset);
        }

        public int getRowOffset() {
            return this.offset.getRow();
        }

        public int getColumnOffset() {
            return this.offset.getColumn();
        }

        public static Offset getOffsetFromPatch(Patch patch, Patch reference) {
            int rowOffset = patch.getMatrixPosition().getRow() - reference.getMatrixPosition().getRow();
            int columnOffset = patch.getMatrixPosition().getColumn() - reference.getMatrixPosition().getColumn();

            return new Offset(rowOffset, columnOffset);
        }

        public static Patch getPatchFromOffset(Environment environment, Patch reference, Offset offset) {
            int newRow = reference.getMatrixPosition().getRow() + offset.getRowOffset();
            int newColumn = reference.getMatrixPosition().getColumn() + offset.getColumnOffset();

            if (newRow >= 0 && newRow < environment.getRows() && newColumn >= 0 && newColumn < environment.getColumns()) {
                Patch patch = environment.getPatch(newRow, newColumn);

                if (patch.getAmenityBlock() == null) {
                    return patch;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}