package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.PlantGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Plant extends Goal {

    public static final Plant.PlantFactory plantFactory;
    private final PlantGraphic plantGraphic;

    static {
        plantFactory = new Plant.PlantFactory();
    }

    protected Plant(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.plantGraphic = new PlantGraphic(this);
    }


    @Override
    public String toString() {
        return "Plant" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.plantGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.plantGraphic.getGraphicLocation();
    }

    public static class PlantBlock extends Amenity.AmenityBlock {
        public static Plant.PlantBlock.PlantBlockFactory plantBlockFactory;

        static {
            plantBlockFactory = new Plant.PlantBlock.PlantBlockFactory();
        }

        private PlantBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class PlantBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Plant.PlantBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Plant.PlantBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class PlantFactory extends GoalFactory {
        public static Plant create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Plant(amenityBlocks, enabled);
        }
    }

}