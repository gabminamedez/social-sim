package com.socialsim.model.core.environment.office.patchobject.passable.gate;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.OfficeGateGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import java.util.List;

public class OfficeGate extends Gate {

    private double chancePerTick;
    private OfficeGate.OfficeGateMode officeGateMode;
    public static final OfficeGate.OfficeGateFactory officeGateFactory;
    private final OfficeGateGraphic officeGateGraphic;

    static {
        officeGateFactory = new OfficeGate.OfficeGateFactory();
    }

    protected OfficeGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, OfficeGate.OfficeGateMode officeGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerTick = chancePerTick;
        this.officeGateMode = officeGateMode;
        this.officeGateGraphic = new OfficeGateGraphic(this);
    }

    public double getChancePerTick() {
        return chancePerTick;
    }

    public void setChancePerTick(double chancePerTick) {
        this.chancePerTick = chancePerTick;
    }

    public OfficeGate.OfficeGateMode getOfficeGateMode() {
        return officeGateMode;
    }

    public void setOfficeGateMode(OfficeGate.OfficeGateMode officeGateMode) {
        this.officeGateMode = officeGateMode;
    }

    @Override
    public String toString() {
        return "Office entrance/exit" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.officeGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.officeGateGraphic.getGraphicLocation();
    }

    public enum OfficeGateMode {
        ENTRANCE("Entrance"), EXIT("Exit"), ENTRANCE_AND_EXIT("Entrance and Exit");

        private final String name;

        OfficeGateMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class OfficeGateBlock extends Gate.GateBlock {
        public static OfficeGate.OfficeGateBlock.OfficeGateBlockFactory officeGateBlockFactory;

        static {
            officeGateBlockFactory = new OfficeGate.OfficeGateBlock.OfficeGateBlockFactory();
        }

        private OfficeGateBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, true, hasGraphic);
        }

        public static class OfficeGateBlockFactory extends Gate.GateBlock.GateBlockFactory {
            @Override
            public OfficeGate.OfficeGateBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new OfficeGate.OfficeGateBlock(patch, attractor, hasGraphic);
            }

            @Override
            public GateBlock create(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
                return null;
            }
        }
    }

    public static class OfficeGateFactory extends GateFactory {
        public static OfficeGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, OfficeGate.OfficeGateMode stationGateMode) {
            return new OfficeGate(amenityBlocks, enabled, chancePerTick, stationGateMode);
        }
    }

}