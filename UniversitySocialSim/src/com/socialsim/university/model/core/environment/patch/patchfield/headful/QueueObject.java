package com.socialsim.university.model.core.environment.patch.patchfield.headful;

public class QueueObject {

    // Any amenity that is queueable must contain a hashmap of floor fields
    // Given a passenger state, a floor field may be retrieved from that goal
    private final Map<QueueingFloorField.FloorFieldState, QueueingFloorField> floorFields = new HashMap<>();

    // Denotes the list of passengers who are queueing for this goal
    private final LinkedList<Passenger> passengersQueueing = new LinkedList<>();

    // Denotes the passenger at the back of the queue
    private Passenger lastPassengerQueueing;

    // Denotes the passenger currently being serviced by this queueable
    private Passenger passengerServiced;

    public Map<QueueingFloorField.FloorFieldState, QueueingFloorField> getFloorFields() {
        return floorFields;
    }

    public LinkedList<Passenger> getPassengersQueueing() {
        return passengersQueueing;
    }

    public Passenger getLastPassengerQueueing() {
        return lastPassengerQueueing;
    }

    public void setLastPassengerQueueing(Passenger lastPassengerQueueing) {
        this.lastPassengerQueueing = lastPassengerQueueing;
    }

    public Passenger getPassengerServiced() {
        return passengerServiced;
    }

    public void setPassengerServiced(Passenger passengerServiced) {
        this.passengerServiced = passengerServiced;
    }

}