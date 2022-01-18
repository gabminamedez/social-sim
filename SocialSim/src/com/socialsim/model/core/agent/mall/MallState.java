package com.socialsim.model.core.agent.mall;

import java.util.ArrayList;

public class MallState {

    public enum Name {
        GOING_TO_SECURITY, WANDERING_AROUND, NEEDS_BATHROOM, GOING_HOME,
        GOING_TO_STORE, IN_STORE, GOING_TO_RESTO, IN_RESTO, GOING_TO_SHOWCASE, IN_SHOWCASE, GOING_TO_DINING, IN_DINING,
        GUARD, STAFF_KIOSK, STAFF_RESTO, STAFF_STORE_SALES, STAFF_STORE_CASHIER
    }

    private Name name;
    private MallRoutePlan routePlan;
    private MallAgent agent;
    private ArrayList<MallAction> actions;

    public MallState(MallState mallState, MallRoutePlan routePlan, MallAgent agent) {
        this.name = mallState.getName();
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = mallState.getActions();
    }

    public MallState(Name a, MallRoutePlan routePlan, MallAgent agent) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = new ArrayList<>();
    }

    public MallState(Name a, MallRoutePlan routePlan, MallAgent agent, ArrayList<MallAction> actions) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = actions;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public MallRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(MallRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public MallAgent getAgent() {
        return agent;
    }

    public void setAgent(MallAgent agent) {
        this.agent = agent;
    }

    public ArrayList<MallAction> getActions() {
        return this.actions;
    }

    public void addAction(MallAction a){
        actions.add(a);
    }

}