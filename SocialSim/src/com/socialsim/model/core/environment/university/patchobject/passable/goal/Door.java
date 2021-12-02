package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.DoorGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.List;

public class Door extends Goal {

    public static final Door.DoorFactory doorFactory;
    private final DoorGraphic doorGraphic;

    static {
        doorFactory = new Door.DoorFactory();
    }

    protected Door(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.doorGraphic = new DoorGraphic(this);
    }


    @Override
    public String toString() {
        return "Door" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.doorGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.doorGraphic.getGraphicLocation();
    }

    public static class DoorBlock extends Amenity.AmenityBlock {
        public static Door.DoorBlock.DoorBlockFactory doorBlockFactory;

        static {
            doorBlockFactory = new Door.DoorBlock.DoorBlockFactory();
        }

        private DoorBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class DoorBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Door.DoorBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Door.DoorBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class DoorFactory extends GoalFactory {
        public Door create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Door(amenityBlocks, enabled);
        }
    }

}