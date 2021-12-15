package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.CollabDeskGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class CollabDesk extends Goal {

    public static final CollabDesk.CollabDeskFactory collabDeskFactory;
    private final CollabDeskGraphic collabDeskGraphic;

    static {
        collabDeskFactory = new CollabDesk.CollabDeskFactory();
    }

    protected CollabDesk(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.collabDeskGraphic = new CollabDeskGraphic(this);
    }


    @Override
    public String toString() {
        return "CollabDesk" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.collabDeskGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.collabDeskGraphic.getGraphicLocation();
    }

    public static class CollabDeskBlock extends Amenity.AmenityBlock {
        public static CollabDesk.CollabDeskBlock.CollabDeskBlockFactory collabDeskBlockFactory;

        static {
            collabDeskBlockFactory = new CollabDesk.CollabDeskBlock.CollabDeskBlockFactory();
        }

        private CollabDeskBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class CollabDeskBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public CollabDesk.CollabDeskBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new CollabDesk.CollabDeskBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class CollabDeskFactory extends GoalFactory {
        public static CollabDesk create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new CollabDesk(amenityBlocks, enabled);
        }
    }

}