package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.FountainGraphic;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.ArrayList;
import java.util.List;

public class Fountain extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    private UniversityAgent agentActing; // Takes note of the agent currently acting in the fountain
    private final QueueObject queueObject;
    public static final FountainFactory fountainFactory;
    private final QueueingPatchField.PatchFieldState fountainPatchFieldState;
    private final FountainGraphic fountainGraphic;

    static {
        fountainFactory = new FountainFactory();
    }

    protected Fountain(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.queueObject = new QueueObject(this, this.getAttractors().get(0).getPatch());
        this.agentActing = null;
        // this.fountainPatchFieldState = new QueueingPatchField.PatchFieldState(UniversityAgentMovement.Disposition.BOARDING, UniversityAgentMovement.State.IN_QUEUE, this);
        QueueingPatchField queueingPatchField = QueueingPatchField.queueingPatchFieldFactory.create(this); // Add a blank patch field
        this.fountainPatchFieldState = null;
        this.getQueueObject().getPatchFields().put(this.fountainPatchFieldState, queueingPatchField); // Using the patch field state defined earlier, create the patch field
        this.getQueueObjectAmenityBlockMap().put(this.getQueueObject(), this.getAttractors().get(0)); // Define the relationships between the queue objects and the attractors
        this.fountainGraphic = new FountainGraphic(this);
    }

    public UniversityAgent getUniversityAgentActing() {
        return agentActing;
    }

    public void setUniversityAgentActing(UniversityAgent agentActing) {
        this.agentActing = agentActing;
    }

    public QueueingPatchField.PatchFieldState getTicketBoothPatchFieldState() {
        return fountainPatchFieldState;
    }

    @Override
    public boolean isFree(QueueObject queueObject) { // Check whether this queueable is free to service an agent
        return this.queueObject.isFree();
    }

    @Override
    public String toString() {
        return "Fountain" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates() {
        List<QueueingPatchField.PatchFieldState> patchFieldStates = new ArrayList<>();
        patchFieldStates.add(this.fountainPatchFieldState);

        return patchFieldStates;
    }

    @Override
    public QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState) {
        return queueObject.getPatchFields().get(patchFieldState);
    }

    @Override
    public boolean isPatchFieldsComplete() { // Denotes whether the patch field for this ticket booth is complete
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), this.fountainPatchFieldState);

        // The patch field of this queueable is complete when there are patch field values present with an apex patch that is equal to the number of attractors in this queueable target
        return queueingPatchField.getApices().size() == this.getAttractors().size() && !queueingPatchField.getAssociatedPatches().isEmpty();
    }

    @Override
    public void deletePatchField(QueueingPatchField.PatchFieldState patchFieldState) { // Clear all patch fields of the given patch field state in this ticket booth
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), patchFieldState);
        QueueingPatchField.clearPatchField(queueingPatchField, patchFieldState);
    }

    @Override
    public void deleteAllPatchFields() { // Sweep through each and every patch field and delete them
        List<QueueingPatchField.PatchFieldState> patchFieldStates = retrievePatchFieldStates();

        for (QueueingPatchField.PatchFieldState patchFieldState : patchFieldStates) {
            deletePatchField(patchFieldState);
        }
    }

    @Override
    public QueueObject getQueueObject() {
        return this.queueObject;
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.fountainGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.fountainGraphic.getGraphicLocation();
    }

    public static class FountainBlock extends AmenityBlock {
        public static FountainBlockFactory fountainBlockFactory;

        static {
            fountainBlockFactory = new FountainBlockFactory();
        }

        private FountainBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class FountainBlockFactory extends AmenityBlockFactory {
            @Override
            public FountainBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new FountainBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class FountainFactory extends Goal.GoalFactory {
        public static Fountain create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Fountain(amenityBlocks, enabled, waitingTime);
        }
    }

}