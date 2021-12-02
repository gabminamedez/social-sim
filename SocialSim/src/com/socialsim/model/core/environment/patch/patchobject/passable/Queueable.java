package com.socialsim.model.core.environment.patch.patchobject.passable;

import com.socialsim.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.patch.patchfield.headful.QueueingPatchField;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public interface Queueable {

    List<QueueingPatchField.PatchFieldState> retrievePatchFieldStates(); // Retrieves the patch field states of this queueable

    QueueingPatchField retrievePatchField(QueueObject queueObject, QueueingPatchField.PatchFieldState patchFieldState); // Retrieves a patch field of this queueable, given the state

    boolean isPatchFieldsComplete(); // Denotes whether this queueable's patch fields are filled

    void deletePatchField(QueueingPatchField.PatchFieldState patchFieldState); // Delete a patch field of a certain state in this queueable

    void deleteAllPatchFields(); // Delete all patch fields in this queueable

    QueueObject getQueueObject(); // Retrieve the queue object

    boolean isFree(QueueObject queueObject); // Check if this queueable is free, given the goal queue object

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