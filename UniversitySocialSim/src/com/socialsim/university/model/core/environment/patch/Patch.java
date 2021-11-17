package com.socialsim.university.model.core.environment.patch;

import com.socialsim.university.model.core.agent.Agent;
import com.socialsim.university.model.core.environment.BaseUniversityObject;
import com.socialsim.university.model.core.environment.Environment;
import com.socialsim.university.model.core.environment.University;
import com.socialsim.university.model.core.environment.patch.patchfield.headful.QueueingPatchField;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.Queueable;
import com.socialsim.university.model.core.environment.patch.position.Coordinates;
import com.socialsim.university.model.core.environment.patch.position.MatrixPosition;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Patch extends BaseUniversityObject implements Environment, Comparable<Patch> {

    public static final double PATCH_SIZE_IN_SQUARE_METERS = 0.6;
    private final MatrixPosition matrixPosition;
    private final Coordinates patchCenterCoordinates;

    private final CopyOnWriteArrayList<Agent> agents;

    private Amenity.AmenityBlock amenityBlock; // Denotes the amenity block present on this patch
    private final University university;
    private final List<MatrixPosition> neighborIndices;
    private final Map<Queueable, Map<QueueingPatchField.PatchFieldState, Double>> floorFieldValues; // Denotes the individual floor field value of this patch, given the queueable goal patch and the desired state

    public Patch(University university, MatrixPosition matrixPosition) {
        super();

        this.matrixPosition = matrixPosition;
        this.patchCenterCoordinates = Coordinates.getPatchCenterCoordinates(this);

        this.agents = new CopyOnWriteArrayList<>();

        this.amenityBlock = null;
        this.university = university;
        this.neighborIndices = this.computeNeighboringPatches();
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

    public List<Patch> getNeighbors() {
        List<Patch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighborIndices) {
            neighboringPatches.add(this.getUniversity().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn()));
        }

        return neighboringPatches;
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

}