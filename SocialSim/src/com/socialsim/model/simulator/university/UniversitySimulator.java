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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private int numStudents;
    private int numProfessors;

    public UniversitySimulator() {
        this.university = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(0, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.numStudents = 0;
        this.numProfessors = 0;
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
                        try {
                            updateAgentsInUniversity(university); // Update the pertinent variables when ticking
                            spawnAgent(university);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization; If the refreshes are frequent enough, update the visualization in a speed-aware manner
                        ((UniversityScreenController) Main.mainScreenController).drawUniversityViewForeground(Main.universitySimulator.getUniversity(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    // Manage all agent-related updates
    public static void updateAgentsInUniversity(University university) throws InterruptedException {
        moveAll(university);
    }

    // Make all agents move for one tick
    private static void moveAll(University university) {
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

        // Get the three agent movement states
        UniversityAgent.Type type = agent.getType();
        UniversityAgent.Persona persona = agent.getPersona();
        UniversityState state = agentMovement.getUniversityState();
        UniversityAction action = agentMovement.getUniversityAction();

        switch (type) {
            case GUARD:
                if (action.getName() == UniversityAction.Name.GUARD_STAY_PUT) {
                    // do nothing
                }
                else if (action.getName() == UniversityAction.Name.GREET_PERSON) {
                    // interact with person
                }

                break;

            case JANITOR:
                if (state.getName() == UniversityState.Name.MAINTENANCE_BATHROOM) {
                    if (action.getName() == UniversityAction.Name.CLEAN_STAY_PUT) {
                        // do nothing
                    }
                    else if (action.getName() == UniversityAction.Name.JANITOR_MOVE_SPOT) {
                        // move to another patch
                    }
                }
                else if (state.getName() == UniversityState.Name.MAINTENANCE_FOUNTAIN) {
                    if (action.getName() == UniversityAction.Name.JANITOR_CHECK_FOUNTAIN) {
                        // check fountain
                    }
                }

                break;

            case STUDENT:
                /* Add 1 to state counter for each case*/
                /*Insert Actions through each state*/
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


//            case ALIGHTING:
//                // The agent has entered the station and is heading towards the platform to board the train
//                switch (state) {
//                    case WALKING:
//                        if (action == AgentMovement.Action.WILL_QUEUE) {
//                            // Look for the goal nearest to this agent
//                            agentMovement.chooseGoal();
//
//                            // Check if this agent is set to use a portal to go to another floor
//                            if (agentMovement.willHeadToPortal()) {
//                                // Make this agent face the goal portal
//                                agentMovement.faceNextPosition();
//
//                                // Move towards that direction
//                                agentMovement.moveSocialForce();
//
//                                // Set the appropriate action
//                                if (agentMovement.isGoalFloorLower()) {
//                                    agentMovement.setAction(AgentMovement.Action.WILL_DESCEND);
//                                    action = AgentMovement.Action.WILL_DESCEND;
//                                } else {
//                                    agentMovement.setAction(AgentMovement.Action.WILL_ASCEND);
//                                    action = AgentMovement.Action.WILL_ASCEND;
//                                }
//
//                                break;
//                            } else {
//                                // This agent is set to stay on this floor, so simply move towards its goal
//                                if (
//                                        agentMovement.getParent().getTicketType()
//                                                == TicketBooth.TicketType.SINGLE_JOURNEY
//                                                || agentMovement.getParent().getTicketType()
//                                                == TicketBooth.TicketType.STORED_VALUE
//                                                && !agentMovement.willPathFind()
//                                ) {
//                                    // Make this agent face the set goal, its queueing area, or the agent at the
//                                    // tail of the queue
//                                    agentMovement.faceNextPosition();
//
//                                    // Move towards that direction
//                                    agentMovement.moveSocialForce();
//
//                                    if (agentMovement.hasEncounteredAgentToFollow()) {
//                                        // If the agent did not move, and there is someone blocking it while queueing,
//                                        // transition into the "in queue" state and the "assembling" action
//                                        agentMovement.joinQueue();
//
//                                        agentMovement.setState(AgentMovement.State.IN_QUEUE);
//                                        state = AgentMovement.State.IN_QUEUE;
//
//                                        agentMovement.setAction(AgentMovement.Action.ASSEMBLING);
//                                        action = AgentMovement.Action.ASSEMBLING;
//
//                                        // If this agent is a stored value card holder, signal that there will
//                                        // be no more need to pathfind
//                                        if (
//                                                agentMovement.getParent().getTicketType()
//                                                        == TicketBooth.TicketType.STORED_VALUE
//                                        ) {
//                                            agentMovement.endStoredValuePathfinding();
//                                        }
//
//                                        break;
//                                    }
//
//                                    // Check whether the agent's next amenity is a queueable
//                                    // If it is, check whether the agent has reached its floor field
//                                    if (agentMovement.isNextAmenityQueueable()) {
//                                        // If the agent has reached the patch with the nearest floor field value,
//                                        // transition
//                                        // into the "in queue" state and the "queueing" action
//                                        if (agentMovement.hasReachedQueueingPatchField()) {
//                                            // Mark this agent as the latest one to join its queue
//                                            agentMovement.joinQueue();
//
//                                            agentMovement.setState(AgentMovement.State.IN_QUEUE);
//                                            state = AgentMovement.State.IN_QUEUE;
//
//                                            if (agentMovement.isNextAmenityTrainDoor()) {
//                                                agentMovement.setAction(AgentMovement.Action.WAITING_FOR_TRAIN);
//                                                action = AgentMovement.Action.WAITING_FOR_TRAIN;
//                                            } else {
//                                                agentMovement.setAction(AgentMovement.Action.QUEUEING);
//                                                action = AgentMovement.Action.QUEUEING;
//                                            }
//
//                                            // If this agent is a stored value card holder, signal that there will
//                                            // be no more need to pathfind
//                                            if (
//                                                    agentMovement.getParent().getTicketType()
//                                                            == TicketBooth.TicketType.STORED_VALUE
//                                            ) {
//                                                agentMovement.endStoredValuePathfinding();
//                                            }
//
//                                            break;
//                                        }
//                                    } else {
//                                        // If the agent has reached its non-queueable goal, transition into the
//                                        // appropriate state and action
//                                        // This non-queueable goal could only be a station gate, so exit the station
//                                        if (agentMovement.hasReachedGoal()) {
//                                            // Have the agent set its current goal
//                                            agentMovement.reachGoal();
//
//                                            // Then have this agent marked for despawning
//                                            agentsToDespawn.add(agent);
//
//                                            break;
//                                        }
//                                    }
//
//                                    // If the agent is stuck, switch to the "rerouting" action except if the
//                                    // agent is a stored value ticket holder
//                                    if (
//                                            agentMovement.isStuck()
//                                                    && agentMovement.getState() != AgentMovement.State.IN_QUEUE
///*                                                    && agentMovement.getParent().getTicketType()
//                                                    != TicketBooth.TicketType.STORED_VALUE*/
//                                    ) {
//                                        agentMovement.setAction(AgentMovement.Action.REROUTING);
//                                        action = AgentMovement.Action.REROUTING;
//                                    }
//
//                                    break;
//                                } else {
//                                    // This agent is a stored value ticket holder so generate a path, if one hasn't
//                                    // been generated yet, then follow it until the agent reaches its goal
//                                    // Get the next path
//                                    if (agentMovement.chooseNextPatchInPath()) {
//                                        // Make this agent face that patch
//                                        agentMovement.faceNextPosition();
//
//                                        // Move towards that patch
//                                        agentMovement.moveSocialForce();
//
//                                        if (agentMovement.hasEncounteredAgentToFollow()) {
//                                            // If the agent did not move, and there is someone blocking it while
//                                            // queueing, transition into the "in queue" state and the "assembling"
//                                            // action
//                                            agentMovement.joinQueue();
//
//                                            agentMovement.setState(AgentMovement.State.IN_QUEUE);
//                                            state = AgentMovement.State.IN_QUEUE;
//
//                                            agentMovement.setAction(AgentMovement.Action.ASSEMBLING);
//                                            action = AgentMovement.Action.ASSEMBLING;
//
//                                            agentMovement.endStoredValuePathfinding();
//
//                                            break;
//                                        }
//
//                                        if (agentMovement.hasReachedNextPatchInPath()) {
//                                            // The agent has reached the next patch in the path, so remove this from
//                                            // this agent's current path
//                                            agentMovement.reachPatchInPath();
//
//                                            // Check if there are still patches left in the path
//                                            // If there are no more patches left, revert back to the "will queue" action
//                                            if (agentMovement.hasAgentReachedFinalPatchInPath()) {
//                                                agentMovement.setState(AgentMovement.State.WALKING);
//                                                state = AgentMovement.State.WALKING;
//
//                                                agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                                action = AgentMovement.Action.WILL_QUEUE;
//
//                                                agentMovement.endStoredValuePathfinding();
//                                            }
//
//                                            break;
//                                        }
//                                    } else {
//                                        // No more next patches, so transition back into the walking state
//                                        agentMovement.setState(AgentMovement.State.WALKING);
//                                        state = AgentMovement.State.WALKING;
//
//                                        agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                        action = AgentMovement.Action.WILL_QUEUE;
//
//                                        agentMovement.endStoredValuePathfinding();
//
//                                        break;
//                                    }
//                                }
//                            }
//                        } else if (
//                                action == AgentMovement.Action.WILL_DESCEND
//                                        || action == AgentMovement.Action.WILL_ASCEND
//                        ) {
//                            // Check if the agent is set to switch floors
//                            // If it is, this agent will now head to its chosen portal
//                            if (
//                                    agentMovement.getParent().getTicketType()
//                                            == TicketBooth.TicketType.SINGLE_JOURNEY
//                                            || agentMovement.getParent().getTicketType()
//                                            == TicketBooth.TicketType.STORED_VALUE
//                                            && !agentMovement.willPathFind()
//                            ) {
//                                // Look for the goal nearest to this agent
//                                agentMovement.chooseGoal();
//
//                                // Make this agent face its portal
//                                agentMovement.faceNextPosition();
//
//                                // Then make the agent move towards that exit
//                                agentMovement.moveSocialForce();
//                            } else {
//                                // This agent is a stored value ticket holder so generate a path, if one hasn't been
//                                // generated yet, then follow it until the agent reaches its goal
//                                // Get the next path
//                                if (agentMovement.chooseNextPatchInPath()) {
//                                    // Make this agent face that patch
//                                    agentMovement.faceNextPosition();
//
//                                    // Move towards that patch
//                                    agentMovement.moveSocialForce();
//
//                                    if (agentMovement.hasReachedNextPatchInPath()) {
//                                        // The agent has reached the next patch in the path, so remove this from
//                                        // this agent's current path
//                                        agentMovement.reachPatchInPath();
//
//                                        // Check if there are still patches left in the path
//                                        // If there are no more patches left, stop using any pathfinding algorithm
//                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
//                                            agentMovement.endStoredValuePathfinding();
//                                        }
//                                    }
//                                } else {
//                                    agentMovement.endStoredValuePathfinding();
//                                }
//                            }
//
//                            if (agentMovement.hasEncounteredPortalWaitingAgent()) {
//                                agentMovement.beginWaitingOnPortal();
//                            } else {
//                                agentMovement.endWaitingOnPortal();
//                            }
//
//                            // Check if the agent is now at the portal
//                            if (
//                                    agentMovement.hasReachedGoal()
//                            ) {
//                                agentMovement.beginWaitingOnPortal();
//
//                                if (agentMovement.willEnterPortal()) {
//                                    agentMovement.endWaitingOnPortal();
//
//                                    // Have the agent set its current goal
//                                    agentMovement.reachGoal();
//
//                                    // Reset the current goal of the agent
//                                    agentMovement.resetGoal(false);
//
//                                    // Then have this agent marked for floor switching
//                                    agentsToSwitchFloors.add(agent);
//                                } else {
//                                    agentMovement.stop();
//                                }
//
//                                break;
//                            } else {
//                                if (agentMovement.willEnterPortal()) {
//                                    agentMovement.endWaitingOnPortal();
//                                }
//                            }
//
//                            // If the agent is stuck, switch to the "rerouting" action except if the agent
//                            // is a stored value ticket holder
//                            if (
//                                    agentMovement.isStuck()/*
//                                            && agentMovement.getParent().getTicketType()
//                                            != TicketBooth.TicketType.STORED_VALUE*/
//                            ) {
//                                agentMovement.setAction(AgentMovement.Action.REROUTING);
//                                action = AgentMovement.Action.REROUTING;
//                            }
//
//                            break;
//                        } else if (action == AgentMovement.Action.EXITING_STATION) {
//                            // This agent is ready to exit
//                            agentMovement.prepareForStationExit();
//
//                            // This agent is now heading to its chosen exit
//                            agentMovement.chooseGoal();
//
//                            // Check if this agent is set to use a portal to go to another floor
//                            if (agentMovement.willHeadToPortal()) {
//                                // Make this agent face the goal portal
//                                agentMovement.faceNextPosition();
//
//                                // Move towards that direction
//                                agentMovement.moveSocialForce();
//
//                                // Set the appropriate action
//                                if (agentMovement.isGoalFloorLower()) {
//                                    agentMovement.setAction(AgentMovement.Action.WILL_DESCEND);
//                                    action = AgentMovement.Action.WILL_DESCEND;
//                                } else {
//                                    agentMovement.setAction(AgentMovement.Action.WILL_ASCEND);
//                                    action = AgentMovement.Action.WILL_ASCEND;
//                                }
//                            }
//
//                            if (
//                                    agentMovement.getParent().getTicketType()
//                                            == TicketBooth.TicketType.SINGLE_JOURNEY
//                                            || agentMovement.getParent().getTicketType()
//                                            == TicketBooth.TicketType.STORED_VALUE
//                                            && !agentMovement.willPathFind()
//                            ) {
//                                // Make this agent face its exit
//                                agentMovement.faceNextPosition();
//
//                                // Then make the agent move towards that exit
//                                agentMovement.moveSocialForce();
//                            } else {
//                                // This agent is a stored value ticket holder so generate a path, if one hasn't been
//                                // generated yet, then follow it until the agent reaches its goal
//                                // Get the next path
//                                if (agentMovement.chooseNextPatchInPath()) {
//                                    // Make this agent face that patch
//                                    agentMovement.faceNextPosition();
//
//                                    // Move towards that patch
//                                    agentMovement.moveSocialForce();
//
//                                    if (agentMovement.hasReachedNextPatchInPath()) {
//                                        // The agent has reached the next patch in the path, so remove this from
//                                        // this agent's current path
//                                        agentMovement.reachPatchInPath();
//
//                                        // Check if there are still patches left in the path
//                                        // If there are no more patches left, stop using any pathfinding algorithm
//                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
//                                            agentMovement.endStoredValuePathfinding();
//                                        }
//                                    }
//                                } else {
//                                    agentMovement.endStoredValuePathfinding();
//                                }
//                            }
//
//                            // Check if the agent is now at the exit
//                            if (agentMovement.hasReachedGoal()) {
//                                // Have the agent set its current goal
//                                agentMovement.reachGoal();
//
//                                // Then have this agent marked for despawning
//                                agentsToDespawn.add(agent);
//
//                                break;
//                            }
//
//                            // If the agent is stuck, switch to the "rerouting" action except if the agent
//                            // is a stored value ticket holder
//                            if (
//                                    agentMovement.isStuck()/*
//                                            && agentMovement.getParent().getTicketType()
//                                            != TicketBooth.TicketType.STORED_VALUE*/
//                            ) {
//                                agentMovement.setAction(AgentMovement.Action.REROUTING);
//                                action = AgentMovement.Action.REROUTING;
//
//                                break;
//                            }
//
//                            break;
//                        } else if (action == AgentMovement.Action.REROUTING) {
//                            // This agent is stuck, so generate a path, if one hasn't been generated yet, then
//                            // follow it until the agent is not stuck anymore
//                            // Get the next path
//                            if (agentMovement.chooseNextPatchInPath()) {
//                                // Make this agent face that patch
//                                agentMovement.faceNextPosition();
//
//                                // Move towards that patch
//                                agentMovement.moveSocialForce();
//
//                                // Check if the agent has reached its goal
//                                if (agentMovement.hasReachedGoal()) {
//                                    // Have the agent set its current goal
//                                    agentMovement.reachGoal();
//
//                                    if (agentMovement.getGoalAmenity() instanceof StationGate) {
//                                        agentMovement.setState(AgentMovement.State.WALKING);
//                                        state = AgentMovement.State.WALKING;
//
//                                        agentMovement.setAction(AgentMovement.Action.EXITING_STATION);
//                                        action = AgentMovement.Action.EXITING_STATION;
//                                    } else {
//                                        agentMovement.setState(AgentMovement.State.WALKING);
//                                        state = AgentMovement.State.WALKING;
//
//                                        agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                        action = AgentMovement.Action.WILL_QUEUE;
//                                    }
//
//                                    break;
//                                }
//
//                                if (agentMovement.isReadyToFree()) {
//                                    // If the agent has been moving again for a consistent period of time, free the
//                                    // agent and don't follow the path anymore
//                                    if (agentMovement.getGoalAmenity() instanceof StationGate) {
//                                        agentMovement.setState(AgentMovement.State.WALKING);
//                                        state = AgentMovement.State.WALKING;
//
//                                        agentMovement.setAction(AgentMovement.Action.EXITING_STATION);
//                                        action = AgentMovement.Action.EXITING_STATION;
//                                    } else {
//                                        agentMovement.setState(AgentMovement.State.WALKING);
//                                        state = AgentMovement.State.WALKING;
//
//                                        agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                        action = AgentMovement.Action.WILL_QUEUE;
//                                    }
//
//                                    // Then this agent will not be stuck anymore
//                                    agentMovement.free();
//
//                                    break;
//                                }
//
//                                if (agentMovement.hasEncounteredAgentToFollow()) {
//                                    // If the agent did not move, and there is someone blocking it while queueing,
//                                    // transition into the "in queue" state and the "assembling" action
//                                    agentMovement.joinQueue();
//
//                                    agentMovement.setState(AgentMovement.State.IN_QUEUE);
//                                    state = AgentMovement.State.IN_QUEUE;
//
//                                    agentMovement.setAction(AgentMovement.Action.ASSEMBLING);
//                                    action = AgentMovement.Action.ASSEMBLING;
//
//                                    // Then this agent will not be stuck anymore
//                                    agentMovement.free();
//
//                                    break;
//                                }
//
//                                if (agentMovement.getGoalAmenity() instanceof Portal) {
//                                    // Check if the agent is now at the portal
//                                    if (
//                                            agentMovement.hasReachedGoal()
//                                    ) {
//                                        agentMovement.beginWaitingOnPortal();
//
//                                        if (agentMovement.willEnterPortal()) {
//                                            agentMovement.endWaitingOnPortal();
//
//                                            // Have the agent set its current goal
//                                            agentMovement.reachGoal();
//
//                                            // Reset the current goal of the agent
//                                            agentMovement.resetGoal(false);
//
//                                            // Then have this agent marked for floor switching
//                                            agentsToSwitchFloors.add(agent);
//                                        } else {
//                                            agentMovement.stop();
//                                        }
//
//                                        break;
//                                    } else {
//                                        if (agentMovement.willEnterPortal()) {
//                                            agentMovement.endWaitingOnPortal();
//                                        }
//                                    }
//                                }
//
//                                if (agentMovement.hasReachedNextPatchInPath()) {
//                                    // The agent has reached the next patch in the path, so remove this from this
//                                    // agent's current path
//                                    agentMovement.reachPatchInPath();
//
//                                    // Check if there are still patches left in the path
//                                    // If there are no more patches left, revert back to the "will queue" action
//                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
//                                        if (agentMovement.getGoalAmenity() instanceof StationGate) {
//                                            agentMovement.setState(AgentMovement.State.WALKING);
//                                            state = AgentMovement.State.WALKING;
//
//                                            agentMovement.setAction(AgentMovement.Action.EXITING_STATION);
//                                            action = AgentMovement.Action.EXITING_STATION;
//                                        } else {
//                                            agentMovement.setState(AgentMovement.State.WALKING);
//                                            state = AgentMovement.State.WALKING;
//
//                                            agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                            action = AgentMovement.Action.WILL_QUEUE;
//                                        }
//
//                                        // Then this agent will not be stuck anymore
//                                        agentMovement.free();
//                                    }
//
//                                    break;
//                                }
//                            } else {
//                                if (agentMovement.getGoalAmenity() instanceof StationGate) {
//                                    agentMovement.setState(AgentMovement.State.WALKING);
//                                    state = AgentMovement.State.WALKING;
//
//                                    agentMovement.setAction(AgentMovement.Action.EXITING_STATION);
//                                    action = AgentMovement.Action.EXITING_STATION;
//                                } else {
//                                    agentMovement.setState(AgentMovement.State.WALKING);
//                                    state = AgentMovement.State.WALKING;
//
//                                    agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                    action = AgentMovement.Action.WILL_QUEUE;
//                                }
//
//                                // Then this agent will not be stuck anymore
//                                agentMovement.free();
//
//                                break;
//                            }
//                        }
//                    case IN_NONQUEUEABLE:
//                        // TODO: Like in IN_QUEUEABLE
//                        break;
//                    case IN_QUEUE:
//                        if (action == AgentMovement.Action.ASSEMBLING) {
//                            // The agent is not yet in the queueing area, but is already queueing
//                            // So keep following the end of the queue until the queueing area is reached
//                            // Make this agent face the set goal, its queueing area, or the agent at the tail of
//                            // the queue
//                            agentMovement.faceNextPosition();
//
//                            // Move towards that direction
//                            agentMovement.moveSocialForce();
//
//                            if (agentMovement.isReadyToFree()) {
//                                // Then this agent will not be stuck anymore
//                                agentMovement.free();
//                            }
//
//                            // Check whether the agent has reached its floor field
//                            // If the agent has reached the patch with the nearest floor field value, transition
//                            // into the "queueing" action
//                            if (agentMovement.hasReachedQueueingPatchField()) {
//                                if (agentMovement.isNextAmenityTrainDoor()) {
//                                    agentMovement.setAction(AgentMovement.Action.WAITING_FOR_TRAIN);
//                                    action = AgentMovement.Action.WAITING_FOR_TRAIN;
//                                } else {
//                                    agentMovement.setAction(AgentMovement.Action.QUEUEING);
//                                    action = AgentMovement.Action.QUEUEING;
//                                }
//                            }
//
//                            // Check if this agent has not encountered a queueing agent anymore
//                            if (!agentMovement.hasEncounteredAgentToFollow()) {
//                                // If the agent did not move, and there is someone blocking it while queueing,
//                                // transition into the "in queue" state and the "assembling" action
//                                agentMovement.leaveQueue();
//
//                                agentMovement.setState(AgentMovement.State.WALKING);
//                                state = AgentMovement.State.WALKING;
//
//                                agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                action = AgentMovement.Action.WILL_QUEUE;
//
//                                break;
//                            }
//                        } else if (action == AgentMovement.Action.QUEUEING) {
//                            // The agent is still queueing, so follow the path set by the floor field and its values
//                            // Only move if the agent is not waiting for the amenity to be vacant
//                            if (!agentMovement.isWaitingOnAmenity()) {
//                                // In its neighboring patches, look for the patch with the highest floor field
//                                agentMovement.chooseBestQueueingPatch();
//
//                                // Make this agent face that patch
//                                agentMovement.faceNextPosition();
//
//                                // Move towards that patch
//                                agentMovement.moveSocialForce();
//                            }
//
//                            if (agentMovement.isReadyToFree()) {
//                                // Then this agent will not be stuck anymore
//                                agentMovement.free();
//                            }
//
//                            // Check if the agent is on one of the current floor field's apices
//                            // If not, keep following the floor field until it is reached
//                            if (agentMovement.hasReachedQueueingPatchFieldApex()) {
//                                // Have the agent waiting for the amenity to be vacant
//                                // TODO: Add waiting for turn state
//                                agentMovement.beginWaitingOnAmenity();
//
//                                // Check first if the goal of this agent is not currently serving other agents
//                                // If it is, the agent will now transition into the "heading to queueable" action
//                                // Do nothing if there is another agent still being serviced
//                                if (agentMovement.isGoalFree()) {
//                                    // The amenity is vacant, so no need to wait anymore
//                                    agentMovement.endWaitingOnAmenity();
//
//                                    // Have the amenity mark this agent as the one to be served next
//                                    agentMovement.beginServicingThisAgent();
//
//                                    agentMovement.setAction(AgentMovement.Action.HEADING_TO_QUEUEABLE);
//                                    action = AgentMovement.Action.HEADING_TO_QUEUEABLE;
//
//                                    // Then this agent will not be stuck anymore
//                                    agentMovement.free();
//                                } else {
//                                    // Just stop and wait
//                                    agentMovement.stop();
//                                }
//                            }
//                        } else if (action == AgentMovement.Action.WAITING_FOR_TRAIN) {
//                            if (agentMovement.isReadyToFree()) {
//                                // Then this agent will not be stuck anymore
//                                agentMovement.free();
//                            }
//
//                            if (agentMovement.willEnterTrain()) {
//                                // Have the amenity mark this agent as the one to be served next
//                                agentMovement.beginServicingThisAgent();
//
//                                agentMovement.setAction(AgentMovement.Action.HEADING_TO_TRAIN_DOOR);
//                                action = AgentMovement.Action.HEADING_TO_TRAIN_DOOR;
//
//                                // Then this agent will not be stuck anymore
//                                agentMovement.free();
//                            } else {
//                                // In its neighboring patches, look for the patch with the highest floor field
//                                agentMovement.chooseBestQueueingPatch();
//
//                                // Make this agent face that patch
//                                agentMovement.faceNextPosition();
//
//                                // Move towards that patch
//                                agentMovement.moveSocialForce();
//                            }
//                        } else if (action == AgentMovement.Action.HEADING_TO_QUEUEABLE) {
//                            // Check if the agent is now in the goal
//                            if (agentMovement.hasReachedGoal()) {
//                                // Check if the agent is in a pure goal (an amenity with waiting time variables)
//                                if (agentMovement.isNextAmenityGoal()) {
//                                    // Transition into the "in queueable" state and the appropriate action
//                                    agentMovement.setState(AgentMovement.State.IN_QUEUEABLE);
//                                    state = AgentMovement.State.IN_QUEUEABLE;
//
//                                    if (agentMovement.getGoalAmenity() instanceof Security) {
//                                        agentMovement.setAction(AgentMovement.Action.SECURITY_CHECKING);
//                                        action = AgentMovement.Action.SECURITY_CHECKING;
//                                    } else if (agentMovement.getGoalAmenity() instanceof TicketBooth) {
//                                        agentMovement.setAction(AgentMovement.Action.TRANSACTING_TICKET);
//                                        action = AgentMovement.Action.TRANSACTING_TICKET;
//                                    } else if (agentMovement.getGoalAmenity() instanceof Turnstile) {
//                                        agentMovement.setAction(AgentMovement.Action.USING_TICKET);
//                                        action = AgentMovement.Action.USING_TICKET;
//                                    }
//                                } else {
//                                    // Either the next goal is an elevator
//                                    if (agentMovement.getGoalAmenity() instanceof ElevatorPortal) {
//                                        // TODO: The next goal is an elevator, so change to the appropriate actions and
//                                        // states
//                                    }
//                                }
//                            } else {
//                                // The agent has exited its goal's floor field and is now headed to the goal itself
//                                agentMovement.chooseGoal();
//
//                                // Make this agent face the set goal, or its queueing area
//                                agentMovement.faceNextPosition();
//
//                                // Then make the agent move towards that goal
//                                agentMovement.moveSocialForce();
//                            }
//                        } else if (action == AgentMovement.Action.HEADING_TO_TRAIN_DOOR) {
//                            // Check if the agent is now in the goal
//                            if (agentMovement.hasReachedGoal()) {
//                                if (agentMovement.willEnterTrain()) {
//                                    // Transition into the "in queueable" state and the appropriate action
//                                    agentMovement.setState(AgentMovement.State.IN_QUEUEABLE);
//                                    state = AgentMovement.State.IN_QUEUEABLE;
//
//                                    agentMovement.setAction(AgentMovement.Action.BOARDING_TRAIN);
//                                    action = AgentMovement.Action.BOARDING_TRAIN;
//                                } else {
//                                    agentMovement.endServicingThisAgent();
//
//                                    // The train door has closed, so revert to waiting for a train
//                                    agentMovement.setState(AgentMovement.State.IN_QUEUE);
//                                    state = AgentMovement.State.IN_QUEUE;
//
//                                    agentMovement.setAction(AgentMovement.Action.WAITING_FOR_TRAIN);
//                                    action = AgentMovement.Action.WAITING_FOR_TRAIN;
//                                }
//                            } else {
//                                if (agentMovement.willEnterTrain()) {
//                                    // The agent has exited its goal's floor field and is now headed to the goal itself
//                                    agentMovement.chooseGoal();
//
//                                    // Make this agent face the set goal, or its queueing area
//                                    agentMovement.faceNextPosition();
//
//                                    // Then make the agent move towards that goal
//                                    agentMovement.moveSocialForce();
//                                } else {
//                                    agentMovement.endServicingThisAgent();
//
//                                    // The train door has closed, so revert to waiting for a train
//                                    agentMovement.setState(AgentMovement.State.IN_QUEUE);
//                                    state = AgentMovement.State.IN_QUEUE;
//
//                                    agentMovement.setAction(AgentMovement.Action.WAITING_FOR_TRAIN);
//                                    action = AgentMovement.Action.WAITING_FOR_TRAIN;
//                                }
//                            }
//                        }
//                    case IN_QUEUEABLE: //TODO IN_QUEABLE
//                        if (action == AgentMovement.Action.BOARDING_TRAIN) {
//                            // Have this agent's goal wrap up serving this agent
//                            agentMovement.endServicingThisAgent();
//
//                            // Leave the queue
//                            agentMovement.leaveQueue();
//
//                            // Have the agent set its current goal
//                            agentMovement.reachGoal();
//
//                            // Then have this agent marked for boarding
//                            if (willDrawFromAgentList) {
//                                agentsToBoard.add(agent);
//                            } else {
//                                agentsToDespawn.add(agent);
//                            }
//                        } else if (
//                                action == AgentMovement.Action.ASCENDING
//                                        || action == AgentMovement.Action.DESCENDING
//                        ) {
//                            // Have the agent set its current goal
//                            agentMovement.reachGoal();
//
//                            // Leave the queue
//                            agentMovement.leaveQueue();
//                        } else if (
//                                action == AgentMovement.Action.SECURITY_CHECKING
//                                        || action == AgentMovement.Action.TRANSACTING_TICKET
//                                        || action == AgentMovement.Action.USING_TICKET
//                        ) {
//                            // Record the time it took
//                            switch (action) {
//                                case SECURITY_CHECKING:
//                                    agent.getAgentTime().passSecurity();
//
//                                    break;
//                                case USING_TICKET:
//                                    if (agentMovement.getDisposition() == AgentMovement.Disposition.BOARDING) {
//                                        agent.getAgentTime().tapInTurnstile();
//                                    } else {
//                                        agent.getAgentTime().tapOutTurnstile();
//                                    }
//
//                                    break;
//                            }
//
//                            // Have the agent set its current goal
//                            agentMovement.reachGoal();
//
//                            // Check if the agent is allowed passage by the goal
//                            // If it is, proceed to the next state
//                            // If not, wait for an additional second
//                            if (
//                                    agentMovement.isAllowedPass()
//                                            && (
//                                            action == AgentMovement.Action.TRANSACTING_TICKET
//                                                    || agentMovement.isFirstStepPositionFree()
//                                                    || (
//                                                    agentMovement.getCurrentTurnstileGate() != null
//                                                            && agentMovement.getCurrentTurnstileGate()
//                                                            .getTurnstileMode()
//                                                            == Turnstile.TurnstileMode.BIDIRECTIONAL
//                                            )
//                                    )
//                            ) {
//                                // Have this agent's goal wrap up serving this agent
//                                agentMovement.endServicingThisAgent();
//
//                                // Leave the queue
//                                agentMovement.leaveQueue();
//
//                                // Move forward and go looking for the
//                                // next one
//                                agentMovement.getRoutePlan().setNextAmenityClass();
//
//                                // Reset the current goal of the agent
//                                // The agent is set to step forward initially if the agent is coming from a
//                                // security entrance or a turnstile
//                                agentMovement.resetGoal(
//                                        action == AgentMovement.Action.SECURITY_CHECKING
//                                                || action == AgentMovement.Action.USING_TICKET
//                                );
//
//                                // Transition back into the "walking" state, and the "will queue" action
//                                // Or the "exiting station" action, if this agent is alighting and has left a
//                                // turnstile
//                                agentMovement.setState(AgentMovement.State.WALKING);
//                                state = AgentMovement.State.WALKING;
//
//                                if (
//                                        agentMovement.getDisposition() == AgentMovement.Disposition.ALIGHTING
//                                                && action == AgentMovement.Action.USING_TICKET
//                                ) {
//                                    agentMovement.setAction(AgentMovement.Action.EXITING_STATION);
//                                    action = AgentMovement.Action.EXITING_STATION;
//                                } else {
//                                    agentMovement.setAction(AgentMovement.Action.WILL_QUEUE);
//                                    action = AgentMovement.Action.WILL_QUEUE;
//                                }
//                            } else {
//                                // Just stop and wait
//                                agentMovement.stop();
//                            }
//                        }
//
//                        break;
//                }
//
//                break;
//
//            case RIDING_TRAIN:
//                // The agent is riding the train
//                switch (state) {
//                    case IN_TRAIN: break;
//                }
//
//                break;
        }
    }

    private void spawnAgent(University university) {
        UniversityGate gate = university.getUniversityGates().get(1);
        Gate.GateBlock spawner = gate.getSpawners().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4));
        UniversityAgent agent = null;

        int spawnChance = gate.getChancePerTick();
        int CHANCE_SPAWN = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        if (spawnChance < CHANCE_SPAWN && UniversityAgent.studentCount != this.MAX_STUDENTS) {
            agent = UniversityAgent.UniversityAgentFactory.create(UniversityAgent.Type.STUDENT, spawner.getPatch(), false);
            university.getAgents().add(agent);
            university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
        }
    }

}