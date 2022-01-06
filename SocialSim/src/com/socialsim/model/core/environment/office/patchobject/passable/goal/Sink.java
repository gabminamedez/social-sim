package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.SinkGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Sink extends Goal {

    public static final Sink.SinkFactory sinkFactory;
    private final SinkGraphic sinkGraphic;

    static {
        sinkFactory = new Sink.SinkFactory();
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
    public OfficeAmenityGraphic getGraphicObject() {
        return this.sinkGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.sinkGraphic.getGraphicLocation();
    }

    public static class SinkBlock extends Amenity.AmenityBlock {
        public static Sink.SinkBlock.SinkBlockFactory sinkBlockFactory;

        static {
            sinkBlockFactory = new Sink.SinkBlock.SinkBlockFactory();
        }

        private SinkBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class SinkBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Sink.SinkBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Sink.SinkBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class SinkFactory extends GoalFactory {
        public static Sink create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Sink(amenityBlocks, enabled);
        }
    }

}