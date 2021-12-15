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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Mall extends Environment {

    private final CopyOnWriteArrayList<MallAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;

    private final List<MallGate> mallGates;
    private final List<Bench> benches;
    private final List<Digital> digitals;
    private final List<Kiosk> kiosks;
    private final List<Plant> plants;
    private final List<Security> securities;
    private final List<StoreCounter> storeCounters;
    private final List<Table> tables;
    private final List<Trash> trashes;

    private final List<Bathroom> bathrooms;
    private final List<Dining> dinings;
    private final List<Restaurant> restaurants;
    private final List<Showcase> showcases;
    private final List<Store> stores;
    private final List<Wall> walls;

    List<MallAgent> agentBacklogs;

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

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.dinings = Collections.synchronizedList(new ArrayList<>());
        this.restaurants = Collections.synchronizedList(new ArrayList<>());
        this.showcases = Collections.synchronizedList(new ArrayList<>());
        this.stores = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());

        this.agentBacklogs = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<MallAgent> getAgents() {
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

    public List<MallAgent> getAgentBacklogs() {
        return agentBacklogs;
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
        else {
            return null;
        }
    }

    public static class MallFactory extends BaseObject.ObjectFactory {
        public static Mall create(int rows, int columns) {
            return new Mall(rows, columns);
        }
    }

}