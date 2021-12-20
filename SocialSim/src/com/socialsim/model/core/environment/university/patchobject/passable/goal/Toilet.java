package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.ToiletGraphic;
import com.socialsim.controller.university.graphics.amenity.graphic.ToiletGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Toilet extends Goal {

    public static final Toilet.ToiletFactory toiletFactory;
    private final ToiletGraphic toiletGraphic;

    static {
        toiletFactory = new Toilet.ToiletFactory();
    }

    protected Toilet(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.toiletGraphic = new ToiletGraphic(this);
    }


    @Override
    public String toString() {
        return "Toilet" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.toiletGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.toiletGraphic.getGraphicLocation();
    }

    public static class ToiletBlock extends Amenity.AmenityBlock {
        public static Toilet.ToiletBlock.ToiletBlockFactory toiletBlockFactory;

        static {
            toiletBlockFactory = new Toilet.ToiletBlock.ToiletBlockFactory();
        }

        private ToiletBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ToiletBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Toilet.ToiletBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Toilet.ToiletBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ToiletFactory extends GoalFactory {
        public static Toilet create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Toilet(amenityBlocks, enabled);
        }
    }

}