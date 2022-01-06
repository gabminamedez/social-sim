package com.socialsim.model.core.environment.grocery;

import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchfield.CashierCounterField;
import com.socialsim.model.core.environment.grocery.patchfield.SecurityField;
import com.socialsim.model.core.environment.grocery.patchfield.ServiceCounterField;
import com.socialsim.model.core.environment.grocery.patchfield.StallField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Grocery extends Environment {

    private final CopyOnWriteArrayList<GroceryAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;

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
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
        this.serviceCounterFields = Collections.synchronizedList(new ArrayList<>());
        this.stallFields = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<GroceryAgent> getAgents() {
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

    public static class GroceryFactory extends BaseObject.ObjectFactory {
        public static Grocery create(int rows, int columns) {
            return new Grocery(rows, columns);
        }
    }

}