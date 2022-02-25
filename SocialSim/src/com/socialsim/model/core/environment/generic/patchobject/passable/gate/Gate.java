package com.socialsim.model.core.environment.generic.patchobject.passable.gate;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import java.util.ArrayList;
import java.util.List;

public abstract class Gate extends NonObstacle implements Drawable {

    private final List<GateBlock> spawners;

    protected Gate(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        if (this.getAmenityBlocks() != null) {
            this.spawners = new ArrayList<>();

            for (AmenityBlock amenityBlock : this.getAmenityBlocks()) {
                GateBlock gateBlock = ((GateBlock) amenityBlock);

                if (gateBlock.isSpawner()) {
                    this.spawners.add(gateBlock);
                }
            }
        }
        else {
            this.spawners = null;
        }
    }

    public List<GateBlock> getSpawners() {
        return spawners;
    }

    public static abstract class GateBlock extends AmenityBlock {
        private final boolean spawner;

        public GateBlock(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);

            this.spawner = spawner;
        }

        public boolean isSpawner() {
            return spawner;
        }

        public static abstract class GateBlockFactory extends AmenityBlockFactory {
            public abstract GateBlock create(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic);
        }
    }

    public static abstract class GateFactory extends NonObstacleFactory {
    }

}