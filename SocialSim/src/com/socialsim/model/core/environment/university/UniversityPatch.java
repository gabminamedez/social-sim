package com.socialsim.model.core.environment.university;

import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.patch.BaseObject;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.Queueable;
import com.socialsim.model.core.environment.patch.position.Coordinates;
import com.socialsim.model.core.environment.patch.position.MatrixPosition;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.patch.patchfield.headful.QueueingPatchField;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class UniversityPatch extends BaseObject implements Environment, Comparable<UniversityPatch> {

    public static final double PATCH_SIZE_IN_SQUARE_METERS = 0.6;
    private final MatrixPosition matrixPosition;
    private final Coordinates patchCenterCoordinates;
    private final CopyOnWriteArrayList<Agent> agents;
    private Amenity.AmenityBlock amenityBlock; // Denotes the amenity block present on this patch
    private final University university;
    private final List<MatrixPosition> neighborIndices;
    private final List<MatrixPosition> neighbor7x7Indices; // Denotes the positions of the neighbors of this patch within a 7x7 range
    private int amenityBlocksAround; // Denotes the number of amenity blocks around this patch
    private final Map<Queueable, Map<QueueingPatchField.PatchFieldState, Double>> floorFieldValues; // Denotes the individual floor field value of this patch, given the queueable goal patch and the desired state

    public UniversityPatch(University university, MatrixPosition matrixPosition) {
        super();

        this.matrixPosition = matrixPosition;
        this.patchCenterCoordinates = Coordinates.getPatchCenterCoordinates(this);
        this.agents = new CopyOnWriteArrayList<>();
        this.amenityBlock = null;
        this.university = university;
        this.neighborIndices = this.computeNeighboringPatches();
        this.neighbor7x7Indices = this.compute7x7Neighbors();
        this.amenityBlocksAround = 0;
        this.floorFieldValues = new HashMap<>();
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public CopyOnWriteArrayList<Agent> getAgents() {
        return agents;
    }

    public Map<Queueable, Map<QueueingPatchField.PatchFieldState, Double>> getFloorFieldValues() {
        return floorFieldValues;
    }

    public Amenity.AmenityBlock getAmenityBlock() {
        return amenityBlock;
    }

    public void setAmenityBlock(Amenity.AmenityBlock amenityBlock) {
        this.amenityBlock = amenityBlock;
    }

    public int getAmenityBlocksAround() {
        return amenityBlocksAround;
    }

    public University getUniversity() {
        return university;
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

        if (patchRow - 1 >= 0 && patchColumn + 1 < this.getUniversity().getColumns()) { // Top-right of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn + 1));
        }

        if (patchColumn - 1 >= 0) { // Left of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow, patchColumn - 1));
        }

        if (patchColumn + 1 < this.getUniversity().getColumns()) { // Right of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow, patchColumn + 1));
        }

        if (patchRow + 1 < this.getUniversity().getRows() && patchColumn - 1 >= 0) { // Bottom-left of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn - 1));
        }

        if (patchRow + 1 < this.getUniversity().getRows()) { // Bottom of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn));
        }

        if (patchRow + 1 < this.getUniversity().getRows() && patchColumn + 1 < this.getUniversity().getColumns()) { // Bottom-right of patch
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn + 1));
        }

        return neighboringPatchIndices;
    }

    private List<MatrixPosition> compute7x7Neighbors() {
        int patchRow = this.matrixPosition.getRow();
        int patchColumn = this.matrixPosition.getColumn();

        int truncatedX = (int) (this.getPatchCenterCoordinates().getX() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (this.getPatchCenterCoordinates().getY() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS);

        List<MatrixPosition> patchIndicesToExplore = new ArrayList<>();

        for (int rowOffset = -3; rowOffset <= 3; rowOffset++) {
            for (int columnOffset = -3; columnOffset <= 3; columnOffset++) {
                boolean xCondition;
                boolean yCondition;

                if (rowOffset < 0) { // Separate upper and lower rows
                    yCondition = truncatedY + rowOffset > 0;
                }
                else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < university.getRows();
                }
                else {
                    yCondition = true;
                }

                if (columnOffset < 0) { // Separate left and right columns
                    xCondition = truncatedX + columnOffset > 0;
                }
                else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < university.getColumns();
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

    public List<UniversityPatch> getNeighbors() {
        List<UniversityPatch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighborIndices) {
            UniversityPatch patch = this.getUniversity().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn());

            if (patch != null) {
                neighboringPatches.add(patch);
            }
        }

        return neighboringPatches;
    }

    public List<UniversityPatch> get7x7Neighbors(boolean includeCenterPatch) {
        List<UniversityPatch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighbor7x7Indices) {
            UniversityPatch patch = this.getUniversity().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn());

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

        for (UniversityPatch neighbor : this.getNeighbors()) {
            neighbor.incrementAmenityBlocksAround();
        }
    }

    public void signalRemoveAmenityBlock() { // Signal to this patch and to its neighbors that an amenity block was removed from here
        this.decrementAmenityBlocksAround();

        for (UniversityPatch neighbor : this.getNeighbors()) {
            neighbor.decrementAmenityBlocksAround();
        }
    }

    public boolean isNextToAmenityBlock() {
        return this.amenityBlocksAround > 0;
    }

    private void incrementAmenityBlocksAround() {
        this.amenityBlocksAround++;
    }

    private void decrementAmenityBlocksAround() {
        this.amenityBlocksAround--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniversityPatch patch = (UniversityPatch) o;

        return matrixPosition.equals(patch.matrixPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrixPosition);
    }

    @Override
    public int compareTo(UniversityPatch patch) {
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

    public static class UniversityPatchPair implements Environment {
        private final UniversityPatch patch1;
        private final UniversityPatch patch2;

        public UniversityPatchPair(UniversityPatch patch1, UniversityPatch patch2) {
            this.patch1 = patch1;
            this.patch2 = patch2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UniversityPatchPair patchPair = (UniversityPatchPair) o;
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

        public static Offset getOffsetFromPatch(UniversityPatch patch, UniversityPatch reference) {
            int rowOffset = patch.getMatrixPosition().getRow() - reference.getMatrixPosition().getRow();
            int columnOffset = patch.getMatrixPosition().getColumn() - reference.getMatrixPosition().getColumn();

            return new Offset(rowOffset, columnOffset);
        }

        public static UniversityPatch getPatchFromOffset(University university, UniversityPatch reference, Offset offset) {
            int newRow = reference.getMatrixPosition().getRow() + offset.getRowOffset();
            int newColumn = reference.getMatrixPosition().getColumn() + offset.getColumnOffset();

            if (newRow >= 0 && newRow < university.getRows() && newColumn >= 0 && newColumn < university.getColumns()) {
                UniversityPatch patch = university.getPatch(newRow, newColumn);

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

}