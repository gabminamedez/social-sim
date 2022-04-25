package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.WhiteboardGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Whiteboard extends Goal {

    public static final Whiteboard.WhiteboardFactory whiteboardFactory;
    private final WhiteboardGraphic whiteboardGraphic;

    static {
        whiteboardFactory = new Whiteboard.WhiteboardFactory();
    }

    protected Whiteboard(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.whiteboardGraphic = new WhiteboardGraphic(this);
    }


    @Override
    public String toString() {
        return "Whiteboard" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.whiteboardGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.whiteboardGraphic.getGraphicLocation();
    }

    public static class WhiteboardBlock extends Amenity.AmenityBlock {
        public static Whiteboard.WhiteboardBlock.WhiteboardBlockFactory whiteboardBlockFactory;

        static {
            whiteboardBlockFactory = new Whiteboard.WhiteboardBlock.WhiteboardBlockFactory();
        }

        private WhiteboardBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class WhiteboardBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Whiteboard.WhiteboardBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Whiteboard.WhiteboardBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class WhiteboardFactory extends GoalFactory {
        public static Whiteboard create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Whiteboard(amenityBlocks, enabled);
        }
    }

}