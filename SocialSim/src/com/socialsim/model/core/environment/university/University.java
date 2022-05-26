package com.socialsim.model.core.environment.university;

import com.socialsim.model.core.agent.university.UniversityAction;
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
    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultInteractionTypeChances;

    private final CopyOnWriteArrayList<UniversityAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;

    private int[][] CLASSROOM_SIZES_STUDENT = new int[][]{{40 ,48, 40, 40, 40, 40},{40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}};
    private int[][] CLASSROOM_SIZES_PROF = new int[][]{{1, 1, 1, 1, 1, 1},{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};

    private UniversityAgent[][] PROFS_PER_SCHEDULE = new UniversityAgent[6][6];

    private int nonverbalMean;
    private int nonverbalStdDev;
    private int cooperativeMean;
    private int cooperativeStdDev;
    private int exchangeMean;
    private int exchangeStdDev;
    private int fieldOfView;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> IOSScales;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> IOSInteractionChances;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> interactionTypeChances;
    private int MAX_STUDENTS;
    private int MAX_PROFESSORS;
    private int MAX_CURRENT_STUDENTS;
    private int MAX_CURRENT_PROFESSORS;

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
    private final List<OfficeTable> officeTables;
    private final List<Cabinet> cabinets;

    private final List<Bathroom> bathrooms;
    private final List<Cafeteria> cafeterias;
    private final List<Classroom> classrooms;
    private final List<FountainField> fountainFields;
    private final List<Laboratory> laboratories;
    private final List<SecurityField> securityFields;
    private final List<StallField> stallFields;
    private final List<StudyArea> studyAreas;
    private final List<StaffOffice> staffOffices;
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
        this.officeTables = Collections.synchronizedList(new ArrayList<>());
        this.cabinets = Collections.synchronizedList(new ArrayList<>());

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.cafeterias = Collections.synchronizedList(new ArrayList<>());
        this.classrooms = Collections.synchronizedList(new ArrayList<>());
        this.fountainFields = Collections.synchronizedList(new ArrayList<>());
        this.laboratories = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
        this.stallFields = Collections.synchronizedList(new ArrayList<>());
        this.studyAreas = Collections.synchronizedList(new ArrayList<>());
        this.staffOffices = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());
    }

    public int[][] getClassroomSizesStudent() {
        return CLASSROOM_SIZES_STUDENT;
    }

    public int[][] getClassroomSizesProf() {
        return CLASSROOM_SIZES_PROF;
    }

    public UniversityAgent[][] getProfsPerSchedule() {
        return PROFS_PER_SCHEDULE;
    }

    public void resetClassroomSizes() {
        CLASSROOM_SIZES_STUDENT = new int[][]{{40 ,48, 40, 40, 40, 40},{40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}};
        CLASSROOM_SIZES_PROF = new int[][]{{1, 1, 1, 1, 1, 1},{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
        PROFS_PER_SCHEDULE = new UniversityAgent[6][6];
    }


    public int numBathroomsFree(){
        List<? extends Amenity> amenityListInFloor = this.getAmenityList(Toilet.class);
        int ctr = 0;
        for (Amenity amenity : amenityListInFloor)
            if (!amenity.getAmenityBlocks().get(0).getIsReserved()) {
                ctr++;
                break;
            }
        return ctr;
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

    public List<OfficeTable> getOfficeTables() {
        return officeTables;
    }

    public List<Cabinet> getCabinets() {
        return cabinets;
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

    public List<StaffOffice> getStaffOffices() {
        return staffOffices;
    }

    public List<Wall> getWalls() {
        return walls;
    }


    public int getNonverbalMean() {
        return nonverbalMean;
    }

    public void setNonverbalMean(int nonverbalMean) {
        this.nonverbalMean = nonverbalMean;
    }

    public int getNonverbalStdDev() {
        return nonverbalStdDev;
    }

    public void setNonverbalStdDev(int nonverbalStdDev) {
        this.nonverbalStdDev = nonverbalStdDev;
    }

    public int getCooperativeMean() {
        return cooperativeMean;
    }

    public void setCooperativeMean(int cooperativeMean) {
        this.cooperativeMean = cooperativeMean;
    }

    public int getCooperativeStdDev() {
        return cooperativeStdDev;
    }

    public void setCooperativeStdDev(int cooperativeStdDev) {
        this.cooperativeStdDev = cooperativeStdDev;
    }

    public int getExchangeMean() {
        return exchangeMean;
    }

    public void setExchangeMean(int exchangeMean) {
        this.exchangeMean = exchangeMean;
    }

    public int getExchangeStdDev() {
        return exchangeStdDev;
    }

    public void setExchangeStdDev(int exchangeStdDev) {
        this.exchangeStdDev = exchangeStdDev;
    }

    public int getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(int fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public int getMAX_STUDENTS() {
        return MAX_STUDENTS;
    }

    public void setMAX_STUDENTS(int MAX_STUDENTS) {
        this.MAX_STUDENTS = MAX_STUDENTS;
    }

    public int getMAX_PROFESSORS() {
        return MAX_PROFESSORS;
    }

    public void setMAX_PROFESSORS(int MAX_PROFESSORS) {
        this.MAX_PROFESSORS = MAX_PROFESSORS;
    }

    public int getMAX_CURRENT_STUDENTS() {
        return MAX_CURRENT_STUDENTS;
    }

    public void setMAX_CURRENT_STUDENTS(int MAX_CURRENT_STUDENTS) {
        this.MAX_CURRENT_STUDENTS = MAX_CURRENT_STUDENTS;
    }

    public int getMAX_CURRENT_PROFESSORS() {
        return MAX_CURRENT_PROFESSORS;
    }

    public void setMAX_CURRENT_PROFESSORS(int MAX_CURRENT_PROFESSORS) {
        this.MAX_CURRENT_PROFESSORS = MAX_CURRENT_PROFESSORS;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == UniversityGate.class) {
            return this.getUniversityGates();
        }
        else if (amenityClass == OfficeTable.class) {
            return this.getOfficeTables();
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
        else if (amenityClass == Cabinet.class){
            return this.getCabinets();
        }
        else {
            return null;
        }
    }

    public void createInitialAgentDemographics(int MAX_STUDENTS, int MAX_PROFESSORS){
        UniversityAgent guard = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.GUARD, true);
        this.getAgents().add(guard);

        UniversityAgent janitor1 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.JANITOR, true);
        this.getAgents().add(janitor1);

        UniversityAgent janitor2 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.JANITOR, true);
        this.getAgents().add(janitor2);

        UniversityAgent staff1 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STAFF, true);
        this.getAgents().add(staff1);

        UniversityAgent staff2 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STAFF, true);
        this.getAgents().add(staff2);

        UniversityAgent staff3 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STAFF, true);
        this.getAgents().add(staff3);

        int ctr = 0;

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
    }

    public double convertToChanceInteraction(int x){
        double CHANCE = ((double) x - 1) / 7 + Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * 1/7;
        return CHANCE;
    }

    public void convertIOSToChances(){
        IOSInteractionChances = new CopyOnWriteArrayList<>();
//        IOSScales.toString();
        for(int i = 0; i < agents.size(); i++){
            IOSInteractionChances.add(new CopyOnWriteArrayList<>());
            for(int j = 0; j < agents.size(); j++){
                if (i == j){
                    IOSInteractionChances.get(i).add((double) 0);
                }
                else{
                    UniversityAgent agent1 = agents.get(i), agent2 = agents.get(j);
                    int IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                    IOSInteractionChances.get(i).add(this.convertToChanceInteraction(IOS));
                }
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
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
                            case EXT_Y2_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y4_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case EXT_Y1_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y2_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_Y3_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_Y4_ORG_STUDENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
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
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                        }
                    }
                    case STAFF -> {
                        switch (persona2){
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
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
                            case STRICT_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3)));
                            case APPROACHABLE_PROFESSOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                            case STAFF -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5, 6)));
                        }
                    }
                }
            }
            defaultIOS.add(personaIOS);
        }
    }

    public static void configureDefaultInteractionTypeChances(){
        defaultInteractionTypeChances = new CopyOnWriteArrayList<>();
        for (int i = 0; i < PersonaActionGroup.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> interactionChances = new CopyOnWriteArrayList<>();
            for (int j = 0 ; j < UniversityAction.Name.values().length; j++){
                PersonaActionGroup personaGroup = PersonaActionGroup.values()[i];
                UniversityAction.Name action = UniversityAction.Name.values()[j];
                switch (personaGroup){
                    case GUARD -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case JANITOR -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case INT_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                        }
                    }
                    case INT_ORG_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                        }
                    }
                    case EXT_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                        }
                    }
                    case EXT_ORG_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                        }
                    }
                    case STRICT_PROFESSOR -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                        }
                    }
                    case APPROACHABLE_PROFESSOR -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 0, 30)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                        }
                    }
                    case STAFF -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRINK_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_PROFESSOR_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BLACKBOARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_PROFESSOR_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_STUDENT_QUESTION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_STUDY_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case CHECK_TABLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CLASS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                }
            }
            defaultInteractionTypeChances.add(interactionChances);
        }
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> getIOSScales(){
        return this.IOSScales;
    }

    public void setIOSScales(CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> IOSScales){
        this.IOSScales = IOSScales;
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> getInteractionTypeChances(){
        return this.interactionTypeChances;
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

    public void copyDefaultToInteractionTypeChances(){
        this.interactionTypeChances = new CopyOnWriteArrayList<>();
        for(int i = 0; i < defaultInteractionTypeChances.size(); i++){
            this.interactionTypeChances.add(new CopyOnWriteArrayList<>());
            for(int j = 0; j < defaultInteractionTypeChances.get(i).size(); j++){
                this.interactionTypeChances.get(i).add(new CopyOnWriteArrayList<>());
                for (int k = 0; k < defaultInteractionTypeChances.get(i).get(j).size(); k++){
                    this.interactionTypeChances.get(i).get(j).add(defaultInteractionTypeChances.get(i).get(j).get(k));
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