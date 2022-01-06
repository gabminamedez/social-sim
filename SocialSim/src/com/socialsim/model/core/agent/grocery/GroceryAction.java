package com.socialsim.model.core.agent.grocery;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

public class GroceryAction {

    public enum Name{
        GREET_GUARD,
        GO_THROUGH_SCANNER,

        GO_TO_CART_AREA,
        GO_TO_AISLE,
        GO_TO_PRODUCT_WALL,
        GO_TO_FROZEN,
        GO_TO_FRESH,
        GO_TO_MEAT,
        GO_TO_ASK_STAFF,
        GO_TO_CUSTOMER_SERVICE,
        GO_TO_FOOD_STALL,

        GET_CART,

        FIND_PRODUCTS,
        CHECK_PRODUCTS,
        FOLLOW_LEADER_SHOP,

        GO_TO_CHECKOUT,
        QUEUE_CHECKOUT,
        CHECKOUT,
        TALK_TO_CASHIER,
        TALK_TO_BAGGER,

        TALK_TO_CUSTOMER_SERVICE,
        WAIT_FOR_CUSTOMER_SERVICE,
        FOLLOW_LEADER_SERVICE,

        QUEUE_FOOD,
        BUY_FOOD,
        FOLLOW_LEADER_EAT,
        FIND_SEAT_FOOD_COURT,
        EATING_FOOD,

        CHECKOUT_GROCERIES_CUSTOMER,
        CHECKOUT_GROCERIES_GUARD,

        LEAVE_BUILDING,

        BUTCHER_STATION,
        BUTCHER_SERVE_CUSTOMER,

        CASHIER_STATION,
        CASHIER_SERVE_CUSTOMER,

        BAGGER_STATION,
        BAGGER_SERVE_CUSTOMER,

        SERVICE_STATION,
        SERVICE_SERVE_CUSTOMER,


        GREET_PERSON,
        GUARD_STATION,
        GUARD_CHECK_GROCERIES,

        STAFF_FOOD_STATION,
        STAFF_FOOD_SERVE_CUSTOMER,

        STAFF_AISLE_ORGANIZE,
        STAFF_AISLE_ANSWER_CUSTOMER
    }

    private Name name;
    private int duration;
    private Patch destination;
    private int aisleID;
    private GroceryAgent leaderAgent;

    public GroceryAction(Name name){ // For actions where the destination depends on the chooseGoal/chooseStall, and the duration also depends on the movement
        this.name = name;
    }

    public GroceryAction(Name name, Patch destination){ // For going to somewhere (since time will depend on AgentMovement)
        this.name = name;
        this.destination = destination;
    }

    public GroceryAction(Name name, int aisleID){ // For queueables (i.e. no need to specify patch) OR amenities where the specific patch is TBD (e.g. yet to find nearest amenity)
        this.name = name;
        this.aisleID = aisleID;
    }

    public GroceryAction(Name name, GroceryAgent agent, int duration){ // For agents following a leader
        this.name = name;
        this.leaderAgent = agent;
        this.duration = duration;
    }

    public GroceryAction(Name name, GroceryAgent agent){ // For agents following a leader
        this.name = name;
        this.leaderAgent = agent;
    }

    public GroceryAction(Name name, int minimumDuration, int maximumDuration){ // For queueables (i.e. no need to specify patch) OR amenities where the specific patch is TBD (e.g. yet to find nearest amenity)
        this.name = name;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public GroceryAction(Name name, Patch destination, int minimumDuration, int maximumDuration){ // For complete actions with undefined duration
        this.name = name;
        this.destination = destination;
        this.duration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(maximumDuration - minimumDuration + 1) + minimumDuration;
    }

    public GroceryAction(Name name, Patch destination, int duration) { // For complete actions with defined duration
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