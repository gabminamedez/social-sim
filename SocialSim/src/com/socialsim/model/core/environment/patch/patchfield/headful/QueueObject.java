package com.socialsim.model.core.environment.patch.patchfield.headful;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchfield.AbstractPatchField;
import com.socialsim.model.core.environment.patch.patchobject.passable.Queueable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QueueObject extends AbstractPatchField { // Any amenity that is queueable must contain a hashmap of floor fields

    private final Queueable parent; // Denotes the parent queueable of this queue object
    private final UniversityPatch patch; // Denotes the patch where this queue object is
    private final Map<QueueingPatchField.PatchFieldState, QueueingPatchField> patchFields = new HashMap<>(); // Any amenity that is queueable must contain a hashmap of floor fields
    private final LinkedList<Agent> agentsQueueing = new LinkedList<>(); // Denotes the list of agents who are queueing for this goal
    private Agent lastAgentQueueing; // Denotes the agent at the back of the queue
    private Agent agentServiced; // Denotes the agent currently being serviced by this queueable

    public QueueObject(Queueable parent, UniversityPatch patch) {
        this.parent = parent;
        this.patch = patch;
    }

    public Queueable getParent() {
        return parent;
    }

    public UniversityPatch getPatch() {
        return patch;
    }

    public Map<QueueingPatchField.PatchFieldState, QueueingPatchField> getPatchFields() {
        return patchFields;
    }

    public LinkedList<Agent> getAgentsQueueing() {
        return agentsQueueing;
    }

    public Agent getLastAgentQueueing() {
        return lastAgentQueueing;
    }

    public void setLastAgentQueueing(Agent lastAgentQueueing) {
        this.lastAgentQueueing = lastAgentQueueing;
    }

    public Agent getAgentServiced() {
        return agentServiced;
    }

    public void setAgentServiced(Agent agentServiced) {
        this.agentServiced = agentServiced;
    }

    public boolean isFree() {
        return this.getAgentServiced() == null;
    }

}