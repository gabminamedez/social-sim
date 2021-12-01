package com.socialsim.model.core.environment.patch.patchobject.passable.goal;

import com.socialsim.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.Drawable;
import com.socialsim.model.core.environment.patch.patchobject.passable.NonObstacle;

import java.util.List;

public abstract class Goal extends NonObstacle implements Drawable {

    protected Goal(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);
    }

    public static boolean isGoal(Amenity amenity) {
        return amenity instanceof Goal;
    }

    public static Goal toGoal(Amenity amenity) {
        if (isGoal(amenity)) {
            return (Goal) amenity;
        }
        else {
            return null;
        }
    }

    public static abstract class GoalFactory extends AmenityFactory {
    }

}