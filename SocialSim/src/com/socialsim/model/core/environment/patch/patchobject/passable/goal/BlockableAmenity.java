package com.socialsim.model.core.environment.patch.patchobject.passable.goal;

import com.socialsim.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public abstract class BlockableAmenity extends QueueableGoal {

    private boolean blockEntry; // Denotes whether agents are able to pass through this amenity

    public BlockableAmenity(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, boolean blockEntry) {
        super(amenityBlocks, enabled, waitingTime);

        this.blockEntry = blockEntry;
    }

    public boolean blockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }

    public static boolean isBlockable(Amenity amenity) {
        return amenity instanceof BlockableAmenity;
    }

    public static BlockableAmenity asBlockable(Amenity amenity) {
        if (isBlockable(amenity)) {
            return (BlockableAmenity) amenity;
        }
        else {
            return null;
        }
    }

}