package com.socialsim.model.core.environment.university;

import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.university.patchfield.*;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class University extends Environment {

    private final CopyOnWriteArrayList<UniversityAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;

    private final List<UniversityGate> universityGates;
    private final List<Bench> benches;
    private final List<Board> boards;
    private final List<Bulletin> bulletins;
    private final List<Chair> chairs;
    private final List<Door> doors;
    private final List<EatTable> eatTables;
    private final List<Fountain> fountains;
    private final List<Toilet> toilets;
    private final List<Sink> sinks;
    private final List<LabTable> labTables;
    private final List<ProfTable> profTables;
    private final List<Security> securities;
    private final List<Staircase> staircases;
    private final List<Stall> stalls;
    private final List<StudyTable> studyTables;
    private final List<Trash> trashes;

    private final List<Bathroom> bathrooms;
    private final List<Cafeteria> cafeterias;
    private final List<Classroom> classrooms;
    private final List<Laboratory> laboratories;
    private final List<StudyArea> studyAreas;
    private final List<Wall> walls;

    List<UniversityAgent> agentBacklogs;

    private static final University.UniversityFactory universityFactory;

    static {
        universityFactory = new UniversityFactory();
    }

    public University(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.agentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.universityGates = Collections.synchronizedList(new ArrayList<>());
        this.benches = Collections.synchronizedList(new ArrayList<>());
        this.boards = Collections.synchronizedList(new ArrayList<>());
        this.bulletins = Collections.synchronizedList(new ArrayList<>());
        this.chairs = Collections.synchronizedList(new ArrayList<>());
        this.doors = Collections.synchronizedList(new ArrayList<>());
        this.eatTables = Collections.synchronizedList(new ArrayList<>());
        this.fountains = Collections.synchronizedList(new ArrayList<>());
        this.toilets = Collections.synchronizedList(new ArrayList<>());
        this.sinks = Collections.synchronizedList(new ArrayList<>());
        this.labTables = Collections.synchronizedList(new ArrayList<>());
        this.profTables = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.staircases = Collections.synchronizedList(new ArrayList<>());
        this.stalls = Collections.synchronizedList(new ArrayList<>());
        this.studyTables = Collections.synchronizedList(new ArrayList<>());
        this.trashes = Collections.synchronizedList(new ArrayList<>());

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.cafeterias = Collections.synchronizedList(new ArrayList<>());
        this.classrooms = Collections.synchronizedList(new ArrayList<>());
        this.laboratories = Collections.synchronizedList(new ArrayList<>());
        this.studyAreas = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());

        this.agentBacklogs = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<UniversityAgent> getAgents() {
        return agents;
    }

    @Override
    public SortedSet<Patch> getAmenityPatchSet() {
        return amenityPatchSet;
    }

    @Override
    public SortedSet<Patch> getAgentPatchSet() {
        return agentPatchSet;
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

    public List<EatTable> getEatTables() {
        return eatTables;
    }

    public List<Fountain> getFountains() {
        return fountains;
    }

    public List<Toilet> getToilets() {
        return toilets;
    }

    public List<Sink> getSinks() {
        return sinks;
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

    public List<Stall> getStalls() {
        return stalls;
    }

    public List<StudyTable> getStudyTables() {
        return studyTables;
    }

    public List<Trash> getTrashes() {
        return trashes;
    }

    public List<Bathroom> getBathrooms() {
        return bathrooms;
    }

    public List<Cafeteria> getCafeterias() {
        return cafeterias;
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public List<Laboratory> getLaboratories() {
        return laboratories;
    }

    public List<StudyArea> getStudyAreas() {
        return studyAreas;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<UniversityAgent> getAgentBacklogs() {
        return agentBacklogs;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == UniversityGate.class) {
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
        else if (amenityClass == EatTable.class) {
            return this.getEatTables();
        }
        else if (amenityClass == Fountain.class) {
            return this.getFountains();
        }
        else if (amenityClass == Toilet.class) {
            return this.getToilets();
        }
        else if (amenityClass == Sink.class) {
            return this.getSinks();
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
        else if (amenityClass == Stall.class) {
            return this.getStalls();
        }
        else if (amenityClass == StudyTable.class) {
            return this.getStudyTables();
        }
        else if (amenityClass == Trash.class) {
            return this.getTrashes();
        }
        else {
            return null;
        }
    }

    public static class UniversityFactory extends BaseObject.ObjectFactory {
        public static University create(int rows, int columns) {
            return new University(rows, columns);
        }
    }

}