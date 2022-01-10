package com.socialsim.model.simulator.grocery;

import com.socialsim.controller.Main;
import com.socialsim.controller.grocery.controls.GroceryScreenController;
import com.socialsim.controller.grocery.controls.GroceryScreenController;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAction;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAgentMovement;
import com.socialsim.model.core.agent.grocery.GroceryState;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAction;
import com.socialsim.model.core.agent.grocery.GroceryState;
import com.socialsim.model.core.agent.office.OfficeAction;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Couch;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class GrocerySimulator extends Simulator {

    private Grocery grocery;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    private int MAX_FAMILY = 10;
    private int MAX_ALONE = 10;

    public GrocerySimulator() {
        this.grocery = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(6, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start(); // Start the simulation thread, but in reality it would be activated much later
    }

    public Grocery getGrocery() {
        return grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
    }

    public AtomicBoolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public boolean isRunning() {
        return running.get();
    }

    public SimulationTime getSimulationTime() {
        return time;
    }

    public Semaphore getPlaySemaphore() {
        return playSemaphore;
    }

    public void resetToDefaultConfiguration(Grocery grocery) {
        this.grocery = grocery;
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(Grocery grocery) {
        GroceryAgent guard1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, null, null, null, grocery.getPatch(57,52), true, null, -1);
        grocery.getAgents().add(guard1);
        grocery.getAgentPatchSet().add(guard1.getAgentMovement().getCurrentPatch());
        GroceryAgent guard2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, null, null, null, grocery.getPatch(57,47), true, null, -1);
        grocery.getAgents().add(guard2);
        grocery.getAgentPatchSet().add(guard2.getAgentMovement().getCurrentPatch());

        GroceryAgent cashier1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,20), true, null, -1);
        grocery.getAgents().add(cashier1);
        grocery.getAgentPatchSet().add(cashier1.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,26), true, null, -1);
        grocery.getAgents().add(cashier2);
        grocery.getAgentPatchSet().add(cashier2.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,32), true, null, -1);
        grocery.getAgents().add(cashier3);
        grocery.getAgentPatchSet().add(cashier3.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,38), true, null, -1);
        grocery.getAgents().add(cashier4);
        grocery.getAgentPatchSet().add(cashier4.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,44), true, null, -1);
        grocery.getAgents().add(cashier5);
        grocery.getAgentPatchSet().add(cashier5.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,50), true, null, -1);
        grocery.getAgents().add(cashier6);
        grocery.getAgentPatchSet().add(cashier6.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,56), true, null, -1);
        grocery.getAgents().add(cashier7);
        grocery.getAgentPatchSet().add(cashier7.getAgentMovement().getCurrentPatch());
        GroceryAgent cashier8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CASHIER, null, null, null, grocery.getPatch(44,62), true, null, -1);
        grocery.getAgents().add(cashier8);
        grocery.getAgentPatchSet().add(cashier8.getAgentMovement().getCurrentPatch());

        GroceryAgent bagger1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,20), true, null, -1);
        grocery.getAgents().add(bagger1);
        grocery.getAgentPatchSet().add(bagger1.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,26), true, null, -1);
        grocery.getAgents().add(bagger2);
        grocery.getAgentPatchSet().add(bagger2.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,32), true, null, -1);
        grocery.getAgents().add(bagger3);
        grocery.getAgentPatchSet().add(bagger3.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,38), true, null, -1);
        grocery.getAgents().add(bagger4);
        grocery.getAgentPatchSet().add(bagger4.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,44), true, null, -1);
        grocery.getAgents().add(bagger5);
        grocery.getAgentPatchSet().add(bagger5.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,50), true, null, -1);
        grocery.getAgents().add(bagger6);
        grocery.getAgentPatchSet().add(bagger6.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,56), true, null, -1);
        grocery.getAgents().add(bagger7);
        grocery.getAgentPatchSet().add(bagger7.getAgentMovement().getCurrentPatch());
        GroceryAgent bagger8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BAGGER, null, null, null, grocery.getPatch(45,62), true, null, -1);
        grocery.getAgents().add(bagger8);
        grocery.getAgentPatchSet().add(bagger8.getAgentMovement().getCurrentPatch());

        GroceryAgent service1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, null, null, null, grocery.getPatch(44,4), true, null, -1);
        grocery.getAgents().add(service1);
        grocery.getAgentPatchSet().add(service1.getAgentMovement().getCurrentPatch());
        GroceryAgent service2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, null, null, null, grocery.getPatch(44,8), true, null, -1);
        grocery.getAgents().add(service2);
        grocery.getAgentPatchSet().add(service2.getAgentMovement().getCurrentPatch());
        GroceryAgent service3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER_SERVICE, null, null, null, grocery.getPatch(44,12), true, null, -1);
        grocery.getAgents().add(service3);
        grocery.getAgentPatchSet().add(service3.getAgentMovement().getCurrentPatch());

        GroceryAgent food1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,8), true, null, -1);
        grocery.getAgents().add(food1);
        grocery.getAgentPatchSet().add(food1.getAgentMovement().getCurrentPatch());
        GroceryAgent food2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,17), true, null, -1);
        grocery.getAgents().add(food2);
        grocery.getAgentPatchSet().add(food2.getAgentMovement().getCurrentPatch());
        GroceryAgent food3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,26), true, null, -1);
        grocery.getAgents().add(food3);
        grocery.getAgentPatchSet().add(food3.getAgentMovement().getCurrentPatch());
        GroceryAgent food4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,35), true, null, -1);
        grocery.getAgents().add(food4);
        grocery.getAgentPatchSet().add(food4.getAgentMovement().getCurrentPatch());
        GroceryAgent food5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,63), true, null, -1);
        grocery.getAgents().add(food5);
        grocery.getAgentPatchSet().add(food5.getAgentMovement().getCurrentPatch());
        GroceryAgent food6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,72), true, null, -1);
        grocery.getAgents().add(food6);
        grocery.getAgentPatchSet().add(food6.getAgentMovement().getCurrentPatch());
        GroceryAgent food7 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,81), true, null, -1);
        grocery.getAgents().add(food7);
        grocery.getAgentPatchSet().add(food7.getAgentMovement().getCurrentPatch());
        GroceryAgent food8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_FOOD, null, null, null, grocery.getPatch(58,90), true, null, -1);
        grocery.getAgents().add(food8);
        grocery.getAgentPatchSet().add(food8.getAgentMovement().getCurrentPatch());

        GroceryAgent butcher1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BUTCHER, null, null, null, grocery.getPatch(29,1), true, null, -1);
        grocery.getAgents().add(butcher1);
        grocery.getAgentPatchSet().add(butcher1.getAgentMovement().getCurrentPatch());
        GroceryAgent butcher2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.BUTCHER, null, null, null, grocery.getPatch(37,1), true, null, -1);
        grocery.getAgents().add(butcher2);
        grocery.getAgentPatchSet().add(butcher2.getAgentMovement().getCurrentPatch());

//        GroceryAgent aisle1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(3,15), true, null, -1);
//        grocery.getAgents().add(aisle1);
//        grocery.getAgentPatchSet().add(aisle1.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(3,42), true, null, -1);
//        grocery.getAgents().add(aisle2);
//        grocery.getAgentPatchSet().add(aisle2.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(3,69), true, null, -1);
//        grocery.getAgents().add(aisle3);
//        grocery.getAgentPatchSet().add(aisle3.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(18,95), true, null, -1);
//        grocery.getAgents().add(aisle4);
//        grocery.getAgentPatchSet().add(aisle4.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle5 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(44,95), true, null, -1);
//        grocery.getAgents().add(aisle5);
//        grocery.getAgentPatchSet().add(aisle5.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle6 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(10,4), true, null, -1);
//        grocery.getAgents().add(aisle6);
//        grocery.getAgentPatchSet().add(aisle6.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle8 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(12,29), true, null, -1);
//        grocery.getAgents().add(aisle8);
//        grocery.getAgentPatchSet().add(aisle8.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle9 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(18,29), true, null, -1);
//        grocery.getAgents().add(aisle9);
//        grocery.getAgentPatchSet().add(aisle9.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle10 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(24,29), true, null, -1);
//        grocery.getAgents().add(aisle10);
//        grocery.getAgentPatchSet().add(aisle10.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle11 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(30,29), true, null, -1);
//        grocery.getAgents().add(aisle11);
//        grocery.getAgentPatchSet().add(aisle11.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle12 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(32,31), true, null, -1);
//        grocery.getAgents().add(aisle12);
//        grocery.getAgentPatchSet().add(aisle12.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle13 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(32,70), true, null, -1);
//        grocery.getAgents().add(aisle13);
//        grocery.getAgentPatchSet().add(aisle13.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle14 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(14,14), true, null, -1);
//        grocery.getAgents().add(aisle14);
//        grocery.getAgentPatchSet().add(aisle14.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle15 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(26,14), true, null, -1);
//        grocery.getAgents().add(aisle15);
//        grocery.getAgentPatchSet().add(aisle15.getAgentMovement().getCurrentPatch());
    }

    public void reset() {
        this.time.reset();
    }

    private void start() {
        new Thread(() -> {
            final int speedAwarenessLimitMilliseconds = 10; // For times shorter than this, speed awareness will be implemented

            while (true) {
                try {
                    playSemaphore.acquire(); // Wait until the play button has been pressed

                    while (this.isRunning()) { // Keep looping until paused
                        long currentTick = this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5;
                        try {
                            updateAgentsInGrocery(grocery);
                            spawnAgent(grocery, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization
                        // If the refreshes are frequent enough, update the visualization in a speed-aware manner
                        ((GroceryScreenController) Main.mainScreenController).drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 10800) {
                            ((GroceryScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInGrocery(Grocery grocery) throws InterruptedException { // Manage all agent-related updates
        moveAll(grocery);
    }

    private static void moveAll(Grocery grocery) { // Make all agents move for one tick
        for (GroceryAgent agent : grocery.getAgents()) {
            try {
                moveOne(agent);
                agent.getAgentGraphic().change();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(GroceryAgent agent) throws Throwable {
        GroceryAgentMovement agentMovement = agent.getAgentMovement();

        GroceryAgent.Type type = agent.getType();
        GroceryAgent.Persona persona = agent.getPersona();
        GroceryState state = agentMovement.getCurrentState();
        GroceryAction action = agentMovement.getCurrentAction();

        switch (type) {
            case STAFF_AISLE:
                if (state.getName() == GroceryState.Name.STAFF_AISLE) {
                    if (action.getName() == GroceryAction.Name.STAFF_AISLE_ORGANIZE) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseRandomAisle();
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() == 0) {
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }

                break;

            case CUSTOMER:
                if (state.getName() == GroceryState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == GroceryAction.Name.GOING_TO_SECURITY_QUEUE) {
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.grocerySimulator.getGrocery().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == GroceryAction.Name.GO_THROUGH_SCANNER) {
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == GroceryState.Name.GOING_CART) {
                    if (action.getName() == GroceryAction.Name.GET_CART) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == GroceryState.Name.GOING_TO_PRODUCTS) {
                    if (action.getName() == GroceryAction.Name.GO_TO_PRODUCT_WALL || action.getName() == GroceryAction.Name.GO_TO_AISLE || action.getName() == GroceryAction.Name.GO_TO_FROZEN || action.getName() == GroceryAction.Name.GO_TO_FRESH || action.getName() == GroceryAction.Name.GO_TO_MEAT) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getCurrentAction().getDestination().getAmenityBlock());
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                    }
                }
                else if (state.getName() == GroceryState.Name.IN_PRODUCTS_WALL || state.getName() == GroceryState.Name.IN_PRODUCTS_AISLE || state.getName() == GroceryState.Name.IN_PRODUCTS_FROZEN || state.getName() == GroceryState.Name.IN_PRODUCTS_FRESH || state.getName() == GroceryState.Name.IN_PRODUCTS_MEAT) {
                    if (action.getName() == GroceryAction.Name.CHECK_PRODUCTS) {
                        if (agentMovement.getGoalAmenity() != null) {
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == GroceryState.Name.GOING_TO_PAY || state.getName() == GroceryState.Name.GOING_TO_SERVICE || state.getName() == GroceryState.Name.GOING_TO_EAT) {
                    if (action.getName() == GroceryAction.Name.GO_TO_CHECKOUT || action.getName() == GroceryAction.Name.GO_TO_CUSTOMER_SERVICE || action.getName() == GroceryAction.Name.GO_TO_FOOD_STALL) {
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            if (action.getName() == GroceryAction.Name.GO_TO_CHECKOUT) {
                                agentMovement.chooseCashierCounter();
                            }
                            else if (action.getName() == GroceryAction.Name.GO_TO_CUSTOMER_SERVICE) {
                                agentMovement.chooseServiceCounter();
                            }
                            else if (action.getName() == GroceryAction.Name.GO_TO_FOOD_STALL) {
                                agentMovement.chooseStall();
                            }
                        }
                        if (agentMovement.getGoalQueueingPatchField() != null) {
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == GroceryAction.Name.QUEUE_CHECKOUT || action.getName() == GroceryAction.Name.QUEUE_SERVICE || action.getName() == GroceryAction.Name.QUEUE_FOOD) {
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                        }
                    }
                    else if (action.getName() == GroceryAction.Name.CHECKOUT || action.getName() == GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE || action.getName() == GroceryAction.Name.BUY_FOOD) {
                        if (agentMovement.getGoalAmenity() != null) {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == GroceryState.Name.EATING) {
                    if (action.getName() == GroceryAction.Name.FIND_SEAT_FOOD_COURT) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseEatTable();
                        }
                        else {
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == GroceryAction.Name.EATING_FOOD) {
                        if (agentMovement.getGoalAmenity() != null) {
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == GroceryState.Name.GOING_HOME) {
                    if (action.getName() == GroceryAction.Name.GO_TO_RECEIPT) {
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.grocerySimulator.getGrocery().getGroceryGateFields().get(0));
                            agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getGroceryGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == GroceryAction.Name.CHECKOUT_GROCERIES_CUSTOMER) {
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (action.getName() == GroceryAction.Name.LEAVE_BUILDING) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getGroceryGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.despawn();
                                }
                            }
                        }
                    }
                }

                break;
        }
    }

    private void spawnAgent(Grocery grocery, long currentTick) {
        GroceryGate gate = grocery.getGroceryGates().get(1);
        GroceryAgent agent1 = null;
        GroceryAgent agent2 = null;
        GroceryAgent agent3 = null;
        GroceryAgent agent4 = null;

        Gate.GateBlock spawner1 = gate.getSpawners().get(0);
        Gate.GateBlock spawner2 = gate.getSpawners().get(1);
        Gate.GateBlock spawner3 = gate.getSpawners().get(2);
        Gate.GateBlock spawner4 = gate.getSpawners().get(3);

        int spawnChance = (int) gate.getChancePerTick();
        int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        boolean isFamily = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

        if (CHANCE > spawnChance) {
            if (isFamily && MAX_FAMILY != 0) {
                int num = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3);

                if (num == 0) { // Complete Family
                    GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                    GroceryAgent.Gender gender2;
                    if (gender1 == GroceryAgent.Gender.MALE) {
                        gender2 = GroceryAgent.Gender.FEMALE;
                    }
                    else {
                        gender2 = GroceryAgent.Gender.MALE;
                    }
                    GroceryAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                    GroceryAgent.Gender gender4 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                    agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner2.getPatch(), false, null, currentTick);
                    grocery.getAgents().add(agent1);
                    grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());

                    agent2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender2, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner1.getPatch(), false, agent1, currentTick);
                    grocery.getAgents().add(agent2);
                    grocery.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());

                    agent3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender3, GroceryAgent.AgeGroup.FROM_15_TO_24, spawner3.getPatch(), false, agent1, currentTick);
                    grocery.getAgents().add(agent3);
                    grocery.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());

                    agent4 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER, gender4, GroceryAgent.AgeGroup.FROM_15_TO_24, spawner3.getPatch(), false, agent1, currentTick);
                    grocery.getAgents().add(agent4);
                    grocery.getAgentPatchSet().add(agent4.getAgentMovement().getCurrentPatch());

                    agent1.getAgentMovement().getFollowers().add(agent2);
                    agent1.getAgentMovement().getFollowers().add(agent3);
                    agent1.getAgentMovement().getFollowers().add(agent4);
                    agent1.getAgentMovement().setNextState();
                }
                else if (num == 1) { // Help Family
                    GroceryAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                    agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Gender.FEMALE, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner2.getPatch(), false, null, currentTick);
                    grocery.getAgents().add(agent1);
                    grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());

                    agent2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, GroceryAgent.Gender.FEMALE, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner1.getPatch(), false, agent1, currentTick);
                    grocery.getAgents().add(agent2);
                    grocery.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());

                    agent3 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.HELP_FAMILY_CUSTOMER, gender3, GroceryAgent.AgeGroup.FROM_15_TO_24, spawner3.getPatch(), false, agent1, currentTick);
                    grocery.getAgents().add(agent3);
                    grocery.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());

                    agent1.getAgentMovement().getFollowers().add(agent2);
                    agent1.getAgentMovement().getFollowers().add(agent3);
                    agent1.getAgentMovement().setNextState();
                }
                else { // Duo Family
                    GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;
                    GroceryAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                    agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner2.getPatch(), false, null, currentTick);
                    grocery.getAgents().add(agent1);
                    grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());

                    agent2 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.DUO_FAMILY_CUSTOMER, gender2, GroceryAgent.AgeGroup.FROM_15_TO_24, spawner1.getPatch(), false, agent1, currentTick);
                    grocery.getAgents().add(agent2);
                    grocery.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());

                    agent1.getAgentMovement().getFollowers().add(agent2);
                    agent1.getAgentMovement().setNextState();
                }

                MAX_FAMILY -= 1;
            }
            else if (!isFamily && MAX_ALONE != 0) {
                boolean isSttp = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                GroceryAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.MALE : GroceryAgent.Gender.FEMALE;

                if (isSttp) {
                    agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.STTP_ALONE_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner2.getPatch(), false, null, currentTick);
                }
                else {
                    agent1 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.CUSTOMER, GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER, gender1, GroceryAgent.AgeGroup.FROM_25_TO_54, spawner2.getPatch(), false, null, currentTick);
                }
                grocery.getAgents().add(agent1);
                grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());

                MAX_ALONE -= 1;
            }
        }
    }
}