package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.FrozenWallGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class FrozenWall extends Goal {

    public static final FrozenWall.FrozenWallFactory frozenWallFactory;
    private final FrozenWallGraphic frozenWallGraphic;

    static {
        frozenWallFactory = new FrozenWall.FrozenWallFactory();
    }

    protected FrozenWall(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.frozenWallGraphic = new FrozenWallGraphic(this);
    }


    @Override
    public String toString() {
        return "FrozenWall" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.frozenWallGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.frozenWallGraphic.getGraphicLocation();
    }

    public static class FrozenWallBlock extends Amenity.AmenityBlock {
        public static FrozenWall.FrozenWallBlock.FrozenWallBlockFactory frozenWallBlockFactory;

        static {
            frozenWallBlockFactory = new FrozenWall.FrozenWallBlock.FrozenWallBlockFactory();
        }

        private FrozenWallBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class FrozenWallBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public FrozenWall.FrozenWallBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new FrozenWall.FrozenWallBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class FrozenWallFactory extends Goal.GoalFactory {
        public static FrozenWall create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new FrozenWall(amenityBlocks, enabled);
        }
    }

}