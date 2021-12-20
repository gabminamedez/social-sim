package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.StoreCounterGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class StoreCounter extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final StoreCounter.StoreCounterFactory storeCounterFactory;
    private final StoreCounterGraphic storeCounterGraphic;

    static {
        storeCounterFactory = new StoreCounter.StoreCounterFactory();
    }

    protected StoreCounter(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.storeCounterGraphic = new StoreCounterGraphic(this);
    }

    @Override
    public String toString() {
        return "StoreCounter" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.storeCounterGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.storeCounterGraphic.getGraphicLocation();
    }

    public static class StoreCounterBlock extends AmenityBlock {
        public static StoreCounter.StoreCounterBlock.StoreCounterBlockFactory storeCounterBlockFactory;

        static {
            storeCounterBlockFactory = new StoreCounter.StoreCounterBlock.StoreCounterBlockFactory();
        }

        private StoreCounterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class StoreCounterBlockFactory extends AmenityBlockFactory {
            @Override
            public StoreCounter.StoreCounterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new StoreCounter.StoreCounterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class StoreCounterFactory extends Goal.GoalFactory {
        public static StoreCounter create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new StoreCounter(amenityBlocks, enabled, waitingTime);
        }
    }

}