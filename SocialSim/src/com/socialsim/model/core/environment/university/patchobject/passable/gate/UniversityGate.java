package com.socialsim.model.core.environment.university.patchobject.passable.gate;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.UniversityGateGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.university.University;

import java.util.HashSet;
import java.util.List;

public class UniversityGate extends Gate {

    private double chancePerSecond; // Denotes the chance of generating an agent per second
    private UniversityGateMode universityGateMode; // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private int agentBacklogCount; // Denotes the number of agents who are supposed to enter the station gate, but cannot
    public static final UniversityGateFactory universityGateFactory;
    private final UniversityGateGraphic universityGateGraphic;

    static {
        universityGateFactory = new UniversityGateFactory();
    }

    protected UniversityGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerSecond, UniversityGateMode universityGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerSecond = chancePerSecond;
        this.universityGateMode = universityGateMode;
        this.agentBacklogCount = 0;
        this.universityGateGraphic = new UniversityGateGraphic(this);
    }

    public double getChancePerSecond() {
        return chancePerSecond;
    }

    public void setChancePerSecond(double chancePerSecond) {
        this.chancePerSecond = chancePerSecond;
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
    public AmenityGraphic getGraphicObject() {
        return this.universityGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.universityGateGraphic.getGraphicLocation();
    }

    @Override
    public Agent spawnAgent() { // Spawn an agent in this position
        University university = this.getAmenityBlocks().get(0).getPatch().getUniversity();
        GateBlock spawner = this.getSpawners().get(0);

        if (university != null) {
            return Agent.agentFactory.create(Agent.Type.STUDENT, Agent.Gender.MALE, 21, spawner.getPatch());
        }
        else {
            return null;
        }
    }

    public boolean isGateFree() {
        HashSet<UniversityPatch> patchesToCheck = new HashSet<>();
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

        for (UniversityPatch patchToCheck : patchesToCheck) {
            if (!patchToCheck.getAgents().isEmpty()) {
                patchesFree = false;
                break;
            }
        }

        return patchesFree;
    }

    public Agent spawnAgentFromBacklogs(boolean forceEntry) { // Spawn an agent from the backlogs
        University university = this.getAmenityBlocks().get(0).getPatch().getUniversity();

        if (university != null) {
            List<Agent> universityGateQueue = university.getAgentBacklogs();

            if (!universityGateQueue.isEmpty()) { // If the backlog queue isn't empty, check if this gate is free from agents
                if (forceEntry || this.isGateFree()) { // If this gate is free from other agents, get one from the backlog queue
                    Agent agent = universityGateQueue.remove(0);
                    UniversityPatch spawnPatch = this.getSpawners().get(0).getPatch();

                    return agent;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
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

        private UniversityGateBlock(UniversityPatch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, true, hasGraphic);
        }

        public static class UniversityGateBlockFactory extends Gate.GateBlock.GateBlockFactory {
            @Override
            public UniversityGateBlock create(UniversityPatch patch, boolean attractor, boolean hasGraphic) {
                return new UniversityGateBlock(patch, attractor, hasGraphic);
            }

            @Override
            public GateBlock create(UniversityPatch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
                return null;
            }
        }
    }

    public static class UniversityGateFactory extends GateFactory {
        public static UniversityGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerSecond, UniversityGateMode stationGateMode) {
            return new UniversityGate(amenityBlocks, enabled, chancePerSecond, stationGateMode);
        }
    }

}