package com.socialsim.model.core.agent.grocery;

import java.util.ArrayList;

public class GroceryState {

    public enum Name {
        GOING_TO_SECURITY, GOING_CART, NEEDS_HELP,
        GOING_TO_PRODUCTS, IN_PRODUCTS_AISLE, IN_PRODUCTS_WALL, IN_PRODUCTS_FROZEN, IN_PRODUCTS_FRESH, IN_PRODUCTS_MEAT,
        FOLLOW_LEADER_SHOP, FOLLOW_LEADER_SERVICE,
        GOING_TO_PAY, GOING_TO_SERVICE, GOING_TO_EAT, EATING,
        GOING_HOME, GUARD_ENTRANCE, GUARD_EXIT, BUTCHER, CASHIER, BAGGER, CUSTOMER_SERVICE, STAFF_FOOD, NEEDS_BATHROOM, STAFF_AISLE,
        WAIT_INFRONT_OF_BATHROOM
    }

    public enum AisleCluster {
        RIGHT_WALL_CLUSTER(0),
        TOP_WALL_CLUSTER(1),
        AISLE_1_2_CLUSTER(2),
        AISLE_2_3_CLUSTER(3),
        AISLE_3_4_CLUSTER(4),
        AISLE_4_FRONT_CLUSTER(5),
        FROZEN_1_CLUSTER(6),
        FROZEN_2_CLUSTER(7),
        FROZEN_3_AISLE_CLUSTER(8),
        MEAT_AISLE_1_CLUSTER(9),
        MEAT_AISLE_FRONT_CLUSTER(10),
        MEAT_CLUSTER(11),
        NEEDS_BATHROOM(12);

        public final int ID;
        AisleCluster(int ID) {
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }
    }

    public static final int NUM_CLUSTERS = 12;

    private Name name;
    private GroceryRoutePlan routePlan;
    private GroceryAgent agent;
    private ArrayList<GroceryAction> actions;

    private AisleCluster aisleCluster;

    public GroceryState (GroceryState groceryState, GroceryRoutePlan routePlan, GroceryAgent agent) {
        this.name = groceryState.getName();
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = groceryState.getActions();

        if (groceryState.getAisleCluster() != null) {
            this.aisleCluster = groceryState.getAisleCluster();
        }
    }

    public GroceryState (Name a, GroceryRoutePlan routePlan, GroceryAgent agent, ArrayList<GroceryAction> actions, AisleCluster cluster) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.aisleCluster = cluster;
        this.actions = actions;
    }

    public GroceryState (Name a, GroceryRoutePlan routePlan, GroceryAgent agent, ArrayList<GroceryAction> actions) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = actions;
    }

    public AisleCluster getAisleCluster() {
        return aisleCluster;
    }

    public AisleCluster getAisleCluster(int id) {
        return AisleCluster.values()[id];
    }

    public void setAisleCluster(AisleCluster aisleCluster) {
        this.aisleCluster = aisleCluster;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public GroceryRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(GroceryRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public GroceryAgent getAgent() {
        return agent;
    }

    public void setAgent(GroceryAgent agent) {
        this.agent = agent;
    }

    public ArrayList<GroceryAction> getActions() {
        return this.actions;
    }

    public void addAction(GroceryAction a){
        actions.add(a);
    }

    public static AisleCluster[] route1 () {
        return new GroceryState.AisleCluster[] {
                AisleCluster.RIGHT_WALL_CLUSTER,
                AisleCluster.TOP_WALL_CLUSTER,
                AisleCluster.AISLE_1_2_CLUSTER,
                AisleCluster.AISLE_2_3_CLUSTER,
                AisleCluster.AISLE_3_4_CLUSTER,
                AisleCluster.AISLE_4_FRONT_CLUSTER,
                AisleCluster.FROZEN_1_CLUSTER,
                AisleCluster.FROZEN_2_CLUSTER,
                AisleCluster.FROZEN_3_AISLE_CLUSTER,
                AisleCluster.MEAT_AISLE_1_CLUSTER,
                AisleCluster.MEAT_AISLE_FRONT_CLUSTER,
                AisleCluster.MEAT_CLUSTER};
    }

    public static AisleCluster[] route2 () {
        return new GroceryState.AisleCluster[] {
                AisleCluster.AISLE_4_FRONT_CLUSTER,
                AisleCluster.AISLE_3_4_CLUSTER,
                AisleCluster.AISLE_2_3_CLUSTER,
                AisleCluster.AISLE_1_2_CLUSTER,
                AisleCluster.RIGHT_WALL_CLUSTER,
                AisleCluster.TOP_WALL_CLUSTER,
                AisleCluster.FROZEN_1_CLUSTER,
                AisleCluster.FROZEN_2_CLUSTER,
                AisleCluster.FROZEN_3_AISLE_CLUSTER,
                AisleCluster.MEAT_AISLE_1_CLUSTER,
                AisleCluster.MEAT_AISLE_FRONT_CLUSTER,
                AisleCluster.MEAT_CLUSTER};
    }

    public static AisleCluster[] route3 () {
        return new GroceryState.AisleCluster[] {
                AisleCluster.AISLE_4_FRONT_CLUSTER,
                AisleCluster.MEAT_AISLE_FRONT_CLUSTER,
                AisleCluster.MEAT_AISLE_1_CLUSTER,
                AisleCluster.AISLE_3_4_CLUSTER,
                AisleCluster.AISLE_2_3_CLUSTER,
                AisleCluster.AISLE_1_2_CLUSTER,
                AisleCluster.RIGHT_WALL_CLUSTER,
                AisleCluster.TOP_WALL_CLUSTER,
                AisleCluster.FROZEN_1_CLUSTER,
                AisleCluster.FROZEN_2_CLUSTER,
                AisleCluster.FROZEN_3_AISLE_CLUSTER,
                AisleCluster.MEAT_CLUSTER};
    }

//    public static AisleCluster[] route4 () {
//        return new GroceryState.AisleCluster[] {
//                AisleCluster.MEAT_AISLE_FRONT_CLUSTER,
//                AisleCluster.MEAT_AISLE_1_CLUSTER,
//                AisleCluster.MEAT_CLUSTER,
//                AisleCluster.FROZEN_3_AISLE_CLUSTER,
//                AisleCluster.FROZEN_2_CLUSTER,
//                AisleCluster.FROZEN_1_CLUSTER,
//                AisleCluster.TOP_WALL_CLUSTER,
//                AisleCluster.AISLE_1_2_CLUSTER,
//                AisleCluster.AISLE_2_3_CLUSTER,
//                AisleCluster.AISLE_3_4_CLUSTER,
//                AisleCluster.AISLE_4_FRONT_CLUSTER,
//                AisleCluster.RIGHT_WALL_CLUSTER};
//    }

    public static AisleCluster[] createRoute (int route) {
        switch (route) {
            case 0 -> {
                return route1();
            }
            case 1 -> {
                return route2();
            }
            default -> {
                return route3();
            }
//            default -> {
//                return route4();
//            }
        }
    }

}