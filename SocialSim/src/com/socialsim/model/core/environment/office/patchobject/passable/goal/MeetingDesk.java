package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.MeetingDeskGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class MeetingDesk extends Goal {

    public static final MeetingDesk.MeetingDeskFactory meetingDeskFactory;
    private final MeetingDeskGraphic meetingDeskGraphic;

    static {
        meetingDeskFactory = new MeetingDesk.MeetingDeskFactory();
    }

    protected MeetingDesk(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.meetingDeskGraphic = new MeetingDeskGraphic(this);
    }


    @Override
    public String toString() {
        return "MeetingDesk" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.meetingDeskGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.meetingDeskGraphic.getGraphicLocation();
    }

    public static class MeetingDeskBlock extends Amenity.AmenityBlock {
        public static MeetingDesk.MeetingDeskBlock.MeetingDeskBlockFactory meetingDeskBlockFactory;

        static {
            meetingDeskBlockFactory = new MeetingDesk.MeetingDeskBlock.MeetingDeskBlockFactory();
        }

        private MeetingDeskBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class MeetingDeskBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public MeetingDesk.MeetingDeskBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new MeetingDesk.MeetingDeskBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class MeetingDeskFactory extends GoalFactory {
        public static MeetingDesk create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new MeetingDesk(amenityBlocks, enabled);
        }
    }

}