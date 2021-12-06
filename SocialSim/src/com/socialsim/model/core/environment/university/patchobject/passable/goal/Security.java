package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.SecurityGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.BlockableAmenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public class Security extends BlockableAmenity {

    public static final long serialVersionUID = -5458621245735102190L;
    public static final double standardDeviation = 3.0;
    private final QueueObject queueObject; // Denotes the queueing object associated with this security
    public static final SecurityFactory securityFactory;
    private final QueueingPatchField.PatchFieldState securityPatchFieldState;
    private final SecurityGraphic securityGraphic;

    static {
        securityFactory = new SecurityFactory();
    }

    protected Security(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, boolean blockAgents) {
        super(amenityBlocks, enabled, waitingTime, blockAgents);

        this.queueObject = new QueueObject(this, this.getAttractors().get(0).getPatch());
        // this.securityPatchFieldState = new QueueingPatchField.PatchFieldState(AgentMovement.Disposition.BOARDING, AgentMovement.State.IN_QUEUE, this);
        QueueingPatchField queueingPatchField = QueueingPatchField.queueingPatchFieldFactory.create(this); // Add a blank patch field
        this.securityPatchFieldState = null;
        this.getQueueObject().getPatchFields().put(this.securityPatchFieldState, queueingPatchField); // Using the patch field state defined earlier, create the patch field
        this.getQueueObjectAmenityBlockMap().put(this.getQueueObject(), this.getAttractors().get(0)); // Define the relationships between the queue objects and the attractors
        this.securityGraphic = new SecurityGraphic(this);
    }

    public QueueingPatchField.PatchFieldState getSecurityPatchFieldState() {
        return securityPatchFieldState;
    }

    @Override
    public boolean isFree(QueueObject queueObject) { // Check whether this queueable is free to service an agent
        return this.queueObject.isFree();
    }

    @Override
    public String toString() {
        return "Security" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates() {
        List<QueueingPatchField.PatchFieldState> patchFieldStates = new ArrayList<>();

        patchFieldStates.add(this.securityPatchFieldState);

        return patchFieldStates;
    }

    @Override
    public QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState) {
        return queueObject.getPatchFields().get(patchFieldState);
    }

    @Override
    public boolean isPatchFieldsComplete() { // Denotes whether the patch field for this security is complete
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), this.securityPatchFieldState);

        // The patch field of this queueable is complete when there are patch field values present with an apex patch that is equal to the number of attractors in this queueable target
        return queueingPatchField.getApices().size() == this.getAttractors().size() && !queueingPatchField.getAssociatedPatches().isEmpty();
    }

    @Override
    public void deletePatchField(QueueingPatchField.PatchFieldState patchFieldState) { // Clear all patch fields of the given patch field state in this security
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
    public AmenityGraphic getGraphicObject() {
        return this.securityGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.securityGraphic.getGraphicLocation();
    }

    public static class SecurityBlock extends AmenityBlock {
        public static SecurityBlockFactory securityBlockFactory;

        static {
            securityBlockFactory = new SecurityBlockFactory();
        }

        private SecurityBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class SecurityBlockFactory extends AmenityBlockFactory {
            @Override
            public SecurityBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new SecurityBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class SecurityFactory extends Goal.GoalFactory {
        public static Security create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, boolean blockAgents) {
            return new Security(amenityBlocks, enabled, waitingTime, blockAgents);
        }
    }

}