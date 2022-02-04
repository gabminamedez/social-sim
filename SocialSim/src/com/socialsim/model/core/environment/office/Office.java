package com.socialsim.model.core.environment.office;

import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.*;
import com.socialsim.model.simulator.Simulator;
import com.socialsim.model.simulator.office.OfficeSimulator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.socialsim.model.core.agent.office.OfficeAgent.*;
//import static com.socialsim.model.core.agent.office.OfficeAgent.EXT_ORG_CHANCE_SPAWN;

public class Office extends Environment {

    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultIOS;
    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultInteractionTypeChances;

    private final CopyOnWriteArrayList<OfficeAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;

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
    public static int MAX_CLIENTS = 6;
    public static int MAX_DRIVERS = 3;
    public static int MAX_VISITORS = 1;

    private final List<OfficeGate> officeGates;
    private final List<Cabinet> cabinets;
    private final List<Chair> chairs;
    private final List<CollabDesk> collabDesks;
    private final List<Couch> couches;
    private final List<Cubicle> cubicles;
    private final List<Door> doors;
    private final List<MeetingDesk> meetingDesks;
    private final List<OfficeDesk> officeDesks;
    private final List<Plant> plants;
    private final List<Printer> printers;
    private final List<ReceptionTable> receptionTables;
    private final List<Security> securities;
    private final List<Table> tables;
    private final List<Sink> sinks;
    private final List<Toilet> toilets;

    private final List<Bathroom> bathrooms;
    private final List<Breakroom> breakrooms;
    private final List<MeetingRoom> meetingRooms;
    private final List<OfficeRoom> officeRooms;
    private final List<Reception> receptions;
    private final List<Wall> walls;
    private final List<SecurityField> securityFields;

    public static final Office.OfficeFactory officeFactory;

    static {
        officeFactory = new Office.OfficeFactory();
    }

    public Office(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();
        this.IOSInteractionChances = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.agentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.officeGates = Collections.synchronizedList(new ArrayList<>());
        this.cabinets = Collections.synchronizedList(new ArrayList<>());
        this.chairs = Collections.synchronizedList(new ArrayList<>());
        this.collabDesks = Collections.synchronizedList(new ArrayList<>());
        this.couches = Collections.synchronizedList(new ArrayList<>());
        this.cubicles = Collections.synchronizedList(new ArrayList<>());
        this.doors = Collections.synchronizedList(new ArrayList<>());
        this.meetingDesks = Collections.synchronizedList(new ArrayList<>());
        this.officeDesks = Collections.synchronizedList(new ArrayList<>());
        this.plants = Collections.synchronizedList(new ArrayList<>());
        this.printers = Collections.synchronizedList(new ArrayList<>());
        this.receptionTables = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.tables = Collections.synchronizedList(new ArrayList<>());
        this.sinks = Collections.synchronizedList(new ArrayList<>());
        this.toilets = Collections.synchronizedList(new ArrayList<>());

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.breakrooms = Collections.synchronizedList(new ArrayList<>());
        this.meetingRooms = Collections.synchronizedList(new ArrayList<>());
        this.officeRooms = Collections.synchronizedList(new ArrayList<>());
        this.receptions = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<OfficeAgent> getAgents() {
        return agents;
    }

    public CopyOnWriteArrayList<OfficeAgent> getMovableAgents() {
        CopyOnWriteArrayList<OfficeAgent> movable = new CopyOnWriteArrayList<>();
        for (OfficeAgent agent: getAgents()){
            if (agent.getAgentMovement() != null)
                movable.add(agent);
        }
        return movable;
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> getIOS() {
        return this.IOSInteractionChances;
    }

    public CopyOnWriteArrayList<OfficeAgent> getUnspawnedWorkingAgents() {
        CopyOnWriteArrayList<OfficeAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<Type> working = new ArrayList<>(Arrays.asList(Type.BOSS, Type.MANAGER, Type.BUSINESS, Type.RESEARCHER, Type.TECHNICAL, Type.SECRETARY));
        for (OfficeAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && working.contains(agent.getType()))
                unspawned.add(agent);
        }
        return unspawned;
    }
    public CopyOnWriteArrayList<OfficeAgent> getUnspawnedVisitingAgents() {
        CopyOnWriteArrayList<OfficeAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<Type> visiting = new ArrayList<>(Arrays.asList(Type.CLIENT, Type.DRIVER, Type.VISITOR));
        for (OfficeAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && visiting.contains(agent.getType()))
                unspawned.add(agent);
        }
        return unspawned;
    }

    public ArrayList<OfficeAgent> getTeamMembers(int team){
        ArrayList<OfficeAgent> agents = new ArrayList<>();
        for (OfficeAgent agent: getAgents()){
            if (agent.getTeam() == team){
                agents.add(agent);
            }
        }

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

    public List<OfficeGate> getOfficeGates() {
        return officeGates;
    }

    public List<Cabinet> getCabinets() {
        return cabinets;
    }

    public List<Chair> getChairs() {
        return chairs;
    }

    public List<CollabDesk> getCollabDesks() {
        return collabDesks;
    }

    public List<Couch> getCouches() {
        return couches;
    }

    public List<Cubicle> getCubicles() {
        return cubicles;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<MeetingDesk> getMeetingDesks() {
        return meetingDesks;
    }

    public List<OfficeDesk> getOfficeDesks() {
        return officeDesks;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public List<ReceptionTable> getReceptionTables() {
        return receptionTables;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public List<Toilet> getToilets() {
        return toilets;
    }

    public List<Bathroom> getBathrooms() {
        return bathrooms;
    }

    public List<Breakroom> getBreakrooms() {
        return breakrooms;
    }

    public List<MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }

    public List<OfficeRoom> getOfficeRooms() {
        return officeRooms;
    }

    public List<Reception> getReceptions() {
        return receptions;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<SecurityField> getSecurityFields() {
        return securityFields;
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

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == OfficeGate.class) {
            return this.getOfficeGates();
        }
        else if (amenityClass == Cabinet.class) {
            return this.getCabinets();
        }
        else if (amenityClass == Chair.class) {
            return this.getChairs();
        }
        else if (amenityClass == CollabDesk.class) {
            return this.getCollabDesks();
        }
        else if (amenityClass == Couch.class) {
            return this.getCouches();
        }
        else if (amenityClass == Cubicle.class) {
            return this.getCubicles();
        }
        else if (amenityClass == Door.class) {
            return this.getDoors();
        }
        else if (amenityClass == MeetingDesk.class) {
            return this.getMeetingDesks();
        }
        else if (amenityClass == OfficeDesk.class) {
            return this.getOfficeDesks();
        }
        else if (amenityClass == Plant.class) {
            return this.getPlants();
        }
        else if (amenityClass == Printer.class) {
            return this.getPrinters();
        }
        else if (amenityClass == ReceptionTable.class) {
            return this.getReceptionTables();
        }
        else if (amenityClass == Security.class) {
            return this.getSecurities();
        }
        else if (amenityClass == Table.class) {
            return this.getTables();
        }
        else if (amenityClass == Sink.class) {
            return this.getSinks();
        }
        else if (amenityClass == Toilet.class) {
            return this.getToilets();
        }
        else {
            return null;
        }
    }

    public void createInitialAgentDemographics(int MAX_CLIENTS, int MAX_DRIVERS, int MAX_VISITORS){
        //Janitor
        OfficeAgent janitor = OfficeAgent.OfficeAgentFactory.create(Type.JANITOR, true, 0);
        this.getAgents().add(janitor);

        //Guard
        OfficeAgent guard = OfficeAgent.OfficeAgentFactory.create(Type.GUARD, true, 0);
        this.getAgents().add(guard);

        // Receptionist
        OfficeAgent receptionist = OfficeAgent.OfficeAgentFactory.create(Type.RECEPTIONIST, true, 0);
        this.getAgents().add(receptionist);

        //Boss
        OfficeAgent boss = OfficeAgent.OfficeAgentFactory.create(Type.BOSS, true, 0);
        this.getAgents().add(boss);

        //Team 1
        OfficeAgent manager_1 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true, 1);
        this.getAgents().add(manager_1);

        OfficeAgent technical_1 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true, 1);
        this.getAgents().add(technical_1);

        for (int i = 0; i < 4; i++){
            OfficeAgent business_1 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true, 1);
            this.getAgents().add(business_1);
        }

        for (int i = 0; i < 4; i++){
            OfficeAgent researcher_1 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true, 1);
            this.getAgents().add(researcher_1);
        }

        //Team 2
        OfficeAgent manager_2 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true, 2);
        this.getAgents().add(manager_2);

        OfficeAgent technical_2 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true, 2);
        this.getAgents().add(technical_2);

        for (int i = 0; i < 4; i++){
            OfficeAgent business_2 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true, 2);
            this.getAgents().add(business_2);
        }

        for (int i = 0; i < 4; i++){
            OfficeAgent researcher_2 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true, 2);
            this.getAgents().add(researcher_2);
        }

        //Team 3
        OfficeAgent manager_3 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true, 3);
        this.getAgents().add(manager_3);

        OfficeAgent technical_3 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true, 3);
        this.getAgents().add(technical_3);

        for (int i = 0; i < 4; i++){
            OfficeAgent business_3 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true, 3);
            this.getAgents().add(business_3);
        }

        for (int i = 0; i < 4; i++){
            OfficeAgent researcher_3 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true, 3);
            this.getAgents().add(researcher_3);
        }

        //Team 4
        OfficeAgent manager_4 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true, 4);
        this.getAgents().add(manager_4);

        OfficeAgent technical_4 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true, 4);
        this.getAgents().add(technical_4);

        for (int i = 0; i < 7; i++){
            OfficeAgent business_4 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true, 4);
            this.getAgents().add(business_4);
        }

        for (int i = 0; i < 7; i++){
            OfficeAgent researcher_4 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true, 4);
            this.getAgents().add(researcher_4);
        }

        // Secretary
        OfficeAgent secretary = OfficeAgent.OfficeAgentFactory.create(Type.SECRETARY, true, 0);
        this.getAgents().add(secretary);


        int ctr = 0;

        while (ctr < MAX_CLIENTS){
            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.CLIENT, true, 0);
            ctr++;
            this.getAgents().add(newAgent);
        }
        ctr = 0;
        while (ctr < MAX_DRIVERS){
            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.DRIVER, true, 0);
            ctr++;
            this.getAgents().add(newAgent);
        }
        ctr = 0;
        while (ctr < MAX_VISITORS){
            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.VISITOR, true, 0);
            ctr++;
            this.getAgents().add(newAgent);
        }
//        ctr = 0;
//        while (ctr < OfficeSimulator.MAX_SECRETARIES){
//            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.SECRETARY, true, 0);
//            ctr++;
//            this.getAgents().add(newAgent);
//        }


        for (int i = 0; i < this.getAgents().size(); i++){
            Persona agent1 = agents.get(i).getPersona();
            ArrayList<Integer> IOSScales = new ArrayList<>();
            for (int j = 0 ; j < this.getAgents().size(); j++){
                if (i == j){
                    IOSScales.add(0);
                }
                else {
                    Persona agent2 = agents.get(j).getPersona();
                    if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 2);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                        }
                    }
                    else if (agent1 == Persona.APPROACHABLE_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 2);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 2);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 2);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                        }
                    }
                    else if (agent1 == Persona.MANAGER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.INT_BUSINESS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.EXT_BUSINESS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.INT_RESEARCHER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.EXT_RESEARCHER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.INT_TECHNICAL){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent){
                                    int[] listIOS = new int[]{1, 2, 5, 6};
                                    IOSScales.add(listIOS[Simulator.RANDOM_NUMBER_GENERATOR.nextInt(listIOS.length)]);
                                }
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.EXT_TECHNICAL){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case MANAGER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_BUSINESS -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_RESEARCHER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case INT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case EXT_TECHNICAL -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == Persona.JANITOR){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                    else if (agent1 == Persona.CLIENT){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                    else if (agent1 == Persona.DRIVER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                    else if (agent1 == Persona.VISITOR){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 5);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 5);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                    else if (agent1 == Persona.GUARD){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                    else if (agent1 == Persona.RECEPTIONIST){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                    else if (agent1 == Persona.SECRETARY){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 3);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                        }
                    }
                }
            }
            IOSInteractionChances.add(convertToChanceInteraction(IOSScales));
        }
    }

    public static void configureDefaultIOS(){
        defaultIOS = new CopyOnWriteArrayList<>();
        for (int i = 0; i < OfficeAgent.Persona.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> personaIOS = new CopyOnWriteArrayList<>();
            for (int j = 0; j < OfficeAgent.Persona.values().length; j++){
                OfficeAgent.Persona persona1 = OfficeAgent.Persona.values()[i];
                OfficeAgent.Persona persona2 = OfficeAgent.Persona.values()[j];
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

    public static void configureDefaultInteractionTypeChances(){
        defaultInteractionTypeChances = new CopyOnWriteArrayList<>();
        for (int i = 0; i < OfficeAgent.PersonaActionGroup.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> interactionChances = new CopyOnWriteArrayList<>();
            for (int j = 0; j < GroceryAction.Name.values().length; j++){
                OfficeAgent.PersonaActionGroup personaGroup = OfficeAgent.PersonaActionGroup.values()[i];
                GroceryAction.Name action = GroceryAction.Name.values()[j];
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
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
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
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                        }
                    }
                    case INT_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
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
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case INT_ORG_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(85, 0, 15)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
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
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case EXT_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
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
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case EXT_ORG_STUDENT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
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
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case STRICT_PROFESSOR -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(5, 20, 75)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(15, 30, 55)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
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
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(75, 0, 25)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case APPROACHABLE_PROFESSOR -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_STUDY_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_CLASSROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_DRINKING_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case CLASSROOM_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STUDY_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LUNCH_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case FIND_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case VIEW_BULLETIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 30, 60)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case THROW_ITEM_TRASH_CAN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case FIND_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case QUEUE_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
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
                            case QUEUE_VENDOR -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case FIND_SEAT_CAFETERIA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GO_TO_TRASH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(65, 0, 35)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_GO_FOUNTAIN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CHECK_FOUNTAIN  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
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

    public static class OfficeFactory extends BaseObject.ObjectFactory {
        public static Office create(int rows, int columns) {
            return new Office(rows, columns);
        }
    }

}