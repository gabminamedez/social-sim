package com.socialsim.model.core.environment.grocery;

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

    private final CopyOnWriteArrayList<GroceryAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> IOS;

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

    private final List<Wall> walls;
    private final List<CashierCounterField> cashierCounterFields;
    private final List<GroceryGateField> groceryGateFields;
    private final List<SecurityField> securityFields;
    private final List<ServiceCounterField> serviceCounterFields;
    private final List<StallField> stallFields;

    public static final Grocery.GroceryFactory groceryFactory;

    static {
        groceryFactory = new Grocery.GroceryFactory();
    }

    public Grocery(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();
        this.IOS = new CopyOnWriteArrayList<>();

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

        this.walls = Collections.synchronizedList(new ArrayList<>());
        this.cashierCounterFields = Collections.synchronizedList(new ArrayList<>());
        this.groceryGateFields = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
        this.serviceCounterFields = Collections.synchronizedList(new ArrayList<>());
        this.stallFields = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<GroceryAgent> getAgents() {
        return agents;
    }

    public CopyOnWriteArrayList<GroceryAgent> getUnspawnedFamilyAgents() {
        CopyOnWriteArrayList<GroceryAgent> unspawned = new CopyOnWriteArrayList<>();
        ArrayList<GroceryAgent.Persona> family = new ArrayList<>(Arrays.asList(GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER));
        for (GroceryAgent agent: getAgents()){
            if (agent.getAgentMovement() == null && family.contains(agent.getPersona()) && agent.isLeader())
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
        else {
            return null;
        }
    }

    public void createInitialAgentDemographics(int MAX_FAMILY, int MAX_ALONE){
        //Guards
        GroceryAgent guard1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, null, null, null, false, true);
        this.getAgents().add(guard1);
        GroceryAgent guard2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, null, null, null, false, true);
        this.getAgents().add(guard2);

        //Cashiers
        GroceryAgent cashier1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier1);
        GroceryAgent cashier2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier2);
        GroceryAgent cashier3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier3);
        GroceryAgent cashier4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier4);
        GroceryAgent cashier5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier5);
        GroceryAgent cashier6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier6);
        GroceryAgent cashier7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier7);
        GroceryAgent cashier8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, false, true);
        this.getAgents().add(cashier8);

        //Baggers
        GroceryAgent bagger1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger1);
        GroceryAgent bagger2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger2);
        GroceryAgent bagger3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger3);
        GroceryAgent bagger4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger4);
        GroceryAgent bagger5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger5);
        GroceryAgent bagger6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger6);
        GroceryAgent bagger7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger7);
        GroceryAgent bagger8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, false, true);
        this.getAgents().add(bagger8);

        //Service
        GroceryAgent service1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, null, null, null, false, true);
        this.getAgents().add(service1);
        GroceryAgent service2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, null, null, null, false, true);
        this.getAgents().add(service2);
        GroceryAgent service3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, null, null, null, false, true);
        this.getAgents().add(service3);

        //Staff Food
        GroceryAgent food1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food1);
        GroceryAgent food2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food2);
        GroceryAgent food3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food3);
        GroceryAgent food4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food4);
        GroceryAgent food5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food5);
        GroceryAgent food6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food6);
        GroceryAgent food7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food7);
        GroceryAgent food8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, false, true);
        this.getAgents().add(food8);

        //Butcher
        GroceryAgent butcher1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BUTCHER, null, null, null, false, true);
        this.getAgents().add(butcher1);
        GroceryAgent butcher2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BUTCHER, null, null, null, false, true);
        this.getAgents().add(butcher2);

        //Staff Aisle
        GroceryAgent aisle1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle1);
        GroceryAgent aisle2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle2);
        GroceryAgent aisle3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle3);
        GroceryAgent aisle4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle4);
        GroceryAgent aisle5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle5);
        GroceryAgent aisle6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle6);
        GroceryAgent aisle7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle7);
        GroceryAgent aisle8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle8);
        GroceryAgent aisle9 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle9);
        GroceryAgent aisle10 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, false, true);
        this.getAgents().add(aisle10);

        //Family Customers

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

                GroceryAgent agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, true, false);
                this.getAgents().add(agent1);
                GroceryAgent agent2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender2, GroceryAgent.AgeGroup.FROM_25_TO_54, false, false);
                this.getAgents().add(agent2);
                GroceryAgent agent3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender3, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent3);
                GroceryAgent agent4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender4, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent4);

            }
            else if (familyType == 1){
                GroceryAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                GroceryAgent agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Gender.FEMALE, GroceryAgent.AgeGroup.FROM_25_TO_54, true, false);
                this.getAgents().add(agent1);
                GroceryAgent agent2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Gender.FEMALE, GroceryAgent.AgeGroup.FROM_25_TO_54, false, false);
                this.getAgents().add(agent2);
                GroceryAgent agent3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, gender3, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent3);
            }
            else{
                GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                GroceryAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                GroceryAgent agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, true, false);
                this.getAgents().add(agent1);
                GroceryAgent agent2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER, gender2, GroceryAgent.AgeGroup.FROM_15_TO_24, false, false);
                this.getAgents().add(agent2);
            }
            ctr++;
        }

        //Alone Customers

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

        for (int i = 0; i < this.getAgents().size(); i++){
            GroceryAgent.Persona agent1 = agents.get(i).getPersona();
            ArrayList<Integer> IOSScales = new ArrayList<>();
            for (int j = 0 ; j < this.getAgents().size(); j++){
                if (i == j){
                    IOSScales.add(0);
                }
                else {
                    GroceryAgent.Persona agent2 = agents.get(j).getPersona();
                    if (agent1 == GroceryAgent.Persona.GUARD_ENTRANCE){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.GUARD_EXIT){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.STAFF_AISLE){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.BUTCHER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.CASHIER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.BAGGER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.CUSTOMER_SERVICE){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.STAFF_FOOD){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.STTP_ALONE_CUSTOMER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                        }
                    }
                    else if (agent1 == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER){
                        //1. Get IOS Scale of each agent then put in an array
                        //2. Place in convert function and replace IOS
                        switch (agent2){
                            case GUARD_ENTRANCE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1));
                            case GUARD_EXIT -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2);
                            case STAFF_AISLE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BUTCHER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CASHIER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case BAGGER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 1);
                            case CUSTOMER_SERVICE -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STAFF_FOOD -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case STTP_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case MODERATE_ALONE_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 1);
                            case COMPLETE_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case HELP_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
                            case DUO_FAMILY_CUSTOMER -> IOSScales.add(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1);
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

    public static class GroceryFactory extends BaseObject.ObjectFactory {
        public static Grocery create(int rows, int columns) {
            return new Grocery(rows, columns);
        }
    }

}