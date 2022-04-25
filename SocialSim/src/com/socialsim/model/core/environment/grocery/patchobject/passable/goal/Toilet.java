package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.ToiletGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Toilet extends Goal {

    public static final ToiletFactory toiletFactory;
    private final ToiletGraphic toiletGraphic;

    static {
        toiletFactory = new ToiletFactory();
    }

    protected Toilet(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.toiletGraphic = new ToiletGraphic(this);
    }


    @Override
    public String toString() {
        return "Toilet" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.toiletGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.toiletGraphic.getGraphicLocation();
    }

    public static class ToiletBlock extends AmenityBlock {
        public static ToiletBlockFactory toiletBlockFactory;

        static {
            toiletBlockFactory = new ToiletBlockFactory();
        }

        private ToiletBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ToiletBlockFactory extends AmenityBlockFactory {
            @Override
            public ToiletBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ToiletBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ToiletFactory extends GoalFactory {
        public static Toilet create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Toilet(amenityBlocks, enabled);
        }
    }

}