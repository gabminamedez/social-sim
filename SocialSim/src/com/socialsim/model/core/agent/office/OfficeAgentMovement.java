package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.generic.pathfinding.AgentMovement;
import com.socialsim.model.core.agent.generic.pathfinding.AgentPath;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.Vector;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchfield.Breakroom;
import com.socialsim.model.core.environment.office.patchfield.MeetingRoom;
import com.socialsim.model.core.environment.office.patchfield.OfficeRoom;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.*;
import com.socialsim.model.core.environment.office.patchfield.Bathroom;
import com.socialsim.model.simulator.Simulator;
import com.socialsim.model.simulator.office.OfficeSimulator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OfficeAgentMovement extends AgentMovement {

    public static int defaultNonverbalMean = 2;
    public static int defaultNonverbalStdDev = 1;
    public static int defaultCooperativeMean = 24;
    public static int defaultCooperativeStdDev = 12;
    public static int defaultExchangeMean = 24;
    public static int defaultExchangeStdDev = 12;
    public static int defaultFieldOfView = 30;

    private final OfficeAgent parent;
    private final Coordinates position; // Denotes the position of the agent
    private final Office office;
    private final double baseWalkingDistance; // Denotes the distance (m) the agent walks in one second
    private double preferredWalkingDistance;
    private double currentWalkingDistance;
    private double proposedHeading;// Denotes the proposed heading of the agent in degrees where E = 0 degrees, N = 90 degrees, W = 180 degrees, S = 270 degrees
    private double heading;
    private double previousHeading;
    private int team;
    private Cubicle assignedCubicle;

    private Patch currentPatch;
    private Amenity currentAmenity;
    private PatchField currentPatchField;
    private Patch goalPatch;
    private Amenity.AmenityBlock goalAttractor;
    private Amenity goalAmenity;
    private PatchField goalPatchField;
    private QueueingPatchField goalQueueingPatchField; // Denotes the patch field of the agent goal
    private Patch goalNearestQueueingPatch; // Denotes the patch with the nearest queueing patch

    private OfficeRoutePlan routePlan;
    private AgentPath currentPath; // Denotes the current path followed by this agent, if any
    private int stateIndex;
    private int actionIndex;
    private OfficeState currentState;
    private OfficeAction currentAction; // Low-level description of what the agent is doing

    private boolean isWaitingOnAmenity; // Denotes whether the agent is temporarily waiting on an amenity to be vacant
    private boolean hasEncounteredAgentToFollow; // Denotes whether this agent has encountered the agent to be followed in the queue
    private OfficeAgent agentFollowedWhenAssembling; // Denotes the agent this agent is currently following while assembling
    private double distanceMovedInTick; // Denotes the distance moved by this agent in the previous tick
    private int tickEntered;
    private int duration;
    private int noMovementCounter; // Counts the ticks this agent moved a distance under a certain threshold
    private int movementCounter; // Counts the ticks this agent has spent moving - this will reset when stopping
    private int noNewPatchesSeenCounter; // Counts the ticks this agent has seen less than the defined number of patches
    private int newPatchesSeenCounter; // Counts the ticks this agent has spent seeing new patches - this will reset otherwise
    private boolean isStuck; // Denotes whether the agent is stuck
    private int stuckCounter; // Counts the ticks this agent has spent being stuck - this will reset when a condition is reached
    private int timeSinceLeftPreviousGoal; // Denotes the time since the agent left its previous goal
    private final int ticksUntilFullyAccelerated; // Denotes the time until the agent accelerates fully from non-movement
    private int ticksAcceleratedOrMaintainedSpeed; // Denotes the time the agent has spent accelerating or moving at a constant speed so far without slowing down or stopping
    private final double fieldOfViewAngle; // Denotes the field of view angle of the agent
    private boolean isReadyToFree; // Denotes whether the agent is ready to be freed from being stuck
    private final ConcurrentHashMap<Patch, Integer> recentPatches; // Denotes the recent patches this agent has been in

    // The vectors of this agent
    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    // Interaction parameters
    private boolean isInteracting; // Denotes whether the agent is currently interacting with another agent
    private boolean isSimultaneousInteractionAllowed; // Denotes whether an interaction is allowed while an action is being done simultaneously
    private int interactionDuration;
    private OfficeAgentMovement.InteractionType interactionType;

    private Patch collabTablePatch;
    private Patch meetingPatch;

    public enum InteractionType {
        NON_VERBAL,
        COOPERATIVE,
        EXCHANGE
    }

    public OfficeAgentMovement(Patch spawnPatch, OfficeAgent parent, double baseWalkingDistance, Coordinates coordinates, long tickEntered, int team, Cubicle assignedCubicle) { // For inOnStart agents
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
        this.team = team;
        this.assignedCubicle = assignedCubicle;

        final double interQuartileRange = 0.12; // The walking speed values shall be in m/s
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        this.currentPatch = spawnPatch; // Add this agent to the spawn patch
        this.currentPatch.getAgents().add(parent);
        this.office = (Office) currentPatch.getEnvironment();

        if (parent.getInOnStart()) { // All inOnStart agents will face the south by default
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.previousHeading = Math.toRadians(270.0);
            this.fieldOfViewAngle = this.office.getFieldOfView();
        }
        else { // All newly generated agents will face the north by default
            this.proposedHeading = Math.toRadians(90.0);
            this.heading = Math.toRadians(90.0);
            this.previousHeading = Math.toRadians(90.0);
            this.fieldOfViewAngle = this.office.getFieldOfView();
        }

        this.currentPatchField = null;
        this.tickEntered = (int) tickEntered;
        this.ticksUntilFullyAccelerated = 10; // Set the agent's time until it fully accelerates
        this.ticksAcceleratedOrMaintainedSpeed = 0;

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal(); // Set the agent goal

        this.routePlan = new OfficeRoutePlan(parent, office, currentPatch, (int) tickEntered, team, assignedCubicle);
        this.stateIndex = 0;
        this.actionIndex = 0;
        this.currentState = this.routePlan.getCurrentState();
        this.currentAction = this.routePlan.getCurrentState().getActions().get(actionIndex);
        if (!parent.getInOnStart()) {
            this.currentAmenity = office.getOfficeGates().get(1); // Getting Entrance Gate
        }
        if (this.currentAction.getDestination() != null) {
            this.goalAttractor = this.currentAction.getDestination().getAmenityBlock();
        }
        if (this.currentAction.getDuration() != 0) {
            this.duration = this.currentAction.getDuration();
        }

        this.isInteracting = false;

        this.collabTablePatch = null;
        this.meetingPatch = null;
    }

    public OfficeAgent getParent() {
        return parent;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates coordinates) {
        final int timeElapsedExpiration = 10;
        Patch previousPatch = this.currentPatch;
        this.position.setX(coordinates.getX());
        this.position.setY(coordinates.getY());

        Patch newPatch = this.office.getPatch(new Coordinates(coordinates.getX(), coordinates.getY())); // Get the patch of the new position
        if (!previousPatch.equals(newPatch)) { // If the new position is on a different patch, remove the agent from its old patch, then add it to the new patch
            previousPatch.removeAgent(this.parent);
            newPatch.addAgent(this.parent);

            SortedSet<Patch> previousPatchSet = previousPatch.getEnvironment().getAgentPatchSet();
            SortedSet<Patch> newPatchSet = newPatch.getEnvironment().getAgentPatchSet();

            if (previousPatchSet.contains(previousPatch) && hasNoAgent(previousPatch)) {
                previousPatchSet.remove(previousPatch);
            }

            newPatchSet.add(newPatch);
            this.currentPatch = newPatch;
            updateRecentPatches(this.currentPatch, timeElapsedExpiration); // Update the recent patch list
        }
        else {
            updateRecentPatches(null, timeElapsedExpiration); // Update the recent patch list
        }
    }

    public Office getOffice() {
        return office;
    }

    public int getTeam() {
        return team;
    }

    public Cubicle getAssignedCubicle() {
        return assignedCubicle;
    }

    public double getCurrentWalkingDistance() {
        return currentWalkingDistance;
    }

    public double getProposedHeading() {
        return proposedHeading;
    }

    public double getHeading() {
        return heading;
    }

    public Patch getCurrentPatch() {
        return currentPatch;
    }

    public void setCurrentPatch(Patch currentPatch) {
        this.currentPatch = currentPatch;
    }

    public AgentPath getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(AgentPath currentPath) {
        this.currentPath = currentPath;
    }

    public int getStateIndex() {
        return stateIndex;
    }

    public void setStateIndex(int stateIndex) {
        this.stateIndex = stateIndex;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public OfficeState getCurrentState() {
        return currentState;
    }

    public void setNextState(int i) {
        this.currentState = this.currentState.getRoutePlan().setNextState(i);
    }

    public void setPreviousState(int i) {
        this.currentState = this.currentState.getRoutePlan().setPreviousState(i);
    }

    public OfficeAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(OfficeAction currentAction) {
        this.currentAction = currentAction;
    }

    public Amenity getCurrentAmenity() {
        return currentAmenity;
    }

    public void setCurrentAmenity(Amenity currentAmenity) {
        this.currentAmenity = currentAmenity;
    }

    public PatchField getCurrentPatchField() {
        return currentPatchField;
    }

    public void setCurrentPatchField(PatchField currentPatchField) {
        this.currentPatchField = currentPatchField;
    }

    public Patch getGoalPatch() {
        return goalPatch;
    }

    public Amenity.AmenityBlock getGoalAttractor() {
        return goalAttractor;
    }

    public void setGoalAttractor(Amenity.AmenityBlock goalAttractor) {
        this.goalAttractor = goalAttractor;
    }

    public Amenity getGoalAmenity() {
        return goalAmenity;
    }

    public void setGoalAmenity(Amenity goalAmenity) {
        this.goalAmenity = goalAmenity;
    }

    public PatchField getGoalPatchField() {
        return goalPatchField;
    }

    public QueueingPatchField getGoalQueueingPatchField() {
        return goalQueueingPatchField;
    }

    public void setGoalQueueingPatchField(QueueingPatchField goalQueueingPatchField) {
        this.goalQueueingPatchField = goalQueueingPatchField;
    }

    public Patch getGoalNearestQueueingPatch() {
        return goalNearestQueueingPatch;
    }

    public OfficeRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(OfficeRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public boolean isWaitingOnAmenity() {
        return isWaitingOnAmenity;
    }

    public OfficeAgent getAgentFollowedWhenAssembling() {
        return agentFollowedWhenAssembling;
    }

    public boolean hasEncounteredAgentToFollow() {
        return hasEncounteredAgentToFollow;
    }

    public ConcurrentHashMap<Patch, Integer> getRecentPatches() {
        return recentPatches;
    }

    public double getDistanceMovedInTick() {
        return distanceMovedInTick;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTickEntered() {
        return tickEntered;
    }

    public void setTickEntered(int tickEntered) {
        this.tickEntered = tickEntered;
    }

    public double getFieldOfViewAngle() {
        return fieldOfViewAngle;
    }

    public int getNoMovementCounter() {
        return noMovementCounter;
    }

    public int getMovementCounter() {
        return movementCounter;
    }

    public int getNoNewPatchesSeenCounter() {
        return noNewPatchesSeenCounter;
    }

    public int getNewPatchesSeenCounter() {
        return newPatchesSeenCounter;
    }

    public int getStuckCounter() {
        return stuckCounter;
    }

    public int getTimeSinceLeftPreviousGoal() {
        return timeSinceLeftPreviousGoal;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public boolean isReadyToFree() {
        return isReadyToFree;
    }

    public List<Vector> getRepulsiveForceFromAgents() {
        return repulsiveForceFromAgents;
    }

    public List<Vector> getRepulsiveForcesFromObstacles() {
        return repulsiveForcesFromObstacles;
    }

    public Vector getAttractiveForce() {
        return attractiveForce;
    }

    public Vector getMotivationForce() {
        return motivationForce;
    }

    public Goal getGoalAmenityAsGoal() {
        return Goal.toGoal(this.goalAmenity);
    }

    public QueueableGoal getGoalAmenityAsQueueableGoal() {
        return QueueableGoal.toQueueableGoal(this.goalAmenity);
    }

    public void removeCollaborationTeam(){
        if(this.collabTablePatch != null && this.collabTablePatch.getTeam() != -1){
            this.collabTablePatch.setTeam(-1);
            this.collabTablePatch = null;
        }
    }

    public void resetGoal() { // Reset the agent's goal
        this.goalPatch = null;
        this.goalAmenity = null;
        this.goalAttractor = null;
        this.goalPatchField = null;
        this.currentPath = null;
        this.currentAmenity = null;
        this.goalQueueingPatchField = null; // Take note of the patch field of the agent's goal
        this.goalNearestQueueingPatch = null; // Take note of the agent's nearest queueing patch
        this.hasEncounteredAgentToFollow = false; // No agents have been encountered yet
        this.isWaitingOnAmenity = false; // This agent is not yet waiting
        this.agentFollowedWhenAssembling = null; // This agent is not following anyone yet
        this.distanceMovedInTick = 0.0; // This agent hasn't moved yet
        this.noMovementCounter = 0;
        this.movementCounter = 0;
        this.noNewPatchesSeenCounter = 0;
        this.newPatchesSeenCounter = 0;
        this.timeSinceLeftPreviousGoal = 0;
        this.recentPatches.clear(); // This agent has no recent patches yet
        this.free(); // This agent is not yet stuck
    }

    // Use the A* algorithm
    public AgentPath computePath(Patch startingPatch, Patch goalPatch, boolean includeStartingPatch, boolean includeGoalPatch) {
        HashSet<Patch> openSet = new HashSet<>();
        HashMap<Patch, Double> gScores = new HashMap<>();
        HashMap<Patch, Double> fScores = new HashMap<>();
        HashMap<Patch, Patch> cameFrom = new HashMap<>();

        for (Patch[] patchRow : startingPatch.getEnvironment().getPatches()) {
            for (Patch patch : patchRow) {
                gScores.put(patch, Double.MAX_VALUE);
                fScores.put(patch, Double.MAX_VALUE);
            }
        }

        gScores.put(startingPatch, 0.0);
        fScores.put(startingPatch, Coordinates.distance(startingPatch, goalPatch));
        openSet.add(startingPatch);

        while (!openSet.isEmpty()) {
            Patch patchToExplore;
            double minimumDistance = Double.MAX_VALUE;
            Patch patchWithMinimumDistance = null;
            for (Patch patchInQueue : openSet) {
                double fScore = fScores.get(patchInQueue);
                if (fScore < minimumDistance) {
                    minimumDistance = fScore;
                    patchWithMinimumDistance = patchInQueue;
                }
            }

            patchToExplore = patchWithMinimumDistance;
            if (patchToExplore != null && patchToExplore.equals(goalPatch)) {
                Stack<Patch> path = new Stack<>();
                if(goalAmenity.getClass() == Chair.class ||
                        goalAmenity.getClass() == Door.class || goalAmenity.getClass() == Toilet.class ||
                        goalAmenity.getClass() == Couch.class || goalAmenity.getClass() == OfficeGate.class
                        || goalAmenity.getClass() == Table.class || goalAmenity.getClass() == MeetingDesk.class
                        || (goalAmenity.getClass() == Cubicle.class && (this.parent.getPersona() !=
                        OfficeAgent.Persona.EXT_TECHNICAL || this.parent.getPersona() !=
                        OfficeAgent.Persona.INT_TECHNICAL))) {
                    path.push(goalPatch);
                }
                double length = 0.0;
                Patch currentPatch = goalPatch;
                while (cameFrom.containsKey(currentPatch)) {
                    Patch previousPatch = cameFrom.get(currentPatch);
                    length += Coordinates.distance(previousPatch.getPatchCenterCoordinates(), currentPatch.getPatchCenterCoordinates());
                    currentPatch = previousPatch;
                    path.push(currentPatch);
                }

                return new AgentPath(length, path);
            }
            openSet.remove(patchToExplore);

            List<Patch> patchToExploreNeighbors = patchToExplore.getNeighbors();
            for (Patch patchToExploreNeighbor : patchToExploreNeighbors) {
                if ((patchToExploreNeighbor.getAmenityBlock() == null && patchToExploreNeighbor.getPatchField() == null)
                        || (patchToExploreNeighbor.getAmenityBlock() != null && patchToExploreNeighbor.getPatchField() == null && patchToExploreNeighbor.getAmenityBlock().getParent() == goalAmenity)
                        || (patchToExploreNeighbor.getAmenityBlock() != null && patchToExploreNeighbor.getPatchField() != null && patchToExploreNeighbor.getAmenityBlock().getParent() == goalAmenity)
                        || (patchToExploreNeighbor.getAmenityBlock() != null && patchToExploreNeighbor.getAmenityBlock().getParent().getClass() == Door.class)
                        || (patchToExploreNeighbor.getAmenityBlock() != null && patchToExploreNeighbor.getAmenityBlock().getParent().getClass() == Security.class)
                        || (patchToExploreNeighbor.getPatchField() != null && patchToExploreNeighbor.getPatchField().getKey().getClass() != Wall.class)
                        || (!includeStartingPatch && patchToExplore.equals(startingPatch) || !includeGoalPatch && patchToExploreNeighbor.equals(goalPatch))) {
                    double obstacleClosenessPenalty = (patchToExploreNeighbor.getAmenityBlocksAround() + patchToExploreNeighbor.getWallsAround()) * 2.0;
                    double tentativeGScore = gScores.get(patchToExplore) + Coordinates.distance(patchToExplore, patchToExploreNeighbor) + obstacleClosenessPenalty;
                    if (tentativeGScore < gScores.get(patchToExploreNeighbor)) {
                        cameFrom.put(patchToExploreNeighbor, patchToExplore);
                        gScores.put(patchToExploreNeighbor, tentativeGScore);
                        fScores.put(patchToExploreNeighbor, gScores.get(patchToExploreNeighbor) + Coordinates.distance(patchToExploreNeighbor, goalPatch));
                        openSet.add(patchToExploreNeighbor);
                    }
                }
            }
        }

        return null;
    }

    public boolean chooseGoal(Class<? extends Amenity> nextAmenityClass) { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            List<? extends Amenity> amenityListInFloor = this.office.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                    double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                    distancesToAttractors.put(attractor, distanceToAttractor);
                }
            }

            // Sort amenity by distance, from nearest to furthest
            List<Map.Entry<Amenity.AmenityBlock, Double> > list =
                    new LinkedList<Map.Entry<Amenity.AmenityBlock, Double> >(distancesToAttractors.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<Amenity.AmenityBlock, Double> >() {
                public int compare(Map.Entry<Amenity.AmenityBlock, Double> o1,
                                   Map.Entry<Amenity.AmenityBlock, Double> o2)
                {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            HashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<Amenity.AmenityBlock, Double>();
            for (Map.Entry<Amenity.AmenityBlock, Double> aa : list) {
                sortedDistances.put(aa.getKey(), aa.getValue());
            }
            int temp = 0;
            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) { // Look for a vacant amenity
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                temp++;
                if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) {
                    this.goalAmenity =  candidateAttractor.getParent();
                    this.goalAttractor = candidateAttractor;

                    getGoalAttractor().setIsReserved(true);
                    return true;
                }else if(temp == sortedDistances.size()){
                    return false;
                }
            }
        }

        return false;
    }

    public boolean chooseBreakroomSeat() { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            List<? extends Amenity> tables = this.office.getAmenityList(Table.class);
            List<? extends Amenity> couches = this.office.getAmenityList(Couch.class);
            List<? extends Amenity> amenityListInFloor = Stream.concat(tables.stream(), couches.stream()).
                    collect(Collectors.toList());

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField() != null && amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getKey() == this.office.getBreakrooms().get(0)) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
                    }
                }
            }

            // Sort amenity by distance, from nearest to furthest
            List<Map.Entry<Amenity.AmenityBlock, Double> > list = new LinkedList<Map.Entry<Amenity.AmenityBlock, Double> >(distancesToAttractors.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<Amenity.AmenityBlock, Double> >() {
                public int compare(Map.Entry<Amenity.AmenityBlock, Double> o1, Map.Entry<Amenity.AmenityBlock, Double> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            HashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<Amenity.AmenityBlock, Double>();
            for (Map.Entry<Amenity.AmenityBlock, Double> aa : list) {
                sortedDistances.put(aa.getKey(), aa.getValue());
            }

            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) { // Look for a vacant amenity
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) {
                    this.goalAmenity =  candidateAttractor.getParent();
                    this.goalAttractor = candidateAttractor;

                    getGoalAttractor().setIsReserved(true);
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    public boolean chooseCollaborationChair(){ // Assign a table to a team and find available chairs

        if(this.goalAmenity == null){
            List<Amenity> temp = new ArrayList<>(); // list of chairs
            int start1 = 0, start2 = 0; // starting index of chairs near a table
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
            int count = 1;
            int table = -1;

            List<? extends Amenity> amenityListInFloor = this.office.getAmenityList(CollabDesk.class);
            // Check if a table has been claimed by a team

            for (Amenity amenity : amenityListInFloor) {
                // first check if a table has been claimed
                if(amenity.getAttractors().get(0).getPatch().getTeam() == this.team){
                    table = count;
                    this.collabTablePatch = amenity.getAttractors().get(0).getPatch();
                    break;
                }
                count++;
                if(count>5){
                    count = 0;
                    break;
                }
            }

            if(table == -1){
                for (Amenity amenity : amenityListInFloor) {
                    // look for empty table
                    count++;
                    if(amenity.getAttractors().get(0).getPatch().getTeam() == -1){
                        table = count;
                        this.collabTablePatch = amenity.getAttractors().get(0).getPatch();
                        amenity.getAttractors().get(0).getPatch().setTeam(this.team);
                        break;
                    }
                    if(count == 5){
                        break;
                    }
                }
            }

            if (table != -1) {
                switch (table) {
                    case 1 -> {
                        start1 = 66;
                        start2 = 67;
                    }
                    case 2 -> {
                        start1 = 68;
                        start2 = 69;
                    }
                    case 3 -> {
                        start1 = 70;
                        start2 = 71;
                    }
                    case 4 -> {
                        start1 = 72;
                        start2 = 73;
                    }
                }

                // add the chairs to the list
                for (int i = start1; i < start1 + 51; i += 10) {
                    temp.add(this.office.getChairs().get(i));
                }
                for (int i = start2; i < start2 + 51; i += 10) {
                    temp.add(this.office.getChairs().get(i));
                }

                // Compute the distance to each attractor
                for (Amenity amenity : temp) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
                    }
                }

                // Sort amenity by distance, from nearest to furthest
                List<Map.Entry<Amenity.AmenityBlock, Double>> list =
                        new LinkedList<Map.Entry<Amenity.AmenityBlock, Double>>(distancesToAttractors.entrySet());

                list.sort(new Comparator<Map.Entry<Amenity.AmenityBlock, Double>>() {
                    public int compare(Map.Entry<Amenity.AmenityBlock, Double> o1,
                                       Map.Entry<Amenity.AmenityBlock, Double> o2) {
                        return (o1.getValue()).compareTo(o2.getValue());
                    }
                });

                HashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<Amenity.AmenityBlock, Double>();
                for (Map.Entry<Amenity.AmenityBlock, Double> aa : list) {
                    sortedDistances.put(aa.getKey(), aa.getValue());
                }

                // Look for a vacant amenity
                for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                    Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();

                    if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) {
                        this.goalAmenity = candidateAttractor.getParent();
                        this.goalAttractor = candidateAttractor;

                        getGoalAttractor().setIsReserved(true);
                        return true;
                    }
                }

            }
            return false;
        }

        return false;
    }

    public void chooseMeetingGoal(int room){
        if(this.goalAmenity == null){

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

//            List<Amenity> temp = new ArrayList<>();
//
//            if(room == 1){
//                temp.add(this.office.getMeetingDesks().get(0));
//                temp.add(this.office.getMeetingDesks().get(1));
//            }else if(room == 2){
//                temp.add(this.office.getMeetingDesks().get(2));
//                temp.add(this.office.getMeetingDesks().get(3));
//            }else if(room == 3){
//                temp.add(this.office.getMeetingDesks().get(4));
//                temp.add(this.office.getMeetingDesks().get(5));
//            }

            int start1, start2;
            start1 = start2 = 0;
            List<Amenity> temp = new ArrayList<>();

            if(room == 1){
                start1 = 6;
                start2 = 7;
                for(int i = 54; i<58; i++){
                    temp.add(this.office.getChairs().get(i));
                }
            }else if(room == 2){
                start1 = 8;
                start2 = 9;
                for(int i = 58; i<62; i++){
                    temp.add(this.office.getChairs().get(i));
                }
            }else if(room == 3){
                start1 = 10;
                start2 = 11;
                for(int i = 62; i<66; i++){
                    temp.add(this.office.getChairs().get(i));
                }
            }

            for(int i = start1; i < start1 + 43; i += 6){
                temp.add(this.office.getChairs().get(i));
            }

            for(int i = start2; i < start2 + 43; i += 6){
                temp.add(this.office.getChairs().get(i));
            }


            for (Amenity amenity : temp) {
                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                    double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                    distancesToAttractors.put(attractor, distanceToAttractor);
                }
            }

            List<Map.Entry<Amenity.AmenityBlock, Double> > list =
                    new LinkedList<Map.Entry<Amenity.AmenityBlock, Double> >(distancesToAttractors.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<Amenity.AmenityBlock, Double> >() {
                public int compare(Map.Entry<Amenity.AmenityBlock, Double> o1,
                                   Map.Entry<Amenity.AmenityBlock, Double> o2)
                {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            HashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<Amenity.AmenityBlock, Double>();
            for (Map.Entry<Amenity.AmenityBlock, Double> aa : list) {
                sortedDistances.put(aa.getKey(), aa.getValue());
            }

            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                // Look for a vacant amenity
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();

                if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) {
                    this.goalAmenity =  candidateAttractor.getParent();
                    this.goalAttractor = candidateAttractor;

                    getGoalAttractor().setIsReserved(true);
                    break;
                }
            }
        }
    }

    private Coordinates getFuturePosition(double walkingDistance) {
        return getFuturePosition(this.goalAmenity, this.proposedHeading, walkingDistance);
    }

    public Coordinates getFuturePosition(Coordinates startingPosition, double heading, double magnitude) {
        return Coordinates.computeFuturePosition(startingPosition, heading, magnitude);
    }

    public Coordinates getFuturePosition(Amenity goal, double heading, double walkingDistance) {
        double minimumDistance = Double.MAX_VALUE; // Get the nearest attractor to this agent
        double distance;

        Amenity.AmenityBlock nearestAttractor = null;

        for (Amenity.AmenityBlock attractor : goal.getAttractors()) {
            distance = Coordinates.distance(this.position, attractor.getPatch().getPatchCenterCoordinates());
            if (distance < minimumDistance) {
                minimumDistance = distance;
                nearestAttractor = attractor;
            }
        }

        assert nearestAttractor != null;

        if (minimumDistance < walkingDistance) { // If distance between agent and goal is less than distance agent covers every time it walks, "snap" the position of agent to center of goal immediately to avoid overshooting its target
            return new Coordinates(nearestAttractor.getPatch().getPatchCenterCoordinates().getX(), nearestAttractor.getPatch().getPatchCenterCoordinates().getY());
        }
        else { // If not, compute the next coordinates normally
            Coordinates futurePosition = this.getFuturePosition(this.position, heading, walkingDistance);
            double newX = futurePosition.getX();
            double newY = futurePosition.getY();

            // Check if the new coordinates are out of bounds; If they are, adjust them such that they stay within bounds
            if (newX < 0) {
                newX = 0.0;
            }
            else if (newX > 120 - 1) {
                newX = 120 - 0.5;
            }

            if (newY < 0) {
                newY = 0.0;
            }
            else if (newY > 60 - 1) {
                newY = 60 - 0.5;
            }

            return new Coordinates(newX, newY);
        }
    }

    public void moveSocialForce() { // Make the agent move in accordance with social forces
        final double minimumAgentRepulsion = 0.01 * this.preferredWalkingDistance; // The smallest repulsion an agent may inflict on another
        final int noMovementTicksThreshold = 5; // stuck if not moved for this no. of ticks
        final int noNewPatchesSeenTicksThreshold = 5; // If the agent has not seen new patches for more than this number of ticks, the agent will be considered stuck
        final int unstuckTicksThreshold = 60; // Stuck agent must move this no. of ticks
        final double noMovementThreshold = 0.01 * this.preferredWalkingDistance; // If distance the agent moves per tick is less than this distance, this agent is considered to not have moved
        final double noNewPatchesSeenThreshold = 5; // UniversityAgent hasn't moved if new patches seen are less than this
        final double slowdownStartDistance = 2.0; // The distance to another agent before this agent slows down
        final double minimumStopDistance = 0.6; // The minimum allowable distance from another agent at its front before this agent stops
        double maximumStopDistance = 1.0; // The maximum allowable distance from another agent at its front before this agent stops
        int numberOfAgents = 0; // Count the number of agents in the relevant patches
        int numberOfObstacles = 0; // Count the number of obstacles in the relevant patches
        double maximumAgentStopDistance = 1.0; // The distance from the agent's center by which repulsive effects from agents start to occur
        final double minimumAgentStopDistance = 0.6; // The distance from the agent's center by which repulsive effects from agents are at a maximum
        double maximumObstacleStopDistance = 1.0; // The distance from the agent's center by which repulsive effects from obstacles start to occur
        final double minimumObstacleStopDistance = 0.6; // The distance from the agent's center by which repulsive effects from obstacles are at a maximum

        List<Patch> patchesToExplore = this.get7x7Field(this.proposedHeading, true, Math.toRadians(360.0));

        // Clear vectors from the previous computations
        this.repulsiveForceFromAgents.clear();
        this.repulsiveForcesFromObstacles.clear();
        this.attractiveForce = null;
        this.motivationForce = null;

        // Add the repulsive effects from nearby agents and obstacles
        TreeMap<Double, Patch> obstaclesEncountered = new TreeMap<>();
        TreeMap<Double, Patch> wallsEncountered = new TreeMap<>();

        List<Vector> vectorsToAdd = new ArrayList<>(); // This will contain the final motivation vector

        this.previousHeading = this.heading; // Get the current heading, which will be the previous heading later
        Coordinates proposedNewPosition = this.getFuturePosition(this.preferredWalkingDistance); // Compute for the proposed future position
        this.preferredWalkingDistance = this.baseWalkingDistance;

        // Slow down when near goal/obstacle
        final double distanceSlowdownStart = 5.0;
        final double speedDecreaseFactor = 0.5;

        double distanceToGoal = Coordinates.distance(this.currentPatch, this.getGoalAmenity().getAttractors().get(0).getPatch());
        if (distanceToGoal < distanceSlowdownStart && this.hasClearLineOfSight(this.position, this.goalAmenity.getAttractors().get(0).getPatch().getPatchCenterCoordinates(), true)) {
            this.preferredWalkingDistance *= speedDecreaseFactor;
        }

        // If this agent is queueing, the only social forces that apply are attractive forces to agents and obstacles
        // (if not in queueing action)
        // TODO: add code to check if agent is already in queue / queueing logic
//        if (this.currentState.getName() == UniversityState.Name.GOING_TO_SECURITY ||
//                this.currentState.getName() == UniversityState.Name.GOING_TO_LUNCH ||
//                this.currentState.getName() == UniversityState.Name.NEEDS_DRINK) {
//            // looking for queue
//            /*if (this.currentAction.getName() == UniversityAction.Name.GO_THROUGH_SCANNER ||
//                    this.currentAction.getName() == UniversityAction.Name.QUEUE_VENDOR ||
//                    this.currentAction.getName() == UniversityAction.Name.QUEUE_FOUNTAIN) {
//                Patch nextQueuePatch = this.currentPatch.getQueueingPatchField().getKey().getNextQueuePatch(currentPatch);
//                System.out.println("queueingPatchField: " + this.currentPatch.getQueueingPatchField()
//                + " key: " + this.currentPatch.getQueueingPatchField().getKey());
//                if (!nextQueuePatch.getAgents().isEmpty()) {
//                    this.stop();
//                }
//            }else*/ if (this.currentAction.getName() == UniversityAction.Name.CHECKOUT || this.currentAction.getName() == UniversityAction.Name.DRINK_FOUNTAIN ||
//                    this.currentAction.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT || this.currentAction.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT ||
//                    this.currentAction.getName() == UniversityAction.Name.LUNCH_STAY_PUT || this.currentAction.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE ||
//                    this.currentAction.getName() == UniversityAction.Name.VIEW_BULLETIN || this.currentAction.getName() == UniversityAction.Name.SIT_ON_BENCH ||
//                    this.currentAction.getName() == UniversityAction.Name.GUARD_STAY_PUT || this.currentAction.getName() == UniversityAction.Name.JANITOR_CLEAN_TOILET || this.currentAction.getName() == UniversityAction.Name.JANITOR_CHECK_FOUNTAIN) {
//                this.stop();
//            }else { // Not in queue and not staying put
//                // TODO: Calculate which queue to go to for cafeteria gogo julian
//                if (this.isStuck || this.isServicedByQueueableGoal() && this.noMovementCounter > noMovementTicksThreshold) {
//                    this.isStuck = true;
//                    this.stuckCounter++;
//                }
//
//                TreeMap<Double, UniversityAgent> agentsWithinFieldOfView = new TreeMap<>(); // Count agents within FOV
//
//                for (Patch patch : patchesToExplore) { // Look around the patches that fall on the agent's field of view
//                    if (this.currentAction.getName() != UniversityAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != UniversityAction.Name.QUEUE_VENDOR && this.currentAction.getName() != UniversityAction.Name.QUEUE_FOUNTAIN) { // If not in queue, count obstacles
//                        Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
//                        // Get the distance between this agent and the obstacle on this patch
//                        if (hasObstacle(patch, goalAmenity)) {
//                            numberOfObstacles++;
//                            double distanceToObstacle = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
//                            if (distanceToObstacle <= slowdownStartDistance) {
//                                obstaclesEncountered.put(distanceToObstacle, patch);
//                            }
//                        }
//                    }
//
//                    // confirm other agents within FOV
//                    if (!this.isStuck) { // make sure agent is not stuck
//                        for (Agent otherAgent : patch.getAgents()) {
//                            UniversityAgent universityAgent = (UniversityAgent) otherAgent;
//                            if (!otherAgent.equals(this.getParent())) { // Make sure that the agent discovered isn't itself
//                                numberOfAgents++; // Take note of the agent density in this area
//
//                                // Check if this agent is within the field of view and within the slowdown distance
//                                double distanceToAgent = Coordinates.distance(this.position, universityAgent.getAgentMovement().getPosition());
//                                if (Coordinates.isWithinFieldOfView(this.position, universityAgent.getAgentMovement().getPosition(), this.proposedHeading, this.fieldOfViewAngle) && distanceToAgent <= slowdownStartDistance) {
//                                    agentsWithinFieldOfView.put(distanceToAgent, universityAgent);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // Compute the perceived density of the agents
//                final double maximumDensityTolerated = 3.0;
//                final double agentDensity = (numberOfAgents > maximumDensityTolerated ? maximumDensityTolerated : numberOfAgents) / maximumDensityTolerated;
//
//                Map.Entry<Double, UniversityAgent> nearestAgentEntry = agentsWithinFieldOfView.firstEntry(); // For each agent found within the slowdown distance, get the nearest one, if there is any
//
//                // If there are no agents within the field of view, good - move normally
//                if (nearestAgentEntry == null|| nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity() != null && !nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity().equals(this.goalAmenity)) {
//                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//                    // Get the attractive force of this agent to the new position
//                    this.attractiveForce = this.computeAttractiveForce(new Coordinates(this.position), this.proposedHeading, proposedNewPosition, this.preferredWalkingDistance);
//                    vectorsToAdd.add(attractiveForce);
//                }else { // If there are agents in the way
//                    // Get a random (but weighted) floor field value around the other agent
//                    Patch PatchFieldPatch = this.getBestQueueingPatchAroundAgent(nearestAgentEntry.getValue());
//
//                    // Check the distance of that nearest agent to this agent
//                    //double distanceToNearestAgent = nearestAgentEntry.getKey();
//
//                    // Modify the maximum stopping distance depending on the density of the environment
//                    // That is, the denser the surroundings, the less space this agent will allow between other
//                    // agents
//                    /*maximumStopDistance -= (maximumStopDistance - minimumStopDistance) * agentDensity;
//
//                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;*/
//
//                    // Else, just slow down and move towards the direction of that agent in front
//                    //final double slowdownFactor = (distanceToNearestAgent - maximumStopDistance) / (slowdownStartDistance - maximumStopDistance);
//                    double computedWalkingDistance = /*slowdownFactor **/ this.preferredWalkingDistance;
//
//                    // TODO Used to calculate when queueing for train; can be used for cafeteria
//
//                    // Only head towards that patch if the distance from that patch to the goal is further than the distance from this agent to the goal
//                    double distanceFromChosenPatchToGoal = Coordinates.distance(PatchFieldPatch, this.goalPatch);
//                    double distanceFromThisAgentToGoal = Coordinates.distance(this.currentPatch, this.goalPatch);
//                    double revisedHeading;
//                    Coordinates revisedPosition;
//
//                    if (distanceFromChosenPatchToGoal < distanceFromThisAgentToGoal) {
//                        revisedHeading = Coordinates.headingTowards(this.position, PatchFieldPatch.getPatchCenterCoordinates());
//                        revisedPosition = this.getFuturePosition(this.position, revisedHeading, computedWalkingDistance);
//                        this.attractiveForce = this.computeAttractiveForce(new Coordinates(this.position), revisedHeading, revisedPosition, computedWalkingDistance);
//                        vectorsToAdd.add(attractiveForce);
//
//                        if(!agentsWithinFieldOfView.entrySet().isEmpty()){
//                            for (Map.Entry<Double, UniversityAgent> otherAgentEntry : agentsWithinFieldOfView.entrySet()) {
//                                final int maximumAgentCountTolerated = 5; // Then compute the repulsive force from this agent; Compute the perceived density of the agents assuming the maximum density an agent sees within its environment is 5 before it thinks the crowd is very dense
//
//                                // The distance by which the repulsion starts to kick in will depend on the density of the agent's surroundings
//                                final int minimumAgentCount = 1;
//                                final double maximumDistance = 2.0;
//                                final int maximumAgentCount = 5;
//                                final double minimumDistance = 0.7;
//                                double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumAgentCountTolerated, minimumAgentCount, maximumDistance, maximumAgentCount, minimumDistance);
//                                Vector agentRepulsiveForce = computeSocialForceFromAgent(otherAgentEntry.getValue(), otherAgentEntry.getKey(), computedMaximumDistance, minimumAgentStopDistance, this.preferredWalkingDistance);
//                                this.repulsiveForceFromAgents.add(agentRepulsiveForce);
//                            }
//                        }
//                    }else {
//                        revisedPosition = this.getFuturePosition(computedWalkingDistance);
//
//                        // Get the attractive force of this agent to the new position
//                        this.attractiveForce = this.computeAttractiveForce(
//                                new Coordinates(this.position),
//                                this.proposedHeading,
//                                revisedPosition,
//                                computedWalkingDistance
//                        );
//
//                        vectorsToAdd.add(attractiveForce);
//                    }
//                }
//            }
//        }
//        else {
        if (this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold) { // Check if agent is stuck
            this.isStuck = true;
            this.stuckCounter++;
        }

        // Only apply the social forces of a set number of agents and obstacles
        int agentsProcessed = 0;
        final int agentsProcessedLimit = 5;

        for (Patch patch : patchesToExplore) { // Look around the patches that fall on the agent's field of view
            Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock(); // If this patch has an obstacle, take note of it to add a repulsive force from it later

            if (hasObstacle(patch, goalAmenity)) { // Get the distance between this agent and the obstacle on this patch
                numberOfObstacles++; // Take note of the obstacle density in this area

                double distanceToObstacle = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
                if (distanceToObstacle <= slowdownStartDistance) {
                    obstaclesEncountered.put(distanceToObstacle, patch);
                }
            }

            if(this.currentState.getName() != OfficeState.Name.GOING_TO_SECURITY && this.currentState.getName() != OfficeState.Name.GOING_HOME
                    && this.currentAction.getName() != OfficeAction.Name.QUEUE_PRINTER && this.currentAction.getName() != OfficeAction.Name.PRINTING
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Bathroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != OfficeRoom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Breakroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != MeetingRoom.class)) {
                for (Agent otherAgent : patch.getAgents()) { // Inspect each agent in each patch in the patches in the field of view
                    OfficeAgent universityAgent = (OfficeAgent) otherAgent;
                    if (agentsProcessed == agentsProcessedLimit) {
                        break;
                    }

                    if (!otherAgent.equals(this.getParent())) { // Make sure that the agent discovered isn't itself
                        numberOfAgents++; // Take note of the agent density in this area

                        // Get the distance between this agent and the other agent
                        double distanceToOtherAgent = Coordinates.distance(this.position, universityAgent.getAgentMovement().getPosition());

                        if (distanceToOtherAgent <= slowdownStartDistance) { // If the distance is less than or equal to the distance when repulsion is supposed to kick in, compute for the magnitude of that repulsion force
                            final int maximumAgentCountTolerated = 5;

                            // The distance by which the repulsion starts to kick in will depend on the density of the agent's surroundings
                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;

                            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumAgentCountTolerated, minimumAgentCount, maximumDistance, maximumAgentCount, minimumDistance);
                            Vector agentRepulsiveForce = computeSocialForceFromAgent(universityAgent, distanceToOtherAgent, computedMaximumDistance, minimumAgentStopDistance, this.preferredWalkingDistance);
                            this.repulsiveForceFromAgents.add(agentRepulsiveForce); // Add the computed vector to the list of vectors

                            agentsProcessed++;
                        }
                    }
                }
            }
        }

        // Get the attractive force of this agent to the new position
        this.attractiveForce = this.computeAttractiveForce(new Coordinates(this.position), this.proposedHeading, proposedNewPosition, this.preferredWalkingDistance);
        vectorsToAdd.add(attractiveForce);
        //}

        double previousWalkingDistance = this.currentWalkingDistance; // Take note of the previous walking distance of this agent
        vectorsToAdd.addAll(this.repulsiveForceFromAgents);
        Vector partialMotivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd); // Then compute the partial motivation force of the agent
        if (partialMotivationForce != null) { // If the resultant vector is null (i.e., no change in position), simply don't move at all
            // Calculate repulsion
            final int minimumObstacleCount = 1;
            final double maximumDistance = 2.0;
            final int maximumObstacleCount = 2;
            final double minimumDistance = 0.7;
            final int maximumObstacleCountTolerated = 2;
            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumObstacleCountTolerated, minimumObstacleCount, maximumDistance, maximumObstacleCount, minimumDistance);

            // Only apply the social forces on a set number of obstacles
            int obstaclesProcessed = 0;
            final int obstaclesProcessedLimit = 4;

            for (Map.Entry<Double, Patch> obstacleEntry : obstaclesEncountered.entrySet()) {
                if (obstaclesProcessed == obstaclesProcessedLimit) {
                    break;
                }

                this.repulsiveForcesFromObstacles.add(computeSocialForceFromObstacle(obstacleEntry.getValue(), obstacleEntry.getKey(), computedMaximumDistance, minimumObstacleStopDistance, partialMotivationForce.getMagnitude()));
                obstaclesProcessed++;
            }

            vectorsToAdd.clear();
            vectorsToAdd.add(partialMotivationForce);
            vectorsToAdd.addAll(this.repulsiveForcesFromObstacles);
            this.motivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd); // Finally, compute the final motivation force

            if (this.motivationForce != null) {
                // Cap the magnitude of the motivation force to the agent's preferred walking distance
                if (this.motivationForce.getMagnitude() > this.preferredWalkingDistance) {
                    this.motivationForce.adjustMagnitude(this.preferredWalkingDistance);
                }

                // Then adjust its heading with minor stochastic deviations
                this.motivationForce.adjustHeading(this.motivationForce.getHeading() + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(5));

                try {
                    double newHeading = motivationForce.getHeading(); // Set the new heading
                    Coordinates candidatePosition = this.motivationForce.getFuturePosition();
                    if (hasClearLineOfSight(this.position, candidatePosition, false)) {
                        this.move(candidatePosition);
                    }
                    else {
                        double revisedHeading;
                        Coordinates newFuturePosition;
                        int attempts = 0;
                        // TODO: Adjustable if no clear line of sight
                        final int attemptLimit = 5;
                        boolean freeSpaceFound;

                        do {
                            // Go back with the same magnitude as the original motivation force, but at a different heading
                            revisedHeading = (motivationForce.getHeading() + Math.toRadians(180)) % Math.toRadians(360);

                            // Add some stochasticity to this revised heading
                            revisedHeading += Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(90);
                            revisedHeading %= Math.toRadians(360);

                            // Then calculate the future position from the current position
                            newFuturePosition = this.getFuturePosition(this.position, revisedHeading, this.preferredWalkingDistance * 0.25);
                            freeSpaceFound = hasClearLineOfSight(this.position, newFuturePosition, false);

                            attempts++;
                        } while (attempts < attemptLimit && !freeSpaceFound);

                        if (attempts != attemptLimit || freeSpaceFound) { // If all the attempts are used and no free space has been found, don't move at all
                            this.move(newFuturePosition);
                        }
                    }

                    if (!this.isStuck || Coordinates.headingDifference(this.heading, newHeading) <= Math.toDegrees(90.0) || this.currentWalkingDistance > noMovementThreshold) {
                        this.heading = newHeading;
                    }
                    this.currentWalkingDistance = motivationForce.getMagnitude(); // Also take note of the new speed
                    this.distanceMovedInTick = motivationForce.getMagnitude(); // Finally, take note of the distance travelled by this agent

                    // If this agent's distance covered falls under the threshold, increment the counter denoting the ticks spent not moving; Otherwise, reset the counter
                    // Do not count for movements/non-movements when the agent is in the "in queue" state
                    if (this.currentAction.getName() != OfficeAction.Name.GO_THROUGH_SCANNER) {
                        if (this.recentPatches.size() <= noNewPatchesSeenThreshold) {
                            this.noNewPatchesSeenCounter++;
                            this.newPatchesSeenCounter = 0;
                        }
                        else {
                            this.noNewPatchesSeenCounter = 0;
                            this.newPatchesSeenCounter++;
                        }
                    }
                    else {
                        if (this.distanceMovedInTick < noMovementThreshold) {
                            this.noMovementCounter++;
                            this.movementCounter = 0;
                        }
                        else {
                            this.noMovementCounter = 0;
                            this.movementCounter++;
                        }
                    }

                    // If the agent has moved above the no-movement threshold for at least this number of ticks, remove the agent from its stuck state
                    /*if (this.isStuck &&
                            (((this.currentAction.getName() == OfficeAction.Name.GO_THROUGH_SCANNER)
                                    || this.currentAction.getName() != OfficeAction.Name.GO_THROUGH_SCANNER &&
                                    this.newPatchesSeenCounter >= unstuckTicksThreshold))){
                        this.isReadyToFree = true;
                    }*/
                    this.timeSinceLeftPreviousGoal++;

                    // Check if the agent has slowed down since the last tick; If it did, reset the time spent accelerating counter
                    if (this.currentWalkingDistance < previousWalkingDistance) {
                        this.ticksAcceleratedOrMaintainedSpeed = 0;
                    }
                    else {
                        this.ticksAcceleratedOrMaintainedSpeed++;
                    }

                    return;
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        // If it reaches this point, there is no movement to be made
        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
        this.stop();
        this.distanceMovedInTick = 0.0; // There was no movement by this agent, so increment the pertinent counter
        this.noMovementCounter++;
        this.movementCounter = 0;
        this.timeSinceLeftPreviousGoal++;
        this.ticksAcceleratedOrMaintainedSpeed = 0;
    }

    private boolean hasNoAgent(Patch patch) {
        if (patch == null) {
            return true;
        }

        List<Agent> agentsOnPatchWithoutThisAgent = patch.getAgents();
        agentsOnPatchWithoutThisAgent.remove(this.parent);

        return agentsOnPatchWithoutThisAgent.isEmpty();
    }

    public List<Patch> get7x7Field(double heading, boolean includeCenterPatch, double fieldOfViewAngle) {
        Patch centerPatch = this.currentPatch;
        List<Patch> patchesToExplore = new ArrayList<>();
        boolean isCenterPatch;

        for (Patch patch : centerPatch.get7x7Neighbors(includeCenterPatch)) {
            // Make sure that the patch to be added is within the field of view of the agent which invoked this method
            isCenterPatch = patch.equals(centerPatch);
            if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(centerPatch.getPatchCenterCoordinates(), patch.getPatchCenterCoordinates(), heading, fieldOfViewAngle)) {
                patchesToExplore.add(patch);
            }
        }

        return patchesToExplore;
    }

    public List<Patch> get3x3Field(double heading, boolean includeCenterPatch, double fieldOfViewAngle) {
        Patch centerPatch = this.currentPatch;
        List<Patch> patchesToExplore = new ArrayList<>();
        boolean isCenterPatch;

        for (Patch patch : centerPatch.get3x3Neighbors(includeCenterPatch)) {
            // Make sure that the patch to be added is within the field of view of the agent which invoked this method
            isCenterPatch = patch.equals(centerPatch);
            if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(centerPatch.getPatchCenterCoordinates(), patch.getPatchCenterCoordinates(), heading, fieldOfViewAngle)) {
                patchesToExplore.add(patch);
            }
        }
        return patchesToExplore;
    }

    private Vector computeAttractiveForce(final Coordinates startingPosition, final double proposedHeading, final Coordinates proposedNewPosition, final double preferredWalkingDistance) {
        return new Vector(startingPosition, proposedHeading, proposedNewPosition, preferredWalkingDistance);
    }

    private double computeMaximumRepulsionDistance(int objectCount, final int maximumObjectCountTolerated, final int minimumObjectCount, final double maximumDistance, final int maximumObjectCount, final double minimumDistance) {
        if (objectCount > maximumObjectCountTolerated) {
            objectCount = maximumObjectCountTolerated;
        }

        final double a = (maximumDistance - minimumDistance) / (minimumObjectCount - maximumDistance);
        final double b = minimumDistance - a * maximumObjectCount;

        return a * objectCount + b;
    }

    private double computeRepulsionMagnitudeFactor(final double distance, final double maximumDistance, final double minimumRepulsionFactor, final double minimumDistance, final double maximumRepulsionFactor) {
        // Formula: for the inverse square law equation y = a / x ^ 2 + b,
        // a = (d_max ^ 2 * (r_min * d_max ^ 2 - r_min * d_min ^ 2 + r_max ^ 2 * d_min ^ 2)) / (d_max ^ 2 - d_min ^ 2)
        // b = -((r_max ^ 2 * d_min ^ 2) / (d_max ^ 2 - d_min ^ 2))
        double differenceOfSquaredDistances = Math.pow(maximumDistance, 2.0) - Math.pow(minimumDistance, 2.0);
        double productOfMaximumRepulsionAndMinimumDistance = Math.pow(maximumRepulsionFactor, 2.0) * Math.pow(minimumDistance, 2.0);

        double a = (Math.pow(maximumDistance, 2.0) * (minimumRepulsionFactor * Math.pow(maximumDistance, 2.0) - minimumRepulsionFactor * Math.pow(minimumDistance, 2.0) + productOfMaximumRepulsionAndMinimumDistance)) / differenceOfSquaredDistances;
        double b = -(productOfMaximumRepulsionAndMinimumDistance / differenceOfSquaredDistances);

        double repulsion = a / Math.pow(distance, 2.0) + b;
        if (repulsion <= 0.0) { // The repulsion value should always be greater or equal to zero
            repulsion = 0.0;
        }

        return repulsion;
    }

    private Vector computeSocialForceFromAgent(OfficeAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at that minimum
        double modifiedDistanceToObstacle = Math.max(distanceToOtherAgent, minimumDistance);
        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        if (this.isStuck) { // If an agent is stuck, do not exert much force from this agent
            final double factor = 0.05;
            repulsionMagnitude -= this.stuckCounter * factor;
            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        double headingFromOtherAgent = Coordinates.headingTowards(agentPosition, this.position); // Then compute the heading from that other agent to this agent
        Coordinates agentRepulsionVectorFuturePosition = this.getFuturePosition(agentPosition, headingFromOtherAgent, repulsionMagnitude); // Then compute for a future position given the other agent's position, the heading, and the magnitude; This will be used as the endpoint of the repulsion vector from this obstacle

        return new Vector(agentPosition, headingFromOtherAgent, agentRepulsionVectorFuturePosition, repulsionMagnitude); // Finally, given the current position, heading, and future position, create the vector from the other agent to the current agent
    }

    private Vector computeSocialForceFromObstacle(Amenity.AmenityBlock amenityBlock, final double distanceToObstacle, final double maximumDistance, double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates repulsionVectorStartingPosition = amenityBlock.getPatch().getPatchCenterCoordinates();

        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at that minimum
        double modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        if (this.isStuck) { // If an agent is stuck, do not exert much force from this obstacle
            final double factor = 0.05;
            repulsionMagnitude -= this.stuckCounter * factor;
            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position); // Compute the heading from that origin point to this agent
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude); // Then compute for a future position given the obstacle's position, the heading, and the magnitude

        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude); // Finally, given the current position, heading, and future position, create the vector from the obstacle to the current agent
    }

    private Vector computeSocialForceFromObstacle(Patch wallPatch, final double distanceToObstacle, final double maximumDistance, double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates repulsionVectorStartingPosition = wallPatch.getPatchCenterCoordinates();

        double modifiedDistanceToObstacle;
        /*if(currentPatch.getPatchField() != null && currentPatch.getPatchField().getKey().getClass() == Classroom.class
        || this.currentPatch.getAmenityBlock() != null && !this.currentPatch.getAmenityBlock().getParent()
                .equals(Door.class)){*/
        modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
        /*}else{
            modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
        }*/
        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at
        // that minimum
        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        if (this.isStuck) { // If an agent is stuck, do not exert much force from this obstacle
            final double factor = 0.05;
            repulsionMagnitude -= this.stuckCounter * factor;
            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position); // Compute the heading from that origin point to this agent
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude); // Then compute for a future position given the obstacle's position, the heading, and the magnitude

        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude); // Finally, given the current position, heading, and future position, create the vector from the obstacle to the current agent
    }

    private void move(double walkingDistance) { // Make the agent move given a walking distance
        this.setPosition(this.getFuturePosition(walkingDistance));
    }

    private void move(Coordinates futurePosition) { // Make the agent move given the future position
        this.setPosition(futurePosition);
    }

    public void stop() { // Have the agent stop
        this.currentWalkingDistance = 0.0;
    }

    public boolean hasReachedQueueingPatchField() { // Check if this agent has reached its goal's queueing patch field
        for (Patch patch : this.goalQueueingPatchField.getAssociatedPatches()) {
            if (isOnOrCloseToPatch(patch) && hasClearLineOfSight(this.position, patch.getPatchCenterCoordinates(), true)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPath() { // Check if this agent has a path to follow
        return this.currentPath != null;
    }

    public boolean hasReachedNextPatchInPath() { // Check if this agent is on the next patch of its path
        return isOnOrCloseToPatch(this.currentPath.getPath().peek());
    }

    public void joinQueue() { // Register this agent to its queueable goal patch field's queue
        this.goalQueueingPatchField.getQueueingAgents().add(this.parent);

        Stack<Patch> path = new Stack<>(); // Manually set the patch to
        for (int i = 0; i < this.goalQueueingPatchField.getAssociatedPatches().size(); i++) {
            path.push(this.goalQueueingPatchField.getAssociatedPatches().get(i));
        }
        this.currentPath = new AgentPath(0, path);
        this.duration = currentAction.getDuration();
    }

    public void leaveQueue() { // Unregister this agent to its queueable goal patch field's queue
        this.goalQueueingPatchField.getQueueingAgents().remove(this.parent);
        this.goalQueueingPatchField = null;
    }

    public void beginWaitingOnAmenity() { // Have this agent start waiting for an amenity to become vacant
        this.isWaitingOnAmenity = true;
    }

    public boolean isQueueableGoalFree() { // Check if the goal of this agent is currently not servicing anyone
        return this.getGoalAmenityAsQueueableGoal().getAttractors().get(0).getPatch().getQueueingPatchField().getKey().getCurrentAgent() == null && this.getGoalAmenityAsQueueableGoal().getAttractors().get(0).getPatch().getAgents().isEmpty();
    }

    public boolean isServicedByQueueableGoal() { // Check if this agent the one currently served by its goal
        OfficeAgent agentServiced = (OfficeAgent) this.goalQueueingPatchField.getCurrentAgent();

        return agentServiced != null && agentServiced.equals(this.parent);
    }

    public void endWaitingOnAmenity() { // Have this agent stop waiting for an amenity to become vacant
        this.isWaitingOnAmenity = false;
    }

    public boolean hasReachedGoalPatch() { // Check if this agent has reached its goal
        if (this.isWaitingOnAmenity) { // If the agent is still waiting for an amenity to be vacant, it hasn't reached the goal yet
            return false;
        }

        return isOnOrCloseToPatch(this.goalPatch);
    }

    public void reachGoal() { // Set the agent's current amenity and position as it reaches the next goal
        // Just in case the agent isn't actually on its goal, but is adequately close to it, just move the agent there
        // Make sure to offset the agent from the center a little so a force will be applied to this agent
        Coordinates patchCenter = this.goalPatch.getPatchCenterCoordinates();
        Coordinates offsetPatchCenter = this.getFuturePosition(patchCenter, this.previousHeading, Patch.PATCH_SIZE_IN_SQUARE_METERS * 0.1);

        this.setPosition(offsetPatchCenter);
        this.currentAmenity = this.goalAmenity;
    }

    public void reachPatchInPath() { // Set the agent's next patch in its current path as it reaches it
        Patch nextPatch;

        // Keep popping while there are still patches from the path to pop and these patches are still close enough to the agent
        do {
            this.currentPath.getPath().pop();

            if (!this.currentPath.getPath().isEmpty()) { // If there are no more next patches, terminate the loop
                nextPatch = this.currentPath.getPath().peek();
            }
            else {
                break;
            }
        } while (!this.currentPath.getPath().isEmpty() && nextPatch.getAmenityBlocksAround() == 0/* && nextPatch.getWallsAround() == 0*/
                && this.isOnOrCloseToPatch(nextPatch) && this.hasClearLineOfSight(this.position, nextPatch.getPatchCenterCoordinates(), true));
    }

    public void beginServicingThisAgent() { // Have this agent's goal service this agent
        this.goalQueueingPatchField.setCurrentAgent(this.parent);
    }

    public void endServicingThisAgent() { // Have this agent's goal finish serving this agent
        this.goalQueueingPatchField.setCurrentAgent(null);
        if (this.getGoalAmenityAsQueueableGoal() != null) { // Reset the goal's waiting time counter
            this.getGoalAmenityAsQueueableGoal().resetWaitingTime();
        }
    }

    public boolean hasAgentReachedFinalPatchInPath() { // Check if this agent has reached the final patch in its current path
        return this.currentPath.getPath().isEmpty();
    }

    private boolean isOnPatch(Patch patch) { // Check if this agent has reached the specified patch
        return ((int) (this.position.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getColumn() && ((int) (this.position.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getRow();
    }

    private boolean isOnOrCloseToPatch(Patch patch) { // Check if this agent is adequately close enough to a patch
        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
    }

    public void despawn() {
        if (this.currentPatch != null) {
            this.currentPatch.getAgents().remove(this.parent);
            this.getOffice().getAgents().remove(this.parent);

            SortedSet<Patch> currentPatchSet = this.getOffice().getAgentPatchSet();
            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
                currentPatchSet.remove(this.currentPatch);
            }

            switch (this.getParent().getType()) {
                case MANAGER -> OfficeSimulator.currentManagerCount--;
                case BUSINESS -> OfficeSimulator.currentBusinessCount--;
                case RESEARCHER -> OfficeSimulator.currentResearchCount--;
                case TECHNICAL -> OfficeSimulator.currentTechnicalCount--;
                case SECRETARY -> OfficeSimulator.currentSecretaryCount--;
                case DRIVER -> OfficeSimulator.currentDriverCount--;
                case VISITOR -> OfficeSimulator.currentVisitorCount--;
                case CLIENT -> OfficeSimulator.currentClientCount--;
            }

            switch (this.getParent().getTeam()) {
                case 1 -> OfficeSimulator.currentTeam1Count--;
                case 2 -> OfficeSimulator.currentTeam2Count--;
                case 3 -> OfficeSimulator.currentTeam3Count--;
                case 4 -> OfficeSimulator.currentTeam4Count--;
            }
        }
    }

    public void faceNextPosition() { // Have the agent face its current goal, or its queueing area, or the agent at the end of the queue
        this.proposedHeading = Coordinates.headingTowards(this.position, this.goalPatch.getPatchCenterCoordinates());
    }

    public void free() { // Make this agent free from being stuck
        this.isStuck = false;
        this.stuckCounter = 0;
        this.noMovementCounter = 0;
        this.noNewPatchesSeenCounter = 0;
        this.currentPath = null;
        this.isReadyToFree = false;
    }

    public boolean chooseNextPatchInPath() { // If the agent is following a path, have the agent face the next one, if any
        boolean wasPathJustGenerated = false; // Generate a path, if one hasn't been generated yet
        final int recomputeThreshold = 10;

        if (this.currentPath == null || this.isStuck && this.noNewPatchesSeenCounter > recomputeThreshold) {
            AgentPath agentPath = null;

            // TODO: for queues
//            if (this.getGoalAmenityAsQueueable() != null) {
//                // Head towards the queue of the goal
//                LinkedList<OfficeAgent> agentsQueueing
//                        = this.goalQueueObject.getAgentsQueueing();
//
//                // If there are no agents in that queue at all, simply head for the goal patch
//                if (agentsQueueing.isEmpty()) {
//                    agentPath = computePathWithinFloor(
//                            this.currentPatch,
//                            this.goalPatch,
//                            true,
//                            true,
//                            false
//                    );
//                } else {
//                    // If there are agents in the queue, this agent should only follow the last agent in
//                    // that queue if that agent is assembling
//                    // If the last agent is not assembling, simply head for the goal patch instead
//                    OfficeAgent lastAgent = agentsQueueing.getLast();
//
//                    if (
//                            !(this.getGoalAmenityAsQueueable() instanceof TrainDoor)
//                                    && !(this.getGoalAmenityAsQueueable() instanceof Turnstile)
//                                    && lastAgent.getAgentMovement().getOfficeAction() == OfficeAction.ASSEMBLING
//                    ) {
//                        double distanceToGoalPatch = Coordinates.distance(
//                                this.currentFloor.getStation(),
//                                this.currentPatch,
//                                this.goalPatch
//                        );
//
//                        double distanceToLastAgent = Coordinates.distance(
//                                this.currentFloor.getStation(),
//                                this.currentPatch,
//                                lastAgent.getAgentMovement().getCurrentPatch()
//                        );
//
//                        // Head to whichever is closer to this agent, the last agent, or the nearest queueing
//                        // path
//                        if (distanceToGoalPatch <= distanceToLastAgent) {
//                            agentPath = computePathWithinFloor(
//                                    this.currentPatch,
//                                    this.goalPatch,
//                                    true,
//                                    true,
//                                    false
//                            );
//                        } else {
//                            agentPath = computePathWithinFloor(
//                                    this.currentPatch,
//                                    lastAgent.getAgentMovement().getCurrentPatch(),
//                                    true,
//                                    true,
//                                    false
//                            );
//                        }
//                    } else {
//                        agentPath = computePathWithinFloor(
//                                this.currentPatch,
//                                this.goalPatch,
//                                true,
//                                true,
//                                false
//                        );
//                    }
//                }
//            } else {
//                agentPath = computePathWithinFloor(
//                        this.currentPatch,
//                        this.goalPatch,
//                        true,
//                        false,
//                        false
//                );
//            }

            if (goalQueueingPatchField == null) { // If not queueing
                agentPath = computePath(this.currentPatch, this.goalAttractor.getPatch(), true, true);
            }
            else {
                agentPath = computePath(this.currentPatch, this.goalQueueingPatchField.getLastQueuePatch(), true, true);
            }

            if (agentPath != null) {
                this.currentPath = new AgentPath(agentPath); // Create a copy of the object, to avoid using up the path directly from the cache
                wasPathJustGenerated = true;
            }
        }

        if (this.currentPath == null || this.currentPath.getPath().isEmpty()) { // Get the first patch still unvisited in the path
            return false;
        }

        if (wasPathJustGenerated) { // If a path was just generated, determine the first patch to visit
            Patch nextPatchInPath;

            while (true) {
                nextPatchInPath = this.currentPath.getPath().peek();
                if (!(this.currentPath.getPath().size() > 1 && nextPatchInPath.getAmenityBlocksAround() == 0 /*&& nextPatchInPath.getWallsAround() == 0*/
                        && this.isOnOrCloseToPatch(nextPatchInPath)
                        && this.hasClearLineOfSight(this.position, nextPatchInPath.getPatchCenterCoordinates(), true))) {
                    break;
                }
                this.currentPath.getPath().pop();
            }
            this.goalPatch = nextPatchInPath;
        }
        else {
            this.goalPatch = this.currentPath.getPath().peek();
        }

        return true;
    }

    private Patch getBestQueueingPatchAroundAgent(OfficeAgent agent) {
        return this.currentPatch;
    }

    private boolean hasObstacle(Patch patch, Amenity amenity) { // Check if the given patch has an obstacle
        if (patch.getPatchField() != null && patch.getPatchField().getKey().getClass() == Wall.class) {
            return true;
        }
        else if (patch.getAmenityBlock() != null && !patch.getAmenityBlock().getParent().equals(amenity)) {
            if (patch.getAmenityBlock().getParent().getClass() == Door.class ||
                    patch.getAmenityBlock().getParent().getClass() == Security.class ||
                    patch.getAmenityBlock().getParent().getClass() == Chair.class ||
                    patch.getAmenityBlock().getParent().getClass() == Toilet.class ||
                    patch.getAmenityBlock().getParent().getClass() == Couch.class ||
                    patch.getAmenityBlock().getParent().getClass() == Printer.class ||
                    patch.getAmenityBlock().getParent().getClass() == Table.class ||
                    patch.getAmenityBlock().getParent().getClass() == MeetingDesk.class
            ) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

    public void checkIfStuck() {
        if (this.currentAmenity == null) {
            if (this.currentPatch.getAmenityBlock() != null && this.currentPatch.getAmenityBlock().getParent().getClass() != Door.class &&  this.currentPatch.getAmenityBlock().getParent() != this.goalAmenity) {
                List<Patch> candidatePatches = this.currentPatch.getNeighbors();
                for (Patch candidate: candidatePatches) {
                    if (candidate.getAmenityBlock() == null && (candidate.getPatchField() == null || (candidate.getPatchField() != null && candidate.getPatchField().getKey().getClass() != Wall.class))) {
                        this.setPosition(candidate.getPatchCenterCoordinates());
                        break;
                    }
                }
            }
            else if (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() == Wall.class) {
                List<Patch> candidatePatches = this.currentPatch.getNeighbors();
                for (Patch candidate: candidatePatches) {
                    if (candidate.getAmenityBlock() == null && (candidate.getPatchField() == null || (candidate.getPatchField() != null && candidate.getPatchField().getKey().getClass() != Wall.class))) {
                        this.setPosition(candidate.getPatchCenterCoordinates());
                        break;
                    }
                }
            }
        }
    }

    // Check if there is a clear line of sight from one point to another
    private boolean hasClearLineOfSight(Coordinates sourceCoordinates, Coordinates targetCoordinates, boolean includeStartingPatch) {
        if (hasObstacle(this.office.getPatch(targetCoordinates), goalAmenity)) {
            return false;
        }

        final double resolution = 0.2;
        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.office.getPatch(sourceCoordinates);
        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        while (distanceCovered <= distanceToTargetCoordinates) { // Keep looking for blocks while there is still distance to cover
            if (includeStartingPatch || !this.office.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.office.getPatch(currentPosition), goalAmenity)) {
                    return false;
                }
            }

            // If there isn't any, move towards the target coordinates with the given increment
            currentPosition = this.getFuturePosition(currentPosition, headingToTargetCoordinates, resolution);
            distanceCovered += resolution;
        }

        return true; // The target has been reached without finding an obstacle, so there is a clear line of sight between the two given points
    }

    private void updateRecentPatches(Patch currentPatch, final int timeElapsedExpiration) { // Update the agent's recent patches
        List<Patch> patchesToForget = new ArrayList<>();

        for (Map.Entry<Patch, Integer> recentPatchesAndTimeElapsed : this.recentPatches.entrySet()) { // Update the time elapsed in all of the recent patches
            this.recentPatches.put(recentPatchesAndTimeElapsed.getKey(), recentPatchesAndTimeElapsed.getValue() + 1);
            if (recentPatchesAndTimeElapsed.getValue() == timeElapsedExpiration) { // Remove all patches that are equal to the expiration time given
                patchesToForget.add(recentPatchesAndTimeElapsed.getKey());
            }
        }

        if (currentPatch != null) { // If there is a new patch to add or update to the recent patch list, do so
            this.recentPatches.put(currentPatch, 0); // The time lapsed value of any patch added or updated will always be zero, as it means this patch has been recently encountered by this agent
        }

        for (Patch patchToForget : patchesToForget) { // Remove all patches set to be forgotten
            this.recentPatches.remove(patchToForget);
        }
    }

    public void decrementDuration(){
        this.duration = getDuration() - 1;
    }

    public void forceActionInteraction(OfficeAgent agent, OfficeAgentMovement.InteractionType interactionType, int duration){
        //TODO: Statistics in interaction

        // set own agent interaction parameters
        this.isInteracting = true;
        this.interactionType = interactionType;
        // set other agent interaction parameters
        agent.getAgentMovement().setInteracting(true);
        agent.getAgentMovement().setInteractionType(interactionType);
        double interactionStdDeviation, interactionMean;

        if (interactionType == OfficeAgentMovement.InteractionType.NON_VERBAL){
            interactionStdDeviation = 1;
            interactionMean = 2;
        }
        else if (interactionType == OfficeAgentMovement.InteractionType.COOPERATIVE){

            interactionStdDeviation = 5;
            interactionMean = 19;
        }
        else if (interactionType == OfficeAgentMovement.InteractionType.EXCHANGE){

            interactionStdDeviation = 5;
            interactionMean = 19;
        }
        else{
            interactionStdDeviation = 0;
            interactionMean = 0;
        }
        if (duration == -1)
            this.interactionDuration = (int) Math.floor(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean);
        else
            this.interactionDuration = duration;

    }
    public void rollAgentInteraction(OfficeAgent agent){
        //TODO: Statistics in interaction

        double IOS1 = office.getIOS().get(this.getParent().getPersona().ordinal()).get(agent.getPersona().ordinal());
        double IOS2 = office.getIOS().get(agent.getPersona().ordinal()).get(this.getParent().getPersona().ordinal());
        // roll if possible interaction
        double CHANCE1 = Simulator.roll();
        double CHANCE2 = Simulator.roll();
        double interactionStdDeviation, interactionMean;
        if (CHANCE1 < IOS1 && CHANCE2 < IOS2){
            // roll if what kind of interaction
            CHANCE1 = Simulator.roll() * IOS1;
            CHANCE2 = Simulator.roll() * IOS2;
            double CHANCE = (CHANCE1 + CHANCE2) / 2;
//            double CHANCE_NONVERBAL1 = OfficeAgent.chancePerActionInteractionType[this.getParent().getPersona().getID()][this.getParent().getAgentMovement().getCurrentAction().getName().getID()][0],
//                    CHANCE_COOPERATIVE1 = OfficeAgent.chancePerActionInteractionType[this.getParent().getPersona().getID()][this.getParent().getAgentMovement().getCurrentAction().getName().getID()][1],
//                    CHANCE_EXCHANGE1 = OfficeAgent.chancePerActionInteractionType[this.getParent().getPersona().getID()][this.getParent().getAgentMovement().getCurrentAction().getName().getID()][2],
//                    CHANCE_NONVERBAL2 = OfficeAgent.chancePerActionInteractionType[agent.getPersona().getID()][agent.getAgentMovement().getCurrentAction().getName().getID()][0],
//                    CHANCE_COOPERATIVE2 = OfficeAgent.chancePerActionInteractionType[agent.getPersona().getID()][agent.getAgentMovement().getCurrentAction().getName().getID()][1],
//                    CHANCE_EXCHANGE2 = OfficeAgent.chancePerActionInteractionType[agent.getPersona().getID()][agent.getAgentMovement().getCurrentAction().getName().getID()][2];
            double CHANCE_NONVERBAL1 = 0,
                    CHANCE_COOPERATIVE1 = 0,
                    CHANCE_EXCHANGE1 = 0,
                    CHANCE_NONVERBAL2 = 0,
                    CHANCE_COOPERATIVE2 = 0,
                    CHANCE_EXCHANGE2 = 0;
            if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2) / 2){
                OfficeSimulator.currentNonverbalCount++;
                this.getParent().getAgentMovement().setInteractionType(OfficeAgentMovement.InteractionType.NON_VERBAL);
                agent.getAgentMovement().setInteractionType(OfficeAgentMovement.InteractionType.NON_VERBAL);
                interactionMean = getOffice().getNonverbalMean();
                interactionStdDeviation = getOffice().getNonverbalStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2) / 2){
                OfficeSimulator.currentCooperativeCount++;
                this.getParent().getAgentMovement().setInteractionType(OfficeAgentMovement.InteractionType.COOPERATIVE);
                agent.getAgentMovement().setInteractionType(OfficeAgentMovement.InteractionType.COOPERATIVE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getOffice().getCooperativeMean();
                interactionStdDeviation = getOffice().getCooperativeStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2 + CHANCE_EXCHANGE1 + CHANCE_EXCHANGE2) / 2){
                OfficeSimulator.currentExchangeCount++;
                this.getParent().getAgentMovement().setInteractionType(OfficeAgentMovement.InteractionType.EXCHANGE);
                agent.getAgentMovement().setInteractionType(OfficeAgentMovement.InteractionType.EXCHANGE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getOffice().getExchangeMean();
                interactionStdDeviation = getOffice().getExchangeStdDev();
            }
            else{
                return;
            }

            // set own agent interaction parameters
            this.isInteracting = true;
            // set other agent interaction parameters
            agent.getAgentMovement().setInteracting(true);
            if (this.parent.getType() == OfficeAgent.Type.BOSS){
                switch (agent.getType()){
                    case MANAGER -> OfficeSimulator.currentBossManagerCount++;
                    case BUSINESS -> OfficeSimulator.currentBossBusinessCount++;
                    case RESEARCHER -> OfficeSimulator.currentBossResearcherCount++;
                    case TECHNICAL -> OfficeSimulator.currentBossTechnicalCount++;
                    case JANITOR -> OfficeSimulator.currentBossJanitorCount++;
                    case CLIENT -> OfficeSimulator.currentBossClientCount++;
                    case DRIVER -> OfficeSimulator.currentBossDriverCount++;
                    case VISITOR -> OfficeSimulator.currentBossVisitorCount++;
                    case GUARD -> OfficeSimulator.currentBossGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentBossReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentBossSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.MANAGER){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossManagerCount++;
                    case MANAGER -> OfficeSimulator.currentManagerManagerCount++;
                    case BUSINESS -> OfficeSimulator.currentManagerBusinessCount++;
                    case RESEARCHER -> OfficeSimulator.currentManagerResearcherCount++;
                    case TECHNICAL -> OfficeSimulator.currentManagerTechnicalCount++;
                    case JANITOR -> OfficeSimulator.currentManagerJanitorCount++;
                    case CLIENT -> OfficeSimulator.currentManagerClientCount++;
                    case DRIVER -> OfficeSimulator.currentManagerDriverCount++;
                    case VISITOR -> OfficeSimulator.currentManagerVisitorCount++;
                    case GUARD -> OfficeSimulator.currentManagerGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentManagerReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentManagerSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.BUSINESS){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossBusinessCount++;
                    case MANAGER -> OfficeSimulator.currentManagerBusinessCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessBusinessCount++;
                    case RESEARCHER -> OfficeSimulator.currentBusinessResearcherCount++;
                    case TECHNICAL -> OfficeSimulator.currentBusinessTechnicalCount++;
                    case JANITOR -> OfficeSimulator.currentBusinessJanitorCount++;
                    case CLIENT -> OfficeSimulator.currentBusinessClientCount++;
                    case DRIVER -> OfficeSimulator.currentBusinessDriverCount++;
                    case VISITOR -> OfficeSimulator.currentBusinessVisitorCount++;
                    case GUARD -> OfficeSimulator.currentBusinessGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentBusinessReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentBusinessSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.RESEARCHER){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossResearcherCount++;
                    case MANAGER -> OfficeSimulator.currentManagerResearcherCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessResearcherCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherResearcherCount++;
                    case TECHNICAL -> OfficeSimulator.currentResearcherTechnicalCount++;
                    case JANITOR -> OfficeSimulator.currentResearcherJanitorCount++;
                    case CLIENT -> OfficeSimulator.currentResearcherClientCount++;
                    case DRIVER -> OfficeSimulator.currentResearcherDriverCount++;
                    case VISITOR -> OfficeSimulator.currentResearcherVisitorCount++;
                    case GUARD -> OfficeSimulator.currentResearcherGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentResearcherReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentResearcherSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.TECHNICAL){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossTechnicalCount++;
                    case MANAGER -> OfficeSimulator.currentManagerTechnicalCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessTechnicalCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherTechnicalCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalTechnicalCount++;
                    case JANITOR -> OfficeSimulator.currentTechnicalJanitorCount++;
                    case CLIENT -> OfficeSimulator.currentTechnicalClientCount++;
                    case DRIVER -> OfficeSimulator.currentTechnicalDriverCount++;
                    case VISITOR -> OfficeSimulator.currentTechnicalVisitorCount++;
                    case GUARD -> OfficeSimulator.currentTechnicalGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentTechnicalReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentTechnicalSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.JANITOR){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossJanitorCount++;
                    case MANAGER -> OfficeSimulator.currentManagerJanitorCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessJanitorCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherJanitorCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalJanitorCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorJanitorCount++;
                    case CLIENT -> OfficeSimulator.currentJanitorClientCount++;
                    case DRIVER -> OfficeSimulator.currentJanitorDriverCount++;
                    case VISITOR -> OfficeSimulator.currentJanitorVisitorCount++;
                    case GUARD -> OfficeSimulator.currentJanitorGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentJanitorReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentJanitorSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.CLIENT){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossClientCount++;
                    case MANAGER -> OfficeSimulator.currentManagerClientCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessClientCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherClientCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalClientCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorClientCount++;
                    case CLIENT -> OfficeSimulator.currentClientClientCount++;
                    case DRIVER -> OfficeSimulator.currentClientDriverCount++;
                    case VISITOR -> OfficeSimulator.currentClientVisitorCount++;
                    case GUARD -> OfficeSimulator.currentClientGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentClientReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentClientSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.DRIVER){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossDriverCount++;
                    case MANAGER -> OfficeSimulator.currentManagerDriverCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessDriverCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherDriverCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalDriverCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorDriverCount++;
                    case CLIENT -> OfficeSimulator.currentClientDriverCount++;
                    case DRIVER -> OfficeSimulator.currentDriverDriverCount++;
                    case VISITOR -> OfficeSimulator.currentDriverVisitorCount++;
                    case GUARD -> OfficeSimulator.currentDriverGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentDriverReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentDriverSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.VISITOR){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossVisitorCount++;
                    case MANAGER -> OfficeSimulator.currentManagerVisitorCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessVisitorCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherVisitorCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalVisitorCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorVisitorCount++;
                    case CLIENT -> OfficeSimulator.currentClientVisitorCount++;
                    case DRIVER -> OfficeSimulator.currentDriverVisitorCount++;
                    case VISITOR -> OfficeSimulator.currentVisitorVisitorCount++;
                    case GUARD -> OfficeSimulator.currentVisitorGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentVisitorReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentVisitorSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.GUARD){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossGuardCount++;
                    case MANAGER -> OfficeSimulator.currentManagerGuardCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessGuardCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherGuardCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalGuardCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorGuardCount++;
                    case CLIENT -> OfficeSimulator.currentClientGuardCount++;
                    case DRIVER -> OfficeSimulator.currentDriverGuardCount++;
                    case VISITOR -> OfficeSimulator.currentVisitorGuardCount++;
                    case GUARD -> OfficeSimulator.currentGuardGuardCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentGuardReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentGuardSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.RECEPTIONIST){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossReceptionistCount++;
                    case MANAGER -> OfficeSimulator.currentManagerReceptionistCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessReceptionistCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherReceptionistCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalReceptionistCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorReceptionistCount++;
                    case CLIENT -> OfficeSimulator.currentClientReceptionistCount++;
                    case DRIVER -> OfficeSimulator.currentDriverReceptionistCount++;
                    case VISITOR -> OfficeSimulator.currentVisitorReceptionistCount++;
                    case GUARD -> OfficeSimulator.currentGuardReceptionistCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentReceptionistReceptionistCount++;
                    case SECRETARY -> OfficeSimulator.currentReceptionistSecretaryCount++;
                }
            }
            else if (this.parent.getType() == OfficeAgent.Type.SECRETARY){
                switch (agent.getType()){
                    case BOSS -> OfficeSimulator.currentBossSecretaryCount++;
                    case MANAGER -> OfficeSimulator.currentManagerSecretaryCount++;
                    case BUSINESS -> OfficeSimulator.currentBusinessSecretaryCount++;
                    case RESEARCHER -> OfficeSimulator.currentResearcherSecretaryCount++;
                    case TECHNICAL -> OfficeSimulator.currentTechnicalSecretaryCount++;
                    case JANITOR -> OfficeSimulator.currentJanitorSecretaryCount++;
                    case CLIENT -> OfficeSimulator.currentClientSecretaryCount++;
                    case DRIVER -> OfficeSimulator.currentDriverSecretaryCount++;
                    case VISITOR -> OfficeSimulator.currentVisitorSecretaryCount++;
                    case GUARD -> OfficeSimulator.currentGuardSecretaryCount++;
                    case RECEPTIONIST -> OfficeSimulator.currentReceptionistSecretaryCount++;
                    case SECRETARY -> OfficeSimulator.currentSecretarySecretaryCount++;
                }
            }
            // roll duration (NOTE GAUSSIAN)
            this.interactionDuration = (int) (Math.floor((Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean) * (CHANCE1 + CHANCE2) / 2));
            if (agent.getAgentMovement().getInteractionType() == OfficeAgentMovement.InteractionType.NON_VERBAL)
                OfficeSimulator.averageNonverbalDuration = (OfficeSimulator.averageNonverbalDuration * (OfficeSimulator.currentNonverbalCount - 1) + this.interactionDuration) / OfficeSimulator.currentNonverbalCount;
            else if (agent.getAgentMovement().getInteractionType() == OfficeAgentMovement.InteractionType.COOPERATIVE)
                OfficeSimulator.averageCooperativeDuration = (OfficeSimulator.averageCooperativeDuration * (OfficeSimulator.currentCooperativeCount - 1) + this.interactionDuration) / OfficeSimulator.currentCooperativeCount;
            else if (agent.getAgentMovement().getInteractionType() == OfficeAgentMovement.InteractionType.EXCHANGE)
                OfficeSimulator.averageExchangeDuration = (OfficeSimulator.averageExchangeDuration * (OfficeSimulator.currentExchangeCount - 1) + this.interactionDuration) / OfficeSimulator.currentExchangeCount;
        }
    }
    public void interact(){
        //TODO: Statistics in interaction

        // if 0 na, remove interacting phase for agent
        if (this.interactionDuration == 0){
            this.isInteracting = false;
            this.interactionType = null;
        }
        // -- interaction
        else{
            this.interactionDuration--;
        }
    }

    public boolean isInteracting() {
        return isInteracting;
    }

    public void setInteracting(boolean interacting) {
        isInteracting = interacting;
    }

    public boolean isSimultaneousInteractionAllowed() {
        return isSimultaneousInteractionAllowed;
    }

    public void setSimultaneousInteractionAllowed(boolean simultaneousInteractionAllowed) {
        isSimultaneousInteractionAllowed = simultaneousInteractionAllowed;
    }

    public int getInteractionDuration() {
        return interactionDuration;
    }

    public void setInteractionDuration(int interactionDuration) {
        this.interactionDuration = interactionDuration;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }
}