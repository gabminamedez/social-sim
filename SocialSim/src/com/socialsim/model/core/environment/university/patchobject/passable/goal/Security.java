package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.SecurityGraphic;
import com.socialsim.model.core.agent.AgentMovement;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.BlockableAmenity;

import java.util.ArrayList;
import java.util.List;

public class Security extends BlockableAmenity {

    private boolean blockEntry; // Denotes whether to block agents from passing through
    public static final SecurityFactory securityFactory;
    private final QueueingPatchField.PatchFieldState securityPatchFieldState; // Denotes the patch field state needed to access the floor fields of this security gate
    private final SecurityGraphic securityGraphic;

    static {
        securityFactory = new SecurityFactory();
    }

    protected Security(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, boolean blockAgents) {
        super(amenityBlocks, enabled, waitingTime, new QueueObject(), blockAgents);

        this.securityPatchFieldState = new QueueingPatchField.PatchFieldState(AgentMovement.Direction.BOARDING, AgentMovement.State.IN_QUEUE, this);
        QueueingPatchField queueingPatchField = QueueingPatchField.queueingPatchFieldFactory.create(this);
        this.getQueueObject().getFloorFields().put(this.securityPatchFieldState, queueingPatchField);
        this.securityGraphic = new SecurityGraphic(this);
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }

    public QueueingPatchField.PatchFieldState getSecurityPatchFieldState() {
        return securityPatchFieldState;
    }

    @Override
    public String toString() {
        return "Security" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates() {
        List<QueueingPatchField.PatchFieldState> floorFieldStates = new ArrayList<>();
        floorFieldStates.add(this.securityPatchFieldState);

        return floorFieldStates;
    }

    @Override
    public QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState) {
        return queueObject.getFloorFields().get(patchFieldState);
    }

    @Override
    public boolean isFloorFieldsComplete() {
        QueueingPatchField queueingFloorField = retrievePatchField(this.getQueueObject(), this.securityPatchFieldState);

        // The floor field of this queueable is complete when there are floor field values present with an apex patch that is equal to the number of attractors in this queueable target.
        return queueingFloorField.getApices().size() == this.getAttractors().size() && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    public void deleteFloorField(QueueingPatchField.PatchFieldState patchFieldState) {
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), patchFieldState);
        QueueingPatchField.clearPatchField(queueingPatchField, patchFieldState);
    }

    @Override
    public void deleteAllPatchFields() {
        List<QueueingPatchField.PatchFieldState> patchFieldStates = retrievePatchFieldStates();

        for (QueueingPatchField.PatchFieldState patchFieldState : patchFieldStates) {
            deleteFloorField(patchFieldState);
        }
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.securityGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.securityGraphic.getGraphicLocation();
    }

    public static class SecurityBlock extends Amenity.AmenityBlock {
        public static SecurityBlockFactory securityBlockFactory;

        static {
            securityBlockFactory = new SecurityBlockFactory();
        }

        private SecurityBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class SecurityBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Security.SecurityBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new SecurityBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class SecurityFactory extends GoalFactory {
        public Security create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, boolean blockAgents) {
            return new Security(amenityBlocks, enabled, waitingTime, blockAgents);
        }
    }

}