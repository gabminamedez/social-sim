package com.socialsim.model.core.environment.patch.patchobject.passable.gate;

import com.socialsim.model.core.environment.patch.patchobject.Drawable;
import com.socialsim.model.core.environment.patch.patchobject.passable.NonObstacle;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public abstract class Gate extends NonObstacle implements Drawable {

    protected Gate(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);
    }

    public Agent spawnAgent() {
        for (AmenityBlock attractor : this.getAttractors()) { // Check if all attractors in this amenity have no agents
            if (!attractor.getPatch().getAgents().isEmpty()) {
                return null;
            }
        }

        AmenityBlock attractor = this.getAttractors().get(0); // Get a random attractor

        if (attractor.getPatch().getAgents().isEmpty()) { // If that random attractor is free from agents, generate one
            return Agent.agentFactory.create(Agent.Type.STUDENT, Agent.Gender.MALE, 21, attractor.getPatch()); // TODO: Change parameters here
        }
        else {
            return null;
        }
    }

    public void despawnAgent(Agent agent) {
        agent.getAgentMovement().despawnAgent();
    }

    public static abstract class GateFactory extends NonObstacleFactory {
    }

}