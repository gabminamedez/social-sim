package com.socialsim.model.simulator.university;

import com.socialsim.controller.Main;
import com.socialsim.controller.university.controls.UniversityScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.agent.university.*;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class UniversitySimulator extends Simulator {

    private University university;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    public static int currentProfessorCount = 0;
    public static int currentStudentCount = 0;
    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;

    public static int averageNonverbalDuration = 0;
    public static int averageCooperativeDuration = 0;
    public static int averageExchangeDuration = 0;

    public static int currentStudentStudentCount = 0;
    public static int currentStudentProfCount = 0;
    public static int currentStudentGuardCount = 0;
    public static int currentStudentJanitorCount = 0;
    public static int currentProfProfCount = 0;
    public static int currentProfGuardCount = 0;
    public static int currentProfJanitorCount = 0;
    public static int currentGuardJanitorCount = 0;
    public static int currentJanitorJanitorCount = 0;
    public static int[][] currentPatchCount;
    public static final int MAX_STUDENTS = 50; //250
    public static final int MAX_PROFESSORS = 0;
    public static final int MAX_CURRENT_STUDENTS = 3; //250
    public static final int MAX_CURRENT_PROFESSORS = 0;

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
        currentPatchCount = new int[university.getRows()][university.getColumns()];
        for (int j = 0; j < university.getRows(); j++)
            Arrays.setAll(currentPatchCount[j], i -> Simulator.RANDOM_NUMBER_GENERATOR.nextInt(255));
    }

    public void spawnInitialAgents(University university) {
        university.createInitialAgentDemographics(MAX_STUDENTS, MAX_PROFESSORS);
        UniversityAgent guard = university.getAgents().get(0); // 0
        guard.setAgentMovement(new UniversityAgentMovement(university.getPatch(57,12), guard, 1.27, university.getPatch(57,12).getPatchCenterCoordinates(), -1));
//        university.getAgents().add(guard);
        university.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
        UniversityAgent.guardCount++;
        UniversityAgent.agentCount++;

        UniversityAgent janitor1 = university.getAgents().get(1); // 1
        janitor1.setAgentMovement(new UniversityAgentMovement(university.getPatch(6,65), janitor1, 1.27, university.getPatch(6,65).getPatchCenterCoordinates(), -1));
//        university.getAgents().add(janitor1);
        university.getAgentPatchSet().add(janitor1.getAgentMovement().getCurrentPatch());
        UniversityAgent.janitorCount++;
        UniversityAgent.agentCount++;

        UniversityAgent janitor2 = university.getAgents().get(2); // 2
        janitor2.setAgentMovement(new UniversityAgentMovement(university.getPatch(7,66), janitor2, 1.27, university.getPatch(7,66).getPatchCenterCoordinates(), -1));
//        university.getAgents().add(janitor2);
        university.getAgentPatchSet().add(janitor2.getAgentMovement().getCurrentPatch());
        UniversityAgent.janitorCount++;
        UniversityAgent.agentCount++;
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
                            updateAgentsInUniversity(university,currentTick);
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
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInUniversity(University university, long currentTick) throws InterruptedException { // Manage all agent-related updates
        moveAll(university,currentTick);
    }

    private static void moveAll(University university,long currentTick) { // Make all agents move for one tick
        for (UniversityAgent agent : university.getMovableAgents()) {
            boolean hasClass = false;
            int i = agent.getAgentMovement().getStateIndex();
            int classIndex = -1;
            try {
                if (currentTick == 540 && (agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    while(i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && hasClass==false){
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 720){
                            hasClass = true;
                            classIndex = i;
                        }
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 720){
                            hasClass = true;
                            classIndex = i;
                        }
                        i++;
                    }
                    if(hasClass) {
                        agent.getAgentMovement().setNextState(classIndex-1);
                        agent.getAgentMovement().setStateIndex(classIndex);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                        agent.getAgentMovement().resetGoal();
                        System.out.println("SKIP ACTION: GOING TO CLASS NOW");
                    }
                }

                else if (currentTick == 1800 && (agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    while(i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && hasClass==false){
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT&& agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 1980){
                            hasClass = true;
                            classIndex = i;
                        }
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR&& agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 1980){
                            hasClass = true;
                            classIndex = i;
                        }
                        i++;
                    }
                    if(hasClass) {
                        agent.getAgentMovement().setNextState(classIndex-1);
                        agent.getAgentMovement().setStateIndex(classIndex);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                        agent.getAgentMovement().resetGoal();
                        System.out.println("SKIP ACTION: GOING TO CLASS NOW");
                    }
                }

                else if (currentTick == 3060 && (agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    while(i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && hasClass==false){
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 3240){
                            hasClass = true;
                            classIndex = i;
                        }
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 3240){
                            hasClass = true;
                            classIndex = i;
                        }
                        i++;
                    }
                    if(hasClass) {
                        agent.getAgentMovement().setNextState(classIndex-1);
                        agent.getAgentMovement().setStateIndex(classIndex);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                        agent.getAgentMovement().resetGoal();
                        System.out.println("SKIP ACTION: GOING TO CLASS NOW");
                    }
                }

                else if (currentTick == 4320 && (agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    while(i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && hasClass==false){
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 4500){
                            hasClass = true;
                            classIndex = i;
                        }
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 4500){
                            hasClass = true;
                            classIndex = i;
                        }
                        i++;
                    }
                    if(hasClass) {
                        agent.getAgentMovement().setNextState(classIndex-1);
                        agent.getAgentMovement().setStateIndex(classIndex);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                        agent.getAgentMovement().resetGoal();
                        System.out.println("SKIP ACTION: GOING TO CLASS NOW");
                    }
                }

                else if (currentTick == 5580 && (agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    while(i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && hasClass==false){
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 5760){
                            hasClass = true;
                            classIndex = i;
                        }
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 5760){
                            hasClass = true;
                            classIndex = i;
                        }
                        i++;
                    }
                    if(hasClass) {
                        agent.getAgentMovement().setNextState(classIndex-1);
                        agent.getAgentMovement().setStateIndex(classIndex);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                        agent.getAgentMovement().resetGoal();
                        System.out.println("SKIP ACTION: GOING TO CLASS NOW");
                    }
                }

                else if (currentTick == 6840 && (agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    while(i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && hasClass==false){
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT&& agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020){
                            hasClass = true;
                            classIndex = i;
                        }
                        if(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020){
                            hasClass = true;
                            classIndex = i;
                        }
                        i++;
                    }
                    if(hasClass) {
                        agent.getAgentMovement().setNextState(classIndex-1);
                        agent.getAgentMovement().setStateIndex(classIndex);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                        agent.getAgentMovement().resetGoal();
                        System.out.println("SKIP ACTION: GOING TO CLASS NOW");
                    }
                }

                moveOne(agent);
                agent.getAgentGraphic().change();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(UniversityAgent agent) throws Throwable {
        UniversityAgentMovement agentMovement = agent.getAgentMovement();
        UniversityAgent.Type type = agent.getType();
        UniversityAgent.Persona persona = agent.getPersona();
        UniversityState state = agentMovement.getCurrentState();
        UniversityAction action = agentMovement.getCurrentAction();
        // TODO: If interacting, then call functions. If not interacting, move
        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()) {
            switch (type) {
                case JANITOR:
                    if (state.getName() == UniversityState.Name.MAINTENANCE_BATHROOM) {
                        if (action.getName() == UniversityAction.Name.JANITOR_CLEAN_TOILET) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                agentMovement.checkIfStuck();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.MAINTENANCE_FOUNTAIN) {
                        if (action.getName() == UniversityAction.Name.JANITOR_CHECK_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                agentMovement.checkIfStuck();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setPreviousState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
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
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getSecurities().get(0));
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
                        } else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.WANDERING_AROUND) {
                        if (action.getName() == UniversityAction.Name.FIND_BENCH || action.getName() == UniversityAction.Name.FIND_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (action.getName() == UniversityAction.Name.FIND_BENCH) {
                                    if (!agentMovement.chooseGoal(Bench.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                } else {
                                    if (!agentMovement.chooseGoal(Bulletin.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                            } else {
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
                                    } else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
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
                            }
                        } else if (action.getName() == UniversityAction.Name.SIT_ON_BENCH || action.getName() == UniversityAction.Name.VIEW_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == UniversityAction.Name.GO_TO_BATHROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    } else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    } else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                            } else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                }
                            }
                        } else if (action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            }
                        } else if (action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                agentMovement.resetGoal();
                                //System.out.println("Transition to Wash  in sink");
                            }
                        } else if (action.getName() == UniversityAction.Name.WASH_IN_SINK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    } else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    } else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                } else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            } else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    }
                                } else {
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        if (agentMovement.getRoutePlan().isFromStudying()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromStudying(false);
                                        } else if (agentMovement.getRoutePlan().isFromClass()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromClass(false);
                                        } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromLunch(false);
                                        } else {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                        }
                                    }
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.NEEDS_DRINK) {
                        if (action.getName() == UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getFountains().get(0).getAmenityBlocks().get(0).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getFountains().get(0));
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
                        } else if (action.getName() == UniversityAction.Name.QUEUE_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        } else if (action.getName() == UniversityAction.Name.DRINK_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getRoutePlan().isFromStudying()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(false);
                                } else if (agentMovement.getRoutePlan().isFromClass()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(false);
                                } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(false);
                                } else {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_TO_STUDY) {
                        if (action.getName() == UniversityAction.Name.GO_TO_STUDY_ROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(StudyTable.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex() + 1);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 2);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            } else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    } else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                    agentMovement.setActionIndex(0);
                                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.STUDYING) {
                        if (action.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false); //TODO: Why it nullpointer exception
                                agentMovement.resetGoal();
                            } else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);

                                double CHANCE = Simulator.roll();
                                double CHANCE2 = Simulator.roll();
                                double MAX = 0;
                                double MAX_DRINK = 0;
                                if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                    //EXT STUDENT
                                    MAX = UniversityRoutePlan.EXT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){
                                    //INT STUDENT
                                    MAX = UniversityRoutePlan.INT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                                    //INT ORG STUDENT
                                    MAX = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                                    //EXT ORG STUDENT
                                    MAX = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1)
                                {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if(CHANCE2 < MAX_DRINK && agentMovement.getRoutePlan().getUrgentCtr() >= 1){
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT) {
                        if (action.getName() == UniversityAction.Name.GO_TO_CLASSROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseClassroomGoal(Chair.class, agentMovement.getCurrentState().getClassroomID());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                } else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                            if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.WAIT_FOR_CLASS_STUDENT) {
                        if (action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.IN_CLASS_STUDENT) {
                        if (action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            } else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double CHANCE2 = Simulator.roll();
                                double MAX = 0;
                                double MAX_DRINK = 0;
                                if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                    //EXT STUDENT
                                    MAX = UniversityRoutePlan.EXT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){
                                    //INT STUDENT
                                    MAX = UniversityRoutePlan.INT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                                    //INT ORG STUDENT
                                    MAX = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                                    //EXT ORG STUDENT
                                    MAX = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1)
                                {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if(CHANCE2 < MAX_DRINK && agentMovement.getRoutePlan().getUrgentCtr() >= 1){
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_TO_LUNCH) {
                        if (action.getName() == UniversityAction.Name.GO_TO_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseStall();
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
                        } else if (action.getName() == UniversityAction.Name.QUEUE_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        } else if (action.getName() == UniversityAction.Name.CHECKOUT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                //System.out.println("Done Checkout");
                                agentMovement.resetGoal();
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.EATING_LUNCH) {
                        if (action.getName() == UniversityAction.Name.FIND_SEAT_CAFETERIA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseGoal(EatTable.class);
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        //System.out.println("Find seat to Eating");
                                    }
                                }
                            }
                        } else if (action.getName() == UniversityAction.Name.LUNCH_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            } else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double CHANCE2 = Simulator.roll();
                                double MAX = 0;
                                double MAX_DRINK = 0;
                                if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                    //EXT STUDENT
                                    MAX = UniversityRoutePlan.EXT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){
                                    //INT STUDENT
                                    MAX = UniversityRoutePlan.INT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                                    //INT ORG STUDENT
                                    MAX = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                                    //EXT ORG STUDENT
                                    MAX = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1)
                                {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if(CHANCE2 < MAX_DRINK && agentMovement.getRoutePlan().getUrgentCtr() >= 1){
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_HOME) {
                        if (action.getName() == UniversityAction.Name.LEAVE_BUILDING) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
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


                case PROFESSOR:
                    if (state.getName() == UniversityState.Name.GOING_TO_SECURITY) {
                        if (action.getName() == UniversityAction.Name.GOING_TO_SECURITY_QUEUE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getSecurities().get(0));
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
                        } else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.WANDERING_AROUND) {
                        if (action.getName() == UniversityAction.Name.FIND_BENCH || action.getName() == UniversityAction.Name.FIND_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (action.getName() == UniversityAction.Name.FIND_BENCH) {
                                    if (!agentMovement.chooseGoal(Bench.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                } else {
                                    if (!agentMovement.chooseGoal(Bulletin.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                            } else {
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
                                    } else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
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
                            }
                        } else if (action.getName() == UniversityAction.Name.SIT_ON_BENCH || action.getName() == UniversityAction.Name.VIEW_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == UniversityAction.Name.GO_TO_BATHROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    } else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    } else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                            } else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                }
                            }
                        } else if (action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            }
                        } else if (action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                agentMovement.resetGoal();
                            }
                        } else if (action.getName() == UniversityAction.Name.WASH_IN_SINK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    } else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    } else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                } else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            } else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    }
                                } else {
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        if (agentMovement.getRoutePlan().isFromStudying()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromStudying(false);
                                        } else if (agentMovement.getRoutePlan().isFromClass()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromClass(false);
                                        } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromLunch(false);
                                        } else {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                            agentMovement.resetGoal();
                                        }
                                    }
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.NEEDS_DRINK) {
                        if (action.getName() == UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getFountains().get(0).getAmenityBlocks().get(0).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getFountains().get(0));
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
                        } else if (action.getName() == UniversityAction.Name.QUEUE_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        } else if (action.getName() == UniversityAction.Name.DRINK_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getRoutePlan().isFromStudying()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(false);
                                } else if (agentMovement.getRoutePlan().isFromClass()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(false);
                                } else if (agentMovement.getRoutePlan().isFromLunch()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(false);
                                } else {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_TO_STUDY) {
                        if (action.getName() == UniversityAction.Name.GO_TO_STUDY_ROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(StudyTable.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex() + 1);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 2);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            } else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    } else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                    agentMovement.setActionIndex(0);
                                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.STUDYING) {
                        if (action.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false); //TODO: Why it nullpointer exception
                                agentMovement.resetGoal();
                            } else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double CHANCE2 = Simulator.roll();
                                double MAX = UniversityRoutePlan.PROF_CHANCE_NEEDS_BATHROOM_STUDYING;
                                double MAX_DRINK = UniversityRoutePlan.PROF_CHANCE_NEEDS_DRINK_STUDYING;

                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1)
                                {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if(CHANCE2 < MAX_DRINK && agentMovement.getRoutePlan().getUrgentCtr() >= 1){
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR) {
                        if (action.getName() == UniversityAction.Name.GO_TO_CLASSROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseClassroomGoal(ProfTable.class, agentMovement.getCurrentState().getClassroomID());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                } else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                            if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR) {
                        if (action.getName() == UniversityAction.Name.SIT_PROFESSOR_TABLE) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.IN_CLASS_PROFESSOR) {
                        if (action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            } else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double CHANCE2 = Simulator.roll();
                                double MAX = UniversityRoutePlan.PROF_CHANCE_NEEDS_BATHROOM_STUDYING;
                                double MAX_DRINK = UniversityRoutePlan.PROF_CHANCE_NEEDS_DRINK_STUDYING;
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1)
                                {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if(CHANCE2 < MAX_DRINK && agentMovement.getRoutePlan().getUrgentCtr() >= 1){
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_TO_LUNCH) {
                        if (action.getName() == UniversityAction.Name.GO_TO_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseStall();
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
                        } else if (action.getName() == UniversityAction.Name.QUEUE_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            } else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        } else if (action.getName() == UniversityAction.Name.CHECKOUT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                //System.out.println("Done Checkout");
                                agentMovement.resetGoal();
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.EATING_LUNCH) {
                        if (action.getName() == UniversityAction.Name.FIND_SEAT_CAFETERIA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseGoal(EatTable.class);
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        //System.out.println("Find seat to Eating");
                                    }
                                }
                            }
                        } else if (action.getName() == UniversityAction.Name.LUNCH_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            } else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double CHANCE2 = Simulator.roll();
                                double MAX = UniversityRoutePlan.PROF_CHANCE_NEEDS_BATHROOM_STUDYING;
                                double MAX_DRINK = UniversityRoutePlan.PROF_CHANCE_NEEDS_DRINK_STUDYING;
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1)
                                {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if(CHANCE2 < MAX_DRINK && agentMovement.getRoutePlan().getUrgentCtr() >= 1){
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1); //Need -2 because need to go through the go to study room index
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    } else if (state.getName() == UniversityState.Name.GOING_HOME) {
                        if (action.getName() == UniversityAction.Name.LEAVE_BUILDING) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
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
                    //System.out.println(agentMovement.getCurrentAction().getName());
                    //System.out.println("StateIndex: " + agentMovement.getStateIndex());
                    break;

            }
        }

        if (agentMovement.isInteracting()) {
            // cases: early termination of interaction
            // reducing of interaction duration
            // termination of interaction
            if (agentMovement.getDuration() <= 0) {
                agentMovement.setInteracting(false);
                agentMovement.setInteractionType(null);
            }
            else {
                agentMovement.interact();
            }

        }
        else {
            List<Patch> patches = agentMovement.get7x7Field(agentMovement.getHeading(), true, agentMovement.getFieldOfViewAngle());
            for (Patch patch: patches) {
                for (Agent otherAgent: patch.getAgents()) {
                    UniversityAgent universityAgent = (UniversityAgent) otherAgent;
                    if (!universityAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), universityAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(universityAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), universityAgent.getAgentMovement().getProposedHeading(), universityAgent.getAgentMovement().getFieldOfViewAngle())){
                                agentMovement.rollAgentInteraction(universityAgent);
                                if (agentMovement.isInteracting()){ // interaction was successful
                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
                                    currentPatchCount[universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
                                }
                            }
                    if (agentMovement.isInteracting())
                        break;
                }
                if (agentMovement.isInteracting())
                    break;
            }
            patches = agentMovement.get3x3Field(agentMovement.getHeading(), true, Math.toRadians(270));
            for (Patch patch: patches){
                for (Agent otherAgent: patch.getAgents()){
                    UniversityAgent universityAgent = (UniversityAgent) otherAgent;
                    if (!universityAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), universityAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
                            if (Coordinates.isWithinFieldOfView(universityAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), universityAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
                                agentMovement.rollAgentInteraction(universityAgent);
                                if (agentMovement.isInteracting()){ // interaction was successful
                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
                                    currentPatchCount[universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
                                }
                            }
                    if (agentMovement.isInteracting())
                        break;
                }

                if (agentMovement.isInteracting())
                    break;
            }
        }
    }

    private void spawnAgent(University university, long currentTick) {
        UniversityGate gate = university.getUniversityGates().get(1);
        double spawnChance = gate.getChancePerTick();
        UniversityAgent agent = null;

        for (int i = 0; i < 4; i++){ // 4 gates
            Gate.GateBlock spawner = gate.getSpawners().get(i);
            double CHANCE = Simulator.roll();
            if (CHANCE < spawnChance){
                agent = university.getUnspawnedAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(university.getUnspawnedAgents().size()));
                if (agent.getType() == UniversityAgent.Type.STUDENT && UniversityAgent.studentCount < MAX_STUDENTS && currentStudentCount < MAX_CURRENT_STUDENTS){
                    agent.setAgentMovement(new UniversityAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick));
//                    university.getAgents().add(agent);
                    university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    currentStudentCount++;
                    UniversityAgent.studentCount++;
                    UniversityAgent.agentCount++;
                }
                else if (agent.getType() == UniversityAgent.Type.PROFESSOR && UniversityAgent.professorCount < MAX_PROFESSORS && currentProfessorCount < MAX_CURRENT_PROFESSORS){
                    agent.setAgentMovement(new UniversityAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick));
//                    university.getAgents().add(agent);
                    university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    currentProfessorCount++;
                    UniversityAgent.professorCount++;
                    UniversityAgent.agentCount++;
                }
            }
        }
    }

}