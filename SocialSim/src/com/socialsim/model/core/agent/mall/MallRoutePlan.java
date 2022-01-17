package com.socialsim.model.core.agent.mall;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.simulator.Simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MallRoutePlan {

    private ListIterator<MallState> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private MallState currentState; // Denotes the current class of the amenity/patchfield in the route plan
    private ArrayList<MallState> routePlan;

    private static final int MIN_PRODUCTS = 2;
    private static final int MAX_PRODUCTS = 20;

    public static final int STTP_ALL_AISLE_CHANCE = 20, STTP_CHANCE_SERVICE = 0, STTP_CHANCE_FOOD = 20, STTP_CHANCE_EAT_TABLE = 10;
    public static final int MODERATE_ALL_AISLE_CHANCE = 40, MODERATE_CHANCE_SERVICE = 0, MODERATE_CHANCE_FOOD = 20, MODERATE_CHANCE_EAT_TABLE = 10;
    public static final int COMPLETE_FAMILY_ALL_AISLE_CHANCE = 60, COMPLETE_FAMILY_CHANCE_SERVICE = 20, COMPLETE_FAMILY_CHANCE_FOOD = 30, COMPLETE_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int HELP_FAMILY_ALL_AISLE_CHANCE = 50, HELP_FAMILY_CHANCE_SERVICE = 20, HELP_FAMILY_CHANCE_FOOD = 30, HELP_FAMILY_CHANCE_EAT_TABLE = 50;
    public static final int DUO_FAMILY_ALL_AISLE_CHANCE = 50, DUO_FAMILY_CHANCE_SERVICE = 20, DUO_FAMILY_CHANCE_FOOD = 30, DUO_FAMILY_CHANCE_EAT_TABLE = 50;

    public MallRoutePlan(MallAgent agent, MallAgent leaderAgent, Mall mall, Patch spawnPatch, int tickEntered) { //leaderAgent is only for agents that follow and deviate
        List<MallState> routePlan = new ArrayList<>();
        ArrayList<MallAction> actions;

        if (agent.getPersona() == MallAgent.Persona.GUARD) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.GUARD_STATION, spawnPatch));
            routePlan.add(new MallState(MallState.Name.GUARD, this, agent, actions));
        }
        else if (agent.getPersona() == MallAgent.Persona.STAFF_KIOSK) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.STAFF_KIOSK_STATION, spawnPatch));
            routePlan.add(new MallState(MallState.Name.STAFF_KIOSK, this, agent, actions));
        }
        else if (agent.getPersona() == MallAgent.Persona.STAFF_RESTO) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.STAFF_RESTO_SERVE, 60, 120));
            routePlan.add(new MallState(MallState.Name.STAFF_RESTO, this, agent, actions));
        }
        else if (agent.getPersona() == MallAgent.Persona.STAFF_STORE_SALES) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.STAFF_STORE_STATION, spawnPatch));
            routePlan.add(new MallState(MallState.Name.STAFF_STORE_SALES, this, agent, actions));
        }
        else if (agent.getPersona() == MallAgent.Persona.STAFF_STORE_CASHIER) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.STAFF_STORE_STATION, spawnPatch));
            routePlan.add(new MallState(MallState.Name.STAFF_STORE_CASHIER, this, agent, actions));
        }
        else {
            if (agent.getPersona() == MallAgent.Persona.STTP_ALONE_CUSTOMER) {
                routePlan = createSTTPRoute(agent, spawnPatch, mall);
                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_CHANCE_SERVICE) {
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_CUSTOMER_SERVICE));
                    actions.add(new MallAction(MallAction.Name.QUEUE_SERVICE));
                    actions.add(new MallAction(MallAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_SERVICE, this, agent, actions));
                }
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < STTP_CHANCE_FOOD) {
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FOOD_STALL));
                    actions.add(new MallAction(MallAction.Name.QUEUE_FOOD));
                    actions.add(new MallAction(MallAction.Name.BUY_FOOD, 36, 96));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_EAT, this, agent, actions));
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < STTP_CHANCE_EAT_TABLE) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.FIND_SEAT_FOOD_COURT));
                        actions.add(new MallAction(MallAction.Name.EATING_FOOD, 180, 540));
                        routePlan.add(new MallState(MallState.Name.EATING, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.MODERATE_ALONE_CUSTOMER){
                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_ALL_AISLE_CHANCE)
                    routePlan = createFullRoute(agent, spawnPatch, mall);
                else
                    routePlan = createSTTPRoute(agent, spawnPatch, mall);
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_CHANCE_SERVICE) {
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_CUSTOMER_SERVICE));
                    actions.add(new MallAction(MallAction.Name.QUEUE_SERVICE));
                    actions.add(new MallAction(MallAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_SERVICE, this, agent, actions));
                }
                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < MODERATE_CHANCE_FOOD) {
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FOOD_STALL));
                    actions.add(new MallAction(MallAction.Name.QUEUE_FOOD));
                    actions.add(new MallAction(MallAction.Name.BUY_FOOD, 36, 96));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_EAT, this, agent, actions));
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < MODERATE_CHANCE_EAT_TABLE) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.FIND_SEAT_FOOD_COURT));
                        actions.add(new MallAction(MallAction.Name.EATING_FOOD, 180, 540));
                        routePlan.add(new MallState(MallState.Name.EATING, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.COMPLETE_FAMILY_CUSTOMER){
                if (leaderAgent == null) { // The current agent is the leader itself
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch, mall);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch, mall);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_CUSTOMER_SERVICE));
                        actions.add(new MallAction(MallAction.Name.QUEUE_SERVICE));
                        actions.add(new MallAction(MallAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SERVICE, this, agent, actions));
                    }
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < COMPLETE_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new MallAction(MallAction.Name.QUEUE_FOOD));
                        actions.add(new MallAction(MallAction.Name.BUY_FOOD, 36, 96));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < COMPLETE_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new MallAction(MallAction.Name.EATING_FOOD, 180, 540));
                            routePlan.add(new MallState(MallState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else{ // deviating or following
                    routePlan = createFollowingRoute2(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.HELP_FAMILY_CUSTOMER){
                if (leaderAgent == null) { // The current agent is the leader itself
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch, mall);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch, mall);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_CUSTOMER_SERVICE));
                        actions.add(new MallAction(MallAction.Name.QUEUE_SERVICE));
                        actions.add(new MallAction(MallAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SERVICE, this, agent, actions));
                    }
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < HELP_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new MallAction(MallAction.Name.QUEUE_FOOD));
                        actions.add(new MallAction(MallAction.Name.BUY_FOOD, 36, 96));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < HELP_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new MallAction(MallAction.Name.EATING_FOOD, 180, 540));
                            routePlan.add(new MallState(MallState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else{ // deviating or following
                    routePlan = createFollowingRoute2(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.DUO_FAMILY_CUSTOMER){
                if (leaderAgent == null) { // The current agent is the leader itself
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_ALL_AISLE_CHANCE)
                        routePlan = createFullRoute(agent, spawnPatch, mall);
                    else
                        routePlan = createSTTPRoute(agent, spawnPatch, mall);
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_CHANCE_SERVICE) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_CUSTOMER_SERVICE));
                        actions.add(new MallAction(MallAction.Name.QUEUE_SERVICE));
                        actions.add(new MallAction(MallAction.Name.WAIT_FOR_CUSTOMER_SERVICE, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SERVICE, this, agent, actions));
                    }
                    x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                    if (x < DUO_FAMILY_CHANCE_FOOD) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FOOD_STALL));
                        actions.add(new MallAction(MallAction.Name.QUEUE_FOOD));
                        actions.add(new MallAction(MallAction.Name.BUY_FOOD, 36, 96));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_EAT, this, agent, actions));
                        x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < DUO_FAMILY_CHANCE_EAT_TABLE) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.FIND_SEAT_FOOD_COURT));
                            actions.add(new MallAction(MallAction.Name.EATING_FOOD, 180, 540));
                            routePlan.add(new MallState(MallState.Name.EATING, this, agent, actions));
                        }
                    }
                }
                else{ // deviating or following
                    routePlan = createFollowingRoute2(agent, leaderAgent);
                }
            }

        }

        if (leaderAgent == null) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.GO_TO_RECEIPT));
            actions.add(new MallAction(MallAction.Name.CHECKOUT_GROCERIES_CUSTOMER, 6, 12));
            actions.add(new MallAction(MallAction.Name.LEAVE_BUILDING));
            routePlan.add(new MallState(MallState.Name.GOING_HOME, this, agent, actions));
        }

        setNextState(-1);
    }

    public void addUrgentRoute(MallState s){
        this.currentState = s;
    }

    public MallState setNextState(int i) { // Set the next class in the route plan
        // this.currentState = this.currentRoutePlan.next();
        this.currentState = this.routePlan.get(i+1);
        return this.currentState;
    }

    public MallState setPreviousState(int i) {
        // this.currentState = this.currentRoutePlan.previous();
        this.currentState = this.routePlan.get(i-1);
        return this.currentState;
    }

    public ArrayList<MallState> getCurrentRoutePlan() {
        return routePlan;
    }

    public MallState getCurrentState() {
        return currentState;
    }

    public ArrayList<MallState> createSTTPRoute(MallAgent agent, Patch spawnPatch, Mall mall) {
        ArrayList<MallState> routePlan = new ArrayList<>();
        ArrayList<MallAction> actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new MallAction(MallAction.Name.GO_THROUGH_SCANNER, (MallAgent) null, 2));
        routePlan.add(new MallState(MallState.Name.GOING_TO_SECURITY, this, agent, actions));
        int numProducts = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_PRODUCTS - MIN_PRODUCTS) + MIN_PRODUCTS;
        actions = new ArrayList<>();
        if (numProducts >= CART_THRESHOLD) {
            Patch randomCart = mall.getCartRepos().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getPatch();
            actions.add(new MallAction(MallAction.Name.GET_CART, randomCart, 2));
            routePlan.add(new MallState(MallState.Name.GOING_CART, this, agent, actions));
        }
        while (numProducts > 0) {
            int newCluster = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MallState.NUM_CLUSTERS);
            switch (newCluster) {
                case 0 -> {
                    Patch randomWall0 = mall.getProductWalls().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 10).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_PRODUCT_WALL, randomWall0));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.RIGHT_WALL_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_WALL, this, agent, actions, MallState.AisleCluster.RIGHT_WALL_CLUSTER));
                }
                case 1 -> {
                    Patch randomWall1 = mall.getProductWalls().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(10)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_PRODUCT_WALL, randomWall1));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.TOP_WALL_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_WALL, this, agent, actions, MallState.AisleCluster.TOP_WALL_CLUSTER));
                }
                case 2 -> {
                    Patch randomAisle2A = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomAisle2B = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                }
                case 3 -> {
                    Patch randomAisle3A = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomAisle3B = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle3A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle3B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                }
                case 4 -> {
                    Patch randomAisle4A = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomAisle4B = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 9).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle4A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle4B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));

                }
                case 5 -> {
                    Patch randomAisle5 = mall.getProductAisles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch randomShelf5 = mall.getProductShelves().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8) + 8).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomAisle5));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomShelf5));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                }
                case 6 -> {
                    Patch randomFrozen6 = mall.getFrozenWalls().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FROZEN, randomFrozen6));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FROZEN_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, MallState.AisleCluster.FROZEN_1_CLUSTER));
                }
                case 7 -> {
                    Patch randomFrozen7 = mall.getFrozenWalls().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FROZEN, randomFrozen7));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FROZEN_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, MallState.AisleCluster.FROZEN_2_CLUSTER));
                }
                case 8 -> {
                    boolean frozen = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
                    if (frozen) {
                        Patch randomFrozen8 = mall.getFrozenProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FROZEN, randomFrozen8));
                    }
                    else {
                        Patch randomFresh8 = mall.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FRESH, randomFresh8));
                    }
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    if (frozen) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 36));
                        routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, MallState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 60));
                        routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FRESH, this, agent, actions, MallState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    }
                }
                case 9 -> {
                    Patch randomFresh9 = mall.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FRESH, randomFresh9));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FRESH_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FRESH, this, agent, actions, MallState.AisleCluster.FRESH_1_2_CLUSTER));
                }
                case 10 -> {
                    Patch randomFresh10 = mall.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 2).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                    Patch randomShelf10 = mall.getProductShelves().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8) + 8).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FRESH, randomFresh10));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FRESH_2_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FRESH, this, agent, actions, MallState.AisleCluster.FRESH_2_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomShelf10));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FRESH_2_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.FRESH_2_FRONT_CLUSTER));
                }
                default -> {
                    Patch randomMeat11 = mall.getMeatSections().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_MEAT, randomMeat11));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.MEAT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 36, 96));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_MEAT, this, agent, actions, MallState.AisleCluster.MEAT_CLUSTER));
                }
            }
            numProducts--;
        }

        actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GO_TO_CHECKOUT));
        actions.add(new MallAction(MallAction.Name.QUEUE_CHECKOUT));
        actions.add(new MallAction(MallAction.Name.CHECKOUT, 12, 36));
        routePlan.add(new MallState(MallState.Name.GOING_TO_PAY, this, agent, actions));

        return routePlan;
    }

    public ArrayList<MallState> createFullRoute(MallAgent agent, Patch spawnPatch, Mall mall) {
        ArrayList<MallState> routePlan = new ArrayList<>();
        ArrayList<MallAction> actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new MallAction(MallAction.Name.GO_THROUGH_SCANNER, (MallAgent) null, 2));
        routePlan.add(new MallState(MallState.Name.GOING_TO_SECURITY, this, agent, actions));
        int routeIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4); // 4 Routes available
        int routeIndexFinal = routeIndex;
        MallState.AisleCluster[] route = MallState.createRoute(routeIndex);
        routeIndex = 0;

        actions = new ArrayList<>();
        Patch randomCart = mall.getCartRepos().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getPatch();
        actions.add(new MallAction(MallAction.Name.GET_CART, randomCart, 2));
        routePlan.add(new MallState(MallState.Name.GOING_CART, this, agent, actions));

        while (routeIndex < route.length) {
            switch (route[routeIndex].getID()) {
                case 0 -> {
                    List<Patch> walls0 = new ArrayList<>();
                    for (int i = 10; i < 14; i++) {
                        walls0.add(mall.getProductWalls().get(i).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch());
                    }

                    if (routeIndexFinal == 0 || routeIndexFinal == 1) {
                        for (int i = 3; i >= 0; i--) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_PRODUCT_WALL, walls0.get(i)));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                    else {
                        for (int i = 0; i < 4; i ++) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_PRODUCT_WALL, walls0.get(i)));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                }
                case 1 -> {
                    List<Patch> walls1 = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        walls1.add(mall.getProductWalls().get(i).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch());
                    }

                    if (routeIndexFinal == 0 || routeIndexFinal == 1) {
                        for (int i = 9; i >= 0; i--) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_PRODUCT_WALL, walls1.get(i)));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                    else {
                        for (int i = 0; i < 10; i ++) {
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_PRODUCT_WALL, walls1.get(i)));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                            routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_WALL, this, agent, actions, route[routeIndex]));
                        }
                    }
                }
                case 2 -> {
                    Patch aisle2A = mall.getProductAisles().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = mall.getProductShelves().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = mall.getProductAisles().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = mall.getProductShelves().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = mall.getProductAisles().get(2).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_1_2_CLUSTER));
                }
                case 3 -> {
                    Patch aisle2A = mall.getProductAisles().get(3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = mall.getProductShelves().get(2).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = mall.getProductAisles().get(4).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = mall.getProductShelves().get(3).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = mall.getProductAisles().get(5).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_2_3_CLUSTER));
                }
                case 4 -> {
                    Patch aisle2A = mall.getProductAisles().get(6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = mall.getProductShelves().get(4).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = mall.getProductAisles().get(7).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = mall.getProductShelves().get(5).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = mall.getProductAisles().get(8).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_3_4_CLUSTER));
                }
                case 5 -> {
                    Patch aisle2A = mall.getProductAisles().get(9).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2A = mall.getProductShelves().get(6).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2B = mall.getProductAisles().get(10).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();
                    Patch shelf2B = mall.getProductShelves().get(7).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    Patch aisle2C = mall.getProductAisles().get(11).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(20)).getPatch();

                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2A));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelf2B));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, aisle2C));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, MallState.AisleCluster.AISLE_4_FRONT_CLUSTER));
                }
                case 6 -> {
                    Patch frozen6 = mall.getFrozenWalls().get(0).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FROZEN, frozen6));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FROZEN_1_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, MallState.AisleCluster.FROZEN_1_CLUSTER));
                }
                case 7 -> {
                    Patch frozen7 = mall.getFrozenWalls().get(1).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FROZEN, frozen7));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FROZEN_2_CLUSTER));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 36));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, MallState.AisleCluster.FROZEN_2_CLUSTER));
                }
                case 8 -> {
                    boolean frozen = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) == 0;
                    if (frozen) {
                        Patch frozen8 = mall.getFrozenProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FROZEN, frozen8));
                    }
                    else {
                        Patch fresh8 = mall.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_FRESH, fresh8));
                    }
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, MallState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    if (frozen) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 36));
                        routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FROZEN, this, agent, actions, MallState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 60));
                        routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FRESH, this, agent, actions, MallState.AisleCluster.FROZEN_3_FRESH_1_CLUSTER));
                    }
                }
                case 9 -> {
                    Patch fresh9 = mall.getFreshProducts().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 2).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_FRESH, fresh9));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 24, 60));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_FRESH, this, agent, actions, route[routeIndex]));
                }
                case 10 -> {
                    List<Patch> shelves10 = new ArrayList<>();
                    for (int i = 8; i < 16; i++) {
                        shelves10.add(mall.getProductShelves().get(i).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch());
                    }

                    for (int i = 0; i < 8; i ++) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, shelves10.get(i)));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 12, 24));
                        routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_AISLE, this, agent, actions, route[routeIndex]));
                    }
                }
                default -> {
                    Patch meat11 = mall.getMeatSections().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2)).getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(8)).getPatch();
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_MEAT, meat11));
                    routePlan.add(new MallState(MallState.Name.GOING_TO_PRODUCTS, this, agent, actions, route[routeIndex]));
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.CHECK_PRODUCTS, 36, 96));
                    routePlan.add(new MallState(MallState.Name.IN_PRODUCTS_MEAT, this, agent, actions, route[routeIndex]));
                }
            }

            routeIndex++;
        }

        actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GO_TO_CHECKOUT));
        actions.add(new MallAction(MallAction.Name.QUEUE_CHECKOUT));
        actions.add(new MallAction(MallAction.Name.CHECKOUT, 12, 36));
        routePlan.add(new MallState(MallState.Name.GOING_TO_PAY, this, agent, actions));

        return routePlan;
    }

    public ArrayList<MallState> createFollowingRoute(MallAgent agent, MallAgent leaderAgent, Patch spawnPatch) {
        ArrayList<MallState> routePlan = new ArrayList<>();
        ArrayList<MallAction> actions = new ArrayList<>();
        //TODO: Deviating is randomized and is only added through the MallSimulator
        ListIterator<MallState> leaderRoutePlan = leaderAgent.getAgentMovement().getRoutePlan().getCurrentRoutePlan();
        actions.add(new MallAction(MallAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new MallAction(MallAction.Name.GO_THROUGH_SCANNER, (MallAgent) null, 2));
        routePlan.add(new MallState(MallState.Name.GOING_TO_SECURITY, this, agent, actions));
        actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.FOLLOW_LEADER_SHOP, leaderAgent, 0));
        routePlan.add(new MallState(MallState.Name.FOLLOW_LEADER_SHOP, this, agent, actions));

        //TODO Make sure that leaderRoutePlan has eating food before adding this
        while (leaderRoutePlan.hasNext()){
            MallState state = leaderRoutePlan.next();
            if (state.getName() == MallState.Name.GOING_TO_SERVICE){
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.FOLLOW_LEADER_SERVICE, leaderAgent, 0));
                routePlan.add(new MallState(MallState.Name.FOLLOW_LEADER_SERVICE, this, agent, actions));
            }
            else if (state.getName() == MallState.Name.GOING_TO_EAT){
                actions.add(new MallAction(MallAction.Name.FOLLOW_LEADER_EAT, leaderAgent, 0));
                actions.add(new MallAction(MallAction.Name.FIND_SEAT_FOOD_COURT, leaderAgent));
                routePlan.add(new MallState(MallState.Name.GOING_TO_EAT, this, agent, actions));
            }
            else if (state.getName() == MallState.Name.EATING){
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.EATING_FOOD, leaderAgent));
                routePlan.add(new MallState(MallState.Name.EATING, this, agent, actions));
            }
        }
        return routePlan;
    }

    public ArrayList<MallState> createFollowingRoute2(MallAgent agent, MallAgent leaderAgent) {
        ArrayList<MallState> routePlan = new ArrayList<>();
        ListIterator<MallState> leaderRoutePlan = leaderAgent.getAgentMovement().getRoutePlan().getCurrentRoutePlan();

        while (leaderRoutePlan.hasPrevious()) {
            leaderRoutePlan.previous();
        }

        while (leaderRoutePlan.hasNext()) {
            routePlan.add(new MallState(leaderRoutePlan.next(), this, agent));
        }

        while (leaderRoutePlan.hasPrevious()) {
            leaderRoutePlan.previous();
        }

        return routePlan;
    }

}