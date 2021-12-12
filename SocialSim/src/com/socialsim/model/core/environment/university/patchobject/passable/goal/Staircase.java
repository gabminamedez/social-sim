package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.StaircaseGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Staircase extends Goal {

    public static final Staircase.StaircaseFactory staircaseFactory;
    private final StaircaseGraphic staircaseGraphic;

    static {
        staircaseFactory = new Staircase.StaircaseFactory();
    }

    protected Staircase(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.staircaseGraphic = new StaircaseGraphic(this);
    }


    @Override
    public String toString() {
        return "Staircase" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.staircaseGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.staircaseGraphic.getGraphicLocation();
    }

    public static class StaircaseBlock extends Amenity.AmenityBlock {
        public static Staircase.StaircaseBlock.StaircaseBlockFactory staircaseBlockFactory;

        static {
            staircaseBlockFactory = new Staircase.StaircaseBlock.StaircaseBlockFactory();
        }

        private StaircaseBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class StaircaseBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Staircase.StaircaseBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Staircase.StaircaseBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class StaircaseFactory extends GoalFactory {
        public static Staircase create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Staircase(amenityBlocks, enabled);
        }
    }

}