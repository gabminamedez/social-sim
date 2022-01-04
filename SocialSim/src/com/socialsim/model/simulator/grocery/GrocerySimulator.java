package com.socialsim.model.simulator.grocery;

import com.socialsim.controller.Main;
import com.socialsim.controller.grocery.controls.GroceryScreenController;
import com.socialsim.controller.grocery.controls.GroceryScreenController;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAction;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAgentMovement;
import com.socialsim.model.core.agent.grocery.GroceryState;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
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
//        GroceryAgent guard = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.GUARD, grocery.getPatch(57,35), true, -1, 0, null);
//        grocery.getAgents().add(guard);
//        grocery.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
//
//        GroceryAgent janitor = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.JANITOR, grocery.getPatch(6,23), true, -1, 0, null);
//        grocery.getAgents().add(janitor);
//        grocery.getAgentPatchSet().add(janitor.getAgentMovement().getCurrentPatch());
//
//        GroceryAgent receptionist = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.RECEPTIONIST, grocery.getPatch(46,37), true, -1, 0, null);
//        grocery.getAgents().add(receptionist);
//        grocery.getAgentPatchSet().add(receptionist.getAgentMovement().getCurrentPatch());
    }

    public void reset() {
        this.time.reset();
    }

    private void start() {
        new Thread(() -> {
            final int speedAwarenessLimitMilliseconds = 10; // For times shorter than this, speed awareness will be implemented
            long currentTick = this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5;

            while (true) {
                try {
                    playSemaphore.acquire(); // Wait until the play button has been pressed

                    while (this.isRunning()) { // Keep looping until paused
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

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 10440) {
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

//        switch (type) {
//            case JANITOR:
//                if (state.getName() == GroceryState.Name.MAINTENANCE_BATHROOM) {
//                    if (action.getName() == GroceryAction.Name.JANITOR_GO_TOILET) {
//                        if (agentMovement.getGoalAmenity() == null) {
//                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                        }
//
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
//                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
//                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    agentMovement.resetGoal();
//                                }
//                            }
//                        }
//                    }
//                    else if (action.getName() == GroceryAction.Name.JANITOR_CLEAN_TOILET) {
//                        if (agentMovement.getGoalAmenity() == null) {
//                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
//                        }
//
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath();
//                            }
//                        }
//                        else {
//                            agentMovement.setDuration(agentMovement.getDuration() - 1);
//                            if (agentMovement.getDuration() == 0) {
//                                agentMovement.setNextState();
//                                agentMovement.setActionIndex(0);
//                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                agentMovement.resetGoal();
//                            }
//                        }
//                    }
//                }
//                else if (state.getName() == GroceryState.Name.MAINTENANCE_PLANT) {
//                    if (action.getName() == GroceryAction.Name.JANITOR_GO_PLANT) {
//                        if (agentMovement.getGoalAmenity() == null) {
//                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                        }
//
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
//                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
//                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    agentMovement.resetGoal();
//                                }
//                            }
//                        }
//                    }
//                    else if (action.getName() == GroceryAction.Name.JANITOR_WATER_PLANT) {
//                        if (agentMovement.getGoalAmenity() == null) {
//                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
//                        }
//
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath();
//                            }
//                        }
//                        else {
//                            agentMovement.setDuration(agentMovement.getDuration() - 1);
//                            if (agentMovement.getDuration() == 0) {
//                                agentMovement.setPreviousState();
//                                agentMovement.setActionIndex(0);
//                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                agentMovement.resetGoal();
//                            }
//                        }
//                    }
//                }
//
//                break;
//
//            case MANAGER: case BUSINESS: case RESEARCHER: case TECHNICAL:
//                if (state.getName() == GroceryState.Name.GOING_TO_SECURITY) {
//                    if (action.getName() == GroceryAction.Name.GOING_TO_SECURITY_QUEUE) {
//                        if (agentMovement.getGoalQueueingPatchField() == null) {
//                            agentMovement.setGoalQueueingPatchField(Main.grocerySimulator.getGrocery().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
//                            agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getSecurities().get(0));
//                        }
//
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath();
//                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
//                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    agentMovement.joinQueue();
//                                }
//                            }
//                        }
//                    }
//                    else if (action.getName() == GroceryAction.Name.GO_THROUGH_SCANNER) {
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath();
//                            }
//                        }
//                        else {
//                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
//                            agentMovement.setDuration(agentMovement.getDuration() - 1);
//                            if (agentMovement.getDuration() <= 0) {
//                                agentMovement.leaveQueue();
//                                agentMovement.setNextState();
//                                agentMovement.setActionIndex(0);
//                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                agentMovement.resetGoal();
//                            }
//                        }
//                    }
//                }
//                else if (state.getName() == GroceryState.Name.WORKING) {
//                    if (action.getName() == GroceryAction.Name.GO_TO_STATION) {
//                        if (agentMovement.getGoalAmenity() == null) {
//                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                        }
//
//                        if (agentMovement.chooseNextPatchInPath()) {
//                            agentMovement.faceNextPosition();
//                            agentMovement.moveSocialForce();
//                            if (agentMovement.hasReachedNextPatchInPath()) {
//                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
//                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
////                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
////                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
////                                    agentMovement.resetGoal();
//                                    System.out.println("time for business");
//                                }
//                            }
//                        }
//                    }
//                }
//
//                break;
//        }
    }

    private void spawnAgent(Grocery grocery, long currentTick) {
        GroceryGate gate = grocery.getGroceryGates().get(1);
        GroceryAgent agent = null;
    }
}