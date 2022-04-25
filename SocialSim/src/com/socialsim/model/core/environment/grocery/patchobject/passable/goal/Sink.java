package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.SinkGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Sink extends Goal {

    public static final SinkFactory sinkFactory;
    private final SinkGraphic sinkGraphic;

    static {
        sinkFactory = new SinkFactory();
    }

    protected Sink(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.sinkGraphic = new SinkGraphic(this);
    }


    @Override
    public String toString() {
        return "Sink" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.sinkGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.sinkGraphic.getGraphicLocation();
    }

    public static class SinkBlock extends AmenityBlock {
        public static SinkBlockFactory sinkBlockFactory;

        static {
            sinkBlockFactory = new SinkBlockFactory();
        }

        private SinkBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class SinkBlockFactory extends AmenityBlockFactory {
            @Override
            public SinkBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new SinkBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class SinkFactory extends GoalFactory {
        public static Sink create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Sink(amenityBlocks, enabled);
        }
    }

}