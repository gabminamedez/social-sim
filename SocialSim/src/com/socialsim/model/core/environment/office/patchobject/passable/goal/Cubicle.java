package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.CubicleGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Cubicle extends Goal {

    public static final Cubicle.CubicleFactory cubicleFactory;
    private final CubicleGraphic cubicleGraphic;

    static {
        cubicleFactory = new Cubicle.CubicleFactory();
    }

    protected Cubicle(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.cubicleGraphic = new CubicleGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "Cubicle" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.cubicleGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.cubicleGraphic.getGraphicLocation();
    }

    public static class CubicleBlock extends Amenity.AmenityBlock {
        public static Cubicle.CubicleBlock.CubicleBlockFactory cubicleBlockFactory;

        static {
            cubicleBlockFactory = new Cubicle.CubicleBlock.CubicleBlockFactory();
        }

        private CubicleBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class CubicleBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Cubicle.CubicleBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Cubicle.CubicleBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class CubicleFactory extends GoalFactory {
        public static Cubicle create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new Cubicle(amenityBlocks, enabled, facing);
        }
    }

}