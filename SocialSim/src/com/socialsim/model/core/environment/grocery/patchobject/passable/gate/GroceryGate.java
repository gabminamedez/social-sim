package com.socialsim.model.core.environment.grocery.patchobject.passable.gate;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.GroceryGateGraphic;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.grocery.Grocery;

import java.util.HashSet;
import java.util.List;

public class GroceryGate extends Gate {

    private double chancePerTick; // Denotes the chance of generating an agent per second
    private GroceryGateMode groceryGateMode;
    private int agentBacklogCount; // Denotes the number of agents who are supposed to enter the gate, but cannot
    public static final GroceryGateFactory groceryGateFactory;
    private final GroceryGateGraphic groceryGateGraphic;

    static {
        groceryGateFactory = new GroceryGateFactory();
    }

    protected GroceryGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, GroceryGateMode groceryGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerTick = chancePerTick;
        this.groceryGateMode = groceryGateMode;
        this.agentBacklogCount = 0;
        this.groceryGateGraphic = new GroceryGateGraphic(this);
    }

    public double getChancePerTick() {
        return chancePerTick;
    }

    public void setChancePerTick(double chancePerTick) {
        this.chancePerTick = chancePerTick;
    }

    public GroceryGateMode getGroceryGateMode() {
        return groceryGateMode;
    }

    public int getAgentBacklogCount() {
        return agentBacklogCount;
    }

    public void incrementBacklogs() {
        this.agentBacklogCount++;
    }

    public void resetBacklogs() {
        this.agentBacklogCount = 0;
    }

    public void setGroceryGateMode(GroceryGateMode groceryGateMode) {
        this.groceryGateMode = groceryGateMode;
    }

    @Override
    public String toString() {
        return "Grocery entrance/exit" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.groceryGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.groceryGateGraphic.getGraphicLocation();
    }

    @Override
    public GroceryAgent spawnAgent() { // Spawn an agent in this position
        Grocery grocery = (Grocery) this.getAmenityBlocks().get(0).getPatch().getEnvironment();
        GateBlock spawner = this.getSpawners().get(0);

        if (grocery != null) {
            // return GroceryAgent.GroceryAgentFactory.create(UniversityAgent.Type.STUDENT, UniversityAgent.Gender.MALE, 21, spawner.getPatch());
            return null; // For the meantime
        }
        else {
            return null;
        }
    }

    public boolean isGateFree() {
        HashSet<Patch> patchesToCheck = new HashSet<>();
        boolean patchesFree = true;

        // Check if all attractors and spawners in this amenity have no agents
        for (AmenityBlock attractor : this.getAttractors()) {
            patchesToCheck.add(attractor.getPatch());
            patchesToCheck.addAll(attractor.getPatch().getNeighbors());
        }

        for (GateBlock spawner : this.getSpawners()) {
            patchesToCheck.add(spawner.getPatch());
            patchesToCheck.addAll(spawner.getPatch().getNeighbors());
        }

        for (Patch patchToCheck : patchesToCheck) {
            if (!patchToCheck.getAgents().isEmpty()) {
                patchesFree = false;
                break;
            }
        }

        return patchesFree;
    }

    public enum GroceryGateMode {
        ENTRANCE("Entrance"), EXIT("Exit"), ENTRANCE_AND_EXIT("Entrance and Exit");

        private final String name;

        GroceryGateMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class GroceryGateBlock extends Gate.GateBlock {
        public static GroceryGateBlockFactory groceryGateBlockFactory;

        static {
            groceryGateBlockFactory = new GroceryGateBlockFactory();
        }

        private GroceryGateBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, true, hasGraphic);
        }

        public static class GroceryGateBlockFactory extends Gate.GateBlock.GateBlockFactory {
            @Override
            public GroceryGateBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new GroceryGateBlock(patch, attractor, hasGraphic);
            }

            @Override
            public GateBlock create(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
                return null;
            }
        }
    }

    public static class GroceryGateFactory extends GateFactory {
        public static GroceryGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, GroceryGateMode groceryGateMode) {
            return new GroceryGate(amenityBlocks, enabled, chancePerTick, groceryGateMode);
        }
    }

}