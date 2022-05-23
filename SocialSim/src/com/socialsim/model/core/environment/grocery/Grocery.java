package com.socialsim.model.core.environment.grocery;

import com.socialsim.model.core.agent.grocery.GroceryAction;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchfield.*;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.*;
import com.socialsim.model.simulator.Simulator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Grocery extends Environment {

    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultIOS;
    public static CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> defaultInteractionTypeChances;

    private final CopyOnWriteArrayList<GroceryAgent> agents;
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
    private int MAX_ALONE;
    private int MAX_CURRENT_FAMILY;
    private int MAX_CURRENT_ALONE;

    private final List<GroceryGate> groceryGates;
    private final List<CartRepo> cartRepos;
    private final List<CashierCounter> cashierCounters;
    private final List<FreshProducts> freshProducts;
    private final List<FrozenProducts> frozenProducts;
    private final List<FrozenWall> frozenWalls;
    private final List<MeatSection> meatSections;
    private final List<ProductAisle> productAisles;
    private final List<ProductShelf> productShelves;
    private final List<ProductWall> productWalls;
    private final List<Security> securities;
    private final List<ServiceCounter> serviceCounters;
    private final List<Stall> stalls;
    private final List<Table> tables;
    private final List<Sink> sinks;
    private final List<Toilet> toilets;

    private final List<Wall> walls;
    private final List<CashierCounterField> cashierCounterFields;
    private final List<GroceryGateField> groceryGateFields;
    private final List<SecurityField> securityFields;
    private final List<ServiceCounterField> serviceCounterFields;
    private final List<StallField> stallFields;
    private final List<BathroomField> bathroomFields;

    public static final Grocery.GroceryFactory groceryFactory;

    static {
        groceryFactory = new Grocery.GroceryFactory();
    }

    public Grocery(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();
        this.IOSInteractionChances = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.agentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.groceryGates = Collections.synchronizedList(new ArrayList<>());
        this.cartRepos = Collections.synchronizedList(new ArrayList<>());
        this.cashierCounters = Collections.synchronizedList(new ArrayList<>());
        this.freshProducts = Collections.synchronizedList(new ArrayList<>());
        this.frozenProducts = Collections.synchronizedList(new ArrayList<>());
        this.frozenWalls = Collections.synchronizedList(new ArrayList<>());
        this.meatSections = Collections.synchronizedList(new ArrayList<>());
        this.productAisles = Collections.synchronizedList(new ArrayList<>());
        this.productShelves = Collections.synchronizedList(new ArrayList<>());
        this.productWalls = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.serviceCounters = Collections.synchronizedList(new ArrayList<>());
        this.stalls = Collections.synchronizedList(new ArrayList<>());
        this.tables = Collections.synchronizedList(new ArrayList<>());
        this.sinks = Collections.synchronizedList(new ArrayList<>());
        this.toilets = Collections.synchronizedList(new ArrayList<>());

        this.walls = Collections.synchronizedList(new ArrayList<>());
        this.cashierCounterFields = Collections.synchronizedList(new ArrayList<>());
        this.groceryGateFields = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
        this.serviceCounterFields = Collections.synchronizedList(new ArrayList<>());
        this.stallFields = Collections.synchronizedList(new ArrayList<>());
        this.bathroomFields = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<GroceryAgent> getAgents() {
        return agents;
    }

    public CopyOnWriteArrayList<GroceryAgent> getUnspawnedFamilyAgents() {
        CopyOnWriteArrayList<GroceryAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<GroceryAgent.Persona> family = new ArrayList<>(Arrays.asList(GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER));
        for (GroceryAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && family.contains(agent.getPersona()))
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<GroceryAgent> getUnspawnedAloneAgents() {
        CopyOnWriteArrayList<GroceryAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<GroceryAgent.Persona> alone = new ArrayList<>(Arrays.asList(GroceryAgent.Persona.STTP_ALONE_CUSTOMER, GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER));
        for (GroceryAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && alone.contains(agent.getPersona()))
                unspawned.add(agent);
        }
        return unspawned;
    }

    public CopyOnWriteArrayList<GroceryAgent> getMovableAgents() {
        CopyOnWriteArrayList<GroceryAgent> movable = new CopyOnWriteArrayList<>();
        for (GroceryAgent agent: getAgents()){
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

    public List<GroceryGate> getGroceryGates() {
        return groceryGates;
    }

    public List<CartRepo> getCartRepos() {
        return cartRepos;
    }

    public List<CashierCounter> getCashierCounters() {
        return cashierCounters;
    }

    public List<FreshProducts> getFreshProducts() {
        return freshProducts;
    }

    public List<FrozenProducts> getFrozenProducts() {
        return frozenProducts;
    }

    public List<FrozenWall> getFrozenWalls() {
        return frozenWalls;
    }

    public List<MeatSection> getMeatSections() {
        return meatSections;
    }

    public List<ProductAisle> getProductAisles() {
        return productAisles;
    }

    public List<ProductShelf> getProductShelves() {
        return productShelves;
    }

    public List<ProductWall> getProductWalls() {
        return productWalls;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<ServiceCounter> getServiceCounters() {
        return serviceCounters;
    }

    public List<Stall> getStalls() {
        return stalls;
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

    public List<Wall> getWalls() {
        return walls;
    }

    public List<CashierCounterField> getCashierCounterFields() {
        return cashierCounterFields;
    }

    public List<GroceryGateField> getGroceryGateFields() {
        return groceryGateFields;
    }

    public List<SecurityField> getSecurityFields() {
        return securityFields;
    }

    public List<ServiceCounterField> getServiceCounterFields() {
        return serviceCounterFields;
    }

    public List<StallField> getStallFields() {
        return stallFields;
    }

    public List<BathroomField> getBathroomFields() {
        return bathroomFields;
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

    public int getMAX_CURRENT_ALONE() {
        return MAX_CURRENT_ALONE;
    }

    public void setMAX_CURRENT_ALONE(int MAX_CURRENT_ALONE) {
        this.MAX_CURRENT_ALONE = MAX_CURRENT_ALONE;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == GroceryGate.class) {
            return this.getGroceryGates();
        }
        else if (amenityClass == CartRepo.class) {
            return this.getCartRepos();
        }
        else if (amenityClass == CashierCounter.class) {
            return this.getCashierCounters();
        }
        else if (amenityClass == FreshProducts.class) {
            return this.getFreshProducts();
        }
        else if (amenityClass == FrozenProducts.class) {
            return this.getFrozenProducts();
        }
        else if (amenityClass == FrozenWall.class) {
            return this.getFrozenWalls();
        }
        else if (amenityClass == MeatSection.class) {
            return this.getMeatSections();
        }
        else if (amenityClass == ProductAisle.class) {
            return this.getProductAisles();
        }
        else if (amenityClass == ProductShelf.class) {
            return this.getProductShelves();
        }
        else if (amenityClass == ProductWall.class) {
            return this.getProductWalls();
        }
        else if (amenityClass == Security.class) {
            return this.getSecurities();
        }
        else if (amenityClass == ServiceCounter.class) {
            return this.getServiceCounters();
        }
        else if (amenityClass == Stall.class) {
            return this.getStalls();
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

    public void createInitialAgentDemographics(int MAX_FAMILY, int MAX_ALONE){
        GroceryAgent guard1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, GroceryAgent.Persona.GUARD_ENTRANCE, null, null, false, true);
        this.getAgents().add(guard1);
        GroceryAgent guard2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, GroceryAgent.Persona.GUARD_EXIT, null, null, false, true);
        this.getAgents().add(guard2);

        GroceryAgent cashier1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier1);
        GroceryAgent cashier2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier2);
        GroceryAgent cashier3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier3);
        GroceryAgent cashier4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier4);
        GroceryAgent cashier5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier5);
        GroceryAgent cashier6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier6);
        GroceryAgent cashier7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier7);
        GroceryAgent cashier8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, GroceryAgent.Persona.CASHIER, null, null, false, true);
        this.getAgents().add(cashier8);

        GroceryAgent bagger1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger1);
        GroceryAgent bagger2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger2);
        GroceryAgent bagger3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger3);
        GroceryAgent bagger4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger4);
        GroceryAgent bagger5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger5);
        GroceryAgent bagger6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger6);
        GroceryAgent bagger7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger7);
        GroceryAgent bagger8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, GroceryAgent.Persona.BAGGER, null, null, false, true);
        this.getAgents().add(bagger8);

        GroceryAgent service1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, GroceryAgent.Persona.CUSTOMER_SERVICE, null, null, false, true);
        this.getAgents().add(service1);
        GroceryAgent service2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, GroceryAgent.Persona.CUSTOMER_SERVICE, null, null, false, true);
        this.getAgents().add(service2);
        GroceryAgent service3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, GroceryAgent.Persona.CUSTOMER_SERVICE, null, null, false, true);
        this.getAgents().add(service3);

        GroceryAgent food1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, GroceryAgent.Persona.STAFF_FOOD, null, null, false, true);
        this.getAgents().add(food1);
        GroceryAgent food2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, GroceryAgent.Persona.STAFF_FOOD, null, null, false, true);
        this.getAgents().add(food2);
        GroceryAgent food3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, GroceryAgent.Persona.STAFF_FOOD, null, null, false, true);
        this.getAgents().add(food3);
        GroceryAgent food4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, GroceryAgent.Persona.STAFF_FOOD, null, null, false, true);
        this.getAgents().add(food4);
        GroceryAgent food5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, GroceryAgent.Persona.STAFF_FOOD, null, null, false, true);
        this.getAgents().add(food5);

        GroceryAgent butcher1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BUTCHER, GroceryAgent.Persona.BUTCHER, null, null, false, true);
        this.getAgents().add(butcher1);
        GroceryAgent butcher2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BUTCHER, GroceryAgent.Persona.BUTCHER, null, null, false, true);
        this.getAgents().add(butcher2);

        GroceryAgent aisle1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle1);
        GroceryAgent aisle2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle2);
        GroceryAgent aisle3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle3);
        GroceryAgent aisle4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle4);
        GroceryAgent aisle5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle5);
        GroceryAgent aisle6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle6);
        GroceryAgent aisle7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle7);
        GroceryAgent aisle8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle8);
        GroceryAgent aisle9 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle9);
        GroceryAgent aisle10 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, GroceryAgent.Persona.STAFF_AISLE, null, null, false, true);
        this.getAgents().add(aisle10);

        int ctr = 0;

        while (ctr < MAX_FAMILY){
            int familyType = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3);
            if (familyType == 0){
                GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                GroceryAgent.Gender gender2;
                if (gender1 == GroceryAgent.Gender.MALE) {
                    gender2 = GroceryAgent.Gender.FEMALE;
                }
                else {
                    gender2 = GroceryAgent.Gender.MALE;
                }
                GroceryAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                GroceryAgent.Gender gender4 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                GroceryAgent persona1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, true, false);
                this.getAgents().add(persona1);
                GroceryAgent persona2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender2, GroceryAgent.AgeGroup.FROM_25_TO_54, false, false);
                this.getAgents().add(persona2);
                GroceryAgent agent3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender3, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent3);
                GroceryAgent agent4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender4, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent4);

            }
            else if (familyType == 1){
                GroceryAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                GroceryAgent persona1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Gender.FEMALE, GroceryAgent.AgeGroup.FROM_25_TO_54, true, false);
                this.getAgents().add(persona1);
                GroceryAgent persona2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Gender.FEMALE, GroceryAgent.AgeGroup.FROM_25_TO_54, false, false);
                this.getAgents().add(persona2);
                GroceryAgent agent3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, gender3, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent3);
            }
            else{
                GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                GroceryAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                GroceryAgent persona1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, true, false);
                this.getAgents().add(persona1);
                GroceryAgent persona2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER, gender2, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(persona2);
            }
            ctr++;
        }

        ctr = 0;
        while (ctr < MAX_ALONE){
            boolean isSttp = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

            if (isSttp) {
                GroceryAgent agent = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.STTP_ALONE_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, false, false);
                this.getAgents().add(agent);
            }
            else {
                GroceryAgent agent = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, false, false);
                this.getAgents().add(agent);
            }
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
                    GroceryAgent agent1 = agents.get(i), agent2 = agents.get(j);
                    int IOS;
                    if (agent1.getPersonaActionGroup() == GroceryAgent.PersonaActionGroup.FAMILY){
                        if (agent1.getPersona().getID() > agent2.getPersona().getID()){
                            IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                        }
                        else if (agent1.getPersona().getID() == agent2.getPersona().getID()){
                            if (agent1.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER){
                                if (agent1.isLeader() && agent2.getId() - agent1.getId() > 0 && agent2.getId() - agent1.getId() <= 3
                                        || agent2.isLeader() && agent1.getId() - agent2.getId() > 0 && agent1.getId() - agent2.getId() <= 3){
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID()).size()));
                                }
                                else{
                                    IOS = IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(IOSScales.get(agent1.getPersona().getID()).get(agent2.getPersona().getID() + 1).size()));
                                }
                            }
                            else if (agent1.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER){
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
        for (int i = 0; i < GroceryAgent.Persona.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> personaIOS = new CopyOnWriteArrayList<>();
            for (int j = 0; j < GroceryAgent.Persona.values().length; j++){
                GroceryAgent.Persona persona1 = GroceryAgent.Persona.values()[i];
                GroceryAgent.Persona persona2 = GroceryAgent.Persona.values()[j];
                if (persona1 == GroceryAgent.Persona.GUARD_ENTRANCE){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.GUARD_EXIT){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.STAFF_AISLE){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.BUTCHER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.CASHIER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.BAGGER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.CUSTOMER_SERVICE){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.STAFF_FOOD){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.STTP_ALONE_CUSTOMER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(3, 4, 5)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case COMPLETE_FAMILY_CUSTOMER -> {
                            personaIOS.add(new CopyOnWriteArrayList<>(List.of(5, 6, 7)));
                            personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case HELP_FAMILY_CUSTOMER -> {
                            personaIOS.add(new CopyOnWriteArrayList<>(List.of(5, 6, 7)));
                            personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                        case DUO_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                    }
                }
                else if (persona1 == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER){
                    switch (persona2){
                        case GUARD_ENTRANCE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case GUARD_EXIT -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_AISLE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BUTCHER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CASHIER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case BAGGER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case CUSTOMER_SERVICE -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2)));
                        case STAFF_FOOD -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case STTP_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case MODERATE_ALONE_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case COMPLETE_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case HELP_FAMILY_CUSTOMER -> personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        case DUO_FAMILY_CUSTOMER -> {
                            personaIOS.add(new CopyOnWriteArrayList<>(List.of(5, 6, 7)));
                            personaIOS.add(new CopyOnWriteArrayList<>(List.of(1, 2, 3)));
                        }
                    }
                }
            }
            defaultIOS.add(personaIOS);
        }
    }

    public static void configureDefaultInteractionTypeChances(){
        defaultInteractionTypeChances = new CopyOnWriteArrayList<>();
        for (int i = 0; i < GroceryAgent.PersonaActionGroup.values().length; i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> interactionChances = new CopyOnWriteArrayList<>();
            for (int j = 0; j < GroceryAction.Name.values().length; j++){
                GroceryAgent.PersonaActionGroup personaGroup = GroceryAgent.PersonaActionGroup.values()[i];
                GroceryAction.Name action = GroceryAction.Name.values()[j];
                switch (personaGroup){
                    case GUARD_ENTRANCE -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }

                    case GUARD_EXIT -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case STAFF_AISLE -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(20, 40, 40)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case BUTCHER -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case CASHIER -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case BAGGER -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case CUSTOMER_SERVICE -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case STAFF_FOOD -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(10, 0, 90)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                        }
                    }
                    case ALONE -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 10)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                        }
                    }
                    case FAMILY -> {
                        switch(action){
                            case GREET_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GOING_TO_SECURITY_QUEUE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GO_THROUGH_SCANNER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_AISLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_PRODUCT_WALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FROZEN -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FRESH -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_MEAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_ASK_STAFF -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(80, 0, 20)));
                            case GO_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_FOOD_STALL -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GET_CART -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECK_PRODUCTS -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(25, 25, 50)));
                            case FOLLOW_LEADER_SHOP -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(60, 0, 40)));
                            case QUEUE_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CASHIER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_BAGGER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WAIT_FOR_CUSTOMER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_SERVICE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case QUEUE_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUY_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case TALK_TO_STAFF_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FOLLOW_LEADER_EAT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case FIND_SEAT_FOOD_COURT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case EATING_FOOD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_RECEIPT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CHECKOUT_GROCERIES_GUARD -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case LEAVE_BUILDING -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BUTCHER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case CASHIER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case BAGGER_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case SERVICE_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GREET_PERSON  -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GUARD_CHECK_GROCERIES -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_STATION -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_FOOD_SERVE_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ORGANIZE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case STAFF_AISLE_ANSWER_CUSTOMER -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case GO_TO_BATHROOM -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case RELIEVE_IN_CUBICLE -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(0, 0, 0)));
                            case WASH_IN_SINK -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(50, 0, 50)));
                            case GO_TO_WAIT_AREA -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
                            case WAIT_FOR_VACANT -> interactionChances.add(new CopyOnWriteArrayList<>(List.of(90, 0, 10)));
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

    public static class GroceryFactory extends BaseObject.ObjectFactory {
        public static Grocery create(int rows, int columns) {
            return new Grocery(rows, columns);
        }
    }

}