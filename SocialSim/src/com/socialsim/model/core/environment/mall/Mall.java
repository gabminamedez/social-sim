package com.socialsim.model.core.environment.mall;

import com.socialsim.model.core.agent.mall.MallAction;
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

    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultIOS;
    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultInteractionTypeChances;

    private final CopyOnWriteArrayList<MallAgent> agents;
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
    private int MAX_FAMILY;
    private int MAX_FRIENDS;
    private int MAX_COUPLE;
    private int MAX_ALONE;

    private int MAX_CURRENT_FAMILY;
    private int MAX_CURRENT_FRIENDS;
    private int MAX_CURRENT_COUPLE;
    private int MAX_CURRENT_ALONE;

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
    private final List<Concierge> concierges;
    private final List<RestoCounter> restoCounters;

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
        this.IOSInteractionChances = new CopyOnWriteArrayList<>();

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
        this.concierges = Collections.synchronizedList(new ArrayList<>());
        this.restoCounters = Collections.synchronizedList(new ArrayList<>());

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
            if (agent.getAgentMovement() == null && family.contains(agent.getPersona()))
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<MallAgent> getUnspawnedFriendsAgents() {
        CopyOnWriteArrayList<MallAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<MallAgent.Persona> friends = new ArrayList<>(Arrays.asList(MallAgent.Persona.ERRAND_FRIENDS, MallAgent.Persona.LOITER_FRIENDS));
        for (MallAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && friends.contains(agent.getPersona()))
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
        ArrayList<MallAgent.Persona> couple = new ArrayList<>(Arrays.asList(MallAgent.Persona.LOITER_COUPLE));
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
        return IOSInteractionChances;
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

    public List<Concierge> getConcierges() {
        return concierges;
    }

    public List<RestoCounter> getRestoCounters() {
        return restoCounters;
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

    public int getMAX_FAMILY() {
        return MAX_FAMILY;
    }

    public void setMAX_FAMILY(int MAX_FAMILY) {
        this.MAX_FAMILY = MAX_FAMILY;
    }

    public int getMAX_FRIENDS() {
        return MAX_FRIENDS;
    }

    public void setMAX_FRIENDS(int MAX_FRIENDS) {
        this.MAX_FRIENDS = MAX_FRIENDS;
    }

    public int getMAX_COUPLE() {
        return MAX_COUPLE;
    }

    public void setMAX_COUPLE(int MAX_COUPLE) {
        this.MAX_COUPLE = MAX_COUPLE;
    }

    public int getMAX_ALONE() {
        return MAX_ALONE;
    }

    public void setMAX_ALONE(int MAX_ALONE) {
        this.MAX_ALONE = MAX_ALONE;
    }

    public int getMAX_CURRENT_FAMILY() {
        return MAX_CURRENT_FAMILY;
    }

    public void setMAX_CURRENT_FAMILY(int MAX_CURRENT_FAMILY) {
        this.MAX_CURRENT_FAMILY = MAX_CURRENT_FAMILY;
    }

    public int getMAX_CURRENT_FRIENDS() {
        return MAX_CURRENT_FRIENDS;
    }

    public void setMAX_CURRENT_FRIENDS(int MAX_CURRENT_FRIENDS) {
        this.MAX_CURRENT_FRIENDS = MAX_CURRENT_FRIENDS;
    }

    public int getMAX_CURRENT_COUPLE() {
        return MAX_CURRENT_COUPLE;
    }

    public void setMAX_CURRENT_COUPLE(int MAX_CURRENT_COUPLE) {
        this.MAX_CURRENT_COUPLE = MAX_CURRENT_COUPLE;
    }

    public int getMAX_CURRENT_ALONE() {
        return MAX_CURRENT_ALONE;
    }

    public void setMAX_CURRENT_ALONE(int MAX_CURRENT_ALONE) {
        this.MAX_CURRENT_ALONE = MAX_CURRENT_ALONE;
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
        else if (amenityClass == Concierge.class) {
            return this.getConcierges();
        }
        else {
            return null;
        }
    }

    public void createInitialAgentDemographics(int MAX_FAMILY, int MAX_FRIENDS, int MAX_COUPLE, int MAX_ALONE){
        MallAgent guard = MallAgent.MallAgentFactory.create(MallAgent.Type.GUARD, MallAgent.Persona.GUARD, null, null, false, true, 0);
        this.getAgents().add(guard);
        MallAgent guard1 = MallAgent.MallAgentFactory.create(MallAgent.Type.GUARD, MallAgent.Persona.GUARD, null, null, false, true, 0);
        this.getAgents().add(guard1);

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
        MallAgent kiosk8 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, false, true, 0);
        this.getAgents().add(kiosk8);

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
        MallAgent cashier10 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, false, true, 9);
        this.getAgents().add(cashier10);

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
        MallAgent sales17 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, false, true, 9);
        this.getAgents().add(sales17);

        MallAgent concierger = MallAgent.MallAgentFactory.create(MallAgent.Type.CONCIERGER, MallAgent.Persona.CONCIERGER, null, null, false, true, 0);
        this.getAgents().add(concierger);

        int ctr = 0;

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
            thisType = MallAgent.Persona.LOITER_COUPLE;

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
                    MallAgent agent1 = agents.get(i), agent2 = agents.get(j);
                    int IOS;
                    if (agent1.getPersona() == MallAgent.Persona.STAFF_STORE_SALES || agent1.getPersona() == MallAgent.Persona.STAFF_STORE_CASHIER){
                        if (agent2.getPersona() == MallAgent.Persona.STAFF_STORE_SALES){
                            if (agent1.getTeam() > 0 && agent1.getTeam() == agent2.getTeam())
                                IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                            else
                                IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                        }
                        else if (agent2.getPersona() == MallAgent.Persona.STAFF_STORE_CASHIER){
                            if (agent1.getTeam() > 0 && agent1.getTeam() == agent2.getTeam()){
                                IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                            }
                            else
                                IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 2).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 2).size()));
                        }
                        else{
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 2).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 2).size()));
                        }
                    }
                    else if (agent1.getPersonaActionGroup() == MallAgent.PersonaActionGroup.FAMILY || agent1.getPersonaActionGroup() == MallAgent.PersonaActionGroup.FRIENDS
                        || agent1.getPersonaActionGroup() == MallAgent.PersonaActionGroup.COUPLE){
                        if (agent1.getPersona().getID() > agent2.getPersona().getID()){
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                        }
                        else if (agent1.getPersona().getID() == agent2.getPersona().getID()){
                            if (agent1.getPersonaActionGroup() == MallAgent.PersonaActionGroup.FAMILY){
                                if (agent1.isLeader() && agent2.getId() - agent1.getId() > 0 && agent2.getId() - agent1.getId() <= 3
                                    || agent2.isLeader() && agent1.getId() - agent2.getId() > 0 && agent1.getId() - agent2.getId() <= 3){
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                                }
                                else{
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                                }
                            }
                            else if (agent1.getPersonaActionGroup() == MallAgent.PersonaActionGroup.FRIENDS){
                                if (agent1.isLeader() && agent2.getId() - agent1.getId() > 0 && agent2.getId() - agent1.getId() <= 2
                                        || agent2.isLeader() && agent1.getId() - agent2.getId() > 0 && agent1.getId() - agent2.getId() <= 2){
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                                }
                                else{
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                                }
                            }
                            else{
                                if (agent1.isLeader() && agent2.getId() - agent1.getId() == 1
                                        || agent2.isLeader() && agent1.getId() - agent2.getId() == 1){
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                                }
                                else{
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                                }
                            }
                        }
                        else{
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                        }
                    }
                    else{
                        IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                    }
                    IOSInteractionChances.get(i).add(this.convertToChanceInteraction(IOS));
                }
            }
        }
    }

    public static void configureDefaultIOS(){
        defaultIOS = new CopyOnWriteArrayList<>();
        for (int i = 0; i < MallAgent.Persona.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> personaIOS = new CopyOnWriteArrayList<>();
            for (int j = 0; j < MallAgent.Persona.values().length; j++){
                MallAgent.Persona persona1 = MallAgent.Persona.values()[i];
                MallAgent.Persona persona2 = MallAgent.Persona.values()[j];
                switch (persona1){
                    case STAFF_STORE_SALES -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case STAFF_STORE_CASHIER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case STAFF_STORE_CASHIER -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case STAFF_STORE_CASHIER -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case STAFF_RESTO -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(2, 3, 4)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case STAFF_KIOSK -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case GUARD -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case ERRAND_FAMILY -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(5, 6, 7)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case LOITER_FAMILY -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FAMILY -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(5, 6, 7)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case ERRAND_FRIENDS -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case LOITER_FRIENDS -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FRIENDS -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            }
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case ERRAND_ALONE -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case LOITER_ALONE -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case LOITER_COUPLE -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> {
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(4, 5, 6, 7)));
                                personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            }
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case CONCIERGER -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                    case JANITOR -> {
                        switch (persona2){
                            case STAFF_STORE_SALES -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_STORE_CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_RESTO -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case STAFF_KIOSK -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case GUARD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FAMILY -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_FRIENDS -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case ERRAND_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_ALONE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case LOITER_COUPLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                            case JANITOR -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                            case CONCIERGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        }
                    }
                }
            }
            defaultIOS.add(personaIOS);
        }
    }

    public static void configureDefaultInteractionTypeChances(){
        defaultInteractionTypeChances = new CopyOnWriteArrayList<>();
        for (int i = 0; i < MallAgent.PersonaActionGroup.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> interactionChances = new CopyOnWriteArrayList<>();
            for (int j = 0; j < MallAction.Name.values().length; j++){
                MallAgent.PersonaActionGroup personaGroup = MallAgent.PersonaActionGroup.values()[i];
                MallAction.Name action = MallAction.Name.values()[j];
                switch (personaGroup){
                    case STAFF_STORE_SALES -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 80, 20)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case STAFF_STORE_CASHIER -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case STAFF_RESTO -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 80, 20)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case GUARD -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case STAFF_KIOSK -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case FAMILY -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(30, 5, 65)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(30, 0, 70)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 5, 75)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                        }
                    }
                    case FRIENDS -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(30, 5, 65)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(30, 0, 70)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 5, 75)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 0, 80)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                        }
                    }
                    case ALONE -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 30, 10)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 40, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 30, 10)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 20, 20)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                        }
                    }
                    case COUPLE -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 40, 60)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 5, 45)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 5, 85)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 20, 10)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(70, 5, 25)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 20, 80)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 50, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 5, 15)));
                        }
                    }
                    case CONCIERGER -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case JANITOR -> {
                        switch(action){
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case VIEW_DIRECTORY -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SIT_ON_BENCH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_SALES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_STORE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_KIOSK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RESTAURANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_STAFF_RESTO -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RESTAURANT_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_DINING_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case DINING_AREA_STAY_PUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_KIOSK_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_SERVE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_RESTO_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_STORE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_SALES_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_CASHIER_ANSWER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case ASK_CONCIERGE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
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

    public static class MallFactory extends BaseObject.ObjectFactory {
        public static Mall create(int rows, int columns) {
            return new Mall(rows, columns);
        }
    }

}