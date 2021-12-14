package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.OfficeDeskGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class OfficeDesk extends Goal {

    public static final OfficeDesk.OfficeDeskFactory officeDeskFactory;
    private final OfficeDeskGraphic officeDeskGraphic;

    static {
        officeDeskFactory = new OfficeDesk.OfficeDeskFactory();
    }

    protected OfficeDesk(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.officeDeskGraphic = new OfficeDeskGraphic(this);
    }


    @Override
    public String toString() {
        return "OfficeDesk" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.officeDeskGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.officeDeskGraphic.getGraphicLocation();
    }

    public static class OfficeDeskBlock extends Amenity.AmenityBlock {
        public static OfficeDesk.OfficeDeskBlock.OfficeDeskBlockFactory officeDeskBlockFactory;

        static {
            officeDeskBlockFactory = new OfficeDesk.OfficeDeskBlock.OfficeDeskBlockFactory();
        }

        private OfficeDeskBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class OfficeDeskBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public OfficeDesk.OfficeDeskBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new OfficeDesk.OfficeDeskBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class OfficeDeskFactory extends GoalFactory {
        public static OfficeDesk create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new OfficeDesk(amenityBlocks, enabled);
        }
    }

}