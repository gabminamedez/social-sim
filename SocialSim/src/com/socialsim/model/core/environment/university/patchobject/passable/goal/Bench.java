package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.BenchGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Bench extends Goal {

    public static final Bench.BenchFactory benchFactory;
    private final BenchGraphic benchGraphic;

    static {
        benchFactory = new Bench.BenchFactory();
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
    public UniversityAmenityGraphic getGraphicObject() {
        return this.benchGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.benchGraphic.getGraphicLocation();
    }

    public static class BenchBlock extends Amenity.AmenityBlock {
        public static Bench.BenchBlock.BenchBlockFactory benchBlockFactory;

        static {
            benchBlockFactory = new Bench.BenchBlock.BenchBlockFactory();
        }

        private BenchBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class BenchBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Bench.BenchBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Bench.BenchBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class BenchFactory extends GoalFactory {
        public static Bench create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new Bench(amenityBlocks, enabled, facing);
        }
    }

}