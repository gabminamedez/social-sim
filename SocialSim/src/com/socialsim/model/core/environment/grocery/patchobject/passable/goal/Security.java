package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.SecurityGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class Security extends QueueableGoal {

    public static final long serialVersionUID = -5458621245735102190L;
    public static final double standardDeviation = 3.0;
    public static final SecurityFactory securityFactory;
    private final SecurityGraphic securityGraphic;

    static {
        securityFactory = new SecurityFactory();
    }

    protected Security(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.securityGraphic = new SecurityGraphic(this);
    }

    @Override
    public String toString() {
        return "Security" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.securityGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.securityGraphic.getGraphicLocation();
    }

    public static class SecurityBlock extends Amenity.AmenityBlock {
        public static SecurityBlockFactory securityBlockFactory;

        static {
            securityBlockFactory = new SecurityBlockFactory();
        }

        private SecurityBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class SecurityBlockFactory extends AmenityBlockFactory {
            @Override
            public SecurityBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new SecurityBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class SecurityFactory extends Goal.GoalFactory {
        public static Security create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Security(amenityBlocks, enabled, waitingTime);
        }
    }

}