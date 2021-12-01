package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.BoardGraphic;
import com.socialsim.controller.graphics.amenity.University.ChairGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.List;

public class Chair extends Goal {

    public static final Chair.ChairFactory chairFactory;
    private final ChairGraphic chairGraphic;

    static {
        chairFactory = new Chair.ChairFactory();
    }

    protected Chair(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime, new QueueObject());




        // Using the floor field state defined earlier, create the floor field
        this.getQueueObject().getFloorFields().put(this.ticketBoothFloorFieldState, queueingFloorField);

        this.boardGraphic = new BoardGraphic(this);
    }


    @Override
    public String toString() {
        return "Ticket booth" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.ticketBoothFloorFieldState);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(
            QueueObject queueObject,
            QueueingFloorField.FloorFieldState floorFieldState
    ) {
        return queueObject.getFloorFields().get(
                floorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this ticket booth is complete
    public boolean isFloorFieldsComplete() {
        QueueingFloorField queueingFloorField = retrieveFloorField(
                this.getQueueObject(),
                this.ticketBoothFloorFieldState
        );

        // The floor field of this queueable is complete when there are floor field values present with an apex patch
        // that is equal to the number of attractors in this queueable target
        return queueingFloorField.getApices().size() == this.getAttractors().size()
                && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    // Clear all floor fields of the given floor field state in this ticket booth
    public void deleteFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        QueueingFloorField queueingFloorField = retrieveFloorField(
                this.getQueueObject(),
                floorFieldState
        );

        QueueingFloorField.clearFloorField(
                queueingFloorField,
                floorFieldState
        );
    }

    @Override
    public void deleteAllFloorFields() {
        // Sweep through each and every floor field and delete them
        List<QueueingFloorField.FloorFieldState> floorFieldStates = retrieveFloorFieldStates();

        for (QueueingFloorField.FloorFieldState floorFieldState : floorFieldStates) {
            deleteFloorField(floorFieldState);
        }
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.boardGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.boardGraphic.getGraphicLocation();
    }

    public static class BoardBlock extends Amenity.AmenityBlock {
        public static Board.BoardBlock.BoardBlockFactory ticketBoothBlockFactory;

        static {
            ticketBoothBlockFactory = new Board.BoardBlock.BoardBlockFactory();
        }

        private BoardBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class BoardBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Board.BoardBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Board.BoardBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ChairFactory extends GoalFactory {
        public Chair create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Chair(amenityBlocks, enabled, waitingTime);
        }
    }

}