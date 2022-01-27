package com.socialsim.model.core.environment.mall;

import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.mall.patchfield.*;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.*;
import com.socialsim.model.simulator.Simulator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Mall extends Environment {

    private final CopyOnWriteArrayList<MallAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> IOS;

    private final List<MallGate> mallGates;
    private final List<Bench> benches;
    private final List<Digital> digitals;
    private final List<Kiosk> kiosks;
    private final List<Plant> plants;
    private final List<Security> securities;
    private final List<StoreCounter> storeCounters;
    private final List<Table> tables;
    private final List<Trash> trashes;
    private final List<Toilet> toilets;
    private final List<Sink> sinks;
    private final List<StoreAisle> storeAisles;


    private final List<Bathroom> bathrooms;
    private final List<Dining> dinings;
    private final List<Restaurant> restaurants;
    private final List<Showcase> showcases;
    private final List<Store> stores;
    private final List<Wall> walls;
    private final List<SecurityField> securityFields;
    private final List<KioskField> kioskFields;

    private static final Mall.MallFactory mallFactory;

    static {
        mallFactory = new Mall.MallFactory();
    }

    public Mall(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.agentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.mallGates = Collections.synchronizedList(new ArrayList<>());
        this.benches = Collections.synchronizedList(new ArrayList<>());
        this.digitals = Collections.synchronizedList(new ArrayList<>());
        this.kiosks = Collections.synchronizedList(new ArrayList<>());
        this.plants = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.storeCounters = Collections.synchronizedList(new ArrayList<>());
        this.tables = Collections.synchronizedList(new ArrayList<>());
        this.trashes = Collections.synchronizedList(new ArrayList<>());
        this.toilets = Collections.synchronizedList(new ArrayList<>());
        this.sinks = Collections.synchronizedList(new ArrayList<>());
        this.storeAisles = Collections.synchronizedList(new ArrayList<>());

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.dinings = Collections.synchronizedList(new ArrayList<>());
        this.restaurants = Collections.synchronizedList(new ArrayList<>());
        this.showcases = Collections.synchronizedList(new ArrayList<>());
        this.stores = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
        this.kioskFields = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<MallAgent> getAgents() {
        return agents;
    }

    public CopyOnWriteArrayList<MallAgent> getUnspawnedFamilyAgents() {
        CopyOnWriteArrayList<MallAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<MallAgent.Persona> family = new ArrayList<>(Arrays.asList(MallAgent.Persona.ERRAND_FAMILY, MallAgent.Persona.LOITER_FAMILY));
        for (MallAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && family.contains(agent.getPersona()) && agent.isLeader())
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<MallAgent> getUnspawnedFriendsAgents() {
        CopyOnWriteArrayList<MallAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<MallAgent.Persona> friends = new ArrayList<>(Arrays.asList(MallAgent.Persona.ERRAND_FRIENDS, MallAgent.Persona.LOITER_FRIENDS));
        for (MallAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && friends.contains(agent.getPersona()) && agent.isLeader())
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<MallAgent> getUnspawnedAloneAgents() {
        CopyOnWriteArrayList<MallAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<MallAgent.Persona> alone = new ArrayList<>(Arrays.asList(MallAgent.Persona.ERRAND_ALONE, MallAgent.Persona.LOITER_ALONE));
        for (MallAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && alone.contains(agent.getPersona()))
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<MallAgent> getUnspawnedCoupleAgents() {
        CopyOnWriteArrayList<MallAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<MallAgent.Persona> couple = new ArrayList<>(Arrays.asList(MallAgent.Persona.ERRAND_COUPLE, MallAgent.Persona.LOITER_COUPLE));
        for (MallAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && couple.contains(agent.getPersona()))
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<MallAgent> getMovableAgents() {
        CopyOnWriteArrayList<MallAgent> movable = new CopyOnWriteArrayList<>();
        for (MallAgent agent: getAgents()){
            if (agent.getAgentMovement() != null)
                movable.add(agent);
        }
        return movable;
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> getIOS() {
        return IOS;
    }

    @Override
    public SortedSet<Patch> getAmenityPatchSet() {
        return amenityPatchSet;
    }

    @Override
    public SortedSet<Patch> getAgentPatchSet() {
        return agentPatchSet;
    }

    public List<MallGate> getMallGates() {
        return mallGates;
    }

    public List<Bench> getBenches() {
        return benches;
    }

    public List<Digital> getDigitals() {
        return digitals;
    }

    public List<Kiosk> getKiosks() {
        return kiosks;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<StoreCounter> getStoreCounters() {
        return storeCounters;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Trash> getTrashes() {
        return trashes;
    }

    public List<Toilet> getToilets() {
        return toilets;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public List<StoreAisle> getStoreAisles() {
        return storeAisles;
    }

    public List<Bathroom> getBathrooms() {
        return bathrooms;
    }

    public List<Dining> getDinings() {
        return dinings;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public List<Showcase> getShowcases() {
        return showcases;
    }

    public List<Store> getStores() {
        return stores;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<SecurityField> getSecurityFields() {
        return securityFields;
    }

    public List<KioskField> getKioskFields() {
        return kioskFields;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == MallGate.class) {
            return this.getMallGates();
        }
        else if (amenityClass == Bench.class) {
            return this.getBenches();
        }
        else if (amenityClass == Digital.class) {
            return this.getDigitals();
        }
        else if (amenityClass == Kiosk.class) {
            return this.getKiosks();
        }
        else if (amenityClass == Plant.class) {
            return this.getPlants();
        }
        else if (amenityClass == Security.class) {
            return this.getSecurities();
        }
        else if (amenityClass == StoreCounter.class) {
            return this.getStoreCounters();
        }
        else if (amenityClass == Table.class) {
            return this.getTables();
        }
        else if (amenityClass == Trash.class) {
            return this.getTrashes();
        }
        else if (amenityClass == Toilet.class) {
            return this.getToilets();
        }
        else if (amenityClass == Sink.class) {
            return this.getSinks();
        }
        else {
            return null;
        }
    }

    public void createInitialAgentDemographics(int MAX_FAMILY, int MAX_FRIENDS, int MAX_COUPLE, int MAX_ALONE){
        //Guard
        MallAgent guard = MallAgent.MallAgentFactory.create(MallAgent.Type.GUARD, MallAgent.Persona.GUARD, null, null, false, true, 0);
        this.getAgents().add(guard);

        MallAgent kiosk1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk1);
        MallAgent kiosk2 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk2);
        MallAgent kiosk3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk3);
        MallAgent kiosk4 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk4);
        MallAgent kiosk5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk5);
        MallAgent kiosk6 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk6);
        MallAgent kiosk7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk7);

        MallAgent resto1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 1);
        this.getAgents().add(resto1);
        MallAgent resto2 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 1);
        this.getAgents().add(resto2);
        MallAgent resto3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 1);
        this.getAgents().add(resto3);
        MallAgent resto4 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 1);
        this.getAgents().add(resto4);
        MallAgent resto5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 2);
        this.getAgents().add(resto5);
        MallAgent resto6 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 2);
        this.getAgents().add(resto6);
        MallAgent resto7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 2);
        this.getAgents().add(resto7);
        MallAgent resto8 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, false, true, 2);
        this.getAgents().add(resto8);

        MallAgent cashier1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 1);
        this.getAgents().add(cashier1);
        MallAgent cashier2 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 2);
        this.getAgents().add(cashier2);
        MallAgent cashier3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 3);
        this.getAgents().add(cashier3);
        MallAgent cashier4 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 4);
        this.getAgents().add(cashier4);
        MallAgent cashier5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 5);
        this.getAgents().add(cashier5);
        MallAgent cashier6 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 6);
        this.getAgents().add(cashier6);
        MallAgent cashier7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 7);
        this.getAgents().add(cashier7);
        MallAgent cashier8 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 8);
        this.getAgents().add(cashier8);
        MallAgent cashier9 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 9);
        this.getAgents().add(cashier9);
        MallAgent cashier10 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 10);
        this.getAgents().add(cashier10);
        MallAgent cashier11 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true,  11);
        this.getAgents().add(cashier11);

        MallAgent sales = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 1);
        this.getAgents().add(sales);
        MallAgent sales1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 2);
        this.getAgents().add(sales1);
        MallAgent sales3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 3);
        this.getAgents().add(sales3);
        MallAgent sales5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 4);
        this.getAgents().add(sales5);
        MallAgent sales7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 5);
        this.getAgents().add(sales7);
        MallAgent sales9 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 6);
        this.getAgents().add(sales9);
        MallAgent sales11 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 7);
        this.getAgents().add(sales11);
        MallAgent sales13 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true,8);
        this.getAgents().add(sales13);
        MallAgent sales15 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true,9);
        this.getAgents().add(sales15);
        MallAgent sales17 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 10);
        this.getAgents().add(sales17);
        MallAgent sales19 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 11);
        this.getAgents().add(sales19);

        int ctr = 0;
        //Students and Professors
        while (ctr < MAX_FAMILY){
            boolean isErrand = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            MallAgent.Persona thisType = null;
            if (isErrand) {
                thisType = MallAgent.Persona.ERRAND_FAMILY;
            }
            else {
                thisType = MallAgent.Persona.LOITER_FAMILY;
            }
            MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.Gender gender2 = null;
            if (gender1 == MallAgent.Gender.MALE) {
                gender2 = MallAgent.Gender.FEMALE;
            }
            else {
                gender2 = MallAgent.Gender.MALE;
            }
            MallAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.Gender gender4 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;

            MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, MallAgent.AgeGroup.FROM_25_TO_54, true, true, 0);
            this.getAgents().add(leaderAgent);

            MallAgent agent2 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender2, MallAgent.AgeGroup.FROM_25_TO_54, false, true, 0);
            this.getAgents().add(agent2);

            MallAgent agent3 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender3, MallAgent.AgeGroup.FROM_15_TO_24, false, true, 0);
            this.getAgents().add(agent3);

            MallAgent agent4 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender4, MallAgent.AgeGroup.FROM_15_TO_24, false, true, 0);
            this.getAgents().add(agent4);

            ctr++;
        }
        ctr = 0;
        while (ctr < MAX_FRIENDS){
            boolean isErrand = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            MallAgent.Persona thisType = null;
            if (isErrand) {
                thisType = MallAgent.Persona.ERRAND_FRIENDS;
            }
            else {
                thisType = MallAgent.Persona.LOITER_FRIENDS;
            }

            MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;

            MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, MallAgent.AgeGroup.FROM_15_TO_24, true, true, 0);
            this.getAgents().add(leaderAgent);

            MallAgent agent2 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender2, MallAgent.AgeGroup.FROM_15_TO_24, false, true, 0);
            this.getAgents().add(agent2);

            MallAgent agent3 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender3, MallAgent.AgeGroup.FROM_15_TO_24, false, true, 0);
            this.getAgents().add(agent3);
            ctr++;
        }
        ctr = 0;
        while (ctr < MAX_COUPLE){
            boolean isErrand = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            MallAgent.Persona thisType = null;
            if (isErrand) {
                thisType = MallAgent.Persona.ERRAND_COUPLE;
            }
            else {
                thisType = MallAgent.Persona.LOITER_COUPLE;
            }

            MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.AgeGroup age1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_15_TO_24 : MallAgent.AgeGroup.FROM_25_TO_54;

            MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, age1, true, true, 0);
            this.getAgents().add(leaderAgent);

            MallAgent agent2 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender2, age1, false, true, 0);
            this.getAgents().add(agent2);

            ctr++;
        }
        ctr = 0;
        while (ctr < MAX_ALONE){
            boolean isErrand = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            MallAgent.Persona thisType = null;
            if (isErrand) {
                thisType = MallAgent.Persona.ERRAND_ALONE;
            }
            else {
                thisType = MallAgent.Persona.LOITER_ALONE;
            }

            MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
            MallAgent.AgeGroup age1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_15_TO_24 : MallAgent.AgeGroup.FROM_25_TO_54;

            MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, age1, false, true, 0);
            this.getAgents().add(leaderAgent);

            ctr++;
        }

        for (int i = 0; i < this.getAgents().size(); i++){
            MallAgent.Persona agent1 = agents.get(i).getPersona();
            ArrayList<Integer> IOSScales = new ArrayList<>();
            for (int j = 0 ; j < this.getAgents().size(); j++){
                if (i == j){
                    IOSScales.add(0);
                }
                else {
                    MallAgent.Persona agent2 = agents.get(j).getPersona();
                    if (agent1 == MallAgent.Persona.STAFF_STORE_SALES){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case STAFF_STORE_SALES -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case STAFF_STORE_CASHIER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.STAFF_STORE_CASHIER){
                        switch (agent2){
                            case STAFF_STORE_SALES -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case STAFF_STORE_CASHIER -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.STAFF_RESTO){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> {
                                int agent = agents.get(i).getTeam(), otherAgent = agents.get(j).getTeam();
                                if (agent == otherAgent)
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.STAFF_KIOSK){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.GUARD){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.ERRAND_FAMILY){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> {
                                MallAgent agent = agents.get(i), otherAgent = agents.get(j);
                                if (agent.isLeader() && agent.getId() < otherAgent.getId() && agent.getId() + 3 >= otherAgent.getId()) // leader of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 5);
                                else if (otherAgent.isLeader() && otherAgent.getId() < agent.getId() && otherAgent.getId() + 3 >= agent.getId()) // follower of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 5);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.LOITER_FAMILY){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FAMILY -> {
                                MallAgent agent = agents.get(i), otherAgent = agents.get(j);
                                if (agent.isLeader() && agent.getId() < otherAgent.getId() && agent.getId() + 3 >= otherAgent.getId()) // leader of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 5);
                                else if (otherAgent.isLeader() && otherAgent.getId() < agent.getId() && otherAgent.getId() + 3 >= agent.getId()) // follower of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 5);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.ERRAND_FRIENDS){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> {
                                MallAgent agent = agents.get(i), otherAgent = agents.get(j);
                                if (agent.isLeader() && agent.getId() < otherAgent.getId() && agent.getId() + 3 >= otherAgent.getId()) // leader of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else if (otherAgent.isLeader() && otherAgent.getId() < agent.getId() && otherAgent.getId() + 3 >= agent.getId()) // follower of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.LOITER_FRIENDS){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_FRIENDS -> {
                                MallAgent agent = agents.get(i), otherAgent = agents.get(j);
                                if (agent.isLeader() && agent.getId() < otherAgent.getId() && agent.getId() + 3 >= otherAgent.getId()) // leader of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else if (otherAgent.isLeader() && otherAgent.getId() < agent.getId() && otherAgent.getId() + 3 >= agent.getId()) // follower of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            }
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.ERRAND_ALONE){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.LOITER_ALONE){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case LOITER_COUPLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                        }
                    }
                    else if (agent1 == MallAgent.Persona.LOITER_COUPLE){
                        switch (agent2){
                            case STAFF_STORE_SALES -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_STORE_CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_RESTO -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case STAFF_KIOSK -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case GUARD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FAMILY -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case LOITER_FRIENDS -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case ERRAND_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_ALONE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case LOITER_COUPLE -> {
                                MallAgent agent = agents.get(i), otherAgent = agents.get(j);
                                if (agent.isLeader() && agent.getId() < otherAgent.getId() && agent.getId() + 3 >= otherAgent.getId()) // leader of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 4);
                                else if (otherAgent.isLeader() && otherAgent.getId() < agent.getId() && otherAgent.getId() + 3 >= agent.getId()) // follower of family
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 4);
                                else
                                    IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            }
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

    public static class MallFactory extends BaseObject.ObjectFactory {
        public static Mall create(int rows, int columns) {
            return new Mall(rows, columns);
        }
    }

}