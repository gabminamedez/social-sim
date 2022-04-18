package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.RestoCounterGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class RestoCounter extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final RestoCounter.RestoCounterFactory restoCounterFactory;
    private final RestoCounterGraphic restoCounterGraphic;

    static {
        restoCounterFactory = new RestoCounter.RestoCounterFactory();
    }

    protected RestoCounter(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.restoCounterGraphic = new RestoCounterGraphic(this);
    }

    @Override
    public String toString() {
        return "RestoCounter" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.restoCounterGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.restoCounterGraphic.getGraphicLocation();
    }

    public static class RestoCounterBlock extends AmenityBlock {
        public static RestoCounter.RestoCounterBlock.RestoCounterBlockFactory restoCounterBlockFactory;

        static {
            restoCounterBlockFactory = new RestoCounter.RestoCounterBlock.RestoCounterBlockFactory();
        }

        private RestoCounterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class RestoCounterBlockFactory extends AmenityBlockFactory {
            @Override
            public RestoCounter.RestoCounterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new RestoCounter.RestoCounterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class RestoCounterFactory extends Goal.GoalFactory {
        public static RestoCounter create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new RestoCounter(amenityBlocks, enabled, waitingTime);
        }
    }

}