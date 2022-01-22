package com.socialsim.model.core.agent.mall;

import com.socialsim.model.core.agent.grocery.GroceryState;

import java.util.ArrayList;

public class MallState {

    public enum Name {
        GOING_TO_SECURITY, WANDERING_AROUND, NEEDS_BATHROOM, GOING_HOME,
        GOING_TO_STORE, IN_STORE, GOING_TO_RESTO, IN_RESTO, GOING_TO_SHOWCASE, IN_SHOWCASE, GOING_TO_DINING, IN_DINING,
        GUARD, STAFF_KIOSK, STAFF_RESTO, STAFF_STORE_SALES, STAFF_STORE_CASHIER
    }

    public enum Shop {
        Store1(0),
        Store2(1),
        Store3(2),
        Store4(3),
        Store5(4),
        Store6(5),
        Store7(6),
        Store8(7),
        Store9(8),
        Store10(9),
        Store11(10),
        Store12(11),
        Store13(12);

        public final int ID;
        Shop(int ID) {
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }
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

    public static Shop[] eatShopLeft() {
        return new Shop[] {
                Shop.Store4,
                Shop.Store3,
                Shop.Store1,
                Shop.Store2,
                Shop.Store5,
                Shop.Store6,
                Shop.Store7,
                Shop.Store8,
                Shop.Store9,
                Shop.Store12,
                Shop.Store13,
                Shop.Store11,
                Shop.Store10
        };
    }

    public static Shop[] eatShopRight() {
        return new Shop[] {
                Shop.Store10,
                Shop.Store11,
                Shop.Store13,
                Shop.Store12,
                Shop.Store9,
                Shop.Store8,
                Shop.Store7,
                Shop.Store6,
                Shop.Store5,
                Shop.Store2,
                Shop.Store4,
                Shop.Store3,
                Shop.Store1
        };
    }

    public static Shop[] shopEatUp() {
        return new Shop[] {
                Shop.Store1,
                Shop.Store2,
                Shop.Store5,
                Shop.Store6,
                Shop.Store7,
                Shop.Store8,
                Shop.Store9,
                Shop.Store12,
                Shop.Store13,
                Shop.Store11,
                Shop.Store10,
                Shop.Store4,
                Shop.Store3
        };
    }

    public static Shop[] shopEatDown() {
        return new Shop[]{
                Shop.Store3,
                Shop.Store4,
                Shop.Store10,
                Shop.Store11,
                Shop.Store13,
                Shop.Store12,
                Shop.Store9,
                Shop.Store8,
                Shop.Store7,
                Shop.Store6,
                Shop.Store5,
                Shop.Store2,
                Shop.Store1
        };
    }

}