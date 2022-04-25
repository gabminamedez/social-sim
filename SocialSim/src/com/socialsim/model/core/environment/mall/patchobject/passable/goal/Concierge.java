package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.ConciergeGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Concierge extends Goal {

    public static final Concierge.ConciergeFactory conciergeFactory;
    private final ConciergeGraphic conciergeGraphic;

    static {
        conciergeFactory = new Concierge.ConciergeFactory();
    }

    protected Concierge(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.conciergeGraphic = new ConciergeGraphic(this);
    }


    @Override
    public String toString() {
        return "Concierge" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.conciergeGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.conciergeGraphic.getGraphicLocation();
    }

    public static class ConciergeBlock extends Amenity.AmenityBlock {
        public static Concierge.ConciergeBlock.ConciergeBlockFactory conciergeBlockFactory;

        static {
            conciergeBlockFactory = new Concierge.ConciergeBlock.ConciergeBlockFactory();
        }

        private ConciergeBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ConciergeBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Concierge.ConciergeBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Concierge.ConciergeBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ConciergeFactory extends GoalFactory {
        public static Concierge create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Concierge(amenityBlocks, enabled);
        }
    }

}