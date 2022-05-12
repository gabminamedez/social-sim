package com.socialsim.model.core.agent.mall;

import com.socialsim.controller.Main;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.StoreAisle;
import com.socialsim.model.simulator.Simulator;

import java.util.ArrayList;

public class MallRoutePlan {

    private MallState currentState;
    private ArrayList<MallState> routePlan;
    private int MAX_BATHROOM = 2;
    public int MAX_STORE_HELP = 1;

    private static final int MIN_ERRAND_PRODUCTS = 1;
    private static final int MIN_LOITER_PRODUCTS = 7;
    private static final int MAX_PRODUCTS = 9;

    public static final int EFAMILY_RESTO_CHANCE = 100, EFAMILY_WANDERING_CHANCE = 30;
    public static final int LFAMILY_RESTO_CHANCE = 100, LFAMILY_WANDERING_CHANCE = 100;
    public static final int EFRIENDS_RESTO_CHANCE = 50, EFRIENDS_WANDERING_CHANCE = 80;
    public static final int LFRIENDS_RESTO_CHANCE = 80, LFRIENDS_WANDERING_CHANCE = 100;
    public static final int LCOUPLE_RESTO_CHANCE = 100, LCOUPLE_WANDERING_CHANCE = 100;
    public static final int EALONE_RESTO_CHANCE = 30, EALONE_WANDERING_CHANCE = 15;
    public static final int LALONE_RESTO_CHANCE = 80, LALONE_WANDERING_CHANCE = 100;
    public static final int BATHROOM_CHANCE = 10, STORE_HELP_CHANCE_LOITER = 8, STORE_HELP_CHANCE_ERRAND = 2;
    public static final int CHANCE_GUARD_INTERACT = 50, CHANCE_GUARD_VERBAL = 10;

    private static ArrayList<StoreAisle> aisles1 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles2 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles3 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles4 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles5 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles6 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles7 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles8 = new ArrayList<>();
    private static ArrayList<StoreAisle> aisles9 = new ArrayList<>();

    static {
        aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(0));
        aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(1));
        for (int i = 27; i < 32; i++) {
            aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
        }

        aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(32));
        aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(33));
        for (int i = 2; i < 6; i++) {
            aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
        }

        for (int i = 6; i < 12; i++) {
            aisles3.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
        }

        aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(34));
        aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(35));
        for (int i = 12; i < 16; i++) {
            aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
        }

        aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(16));
        aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(17));
        aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(36));
        aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(37));

        aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(18));
        aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(19));
        aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(38));
        aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(39));

        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(20));
        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(21));
        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(22));
        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(40));
        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(41));
        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(42));
        aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(43));

        aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(23));
        aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(24));
        aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(44));
        aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(45));

        aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(25));
        aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(26));
        aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(46));
        aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(47));
    }

    public MallRoutePlan(MallAgent agent, MallAgent leaderAgent, Mall mall, Patch spawnPatch, int team) {
        this.routePlan = new ArrayList<>();
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
            actions.add(new MallAction(MallAction.Name.STAFF_RESTO_SERVE, 12, 24));
            routePlan.add(new MallState(MallState.Name.STAFF_RESTO, this, agent, actions));
        }
        else if (agent.getPersona() == MallAgent.Persona.STAFF_STORE_SALES) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.STAFF_STORE_STATION, 120, 180));
            routePlan.add(new MallState(MallState.Name.STAFF_STORE_SALES, this, agent, actions));
        }
        else if (agent.getPersona() == MallAgent.Persona.STAFF_STORE_CASHIER) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.STAFF_STORE_STATION, spawnPatch));
            routePlan.add(new MallState(MallState.Name.STAFF_STORE_CASHIER, this, agent, actions));
        }
        else {
            if (agent.getPersona() == MallAgent.Persona.ERRAND_FAMILY) {
                if (leaderAgent == null) {
                    routePlan = createErrandRoute(agent, mall, EFAMILY_RESTO_CHANCE, EFAMILY_WANDERING_CHANCE);
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.LOITER_FAMILY) {
                if (leaderAgent == null) {
                    routePlan = createLoiterRoute(agent, mall, LFAMILY_RESTO_CHANCE, LFAMILY_WANDERING_CHANCE);
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.ERRAND_FRIENDS) {
                if (leaderAgent == null) {
                    routePlan = createErrandRoute(agent, mall, EFRIENDS_RESTO_CHANCE, EFRIENDS_WANDERING_CHANCE);
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.LOITER_FRIENDS) {
                if (leaderAgent == null) {
                    routePlan = createLoiterRoute(agent, mall, LFRIENDS_RESTO_CHANCE, LFRIENDS_WANDERING_CHANCE);
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.LOITER_COUPLE) {
                if (leaderAgent == null) {
                    routePlan = createLoiterRoute(agent, mall, LCOUPLE_RESTO_CHANCE, LCOUPLE_WANDERING_CHANCE);
                }
                else {
                    routePlan = createFollowingRoute(agent, leaderAgent);
                }
            }
            else if (agent.getPersona() == MallAgent.Persona.ERRAND_ALONE) {
                routePlan = createErrandRoute(agent, mall, EALONE_RESTO_CHANCE, EALONE_WANDERING_CHANCE);
            }
            else if (agent.getPersona() == MallAgent.Persona.LOITER_ALONE) {
                routePlan = createLoiterRoute(agent, mall, LALONE_RESTO_CHANCE, LALONE_WANDERING_CHANCE);
            }
        }

        if (leaderAgent == null) {
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.LEAVE_BUILDING));
            routePlan.add(new MallState(MallState.Name.GOING_HOME, this, agent, actions));
        }

        setNextState(-1);
    }

    public void addUrgentRoute(MallState s) {
        this.currentState = s;
    }

    public MallState setNextState(int i) {
        this.currentState = this.routePlan.get(i+1);
        return this.currentState;
    }

    public MallState setPreviousState(int i) {
        this.currentState = this.routePlan.get(i-1);
        return this.currentState;
    }

    public ArrayList<MallState> getCurrentRoutePlan() {
        return this.routePlan;
    }

    public MallState getCurrentState() {
        return currentState;
    }

    public ArrayList<MallState> createErrandRoute(MallAgent agent, Mall mall, int RESTO_CHANCE, int WANDERING_CHANCE) {
        ArrayList<MallState> routePlan = new ArrayList<>();

        ArrayList<MallAction> actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new MallAction(MallAction.Name.GO_THROUGH_SCANNER, (MallAgent) null, 2));
        routePlan.add(new MallState(MallState.Name.GOING_TO_SECURITY, this, agent, actions));

        boolean willEatBefore = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
        int numProducts = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_PRODUCTS - MIN_ERRAND_PRODUCTS) + MIN_ERRAND_PRODUCTS;

        if (willEatBefore) {
            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < RESTO_CHANCE) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_RESTAURANT));
                routePlan.add(new MallState(MallState.Name.GOING_TO_RESTO, this, agent, actions));
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 360, 1080));
                routePlan.add(new MallState(MallState.Name.IN_RESTO, this, agent, actions));
            }

            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_BATHROOM));
                actions.add(new MallAction(MallAction.Name.RELIEVE_IN_CUBICLE,12,36));
                actions.add(new MallAction(MallAction.Name.WASH_IN_SINK, (MallAgent) null, 12));
                routePlan.add(new MallState(MallState.Name.NEEDS_BATHROOM,this, agent, actions));
                MAX_BATHROOM -= 1;
            }
        }

        MallState.Shop[] route = null;
        int routeIndex = 0;
        if (willEatBefore) {
            boolean isLeft = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isLeft) {
                route = MallState.eatShopLeft();
            }
            else {
                route = MallState.eatShopRight();
            }
        }
        else {
            boolean isUp = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isUp) {
                route = MallState.shopEatUp();
            }
            else {
                route = MallState.shopEatDown();
            }
        }

        while (numProducts > 0) {
            StoreAisle randomAisle = null;
            boolean willCheckout = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            switch(route[routeIndex].getID()) {
                case 0 -> {
                    randomAisle = aisles1.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles1.size()));
                }
                case 1 -> {
                    randomAisle = aisles2.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles2.size()));
                }
                case 2 -> {
                    randomAisle = aisles3.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles3.size()));
                }
                case 3 -> {
                    randomAisle = aisles4.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles4.size()));
                }
                case 4 -> {
                    randomAisle = aisles5.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles5.size()));
                }
                case 5 -> {
                    randomAisle = aisles6.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles6.size()));
                }
                case 6 -> {
                    randomAisle = aisles7.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles7.size()));
                }
                case 7 -> {
                    randomAisle = aisles8.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles8.size()));
                }
                case 8 -> {
                    randomAisle = aisles9.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles9.size()));
                }
            }

            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
            actions = new ArrayList<>();
            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
            if (willCheckout) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.CHECKOUT_STORE, 12, 18));
                routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions, route[routeIndex].getID()));
            }

            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_BATHROOM));
                actions.add(new MallAction(MallAction.Name.RELIEVE_IN_CUBICLE,12,36));
                actions.add(new MallAction(MallAction.Name.WASH_IN_SINK, (MallAgent) null, 12));
                routePlan.add(new MallState(MallState.Name.NEEDS_BATHROOM,this, agent, actions));
                MAX_BATHROOM -= 1;
            }

            numProducts--;
            routeIndex++;
        }

        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        if (x < WANDERING_CHANCE) {
            for (int i = 0; i < 5; i++) {
                double isWander = Simulator.roll();
                if (isWander < 0.3) {
                    boolean isBench = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                    if (isBench) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.FIND_BENCH));
                        actions.add(new MallAction(MallAction.Name.SIT_ON_BENCH, 120, 240));
                        routePlan.add(new MallState(MallState.Name.WANDERING_AROUND, this, agent, actions));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.FIND_DIRECTORY));
                        actions.add(new MallAction(MallAction.Name.VIEW_DIRECTORY, 24, 48));
                        routePlan.add(new MallState(MallState.Name.WANDERING_AROUND, this, agent, actions));
                    }
                }
                else {
                    boolean isShowcase = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                    if (isShowcase) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_KIOSK));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SHOWCASE, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 12, 24));
                        routePlan.add(new MallState(MallState.Name.IN_SHOWCASE, this, agent, actions));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_KIOSK));
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 12, 24));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_DINING, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_DINING_AREA));
                        actions.add(new MallAction(MallAction.Name.DINING_AREA_STAY_PUT, 120, 360));
                        routePlan.add(new MallState(MallState.Name.IN_DINING, this, agent, actions));
                    }
                }

                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_BATHROOM));
                    actions.add(new MallAction(MallAction.Name.RELIEVE_IN_CUBICLE,12,36));
                    actions.add(new MallAction(MallAction.Name.WASH_IN_SINK, (MallAgent) null, 12));
                    routePlan.add(new MallState(MallState.Name.NEEDS_BATHROOM,this, agent, actions));
                    MAX_BATHROOM -= 1;
                }
            }
        }

        if (!willEatBefore) {
            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < RESTO_CHANCE) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_RESTAURANT));
                routePlan.add(new MallState(MallState.Name.GOING_TO_RESTO, this, agent, actions));
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 360, 1080));
                routePlan.add(new MallState(MallState.Name.IN_RESTO, this, agent, actions));
            }
        }

        return routePlan;
    }

    public ArrayList<MallState> createLoiterRoute(MallAgent agent, Mall mall, int RESTO_CHANCE, int WANDERING_CHANCE) {
        ArrayList<MallState> routePlan = new ArrayList<>();

        ArrayList<MallAction> actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GOING_TO_SECURITY_QUEUE));
        actions.add(new MallAction(MallAction.Name.GO_THROUGH_SCANNER, (MallAgent) null, 2));
        routePlan.add(new MallState(MallState.Name.GOING_TO_SECURITY, this, agent, actions));

        boolean willEatBefore = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
        int numProducts = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_PRODUCTS - MIN_LOITER_PRODUCTS) + MIN_ERRAND_PRODUCTS;

        if (willEatBefore) {
            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < RESTO_CHANCE) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_RESTAURANT));
                routePlan.add(new MallState(MallState.Name.GOING_TO_RESTO, this, agent, actions));
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 360, 1080));
                routePlan.add(new MallState(MallState.Name.IN_RESTO, this, agent, actions));
            }

            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_BATHROOM));
                actions.add(new MallAction(MallAction.Name.RELIEVE_IN_CUBICLE,12,36));
                actions.add(new MallAction(MallAction.Name.WASH_IN_SINK, (MallAgent) null, 12));
                routePlan.add(new MallState(MallState.Name.NEEDS_BATHROOM,this, agent, actions));
                MAX_BATHROOM -= 1;
            }
        }

        MallState.Shop[] route = null;
        int routeIndex = 0;
        if (willEatBefore) {
            boolean isLeft = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isLeft) {
                route = MallState.eatShopLeft();
            }
            else {
                route = MallState.eatShopRight();
            }
        }
        else {
            boolean isUp = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isUp) {
                route = MallState.shopEatUp();
            }
            else {
                route = MallState.shopEatDown();
            }
        }

        while (numProducts > 0) {
            StoreAisle randomAisle = null;
            boolean willCheckout = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            switch(route[routeIndex].getID()) {
                case 0 -> {
                    for (int i = 0; i < aisles1.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles1.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles1.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 1 -> {
                    for (int i = 0; i < aisles2.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles2.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles2.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 2 -> {
                    for (int i = 0; i < aisles3.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles3.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles3.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 3 -> {
                    for (int i = 0; i < aisles4.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles4.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles4.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 4 -> {
                    for (int i = 0; i < aisles5.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles5.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles5.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 5 -> {
                    for (int i = 0; i < aisles6.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles6.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles6.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 6 -> {
                    for (int i = 0; i < aisles7.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles7.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles7.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 7 -> {
                    for (int i = 0; i < aisles8.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles8.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles8.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
                case 8 -> {
                    for (int i = 0; i < aisles9.size(); i++) {
                        if (i == 0) {
                            randomAisle = aisles9.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_STORE, randomPatch));
                            routePlan.add(new MallState(MallState.Name.GOING_TO_STORE, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                        else {
                            randomAisle = aisles9.get(i);
                            Patch randomPatch = randomAisle.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(randomAisle.getAttractors().size())).getPatch();
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_TO_AISLE, randomPatch));
                            actions.add(new MallAction(MallAction.Name.CHECK_AISLE, 36, 96));
                            routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions));
                        }
                    }
                }
            }

            if (willCheckout) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.CHECKOUT_STORE, 12, 18));
                routePlan.add(new MallState(MallState.Name.IN_STORE, this, agent, actions, route[routeIndex].getID()));
            }

            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_BATHROOM));
                actions.add(new MallAction(MallAction.Name.RELIEVE_IN_CUBICLE,12,36));
                actions.add(new MallAction(MallAction.Name.WASH_IN_SINK, (MallAgent) null, 12));
                routePlan.add(new MallState(MallState.Name.NEEDS_BATHROOM,this, agent, actions));
                MAX_BATHROOM -= 1;
            }

            numProducts--;
            routeIndex++;
        }

        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        if (x < WANDERING_CHANCE) {
            for (int i = 0; i < 5; i++) {
                boolean isWander = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                if (isWander) {
                    boolean isBench = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                    if (isBench) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.FIND_BENCH));
                        actions.add(new MallAction(MallAction.Name.SIT_ON_BENCH, 120, 360));
                        routePlan.add(new MallState(MallState.Name.WANDERING_AROUND, this, agent, actions));
                    }
                    else {
                        boolean isConcierge = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                        if(isConcierge){
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.GO_CONCIERGE));
                            actions.add(new MallAction(MallAction.Name.ASK_CONCIERGE, 24, 48));
                            routePlan.add(new MallState(MallState.Name.WANDERING_AROUND, this, agent, actions));
                        }else{
                            actions = new ArrayList<>();
                            actions.add(new MallAction(MallAction.Name.FIND_DIRECTORY));
                            actions.add(new MallAction(MallAction.Name.VIEW_DIRECTORY, 24, 48));
                            routePlan.add(new MallState(MallState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                    }
                }
                else {
                    boolean isShowcase = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                    if (isShowcase) {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_KIOSK));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SHOWCASE, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 12, 24));
                        routePlan.add(new MallState(MallState.Name.IN_SHOWCASE, this, agent, actions));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_KIOSK));
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 12, 24));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_DINING, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_DINING_AREA));
                        actions.add(new MallAction(MallAction.Name.DINING_AREA_STAY_PUT, 120, 360));
                        routePlan.add(new MallState(MallState.Name.IN_DINING, this, agent, actions));
                    }
                }

                x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                if (x < BATHROOM_CHANCE && MAX_BATHROOM != 0) {
                    actions = new ArrayList<>();
                    actions.add(new MallAction(MallAction.Name.GO_TO_BATHROOM));
                    actions.add(new MallAction(MallAction.Name.RELIEVE_IN_CUBICLE,12,36));
                    actions.add(new MallAction(MallAction.Name.WASH_IN_SINK, (MallAgent) null, 12));
                    routePlan.add(new MallState(MallState.Name.NEEDS_BATHROOM,this, agent, actions));
                    MAX_BATHROOM -= 1;
                }
            }
        }

        if (!willEatBefore) {
            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < RESTO_CHANCE) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_RESTAURANT));
                routePlan.add(new MallState(MallState.Name.GOING_TO_RESTO, this, agent, actions));
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 24, 48));
                routePlan.add(new MallState(MallState.Name.IN_RESTO, this, agent, actions));
            }
        }

        return routePlan;
    }

    public ArrayList<MallState> createFollowingRoute(MallAgent agent, MallAgent leaderAgent) {
        ArrayList<MallState> routePlan = new ArrayList<>();
        ArrayList<MallState> leaderRoutePlan = leaderAgent.getAgentMovement().getRoutePlan().getCurrentRoutePlan();

        for (MallState mallState : leaderRoutePlan) {
            routePlan.add(new MallState(mallState, this, agent));
        }

        return routePlan;
    }
    public MallState addWaitingRoute(MallAgent agent){
        ArrayList<MallAction> actions;
        actions = new ArrayList<>();
        actions.add(new MallAction(MallAction.Name.GO_TO_WAIT_AREA));
        actions.add(new MallAction(MallAction.Name.WAIT_FOR_VACANT,5,20));
        return new MallState(MallState.Name.WAIT_INFRONT_OF_BATHROOM,this, agent, actions);
    }

}