package com.socialsim.model.core.agent.mall;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class MallAction {

    public enum Name {
        GOING_TO_SECURITY_QUEUE(), GO_THROUGH_SCANNER(), GREET_GUARD(),
        FIND_DIRECTORY(), VIEW_DIRECTORY(), FIND_BENCH(), SIT_ON_BENCH(),
        GO_TO_BATHROOM(), RELIEVE_IN_CUBICLE(), WASH_IN_SINK(),
        GO_TO_STORE(), CHECK_AISLE(), ASK_STAFF_SALES(), GO_TO_AISLE(), CHECKOUT_STORE(), TALK_TO_CASHIER(),
        GO_TO_KIOSK(), QUEUE_KIOSK(), CHECKOUT_KIOSK(),
        GO_TO_RESTAURANT(), ASK_STAFF_RESTO(), RESTAURANT_STAY_PUT(),
        GO_TO_DINING_AREA(), DINING_AREA_STAY_PUT(),
        LEAVE_BUILDING(),
        GUARD_STATION(), GREET_PERSON(),
        STAFF_KIOSK_STATION(), STAFF_KIOSK_ANSWER(),
        STAFF_RESTO_SERVE(), STAFF_RESTO_ANSWER(),
        STAFF_STORE_STATION(), STAFF_SALES_ANSWER(), STAFF_CASHIER_ANSWER(),
        GO_CONCIERGE(), ASK_CONCIERGE(),

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
    private MallAgent leaderAgent;

    public MallAction(Name name) { // For actions where the destination depends on the chooseGoal/chooseStall, and the duration also depends on the movement
        this.name = name;
    }

    public MallAction(Name name, Patch destination) {
        this.name = name;
        this.destination = destination;
    }

    public MallAction(Name name, MallAgent agent, int duration) {
        this.name = name;
        this.leaderAgent = agent;
        this.duration = duration;
    }

    public MallAction(Name name, int minimumDuration, int maximumDuration) {
        this.name = name;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
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

    public MallAgent getLeaderAgent() {
        return leaderAgent;
    }

    public void setLeaderAgent(MallAgent leaderAgent) {
        this.leaderAgent = leaderAgent;
    }

}