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
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class OfficeSimulator extends Simulator {

    private Office office;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    public OfficeSimulator() {
        this.office = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(0, 0, 0);
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
        OfficeAgent guard = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.GUARD, office.getPatch(57,35), true, -1);
        office.getAgents().add(guard);
        office.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());

        OfficeAgent janitor = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.JANITOR, office.getPatch(6,23), true, -1);
        office.getAgents().add(janitor);
        office.getAgentPatchSet().add(janitor.getAgentMovement().getCurrentPatch());

        OfficeAgent receptionist = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.RECEPTIONIST, office.getPatch(46,37), true, -1);
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
                    } else if (action.getName() == OfficeAction.Name.JANITOR_CLEAN_TOILET) {
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
                        } else {
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

                break;
        }
    }

    private void spawnAgent(Office office, long currentTick) {
//        OfficeGate gate = office.getOfficeGates().get(1);
//        Gate.GateBlock spawner0 = gate.getSpawners().get(0);
//        Gate.GateBlock spawner1 = gate.getSpawners().get(1);
//        Gate.GateBlock spawner2 = gate.getSpawners().get(2);
//        Gate.GateBlock spawner3 = gate.getSpawners().get(3);
//        OfficeAgent agent = null;
//
//        int spawnChance = gate.getChancePerTick();
//        int CHANCE_SPAWN_0 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
//        int CHANCE_SPAWN_1 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
//        int CHANCE_SPAWN_2 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
//        int CHANCE_SPAWN_3 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
//        boolean isStudent0 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//        boolean isStudent1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//        boolean isStudent2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//        boolean isStudent3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//
//        if (spawnChance < CHANCE_SPAWN_0 && isStudent0 && OfficeAgent.studentCount != this.MAX_STUDENTS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.STUDENT, spawner0.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//        else if (spawnChance < CHANCE_SPAWN_0 && !isStudent0 && OfficeAgent.professorCount != this.MAX_PROFESSORS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.PROFESSOR, spawner0.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//
//        if (spawnChance < CHANCE_SPAWN_1 && isStudent1 && OfficeAgent.studentCount != this.MAX_STUDENTS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.STUDENT, spawner1.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//        else if (spawnChance < CHANCE_SPAWN_1 && !isStudent1 && OfficeAgent.professorCount != this.MAX_PROFESSORS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.PROFESSOR, spawner1.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//
//        if (spawnChance < CHANCE_SPAWN_2 && isStudent2 && OfficeAgent.studentCount != this.MAX_STUDENTS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.STUDENT, spawner2.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//        else if (spawnChance < CHANCE_SPAWN_2 && !isStudent2 && OfficeAgent.professorCount != this.MAX_PROFESSORS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.PROFESSOR, spawner2.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//
//        if (spawnChance < CHANCE_SPAWN_3 && isStudent3 && OfficeAgent.studentCount != this.MAX_STUDENTS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.STUDENT, spawner3.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
//        else if (spawnChance < CHANCE_SPAWN_3 && !isStudent3 && OfficeAgent.professorCount != this.MAX_PROFESSORS) {
//            agent = OfficeAgent.OfficeAgentFactory.create(OfficeAgent.Type.PROFESSOR, spawner3.getPatch(), false, currentTick);
//            office.getAgents().add(agent);
//            office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
//        }
    }

}