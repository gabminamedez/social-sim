package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.generic.pathfinding.AgentMovement;
import com.socialsim.model.core.agent.generic.pathfinding.AgentPath;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.Vector;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchfield.StallField;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.simulator.Simulator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UniversityAgentMovement extends AgentMovement {

    private final UniversityAgent parent;
    private final Coordinates position; // Denotes the position of the agent
    private final University university;
    private final double baseWalkingDistance; // Denotes the distance (m) the agent walks in one second
    private double preferredWalkingDistance;
    private double currentWalkingDistance;
    private double proposedHeading;// Denotes the proposed heading of the agent in degrees where E = 0 degrees, N = 90 degrees, W = 180 degrees, S = 270 degrees
    private double heading;
    private double previousHeading;

    private Patch currentPatch;
    private Amenity currentAmenity;
    private PatchField currentPatchField;
    private Patch goalPatch;
    private Amenity goalAmenity;
    private Amenity.AmenityBlock goalAttractor;
    private PatchField goalPatchField;
    private QueueingPatchField goalQueueingPatchField; // Denotes the patch field of the agent goal
    private Patch goalNearestQueueingPatch; // Denotes the patch with the nearest queueing patch

    private UniversityRoutePlan routePlan;
    private AgentPath currentPath; // Denotes the current path followed by this agent, if any
    private int stateIndex;
    private UniversityState state;
    private UniversityAction action; // Low-level description of what the agent is doing

    private boolean isWaitingOnAmenity; // Denotes whether the agent is temporarily waiting on an amenity to be vacant
    private boolean hasEncounteredAgentToFollow; // Denotes whether this agent has encountered the agent to be followed in the queue
    private UniversityAgent agentFollowedWhenAssembling; // Denotes the agent this agent is currently following while assembling
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
    private boolean willPathfind; // Denotes whether the agent is ready to pathfind
    private boolean hasPathfound; // Denotes whether this agent has already pathfound
    private boolean shouldStepForward; // Denotes whether this agent should take a step forward after it left its goal
    private final ConcurrentHashMap<Patch, Integer> recentPatches; // Denotes the recent patches this agent has been in

    // The vectors of this agent
    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    public UniversityAgentMovement(Patch spawnPatch, UniversityAgent parent, double baseWalkingDistance, Coordinates coordinates) { // For inOnStart agents
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());

        final double interQuartileRange = 0.12; // The walking speed values shall be in m/s
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        if (parent.getInOnStart()) {
            // All inOnStart agents will face the south by default
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.previousHeading = Math.toRadians(270.0);
            this.fieldOfViewAngle = Math.toRadians(270.0);
        }
        else {
            // All newly generated agents will face the north by default
            this.proposedHeading = Math.toRadians(90.0);
            this.heading = Math.toRadians(90.0);
            this.previousHeading = Math.toRadians(90.0);
            this.fieldOfViewAngle = Math.toRadians(90.0);
        }

        // Add this agent to the spawn patch
        this.currentPatch = spawnPatch;
        this.currentPatch.getAgents().add(parent);
        this.university = (University) currentPatch.getEnvironment();

        // Set the agent's time until it fully accelerates
        this.ticksUntilFullyAccelerated = 10;
        this.ticksAcceleratedOrMaintainedSpeed = 0;

        this.routePlan = new UniversityRoutePlan(parent, university, currentPatch);
        if (parent.getInOnStart()) {
            // TODO: Set initial states and actions if necessary
//            this.state = UniversityState.GOING_TO_SECURITY;
//            this.stateIndex = 0;
//            this.action = UniversityAction.STANDING;
        }
        else {
            // TODO: Set initial states and actions if necessary
//            this.state = UniversityState.GOING_TO_SECURITY;
//            this.stateIndex = 0;
//            this.action = UniversityAction.WILL_QUEUE;
            this.currentAmenity = university.getUniversityGates().get(1); // Getting Entrance Gate
        }

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal(false); // Set the agent goal
    }

    public UniversityAgent getParent() {
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

        Patch newPatch = this.university.getPatch(new Coordinates(coordinates.getX(), coordinates.getY())); // Get the patch of the new position

        if (!previousPatch.equals(newPatch)) { // If the new position is on a different patch, remove the agent from its old patch, then add it to the new patch
            previousPatch.getAgents().remove(this.parent);
            newPatch.getAgents().add(this.parent);

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

    public University getUniversity() {
        return university;
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

    public Amenity getCurrentAmenity() {
        return currentAmenity;
    }

    public PatchField getCurrentPatchField() {
        return currentPatchField;
    }

    public Amenity.AmenityBlock getGoalAttractor() {
        return goalAttractor;
    }

    public Patch getGoalPatch() {
        return goalPatch;
    }

    public Amenity getGoalAmenity() {
        return goalAmenity;
    }

    public PatchField getGoalPatchField() {
        return goalPatchField;
    }

    public QueueingPatchField getGoalQueueingPatchField() {
        return goalQueueingPatchField;
    }

    public Patch getGoalNearestQueueingPatch() {
        return goalNearestQueueingPatch;
    }

    public UniversityRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(UniversityRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public AgentPath getCurrentPath() {
        return currentPath;
    }

    public UniversityState getUniversityState() {
        return state;
    }

    public void setUniversityState(UniversityState state) {
        this.state = state;
    }

    public UniversityAction getUniversityAction() {
        return action;
    }

    public void setUniversityAction(UniversityAction action) {
        this.action = action;
    }

    public boolean isWaitingOnAmenity() {
        return isWaitingOnAmenity;
    }

    public UniversityAgent getAgentFollowedWhenAssembling() {
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

    public boolean willPathFind() {
        return willPathfind;
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

    // Use the A* algorithm (with Euclidean distance to compute the f-score) to find the shortest path to the given goal patch
    /*public static AgentPath computePathWithinFloor(Patch startingPatch, Patch goalPatch, boolean includeStartingPatch, boolean includeGoalPatch, boolean passThroughBlockables) {
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
            if (patchToExplore.equals(goalPatch)) {
                Stack<Patch> path = new Stack<>();
                double length = 0.0;

                Patch currentPatch = goalPatch;

                while (cameFrom.containsKey(currentPatch)) {
                    Patch previousPatch = cameFrom.get(currentPatch);
                    length += Coordinates.distance(previousPatch.getPatchCenterCoordinates(), currentPatch.getPatchCenterCoordinates());
                    currentPatch = previousPatch;
                    path.push(currentPatch);
                }

                AgentPath agentPath = new AgentPath(length, path);

                return agentPath;
            }

            openSet.remove(patchToExplore);

            List<Patch> patchToExploreNeighbors = patchToExplore.getNeighbors();

            for (Patch patchToExploreNeighbor : patchToExploreNeighbors) {
                if (patchToExploreNeighbor.getAmenityBlock() == null  || patchToExploreNeighbor.getAmenityBlock() != null || (!includeStartingPatch && patchToExplore.equals(startingPatch) || !includeGoalPatch && patchToExploreNeighbor.equals(goalPatch))) {
                    // Avoid patches that are close to amenity blocks, unless absolutely necessary
                    double obstacleClosenessPenalty = patchToExploreNeighbor.getAmenityBlocksAround() * 2.0;
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
    }*/

    public boolean isNextAmenityGoal() { // Check whether the current goal amenity is a goal or not
        return Goal.isGoal(this.goalAmenity);
    }

    public boolean hasJustLeftGoal() { // Check whether the agent has just left the goal (if the agent is at a certain number of ticks since leaving the goal)
        final int hasJustLeftGoalThreshold = 3;

        return this.timeSinceLeftPreviousGoal <= hasJustLeftGoalThreshold;
    }

    public void resetGoal(boolean shouldStepForwardFirst) { // Reset the agent's goal
        this.goalPatch = null;
        this.goalAmenity = null;
        this.goalAttractor = null;
        this.goalPatchField = null;
        this.goalQueueingPatchField = null; // Take note of the patch field of the agent's goal
        this.goalNearestQueueingPatch = null; // Take note of the agent's nearest queueing patch

        this.hasEncounteredAgentToFollow = false; // No agents have been encountered yet
        this.isWaitingOnAmenity = false; // This agent is not yet waiting
        this.shouldStepForward = shouldStepForwardFirst;
        this.agentFollowedWhenAssembling = null; // This agent is not following anyone yet

        this.distanceMovedInTick = 0.0; // This agent hasn't moved yet
        this.noMovementCounter = 0;
        this.movementCounter = 0;
        this.noNewPatchesSeenCounter = 0;
        this.newPatchesSeenCounter = 0;
        this.timeSinceLeftPreviousGoal = 0;

        this.willPathfind = false;
        this.hasPathfound = false;
        this.recentPatches.clear(); // This agent has no recent patches yet
        this.free(); // This agent is not yet stuck
    }

//    public void nextPlanItem () { // Parent function to extract the next item in this agent's route plan
//        UniversityState nextItem = this.routePlan.getCurrentClass();
//
//        if (PatchField.class.isAssignableFrom(nextItem)) {
//            goToRoom(nextItem);
//        }
//        else if (Amenity.class.isAssignableFrom(nextItem)) {
//            chooseGoal(nextItem);
//        }
//    }

    public void goToRoom(Class<? extends BaseObject> nextRoomClass) {
        // TODO: go to room mechanisms
    }

    // Set the nearest goal to this agent; That goal should also have the fewer agents queueing for it
    // To determine this, for each two agents in the queue (or fraction thereof), a penalty of one tile is added to the distance to this goal
    public void chooseGoal(Class<? extends BaseObject> nextAmenityClass) {
        if (this.goalAmenity == null) { // Only set the goal if one hasn't been set yet
            // Get the amenity list in this university
            List<? extends Amenity> amenityListInFloor = this.university.getAmenityList((Class<? extends Amenity>) nextAmenityClass);

            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>(); // Compile all attractors from each amenity in the amenity list

            for (Amenity amenity : amenityListInFloor) {
                NonObstacle nonObstacle = ((NonObstacle) amenity);

                if (!nonObstacle.isEnabled()) { // Only consider enabled amenities
                    continue;
                }

                // Filter the amenity search space only to what is compatible with this agent
                if (amenity instanceof UniversityGate) {
                    // If the goal of the agent is a station gate, this means the agent is leaving; So only consider station gates which allow exits and accepts the agent's direction
                    UniversityGate universityGateExit = ((UniversityGate) amenity);

                    if (universityGateExit.getUniversityGateMode() == UniversityGate.UniversityGateMode.ENTRANCE) {
                        continue;
                    }
                }

                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                    double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                    distancesToAttractors.put(attractor, distanceToAttractor);
                }
            }

            double minimumAttractorScore = Double.MAX_VALUE;

            // Then for each compiled amenity and their distance from this agent, see which has the smallest distance while taking into account the agents queueing for that amenity, if any
            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : distancesToAttractors.entrySet()) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                Double candidateDistance = distancesToAttractorEntry.getValue();

                Amenity currentAmenity;

                currentAmenity = candidateAttractor.getParent();

                /*if (currentAmenity instanceof Queueable) { // Only collect queue objects from queueables
                    Queueable queueable = ((Queueable) currentAmenity);
                    currentQueueObject = queueable.getQueueObject();
                }
                else {
                    currentQueueObject = null;
                }

                // If this is a queueable, take into account the agents queueing (except if it is a security gate)
                // If this is not a queueable (or if it's a security gate), the distance will suffice
                double attractorScore;

                if (currentQueueObject != null) {
                    if (!(currentAmenity instanceof Security)) {
                        double agentPenalty = 25.0; // Avoid queueing to long lines
                        attractorScore = candidateDistance + currentQueueObject.getAgentsQueueing().size() * agentPenalty;
                    }
                    else {
                        attractorScore = candidateDistance;
                    }
                }
                else {
                    attractorScore = candidateDistance;
                }

                if (attractorScore < minimumAttractorScore) {
                    minimumAttractorScore = attractorScore;
                    chosenAmenity = currentAmenity;
                    chosenQueueObject = currentQueueObject;
                    chosenAttractor = candidateAttractor;
                }*/
            }

            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
            this.goalPatch = chosenAttractor.getPatch();
        }
    }

    public void chooseStall() {
        if (this.goalAmenity == null) { // Only set the goal if one hasn't been set yet
            List<StallField> stallFields = this.university.getStallFields();
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
            for (StallField stallField : stallFields) {
                Amenity.AmenityBlock attractor = stallField.getAssociatedPatches().get(0).getAmenityBlock();

                double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                distancesToAttractors.put(attractor, distanceToAttractor);
            }

            double minimumAttractorScore = Double.MAX_VALUE;

            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : distancesToAttractors.entrySet()) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                Double candidateDistance = distancesToAttractorEntry.getValue();

                Amenity currentAmenity = candidateAttractor.getParent();
                QueueingPatchField currentStallKey = candidateAttractor.getPatch().getQueueingPatchField().getKey();

                double agentPenalty = 25.0;
                double attractorScore = candidateDistance + currentStallKey.getQueueingAgents().size() * agentPenalty;

                if (attractorScore < minimumAttractorScore) {
                    minimumAttractorScore = attractorScore;
                    chosenAmenity = currentAmenity;
                    chosenAttractor = candidateAttractor;
                }
            }

            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
            this.goalPatch = chosenAttractor.getPatch().getQueueingPatchField().getKey().getLastQueuePatch();
            this.goalQueueingPatchField = chosenAttractor.getPatch().getQueueingPatchField().getKey();
        }
    }

    private Coordinates getFuturePosition() {
        return getFuturePosition(this.goalAmenity, this.proposedHeading, this.preferredWalkingDistance);
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

        // If distance between agent and goal is less than distance agent covers every time it walks, "snap" the position of agent to center of goal immediately to avoid overshooting its target
        if (minimumDistance < walkingDistance) {
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

    public boolean moveSocialForce() { // Make the agent move in accordance with social forces
        // The smallest repulsion an agent may inflict on another
        final double minimumAgentRepulsion = 0.01 * this.preferredWalkingDistance;

        // stuck if not moved for this no. of ticks
        final int noMovementTicksThreshold = (this.getGoalAmenityAsQueueableGoal() != null) ? this.getGoalAmenityAsQueueableGoal().getWaitingTime() : 5;

        // If the agent has not seen new patches for more than this number of ticks, the agent will be considered stuck
        final int noNewPatchesSeenTicksThreshold = 5;

        // Stuck agent must move this no. of ticks
        final int unstuckTicksThreshold = 60;

        // If the distance the agent moves per tick is less than this distance, this agent is considered to not have moved
        final double noMovementThreshold = 0.01 * this.preferredWalkingDistance;

        // UniversityAgent hasn't moved if new patches seen are less than this
        final double noNewPatchesSeenThreshold = 5;

        // The distance to another agent before this agent slows down
        final double slowdownStartDistance = 2.0;

        // The minimum allowable distance from another agent at its front before this agent stops
        final double minimumStopDistance = 0.6;

        // The maximum allowable distance from another agent at its front before this agent stops
        double maximumStopDistance = 1.0;

        // Count the number of agents and obstacles in the the relevant patches
        int numberOfAgents = 0;
        int numberOfObstacles = 0;

        // The distance from the agent's center by which repulsive effects from agents start to occur
        double maximumAgentStopDistance = 1.0;

        // The distance from the agent's center by which repulsive effects from agents are at a maximum
        final double minimumAgentStopDistance = 0.6;

        // The distance from the agent's center by which repulsive effects from obstacles start to occur
        double maximumObstacleStopDistance = 1.0;

        // The distance from the agent's center by which repulsive effects from obstacles are at a maximum
        final double minimumObstacleStopDistance = 0.6;

        List<Patch> patchesToExplore = this.get7x7Field(this.proposedHeading, true, Math.toRadians(360.0));

        // Clear vectors from the previous computations
        this.repulsiveForceFromAgents.clear();
        this.repulsiveForcesFromObstacles.clear();
        this.attractiveForce = null;
        this.motivationForce = null;

        // Add the repulsive effects from nearby agents and obstacles
        TreeMap<Double, Amenity.AmenityBlock> obstaclesEncountered = new TreeMap<>();
        TreeMap<Double, Patch> wallsEncountered = new TreeMap<>();

        // This will contain the final motivation vector
        List<Vector> vectorsToAdd = new ArrayList<>();

        // Get the current heading, which will be the previous heading later
        this.previousHeading = this.heading;

        // Compute the proposed future position
        Coordinates proposedNewPosition;

        // Check if the agent is set to take one initial step forward
        // Compute for the proposed future position
        proposedNewPosition = this.getFuturePosition(this.preferredWalkingDistance);

        this.preferredWalkingDistance = this.baseWalkingDistance;

        // slow down when near goal/obstacle
        final double distanceSlowdownStart = 5.0;
        final double speedDecreaseFactor = 0.5;

        double distanceToGoal = Coordinates.distance(
                this.currentPatch,
                this.getGoalAmenity().getAttractors().get(0).getPatch()
        );

        if (
                distanceToGoal < distanceSlowdownStart
                        && this.hasClearLineOfSight(
                        this.position,
                        this.goalAmenity.getAttractors().get(0).getPatch().getPatchCenterCoordinates(),
                        true
                )
        ) {
            this.preferredWalkingDistance *= speedDecreaseFactor;
        }

        // If this agent is queueing, the only social forces that apply are attractive forces to agents
        // and obstacles (if not in queueing action)
        // TODO add code to check if agent is already in queue / queueing logic
        if (
                this.state.getName() == UniversityState.Name.GOING_TO_SECURITY || this.state.getName() == UniversityState.Name.GOING_TO_LUNCH
                || this.state.getName() == UniversityState.Name.NEEDS_DRINK
        ) {
            //Heading towards the queue, but not inside the queue yet
            if (this.action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER ||
                    this.action.getName() == UniversityAction.Name.QUEUE_VENDOR ||
                    this.action.getName() == UniversityAction.Name.QUEUE_FOUNTAIN){
                Patch nextQueuePatch = this.currentPatch.getQueueingPatchField().getKey().getNextQueuePatch(currentPatch);
                if (nextQueuePatch.getAgents().isEmpty()){
                    move(Coordinates.getPatchCenterCoordinates(nextQueuePatch));
                }
                else if (this.currentPatch.getQueueingPatchField().getKey().inLastQueuePatch(currentPatch)){
                    // wala
                }


            }
            else if (this.action.getName() == UniversityAction.Name.CHECKOUT || this.action.getName() == UniversityAction.Name.DRINK_FOUNTAIN ||
                    this.action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT || this.action.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT ||
                    this.action.getName() == UniversityAction.Name.LUNCH_STAY_PUT || this.action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE ||
                    this.action.getName() == UniversityAction.Name.VIEW_BULLETIN || this.action.getName() == UniversityAction.Name.SIT_ON_BENCH ||
                    this.action.getName() == UniversityAction.Name.GUARD_STAY_PUT || this.action.getName() == UniversityAction.Name.CLEAN_STAY_PUT || this.action.getName() == UniversityAction.Name.JANITOR_CHECK_FOUNTAIN){
                this.stop();
                decrementDuration(); // TODO check if this should be done on Simulator or Movement

            }
            else{ // Not in queue and not staying put
                // TODO Calculate which queue to go to for cafeteria gogo julian
                if (this.isStuck || this.isServicedByQueueableGoal() && this.noMovementCounter > noMovementTicksThreshold) {
                    this.isStuck = true;
                    this.stuckCounter++;
                }

                // Count agents within FOV
                TreeMap<Double, UniversityAgent> agentsWithinFieldOfView = new TreeMap<>();

                // Look around the patches that fall on the agent's field of view
                for (Patch patch : patchesToExplore) {
                    // If not in queue, count obstacles
                    if (this.action.getName() != UniversityAction.Name.GO_THROUGH_SCANNER && this.action.getName() != UniversityAction.Name.QUEUE_VENDOR
                            && this.action.getName() != UniversityAction.Name.QUEUE_FOUNTAIN) {
                        Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
                        Class aPatchField = patch.getPatchField().getKey().getClass();

                        // Get the distance between this agent and the obstacle on this patch
                        if (hasObstacle(patch, goalAmenity)) {
                            // Take note of the obstacle density in this area
                            numberOfObstacles++;

                            // Compute magnitude for repulsion force
                            if(patchAmenityBlock != null){
                                double distanceToObstacle = Coordinates.distance(
                                        this.position,
                                        patchAmenityBlock.getPatch().getPatchCenterCoordinates()
                                );

                                if (distanceToObstacle <= slowdownStartDistance) {
                                    obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
                                }
                            }else{
                                double distanceToObstacle = Coordinates.distance(
                                        this.position,
                                        patch.getPatchCenterCoordinates()
                                );

                                if (distanceToObstacle <= slowdownStartDistance) {
                                    wallsEncountered.put(distanceToObstacle, patch);
                                }
                            }

                        }
                    }
                    // confirm other agents within FOV
                    if (!this.isStuck) { // make sure agent is not stuck
                        for (Agent otherAgent : patch.getAgents()) {
                            UniversityAgent universityAgent = (UniversityAgent) otherAgent;
                            // Make sure that the agent discovered isn't itself
                            if (!otherAgent.equals(this.getParent())) {
                                // Take note of the agent density in this area
                                numberOfAgents++;

                                // Check if this agent is within the field of view and within the slowdown
                                // distance
                                double distanceToAgent = Coordinates.distance(
                                        this.position,
                                        universityAgent.getAgentMovement().getPosition()
                                );

                                if (Coordinates.isWithinFieldOfView(
                                        this.position,
                                        universityAgent.getAgentMovement().getPosition(),
                                        this.proposedHeading,
                                        this.fieldOfViewAngle)
                                        && distanceToAgent <= slowdownStartDistance) {
                                    agentsWithinFieldOfView.put(distanceToAgent, universityAgent);
                                }

                            }
                        }
                    }
                }

                // Compute the perceived density of the agents
                final double maximumDensityTolerated = 3.0;
                final double agentDensity
                        = (numberOfAgents > maximumDensityTolerated ? maximumDensityTolerated : numberOfAgents)
                        / maximumDensityTolerated;

                // For each agent found within the slowdown distance, get the nearest one, if there is any
                Map.Entry<Double, UniversityAgent> nearestAgentEntry = agentsWithinFieldOfView.firstEntry();

                // If there are no agents within the field of view, good - move normally
                if (nearestAgentEntry == null|| nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity() != null
                        && !nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity().equals(this.goalAmenity)) {
                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;

                    // Get the attractive force of this agent to the new position
                    this.attractiveForce = this.computeAttractiveForce(
                            new Coordinates(this.position),
                            this.proposedHeading,
                            proposedNewPosition,
                            this.preferredWalkingDistance
                    );

                    vectorsToAdd.add(attractiveForce);
                } else { // If there are agents in the way
                    // Get a random (but weighted) floor field value around the other agent
                    Patch PatchFieldPatch = this.getBestQueueingPatchAroundAgent(
                            nearestAgentEntry.getValue()
                    );

                    // Check the distance of that nearest agent to this agent
                    double distanceToNearestAgent = nearestAgentEntry.getKey();

                    // Modify the maximum stopping distance depending on the density of the environment
                    // That is, the denser the surroundings, the less space this agent will allow between other
                    // agents
                    maximumStopDistance -= (maximumStopDistance - minimumStopDistance) * agentDensity;

                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;

                    // Else, just slow down and move towards the direction of that agent in front
                    // The slowdown factor linearly depends on the distance between this agent and the other
                    final double slowdownFactor
                            = (distanceToNearestAgent - maximumStopDistance)
                            / (slowdownStartDistance - maximumStopDistance);

                    double computedWalkingDistance = slowdownFactor * this.preferredWalkingDistance;

                    // TODO Used to calculate when queueing for train; can be used for cafeteria

                    // Only head towards that patch if the distance from that patch to the goal is further than the
                    // distance from this agent to the goal
                    double distanceFromChosenPatchToGoal = Coordinates.distance(
                            this.currentFloor.getStation(),
                            PatchFieldPatch,
                            this.goalAttractor.getPatch()
                    );

                    double distanceFromThisAgentToGoal = Coordinates.distance(
                            this.currentFloor.getStation(),
                            this.currentPatch,
                            this.goalAttractor.getPatch()
                    );

                    double revisedHeading;
                    Coordinates revisedPosition;

                    if (distanceFromChosenPatchToGoal < distanceFromThisAgentToGoal) {

                        revisedHeading = Coordinates.headingTowards(
                                this.position,
                                PatchFieldPatch.getPatchCenterCoordinates()
                        );

                        revisedPosition = this.getFuturePosition(
                                this.position,
                                revisedHeading,
                                computedWalkingDistance
                        );

                        // Get the attractive force of this agent to the new position
                        this.attractiveForce = this.computeAttractiveForce(
                                new Coordinates(this.position),
                                revisedHeading,
                                revisedPosition,
                                computedWalkingDistance
                        );

                        vectorsToAdd.add(attractiveForce);
                        // TODO fix if null pointer exception; no agents within FOV
                        for (
                                Map.Entry<Double, UniversityAgent> otherAgentEntry
                                : agentsWithinFieldOfView.entrySet()
                        ) {
                            // Then compute the repulsive force from this agent
                            // Compute the perceived density of the agents
                            // Assuming the maximum density a agent sees within its environment is 5 before it thinks
                            // the crowd
                            // is very dense, rate the perceived density of the surroundings by dividing the number of
                            // people by the
                            // maximum tolerated number of agents
                            final int maximumAgentCountTolerated = 5;

                            // The distance by which the repulsion starts to kick in will depend on the density of the agent's
                            // surroundings
                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;

                            double computedMaximumDistance = computeMaximumRepulsionDistance(
                                    numberOfObstacles,
                                    maximumAgentCountTolerated,
                                    minimumAgentCount,
                                    maximumDistance,
                                    maximumAgentCount,
                                    minimumDistance
                            );

                            Vector agentRepulsiveForce = computeSocialForceFromAgent(
                                    otherAgentEntry.getValue(),
                                    otherAgentEntry.getKey(),
                                    computedMaximumDistance,
                                    minimumAgentStopDistance,
                                    this.preferredWalkingDistance
                            );

                            // Add the computed vector to the list of vectors
                            this.repulsiveForceFromAgents.add(agentRepulsiveForce);
                        }
                    } // end of finding a new patch closer to the goal
                 /*else {
                    Coordinates revisedPosition = this.getFuturePosition(computedWalkingDistance);

                    // Get the attractive force of this agent to the new position
                    this.attractiveForce = this.computeAttractiveForce(
                            new Coordinates(this.position),
                            this.proposedHeading,
                            revisedPosition,
                            computedWalkingDistance
                    );

                    vectorsToAdd.add(attractiveForce);
                }*/
                }
            }

        } else {
            // Check if agent is stuck
            if (
                    this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold
            ) {
                this.isStuck = true;
                this.stuckCounter++;
            }

            // Only apply the social forces of a set number of agents and obstacles
            int agentsProcessed = 0;
            final int agentsProcessedLimit = 5;

            // Look around the patches that fall on the agent's field of view
            for (Patch patch : patchesToExplore) {
                // If this patch has an obstacle, take note of it to add a repulsive force from it later
                Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
                Class aPatchField = patch.getPatchField().getKey().getClass();

                // Get the distance between this agent and the obstacle on this patch
                if (hasObstacle(patch, goalAmenity)) {
                    // Take note of the obstacle density in this area
                    numberOfObstacles++;

                    // Compute magnitude for repulsion force
                    if(patchAmenityBlock != null){
                        double distanceToObstacle = Coordinates.distance(
                                this.position,
                                patchAmenityBlock.getPatch().getPatchCenterCoordinates()
                        );

                        if (distanceToObstacle <= slowdownStartDistance) {
                            obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
                        }
                    }else{
                        double distanceToObstacle = Coordinates.distance(
                                this.position,
                                patch.getPatchCenterCoordinates()
                        );

                        if (distanceToObstacle <= slowdownStartDistance) {
                            wallsEncountered.put(distanceToObstacle, patch);
                        }
                    }
                }
                // Inspect each agent in each patch in the patches in the field of view
                for (Agent otherAgent : patch.getAgents()) {
                    UniversityAgent universityAgent = (UniversityAgent) otherAgent;
                    if (agentsProcessed == agentsProcessedLimit) {
                        break;
                    }

                    // Make sure that the agent discovered isn't itself
                    if (!otherAgent.equals(this.getParent())) {
                        // Take note of the agent density in this area
                        numberOfAgents++;

                        // Get the distance between this agent and the other agent
                        double distanceToOtherAgent = Coordinates.distance(
                                this.position,
                                universityAgent.getAgentMovement().getPosition()
                        );

                        // If the distance is less than or equal to the distance when repulsion is supposed to kick in,
                        // compute for the magnitude of that repulsion force
                        if (distanceToOtherAgent <= slowdownStartDistance) {

                            final int maximumAgentCountTolerated = 5;

                            // The distance by which the repulsion starts to kick in will depend on the density of the agent's
                            // surroundings
                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;

                            double computedMaximumDistance = computeMaximumRepulsionDistance(
                                    numberOfObstacles,
                                    maximumAgentCountTolerated,
                                    minimumAgentCount,
                                    maximumDistance,
                                    maximumAgentCount,
                                    minimumDistance
                            );

                            Vector agentRepulsiveForce = computeSocialForceFromAgent(
                                    universityAgent,
                                    distanceToOtherAgent,
                                    computedMaximumDistance,
                                    minimumAgentStopDistance,
                                    this.preferredWalkingDistance
                            );

                            // Add the computed vector to the list of vectors
                            this.repulsiveForceFromAgents.add(agentRepulsiveForce);

                            // If a queueing agent has been encountered, do not pathfind anymore for this
                            // goal
                            // TODO can be used if agent has found a queue
                            /*if (
                                    this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
                                            && this.hasEncounteredAgentToFollow
                            ) {
                                this.hasPathfound = true;
                            }*/

                            agentsProcessed++;
                        }
                    }
                }
            }

            // Get the attractive force of this agent to the new position
            this.attractiveForce = this.computeAttractiveForce(
                    new Coordinates(this.position),
                    this.proposedHeading,
                    proposedNewPosition,
                    this.preferredWalkingDistance
            );

            vectorsToAdd.add(attractiveForce);
        }


        // Take note of the previous walking distance of this agent
        double previousWalkingDistance = this.currentWalkingDistance;

        vectorsToAdd.addAll(this.repulsiveForceFromAgents);

        // Then compute the partial motivation force of the agent
        Vector partialMotivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd);

        // If the resultant vector is null (i.e., no change in position), simply don't move at all
        if (partialMotivationForce != null) {
            // calculate repulsion
            final int minimumObstacleCount = 1;
            final double maximumDistance = 2.0;
            final int maximumObstacleCount = 2;
            final double minimumDistance = 0.7;

            final int maximumObstacleCountTolerated = 2;

            double computedMaximumDistance = computeMaximumRepulsionDistance(
                    numberOfObstacles,
                    maximumObstacleCountTolerated,
                    minimumObstacleCount,
                    maximumDistance,
                    maximumObstacleCount,
                    minimumDistance
            );

            // Only apply the social forces on a set number of obstacles
            int obstaclesProcessed = 0;
            final int obstaclesProcessedLimit = 4;
            // Added code block to consider walls as obstacles
            for (Map.Entry<Double, Patch> wallEntry : wallsEncountered.entrySet()) {
                if (obstaclesProcessed == obstaclesProcessedLimit) {
                    break;
                }

                this.repulsiveForcesFromObstacles.add(
                        computeSocialForceFromObstacle(
                                wallEntry.getValue(),
                                wallEntry.getKey(),
                                computedMaximumDistance,
                                minimumObstacleStopDistance,
                                partialMotivationForce.getMagnitude()
                        )
                );

                obstaclesProcessed++;
            }

            for (Map.Entry<Double, Amenity.AmenityBlock> obstacleEntry : obstaclesEncountered.entrySet()) {
                if (obstaclesProcessed == obstaclesProcessedLimit) {
                    break;
                }

                this.repulsiveForcesFromObstacles.add(
                        computeSocialForceFromObstacle(
                                obstacleEntry.getValue(),
                                obstacleEntry.getKey(),
                                computedMaximumDistance,
                                minimumObstacleStopDistance,
                                partialMotivationForce.getMagnitude()
                        )
                );

                obstaclesProcessed++;
            }

            vectorsToAdd.clear();

            vectorsToAdd.add(partialMotivationForce);
            vectorsToAdd.addAll(this.repulsiveForcesFromObstacles);

            // Finally, compute the final motivation force
            this.motivationForce = Vector.computeResultantVector(
                    new Coordinates(this.position),
                    vectorsToAdd
            );

            if (this.motivationForce != null) {
                // Cap the magnitude of the motivation force to the agent's preferred walking distance
                if (this.motivationForce.getMagnitude() > this.preferredWalkingDistance) {
                    this.motivationForce.adjustMagnitude(this.preferredWalkingDistance);
                }

                // Then adjust its heading with minor stochastic deviations
                this.motivationForce.adjustHeading(
                        this.motivationForce.getHeading()
                                + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(5)
                );

                try {
                    // Set the new heading
                    double newHeading = motivationForce.getHeading();

                    Coordinates candidatePosition = this.motivationForce.getFuturePosition();

                    if (hasClearLineOfSight(this.position, candidatePosition, false)) {
                        this.move(candidatePosition);
                    } else {
                        double revisedHeading;
                        Coordinates newFuturePosition;

                        int attempts = 0;
                        final int attemptLimit = 2;

                        boolean freeSpaceFound;

                        do {
                            // Go back with the same magnitude as the original motivation force, but at a different
                            // heading
                            revisedHeading
                                    = (motivationForce.getHeading() + Math.toRadians(180)) % Math.toRadians(360);

                            // Add some stochasticity to this revised heading
                            revisedHeading += Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(90);
                            revisedHeading %= Math.toRadians(360);

                            // Then calculate the future position from the current position
                            newFuturePosition = this.getFuturePosition(
                                    this.position,
                                    revisedHeading,
                                    this.preferredWalkingDistance * 0.25
                            );

                            freeSpaceFound
                                    = hasClearLineOfSight(this.position, newFuturePosition, false);

                            attempts++;
                        } while (attempts < attemptLimit && !freeSpaceFound);

                        // If all the attempts are used and no free space has been found, don't move at all
                        if (attempts != attemptLimit || freeSpaceFound) {
                            this.move(newFuturePosition);
                        }
                    }

                    if (
                            !this.isStuck
                                    || Coordinates.headingDifference(
                                    this.heading,
                                    newHeading
                            ) <= Math.toDegrees(90.0)
                                    || this.currentWalkingDistance > noMovementThreshold
                    ) {
                        this.heading = newHeading;
                    }

                    // Also take note of the new speed
                    this.currentWalkingDistance = motivationForce.getMagnitude();

                    // Finally, take note of the distance travelled by this agent
                    this.distanceMovedInTick = motivationForce.getMagnitude();

                    // If this agent's distance covered falls under the threshold, increment the counter denoting the ticks
                    // spent not moving
                    // Otherwise, reset the counter
                    // Do not count for movements/non-movements when the agent is in the "in queue" state
                    if (this.action.getName() != UniversityAction.Name.GO_THROUGH_SCANNER && this.action.getName() != UniversityAction.Name.QUEUE_VENDOR
                            && this.action.getName() != UniversityAction.Name.QUEUE_FOUNTAIN) {
                        if (this.recentPatches.size() <= noNewPatchesSeenThreshold) {
                            this.noNewPatchesSeenCounter++;
                            this.newPatchesSeenCounter = 0;
                        } else {
                            this.noNewPatchesSeenCounter = 0;
                            this.newPatchesSeenCounter++;
                        }
                    } else {
                        if (
                                this.distanceMovedInTick < noMovementThreshold
                        ) {
                            this.noMovementCounter++;
                            this.movementCounter = 0;
                        } else {
                            this.noMovementCounter = 0;
                            this.movementCounter++;
                        }
                    }

                    // If the agent has moved above the no-movement threshold for at least this number of ticks,
                    // remove the agent from its stuck state
                    if (
                            this.isStuck
                                    && (
                                    (
                                            (this.action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER || this.action.getName() == UniversityAction.Name.QUEUE_VENDOR
                                                    || this.action.getName() == UniversityAction.Name.QUEUE_FOUNTAIN
                                                    && this.movementCounter >= unstuckTicksThreshold)
                                                    || this.action.getName() != UniversityAction.Name.GO_THROUGH_SCANNER && this.action.getName() != UniversityAction.Name.QUEUE_VENDOR
                                                    && this.action.getName() != UniversityAction.Name.QUEUE_FOUNTAIN
                                                    && this.newPatchesSeenCounter >= unstuckTicksThreshold/*
                                           || this.agentFollowedWhenAssembling != null*/
                                    )
                            )
                    ) {
                        this.isReadyToFree = true;
                    }

                    this.timeSinceLeftPreviousGoal++;

                    // Check if the agent has slowed down since the last tick
                    // If it did, reset the time spent accelerating counter
                    if (this.currentWalkingDistance < previousWalkingDistance) {
                        this.ticksAcceleratedOrMaintainedSpeed = 0;
                    } else {
                        this.ticksAcceleratedOrMaintainedSpeed++;
                    }

                    return true;
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null; // If it reaches this point,
        // there is no movement to be made

        this.stop();

        this.distanceMovedInTick = 0.0; // There was no movement by this agent, so increment the pertinent counter
        this.noMovementCounter++;
        this.movementCounter = 0;
        this.timeSinceLeftPreviousGoal++;
        this.ticksAcceleratedOrMaintainedSpeed = 0;

        return false;
    }

    //TODO add function to negate forces if agent is in queue

    /*public UniversityAgent getNearestAgentOnFirstStepPosition() {
        Patch firstStepPosition = this.university.getPatch(this.computeFirstStepPosition());

        UniversityAgent nearestAgent = null;
        double nearestDistance = Double.MAX_VALUE;

        for (UniversityAgent agent : firstStepPosition.getAgents()) {
            UniversityAgent universityAgent = (UniversityAgent) agent;
            double distanceFromAgent = Coordinates.distance(this.position, universityAgent.getAgentMovement().getPosition());

            if (distanceFromAgent < nearestDistance) {
                nearestAgent = agent;
                nearestDistance = distanceFromAgent;
            }
        }

        return nearestAgent;
    }*/

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
        // and
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

    private Vector computeSocialForceFromAgent(UniversityAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at that minimum
        double modifiedDistanceToObstacle = Math.max(distanceToOtherAgent, minimumDistance);
        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        if (this.isStuck) { // If a agent is stuck, do not exert much force from this agent
            final double factor = 0.05;

            repulsionMagnitude -= this.stuckCounter * factor;

            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        // Then compute the heading from that other agent to this agent
        double headingFromOtherAgent = Coordinates.headingTowards(agentPosition, this.position);

        // Then compute for a future position given the other agent's position, the heading, and the magnitude; This will be used as the endpoint of the repulsion vector from this obstacle
        Coordinates agentRepulsionVectorFuturePosition = this.getFuturePosition(agentPosition, headingFromOtherAgent, repulsionMagnitude);

        // Finally, given the current position, heading, and future position, create the vector from the other agent to the current agent
        return new Vector(agentPosition, headingFromOtherAgent, agentRepulsionVectorFuturePosition, repulsionMagnitude);
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

        // If an agent is stuck, do not exert much force from this obstacle
        if (this.isStuck) {
            final double factor = 0.05;

            repulsionMagnitude -= this.stuckCounter * factor;

            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        // Compute the heading from that origin point to this agent
        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position);

        // Then compute for a future position given the obstacle's position, the heading, and the magnitude
        // This will be used as the endpoint of the repulsion vector from this obstacle
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude);

        // Finally, given the current position, heading, and future position, create the vector from the obstacle to the current agent
        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude);
    }

    private Vector computeSocialForceFromObstacle(Patch wallPatch, final double distanceToObstacle, final double maximumDistance, double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates repulsionVectorStartingPosition = wallPatch.getPatchCenterCoordinates();

        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at that minimum
        double modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);

        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);

        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        // If an agent is stuck, do not exert much force from this obstacle
        if (this.isStuck) {
            final double factor = 0.05;

            repulsionMagnitude -= this.stuckCounter * factor;

            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        // Compute the heading from that origin point to this agent
        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position);

        // Then compute for a future position given the obstacle's position, the heading, and the magnitude
        // This will be used as the endpoint of the repulsion vector from this obstacle
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude);

        // Finally, given the current position, heading, and future position, create the vector from the obstacle to the current agent
        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude);
    }

    private void move(double walkingDistance) { // Make the agent move given a walking distance
        this.setPosition(this.getFuturePosition(walkingDistance));
    }

    private void move(Coordinates futurePosition) { // Make the agent move given the future position
        this.setPosition(futurePosition);
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
    }

    public void stop() { // Have the agent stop
        this.currentWalkingDistance = 0.0;
    }

    public void leaveQueue() { // Unregister this agent to its queueable goal patch field's queue
        this.goalQueueingPatchField.getQueueingAgents().remove(this.parent);
    }

    public void beginWaitingOnAmenity() { // Have this agent start waiting for an amenity to become vacant
        this.isWaitingOnAmenity = true;
    }

    public boolean isQueueableGoalFree() { // Check if the goal of this agent is currently not servicing anyone
        return this.getGoalAmenityAsQueueableGoal().getAttractors().get(0).getPatch().getQueueingPatchField().getKey().getCurrentAgent() == null && this.getGoalAmenityAsQueueableGoal().getAttractors().get(0).getPatch().getAgents().isEmpty();
    }

    public boolean isServicedByQueueableGoal() { // Check if this agent the one currently served by its goal
        UniversityAgent agentServiced = (UniversityAgent) this.goalQueueingPatchField.getCurrentAgent();

        return agentServiced != null && agentServiced.equals(this.parent);
    }

    public void endWaitingOnAmenity() { // Have this agent stop waiting for an amenity to become vacant
        this.isWaitingOnAmenity = false;
    }

    // Check if this agent has reached its goal
    public boolean hasReachedGoal() {
        if (this.isWaitingOnAmenity) { // If the agent is still waiting for an amenity to be vacant, it hasn't reached the goal yet
            return false;
        }

        return isOnOrCloseToPatch(this.goalAttractor.getPatch());
    }

    public void reachGoal() { // Set the agent's current amenity and position as it reaches the next goal
        // Just in case the agent isn't actually on its goal, but is adequately close to it, just move the agent there
        // Make sure to offset the agent from the center a little so a force will be applied to this agent
        Coordinates patchCenter = this.goalAttractor.getPatch().getPatchCenterCoordinates();
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
        } while (!this.currentPath.getPath().isEmpty() && nextPatch.getAmenityBlocksAround() == 0 && this.isOnOrCloseToPatch(nextPatch)
                && this.hasClearLineOfSight(this.position, nextPatch.getPatchCenterCoordinates(), true));
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

    public boolean hasReachedFinalGoal() { // Check if this agent has reached its final goal
        return !this.routePlan.getCurrentRoutePlan().hasNext();
    }

    public boolean hasAgentReachedFinalPatchInPath() { // Check if this agent has reached the final patch in its current path
        return this.currentPath.getPath().isEmpty();
    }

    private boolean isOnPatch(Patch patch) { // Check if this agent has reached the specified patch
        return ((int) (this.position.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getColumn() && ((int) (this.position.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getRow();
    }

    // Check if this agent is adequately close enough to a patch; In this case, a agent is close enough to a patch when the distance between this agent and the patch is less than the distance covered by the agent per second
    private boolean isOnOrCloseToPatch(Patch patch) {
        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
    }


    public void despawn() { // Despawn this agent
        if (this.currentPatch != null) {
            this.currentPatch.getAgents().remove(this.parent);
            this.getUniversity().getAgents().remove(this.parent);

            SortedSet<Patch> currentPatchSet = this.getUniversity().getAgentPatchSet();

            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
                currentPatchSet.remove(this.currentPatch);
            }
        }
    }

    public void faceNextPosition() { // Have the agent face its current goal, or its queueing area, or the agent at the end of the queue
        double newHeading;
        Patch proposedGoalPatch = this.goalAttractor.getPatch();

        // Compute the heading towards the goal's attractor
        newHeading = Coordinates.headingTowards(this.position, this.goalAttractor.getPatch().getPatchCenterCoordinates());

        if (this.willPathfind) {
            // Get the heading towards the goal patch, which was set as the next patch in the path
            newHeading = Coordinates.headingTowards(this.position, this.goalPatch.getPatchCenterCoordinates());
        } else {
            this.goalPatch = proposedGoalPatch;
        }

        this.proposedHeading = newHeading; // Then set the agent's proposed heading to it
    }

    // If the agent is following a path, have the agent face the next one, if any
    public boolean chooseNextPatchInPath() {
        // Generate a path, if one hasn't been generated yet
        boolean wasPathJustGenerated = false;

        final int recomputeThreshold = 10;

        if (
                this.currentPath == null
                        || this.isStuck
                        && this.noNewPatchesSeenCounter > recomputeThreshold
        ) {
            AgentPath agentPath;

            if (this.getGoalAmenityAsQueueable() != null) {
                // Head towards the queue of the goal
                LinkedList<UniversityAgent> agentsQueueing
                        = this.goalQueueObject.getAgentsQueueing();

                // If there are no agents in that queue at all, simply head for the goal patch
                if (agentsQueueing.isEmpty()) {
                    agentPath = computePathWithinFloor(
                            this.currentPatch,
                            this.goalPatch,
                            true,
                            true,
                            false
                    );
                } else {
                    // If there are agents in the queue, this agent should only follow the last agent in
                    // that queue if that agent is assembling
                    // If the last agent is not assembling, simply head for the goal patch instead
                    UniversityAgent lastAgent = agentsQueueing.getLast();

                    if (
                            !(this.getGoalAmenityAsQueueable() instanceof TrainDoor)
                                    && !(this.getGoalAmenityAsQueueable() instanceof Turnstile)
                                    && lastAgent.getAgentMovement().getUniversityAction() == UniversityAction.ASSEMBLING
                    ) {
                        double distanceToGoalPatch = Coordinates.distance(
                                this.currentFloor.getStation(),
                                this.currentPatch,
                                this.goalPatch
                        );

                        double distanceToLastAgent = Coordinates.distance(
                                this.currentFloor.getStation(),
                                this.currentPatch,
                                lastAgent.getAgentMovement().getCurrentPatch()
                        );

                        // Head to whichever is closer to this agent, the last agent, or the nearest queueing
                        // path
                        if (distanceToGoalPatch <= distanceToLastAgent) {
                            agentPath = computePathWithinFloor(
                                    this.currentPatch,
                                    this.goalPatch,
                                    true,
                                    true,
                                    false
                            );
                        } else {
                            agentPath = computePathWithinFloor(
                                    this.currentPatch,
                                    lastAgent.getAgentMovement().getCurrentPatch(),
                                    true,
                                    true,
                                    false
                            );
                        }
                    } else {
                        agentPath = computePathWithinFloor(
                                this.currentPatch,
                                this.goalPatch,
                                true,
                                true,
                                false
                        );
                    }
                }
            } else {
                agentPath = computePathWithinFloor(
                        this.currentPatch,
                        this.goalPatch,
                        true,
                        false,
                        false
                );
            }

            if (agentPath != null) {
                // Create a copy of the object, to avoid using up the path directly from the cache
                this.currentPath = new AgentPath(agentPath);

                wasPathJustGenerated = true;
            }
        }

        // Get the first patch still unvisited in the path
        if (this.currentPath == null || this.currentPath.getPath().isEmpty()) {
            return false;
        }

        // If a path was just generated, determine the first patch to visit
        if (wasPathJustGenerated) {
            Patch nextPatchInPath;

            while (true) {
                nextPatchInPath = this.currentPath.getPath().peek();

                if (
                        !(
                                this.currentPath.getPath().size() > 1
                                        && nextPatchInPath.getAmenityBlocksAround() == 0
                                        && this.isOnOrCloseToPatch(nextPatchInPath)
                                        && this.hasClearLineOfSight(
                                        this.position,
                                        nextPatchInPath.getPatchCenterCoordinates(),
                                        true
                                )
                        )
                ) {
                    break;
                }

                this.currentPath.getPath().pop();
            }

            this.goalPatch = nextPatchInPath;
        } else {
            this.goalPatch = this.currentPath.getPath().peek();
        }

        return true;
    }

    public void free() { // Make this agent free from being stuck
        this.isStuck = false;

        this.stuckCounter = 0;
        this.noMovementCounter = 0;
        this.noNewPatchesSeenCounter = 0;

        this.currentPath = null;
        this.isReadyToFree = false;
    }

    // TODO Change implementation based on the queues for cafeteria etc
    private Patch computeBestQueueingPatchWeighted(List<Patch> PatchFieldList) {
        // Collect the patches with the highest floor field values
        List<Patch> PatchFieldCandidates = new ArrayList<>();
        List<Double> PatchFieldValueCandidates = new ArrayList<>();

        double valueSum = 0.0;

        for (Patch patch : PatchFieldList) {
            Map<QueueingPatchField.PatchFieldUniversityState, Double> PatchFieldUniversityStateDoubleMap
                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());

            if (
                    !patch.getPatchFieldValues().isEmpty()
                            && PatchFieldUniversityStateDoubleMap != null
                            && !PatchFieldUniversityStateDoubleMap.isEmpty()
                            && PatchFieldUniversityStateDoubleMap.get(
                            this.goalQueueingPatchFieldUniversityState
                    ) != null
            ) {
                double futurePatchFieldValue = patch.getPatchFieldValues()
                        .get(this.getGoalAmenityAsQueueable())
                        .get(this.goalQueueingPatchFieldUniversityState);

//                if (currentPatchFieldValue == null) {
                valueSum += futurePatchFieldValue;

                PatchFieldCandidates.add(patch);
                PatchFieldValueCandidates.add(futurePatchFieldValue);
//                }
            }
        }

        // If it gets to this point without finding a floor field value greater than zero, return early
        if (PatchFieldCandidates.isEmpty()) {
            return null;
        }

        Patch chosenPatch;
        int choiceIndex = 0;

        // Use the floor field values as weights to choose among patches
        for (
                double randomNumber = Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * valueSum;
                choiceIndex < PatchFieldValueCandidates.size() - 1;
                choiceIndex++) {
            randomNumber -= PatchFieldValueCandidates.get(choiceIndex);

            if (randomNumber <= 0.0) {
                break;
            }
        }

        chosenPatch = PatchFieldCandidates.get(choiceIndex);
        return chosenPatch;
    }

    // Get the best queueing patch around the current patch of another agent given the current floor field state
    // TODO Change implementation based on the queues for cafeteria etc
    private Patch getBestQueueingPatchAroundAgent(UniversityAgent otherAgent) {
        // Get the other agent's patch
        Patch otherAgentPatch = otherAgent.getAgentMovement().getCurrentPatch();

        // Get the neighboring patches of that patch
        List<Patch> neighboringPatches = otherAgentPatch.getNeighbors();

        // Remove the patch containing this agent
        neighboringPatches.remove(this.currentPatch);

        // Only add patches with the fewest agents
        List<Patch> neighboringPatchesWithFewestAgents = new ArrayList<>();
        int minimumAgentCount = Integer.MAX_VALUE;

        for (Patch neighboringPatch : neighboringPatches) {
            int neighboringPatchAgentCount = neighboringPatch.getAgents().size();

            if (neighboringPatchAgentCount < minimumAgentCount) {
                neighboringPatchesWithFewestAgents.clear();

                minimumAgentCount = neighboringPatchAgentCount;
            }

            if (neighboringPatchAgentCount == minimumAgentCount) {
                neighboringPatchesWithFewestAgents.add(neighboringPatch);
            }
        }

        // Choose a floor field patch from this
        Patch chosenPatch = this.computeBestQueueingPatchWeighted(neighboringPatchesWithFewestAgents);

        return chosenPatch;
    }

    // Check if the given patch has an obstacle
    private boolean hasObstacle(Patch patch, Amenity amenity) {
        // If there is literally no patch there, then there is no obstacle
        if (!amenity.getAmenityBlocks().contains(patch.getAmenityBlock()) || (patch.getPatchField() != null &&
                patch.getPatchField().getKey().getClass() != Wall.class)) {
            return true;
        }
        return false;
    }

    // Check if there is a clear line of sight from one point to another
    private boolean hasClearLineOfSight(Coordinates sourceCoordinates, Coordinates targetCoordinates, boolean includeStartingPatch) {
        if (hasObstacle(this.university.getPatch(targetCoordinates), null)) {
            return false;
        }

        final double resolution = 0.2;
        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.university.getPatch(sourceCoordinates);
        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        while (distanceCovered <= distanceToTargetCoordinates) { // Keep looking for blocks while there is still distance to cover
            if (includeStartingPatch || !this.university.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.university.getPatch(currentPosition), null)) {
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

    public int getDuration() {
        return duration;
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

    public void decrementDuration(){
        this.duration = getDuration() - 1;
    }

}