package com.socialsim.university.model.core.environment.patch.patchobject.miscellaneous;

import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.university.model.core.environment.patch.patchobject.Drawable;

import java.util.List;

public abstract class Obstacle extends Amenity implements Drawable {

    protected Obstacle(List<AmenityBlock> amenityBlocks) {
        super(amenityBlocks);
    }

    public static abstract class ObstacleFactory extends AmenityFactory {
    }

}