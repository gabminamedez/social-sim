package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.ServiceCounterGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class ServiceCounter extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final ServiceCounter.ServiceCounterFactory serviceCounterFactory;
    private final ServiceCounterGraphic serviceCounterGraphic;

    static {
        serviceCounterFactory = new ServiceCounter.ServiceCounterFactory();
    }

    protected ServiceCounter(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.serviceCounterGraphic = new ServiceCounterGraphic(this);
    }

    @Override
    public String toString() {
        return "ServiceCounter" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.serviceCounterGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.serviceCounterGraphic.getGraphicLocation();
    }

    public static class ServiceCounterBlock extends Amenity.AmenityBlock {
        public static ServiceCounter.ServiceCounterBlock.ServiceCounterBlockFactory serviceCounterBlockFactory;

        static {
            serviceCounterBlockFactory = new ServiceCounter.ServiceCounterBlock.ServiceCounterBlockFactory();
        }

        private ServiceCounterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ServiceCounterBlockFactory extends AmenityBlockFactory {
            @Override
            public ServiceCounter.ServiceCounterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ServiceCounter.ServiceCounterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ServiceCounterFactory extends Goal.GoalFactory {
        public static ServiceCounter create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new ServiceCounter(amenityBlocks, enabled, waitingTime);
        }
    }

}