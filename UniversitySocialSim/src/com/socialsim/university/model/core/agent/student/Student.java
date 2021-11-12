package com.socialsim.university.model.core.agent.student;

import com.socialsim.university.model.core.agent.Agent;
import com.socialsim.university.model.core.environment.patch.patchobject.PatchObject;

public class Student extends PatchObject implements Agent {

    private static int studentCount = 0;

    private final int id;
    private final Gender gender;
    private final int age;

    // Handles how this passenger is displayed
    // private final PassengerGraphic passengerGraphic;

    // Contains the mechanisms for this passenger's movement
    private final StudentMovement studentMovement;

    // Factory for passenger creation
    public static final StudentFactory studentFactory;

    static {
        studentFactory = new StudentFactory();
    }

    private Student(Patch spawnPatch) {
        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        final double singleJourneyPercentage = 1.0;

        this.ticketType
                = Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() < singleJourneyPercentage
                ? TicketBooth.TicketType.SINGLE_JOURNEY : TicketBooth.TicketType.STORED_VALUE;

        // Set the graphic object of this passenger
        this.passengerGraphic = new PassengerGraphic(this);

        // The identifier of this passenger is its serial number (based on the number of passengers generated)
        this.identifier = passengerCount;

        // Increment the number of passengers made by one
        Passenger.passengerCount++;

        Gate gate = (Gate) spawnPatch.getAmenityBlock().getParent();

        // Instantiate all movement-related fields
        this.passengerMovement = new PassengerMovement(
                gate,
                this,
                spawnPatch.getPatchCenterCoordinates()
        );
    }

    public Gender getGender() {
        return gender;
    }

    public TicketBooth.TicketType getTicketType() {
        return ticketType;
    }

    public PassengerGraphic getPassengerGraphic() {
        return passengerGraphic;
    }

    public PassengerMovement getPassengerMovement() {
        return this.passengerMovement;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public static class StudentFactory extends UniversityObjectFactory {
        public Student create(Patch spawnPatch) {
            return new Student(spawnPatch);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return identifier == passenger.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return String.valueOf(this.identifier);
    }

    public enum Gender {
        FEMALE,
        MALE
    }

}