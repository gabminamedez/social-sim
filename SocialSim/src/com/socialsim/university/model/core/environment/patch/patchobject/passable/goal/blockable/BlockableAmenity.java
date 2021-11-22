package com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.blockable;

import com.socialsim.university.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.List;

public abstract class BlockableAmenity extends Goal {

    private boolean blockEntry; // Denotes whether agents are able to pass through this amenity

    public BlockableAmenity(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, QueueObject queueObject, boolean blockEntry) {
        super(amenityBlocks, enabled, waitingTime, queueObject);

        this.blockEntry = blockEntry;
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }

}