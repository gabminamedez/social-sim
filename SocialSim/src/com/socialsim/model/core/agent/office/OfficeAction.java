package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class OfficeAction {

    public enum Name {
        LEAVE_OFFICE, GO_TO_LUNCH, GOING_TO_SECURITY_QUEUE, GO_THROUGH_SCANNER,
        GUARD_STAY_PUT,
        JANITOR_GO_TOILET, JANITOR_CLEAN_TOILET,
        JANITOR_GO_PLANT, JANITOR_WATER_PLANT,
        CLIENT_GO_RECEPTIONIST, CLIENT_GO_COUCH, CLIENT_GO_OFFICE,
        DRIVER_GO_RECEPTIONIST, DRIVER_GO_COUCH,
        VISITOR_GO_RECEPTIONIST, VISITOR_GO_OFFICE,
        RECEPTIONIST_STAY_PUT,
        SECRETARY_STAY_PUT, SECRETARY_CHECK_CABINET, SECRETARY_GO_BOSS,
        GO_TO_STATION
    }

    private Name name;
    private int duration;
    private Patch destination;

    public OfficeAction(Name name){ // For actions where the destination depends on the chooseGoal/chooseStall, and the duration also depends on the movement
        this.name = name;
        this.destination = destination;
    }

    public OfficeAction(Name name, Patch destination){ // For going to somewhere (since time will depend on AgentMovement)
        this.name = name;
        this.destination = destination;
    }

    public OfficeAction(Name name, int duration){ // For queueables (i.e. no need to specify patch) OR amenities where the specific patch is TBD (e.g. yet to find nearest amenity)
        this.name = name;
        this.duration = duration;
    }

    public OfficeAction(Name name, int minimumDuration, int maximumDuration){ // For queueables (i.e. no need to specify patch) OR amenities where the specific patch is TBD (e.g. yet to find nearest amenity)
        this.name = name;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public OfficeAction(Name name, Patch destination, int minimumDuration, int maximumDuration){ // For complete actions with undefined duration
        this.name = name;
        this.destination = destination;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public OfficeAction(Name name, Patch destination, int duration) { // For complete actions with defined duration
        this.name = name;
        this.destination = destination;
        this.duration = duration;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Patch getDestination() {
        return destination;
    }

    public void setDestination(Patch destination) {
        this.destination = destination;
    }
}
