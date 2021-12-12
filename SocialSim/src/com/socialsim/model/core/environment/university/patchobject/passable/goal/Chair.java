package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.ChairGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Chair extends Goal {

    public static final Chair.ChairFactory chairFactory;
    private final ChairGraphic chairGraphic;

    static {
        chairFactory = new Chair.ChairFactory();
    }

    protected Chair(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.chairGraphic = new ChairGraphic(this);
    }


    @Override
    public String toString() {
        return "Chair" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.chairGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.chairGraphic.getGraphicLocation();
    }

    public static class ChairBlock extends Amenity.AmenityBlock {
        public static Chair.ChairBlock.ChairBlockFactory chairBlockFactory;

        static {
            chairBlockFactory = new Chair.ChairBlock.ChairBlockFactory();
        }

        private ChairBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ChairBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Chair.ChairBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Chair.ChairBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ChairFactory extends GoalFactory {
        public static Chair create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Chair(amenityBlocks, enabled);
        }
    }

}