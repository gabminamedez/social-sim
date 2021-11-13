package com.socialsim.university.model.core.environment.patch.patchobject.passable;

public interface Queueable {

    // Retrieves the floor field states of this queueable
    List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates();

    // Retrieves a floor field of this queueable, given the state
    QueueingFloorField retrieveFloorField(QueueObject queueObject, QueueingFloorField.FloorFieldState floorFieldState);

    // Denotes whether this queueable's floor fields are filled
    boolean isFloorFieldsComplete();

    // Delete a floor field of a certain state in this queueable
    void deleteFloorField(QueueingFloorField.FloorFieldState floorFieldState);

    // Delete all floor fields in this queueable
    void deleteAllFloorFields();

    // Retrieve the queue object
    QueueObject getQueueObject();

    static Queueable toQueueable(Amenity amenity) {
        if (isQueueable(amenity)) {
            return (Queueable) amenity;
        } else {
            return null;
        }
    }

    static boolean isQueueable(Amenity amenity) {
        return amenity instanceof Queueable;
    }

}