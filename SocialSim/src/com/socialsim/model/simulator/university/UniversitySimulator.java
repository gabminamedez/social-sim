package com.socialsim.model.simulator.university;

import com.socialsim.controller.Main;
import com.socialsim.controller.university.controls.UniversityScreenController;
import com.socialsim.model.core.agent.university.UniversityAction;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.agent.university.UniversityAgentMovement;
import com.socialsim.model.core.agent.university.UniversityState;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class UniversitySimulator extends Simulator {

    private University university;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    private final int MAX_STUDENTS = 250;
    private final int MAX_PROFESSORS = 10;

    public UniversitySimulator() {
        this.university = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(6, 30, 0);
        this.playSemaphore = new Semaphore(0);
        this.start(); // Start the simulation thread, but in reality it would be activated much later
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
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

    public void resetToDefaultConfiguration(University university) {
        this.university = university;
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(University university) {
        UniversityAgent guard = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.GUARD, university.getPatch(57,12), true, -1);
        university.getAgents().add(guard);
        university.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());

        UniversityAgent janitor1 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.JANITOR, university.getPatch(6,65), true, -1);
        university.getAgents().add(janitor1);
        university.getAgentPatchSet().add(janitor1.getAgentMovement().getCurrentPatch());

        UniversityAgent janitor2 = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.JANITOR, university.getPatch(7,66), true, -1);
        university.getAgents().add(janitor2);
        university.getAgentPatchSet().add(janitor2.getAgentMovement().getCurrentPatch());
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
                            updateAgentsInUniversity(university);
                            spawnAgent(university, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization; If the refreshes are frequent enough, update the visualization in a speed-aware manner
                        ((UniversityScreenController) Main.mainScreenController).drawUniversityViewForeground(Main.universitySimulator.getUniversity(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 9000) {
                            ((UniversityScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInUniversity(University university) throws InterruptedException { // Manage all agent-related updates
        moveAll(university);
    }

    private static void moveAll(University university) { // Make all agents move for one tick
        for (UniversityAgent agent : university.getAgents()) {
            try {
                moveOne(agent);
                agent.getAgentGraphic().change();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(UniversityAgent agent) throws Exception {
        UniversityAgentMovement agentMovement = agent.getAgentMovement();

        UniversityAgent.Type type = agent.getType();
        UniversityAgent.Persona persona = agent.getPersona();
        UniversityState state = agentMovement.getCurrentState();
        UniversityAction action = agentMovement.getCurrentAction();

        switch (type) {
            case JANITOR:
                if (state.getName() == UniversityState.Name.MAINTENANCE_BATHROOM) {
                    if (action.getName() == UniversityAction.Name.JANITOR_GO_TOILET) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                System.out.println(agentMovement.getCurrentPath().getPath());
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // Check if there are still patches left in the path
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (action.getName() == UniversityAction.Name.JANITOR_CLEAN_TOILET) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
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
                else if (state.getName() == UniversityState.Name.MAINTENANCE_FOUNTAIN) {
                    if (action.getName() == UniversityAction.Name.JANITOR_GO_FOUNTAIN) {
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
                    else if (action.getName() == UniversityAction.Name.JANITOR_CHECK_FOUNTAIN) {
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
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

            case STUDENT:
                if (state.getName() == UniversityState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == UniversityAction.Name.GOING_TO_SECURITY_QUEUE) {
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                    agentMovement.joinQueue();
                                    // agentMovement.resetGoal();
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                }
                            }
                        }
                    }
                    else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() == 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == UniversityState.Name.WANDERING_AROUND) {
                    if (action.getName() == UniversityAction.Name.RANDOM_ACTION) {
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
                                }
                            }
                        }
                    }
                } else if (state.getName()== UniversityState.Name.NEEDS_BATHROOM) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.NEEDS_DRINK) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_TO_STUDY) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.STUDYING) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_TO_CLASS_STUDENT) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.WAIT_FOR_CLASS_STUDENT) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.IN_CLASS_STUDENT) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_TO_LUNCH) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.EATING_LUNCH) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_HOME) {
                    /*Insert Action*/
                }
                break;

            case PROFESSOR:
                if (state.getName()== UniversityState.Name.GOING_TO_SECURITY) {
                    /*Insert Action*/
                    action.getName();
                    int duration = action.getDuration();
                    action.getDestination();
                    while(duration!=0)
                    {
                        duration--; //every tick
                    }

                } else if (state.getName()== UniversityState.Name.WANDERING_AROUND) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.NEEDS_BATHROOM) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.NEEDS_DRINK) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_TO_CLASS_PROFESSOR) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.IN_CLASS_PROFESSOR) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_TO_LUNCH) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.EATING_LUNCH) {
                    /*Insert Action*/
                } else if (state.getName()== UniversityState.Name.GOING_HOME) {
                    /*Insert Action*/
                }
                break;
        }
    }

    private void spawnAgent(University university, long currentTick) {
        UniversityGate gate = university.getUniversityGates().get(1);
        Gate.GateBlock spawner0 = gate.getSpawners().get(0);
        Gate.GateBlock spawner1 = gate.getSpawners().get(1);
        Gate.GateBlock spawner2 = gate.getSpawners().get(2);
        Gate.GateBlock spawner3 = gate.getSpawners().get(3);
        UniversityAgent agent = null;

        int spawnChance = gate.getChancePerTick();
        int CHANCE_SPAWN_0 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        int CHANCE_SPAWN_1 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        int CHANCE_SPAWN_2 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        int CHANCE_SPAWN_3 = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        boolean isStudent0 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
        boolean isStudent1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
        boolean isStudent2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
        boolean isStudent3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

        if (spawnChance < CHANCE_SPAWN_0 && isStudent0 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner0.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.studentCount) + " " + String.valueOf(agent.getPersona()));
        }
        else if (spawnChance < CHANCE_SPAWN_0 && !isStudent0 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner0.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.professorCount) + " " + String.valueOf(agent.getPersona()));
        }

        if (spawnChance < CHANCE_SPAWN_1 && isStudent1 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner1.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.studentCount) + " " + String.valueOf(agent.getPersona()));
        }
        else if (spawnChance < CHANCE_SPAWN_1 && !isStudent1 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner1.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.professorCount) + " " + String.valueOf(agent.getPersona()));
        }

        if (spawnChance < CHANCE_SPAWN_2 && isStudent2 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner2.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.studentCount) + " " + String.valueOf(agent.getPersona()));
        }
        else if (spawnChance < CHANCE_SPAWN_2 && !isStudent2 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner2.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.professorCount) + " " + String.valueOf(agent.getPersona()));
        }

        if (spawnChance < CHANCE_SPAWN_3 && isStudent3 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner3.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.studentCount) + " " + String.valueOf(agent.getPersona()));
        }
        else if (spawnChance < CHANCE_SPAWN_3 && !isStudent3 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner3.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
            System.out.println(String.valueOf(UniversityAgent.professorCount) + " " + String.valueOf(agent.getPersona()));
        }
    }

}