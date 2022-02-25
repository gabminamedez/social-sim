package com.socialsim.model.core.environment.university.patchobject.passable.gate;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.UniversityGateGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import java.util.List;

public class UniversityGate extends Gate {

    private double chancePerTick;
    private UniversityGateMode universityGateMode;
    private int agentBacklogCount;
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