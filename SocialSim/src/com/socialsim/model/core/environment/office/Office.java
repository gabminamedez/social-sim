package com.socialsim.model.core.environment.office;

import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.agent.office.OfficeAction;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.*;
import com.socialsim.model.simulator.Simulator;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static com.socialsim.model.core.agent.office.OfficeAgent.*;

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
    private int MAX_CLIENTS;
    private int MAX_DRIVERS;
    private int MAX_VISITORS;
    private int MAX_CURRENT_CLIENTS;
    private int MAX_CURRENT_DRIVERS;
    private int MAX_CURRENT_VISITORS;

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

    private final List<Bulletin> bulletins;
    private final List<Fridge> fridges;
    private final List<WaterDispenser> waterDispensers;
    private final List<Whiteboard> whiteboards;

    private final List<Bathroom> bathrooms;
    private final List<Breakroom> breakrooms;
    private final List<MeetingRoom> meetingRooms;
    private final List<OfficeRoom> officeRooms;
    private final List<Reception> receptions;
    private final List<SecretaryRoom> secretaryRooms;
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

        this.bulletins = Collections.synchronizedList(new ArrayList<>());
        this.fridges = Collections.synchronizedList(new ArrayList<>());
        this.waterDispensers = Collections.synchronizedList(new ArrayList<>());
        this.whiteboards = Collections.synchronizedList(new ArrayList<>());

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.breakrooms = Collections.synchronizedList(new ArrayList<>());
        this.meetingRooms = Collections.synchronizedList(new ArrayList<>());
        this.officeRooms = Collections.synchronizedList(new ArrayList<>());
        this.receptions = Collections.synchronizedList(new ArrayList<>());
        this.secretaryRooms = Collections.synchronizedList(new ArrayList<>());
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

    public List<Bulletin> getBulletins() {
        return bulletins;
    }

    public List<Fridge> getFridges() {
        return fridges;
    }

    public List<WaterDispenser> getWaterDispensers() {
        return waterDispensers;
    }

    public List<Whiteboard> getWhiteboards() {
        return whiteboards;
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

    public List<SecretaryRoom> getSecretaryRooms() {
        return secretaryRooms;
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

    public int getMAX_CLIENTS() {
        return MAX_CLIENTS;
    }

    public void setMAX_CLIENTS(int MAX_CLIENTS) {
        this.MAX_CLIENTS = MAX_CLIENTS;
    }

    public int getMAX_DRIVERS() {
        return MAX_DRIVERS;
    }

    public void setMAX_DRIVERS(int MAX_DRIVERS) {
        this.MAX_DRIVERS = MAX_DRIVERS;
    }

    public int getMAX_VISITORS() {
        return MAX_VISITORS;
    }

    public void setMAX_VISITORS(int MAX_VISITORS) {
        this.MAX_VISITORS = MAX_VISITORS;
    }

    public int getMAX_CURRENT_CLIENTS() {
        return MAX_CLIENTS;
    }

    public void setMAX_CURRENT_CLIENTS(int MAX_CURRENT_CLIENTS) {
        this.MAX_CURRENT_CLIENTS = MAX_CURRENT_CLIENTS;
    }

    public int getMAX_CURRENT_DRIVERS() {
        return MAX_CURRENT_DRIVERS;
    }

    public void setMAX_CURRENT_DRIVERS(int MAX_CURRENT_DRIVERS) {
        this.MAX_CURRENT_DRIVERS = MAX_CURRENT_DRIVERS;
    }

    public int getMAX_CURRENT_VISITORS() {
        return MAX_CURRENT_VISITORS;
    }

    public void setMAX_CURRENT_VISITORS(int MAX_CURRENT_VISITORS) {
        this.MAX_CURRENT_VISITORS = MAX_CURRENT_VISITORS;
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
        else if(amenityClass == Fridge.class){
            return this.getFridges();
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
        else if(amenityClass == WaterDispenser.class){
            return this.getWaterDispensers();
        }
        else {
            return null;
        }
    }

    public void createInitialAgentDemographics(int MAX_CLIENTS, int MAX_DRIVERS, int MAX_VISITORS){
        OfficeAgent janitor = OfficeAgent.OfficeAgentFactory.create(Type.JANITOR, true, 0);
        this.getAgents().add(janitor);

        OfficeAgent guard = OfficeAgent.OfficeAgentFactory.create(Type.GUARD, true, 0);
        this.getAgents().add(guard);

        OfficeAgent receptionist = OfficeAgent.OfficeAgentFactory.create(Type.RECEPTIONIST, true, 0);
        this.getAgents().add(receptionist);

        OfficeAgent boss = OfficeAgent.OfficeAgentFactory.create(Type.BOSS, true, 0);
        this.getAgents().add(boss);

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
    }

    public double convertToChanceInteraction(int x){
        double CHANCE = ((double) x - 1) / 7 + Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * 1/7;
        return CHANCE;
    }

    public void convertIOSToChances(){
        IOSInteractionChances = new CopyOnWriteArrayList<>();
        IOSScales.toString();
        for(int i = 0; i < agents.size(); i++){
            IOSInteractionChances.add(new CopyOnWriteArrayList<>());
            for(int j = 0; j < agents.size(); j++){
                if (i == j){
                    IOSInteractionChances.get(i).add((double) 0);
                }
                else{
                    OfficeAgent agent1 = agents.get(i), agent2 = agents.get(j);
                    int IOS;
                    if (agent1.getPersonaActionGroup() == PersonaActionGroup.MANAGER
                            || agent1.getPersonaActionGroup() == PersonaActionGroup.INT_WORKER
                            || agent1.getPersonaActionGroup() == PersonaActionGroup.EXT_WORKER
                            || agent1.getPersonaActionGroup() == PersonaActionGroup.INT_TECHNICAL
                            || agent1.getPersonaActionGroup() == PersonaActionGroup.EXT_TECHNICAL){
                        int offset;
                        switch (agent2.getPersona()){
                            case PROFESSIONAL_BOSS, APPROACHABLE_BOSS, MANAGER -> offset = 0;
                            case INT_BUSINESS -> offset = 1;
                            case EXT_BUSINESS -> offset = 2;
                            case INT_RESEARCHER -> offset = 3;
                            case EXT_RESEARCHER -> offset = 4;
                            case INT_TECHNICAL -> offset = 5;
                            case EXT_TECHNICAL -> offset = 6;
                            default -> offset = 7;
                        }
                        if (agent2.getTeam() == 0){
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + offset).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + offset).size()));
                        }
                        else if (agent1.getTeam() > 0 && agent1.getTeam() == agent2.getTeam())
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + offset).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + offset).size()));
                        else
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + offset + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + offset + 1).size()));
                    }
                    else
                        IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                    IOSInteractionChances.get(i).add(this.convertToChanceInteraction(IOS));
                }
            }
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
                    case PROFESSIONAL_BOSS -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case APPROACHABLE_BOSS -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4, 5)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                        }
                    }
                    case MANAGER -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case INT_BUSINESS -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case EXT_BUSINESS -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case INT_RESEARCHER -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case EXT_RESEARCHER -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case INT_TECHNICAL -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case EXT_TECHNICAL -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5)));
                            case MANAGER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_BUSINESS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_RESEARCHER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case INT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case EXT_TECHNICAL -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                    case JANITOR -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        }
                    }
                    case CLIENT -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        }
                    }
                    case DRIVER -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        }
                    }
                    case VISITOR -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6, 7)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        }
                    }
                    case GUARD -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        }
                    }
                    case RECEPTIONIST -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        }
                    }
                    case SECRETARY -> {
                        switch (persona2){
                            case PROFESSIONAL_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case APPROACHABLE_BOSS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5, 6)));
                            case MANAGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_BUSINESS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_RESEARCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case INT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case EXT_TECHNICAL -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case CLIENT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case DRIVER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case VISITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case RECEPTIONIST -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                            case SECRETARY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
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
            for (int j = 0; j < OfficeAction.Name.values().length; j++){
                OfficeAgent.PersonaActionGroup personaGroup = OfficeAgent.PersonaActionGroup.values()[i];
                OfficeAction.Name action = OfficeAction.Name.values()[j];
                switch (personaGroup){
                    case PROFESSIONAL_BOSS -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                        }
                    }
                    case APPROACHABLE_BOSS -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                        }
                    }
                    case MANAGER -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));

                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));

                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 30, 70)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));

                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));

                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));

                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 40, 50)));
                        }
                    }
                    case INT_WORKER -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 80, 20)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 70, 30)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                        }
                    }
                    case EXT_WORKER -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 80, 20)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 70, 30)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                        }
                    }
                    case INT_TECHNICAL -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 90, 10)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 70, 30)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 60, 40)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 60, 40)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(40, 30, 30)));
                        }
                    }
                    case EXT_TECHNICAL -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 90, 10)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 70, 30)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 75, 25)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 60, 40)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 60, 40)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                        }
                    }
                    case JANITOR -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 90, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 90, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case CLIENT -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case DRIVER -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case VISITOR -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 100)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case GUARD -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 100, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case RECEPTIONIST -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case SECRETARY -> {
                        switch(action){
                            case LEAVE_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_TO_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case EAT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case EXIT_LUNCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GUARD_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_CLEAN_TOILET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case JANITOR_WATER_PLANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CLIENT_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DRIVER_GO_COUCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_RECEPTIONIST -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VISITOR_GO_OFFICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RECEPTIONIST_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SECRETARY_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SECRETARY_CHECK_CABINET -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case SECRETARY_GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_TO_OFFICE_ROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case ASK_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case ASK_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_BOSS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_MANAGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ANSWER_WORKER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case PRINTING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_COLLAB -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case COLLABORATE  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TECHNICAL_GO_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_PRINTER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIX_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case MEETING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_DISPENSER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GETTING_WATER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GOING_FRIDGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GETTING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case TAKING_BREAK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 30, 50)));
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

    public static class OfficeFactory extends BaseObject.ObjectFactory {
        public static Office create(int rows, int columns) {
            return new Office(rows, columns);
        }
    }

}