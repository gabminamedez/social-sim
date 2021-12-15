package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.StoreCounterGraphic;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.ArrayList;
import java.util.List;

public class StoreCounter extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    private MallAgent agentActing; // Takes note of the agent currently acting in the storeCounter
    private final QueueObject queueObject;
    public static final StoreCounter.StoreCounterFactory storeCounterFactory;
    private final QueueingPatchField.PatchFieldState storeCounterPatchFieldState;
    private final StoreCounterGraphic storeCounterGraphic;

    static {
        storeCounterFactory = new StoreCounter.StoreCounterFactory();
    }

    protected StoreCounter(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.queueObject = new QueueObject(this, this.getAttractors().get(0).getPatch());
        this.agentActing = null;
        // this.storeCounterPatchFieldState = new QueueingPatchField.PatchFieldState(MallAgentMovement.Disposition.BOARDING, MallAgentMovement.State.IN_QUEUE, this);
        QueueingPatchField queueingPatchField = QueueingPatchField.queueingPatchFieldFactory.create(this); // Add a blank patch field
        this.storeCounterPatchFieldState = null;
        this.getQueueObject().getPatchFields().put(this.storeCounterPatchFieldState, queueingPatchField); // Using the patch field state defined earlier, create the patch field
        this.getQueueObjectAmenityBlockMap().put(this.getQueueObject(), this.getAttractors().get(0)); // Define the relationships between the queue objects and the attractors
        this.storeCounterGraphic = new StoreCounterGraphic(this);
    }

    public MallAgent getMallAgentActing() {
        return agentActing;
    }

    public void setMallAgentActing(MallAgent agentActing) {
        this.agentActing = agentActing;
    }

    public QueueingPatchField.PatchFieldState getTicketBoothPatchFieldState() {
        return storeCounterPatchFieldState;
    }

    @Override
    public boolean isFree(QueueObject queueObject) { // Check whether this queueable is free to service an agent
        return this.queueObject.isFree();
    }

    @Override
    public String toString() {
        return "StoreCounter" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates() {
        List<QueueingPatchField.PatchFieldState> patchFieldStates = new ArrayList<>();
        patchFieldStates.add(this.storeCounterPatchFieldState);

        return patchFieldStates;
    }

    @Override
    public QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState) {
        return queueObject.getPatchFields().get(patchFieldState);
    }

    @Override
    public boolean isPatchFieldsComplete() { // Denotes whether the patch field for this ticket booth is complete
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), this.storeCounterPatchFieldState);

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
    public MallAmenityGraphic getGraphicObject() {
        return this.storeCounterGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.storeCounterGraphic.getGraphicLocation();
    }

    public static class StoreCounterBlock extends AmenityBlock {
        public static StoreCounter.StoreCounterBlock.StoreCounterBlockFactory storeCounterBlockFactory;

        static {
            storeCounterBlockFactory = new StoreCounter.StoreCounterBlock.StoreCounterBlockFactory();
        }

        private StoreCounterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class StoreCounterBlockFactory extends AmenityBlockFactory {
            @Override
            public StoreCounter.StoreCounterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new StoreCounter.StoreCounterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class StoreCounterFactory extends Goal.GoalFactory {
        public static StoreCounter create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new StoreCounter(amenityBlocks, enabled, waitingTime);
        }
    }

}