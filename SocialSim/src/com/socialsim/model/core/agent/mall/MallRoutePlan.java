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

    private static final int MIN_ERRAND_PRODUCTS = 2;
    private static final int MIN_LOITER_PRODUCTS = 8;
    private static final int MAX_PRODUCTS = 15;

    public static final int EFAMILY_RESTO_CHANCE = 100, EFAMILY_WANDERING_CHANCE = 30;
    public static final int LFAMILY_RESTO_CHANCE = 100, LFAMILY_WANDERING_CHANCE = 100;
    public static final int EFRIENDS_RESTO_CHANCE = 50, EFRIENDS_WANDERING_CHANCE = 80;
    public static final int LFRIENDS_RESTO_CHANCE = 80, LFRIENDS_WANDERING_CHANCE = 100;
    public static final int ECOUPLE_RESTO_CHANCE = 100, ECOUPLE_WANDERING_CHANCE = 100;
    public static final int LCOUPLE_RESTO_CHANCE = 100, LCOUPLE_WANDERING_CHANCE = 100;
    public static final int EALONE_RESTO_CHANCE = 30, EALONE_WANDERING_CHANCE = 15;
    public static final int LALONE_RESTO_CHANCE = 80, LALONE_WANDERING_CHANCE = 100;

    public MallRoutePlan(MallAgent agent, MallAgent leaderAgent, Mall mall, Patch spawnPatch, int tickEntered) { // leaderAgent is only for agents that follow and deviate
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
            else if (agent.getPersona() == MallAgent.Persona.ERRAND_COUPLE) {
                if (leaderAgent == null) {
                    routePlan = createErrandRoute(agent, mall, ECOUPLE_RESTO_CHANCE, ECOUPLE_WANDERING_CHANCE);
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
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 24, 48));
                routePlan.add(new MallState(MallState.Name.IN_RESTO, this, agent, actions));
            }
        }

        while (numProducts > 0) { // TODO: Shopping here
            numProducts--;
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
                        actions.add(new MallAction(MallAction.Name.SIT_ON_BENCH, 24, 48));
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
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SHOWCASE, this, agent, actions));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_KIOSK));
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_DINING, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_DINING_AREA));
                        actions.add(new MallAction(MallAction.Name.DINING_AREA_STAY_PUT, 24, 48));
                        routePlan.add(new MallState(MallState.Name.IN_DINING, this, agent, actions));
                    }
                }
            }
        }

        if (!willEatBefore) {
            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < RESTO_CHANCE) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_RESTAURANT));
                routePlan.add(new MallState(MallState.Name.GOING_TO_RESTO, this, agent, actions));
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 24, 48));
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
                actions.add(new MallAction(MallAction.Name.RESTAURANT_STAY_PUT, 24, 48));
                routePlan.add(new MallState(MallState.Name.IN_RESTO, this, agent, actions));
            }
        }

        while (numProducts > 0) { // TODO: Shopping here
            numProducts--;
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
                        actions.add(new MallAction(MallAction.Name.SIT_ON_BENCH, 24, 48));
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
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_SHOWCASE, this, agent, actions));
                    }
                    else {
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_KIOSK));
                        actions.add(new MallAction(MallAction.Name.QUEUE_KIOSK));
                        actions.add(new MallAction(MallAction.Name.CHECKOUT_KIOSK, 24, 48));
                        routePlan.add(new MallState(MallState.Name.GOING_TO_DINING, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new MallAction(MallAction.Name.GO_TO_DINING_AREA));
                        actions.add(new MallAction(MallAction.Name.DINING_AREA_STAY_PUT, 24, 48));
                        routePlan.add(new MallState(MallState.Name.IN_DINING, this, agent, actions));
                    }
                }
            }
        }

        if (!willEatBefore) {
            x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < RESTO_CHANCE) {
                actions = new ArrayList<>();
                actions.add(new MallAction(MallAction.Name.GO_TO_RESTAURANT));
                routePlan.add(new MallState(MallState.Name.GOING_TO_RESTO, this, agent, actions));
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

}