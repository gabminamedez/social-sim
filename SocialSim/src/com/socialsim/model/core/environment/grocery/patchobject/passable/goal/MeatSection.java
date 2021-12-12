package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.MeatSectionGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class MeatSection extends Goal {

    public static final MeatSection.MeatSectionFactory meatSectionFactory;
    private final MeatSectionGraphic meatSectionGraphic;

    static {
        meatSectionFactory = new MeatSection.MeatSectionFactory();
    }

    protected MeatSection(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.meatSectionGraphic = new MeatSectionGraphic(this);
    }


    @Override
    public String toString() {
        return "MeatSection" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.meatSectionGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.meatSectionGraphic.getGraphicLocation();
    }

    public static class MeatSectionBlock extends Amenity.AmenityBlock {
        public static MeatSection.MeatSectionBlock.MeatSectionBlockFactory meatSectionBlockFactory;

        static {
            meatSectionBlockFactory = new MeatSection.MeatSectionBlock.MeatSectionBlockFactory();
        }

        private MeatSectionBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class MeatSectionBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public MeatSection.MeatSectionBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new MeatSection.MeatSectionBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class MeatSectionFactory extends Goal.GoalFactory {
        public static MeatSection create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new MeatSection(amenityBlocks, enabled);
        }
    }

}