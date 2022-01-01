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
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class UniversitySimulator extends Simulator {

    private University university;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    private final int MAX_STUDENTS = 5;//250
    private final int MAX_PROFESSORS = 5;

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
        for (UniversityAgent agent : university.getAgents()) {
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
                            agentMovement.chooseGoal(Bench.class);
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
                                    System.out.println("Transition to SIT ON BENCH");
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
                            agentMovement.chooseGoal(Bulletin.class);
                            System.out.println("Finding Bulletin: ");
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {

                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                    //transition to VIEW_BULLETING
                                }
                            }
                        }
                        else {
                            System.out.println("i cant find bulletin");
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
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) { // If agent has reached the QueueuingPatchField
                                    // agentMovement.resetGoal();
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
                            agentMovement.chooseGoal(Toilet.class);
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
                                    System.out.println("Transition to RELIEVE_IN_CUBICLE");
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
                } else if (state.getName()== UniversityState.Name.NEEDS_DRINK) { //TODO: Needs DRINK
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
                        }
                    }
                    else if(action.getName()==UniversityAction.Name.DRINK_FOUNTAIN){
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                        if (agentMovement.getCurrentAction().getDuration() == 0) {
                            System.out.println(agentMovement.getParent().getId() + " = im done queueing");
                            agentMovement.leaveQueue();
                            agentMovement.setNextState(); //TODO: Return to previous state something
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
                                    System.out.println("Transition to FIND SEAT STUDYROOM");
                                }
                            }
                        }
                    }
                    else if (action.getName()==UniversityAction.Name.FIND_SEAT_STUDY_ROOM){
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseGoal(StudyTable.class);
                        }
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
                else if (state.getName()== UniversityState.Name.STUDYING) {
                    if (action.getName()==UniversityAction.Name.STUDY_AREA_STAY_PUT){
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                        if (agentMovement.getCurrentAction().getDuration() == 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.setDuration(agent.getAgentMovement().getDuration());
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

                } else if (state.getName()== UniversityState.Name.GOING_TO_CLASS_STUDENT) {//TODO: GO TO CLASS
                    /*Insert Action*/
//                    STATE_INDEX = 3;
                    if (action.getName()==UniversityAction.Name.GO_TO_CLASSROOM){
                        if(agentMovement.hasReachedGoalPatch()){
                            agentMovement.chooseClassroomDoor(state.getClassroomID());
                            state.setName(UniversityState.Name.WAIT_FOR_CLASS_STUDENT);
                        }
                    }

                }
                else if (state.getName()== UniversityState.Name.WAIT_FOR_CLASS_STUDENT) {
                    /*Insert Action*/
                    if(action.getName()==UniversityAction.Name.FIND_SEAT_CLASSROOM){
                        if(agentMovement.hasReachedGoalPatch()){
                            action.setName(UniversityAction.Name.CLASSROOM_STAY_PUT);
                        }
                    }
                    else if(action.getName()==UniversityAction.Name.CLASSROOM_STAY_PUT){
                        //do nothing
                    }
                    /*
                    if class starts{
                        state.setName(UniversityState.Name.IN_CLASS_STUDENT);
                    }
                     */
                }
                else if (state.getName()== UniversityState.Name.IN_CLASS_STUDENT) {
                    /*Insert Action*/
                    if(action.getName()==UniversityAction.Name.CLASSROOM_STAY_PUT){
                        //do nothing
                    }
                    else if(action.getName()==UniversityAction.Name.ASK_PROFESSOR_QUESTION){
                        //do something
                    }
                }
                else if (state.getName()== UniversityState.Name.GOING_TO_LUNCH) { //TODO: GO TO LUNCH
                    /*Insert Action*/
//                    STATE_INDEX = 4;
                    if(action.getName()==UniversityAction.Name.GO_TO_CAFETERIA){
                        if(agentMovement.hasReachedGoalPatch()){
                            action.setName(UniversityAction.Name.GO_TO_VENDOR);
                        }
                        else if(action.getName()==UniversityAction.Name.GO_TO_VENDOR){
                            if(agentMovement.hasReachedQueueingPatchField()){
                                action.setName(UniversityAction.Name.QUEUE_VENDOR);
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.QUEUE_VENDOR){
                            if(agentMovement.hasReachedGoalPatch()){
                                action.setName(UniversityAction.Name.CHECKOUT);
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.CHECKOUT){
                            if(action.getDuration()==0){ //if done with action
                                action.setName(UniversityAction.Name.FIND_SEAT_CAFETERIA);
                            }
                        }
                        else if(action.getName()==UniversityAction.Name.FIND_SEAT_CAFETERIA){
                            if(agentMovement.hasReachedGoalPatch()){
                                state.setName(UniversityState.Name.EATING_LUNCH);
                            }
                        }
                    }
                }
                else if (state.getName()== UniversityState.Name.EATING_LUNCH) {
                    /*Insert Action*/
                    if(action.getName()==UniversityAction.Name.LUNCH_STAY_PUT){
                        //after number of ticks transition to randomized state
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
//                else if (state.getName()== UniversityState.Name.NEEDS_BATHROOM) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.NEEDS_DRINK) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.GOING_TO_STUDY) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.STUDYING) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.GOING_TO_CLASS_STUDENT) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.WAIT_FOR_CLASS_STUDENT) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.IN_CLASS_STUDENT) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.GOING_TO_LUNCH) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.EATING_LUNCH) {
//                    /*Insert Action*/
//                } else if (state.getName()== UniversityState.Name.GOING_HOME) {
//                    /*Insert Action*/
//                }
//                break;

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
        }
        else if (spawnChance < CHANCE_SPAWN_0 && !isStudent0 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner0.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }

        if (spawnChance < CHANCE_SPAWN_1 && isStudent1 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner1.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }
        else if (spawnChance < CHANCE_SPAWN_1 && !isStudent1 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner1.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }

        if (spawnChance < CHANCE_SPAWN_2 && isStudent2 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner2.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }
        else if (spawnChance < CHANCE_SPAWN_2 && !isStudent2 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner2.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }

        if (spawnChance < CHANCE_SPAWN_3 && isStudent3 && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner3.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }
        else if (spawnChance < CHANCE_SPAWN_3 && !isStudent3 && UniversityAgent.professorCount != this.MAX_PROFESSORS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.PROFESSOR, spawner3.getPatch(), false, currentTick);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }
    }

}