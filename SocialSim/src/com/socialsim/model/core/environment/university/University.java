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
import com.socialsim.model.simulator.Simulator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.socialsim.model.core.agent.university.UniversityAgent.*;

public class University extends Environment {


    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultIOS;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> IOSScales;
    private final CopyOnWriteArrayList<UniversityAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> IOSInteractionChances;

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
    private final List<FountainField> fountainFields;
    private final List<Laboratory> laboratories;
    private final List<SecurityField> securityFields;
    private final List<StallField> stallFields;
    private final List<StudyArea> studyAreas;
    private final List<Wall> walls;

    public static final University.UniversityFactory universityFactory;

    static {
        universityFactory = new UniversityFactory();
    }

    public University(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();
        this.IOSInteractionChances = new CopyOnWriteArrayList<>();

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
        this.fountainFields = Collections.synchronizedList(new ArrayList<>());
        this.laboratories = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
        this.stallFields = Collections.synchronizedList(new ArrayList<>());
        this.studyAreas = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<UniversityAgent> getAgents() {
        return agents;
    }

    public CopyOnWriteArrayList<UniversityAgent> getMovableAgents() {
        CopyOnWriteArrayList<UniversityAgent> movable = new CopyOnWriteArrayList<>();
        for (UniversityAgent agent: getAgents()){
            if (agent.getAgentMovement() != null)
                movable.add(agent);
        }
        return movable;
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> getIOS() {
        return IOSInteractionChances;
    }

    public CopyOnWriteArrayList<UniversityAgent> getUnspawnedAgents() {
        CopyOnWriteArrayList<UniversityAgent> unspawned = new CopyOnWriteArrayList<>();
        for (UniversityAgent agent: getAgents()){
            if (agent.getAgentMovement() == null)
                unspawned.add(agent);
        }
        return unspawned;
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

    public List<FountainField> getFountainFields() {
        return fountainFields;
    }

    public List<Laboratory> getLaboratories() {
        return laboratories;
    }

    public List<SecurityField> getSecurityFields() {
        return securityFields;
    }

    public List<StallField> getStallFields() {
        return stallFields;
    }

    public List<StudyArea> getStudyAreas() {
        return studyAreas;
    }

    public List<Wall> getWalls() {
        return walls;
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

    public void createInitialAgentDemographics(int MAX_STUDENTS, int MAX_PROFESSORS){
        //Guard
        UniversityAgent guard = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.GUARD, true);
        this.getAgents().add(guard);

        //Janitor
        UniversityAgent janitor1 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.JANITOR, true);
        this.getAgents().add(janitor1);

        UniversityAgent janitor2 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.JANITOR, true);
        this.getAgents().add(janitor2);

        int ctr = 0;
        //Students and Professors
        while (ctr < MAX_STUDENTS){
            UniversityAgent newAgent = UniversityAgent.UniversityAgentFactory.create(Type.STUDENT, true);
            ctr++;
            this.getAgents().add(newAgent);
        }
        ctr = 0;
        while (ctr < MAX_PROFESSORS){
            UniversityAgent newAgent = UniversityAgent.UniversityAgentFactory.create(Type.PROFESSOR, true);
            ctr++;
            this.getAgents().add(newAgent);
        }

//        for (int i = 0; i < this.getAgents().size(); i++){
//            Persona agent1 = agents.get(i).getPersona();
//            ArrayList<Integer> IOSScales = new ArrayList<>();
//            for (int j = 0 ; j < this.getAgents().size(); j++){
//                if (i == j){
//                    IOSScales.add(0);
//                }
//                else {
//                    Persona persona2 = agents.get(j).getPersona();
//                    if (agent1 == Persona.GUARD){
//                        //1. Get IOS Scale of each agent then put in an array
//                        //2. Place in convert function and replace IOS
//                        switch (persona2){
//                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.JANITOR){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y1_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case INT_Y1_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y2_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y3_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y4_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y1_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y2_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y2_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 3, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y3_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y3_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 3, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.INT_Y4_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y4_ORG_STUDENT -> {
//                                int[] listIOS = new int[]{1, 2, 3, 5, 6};
//                                IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
//                            }
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y1_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y2_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y3_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y4_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y1_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y2_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y3_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                    else if (agent1 == Persona.EXT_Y4_ORG_STUDENT){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                    else if (agent1 == Persona.STRICT_PROFESSOR){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
//                        }
//                    }
//                    else if (agent1 == Persona.APPROACHABLE_PROFESSOR){
//                        switch (persona2){
//                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
//                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
//                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
//                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
//                        }
//                    }
//                }
//            }
//            IOSInteractionChances.add(convertToChanceInteraction(IOSScales));
//        }
    }

    public double convertToChanceInteraction(int x){// Convert IOS to chance based only on threshold, not 0 to said scale
        double CHANCE = ((double) x - 1) / 7 + Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * 1/7;
        return CHANCE;
    }

    public void convertIOSToChances(){
        IOSInteractionChances = new CopyOnWriteArrayList<>();
        IOSScales.toString();
        for(int i = 0; i < IOSScales.size(); i++){
            IOSInteractionChances.add(new CopyOnWriteArrayList<>());
            for(int j = 0; j < IOSScales.get(i).size(); j++){
               int IOS = IOSScales.get(i).get(j).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(i).get(j).size()));
                IOSInteractionChances.get(i).add(this.convertToChanceInteraction(IOS));
            }
        }
    }

    public static void configureDefaultIOS(){
        defaultIOS = new CopyOnWriteArrayList<>();
        for (int i = 0; i < Persona.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> personaIOS = new CopyOnWriteArrayList<>();
            for (int j = 0 ; j < Persona.values().length; j++){
                Persona persona1 = Persona.values()[i];
                Persona persona2 = Persona.values()[j];
                switch (persona1){
                    case GUARD -> {
                        switch(persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                        }
                    }
                    case JANITOR -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                        }
                    }
                    case INT_Y1_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case INT_Y2_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case INT_Y3_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                        }
                    }
                    case INT_Y4_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 5, 6)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                        }
                    }
                    case INT_Y1_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                        }
                    }
                    case INT_Y2_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 5, 6)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                        }
                    }
                    case INT_Y3_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 5, 6)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                        }
                    }
                    case INT_Y4_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 5, 6)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                        }
                    }
                    case EXT_Y1_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                        }
                    }
                    case EXT_Y2_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                        }
                    }
                    case EXT_Y3_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case EXT_Y4_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case EXT_Y1_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case EXT_Y2_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case EXT_Y3_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case EXT_Y4_ORG_STUDENT -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case STRICT_PROFESSOR -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                        }
                    }
                    case APPROACHABLE_PROFESSOR -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case INT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y1_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                }
            }
            defaultIOS.add(personaIOS);
        }
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> getIOSScales(){
        return this.IOSScales;
    }

    public void copyDefaultToIOS(){
        this.IOSScales = new CopyOnWriteArrayList<>();
        for(int i = 0; i < defaultIOS.size(); i++){
            this.IOSScales.add(new CopyOnWriteArrayList<>());
            for(int j = 0; j < defaultIOS.get(i).size(); j++){
                this.IOSScales.get(i).add(new CopyOnWriteArrayList<>());
                for (int k = 0; k < defaultIOS.get(i).get(j).size(); k++){
                    this.IOSScales.get(i).get(j).add(defaultIOS.get(i).get(j).get(k));
                }
            }
        }
    }

    public static class UniversityFactory extends BaseObject.ObjectFactory {
        public static University create(int rows, int columns) {
            return new University(rows, columns);
        }
    }

}