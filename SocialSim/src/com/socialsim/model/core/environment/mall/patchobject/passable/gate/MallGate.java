package com.socialsim.model.core.environment.mall.patchobject.passable.gate;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.MallGateGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import java.util.List;

public class MallGate extends Gate {

    private double chancePerTick;
    private MallGate.MallGateMode mallGateMode;
    private int agentBacklogCount;
    public static final MallGate.MallGateFactory mallGateFactory;
    private final MallGateGraphic mallGateGraphic;

    static {
        mallGateFactory = new MallGate.MallGateFactory();
    }

    protected MallGate(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, MallGate.MallGateMode mallGateMode) {
        super(amenityBlocks, enabled);

        this.chancePerTick = chancePerTick;
        this.mallGateMode = mallGateMode;
        this.agentBacklogCount = 0;
        this.mallGateGraphic = new MallGateGraphic(this);
    }

    public double getChancePerTick() {
        return chancePerTick;
    }

    public void setChancePerTick(double chancePerTick) {
        this.chancePerTick = chancePerTick;
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
        public static MallGate create(List<AmenityBlock> amenityBlocks, boolean enabled, double chancePerTick, MallGate.MallGateMode stationGateMode) {
            return new MallGate(amenityBlocks, enabled, chancePerTick, stationGateMode);
        }
    }

}