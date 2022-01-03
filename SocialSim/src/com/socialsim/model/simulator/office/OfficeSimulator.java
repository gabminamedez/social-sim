package com.socialsim.model.simulator.office;

import com.socialsim.controller.Main;
import com.socialsim.controller.office.controls.OfficeScreenController;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.agent.office.OfficeAction;
import com.socialsim.model.core.agent.office.OfficeAgentMovement;
import com.socialsim.model.core.agent.office.OfficeState;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class OfficeSimulator extends Simulator {

    private Office office;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    private final int MAX_BOSSES = 1;
    private List<Integer> MANAGERS_1 = new LinkedList<Integer>(List.of(11));
    private List<Integer> MANAGERS_2 = new LinkedList<Integer>(List.of(19));
    private List<Integer> MANAGERS_3 = new LinkedList<Integer>(List.of(27));
    private List<Integer> MANAGERS_4 = new LinkedList<Integer>(List.of(35));
    private List<Integer> BUSINESS_1 = new LinkedList<Integer>(List.of(0, 1, 2, 3, 4));
    private List<Integer> BUSINESS_2 = new LinkedList<Integer>(List.of(36, 37, 38, 39, 40, 41, 42));
    private List<Integer> BUSINESS_3 = new LinkedList<Integer>(List.of(44, 45, 46, 47, 48, 49, 50));
    private List<Integer> BUSINESS_4 = new LinkedList<Integer>(List.of(52, 53, 54, 55, 56, 57, 58));
    private List<Integer> RESEARCH_1 = new LinkedList<Integer>(List.of(5, 6, 7, 8, 9));
    private List<Integer> RESEARCH_2 = new LinkedList<Integer>(List.of(12, 13, 14, 15, 16, 17, 18));
    private List<Integer> RESEARCH_3 = new LinkedList<Integer>(List.of(20, 21, 22, 23, 24, 25, 26));
    private List<Integer> RESEARCH_4 = new LinkedList<Integer>(List.of(28, 29, 30, 31, 32, 33, 34));
    private List<Integer> TECHNICAL_1 = new LinkedList<Integer>(List.of(10));
    private List<Integer> TECHNICAL_2 = new LinkedList<Integer>(List.of(43));
    private List<Integer> TECHNICAL_3 = new LinkedList<Integer>(List.of(51));
    private List<Integer> TECHNICAL_4 = new LinkedList<Integer>(List.of(59));
    private final int MAX_SECRETARIES = 1;
    private final int MAX_CLIENTS = 4;
    private final int MAX_DRIVERS = 2;
    private final int MAX_VISITORS = 1;

    public OfficeSimulator() {
        this.office = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(9, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start(); // Start the simulation thread, but in reality it would be activated much later
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
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

    public void resetToDefaultConfiguration(Office office) {
        this.office = office;
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(Office office) {
        OfficeAgent guard = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.GUARD, office.getPatch(57,35), true, -1, 0, null);
        office.getAgents().add(guard);
        office.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());

        OfficeAgent janitor = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.JANITOR, office.getPatch(6,23), true, -1, 0, null);
        office.getAgents().add(janitor);
        office.getAgentPatchSet().add(janitor.getAgentMovement().getCurrentPatch());

        OfficeAgent receptionist = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.RECEPTIONIST, office.getPatch(46,37), true, -1, 0, null);
        office.getAgents().add(receptionist);
        office.getAgentPatchSet().add(receptionist.getAgentMovement().getCurrentPatch());
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
                            updateAgentsInOffice(office);
                            spawnAgent(office, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization
                        // If the refreshes are frequent enough, update the visualization in a speed-aware manner
                        ((OfficeScreenController) Main.mainScreenController).drawOfficeViewForeground(Main.officeSimulator.getOffice(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 5760) {
                            ((OfficeScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInOffice(Office office) throws InterruptedException { // Manage all agent-related updates
        moveAll(office);
    }

    private static void moveAll(Office office) { // Make all agents move for one tick
        for (OfficeAgent agent : office.getAgents()) {
            try {
                moveOne(agent);
                agent.getAgentGraphic().change();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(OfficeAgent agent) throws Throwable {
        OfficeAgentMovement agentMovement = agent.getAgentMovement();

        OfficeAgent.Type type = agent.getType();
        OfficeAgent.Persona persona = agent.getPersona();
        OfficeState state = agentMovement.getCurrentState();
        OfficeAction action = agentMovement.getCurrentAction();

        switch (type) {
            case JANITOR:
                if (state.getName() == OfficeState.Name.MAINTENANCE_BATHROOM) {
                    if (action.getName() == OfficeAction.Name.JANITOR_GO_TOILET) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.JANITOR_CLEAN_TOILET) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
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
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.MAINTENANCE_PLANT) {
                    if (action.getName() == OfficeAction.Name.JANITOR_GO_PLANT) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.JANITOR_WATER_PLANT) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
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
                                agentMovement.setPreviousState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }

                break;

            case MANAGER: case BUSINESS: case RESEARCHER: case TECHNICAL:
                if (state.getName() == OfficeState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == OfficeAction.Name.GOING_TO_SECURITY_QUEUE) {
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.officeSimulator.getOffice().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getSecurities().get(0));
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
                    else if (action.getName() == OfficeAction.Name.GO_THROUGH_SCANNER) {
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
                else if (state.getName() == OfficeState.Name.WORKING) {
                    if (action.getName() == OfficeAction.Name.GO_TO_STATION) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
//                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    agentMovement.resetGoal();
                                    System.out.println("time for business");
                                }
                            }
                        }
                    }
                }

                break;
        }
    }

    private void spawnAgent(Office office, long currentTick) {
        OfficeGate gate = office.getOfficeGates().get(1);
        OfficeAgent agent = null;

        for (int i = 0; i < 4; i++){ // 4 gates
            Gate.GateBlock spawner = gate.getSpawners().get(i);
            int spawnChance = (int) gate.getChancePerTick();
            int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            int type = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(9); // 0 = Boss; 1 = Manager; 2 = Business; 3 = Researcher; 4 = Technical; 5 = Secretary; 6 = Client; 7 = Driver; 8 = Visitor
            int team = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;

            if (CHANCE > spawnChance) {
                if (type == 0 && OfficeAgent.bossCount != this.MAX_BOSSES) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.BOSS, spawner.getPatch(), false, currentTick, 0, null);
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                }
                else if (type == 1 && team == 1 && MANAGERS_1.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.MANAGER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(MANAGERS_1.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    MANAGERS_1.remove(0);
                }
                else if (type == 2 && team == 1 && BUSINESS_1.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.BUSINESS, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(BUSINESS_1.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    BUSINESS_1.remove(0);
                }
                else if (type == 3 && team == 1 && RESEARCH_1.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.RESEARCHER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(RESEARCH_1.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    RESEARCH_1.remove(0);
                }
                else if (type == 4 && team == 1 && TECHNICAL_1.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.TECHNICAL, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(TECHNICAL_1.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    TECHNICAL_1.remove(0);
                }
                else if (type == 1 && team == 2 && MANAGERS_2.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.MANAGER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(MANAGERS_2.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    MANAGERS_2.remove(0);
                }
                else if (type == 2 && team == 2 && BUSINESS_2.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.BUSINESS, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(BUSINESS_2.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    BUSINESS_2.remove(0);
                }
                else if (type == 3 && team == 2 && RESEARCH_2.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.RESEARCHER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(RESEARCH_2.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    RESEARCH_2.remove(0);
                }
                else if (type == 4 && team == 2 && TECHNICAL_2.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.TECHNICAL, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(TECHNICAL_2.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    TECHNICAL_2.remove(0);
                }
                else if (type == 1 && team == 3 && MANAGERS_3.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.MANAGER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(MANAGERS_3.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    MANAGERS_3.remove(0);
                }
                else if (type == 2 && team == 3 && BUSINESS_3.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.BUSINESS, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(BUSINESS_3.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    BUSINESS_3.remove(0);
                }
                else if (type == 3 && team == 3 && RESEARCH_3.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.RESEARCHER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(RESEARCH_3.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    RESEARCH_3.remove(0);
                }
                else if (type == 4 && team == 3 && TECHNICAL_3.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.TECHNICAL, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(TECHNICAL_3.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    TECHNICAL_3.remove(0);
                }
                else if (type == 1 && team == 4 && MANAGERS_4.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.MANAGER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(MANAGERS_4.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    MANAGERS_4.remove(0);
                }
                else if (type == 2 && team == 4 && BUSINESS_4.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.BUSINESS, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(BUSINESS_4.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    BUSINESS_4.remove(0);
                }
                else if (type == 3 && team == 4 && RESEARCH_4.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.RESEARCHER, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(RESEARCH_4.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    RESEARCH_4.remove(0);
                }
                else if (type == 4 && team == 4 && TECHNICAL_4.size() != 0) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.TECHNICAL, spawner.getPatch(), false, currentTick, team, office.getCubicles().get(TECHNICAL_4.get(0)));
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    TECHNICAL_4.remove(0);
                }
                else if (type == 5 && OfficeAgent.secretaryCount != this.MAX_SECRETARIES) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.SECRETARY, spawner.getPatch(), false, currentTick, 0, null);
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                }
                else if (type == 6 && OfficeAgent.clientCount != this.MAX_CLIENTS) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.CLIENT, spawner.getPatch(), false, currentTick, 0, null);
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                }
                else if (type == 7 && OfficeAgent.driverCount != this.MAX_DRIVERS) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.DRIVER, spawner.getPatch(), false, currentTick, 0, null);
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                }
                else if (type == 8 && OfficeAgent.visitorCount != this.MAX_VISITORS) {
                    agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.VISITOR, spawner.getPatch(), false, currentTick, 0, null);
                    office.getAgents().add(agent);
                    office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                }
            }
        }
    }

}