package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.StoreAisleGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class StoreAisle extends Goal {

    public static final StoreAisle.StoreAisleFactory storeAisleFactory;
    private final StoreAisleGraphic storeAisleGraphic;

    static {
        storeAisleFactory = new StoreAisle.StoreAisleFactory();
    }

    protected StoreAisle(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.storeAisleGraphic = new StoreAisleGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "StoreAisle" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.storeAisleGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.storeAisleGraphic.getGraphicLocation();
    }

    public static class StoreAisleBlock extends Amenity.AmenityBlock {
        public static StoreAisle.StoreAisleBlock.StoreAisleBlockFactory storeAisleBlockFactory;
        static {
            storeAisleBlockFactory = new StoreAisle.StoreAisleBlock.StoreAisleBlockFactory();
        }

        private StoreAisleBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class StoreAisleBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public StoreAisle.StoreAisleBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new StoreAisle.StoreAisleBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class StoreAisleFactory extends GoalFactory {
        public static StoreAisle create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new StoreAisle(amenityBlocks, enabled, facing);
        }
    }

}