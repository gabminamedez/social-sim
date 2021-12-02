package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.BenchGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.List;

public class Bench extends Goal {

    public static final Bench.BenchFactory benchFactory;
    private final BenchGraphic benchGraphic;

    static {
        benchFactory = new Bench.BenchFactory();
    }

    protected Bench(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.benchGraphic = new BenchGraphic(this);
    }


    @Override
    public String toString() {
        return "Bench" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
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
        public Bench create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Bench(amenityBlocks, enabled);
        }
    }

}