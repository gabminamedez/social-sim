package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.ServiceCounterGraphic;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.ArrayList;
import java.util.List;

public class ServiceCounter extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    private GroceryAgent agentActing; // Takes note of the agent currently acting in the serviceCounter
    private final QueueObject queueObject;
    public static final ServiceCounter.ServiceCounterFactory serviceCounterFactory;
    private final QueueingPatchField.PatchFieldState serviceCounterPatchFieldState;
    private final ServiceCounterGraphic serviceCounterGraphic;

    static {
        serviceCounterFactory = new ServiceCounter.ServiceCounterFactory();
    }

    protected ServiceCounter(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.queueObject = new QueueObject(this, this.getAttractors().get(0).getPatch());
        this.agentActing = null;
        // this.serviceCounterPatchFieldState = new QueueingPatchField.PatchFieldState(GroceryAgentMovement.Disposition.BOARDING, GroceryAgentMovement.State.IN_QUEUE, this);
        QueueingPatchField queueingPatchField = QueueingPatchField.queueingPatchFieldFactory.create(this); // Add a blank patch field
        this.serviceCounterPatchFieldState = null;
        this.getQueueObject().getPatchFields().put(this.serviceCounterPatchFieldState, queueingPatchField); // Using the patch field state defined earlier, create the patch field
        this.getQueueObjectAmenityBlockMap().put(this.getQueueObject(), this.getAttractors().get(0)); // Define the relationships between the queue objects and the attractors
        this.serviceCounterGraphic = new ServiceCounterGraphic(this);
    }

    public GroceryAgent getGroceryAgentActing() {
        return agentActing;
    }

    public void setGroceryAgentActing(GroceryAgent agentActing) {
        this.agentActing = agentActing;
    }

    public QueueingPatchField.PatchFieldState getTicketBoothPatchFieldState() {
        return serviceCounterPatchFieldState;
    }

    @Override
    public boolean isFree(QueueObject queueObject) { // Check whether this queueable is free to service an agent
        return this.queueObject.isFree();
    }

    @Override
    public String toString() {
        return "ServiceCounter" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates() {
        List<QueueingPatchField.PatchFieldState> patchFieldStates = new ArrayList<>();
        patchFieldStates.add(this.serviceCounterPatchFieldState);

        return patchFieldStates;
    }

    @Override
    public QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState) {
        return queueObject.getPatchFields().get(patchFieldState);
    }

    @Override
    public boolean isPatchFieldsComplete() { // Denotes whether the patch field for this ticket booth is complete
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), this.serviceCounterPatchFieldState);

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
    public GroceryAmenityGraphic getGraphicObject() {
        return this.serviceCounterGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.serviceCounterGraphic.getGraphicLocation();
    }

    public static class ServiceCounterBlock extends Amenity.AmenityBlock {
        public static ServiceCounter.ServiceCounterBlock.ServiceCounterBlockFactory serviceCounterBlockFactory;

        static {
            serviceCounterBlockFactory = new ServiceCounter.ServiceCounterBlock.ServiceCounterBlockFactory();
        }

        private ServiceCounterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ServiceCounterBlockFactory extends AmenityBlockFactory {
            @Override
            public ServiceCounter.ServiceCounterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ServiceCounter.ServiceCounterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ServiceCounterFactory extends Goal.GoalFactory {
        public static ServiceCounter create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new ServiceCounter(amenityBlocks, enabled, waitingTime);
        }
    }

}