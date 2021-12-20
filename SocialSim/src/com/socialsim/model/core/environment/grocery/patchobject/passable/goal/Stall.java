package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.StallGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class Stall extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final Stall.StallFactory stallFactory;
    private final StallGraphic stallGraphic;

    static {
        stallFactory = new Stall.StallFactory();
    }

    protected Stall(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.stallGraphic = new StallGraphic(this);
    }

    @Override
    public String toString() {
        return "Stall" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.stallGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.stallGraphic.getGraphicLocation();
    }

    public static class StallBlock extends Amenity.AmenityBlock {
        public static Stall.StallBlock.StallBlockFactory stallBlockFactory;

        static {
            stallBlockFactory = new Stall.StallBlock.StallBlockFactory();
        }

        private StallBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class StallBlockFactory extends AmenityBlockFactory {
            @Override
            public Stall.StallBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Stall.StallBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class StallFactory extends Goal.GoalFactory {
        public static Stall create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Stall(amenityBlocks, enabled, waitingTime);
        }
    }

}