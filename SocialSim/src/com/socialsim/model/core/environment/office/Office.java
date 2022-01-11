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

    private final CopyOnWriteArrayList<OfficeAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> IOS;

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
        this.IOS = new CopyOnWriteArrayList<>();

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
        return IOS;
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
        OfficeAgent janitor = OfficeAgent.OfficeAgentFactory.create(Type.JANITOR, true);
        this.getAgents().add(janitor);

        //Guard
        OfficeAgent guard = OfficeAgent.OfficeAgentFactory.create(Type.GUARD, true);
        this.getAgents().add(guard);

        // Receptionist
        OfficeAgent receptionist = OfficeAgent.OfficeAgentFactory.create(Type.RECEPTIONIST, true);
        this.getAgents().add(receptionist);

        //Boss
        OfficeAgent boss = OfficeAgent.OfficeAgentFactory.create(Type.BOSS, true);
        this.getAgents().add(boss);

        //Team 1
        OfficeAgent manager_1 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true);
        this.getAgents().add(manager_1);

        OfficeAgent technical_1 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true);
        this.getAgents().add(technical_1);

        for (int i = 0; i < 4; i++){
            OfficeAgent business_1 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true);
            this.getAgents().add(business_1);
        }

        for (int i = 0; i < 4; i++){
            OfficeAgent researcher_1 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true);
            this.getAgents().add(researcher_1);
        }

        //Team 2
        OfficeAgent manager_2 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true);
        this.getAgents().add(manager_2);

        OfficeAgent technical_2 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true);
        this.getAgents().add(technical_2);

        for (int i = 0; i < 4; i++){
            OfficeAgent business_2 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true);
            this.getAgents().add(business_2);
        }

        for (int i = 0; i < 4; i++){
            OfficeAgent researcher_2 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true);
            this.getAgents().add(researcher_2);
        }

        //Team 3
        OfficeAgent manager_3 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true);
        this.getAgents().add(manager_3);

        OfficeAgent technical_3 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true);
        this.getAgents().add(technical_3);

        for (int i = 0; i < 4; i++){
            OfficeAgent business_3 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true);
            this.getAgents().add(business_3);
        }

        for (int i = 0; i < 4; i++){
            OfficeAgent researcher_3 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true);
            this.getAgents().add(researcher_3);
        }

        //Team 4
        OfficeAgent manager_4 = OfficeAgent.OfficeAgentFactory.create(Type.MANAGER, true);
        this.getAgents().add(manager_4);

        OfficeAgent technical_4 = OfficeAgent.OfficeAgentFactory.create(Type.TECHNICAL, true);
        this.getAgents().add(technical_4);

        for (int i = 0; i < 7; i++){
            OfficeAgent business_4 = OfficeAgent.OfficeAgentFactory.create(Type.BUSINESS, true);
            this.getAgents().add(business_4);
        }

        for (int i = 0; i < 7; i++){
            OfficeAgent researcher_4 = OfficeAgent.OfficeAgentFactory.create(Type.RESEARCHER, true);
            this.getAgents().add(researcher_4);
        }

        // Secretary
        OfficeAgent secretary = OfficeAgent.OfficeAgentFactory.create(Type.SECRETARY, true);
        this.getAgents().add(secretary);


        int ctr = 0;

        while (ctr < MAX_CLIENTS){
            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.CLIENT, true);
            ctr++;
            this.getAgents().add(newAgent);
        }
        ctr = 0;
        while (ctr < MAX_DRIVERS){
            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.DRIVER, true);
            ctr++;
            this.getAgents().add(newAgent);
        }
        ctr = 0;
        while (ctr < MAX_VISITORS){
            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.VISITOR, true);
            ctr++;
            this.getAgents().add(newAgent);
        }
//        ctr = 0;
//        while (ctr < OfficeSimulator.MAX_SECRETARIES){
//            OfficeAgent newAgent = OfficeAgent.OfficeAgentFactory.create(Type.SECRETARY, true);
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
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                    else if (agent1 == Persona.PROFESSIONAL_BOSS){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case PROFESSIONAL_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case APPROACHABLE_BOSS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case MANAGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_BUSINESS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case INT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case EXT_RESEARCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case INT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case EXT_TECHNICAL -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case JANITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case CLIENT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DRIVER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case VISITOR -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case RECEPTIONIST -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                            case SECRETARY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 1);
                        }
                    }
                }
            }
            IOS.add(convertToChanceInteraction(IOSScales));
        }
    }

    public CopyOnWriteArrayList<Double> convertToChanceInteraction(ArrayList<Integer> IOSScales){// Convert IOS to chance based only on threshold, not 0 to said scale
        CopyOnWriteArrayList<Double> listIOS = new CopyOnWriteArrayList<>();
        for (int iosScale : IOSScales) {
            if (iosScale <= 0)
                listIOS.add((double) 0);
            else
                listIOS.add((iosScale - 1) / 7 + Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * 1/7);
//            listIOS.add(Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * iosScale / 7);
        }
        return listIOS;
    }

    public static class OfficeFactory extends BaseObject.ObjectFactory {
        public static Office create(int rows, int columns) {
            return new Office(rows, columns);
        }
    }

}