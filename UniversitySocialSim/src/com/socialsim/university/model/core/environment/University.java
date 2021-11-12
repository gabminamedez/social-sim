package com.socialsim.university.model.core.environment;

import com.socialsim.university.controller.Main;
import com.socialsim.university.model.core.agent.guard.Guard;
import com.socialsim.university.model.core.agent.janitor.Janitor;
import com.socialsim.university.model.core.agent.officer.Officer;
import com.socialsim.university.model.core.agent.professor.Professor;
import com.socialsim.university.model.core.agent.student.Student;
import com.socialsim.university.model.core.environment.patch.Patch;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.university.model.core.environment.patch.position.Coordinates;
import com.socialsim.university.model.core.environment.patch.position.MatrixPosition;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class University extends BaseUniversityObject implements Environment {

    private final int rows;
    private final int columns;
    private final Patch[][] patches;
    private final CopyOnWriteArrayList<Guard> guards;
    private final CopyOnWriteArrayList<Janitor> janitors;
    private final CopyOnWriteArrayList<Officer> officers;
    private final CopyOnWriteArrayList<Professor> professors;
    private final CopyOnWriteArrayList<Student> students;

    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> guardPatchSet;
    private final SortedSet<Patch> janitorPatchSet;
    private final SortedSet<Patch> officerPatchSet;
    private final SortedSet<Patch> professorPatchSet;
    private final SortedSet<Patch> studentPatchSet;

     private final List<StationGate> stationGates;
     private final List<Security> securities;
     private final List<TicketBooth> ticketBooths;
     private final List<Turnstile> turnstiles;
     private final List<TrainDoor> trainDoors;
     private final List<Track> tracks;
     private final List<Wall> walls;

    private static final University.UniversityFactory universityFactory;

    static {
        universityFactory = new UniversityFactory();
    }

    public University(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.patches = new Patch[rows][columns];
        this.initializePatches();

        this.guards = new CopyOnWriteArrayList<>();
        this.janitors = new CopyOnWriteArrayList<>();
        this.officers = new CopyOnWriteArrayList<>();
        this.professors = new CopyOnWriteArrayList<>();
        this.students = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.guardPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.janitorPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.officerPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.professorPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.studentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.stationGates = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.ticketBooths = Collections.synchronizedList(new ArrayList<>());
        this.turnstiles = Collections.synchronizedList(new ArrayList<>());
        this.trainDoors = Collections.synchronizedList(new ArrayList<>());
        this.tracks = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());
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
        return getPatch(
                (int) (coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS),
                (int) (coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS)
        );
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

    public CopyOnWriteArrayList<Guard> getGuards() {
        return guards;
    }

    public CopyOnWriteArrayList<Janitor> getJanitors() {
        return janitors;
    }

    public CopyOnWriteArrayList<Officer> getOfficers() {
        return officers;
    }

    public CopyOnWriteArrayList<Professor> getProfessors() {
        return professors;
    }

    public CopyOnWriteArrayList<Student> getStudents() {
        return students;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == StationGate.class) {
            return this.getStationGates();
        }
        else if (amenityClass == Security.class) {
            return this.getSecurities();
        }
        else if (amenityClass == TicketBooth.class) {
            return this.getTicketBooths();
        }
        else {
            return null;
        }
    }

    public static List<Patch> get7x7Field(Patch centerPatch, double heading, boolean includeCenterPatch, double fieldOfViewAngle) {
        int truncatedX = (int) (centerPatch.getPatchCenterCoordinates().getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (centerPatch.getPatchCenterCoordinates().getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);

        Patch chosenPatch;
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
                    yCondition = truncatedY + rowOffset < Main.simulator.getCurrentFloor().getRows();
                }
                else {
                    yCondition = true;
                }

                if (columnOffset < 0) {
                    xCondition = truncatedX + columnOffset > 0;
                }
                else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < Main.simulator.getCurrentFloor().getColumns();
                }
                else {
                    xCondition = true;
                }

                if (xCondition && yCondition) {
                    chosenPatch = Main.simulator.getCurrentFloor().getPatch(
                            truncatedY + rowOffset,
                            truncatedX + columnOffset
                    );

                    if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(
                            centerPatch.getPatchCenterCoordinates(),
                            chosenPatch.getPatchCenterCoordinates(),
                            heading,
                            fieldOfViewAngle)) {
                        patchesToExplore.add(chosenPatch);
                    }
                }
            }
        }

        return patchesToExplore;
    }

    public static class UniversityFactory extends BaseUniversityObject.UniversityObjectFactory {
        public University create(int rows, int columns) {
            return new University(rows, columns);
        }
    }

}