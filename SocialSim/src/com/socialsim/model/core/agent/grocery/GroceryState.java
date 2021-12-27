package com.socialsim.model.core.agent.grocery;

import com.socialsim.model.core.agent.university.UniversityAgent;

import java.util.ArrayList;

public class GroceryState {

    public enum Name{
        GOING_TO_SECURITY, GOING_CART, NEEDS_HELP, NEEDS_DRINK,
        GOING_TO_PRODUCTS, IN_PRODUCTS_AISLE, IN_PRODUCTS_WALL, IN_PRODUCTS_FROZEN, IN_PRODUCTS_FRESH, IN_PRODUCTS_MEAT,
        GOING_TO_PAY, PAYING, GOING_TO_SERVICE, IN_SERVICE, GOING_TO_EAT, EATING,
        GOING_HOME, GUARD_ENTRANCE, GUARD_EXIT, BUTCHER, CASHIER, BAGGER, CUSTOMER_SERVICE, STAFF_FOOD, STAFF_AISLE;
    }

    private Name name;
    private GroceryRoutePlan routePlan;
    private GroceryAgent agent;
    private ArrayList<GroceryAction> actions;

    private int aisleID;

    public GroceryState(Name a, GroceryRoutePlan routePlan, GroceryAgent agent){
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = new ArrayList<>();
    }

    public GroceryState(Name a, GroceryRoutePlan routePlan, GroceryAgent agent, int aisleID){
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.aisleID = aisleID;
        this.actions = new ArrayList<>();
    }

    public GroceryState(Name a, GroceryRoutePlan routePlan, GroceryAgent agent, ArrayList<GroceryAction> actions){
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = actions;
    }


    public int getAisleID() {
        return aisleID;
    }

    public void setAisleID(int aisleID) {
        this.aisleID = aisleID;
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

    public void addAction(GroceryAction a){
        actions.add(a);
    }
}
