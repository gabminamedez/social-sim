package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class OfficeAction {

    public enum Name {
        LEAVE_OFFICE(), GO_TO_LUNCH(), EAT_LUNCH(), EXIT_LUNCH(),
        GOING_TO_SECURITY_QUEUE(), GO_THROUGH_SCANNER(), GUARD_STAY_PUT(), GREET_PERSON(),
        JANITOR_CLEAN_TOILET(), JANITOR_WATER_PLANT(),
        CLIENT_GO_RECEPTIONIST(), CLIENT_GO_COUCH(), CLIENT_GO_OFFICE(),
        DRIVER_GO_RECEPTIONIST(), DRIVER_GO_COUCH(),
        VISITOR_GO_RECEPTIONIST(), VISITOR_GO_OFFICE(),
        RECEPTIONIST_STAY_PUT(),
        SECRETARY_STAY_PUT(), SECRETARY_CHECK_CABINET(), SECRETARY_GO_BOSS(),
        GO_TO_STATION(), GO_TO_OFFICE_ROOM(), GO_BOSS(), ASK_BOSS(),
        GO_WORKER(), GO_MANAGER(), ASK_WORKER(), ASK_MANAGER(), ANSWER_BOSS(),
        ANSWER_MANAGER(), ANSWER_WORKER(),
        GO_TO_BATHROOM(), RELIEVE_IN_CUBICLE(), FIND_SINK(), WASH_IN_SINK(),
        GO_TO_PRINTER(), QUEUE_PRINTER(), PRINTING(),
        GO_TO_COLLAB(), WAIT_FOR_COLLAB(), COLLABORATE(),
        TECHNICAL_GO_PRINTER(), FIX_PRINTER(), FIX_CUBICLE(),
        GO_MEETING(), WAIT_MEETING(), MEETING(), GOING_DISPENSER(),
        GETTING_WATER(), GOING_FRIDGE(), GETTING_FOOD(), TAKING_BREAK(),

        GO_TO_WAIT_AREA(),WAIT_FOR_VACANT();

        final int ID;

        Name() {
            this.ID = this.ordinal();
        }

        public int getID() {
            return ID;
        }
    }

    private Name name;
    private int duration;
    private Patch destination;

    public OfficeAction(Name name){
        this.name = name;
    }

    public OfficeAction(Name name, Patch destination){
        this.name = name;
        this.destination = destination;
    }

    public OfficeAction(Name name, int duration){
        this.name = name;
        this.duration = duration;
    }

    public OfficeAction(Name name, int minimumDuration, int maximumDuration){
        this.name = name;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public OfficeAction(Name name, Patch destination, int minimumDuration, int maximumDuration){
        this.name = name;
        this.destination = destination;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public OfficeAction(Name name, Patch destination, int duration) {
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