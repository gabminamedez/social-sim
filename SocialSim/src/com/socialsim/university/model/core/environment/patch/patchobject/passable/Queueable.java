package com.socialsim.university.model.core.environment.patch.patchobject.passable;

import com.socialsim.university.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.university.model.core.environment.patch.patchfield.headful.QueueingPatchField;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public interface Queueable {

    // Retrieves the floor field states of this queueable
    List<QueueingPatchField.PatchFieldState> retrieveFloorFieldStates();

    // Retrieves a floor field of this queueable, given the state
    QueueingPatchField retrieveFloorField(QueueObject queueObject, QueueingPatchField.PatchFieldState floorFieldState);

    // Denotes whether this queueable's floor fields are filled
    boolean isFloorFieldsComplete();

    // Delete a floor field of a certain state in this queueable
    void deleteFloorField(QueueingPatchField.PatchFieldState floorFieldState);

    // Delete all floor fields in this queueable
    void deleteAllFloorFields();

    // Retrieve the queue object
    QueueObject getQueueObject();

    static Queueable toQueueable(Amenity amenity) {
        if (isQueueable(amenity)) {
            return (Queueable) amenity;
        }
        else {
            return null;
        }
    }

    static boolean isQueueable(Amenity amenity) {
        return amenity instanceof Queueable;
    }

}