package com.socialsim.model.core.environment.mall.patchobject.passable.gate;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.MallGateGraphic;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.mall.Mall;

import java.util.HashSet;
import java.util.List;

public class MallGate extends Gate {

    private double chancePerSecond; // Denotes the chance of generating an agent per second
    private MallGate.MallGateMode mallGateMode; // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private int agentBacklogCount; // Denotes the number of agents who are supposed to enter the gate, but cannot
    public static final MallGate.MallGateFactory mallGateFactory;
    private final MallGateGraphic mallGateGraphic;

    static {
        mallGateFactory = new MallGate.MallGateFactory();
    }

    protected MallGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerSecond, MallGate.MallGateMode mallGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerSecond = chancePerSecond;
        this.mallGateMode = mallGateMode;
        this.agentBacklogCount = 0;
        this.mallGateGraphic = new MallGateGraphic(this);
    }

    public double getChancePerSecond() {
        return chancePerSecond;
    }

    public void setChancePerSecond(double chancePerSecond) {
        this.chancePerSecond = chancePerSecond;
    }

    public MallGate.MallGateMode getMallGateMode() {
        return mallGateMode;
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

    public void setMallGateMode(MallGate.MallGateMode mallGateMode) {
        this.mallGateMode = mallGateMode;
    }

    @Override
    public String toString() {
        return "Mall entrance/exit" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.mallGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.mallGateGraphic.getGraphicLocation();
    }

    @Override
    public MallAgent spawnAgent() { // Spawn an agent in this position
        Mall mall = (Mall) this.getAmenityBlocks().get(0).getPatch().getEnvironment();
        GateBlock spawner = this.getSpawners().get(0);

        if (mall != null) {
            // return MallAgent.MallAgentFactory.create(MallAgent.Type.STUDENT, MallAgent.Gender.MALE, 21, spawner.getPatch());
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

    public enum MallGateMode {
        ENTRANCE("Entrance"), EXIT("Exit"), ENTRANCE_AND_EXIT("Entrance and Exit");

        private final String name;

        MallGateMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class MallGateBlock extends Gate.GateBlock {
        public static MallGate.MallGateBlock.MallGateBlockFactory mallGateBlockFactory;

        static {
            mallGateBlockFactory = new MallGate.MallGateBlock.MallGateBlockFactory();
        }

        private MallGateBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, true, hasGraphic);
        }

        public static class MallGateBlockFactory extends Gate.GateBlock.GateBlockFactory {
            @Override
            public MallGate.MallGateBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new MallGate.MallGateBlock(patch, attractor, hasGraphic);
            }

            @Override
            public GateBlock create(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
                return null;
            }
        }
    }

    public static class MallGateFactory extends GateFactory {
        public static MallGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerSecond, MallGate.MallGateMode stationGateMode) {
            return new MallGate(amenityBlocks, enabled, chancePerSecond, stationGateMode);
        }
    }

}