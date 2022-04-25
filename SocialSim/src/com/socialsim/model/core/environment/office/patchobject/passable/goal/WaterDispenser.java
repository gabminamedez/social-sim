package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.WaterDispenserGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class WaterDispenser extends Goal {

    public static final WaterDispenser.WaterDispenserFactory waterDispenserFactory;
    private final WaterDispenserGraphic waterDispenserGraphic;

    static {
        waterDispenserFactory = new WaterDispenser.WaterDispenserFactory();
    }

    protected WaterDispenser(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.waterDispenserGraphic = new WaterDispenserGraphic(this);
    }


    @Override
    public String toString() {
        return "WaterDispenser" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.waterDispenserGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.waterDispenserGraphic.getGraphicLocation();
    }

    public static class WaterDispenserBlock extends Amenity.AmenityBlock {
        public static WaterDispenser.WaterDispenserBlock.WaterDispenserBlockFactory waterDispenserBlockFactory;

        static {
            waterDispenserBlockFactory = new WaterDispenser.WaterDispenserBlock.WaterDispenserBlockFactory();
        }

        private WaterDispenserBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class WaterDispenserBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public WaterDispenser.WaterDispenserBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new WaterDispenser.WaterDispenserBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class WaterDispenserFactory extends GoalFactory {
        public static WaterDispenser create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new WaterDispenser(amenityBlocks, enabled);
        }
    }

}