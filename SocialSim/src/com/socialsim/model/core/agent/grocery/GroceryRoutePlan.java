package com.socialsim.model.core.agent.grocery;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.simulator.Simulator;
import java.util.ArrayList;
import java.util.List;

public class GroceryRoutePlan {

    private ArrayList<GroceryState> routePlan;
    private GroceryState currentState;
    private int MAX_BATHROOM = 2;
    public int MAX_AISLE_HELP = 1;

    private static final int MAX_BUTCHER_STATION = 10;
    private static final int MIN_PRODUCTS = 2;
    private static final int MAX_PRODUCTS = 20;
    private static final int CART_THRESHOLD = 5;

    public static final int STTP_ALL_AISLE_CHANCE = 20, STTP_CHANCE_SERVICE = 20, STTP_CHANCE_FOOD = 35, STTP_CHANCE_EAT_TABLE = 10;
    public static final int MODERATE_ALL_AISLE_CHANCE = 40, MODERATE_CHANCE_SERVICE = 20, MODERATE_CHANCE_FOOD = 35, MODERATE_CHANCE_EAT_TABLE = 10;
    public static final int COMPLETE_FAMILY_ALL_AISLE_CHANCE = 60, COMPLETE_FAMILY_CHANCE_SERVICE = 20, COMPLETE_FAMILY_CHANCE_FOOD = 45, COMPLETE_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int HELP_FAMILY_ALL_AISLE_CHANCE = 50, HELP_FAMILY_CHANCE_SERVICE = 20, HELP_FAMILY_CHANCE_FOOD = 45, HELP_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int DUO_FAMILY_ALL_AISLE_CHANCE = 50, DUO_FAMILY_CHANCE_SERVICE = 20, DUO_FAMILY_CHANCE_FOOD = 45, DUO_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int AISLE_HELP_CHANCE_STTP = 2, AISLE_HELP_CHANCE_MODERATE = 6, AISLE_HELP_CHANCE_COMPLETE = 10, AISLE_HELP_CHANCE_HELP = 10, AISLE_HELP_CHANCE_DUO = 8;
    public static final int CHANCE_BAGGER_INTERACT = 30, CHANCE_GUARD_INTERACT = 50, CHANCE_GUARD_VERBAL = 10;
    public static final int BATHROOM_CHANCE = 10;

    public GroceryRoutePlan(GroceryAgent agent, GroceryAgent leaderAgent, Grocery grocery, Patch spawnPatch) {
        this.routePlan = new ArrayList<>();
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
            actions.add(new GroceryAction(GroceryAction.Name.STAFF_AISLE_ORGANIZE, 60, 120));
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
            if (agent.getPersona() == GroceryAgent.Persona.STTP_ALONE_CUSTOMER) {
                routePlan = createSTTPRoute(agent, spawnPatch, grocery);
                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_CHANCE_SERVICE) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                    actions.add(new GroceryAction(GroceryAction.Name.QUEUE_SERVICE));
                    actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                }

                int y = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (y < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                    actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                    actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                    routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                    MAX_BATHROOM -= 1;
                }

                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_CHANCE_FOOD) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                    actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                    actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD, 36, 96));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < STTP_CHANCE_EAT_TABLE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                        actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD, 180, 540));
                        routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_ALL_AISLE_CHANCE)
                    routePlan = createFullRoute(agent, spawnPatch, grocery);
                else
                    routePlan = createSTTPRoute(agent, spawnPatch, grocery);
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_CHANCE_SERVICE) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                    actions.add(new GroceryAction(GroceryAction.Name.QUEUE_SERVICE));
                    actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                }

                int y = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (y < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                    actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                    actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                    routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                    MAX_BATHROOM -= 1;
                }

                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_CHANCE_FOOD) {
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                    actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                    actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD, 36, 96));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < MODERATE_CHANCE_EAT_TABLE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                        actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD, 180, 540));
                        routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER) {
                if (leaderAgent == null) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch, grocery);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch, grocery);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                    }

                    int y = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (y < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                        actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                        actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                        routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                        MAX_BATHROOM -= 1;
                    }

                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                        actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD, 36, 96));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < COMPLETE_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD, 180, 540));
                            routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER) {
                if (leaderAgent == null) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch, grocery);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch, grocery);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                    }

                    int y = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (y < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                        actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                        actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                        routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                        MAX_BATHROOM -= 1;
                    }

                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                        actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD, 36, 96));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < HELP_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD, 180, 540));
                            routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER) {
                if (leaderAgent == null) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch, grocery);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch, grocery);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CUSTOMER_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_SERVICE));
                        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SERVICE, this, agent, actions));
                    }

                    int y = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (y < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                        actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                        actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                        routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                        MAX_BATHROOM -= 1;
                    }

                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_FOOD));
                        actions.add(new GroceryAction(GroceryAction.Name.BUY_FOOD, 36, 96));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < DUO_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new GroceryAction(GroceryAction.Name.EATING_FOOD, 180, 540));
                            routePlan.add(new GroceryState(GroceryState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }

        }

        if (leaderAgent == null) {
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_RECEIPT));
            actions.add(new GroceryAction(GroceryAction.Name.CHECKOUT_GROCERIES_CUSTOMER, 6, 12));
            actions.add(new GroceryAction(GroceryAction.Name.LEAVE_BUILDING));
            routePlan.add(new GroceryState(GroceryState.Name.GOING_HOME, this, agent, actions));
        }

        setNextState(-1);
    }

    public void addUrgentRoute(GroceryState s) {
        this.currentState = s;
    }

    public GroceryState setNextState(int i) {
        this.currentState = this.routePlan.get(i + 1);
        return this.currentState;
    }

    public GroceryState setPreviousState(int i) {
        this.currentState = this.routePlan.get(i-1);
        return this.currentState;
    }

    public ArrayList<GroceryState> getCurrentRoutePlan() {
        return this.routePlan;
    }

    public GroceryState getCurrentState() {
        return currentState;
    }

    public ArrayList<GroceryState> createSTTPRoute(GroceryAgent agent, Patch spawnPatch, Grocery grocery) {
        ArrayList<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryAction> actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new GroceryAction(GroceryAction.Name.GO_THROUGH_SCANNER, (GroceryAgent) null, 2));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SECURITY, this, agent, actions));

        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
            actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
            actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
            routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
            MAX_BATHROOM -= 1;
        }

        int numProducts = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_PRODUCTS - MIN_PRODUCTS) + MIN_PRODUCTS;
        actions = new ArrayList<>();
        if (numProducts >= CART_THRESHOLD) {
            Patch randomCart = grocery.getCartRepos().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getPatch();
            actions.add(new GroceryAction(GroceryAction.Name.GET_CART, randomCart, 2));
            routePlan.add(new GroceryState(GroceryState.Name.GOING_CART, this, agent, actions));
        }
        while (numProducts > 0) {
            int newCluster = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(GroceryState.NUM_CLUSTERS);
            switch (newCluster) {
                case 0 -> {
                    Patch randomWall0 = grocery.getProductWalls().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 10).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, randomWall0));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.RIGHT_WALL_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, GroceryState.AisleCluster.RIGHT_WALL_CLUSTER));
                }
                case 1 -> {
                    Patch randomWall1 = grocery.getProductWalls().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(10)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, randomWall1));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.TOP_WALL_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, GroceryState.AisleCluster.TOP_WALL_CLUSTER));
                }
                case 2 -> {
                    Patch randomAisle2A = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomAisle2B = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                }
                case 3 -> {
                    Patch randomAisle3A = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomAisle3B = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle3A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle3B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                }
                case 4 -> {
                    Patch randomAisle4A = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomAisle4B = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 9).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle4A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle4B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));

                }
                case 5 -> {
                    Patch randomAisle5 = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomShelf5 = grocery.getProductShelves().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 8).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch randomFresh5 = grocery.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(9)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomAisle5));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomShelf5));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, randomFresh5));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                }
                case 6 -> {
                    Patch randomFrozen6 = grocery.getFrozenWalls().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, randomFrozen6));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_1_CLUSTER));
                }
                case 7 -> {
                    Patch randomFrozen7 = grocery.getFrozenWalls().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, randomFrozen7));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_2_CLUSTER));
                }
                case 8 -> {
                    boolean frozen = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
                    if (frozen) {
                        Patch frozen8 = grocery.getFrozenProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, frozen8));
                    }
                    else {
                        Patch aisle3Meat = grocery.getProductAisles().get(10).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle3Meat));
                    }
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_AISLE_CLUSTER));
                    if (frozen) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 36));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_AISLE_CLUSTER));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_AISLE_CLUSTER));
                    }
                }
                case 9 -> {
                    Patch aisle3Meat = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 10).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle3Meat));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_1_CLUSTER));

                    Patch randomShelf10 = grocery.getProductShelves().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 13).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomShelf10));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_1_CLUSTER));
                }
                case 10 -> {
                    Patch aisle = grocery.getProductAisles().get(13).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                    Patch randomShelf10 = grocery.getProductShelves().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5) + 8).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, aisle));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomShelf10));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_FRONT_CLUSTER));
                }
                default -> {
                    Patch randomMeat11 = grocery.getMeatSections().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_MEAT, randomMeat11));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 36, 96));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_MEAT, this, agent, actions, GroceryState.AisleCluster.MEAT_CLUSTER));
                }
            }
            numProducts--;

            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                actions = new ArrayList<>();
                actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                MAX_BATHROOM -= 1;
            }
        }

        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CHECKOUT));
        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_CHECKOUT));
        actions.add(new GroceryAction(GroceryAction.Name.CHECKOUT, 12, 36));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PAY, this, agent, actions));

        return routePlan;
    }

    public ArrayList<GroceryState> createFullRoute(GroceryAgent agent, Patch spawnPatch, Grocery grocery) {
        ArrayList<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryAction> actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new GroceryAction(GroceryAction.Name.GO_THROUGH_SCANNER, (GroceryAgent) null, 2));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_SECURITY, this, agent, actions));

        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
            actions = new ArrayList<>();
            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
            actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
            actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
            routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
            MAX_BATHROOM -= 1;
        }

        int routeIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3);
        int routeIndexFinal = routeIndex;
        GroceryState.AisleCluster[] route = GroceryState.createRoute(routeIndex);
        routeIndex = 0;

        actions = new ArrayList<>();
        Patch randomCart = grocery.getCartRepos().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getPatch();
        actions.add(new GroceryAction(GroceryAction.Name.GET_CART, randomCart, 2));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_CART, this, agent, actions));

        while (routeIndex < route.length) {
            switch (route[routeIndex].getID()) {
                case 0 -> {
                    List<Patch> walls0 = new ArrayList<>();
                    for (int i = 10; i < 14; i++) {
                        walls0.add(grocery.getProductWalls().get(i).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch());
                    }

                    if (routeIndexFinal == 0 || routeIndexFinal == 1) {
                        for (int i = 3; i >= 0; i--) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, walls0.get(i)));
                            routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                    else {
                        for (int i = 0; i < 4; i ++) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, walls0.get(i)));
                            routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                }
                case 1 -> {
                    List<Patch> walls1 = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        walls1.add(grocery.getProductWalls().get(i).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch());
                    }

                    if (routeIndexFinal == 0 || routeIndexFinal == 1) {
                        for (int i = 9; i >= 0; i--) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, walls1.get(i)));
                            routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                    else {
                        for (int i = 0; i < 10; i ++) {
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.GO_TO_PRODUCT_WALL, walls1.get(i)));
                            routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                }
                case 2 -> {
                    Patch aisle2A = grocery.getProductAisles().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = grocery.getProductShelves().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = grocery.getProductAisles().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = grocery.getProductShelves().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = grocery.getProductAisles().get(2).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_1_2_CLUSTER));
                }
                case 3 -> {
                    Patch aisle2A = grocery.getProductAisles().get(3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = grocery.getProductShelves().get(2).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = grocery.getProductAisles().get(4).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = grocery.getProductShelves().get(3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = grocery.getProductAisles().get(5).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_2_3_CLUSTER));
                }
                case 4 -> {
                    Patch aisle2A = grocery.getProductAisles().get(6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = grocery.getProductShelves().get(4).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = grocery.getProductAisles().get(7).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = grocery.getProductShelves().get(5).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = grocery.getProductAisles().get(8).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_3_4_CLUSTER));
                }
                case 5 -> {
                    Patch aisle2A = grocery.getProductAisles().get(9).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = grocery.getProductShelves().get(6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch shelf2B = grocery.getProductShelves().get(7).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = grocery.getProductAisles().get(11).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomFresh5 = grocery.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(9)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FRESH, randomFresh5));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FRESH, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                }
                case 6 -> {
                    Patch frozen6 = grocery.getFrozenWalls().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, frozen6));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_1_CLUSTER));
                }
                case 7 -> {
                    Patch frozen7 = grocery.getFrozenWalls().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, frozen7));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_2_CLUSTER));
                }
                case 8 -> {
                    boolean frozen = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
                    if (frozen) {
                        Patch frozen8 = grocery.getFrozenProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_FROZEN, frozen8));
                    }
                    else {
                        Patch aisle3Meat = grocery.getProductAisles().get(10).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle3Meat));
                    }
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_AISLE_CLUSTER));
                    if (frozen) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 36));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_AISLE_CLUSTER));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.FROZEN_3_AISLE_CLUSTER));
                    }
                }
                case 9 -> {
                    Patch aisle3Meat = grocery.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 12).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, aisle3Meat));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, route[routeIndex]));

                    Patch randomShelf10 = grocery.getProductShelves().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 13).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, randomShelf10));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, GroceryState.AisleCluster.MEAT_AISLE_1_CLUSTER));
                }
                case 10 -> {
                    List<Patch> shelves10 = new ArrayList<>();
                    for (int i = 8; i < 13; i++) {
                        shelves10.add(grocery.getProductShelves().get(i).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch());
                    }

                    for (int i = 0; i < 5; i ++) {
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_AISLE, shelves10.get(i)));
                        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                        actions = new ArrayList<>();
                        actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 12, 24));
                        routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_AISLE, this, agent, actions, route[routeIndex]));
                    }
                }
                default -> {
                    Patch meat11 = grocery.getMeatSections().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.GO_TO_MEAT, meat11));
                    routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions = new ArrayList<>();
                    actions.add(new GroceryAction(GroceryAction.Name.CHECK_PRODUCTS, 36, 96));
                    routePlan.add(new GroceryState(GroceryState.Name.IN_PRODUCTS_MEAT, this, agent, actions, route[routeIndex]));
                }
            }

            routeIndex++;

            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                actions = new ArrayList<>();
                actions.add(new GroceryAction(GroceryAction.Name.GO_TO_BATHROOM));
                actions.add(new GroceryAction(GroceryAction.Name.RELIEVE_IN_CUBICLE,12,36));
                actions.add(new GroceryAction(GroceryAction.Name.WASH_IN_SINK, (GroceryAgent) null, 12));
                routePlan.add(new GroceryState(GroceryState.Name.NEEDS_BATHROOM,this, agent, actions));
                MAX_BATHROOM -= 1;
            }
        }

        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_CHECKOUT));
        actions.add(new GroceryAction(GroceryAction.Name.QUEUE_CHECKOUT));
        actions.add(new GroceryAction(GroceryAction.Name.CHECKOUT, 12, 36));
        routePlan.add(new GroceryState(GroceryState.Name.GOING_TO_PAY, this, agent, actions));

        return routePlan;
    }

    public ArrayList<GroceryState> createFollowingRoute(GroceryAgent agent, GroceryAgent leaderAgent) {
        ArrayList<GroceryState> routePlan = new ArrayList<>();
        ArrayList<GroceryState> leaderRoutePlan = leaderAgent.getAgentMovement().getRoutePlan().getCurrentRoutePlan();

        for (GroceryState mallState : leaderRoutePlan) {
            routePlan.add(new GroceryState(mallState, this, agent));
        }

        return routePlan;
    }
    public GroceryState addWaitingRoute(GroceryAgent agent){
        ArrayList<GroceryAction> actions;
        actions = new ArrayList<>();
        actions.add(new GroceryAction(GroceryAction.Name.GO_TO_WAIT_AREA));
        actions.add(new GroceryAction(GroceryAction.Name.WAIT_FOR_VACANT,5,20));
        return new GroceryState(GroceryState.Name.WAIT_INFRONT_OF_BATHROOM,this, agent, actions);
    }

}