package com.socialsim.university.model.core.environment.patch.patchobject.passable;

import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public abstract class NonObstacle extends Amenity {

    protected boolean enabled; // Denotes whether this passable patch object is enabled or not (agents cannot pass through it)

    public NonObstacle(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks);

        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static abstract class NonObstacleFactory extends AmenityFactory {
    }

}