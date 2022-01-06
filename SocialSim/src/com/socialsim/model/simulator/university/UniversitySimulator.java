package com.socialsim.model.simulator.university;

import com.socialsim.controller.Main;
import com.socialsim.controller.university.controls.UniversityScreenController;
import com.socialsim.model.core.agent.university.UniversityAction;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.agent.university.UniversityAgentMovement;
import com.socialsim.model.core.agent.university.UniversityState;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchfield.StudyArea;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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
    private final int MAX_STUDENTS = 5; //250
    private final int MAX_PROFESSORS = 0;
    private final int NUM_AGENTS = 500;

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
        university.createInitialAgentDemographics(NUM_AGENTS);
        UniversityAgent guard = university.getAgents().get(0); // 0
        guard.setAgentMovement(new UniversityAgentMovement(university.getPatch(57,12), guard, 1.27, university.getPatch(57,12).getPatchCenterCoordinates(), -1));
        university.getAgents().add(guard);
        university.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());

        UniversityAgent janitor1 = university.getAgents().get(1); // 1
        janitor1.setAgentMovement(new UniversityAgentMovement(university.getPatch(6,65), janitor1, 1.27, university.getPatch(6,65).getPatchCenterCoordinates(), -1));
        university.getAgents().add(janitor1);
        university.getAgentPatchSet().add(janitor1.getAgentMovement().getCurrentPatch());

        UniversityAgent janitor2 = university.getAgents().get(2); // 2
        janitor2.setAgentMovement(new UniversityAgentMovement(university.getPatch(7,66), janitor2, 1.27, university.getPatch(7,66).getPatchCenterCoordinates(), -1));
        university.getAgents().add(janitor2);
        university.getAgentPatchSet().add(janitor2.getAgentMovement().getCurrentPatch());
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
                            updateAgentsInUniversity(university);
                            spawnAgent(university, currentTick);
                            System.out.println(currentTick);
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

    public static void updateAgentsInUniversity(University university) throws InterruptedException { // Manage all agent-related updates
        moveAll(university);
    }

    private static void moveAll(University university) { // Make all agents move for one tick
        for (UniversityAgent agent : university.getMovableAgents()) {
            try {
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

        //System.out.println(" Amenity: " + agentMovement.getGoalAmenity() + " Path: " + agentMovement.getCurrentPath()
        //+ " Action: " + action.getName() + " Attractor: " + agentMovement.getGoalAttractor());

        boolean isFull = false; //to check if all amenities are not occupied

        // TODO: If interacting, then call functions. If not interacting, move
        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()){
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
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
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
                                    agentMovement.reachPatchInPath();
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
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                if (agentMovement.getCurrentAction().getDuration() == 0) {
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
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_BENCH){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(Bench.class)){
                                    isFull = true;
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                            get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }

                            if(isFull){
                                isFull = false;
                            }else{
                                //normal code
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                            System.out.println("Transition to SIT ON BENCH");
                                        }
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.SIT_ON_BENCH){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_BULLETIN){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(Bulletin.class)){
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    System.out.println("CANCEL VIEWING BULLETIN");
                                    isFull = true;
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                            //transition to VIEW_BULLETIN
                                        }
                                    }
                                }
                                else {
                                    System.out.println("i cant find bulletin");
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.VIEW_BULLETIN){
                            // System.out.println("VIEWING BULLETING");
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE VIEWING BULLETING");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_BUILDING){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                System.out.println("LEAVING BUILDING");
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.despawn();
                                        System.out.println("Left the building:Despawned");
                                    }
                                }
                            }
                        }

                    }
                    else if (state.getName()== UniversityState.Name.NEEDS_BATHROOM) {

                        /*Insert Action*/
                        if (action.getName()==UniversityAction.Name.GO_TO_BATHROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
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
                                        agentMovement.resetGoal();
                                        System.out.println("Transition to FIND_CUBICLE");
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_CUBICLE){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(Toilet.class)){
                                    isFull = true;
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 2);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    agentMovement.resetGoal();
                                    System.out.println("Transition to Wash  in sink");
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                            // agentMovement.resetGoal();
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                            System.out.println("Transition to RELIEVE_IN_CUBICLE");
                                        }
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.RELIEVE_IN_CUBICLE){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                agentMovement.resetGoal();
                                System.out.println("Transition to Wash  in sink");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.WASH_IN_SINK){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseGoal(Sink.class);
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                }
                            }
                            else{
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                if (agentMovement.getCurrentAction().getDuration() == 0) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    agentMovement.resetGoal();
                                    System.out.println("Transition to Leave Bathroom");
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_BATHROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                //declare bathroom door in routeplan
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        // agentMovement.resetGoal();
                                        agentMovement.setNextState();
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        System.out.println("Transition to NEXT STATE LEFT BATHROOM");
                                        //TODO:Return to previous state.
                                    }
                                }
                            }
                        }
                    } else if (state.getName()== UniversityState.Name.NEEDS_DRINK) {
                        /*Insert Action*/
                        if(action.getName()==UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN){
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
                        }
                        else if(action.getName()==UniversityAction.Name.QUEUE_FOUNTAIN){
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
                        else if(action.getName()==UniversityAction.Name.DRINK_FOUNTAIN){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                System.out.println(agentMovement.getParent().getId() + " = im done queueing");
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                System.out.println("Done DRINKING");
                                agentMovement.resetGoal();
                            }
                        }
                    } else if (state.getName()== UniversityState.Name.GOING_TO_STUDY) {
                        if (action.getName()==UniversityAction.Name.GO_TO_STUDY_ROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        // agentMovement.resetGoal();
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                        agentMovement.resetGoal();
                                        System.out.println("Transition to FIND SEAT STUDY ROOM");
                                    }
                                }
                            }
                        }
                        else if (action.getName()==UniversityAction.Name.FIND_SEAT_STUDY_ROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(StudyTable.class)){
                                    isFull = true;
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    agentMovement.resetGoal();
                                    System.out.println("Leave study area");
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState();
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            System.out.println("Find_seat_studyroom to Studying");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.STUDYING) {
                        if (action.getName()==UniversityAction.Name.STUDY_AREA_STAY_PUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE STUDYING");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_STUDY_AREA){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        agentMovement.setNextState();
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        System.out.println("LEFT THE STUDY AREA");
                                    }
                                }
                            }
                        }

                    } else if (state.getName()== UniversityState.Name.GOING_TO_CLASS_STUDENT) {
                        /*Insert Action*/
//                    STATE_INDEX = 3;
                        if (action.getName()==UniversityAction.Name.GO_TO_CLASSROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseClassroomDoor(3);
                                if(agentMovement.getGoalAttractor() == null && agentMovement.getGoalAmenity() == null){
                                    agentMovement.stop();
                                }
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState();
                                            agentMovement.setActionIndex(0);
                                            agentMovement.resetGoal();
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            System.out.println("Transition to FIND_SEAT");
                                        }
                                    }
                                }
                            }
                        }

                    }
                    else if (state.getName()== UniversityState.Name.WAIT_FOR_CLASS_STUDENT) {
                        /*Insert Action*/
                        if(action.getName()==UniversityAction.Name.FIND_SEAT_CLASSROOM){
                            if(agentMovement.getGoalAmenity() == null){
                                agentMovement.chooseGoal(Chair.class);

                            }if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                        System.out.println("Action to CLASSROOM_STAY_PUT");
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.CLASSROOM_STAY_PUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                System.out.println("DONE WAITING FOR CLASS");
                            }
                        }
                    /*
                    if class starts{
                        state.setName(UniversityState.Name.IN_CLASS_STUDENT);
                    }
                     */
                    }
                    else if (state.getName()== UniversityState.Name.IN_CLASS_STUDENT) {

                        if(action.getName()==UniversityAction.Name.CLASSROOM_STAY_PUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE CLASSROOM_STAY_PUT");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_CLASSROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseClassroomDoor(3); // TODO classroom id hardcoded
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        agentMovement.setNextState();
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        System.out.println("LEFT THE CLASSROOM");
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.ASK_PROFESSOR_QUESTION){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(ProfTable.class)){
                                    isFull = true;
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    //System.out.println("Class Done");
                                    System.out.println(agentMovement.getGoalAttractor());
                                }else{
                                    System.out.println("Ask Professor");
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                            agentMovement.setNextState();
                                            agentMovement.setActionIndex(0);
                                            agentMovement.resetGoal();
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            System.out.println("Transition to LEAVE CLASSROOM");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.GOING_TO_LUNCH) {
//                    if(action.getName()==UniversityAction.Name.GO_TO_CAFETERIA){
//                            if (agentMovement.getGoalAmenity() == null) {
//                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                            }
//                            if (agentMovement.chooseNextPatchInPath()) {
//                                agentMovement.faceNextPosition();
//                                agentMovement.moveSocialForce();
//                                if (agentMovement.hasReachedNextPatchInPath()) {
//                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
//                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
//                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        System.out.println("Transition to GO_TO_VENDOR");
//                                    }
//                                }
//                            }
//                        }
                        if(action.getName()==UniversityAction.Name.GO_TO_VENDOR){
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseStall();
//                            agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getStalls().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
//                            agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getStalls().get(0)); //
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
                        else if(action.getName()==UniversityAction.Name.QUEUE_VENDOR){
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
                        else if(action.getName()==UniversityAction.Name.CHECKOUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                System.out.println(agentMovement.getParent().getId() + " = im done queueing");
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                System.out.println("Done Checkout");
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.EATING_LUNCH) {
                        if(action.getName()==UniversityAction.Name.FIND_SEAT_CAFETERIA){
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
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));;
                                        System.out.println("Find seat to Eating");
                                    }
                                }
                            }
                        }
                        else if (action.getName()==UniversityAction.Name.LUNCH_STAY_PUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE EATING");
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.GOING_HOME) {
                        if(action.getName()==UniversityAction.Name.LEAVE_BUILDING){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                System.out.println("LEAVING BUILDING");
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.despawn();
                                        System.out.println("Left the building:Despawned");
                                    }
                                }
                            }
                        }
                    }
                    break;

                case PROFESSOR:

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
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                if (agentMovement.getCurrentAction().getDuration() == 0) {
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
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_BENCH){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(Bench.class)){
                                    isFull = true;
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                            get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }

                            if(isFull){
                                isFull = false;
                            }else{
                                //normal code
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                            System.out.println("Transition to SIT ON BENCH");
                                        }
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.SIT_ON_BENCH){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_BULLETIN){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(Bulletin.class)){
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    System.out.println("CANCEL VIEWING BULLETIN");
                                    isFull = true;
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                            //transition to VIEW_BULLETIN
                                        }
                                    }
                                }
                                else {
                                    System.out.println("i cant find bulletin");
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.VIEW_BULLETIN){
                            // System.out.println("VIEWING BULLETING");
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE VIEWING BULLETING");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_BUILDING){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                System.out.println("LEAVING BUILDING");
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.despawn();
                                        System.out.println("Left the building:Despawned");
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.GOING_TO_STUDY) {
                        if (action.getName()==UniversityAction.Name.GO_TO_STUDY_ROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        // agentMovement.resetGoal();
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                        agentMovement.resetGoal();
                                        System.out.println("Transition to FIND SEAT STUDY ROOM");
                                    }
                                }
                            }
                        }
                        else if (action.getName()==UniversityAction.Name.FIND_SEAT_STUDY_ROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(StudyTable.class)){
                                    isFull = true;
                                    agentMovement.setNextState();
                                    agentMovement.setActionIndex(1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    agentMovement.resetGoal();
                                    System.out.println("Leave study area");
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState();
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            System.out.println("Find_seat_studyroom to Studying");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.STUDYING) {
                        if (action.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT) {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE STUDYING");
                            }
                        } else if (action.getName() == UniversityAction.Name.LEAVE_STUDY_AREA) {
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        agentMovement.setNextState();
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        System.out.println("LEFT THE STUDY AREA");
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.NEEDS_BATHROOM) {

                        /*Insert Action*/
                        if (action.getName()==UniversityAction.Name.GO_TO_BATHROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
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
                                        agentMovement.resetGoal();
                                        System.out.println("Transition to FIND_CUBICLE");
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_CUBICLE){
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseGoal(Toilet.class)){
                                    isFull = true;
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 2);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    agentMovement.resetGoal();
                                    System.out.println("Transition to Wash  in sink");
                                }
                            }
                            if(isFull){
                                isFull = false;
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                            // agentMovement.resetGoal();
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                            System.out.println("Transition to RELIEVE_IN_CUBICLE");
                                        }
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.RELIEVE_IN_CUBICLE){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                agentMovement.resetGoal();
                                System.out.println("Transition to Wash  in sink");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.WASH_IN_SINK){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseGoal(Sink.class);
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                }
                            }
                            else{
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                if (agentMovement.getCurrentAction().getDuration() == 0) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    agentMovement.resetGoal();
                                    System.out.println("Transition to Leave Bathroom");
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_BATHROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                //declare bathroom door in routeplan
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        // agentMovement.resetGoal();
                                        agentMovement.setNextState();
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        System.out.println("Transition to NEXT STATE LEFT BATHROOM");
                                        //TODO:Return to previous state.
                                    }
                                }
                            }
                        }
                    } else if (state.getName()== UniversityState.Name.NEEDS_DRINK) {
                        /*Insert Action*/
                        if(action.getName()==UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN){
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
                        }
                        else if(action.getName()==UniversityAction.Name.QUEUE_FOUNTAIN){
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
                        else if(action.getName()==UniversityAction.Name.DRINK_FOUNTAIN){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                System.out.println(agentMovement.getParent().getId() + " = im done queueing");
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                System.out.println("Done DRINKING");
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.GOING_TO_CLASS_PROFESSOR) {
                        /*Insert Action*/
//                    STATE_INDEX = 3;
                        if (action.getName()==UniversityAction.Name.GO_TO_CLASSROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseClassroomDoor(3);
                                if(agentMovement.getGoalAttractor() == null && agentMovement.getGoalAmenity() == null){
                                    agentMovement.stop();
                                }
                            }else{
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState();
                                            agentMovement.setActionIndex(0);
                                            agentMovement.resetGoal();
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            System.out.println("Transition to FIND_SEAT");
                                        }
                                    }
                                }
                            }
                        }

                    }
                    else if (state.getName()== UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR) {
                        /*Insert Action*/
                        if(action.getName()==UniversityAction.Name.FIND_SEAT_CLASSROOM){
                            if(agentMovement.getGoalAmenity() == null){
                                agentMovement.chooseGoal(ProfTable.class);

                            }if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                        System.out.println("Action to SIT_PROFESSOR_TABLE");
                                    }
                                }
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.SIT_PROFESSOR_TABLE){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                System.out.println("DONE WAITING FOR CLASS");
                            }
                        }
                    /*
                    if class starts{
                        state.setName(UniversityState.Name.IN_CLASS_STUDENT);
                    }
                     */
                    }
                    else if (state.getName()== UniversityState.Name.IN_CLASS_PROFESSOR) {

                        if(action.getName()==UniversityAction.Name.CLASSROOM_STAY_PUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE CLASSROOM_STAY_PUT");
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.LEAVE_CLASSROOM){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseClassroomDoor(3); // TODO classroom id hardcoded
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                        agentMovement.setNextState();
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        System.out.println("LEFT THE CLASSROOM");
                                    }
                                }
                            }
                        }
//                    else if(action.getName()==UniversityAction.Name.ASK_PROFESSOR_QUESTION){
//                        if (agentMovement.getGoalAmenity() == null) {
//                            if(!agentMovement.chooseGoal(ProfTable.class)){
//                                isFull = true;
//                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                agentMovement.resetGoal();
//                                //System.out.println("Class Done");
//                                System.out.println(agentMovement.getGoalAttractor());
//                            }else{
//                                System.out.println("Ask Professor");
//                            }
//                        }
//                        if(isFull){
//                            isFull = false;
//                        }else{
//                            if (agentMovement.chooseNextPatchInPath()) {
//                                agentMovement.faceNextPosition();
//                                agentMovement.moveSocialForce();
//                                if (agentMovement.hasReachedNextPatchInPath()) {
//                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
//                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
//                                        agentMovement.setNextState();
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.resetGoal();
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        System.out.println("Transition to LEAVE CLASSROOM");
//                                    }
//                                }
//                            }
//                        }
//                    }
                    }
                    else if (state.getName()== UniversityState.Name.GOING_TO_LUNCH) {
//                    if(action.getName()==UniversityAction.Name.GO_TO_CAFETERIA){
//                            if (agentMovement.getGoalAmenity() == null) {
//                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
//                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
//                            }
//                            if (agentMovement.chooseNextPatchInPath()) {
//                                agentMovement.faceNextPosition();
//                                agentMovement.moveSocialForce();
//                                if (agentMovement.hasReachedNextPatchInPath()) {
//                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
//                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
//                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        System.out.println("Transition to GO_TO_VENDOR");
//                                    }
//                                }
//                            }
//                        }
                        if(action.getName()==UniversityAction.Name.GO_TO_VENDOR){
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseStall();
//                            agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getStalls().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
//                            agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getStalls().get(0)); //
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
                        else if(action.getName()==UniversityAction.Name.QUEUE_VENDOR){
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
                        else if(action.getName()==UniversityAction.Name.CHECKOUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                System.out.println(agentMovement.getParent().getId() + " = im done queueing");
                                agentMovement.leaveQueue();
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                System.out.println("Done Checkout");
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.EATING_LUNCH) {
                        if(action.getName()==UniversityAction.Name.FIND_SEAT_CAFETERIA){
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
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));;
                                        System.out.println("Find seat to Eating");
                                    }
                                }
                            }
                        }
                        else if (action.getName()==UniversityAction.Name.LUNCH_STAY_PUT){
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() == 0) {
                                agentMovement.setNextState();
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                System.out.println("DONE EATING");
                            }
                        }
                    }
                    else if (state.getName()== UniversityState.Name.GOING_HOME) {
                        if(action.getName()==UniversityAction.Name.LEAVE_BUILDING){
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                System.out.println("LEAVING BUILDING");
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.despawn();
                                        System.out.println("Left the building:Despawned");
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
        if (agentMovement.isInteracting()){
            // cases: early termination of interaction
            //reducing of interaction duration
            // termination of interaction
            if (agent.getAgentMovement().getCurrentAction().getDuration() == 0){

            }

        }
        else{
            //check for possible interactions; exclude those who are currently interacting
            agentMovement.get7x7Field(agentMovement.getHeading(), true, agentMovement.getFieldOfViewAngle());
        }

        // TODO: After moving, check for possible interactions
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
                if (agent.getType() == UniversityAgent.Type.STUDENT && currentStudentCount < this.MAX_STUDENTS && currentStudentCount < UniversityAgent.studentCount){
                    agent.setAgentMovement(new UniversityAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick));
                    university.getAgents().add(agent);
                    university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    currentStudentCount++;
                }
                else if (agent.getType() == UniversityAgent.Type.PROFESSOR && currentProfessorCount < this.MAX_PROFESSORS && currentProfessorCount < UniversityAgent.professorCount){
                    agent.setAgentMovement(new UniversityAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick));
                    university.getAgents().add(agent);
                    university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                    currentProfessorCount++;
                }
            }
        }
    }

}