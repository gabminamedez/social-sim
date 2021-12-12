package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.university.graphics.amenity.graphic.EatTableGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class EatTable extends Goal {

    public static final EatTable.EatTableFactory eatTableFactory;
    private final EatTableGraphic eatTableGraphic;

    static {
        eatTableFactory = new EatTable.EatTableFactory();
    }

    protected EatTable(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.eatTableGraphic = new EatTableGraphic(this);
    }


    @Override
    public String toString() {
        return "EatTable" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.eatTableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.eatTableGraphic.getGraphicLocation();
    }

    public static class EatTableBlock extends Amenity.AmenityBlock {
        public static EatTable.EatTableBlock.EatTableBlockFactory eatTableBlockFactory;

        static {
            eatTableBlockFactory = new EatTable.EatTableBlock.EatTableBlockFactory();
        }

        private EatTableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class EatTableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public EatTable.EatTableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new EatTable.EatTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class EatTableFactory extends Goal.GoalFactory {
        public static EatTable create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new EatTable(amenityBlocks, enabled);
        }
    }

}