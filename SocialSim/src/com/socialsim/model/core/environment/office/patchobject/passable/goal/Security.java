package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.SecurityGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class Security extends QueueableGoal {

    public static final long serialVersionUID = -5458621245735102190L;
    public static final Security.SecurityFactory securityFactory;
    private final SecurityGraphic securityGraphic;

    static {
        securityFactory = new Security.SecurityFactory();
    }

    protected Security(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.securityGraphic = new SecurityGraphic(this);
    }

    @Override
    public String toString() {
        return "Security" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.securityGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.securityGraphic.getGraphicLocation();
    }

    public static class SecurityBlock extends AmenityBlock {
        public static Security.SecurityBlock.SecurityBlockFactory securityBlockFactory;

        static {
            securityBlockFactory = new Security.SecurityBlock.SecurityBlockFactory();
        }

        private SecurityBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class SecurityBlockFactory extends AmenityBlockFactory {
            @Override
            public Security.SecurityBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Security.SecurityBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class SecurityFactory extends Goal.GoalFactory {
        public static Security create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Security(amenityBlocks, enabled, waitingTime);
        }
    }

}