package com.socialsim.model.core.environment.generic.patchobject.passable;

import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import java.util.List;

public abstract class NonObstacle extends Amenity {

    protected boolean enabled;

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