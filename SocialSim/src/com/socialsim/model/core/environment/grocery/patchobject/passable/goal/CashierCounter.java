package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.CashierCounterGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class CashierCounter extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final CashierCounter.CashierCounterFactory cashierCounterFactory;
    private final CashierCounterGraphic cashierCounterGraphic;

    static {
        cashierCounterFactory = new CashierCounter.CashierCounterFactory();
    }

    protected CashierCounter(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.cashierCounterGraphic = new CashierCounterGraphic(this);
    }

    @Override
    public String toString() {
        return "CashierCounter" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.cashierCounterGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.cashierCounterGraphic.getGraphicLocation();
    }

    public static class CashierCounterBlock extends Amenity.AmenityBlock {
        public static CashierCounter.CashierCounterBlock.CashierCounterBlockFactory cashierCounterBlockFactory;

        static {
            cashierCounterBlockFactory = new CashierCounter.CashierCounterBlock.CashierCounterBlockFactory();
        }

        private CashierCounterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class CashierCounterBlockFactory extends AmenityBlockFactory {
            @Override
            public CashierCounter.CashierCounterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new CashierCounter.CashierCounterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class CashierCounterFactory extends Goal.GoalFactory {
        public static CashierCounter create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new CashierCounter(amenityBlocks, enabled, waitingTime);
        }
    }

}