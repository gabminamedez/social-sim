package com.socialsim.model.core.agent.grocery;

import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAction;
import com.socialsim.model.core.agent.grocery.GroceryState;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.simulator.Simulator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class GroceryRoutePlan {

    private ListIterator<GroceryState> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private GroceryState currentState; // Denotes the current class of the amenity/patchfield in the route plan

    //TODO: Maybe move this into another class that is static
    private static final int MIN_AISLE_ORGANIZE = 10;
    private static final int MAX_BUTCHER_STATION = 10;
    private static final int MIN_PRODUCTS = 2;
    private static final int MAX_PRODUCTS = 30;
    private static final int CART_THRESHOLD = 5;

    public static final int STTP_ALL_AISLE_CHANCE = 20, STTP_CHANCE_SERVICE = 0, STTP_CHANCE_FOOD = 20, STTP_CHANCE_EAT_TABLE = 10;
    public static final int MODERATE_ALL_AISLE_CHANCE = 40, MODERATE_CHANCE_SERVICE = 0, MODERATE_CHANCE_FOOD = 20, MODERATE_CHANCE_EAT_TABLE = 10;
    public static final int COMPLETE_FAMILY_ALL_AISLE_CHANCE = 60, COMPLETE_FAMILY_CHANCE_SERVICE = 20, COMPLETE_FAMILY_CHANCE_FOOD = 30, COMPLETE_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int HELP_FAMILY_ALL_AISLE_CHANCE = 50, HELP_FAMILY_CHANCE_SERVICE = 20, HELP_FAMILY_CHANCE_FOOD = 30, HELP_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int DUO_FAMILY_ALL_AISLE_CHANCE = 50, DUO_FAMILY_CHANCE_SERVICE = 20, DUO_FAMILY_CHANCE_FOOD = 30, DUO_FAMILY_CHANCE_EAT_TABLE = 50;

    public GroceryRoutePlan(GroceryAgent agent, GroceryAgent leaderAgent, Grocery grocery, Patch spawnPatch) { //leaderAgent is only for agents that follow and deviate
        List<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryAction> actions;

        if (agent.getPersona() == GroceryAgent.Persona.GUARD_ENTRANCE) {
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.GUARD_STATION, spawnPatch));
            routePlan.add(new GroceryState(GroceryState.Name.GUARD_ENTRANCE, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.GUARD_EXIT){
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.GUARD_STATION, spawnPatch));
            routePlan.add(new GroceryState(GroceryState.Name.GUARD_EXIT, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.STAFF_AISLE){
            actions = new ArrayList<>();
            for (int i = 0; i < MIN_AISLE_ORGANIZE; i++){
                actions.add(new GroceryAction(GroceryAction.Name.STAFF_AISLE_ORGANIZE, spawnPatch, 0));
            }
            //TODO Note condition if the aisles are all completed, add more aisles to organize throughout the simulation
            routePlan.add(new GroceryState(GroceryState.Name.STAFF_AISLE, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.BUTCHER){
            actions = new ArrayList<>();
            for (int i = 0; i < MAX_BUTCHER_STATION; i++){
                actions.add(new GroceryAction(GroceryAction.Name.BUTCHER_STATION, spawnPatch, 0));
            }
            routePlan.add(new GroceryState(GroceryState.Name.BUTCHER, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.CASHIER){
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.CASHIER_STATION, spawnPatch));
            routePlan.add(new GroceryState(GroceryState.Name.CASHIER, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.BAGGER){
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.BAGGER_STATION, spawnPatch));
            routePlan.add(new GroceryState(GroceryState.Name.BAGGER, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.CUSTOMER_SERVICE){
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.SERVICE_STATION, spawnPatch));
            routePlan.add(new GroceryState(GroceryState.Name.CUSTOMER_SERVICE, this, agent, actions));
        }
        else if (agent.getPersona() == GroceryAgent.Persona.STAFF_FOOD) {
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.STAFF_FOOD_STATION, spawnPatch));
            routePlan.add(new GroceryState(GroceryState.Name.STAFF_FOOD, this, agent, actions));
        }
        else {
            // Customers
            if (agent.getPersona() == GroceryAgent.Persona.STTP_ALONE_CUSTOMER){
                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_ALL_AISLE_CHANCE)
                    routePlan = createFullRoute(agent, spawnPatch);
                else
                    routePlan = createSTTPRoute(agent, spawnPatch);
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_CHANCE_SERVICE) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CUSTOMER_SERVICE));
                    actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_SERVICE, this, agent, actions));
                }
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_CHANCE_FOOD) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                    actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                    actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < STTP_CHANCE_EAT_TABLE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                        actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD));
                        routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER){
                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_ALL_AISLE_CHANCE)
                    routePlan = createFullRoute(agent, spawnPatch);
                else
                    routePlan = createSTTPRoute(agent, spawnPatch);
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_CHANCE_SERVICE) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CUSTOMER_SERVICE));
                    actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_SERVICE, this, agent, actions));
                }
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_CHANCE_FOOD) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                    actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                    actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < MODERATE_CHANCE_EAT_TABLE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                        actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD));
                        routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER){
                if (leaderAgent == null) { // The current agent is the leader itself
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CUSTOMER_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_SERVICE, this, agent, actions));
                    }
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                        actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < COMPLETE_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD));
                            routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else{ // deviating or following
                    routePlan = createFollowingRoute(agent, leaderAgent, spawnPatch);
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER){
                if (leaderAgent == null) { // The current agent is the leader itself
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CUSTOMER_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_SERVICE, this, agent, actions));
                    }
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                        actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < HELP_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD));
                            routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else{ // deviating or following
                    routePlan = createFollowingRoute(agent, leaderAgent, spawnPatch);
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER){
                if (leaderAgent == null) { // The current agent is the leader itself
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CUSTOMER_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_SERVICE, this, agent, actions));
                    }
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                        actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < DUO_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD));
                            routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else{ // deviating or following
                    routePlan = createFollowingRoute(agent, leaderAgent, spawnPatch);
                }
            }

        }
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.CHECKOUT_GROCERIES_CUSTOMER));
        actions.add(new GroceryAction(GroceryAction.Name.LEAVE_BUILDING));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_HOME, this, agent, actions));

        this.currentRoutePlan = routePlan.listIterator();
    }

    public GroceryState setNextState() { // Set the next class in the route plan
        this.currentState = this.currentRoutePlan.next();

        return this.currentState;
    }

    public GroceryState setPreviousState(){
        this.currentState = this.currentRoutePlan.previous();

        return this.currentState;
    }

    public ListIterator<GroceryState> getCurrentRoutePlan() {
        return currentRoutePlan;
    }

    public GroceryState getCurrentState() {
        return currentState;
    }

    public void addUrgentRoute(GroceryState s){
        this.currentState = s;
    }

    public ArrayList<GroceryState> createSTTPRoute(GroceryAgent agent, Patch spawnPatch){
        ArrayList<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryAction> actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GREET_GUARD, spawnPatch, 0)); //TODO: Maybe remove this since interaction
        actions.add(new GroceryAction(GroceryAction.Name.GO_THROUGH_SCANNER, spawnPatch)); //TODO: Change patch destination and duration
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SECURITY, this, agent));
        int numProducts = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_PRODUCTS - MIN_PRODUCTS) + MIN_PRODUCTS;
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CART_AREA, spawnPatch));
        if (numProducts >= CART_THRESHOLD)
            actions.add(new GroceryAction(GroceryAction.Name.GET_CART, spawnPatch, 0));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_CART, this, agent));
        while (numProducts > 0) {
            int newCluster = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(GroceryState.NUM_CLUSTERS);
            actions = new ArrayList<>();
            switch (newCluster) {
                case 0 -> {
                    // All aisles entered once clusters added; same for specific
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.RIGHT_WALL_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, GroceryState.AisleCluster.RIGHT_WALL_CLUSTER));
                }
                case 1 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.TOP_WALL_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, GroceryState.AisleCluster.TOP_WALL_CLUSTER));
                }
                case 2 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                }
                case 3 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                }
                case 4 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                }
                case 5 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                }
                case 6 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_1_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_1_CLUSTER));
                }
                case 7 -> {

                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_2_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_2_CLUSTER));
                }
                case 8 -> {
                    boolean frozen = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
                    if (frozen)
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, spawnPatch));
                    else
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    if (frozen)
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    else
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));

                }
                case 9 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FRESH_1_2_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, GroceryState.AisleCluster.FRESH_1_2_CLUSTER));
                }
                case 10 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FRESH_2_FRONT_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, GroceryState.AisleCluster.FRESH_2_FRONT_CLUSTER));
                }
                default -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_MEAT, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_CLUSTER));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_MEAT, this, agent, actions, GroceryState.AisleCluster.MEAT_CLUSTER));
                }
            }
            numProducts--;
        }
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CHECKOUT));
        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_CHECKOUT));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PAY, this, agent, actions));
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.CHECKOUT, 0, 10));
        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CASHIER, 0, 10));
        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_BAGGER, 0, 10));
        routePlan.add(new GroceryState(GroceryState.Name.PAYING, this, agent, actions));
        return routePlan;
    }

    public ArrayList<GroceryState> createFullRoute(GroceryAgent agent, Patch spawnPatch){
        ArrayList<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryAction> actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GREET_GUARD, spawnPatch, 0)); //TODO: Maybe remove this since interaction
        actions.add(new GroceryAction(GroceryAction.Name.GO_THROUGH_SCANNER, spawnPatch)); //TODO: Change patch destination and duration
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SECURITY, this, agent));
        int numProducts = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_PRODUCTS - MIN_PRODUCTS) + MIN_PRODUCTS;
        int routeIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4); //4 Routes available
        GroceryState.AisleCluster[] route = GroceryState.createRoute(routeIndex);
        routeIndex = 0;
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CART_AREA, spawnPatch));
        if (numProducts >= CART_THRESHOLD)
            actions.add(new GroceryAction(GroceryAction.Name.GET_CART, spawnPatch, 0));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_CART, this, agent));
        while (numProducts > 0) {
            boolean newCluster = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
            actions = new ArrayList<>();
            switch (route[routeIndex].getID()) {
                case 0, 1 -> {
                    // All aisles entered once clusters added; same for specific
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                }
                case 2, 3, 4, 5 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, route[routeIndex]));
                }
                case 6, 7 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, route[routeIndex]));
                }
                case 8 -> {
                    boolean frozen = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
                    if (frozen)
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, spawnPatch));
                    else
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    if (frozen)
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, route[routeIndex]));
                    else
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, route[routeIndex]));

                }
                case 9, 10 -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, route[routeIndex]));
                }
                default -> {
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_MEAT, spawnPatch));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions.add(new GroceryAction(GroceryAction.Name.FIND_PRODUCTS, 0));
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 0, 10));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_MEAT, this, agent, actions, route[routeIndex]));
                }
            }
            if (newCluster && routeIndex + 1 < GroceryState.NUM_CLUSTERS)
                routeIndex++;
            numProducts--;
        }
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CHECKOUT));
        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_CHECKOUT));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PAY, this, agent, actions));
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.CHECKOUT, 0, 10));
        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_CASHIER, 0, 10));
        actions.add(new GroceryAction(GroceryAction.Name.TALK_TO_BAGGER, 0, 10));
        routePlan.add(new GroceryState(GroceryState.Name.PAYING, this, agent, actions));
        return routePlan;
    }
    public ArrayList<GroceryState> createFollowingRoute(GroceryAgent agent, GroceryAgent leaderAgent, Patch spawnPatch){
        ArrayList<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryAction> actions = new ArrayList<>();
        //TODO: Deviating is randomized and is only added through the GrocerySimulator
        ListIterator<GroceryState> leaderRoutePlan = leaderAgent.getAgentMovement().getRoutePlan().getCurrentRoutePlan();
        actions.add(new GroceryAction(GroceryAction.Name.GREET_GUARD, spawnPatch, 0)); //TODO: Maybe remove this since interaction
        actions.add(new GroceryAction(GroceryAction.Name.GO_THROUGH_SCANNER, spawnPatch)); //TODO: Change patch destination and duration
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SECURITY, this, agent));
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.FOLLOW_LEADER_SHOP, leaderAgent, 0));
        routePlan.add(new GroceryState(GroceryState.Name.FOLLOW_LEADER_SHOP, this, agent, actions));

        //TODO Make sure that leaderRoutePlan has eating food before adding this
        while (leaderRoutePlan.hasNext()){
            GroceryState state = leaderRoutePlan.next();
            if (state.getName() == GroceryState.Name.GOING_TO_SERVICE){
                actions = new ArrayList<>();
                actions.add(new GroceryAction(GroceryAction.Name.FOLLOW_LEADER_SERVICE, leaderAgent, 0));
                routePlan.add(new GroceryState(GroceryState.Name.FOLLOW_LEADER_SERVICE, this, agent, actions));
            }
            else if (state.getName() == GroceryState.Name.GOING_TO_EAT){
                actions.add(new GroceryAction(GroceryAction.Name.FOLLOW_LEADER_EAT, leaderAgent, 0));
                actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT, leaderAgent));
                routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
            }
            else if (state.getName() == GroceryState.Name.EATING){
                actions = new ArrayList<>();
                actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD, leaderAgent));
                routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
            }
        }
        return routePlan;
    }
}