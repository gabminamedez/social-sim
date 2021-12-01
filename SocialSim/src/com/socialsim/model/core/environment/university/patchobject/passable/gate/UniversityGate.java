package com.socialsim.model.core.environment.university.patchobject.passable.gate;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.UniversityGateGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.university.University;

import java.util.List;

public class UniversityGate extends Gate {

    private double chancePerSecond; // Denotes the chance of generating an agent per second
    private UniversityGateMode universityGateMode; // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    public static final UniversityGateFactory universityGateFactory;
    private final UniversityGateGraphic universityGateGraphic;

    static {
        universityGateFactory = new UniversityGateFactory();
    }

    protected UniversityGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerSecond, UniversityGateMode universityGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerSecond = chancePerSecond;
        this.universityGateMode = universityGateMode;
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

    public static class UniversityGateBlock extends Amenity.AmenityBlock {
        public static UniversityGateBlockFactory universityGateBlockFactory;

        static {
            universityGateBlockFactory = new UniversityGateBlockFactory();
        }

        private UniversityGateBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class UniversityGateBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public UniversityGateBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new UniversityGateBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class UniversityGateFactory extends GateFactory {
        public UniversityGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerSecond, UniversityGateMode stationGateMode) {
            return new UniversityGate(amenityBlocks, enabled, chancePerSecond, stationGateMode);
        }
    }

}