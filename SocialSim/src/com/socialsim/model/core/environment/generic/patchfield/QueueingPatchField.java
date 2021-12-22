package com.socialsim.model.core.environment.generic.patchfield;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.ArrayList;
import java.util.List;

public class QueueingPatchField extends BaseObject {

    private final List<Patch> associatedPatches;
    private final QueueableGoal target;
    private List<Agent> queueingAgents;
    private Agent currentAgent;

    protected QueueingPatchField(List<Patch> patches, QueueableGoal target) {
        super();

        this.associatedPatches = new ArrayList<>();
        associatedPatches.addAll(patches);
        this.target = target;
        this.queueingAgents = new ArrayList<>();
        this.currentAgent = null;
    }

    public List<Patch> getAssociatedPatches() {
        return associatedPatches;
    }

    public QueueableGoal getQueueableGoal() {
        return target;
    }

    public List<Agent> getQueueingAgents() {
        return queueingAgents;
    }

    public Patch getNextQueuePatch(Patch patch) {
        if (associatedPatches.contains(patch)){
            if (associatedPatches.indexOf(patch) == 0){
                return null;
            }
            else{
                return associatedPatches.get(associatedPatches.indexOf(patch) - 1);
            }
        }
        else{
            return null;
        }
    }

    public Patch getLastQueuePatch() {
        return this.associatedPatches.get(associatedPatches.size() - 1);
    }

    public boolean inLastQueuePatch(Patch patch) {
        if (associatedPatches.indexOf(patch) == associatedPatches.size() - 1){
            return true;
        }
        return false;
    }

    public Agent getCurrentAgent() {
        return currentAgent;
    }

    public void setCurrentAgent(Agent currentAgent) {
        this.currentAgent = currentAgent;
    }

    public static abstract class QueueingPatchFieldFactory extends ObjectFactory {
    }

}