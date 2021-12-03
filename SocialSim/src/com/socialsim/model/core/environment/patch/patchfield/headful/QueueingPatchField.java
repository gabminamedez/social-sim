package com.socialsim.model.core.environment.patch.patchfield.headful;

import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchfield.AbstractPatchField;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QueueingPatchField extends HeadfulPatchField {

    public static QueueingPatchFieldFactory queueingPatchFieldFactory;

    static {
        queueingPatchFieldFactory = new QueueingPatchFieldFactory();
    }

    protected QueueingPatchField(Queueable target) {
        super(target);
    }

    public static boolean addPatchFieldValue(UniversityPatch patch, Queueable target, PatchFieldState patchFieldState, double value) {
        // When adding a floor field value, these things have to happen:
        //   1) Register the patch where the floor field value is to be drawn to the target queueable's floor field
        //   2) Add the floor field value to the patch itself
        // In the target queueable, register the patch into the target's list of floor fields, if possible
        // TODO: See original implementation if there is a queueing PatchField to be implemented

        return true;
    }

    // Add the patch to the floor fields kept tracked by the target queueable
    private static boolean registerPatch(UniversityPatch patch, Queueable target, PatchFieldState patchFieldState, double value) {
        final double EPSILON = 1E-6;

        QueueingPatchField queueingPatchField = target.retrievePatchField(target.getQueueObject(), patchFieldState);
        List<UniversityPatch> associatedPatches = queueingPatchField.getAssociatedPatches();

        Amenity amenity = ((Amenity) target);

        // If the floor field value is one, check if the number of apices in this floor field is already equal to the number of attractors in the amenity. This is to make sure that there is only one apex in the floor field.
        if (Math.abs(value - 1.0) < EPSILON) {
            if (queueingPatchField.getApices().size() == 1) { // If it is, refuse to register the patch
                return false;
            }
            else { // If it hasn't yet, add the patch to the list of apices, if it isn't already in the list
                if (!queueingPatchField.getApices().contains(patch)) {
                    queueingPatchField.getApices().add(patch);
                }
            }
        }

        // Check if the floor field already contains the patch
        if (associatedPatches.contains(patch)) { // If it already does, just modify the value that's already there
            double valuePresent = patch.getFloorFieldValues().get(target).get(queueingPatchField);

            // If the present value is 1.0, and the value to replace it isn't 1.0, indicate that this patch doesn't have an apex anymore as it was replaced with another value
            if (Math.abs(valuePresent - 1.0) < EPSILON && value < 1.0 - EPSILON) {
                queueingPatchField.getApices().remove(patch);
            }
        }
        else { // If it doesn't contain the patch yet, add it
            associatedPatches.add(patch);
        }

        return true;
    }

    // Remove the patch from the floor fields kept tracked on by the target queueable
    private static void unregisterPatch(UniversityPatch patch, Queueable target, PatchFieldState patchFieldState, double value) {
        final double EPSILON = 1E-6;

        QueueingPatchField queueingPatchField = target.retrievePatchField(target.getQueueObject(), patchFieldState);
        queueingPatchField.getAssociatedPatches().remove(patch); // Unregister the patch from this target

        // If the value being removed is 1.0, this means this floor field won't have an apex anymore
        if (Math.abs(value - 1.0) < EPSILON) {
            queueingPatchField.getApices().remove(patch);
        }
    }

    // In a given patch, delete an individual floor field value in a floor field owned by a given target
    public static void deletePatchFieldValue(UniversityPatch patch, Queueable target, PatchFieldState patchFieldState) {
        // When deleting a floor field value, these things have to happen:
        //   1) Unregister the patch where the floor field value to be deleted is from the target queueable's floor
        //      field
        //   2) Remove the floor field value from the patch itself
        // Unregister the patch from the target's list of floor fields
        // Get the value of the floor field value to be removed as well

        Map<PatchFieldState, Double> map = patch.getFloorFieldValues().get(target);

        // TODO: See original implementation if there is a queueing PatchField to be implemented
    }

    // Clear the given floor field
    public static void clearPatchField(QueueingPatchField queueingPatchField, PatchFieldState patchFieldState) {
        // In each patch in the floor field to be deleted, delete the reference to its target. This deletes the value within that patch. Note that deletion should only be done when the patch contains a floor field value in the given floor field state.
        List<UniversityPatch> associatedPatches = queueingPatchField.getAssociatedPatches();
        Queueable target = queueingPatchField.getTarget();

        List<UniversityPatch> associatedPatchesCopy = new ArrayList<>(associatedPatches);

        for (UniversityPatch patch : associatedPatchesCopy) {
            QueueingPatchField.deletePatchFieldValue(patch, target, patchFieldState);
        }

        associatedPatchesCopy.clear();
    }

    public static class PatchFieldState extends AbstractPatchField { // A combination of a passenger's direction, state, and current target, this object is used for the differentiation of floor fields
        public PatchFieldState() {
        }

//        private final AgentMovement.Direction direction;
//        private final AgentMovement.State state;
//        private final Queueable target;
//
//        public PatchFieldState(AgentMovement.Direction direction, AgentMovement.State state, Queueable target) {
//            this.direction = direction;
//            this.state = state;
//            this.target = target;
//        }
//
//        public AgentMovement.Direction getDirection() {
//            return direction;
//        }
//
//        public AgentMovement.State getState() {
//            return state;
//        }
//
//        public Queueable getTarget() {
//            return target;
//        }
//
//        @Override
//        public String toString() {
//            if (direction != null) {
//                return direction.toString();
//            }
//            else {
//                return "(any direction)";
//            }
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            PatchFieldState that = (PatchFieldState) o;
//            return direction == that.direction && state == that.state && target.equals(that.target);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(direction, state, target);
//        }
    }

    public static class QueueingPatchFieldFactory extends HeadfulPatchFieldFactory {
        public QueueingPatchField create(Queueable target) {
            return new QueueingPatchField(target);
        }
    }

}