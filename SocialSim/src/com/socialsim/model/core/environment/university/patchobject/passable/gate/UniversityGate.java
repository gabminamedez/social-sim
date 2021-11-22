package com.socialsim.model.core.environment.university.patchobject.passable.gate;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.UniversityGateGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.gate.Gate;

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
        return "Station entrance/exit" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.universityGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.universityGateGraphic.getGraphicLocation();
    }

    public enum UniversityGateMode {
        ENTRANCE("Entrance"), EXIT("Exit"), ENTRANCE_AND_EXIT("Entrance and exit");

        private final String name;

        StationGateMode(String name) {
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