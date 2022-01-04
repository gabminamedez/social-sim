package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class UniversityAction {

    public enum Name {
        GREET_GUARD(0),
        GOING_TO_SECURITY_QUEUE(1),
        GO_THROUGH_SCANNER(2),
        RANDOM_ACTION(3),

        GO_TO_CAFETERIA(4),
        GO_TO_STUDY_ROOM(5),
        GO_TO_CLASSROOM(6),
        GO_TO_BATHROOM(7),
        GO_TO_DRINKING_FOUNTAIN(8),

        CLASSROOM_STAY_PUT(9),
        STUDY_AREA_STAY_PUT(10),
        LUNCH_STAY_PUT(11),

        FIND_BULLETIN(12),
        VIEW_BULLETIN(13),
        FIND_BENCH(14),
        SIT_ON_BENCH(15),
        LEAVE_BUILDING(16),
        THROW_ITEM_TRASH_CAN(17),

        FIND_CUBICLE(18),
        RELIEVE_IN_CUBICLE(19),
        WASH_IN_SINK(20),

        QUEUE_FOUNTAIN(21),
        DRINK_FOUNTAIN(22),

        FIND_SEAT_STUDY_ROOM(23),
        FIND_SEAT_CLASSROOM(24),
        SIT_PROFESSOR_TABLE(25),
        GO_TO_BLACKBOARD(26),
        ASK_PROFESSOR_QUESTION(27),
        ANSWER_STUDENT_QUESTION(28),

        LEAVE_STUDY_AREA(29),
        LEAVE_BENCH(30),
        LEAVE_BATHROOM(31),

        GO_TO_VENDOR(32),
        QUEUE_VENDOR(33),
        CHECKOUT(34),
        FIND_SEAT_CAFETERIA(35),

        GREET_PERSON(36),
        GUARD_STAY_PUT(37),

        JANITOR_GO_TOILET(38),
        JANITOR_CLEAN_TOILET(39),
        JANITOR_GO_FOUNTAIN(40),
        JANITOR_CHECK_FOUNTAIN(41);

        final int ID;
        Name(int ID){
            this.ID = ID;
        }
        public int getID() {
            return ID;
        }
    }
    private Name name;
    private int duration = 0;
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
