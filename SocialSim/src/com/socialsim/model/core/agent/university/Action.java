package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class Action {

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

        VIEW_BULLETIN,
        FIND_BENCH,
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
        JANITOR_CHECK_FOUNTAIN
    }

    private Name name;
    private int duration;
    private Patch destination;

    public Action(Name name, Patch destination, int minimumDuration, int maximumDuration){
        this.name = name;
        this.destination = destination;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public Action(Name name, Patch destination, int duration){
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
