package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.KioskGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class Kiosk extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final Kiosk.KioskFactory kioskFactory;
    private final KioskGraphic kioskGraphic;

    static {
        kioskFactory = new Kiosk.KioskFactory();
    }

    protected Kiosk(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.kioskGraphic = new KioskGraphic(this);
    }

    @Override
    public String toString() {
        return "Kiosk" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.kioskGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.kioskGraphic.getGraphicLocation();
    }

    public static class KioskBlock extends AmenityBlock {
        public static Kiosk.KioskBlock.KioskBlockFactory kioskBlockFactory;

        static {
            kioskBlockFactory = new Kiosk.KioskBlock.KioskBlockFactory();
        }

        private KioskBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class KioskBlockFactory extends AmenityBlockFactory {
            @Override
            public Kiosk.KioskBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Kiosk.KioskBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class KioskFactory extends Goal.GoalFactory {
        public static Kiosk create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Kiosk(amenityBlocks, enabled, waitingTime);
        }
    }

}