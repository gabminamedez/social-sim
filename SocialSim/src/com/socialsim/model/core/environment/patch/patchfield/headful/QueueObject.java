package com.socialsim.model.core.environment.patch.patchfield.headful;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.patch.patchfield.AbstractPatchField;
import com.socialsim.model.core.environment.patch.patchfield.headful.QueueingPatchField;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QueueObject extends AbstractPatchField { // Any amenity that is queueable must contain a hashmap of floor fields

    private final Map<QueueingPatchField.PatchFieldState, QueueingPatchField> patchFields = new HashMap<>(); // Given a passenger state, a floor field may be retrieved from that goal

    private final LinkedList<Agent> agentsQueueing = new LinkedList<>();

    private Agent lastAgentQueueing; // Denotes the agent at the back of the queue
    private Agent agentServiced; // Denotes the agent currently being serviced by this queueable

    public Map<QueueingPatchField.PatchFieldState, QueueingPatchField> getFloorFields() {
        return patchFields;
    }

    public LinkedList<Agent> getGuardsQueueing() {
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

}