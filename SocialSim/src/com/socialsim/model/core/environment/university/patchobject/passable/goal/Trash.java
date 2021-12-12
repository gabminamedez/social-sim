package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.TrashGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Trash extends Goal {

    public static final Trash.TrashFactory trashFactory;
    private final TrashGraphic trashGraphic;

    static {
        trashFactory = new Trash.TrashFactory();
    }

    protected Trash(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.trashGraphic = new TrashGraphic(this);
    }


    @Override
    public String toString() {
        return "Trash" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.trashGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.trashGraphic.getGraphicLocation();
    }

    public static class TrashBlock extends Amenity.AmenityBlock {
        public static Trash.TrashBlock.TrashBlockFactory trashBlockFactory;

        static {
            trashBlockFactory = new Trash.TrashBlock.TrashBlockFactory();
        }

        private TrashBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class TrashBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Trash.TrashBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Trash.TrashBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class TrashFactory extends GoalFactory {
        public static Trash create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Trash(amenityBlocks, enabled);
        }
    }

}