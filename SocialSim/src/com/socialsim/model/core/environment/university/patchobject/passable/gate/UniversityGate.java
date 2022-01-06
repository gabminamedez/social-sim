package com.socialsim.model.core.environment.university.patchobject.passable.gate;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.UniversityGateGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.university.University;

import java.util.HashSet;
import java.util.List;

public class UniversityGate extends Gate {

    private double chancePerTick; // Denotes the chance of generating an agent per second
    private UniversityGateMode universityGateMode; // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private int agentBacklogCount; // Denotes the number of agents who are supposed to enter the gate, but cannot
    public static final UniversityGateFactory universityGateFactory;
    private final UniversityGateGraphic universityGateGraphic;

    static {
        universityGateFactory = new UniversityGateFactory();
    }

    protected UniversityGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, UniversityGateMode universityGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerTick = chancePerTick;
        this.universityGateMode = universityGateMode;
        this.agentBacklogCount = 0;
        this.universityGateGraphic = new UniversityGateGraphic(this);
    }

    public double getChancePerTick() {
        return chancePerTick;
    }

    public void setChancePerTick(double chancePerTick) {
        this.chancePerTick = chancePerTick;
    }

    public UniversityGateMode getUniversityGateMode() {
        return universityGateMode;
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

    public void setUniversityGateMode(UniversityGateMode universityGateMode) {
        this.universityGateMode = universityGateMode;
    }

    @Override
    public String toString() {
        return "University entrance/exit" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.universityGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.universityGateGraphic.getGraphicLocation();
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

    @Override
    public Agent spawnAgent() {
        return null;
    }

    public enum UniversityGateMode {
        ENTRANCE("Entrance"), EXIT("Exit"), ENTRANCE_AND_EXIT("Entrance and Exit");

        private final String name;

        UniversityGateMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class UniversityGateBlock extends Gate.GateBlock {
        public static UniversityGateBlockFactory universityGateBlockFactory;

        static {
            universityGateBlockFactory = new UniversityGateBlockFactory();
        }

        private UniversityGateBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, true, hasGraphic);
        }

        public static class UniversityGateBlockFactory extends Gate.GateBlock.GateBlockFactory {
            @Override
            public UniversityGateBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new UniversityGateBlock(patch, attractor, hasGraphic);
            }

            @Override
            public GateBlock create(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
                return null;
            }
        }
    }

    public static class UniversityGateFactory extends GateFactory {
        public static UniversityGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, UniversityGateMode stationGateMode) {
            return new UniversityGate(amenityBlocks, enabled, chancePerTick, stationGateMode);
        }
    }

}