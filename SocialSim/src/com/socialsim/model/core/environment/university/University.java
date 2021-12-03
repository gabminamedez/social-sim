package com.socialsim.model.core.environment.university;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.patch.BaseObject;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.position.Coordinates;
import com.socialsim.model.core.environment.patch.position.MatrixPosition;
import com.socialsim.model.core.environment.university.patchobject.miscellaneous.Wall;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class University extends BaseObject implements Environment {

    private final int rows;
    private final int columns;
    private final UniversityPatch[][] patches;
    private final CopyOnWriteArrayList<Agent> agents;

    private final SortedSet<UniversityPatch> amenityPatchSet;
    private final SortedSet<UniversityPatch> agentPatchSet;

    private final List<Wall> walls;
    private final List<UniversityGate> universityGates;
    private final List<Bench> benches;
    private final List<Board> boards;
    private final List<Bulletin> bulletins;
    private final List<Chair> chairs;
    private final List<Door> doors;
    private final List<Fountain> fountains;
    private final List<LabTable> labTables;
    private final List<ProfTable> profTables;
    private final List<Security> securities;
    private final List<Staircase> staircases;
    private final List<Trash> trashes;
    List<Agent> agentBacklogs;

    private static final University.UniversityFactory universityFactory;

    static {
        universityFactory = new UniversityFactory();
    }

    public University(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.patches = new UniversityPatch[rows][columns];
        this.initializePatches();

        this.agents = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.agentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.walls = Collections.synchronizedList(new ArrayList<>());
        this.universityGates = Collections.synchronizedList(new ArrayList<>());
        this.benches = Collections.synchronizedList(new ArrayList<>());
        this.boards = Collections.synchronizedList(new ArrayList<>());
        this.bulletins = Collections.synchronizedList(new ArrayList<>());
        this.chairs = Collections.synchronizedList(new ArrayList<>());
        this.doors = Collections.synchronizedList(new ArrayList<>());
        this.fountains = Collections.synchronizedList(new ArrayList<>());
        this.labTables = Collections.synchronizedList(new ArrayList<>());
        this.profTables = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.staircases = Collections.synchronizedList(new ArrayList<>());
        this.trashes = Collections.synchronizedList(new ArrayList<>());
        this.agentBacklogs = Collections.synchronizedList(new ArrayList<>());
    }

    private void initializePatches() {
        MatrixPosition matrixPosition;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrixPosition = new MatrixPosition(row, column);
                patches[row][column] = new UniversityPatch(this, matrixPosition);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public UniversityPatch getPatch(Coordinates coordinates) {
        return getPatch((int) (coordinates.getY() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS), (int) (coordinates.getX() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS));
    }

    public UniversityPatch getPatch(MatrixPosition matrixPosition) {
        return getPatch(matrixPosition.getRow(), matrixPosition.getColumn());
    }

    public UniversityPatch getPatch(int row, int column) {
        return patches[row][column];
    }

    public UniversityPatch[][] getPatches() {
        return this.patches;
    }

    public CopyOnWriteArrayList<Agent> getAgents() {
        return agents;
    }

    public SortedSet<UniversityPatch> getAmenityPatchSet() {
        return amenityPatchSet;
    }

    public SortedSet<UniversityPatch> getAgentPatchSet() {
        return agentPatchSet;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<UniversityGate> getUniversityGates() {
        return universityGates;
    }

    public List<Bench> getBenches() {
        return benches;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public List<Bulletin> getBulletins() {
        return bulletins;
    }

    public List<Chair> getChairs() {
        return chairs;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Fountain> getFountains() {
        return fountains;
    }

    public List<LabTable> getLabTables() {
        return labTables;
    }

    public List<ProfTable> getProfTables() {
        return profTables;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<Staircase> getStaircases() {
        return staircases;
    }

    public List<Trash> getTrashes() {
        return trashes;
    }

    public List<Agent> getAgentBacklogs() {
        return agentBacklogs;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == Wall.class) {
            return this.getWalls();
        }
        else if (amenityClass == UniversityGate.class) {
            return this.getUniversityGates();
        }
        else if (amenityClass == Bench.class) {
            return this.getBenches();
        }
        else if (amenityClass == Board.class) {
            return this.getBoards();
        }
        else if (amenityClass == Bulletin.class) {
            return this.getBulletins();
        }
        else if (amenityClass == Chair.class) {
            return this.getChairs();
        }
        else if (amenityClass == Door.class) {
            return this.getDoors();
        }
        else if (amenityClass == Fountain.class) {
            return this.getFountains();
        }
        else if (amenityClass == LabTable.class) {
            return this.getLabTables();
        }
        else if (amenityClass == ProfTable.class) {
            return this.getProfTables();
        }
        else if (amenityClass == Security.class) {
            return this.getSecurities();
        }
        else if (amenityClass == Staircase.class) {
            return this.getStaircases();
        }
        else if (amenityClass == Trash.class) {
            return this.getTrashes();
        }
        else {
            return null;
        }
    }

    // Get patches in the agent's field of vision
    public static List<UniversityPatch> get7x7Field(UniversityPatch centerPatch, double heading, boolean includeCenterPatch, double fieldOfViewAngle) {
        int truncatedX = (int) (centerPatch.getPatchCenterCoordinates().getX() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (centerPatch.getPatchCenterCoordinates().getY() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS);

        UniversityPatch chosenPatch;
        List<UniversityPatch> patchesToExplore = new ArrayList<>();

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
                    yCondition = truncatedY + rowOffset < Main.simulator.getUniversity().getRows();
                }
                else {
                    yCondition = true;
                }

                if (columnOffset < 0) {
                    xCondition = truncatedX + columnOffset > 0;
                }
                else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < Main.simulator.getUniversity().getColumns();
                }
                else {
                    xCondition = true;
                }

                if (xCondition && yCondition) {
                    chosenPatch = Main.simulator.getUniversity().getPatch(truncatedY + rowOffset, truncatedX + columnOffset);

                    if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(centerPatch.getPatchCenterCoordinates(), chosenPatch.getPatchCenterCoordinates(), heading, fieldOfViewAngle)) {
                        patchesToExplore.add(chosenPatch);
                    }
                }
            }
        }

        return patchesToExplore;
    }

    public static class UniversityFactory extends BaseObject.ObjectFactory {
        public University create(int rows, int columns) {
            return new University(rows, columns);
        }
    }

}