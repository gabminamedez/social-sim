package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class UniversityAction {

    public enum Name{
        GREET_GUARD,
        GO_THROUGH_SCANNER,

        GO_TO_CAFETERIA,
        GO_TO_STUDY_ROOM,
        GO_TO_CLASSROOM,
        GO_TO_BATHROOM,
        GO_TO_DRINKING_FOUNTAIN,

        CLASSROOM_STAY_PUT,
        STUDY_AREA_STAY_PUT,
        LUNCH_STAY_PUT,

        FIND_BULLETIN,
        VIEW_BULLETIN,
        FIND_BENCH,
        SIT_ON_BENCH,
        LEAVE_BUILDING,
        THROW_ITEM_TRASH_CAN,

        FIND_CUBICLE,
        RELIEVE_IN_CUBICLE,
        WASH_IN_SINK,

        QUEUE_FOUNTAIN,
        DRINK_FOUNTAIN,

        FIND_SEAT_STUDY_ROOM,
        FIND_SEAT_CLASSROOM,
        SIT_PROFESSOR_TABLE,
        GO_TO_BLACKBOARD,
        ASK_PROFESSOR_QUESTION,
        ANSWER_STUDENT_QUESTION,

        GO_TO_VENDOR,
        QUEUE_VENDOR,
        CHECKOUT,
        FIND_SEAT_CAFETERIA,

        GREET_PERSON,
        GUARD_STAY_PUT,

        CLEAN_STAY_PUT,
        JANITOR_MOVE_SPOT,
        JANITOR_GO_TO_FOUNTAIN,
        JANITOR_CHECK_FOUNTAIN
    }

    private Name name;
    private int duration;
    private Patch destination;

    public UniversityAction(Name name){ // For actions where the destination depends on the chooseGoal/chooseStall, and the duration also depends on the movement
        this.name = name;
        this.destination = destination;
    }

    public UniversityAction(Name name, Patch destination){ // For going to somewhere (since time will depend on AgentMovement)
        this.name = name;
        this.destination = destination;
    }

    public UniversityAction(Name name, int duration){ // For queueables (i.e. no need to specify patch) OR amenities where the specific patch is TBD (e.g. yet to find nearest amenity)
        this.name = name;
        this.duration = duration;
    }

    public UniversityAction(Name name, int minimumDuration, int maximumDuration){ // For queueables (i.e. no need to specify patch) OR amenities where the specific patch is TBD (e.g. yet to find nearest amenity)
        this.name = name;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public UniversityAction(Name name, Patch destination, int minimumDuration, int maximumDuration){ // For complete actions with undefined duration
        this.name = name;
        this.destination = destination;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public UniversityAction(Name name, Patch destination, int duration) { // For complete actions with defined duration
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
