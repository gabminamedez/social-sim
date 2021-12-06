package com.socialsim.model.core.environment.patch.patchobject.passable.gate;

import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Drawable;
import com.socialsim.model.core.environment.patch.patchobject.passable.NonObstacle;
import com.socialsim.model.core.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public abstract class Gate extends NonObstacle implements Drawable {

    private final List<GateBlock> spawners; // Denotes the spawners of this gate

    protected Gate(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        if (this.getAmenityBlocks() != null) { // Only proceed when this amenity has blocks
            this.spawners = new ArrayList<>();

            for (AmenityBlock amenityBlock : this.getAmenityBlocks()) { // Set all this amenity's spawners to the pertinent list
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

    public abstract Agent spawnAgent(); // Spawn an agent in this position

    public void despawnPassenger(Agent agent) { // Despawn an agent in this position
        // agent.getAgentMovement().despawn();
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