package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.KioskGraphic;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.generic.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.ArrayList;
import java.util.List;

public class Kiosk extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    private MallAgent agentActing; // Takes note of the agent currently acting in the kiosk
    private final QueueObject queueObject;
    public static final Kiosk.KioskFactory kioskFactory;
    private final QueueingPatchField.PatchFieldState kioskPatchFieldState;
    private final KioskGraphic kioskGraphic;

    static {
        kioskFactory = new Kiosk.KioskFactory();
    }

    protected Kiosk(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.queueObject = new QueueObject(this, this.getAttractors().get(0).getPatch());
        this.agentActing = null;
        // this.kioskPatchFieldState = new QueueingPatchField.PatchFieldState(MallAgentMovement.Disposition.BOARDING, MallAgentMovement.State.IN_QUEUE, this);
        QueueingPatchField queueingPatchField = QueueingPatchField.queueingPatchFieldFactory.create(this); // Add a blank patch field
        this.kioskPatchFieldState = null;
        this.getQueueObject().getPatchFields().put(this.kioskPatchFieldState, queueingPatchField); // Using the patch field state defined earlier, create the patch field
        this.getQueueObjectAmenityBlockMap().put(this.getQueueObject(), this.getAttractors().get(0)); // Define the relationships between the queue objects and the attractors
        this.kioskGraphic = new KioskGraphic(this);
    }

    public MallAgent getMallAgentActing() {
        return agentActing;
    }

    public void setMallAgentActing(MallAgent agentActing) {
        this.agentActing = agentActing;
    }

    public QueueingPatchField.PatchFieldState getTicketBoothPatchFieldState() {
        return kioskPatchFieldState;
    }

    @Override
    public boolean isFree(QueueObject queueObject) { // Check whether this queueable is free to service an agent
        return this.queueObject.isFree();
    }

    @Override
    public String toString() {
        return "Kiosk" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates() {
        List<QueueingPatchField.PatchFieldState> patchFieldStates = new ArrayList<>();
        patchFieldStates.add(this.kioskPatchFieldState);

        return patchFieldStates;
    }

    @Override
    public QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState) {
        return queueObject.getPatchFields().get(patchFieldState);
    }

    @Override
    public boolean isPatchFieldsComplete() { // Denotes whether the patch field for this ticket booth is complete
        QueueingPatchField queueingPatchField = retrievePatchField(this.getQueueObject(), this.kioskPatchFieldState);

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
        return this.kioskGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.kioskGraphic.getGraphicLocation();
    }

    public static class KioskBlock extends AmenityBlock {
        public static Kiosk.KioskBlock.KioskBlockFactory kioskBlockFactory;

        static {
            kioskBlockFactory = new Kiosk.KioskBlock.KioskBlockFactory();
        }

        private KioskBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class KioskBlockFactory extends AmenityBlockFactory {
            @Override
            public Kiosk.KioskBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Kiosk.KioskBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class KioskFactory extends Goal.GoalFactory {
        public static Kiosk create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Kiosk(amenityBlocks, enabled, waitingTime);
        }
    }

}