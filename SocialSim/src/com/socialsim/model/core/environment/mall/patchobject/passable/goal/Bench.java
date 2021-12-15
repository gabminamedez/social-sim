package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.BenchGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Bench extends Goal {

    public static final BenchFactory benchFactory;
    private final BenchGraphic benchGraphic;

    static {
        benchFactory = new BenchFactory();
    }

    protected Bench(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.benchGraphic = new BenchGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "Bench" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.benchGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.benchGraphic.getGraphicLocation();
    }

    public static class BenchBlock extends Amenity.AmenityBlock {
        public static BenchBlock.BenchBlockFactory benchBlockFactory;

        static {
            benchBlockFactory = new BenchBlock.BenchBlockFactory();
        }

        private BenchBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class BenchBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public BenchBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new BenchBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class BenchFactory extends GoalFactory {
        public static com.socialsim.model.core.environment.mall.patchobject.passable.goal.Bench create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new com.socialsim.model.core.environment.mall.patchobject.passable.goal.Bench(amenityBlocks, enabled, facing);
        }
    }

}