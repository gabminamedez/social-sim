package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.DigitalGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Digital extends Goal {

    public static final Digital.DigitalFactory digitalFactory;
    private final DigitalGraphic digitalGraphic;

    static {
        digitalFactory = new Digital.DigitalFactory();
    }

    protected Digital(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.digitalGraphic = new DigitalGraphic(this);
    }


    @Override
    public String toString() {
        return "Digital" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.digitalGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.digitalGraphic.getGraphicLocation();
    }

    public static class DigitalBlock extends Amenity.AmenityBlock {
        public static Digital.DigitalBlock.DigitalBlockFactory digitalBlockFactory;

        static {
            digitalBlockFactory = new Digital.DigitalBlock.DigitalBlockFactory();
        }

        private DigitalBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class DigitalBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Digital.DigitalBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Digital.DigitalBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class DigitalFactory extends GoalFactory {
        public static Digital create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Digital(amenityBlocks, enabled);
        }
    }

}