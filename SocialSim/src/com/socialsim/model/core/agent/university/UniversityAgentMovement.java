package com.socialsim.model.core.agent.university;

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
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Door;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchfield.Classroom;
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
    private boolean isReadyToExit; // Denotes whether this agent is ready to exit the environment immediately
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
//            this.state = State.GOING_TO_SECURITY;
//            this.stateIndex = 0;
//            this.action = Action.STANDING;
        }
        else {
            // TODO: Set initial states and actions if necessary
//            this.state = State.GOING_TO_SECURITY;
//            this.stateIndex = 0;
//            this.action = Action.WILL_QUEUE;
            this.currentAmenity = university.getUniversityGates().get(1); // Getting Entrance Gate
        }

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        this.isReadyToExit = false; // This agent will not exit yet
        resetGoal(false); // Set the agent goal
    }

    public UniversityAgent getParent() {
        return parent;
    }

    public Coordinates getPosition() {
        return position;
    }

//    public void setPosition(Coordinates coordinates) {
//        final int timeElapsedExpiration = 10;
//        Patch previousPatch = this.currentPatch;
//        this.position.setX(coordinates.getX());
//        this.position.setY(coordinates.getY());
//
//        Patch newPatch = this.university.getPatch(new Coordinates(coordinates.getX(), coordinates.getY())); // Get the patch of the new position
//
//        if (!previousPatch.equals(newPatch)) { // If the new position is on a different patch, remove the agent from its old patch, then add it to the new patch
//            previousPatch.getAgents().remove(this.parent);
//            newPatch.getAgents().add(this.parent);
//
//            SortedSet<Patch> previousPatchSet = previousPatch.getEnvironment().getAgentPatchSet();
//            SortedSet<Patch> newPatchSet = newPatch.getEnvironment().getAgentPatchSet();
//
//            if (previousPatchSet.contains(previousPatch) && hasNoAgent(previousPatch)) {
//                previousPatchSet.remove(previousPatch);
//            }
//
//            newPatchSet.add(newPatch);
//            this.currentPatch = newPatch;
//            updateRecentPatches(this.currentPatch, timeElapsedExpiration); // Update the recent patch list
//        }
//        else {
//            updateRecentPatches(null, timeElapsedExpiration); // Update the recent patch list
//        }
//    }

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

    public UniversityState getState() {
        return state;
    }

    public void setState(UniversityState state) {
        this.state = state;
    }

    public UniversityAction getAction() {
        return action;
    }

    public void setAction(UniversityAction action) {
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

    public boolean isReadyToExit() {
        return isReadyToExit;
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

//    public boolean isNextAmenityQueueable() { // Check whether the current goal amenity is a queueable or not
//        return Queueable.isQueueable(this.goalAmenity);
//    }

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
//        State nextItem = this.routePlan.getCurrentClass();
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

    public void chooseClassroom(int classID) {
        if (this.goalAmenity == null && (this.goalPatchField != null && this.goalPatchField.getClass() == Classroom.class)) {
            Amenity chosenAmenity = null;
            Amenity temp1 = null;
            Amenity temp2 = null;
            Amenity.AmenityBlock chosenAttractor = null;

            switch(classID) {
                case 1:
                    temp1 = this.university.getDoors().get(1);
                    temp2 = this.university.getDoors().get(2);
                    break;
                case 2:
                    temp1 = this.university.getDoors().get(3);
                    temp2 = this.university.getDoors().get(4);
                    break;
                case 3:
                    temp1 = this.university.getDoors().get(6);
                    temp2 = this.university.getDoors().get(7);
                    break;
                case 4:
                    temp1 = this.university.getDoors().get(8);
                    temp2 = this.university.getDoors().get(9);
                    break;
                case 5:
                    temp1 = this.university.getDoors().get(10);
                    temp2 = this.university.getDoors().get(11);
                    break;
                case 6:
                    temp1 = this.university.getDoors().get(12);
                    temp2 = this.university.getDoors().get(13);
                    break;
                case 7: // laboratory
                    temp1 = this.university.getDoors().get(14);
                    break;
            }

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity.AmenityBlock attractor : temp1.getAttractors()) { // Compute the distance to each attractor
                double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                distancesToAttractors.put(attractor, distanceToAttractor);
            }

            for (Amenity.AmenityBlock attractor : temp2.getAttractors()) { // Compute the distance to each attractor
                double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                distancesToAttractors.put(attractor, distanceToAttractor);
            }

            // Sort amenity by distance, from nearest to furthest
            LinkedHashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<>();
            sortedDistances.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> distancesToAttractors.put(x.getKey(), x.getValue()));

            // Look for a vacant amenity
            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();

                if(candidateAttractor.getPatch().getAgents() == null){ // Break when first vacant amenity is found
                    chosenAmenity =  candidateAttractor.getParent();
                    chosenAttractor = candidateAttractor;

                    break;
                }

            }

            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
            this.goalPatch = chosenAttractor.getPatch();
        }
    }

    // Set the nearest goal to this agent; That goal should also have the fewer agents queueing for it
    // To determine this, for each two agents in the queue (or fraction thereof), a penalty of one tile is added to the distance to this goal
    @SuppressWarnings("unchecked")
    public void chooseGoal(Class<? extends BaseObject> nextAmenityClass) {
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            // Get the amenity list in this university
            List<? extends Amenity> amenityListInFloor =
                    this.university.getAmenityList((Class<? extends Amenity>) nextAmenityClass);

            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                NonObstacle nonObstacle = ((NonObstacle) amenity);

                if (!nonObstacle.isEnabled()) { // Only consider enabled amenities
                    continue;
                }

                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                    double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                    distancesToAttractors.put(attractor, distanceToAttractor);
                }
            }

            // Sort amenity by distance, from nearest to furthest
            LinkedHashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<>();
            sortedDistances.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> distancesToAttractors.put(x.getKey(), x.getValue()));

            // Look for a vacant amenity
            // TODO take IOS into account
            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();

                //Break when first vacant amenity is found
                if(candidateAttractor.getPatch().getAgents() == null){
                    chosenAmenity =  candidateAttractor.getParent();
                    chosenAttractor = candidateAttractor;

                    break;
                }

            }
            //TODO logic when all amenities are in use
            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
            this.goalPatch = chosenAttractor.getPatch();
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
        TreeMap<Double, Class> wallsEncountered = new TreeMap<>();

        // This will contain the final motivation vector
        List<Vector> vectorsToAdd = new ArrayList<>();

        // Get the current heading, which will be the previous heading later
        this.previousHeading = this.heading;

        // Compute the proposed future position
        Coordinates proposedNewPosition;

        // Check if the agent is set to take one initial step forward
        if (!this.shouldStepForward) {
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
            if (
                    !willEnterTrain && this.state == UniversityState.IN_QUEUE || this.isWaitingOnPortal
            ) {

                if (/*UniversityAgent is not queueing*/) {
                    // Check if agent is stuck
                    if (
                        this.isStuck
                                || (
                                /*Check if there are no people in front the agent while in a queue*/
                        ) && this.noMovementCounter > noMovementTicksThreshold
                    ) {
                        this.isStuck = true;
                        this.stuckCounter++;
                    }
                }

                // Count agents within FOV
                TreeMap<Double, UniversityAgent> agentsWithinFieldOfView = new TreeMap<>();

                // Look around the patches that fall on the agent's field of view
                for (Patch patch : patchesToExplore) {
                    // If not in queue, count obstacles
                    if (this.action != UniversityAction.GO_THROUGH_SCANNER/*queueing actions*/) {
                        Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
                        Class aPatchField = patch.getPatchField().getKey().getClass();

                        // Get the distance between this agent and the obstacle on this patch
                        if (hasObstacle(patch)) {
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
                                    wallsEncountered.put(distanceToObstacle, aPatchField);
                                }
                            }

                        }
                    }

                    if (!this.isStuck) {
                        for (UniversityAgent otherAgent : patch.getAgents()) {
                            // Make sure that the agent discovered isn't itself
                            if (!otherAgent.equals(this.getParent())) {
                                if (allowRepulsionFrom(otherAgent)) {
                                    // Take note of the agent density in this area
                                    numberOfAgents++;

                                    // Check if this agent is within the field of view and within the slowdown
                                    // distance
                                    double distanceToAgent = Coordinates.distance(
                                            this.position,
                                            otherAgent.getAgentMovement().getPosition()
                                    );

                                    if (Coordinates.isWithinFieldOfView(
                                            this.position,
                                            otherAgent.getAgentMovement().getPosition(),
                                            this.proposedHeading,
                                            this.fieldOfViewAngle)
                                            && distanceToAgent <= slowdownStartDistance) {
                                        agentsWithinFieldOfView.put(distanceToAgent, otherAgent);
                                    }
                                }
                            }
                        }
                    }
                }

                // Compute the perceived density of the agents
                // Assuming the maximum density a agent sees within its environment is 3 before it thinks the crowd
                // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
                // maximum tolerated number of agents
                final double maximumDensityTolerated = 3.0;
                final double agentDensity
                        = (numberOfAgents > maximumDensityTolerated ? maximumDensityTolerated : numberOfAgents)
                        / maximumDensityTolerated;

                // For each agent found within the slowdown distance, get the nearest one, if there is any
                Map.Entry<Double, UniversityAgent> nearestAgentEntry = agentsWithinFieldOfView.firstEntry();

                // If there are no agents within the field of view, good - move normally
                if (nearestAgentEntry == null|| nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity() != null && !nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity().equals(this.goalAmenity)) {
                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;

                    // Get the attractive force of this agent to the new position
                    this.attractiveForce = this.computeAttractiveForce(
                            new Coordinates(this.position),
                            this.proposedHeading,
                            proposedNewPosition,
                            this.preferredWalkingDistance
                    );

                    vectorsToAdd.add(attractiveForce);
                } else {
                    // Get a random (but weighted) floor field value around the other agent
                    Patch PatchFieldPatch = this.getBestQueueingPatchAroundAgent(
                            nearestAgentEntry.getValue()
                    );
                    this.chosenQueueingPatch = PatchFieldPatch;

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

                    if (this.isNextAmenityTrainDoor() && PatchFieldPatch != null) {
                        Double PatchFieldValue = null;
                        Map<QueueingPatchField.PatchFieldState, Double> PatchFieldValues
                                = PatchFieldPatch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());

                        if (PatchFieldValues != null) {
                            PatchFieldValue = PatchFieldValues.get(this.goalQueueingPatchFieldState);
                        }

                        if (
                                PatchFieldValue != null
                                        && Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() < PatchFieldValue
                        ) {
                            this.shouldStopAtPlatform = true;
                        } else {
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
                                if (!this.getGoalAmenityAsTrainDoor().isOpen()) {
                                    // Get the heading towards that patch
                                    revisedHeading = Coordinates.headingTowards(
                                            this.position,
                                            PatchFieldPatch.getPatchCenterCoordinates()
                                    );
                                } else {
                                    revisedHeading = Coordinates.headingTowards(
                                            this.position,
                                            this.goalAttractor.getPatch().getPatchCenterCoordinates()
                                    );
                                }

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

                                for (
                                        Map.Entry<Double, UniversityAgent> otherAgentEntry
                                        : agentsWithinFieldOfView.entrySet()
                                ) {
                                    // Then compute the repulsive force from this agent
                                    // Compute the perceived density of the agents
                                    // Assuming the maximum density a agent sees within its environment is 5 before it thinks the crowd
                                    // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
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
                            }
                        }
                    } else {
                        Coordinates revisedPosition = this.getFuturePosition(computedWalkingDistance);

                        // Get the attractive force of this agent to the new position
                        this.attractiveForce = this.computeAttractiveForce(
                                new Coordinates(this.position),
                                this.proposedHeading,
                                revisedPosition,
                                computedWalkingDistance
                        );

                        vectorsToAdd.add(attractiveForce);
                    }
                }
            } else {
                // If the agent hasn't already been moving for a while, consider the agent stuck, and implement some
                // measures to free this agent
                if (
                        this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold
                ) {
                    this.isStuck = true;
                    this.stuckCounter++;
                }

                boolean hasEncounteredQueueingAgentInLoop = false;
                boolean hasEncounteredPortalWaitingAgentInLoop = false;

                // Only apply the social forces of a set number of agents and obstacles
                int agentsProcessed = 0;
                final int agentsProcessedLimit = 5;

                // Look around the patches that fall on the agent's field of view
                for (Patch patch : patchesToExplore) {
                    // If this patch has an obstacle, take note of it to add a repulsive force from it later
                    Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();

                    // Get the distance between this agent and the obstacle on this patch
                    if (hasObstacle(patch)) {
                        // Take note of the obstacle density in this area
                        numberOfObstacles++;

                        // Compute magnitude for repulsion force
                        double distanceToObstacle = Coordinates.distance(
                                this.position,
                                patchAmenityBlock.getPatch().getPatchCenterCoordinates()
                        );

                        if (distanceToObstacle <= slowdownStartDistance) {
                            obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
                        }
                    }

                    // Inspect each agent in each patch in the patches in the field of view
                    for (UniversityAgent otherAgent : patch.getAgents()) {
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
                                    otherAgent.getAgentMovement().getPosition()
                            );

                            // If the distance is less than or equal to the distance when repulsion is supposed to kick in,
                            // compute for the magnitude of that repulsion force
                            if (distanceToOtherAgent <= slowdownStartDistance) {
                                // Compute the perceived density of the agents
                                // Assuming the maximum density a agent sees within its environment is 3 before it thinks the crowd
                                // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
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
                                        otherAgent,
                                        distanceToOtherAgent,
                                        computedMaximumDistance,
                                        minimumAgentStopDistance,
                                        this.preferredWalkingDistance
                                );

                                // Add the computed vector to the list of vectors
                                this.repulsiveForceFromAgents.add(agentRepulsiveForce);

                                // Also, check this agent's state
                                // If this agent is queueing, set the relevant variable - it will stay true even if just
                                // one nearby agent has activated it
                                if (!hasEncounteredQueueingAgentInLoop) {
                                    // Check if the other agent is in a queueing or assembling with the same goal as
                                    // this agent
                                    if (this.agentFollowedWhenAssembling == null) {
                                        this.hasEncounteredAgentToFollow = false;
                                    } else {
                                        if (this.agentFollowedWhenAssembling.equals(otherAgent)) {
                                            // If the other agent encountered is already assembling, decide whether this
                                            // agent will assemble too depending on whether the other agent was selected
                                            // to be followed by this one
                                            this.hasEncounteredAgentToFollow
                                                    = (otherAgent.getAgentMovement().getAction() == UniversityAction.ASSEMBLING
                                                    || otherAgent.getAgentMovement().getAction() == UniversityAction.QUEUEING)
                                                    && otherAgent.getAgentMovement().getGoalAmenity().equals(this.goalAmenity);
                                        } else {
                                            this.hasEncounteredAgentToFollow = false;
                                        }
                                    }
                                }

                                hasEncounteredQueueingAgentInLoop
                                        = this.hasEncounteredAgentToFollow;

                                // Check if this agent has encountered a agent waiting for the same portal
                                if (!hasEncounteredPortalWaitingAgentInLoop) {
                                    // If the other agent encountered is already assembling, decide whether this
                                    // agent will assemble too depending on whether the other agent was selected
                                    // to be followed by this one
                                    this.hasEncounteredPortalWaitingAgent
                                            = otherAgent.getAgentMovement().isWaitingOnPortal()
                                            && otherAgent.getAgentMovement().getGoalAmenity().equals(this.goalAmenity);
                                }

                                hasEncounteredPortalWaitingAgentInLoop
                                        = this.hasEncounteredPortalWaitingAgent;

                                this.isWaitingOnPortal
                                        = this.isWaitingOnPortal || hasEncounteredPortalWaitingAgentInLoop;

                                // If a queueing agent has been encountered, do not pathfind anymore for this
                                // goal
                                if (
                                        this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
                                                && this.hasEncounteredAgentToFollow
                                ) {
                                    this.hasPathfound = true;
                                }

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
        }
        else {
            proposedNewPosition = this.computeFirstStepPosition();

            // Get the attractive force of this agent to the new position
            this.attractiveForce = this.computeAttractiveForce(new Coordinates(this.position),
                    Coordinates.headingTowards(this.position, proposedNewPosition), proposedNewPosition,
                    this.preferredWalkingDistance);
            vectorsToAdd.add(attractiveForce);
            this.shouldStepForward = false;
        }

        // Take note of the previous walking distance of this agent
        double previousWalkingDistance = this.currentWalkingDistance;

        vectorsToAdd.addAll(this.repulsiveForceFromAgents);

        // Then compute the partial motivation force of the agent
        Vector partialMotivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd);

        // If the resultant vector is null (i.e., no change in position), simply don't move at all
        if (!this.shouldStopAtPlatform && partialMotivationForce != null) {
            // The distance by which the repulsion starts to kick in will depend on the density of the agent's
            // surroundings
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

            // TODO add another one for walls encountered

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
                    if (this.state != UniversityState.IN_QUEUE) {
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
                                            this.state == UniversityState.IN_QUEUE
                                                    && this.movementCounter >= unstuckTicksThreshold
                                                    || this.state != UniversityState.IN_QUEUE
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

        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null; // If it reaches this point, there is no movement to be made

        this.stop();

        this.distanceMovedInTick = 0.0; // There was no movement by this agent, so increment the pertinent counter
        this.noMovementCounter++;
        this.movementCounter = 0;
        this.timeSinceLeftPreviousGoal++;
        this.ticksAcceleratedOrMaintainedSpeed = 0;

        return false;
    }

    private boolean allowRepulsionFrom(UniversityAgent otherAgent) { // Checks if another agent should apply a repulsive force on this agent, taking into account parameters other than this agent
        boolean isNotHeadingToQueueable = this.action != UniversityAction.HEADING_TO_QUEUEABLE;
        boolean isNotInOppositeStatesWithOtherAgent
                = !(this.state == UniversityState.IN_QUEUE && otherAgent.getAgentMovement().getState() != UniversityState.IN_QUEUE);

        boolean otherAgentComesBefore;

        if (this.state == UniversityState.IN_QUEUE) {
            Queueable queueable = this.getGoalAmenityAsQueueable();

            if (!(queueable instanceof TrainDoor) && !(queueable instanceof Turnstile)) {
                QueueObject queueObject = queueable.getQueueObject();
                List<UniversityAgent> queueingAgents = queueObject.getAgentsQueueing();

                if (queueingAgents.contains(this.parent) && queueingAgents.contains(otherAgent)) {
                    otherAgentComesBefore = otherAgent.getAgentMovement().comesBefore(this.parent);
                } else {
                    otherAgentComesBefore = true;
                }
            } else {
                otherAgentComesBefore = true;
            }
        } else {
            otherAgentComesBefore = true;
        }

        boolean otherAgentInSameGoal;

        Queueable thisAgentQueueable = this.getGoalAmenityAsQueueable();
        Queueable otherAgentQueueable = otherAgent.getAgentMovement().getGoalAmenityAsQueueable();

        if (thisAgentQueueable != null && otherAgentQueueable != null) {
            otherAgentInSameGoal = thisAgentQueueable.equals(otherAgentQueueable);
        } else {
            otherAgentInSameGoal = true;
        }

        return
                isNotHeadingToQueueable
                        && isNotInOppositeStatesWithOtherAgent
                        && otherAgentComesBefore
                        && otherAgentInSameGoal;
    }

    private double computeSecurityFirstStepHeading() {
        Security security = (Security) this.currentPatch.getAmenityBlock().getParent(); // First, get the apex of the floor field with the state of the agent

        return computeHeadingFromApexToAttractor(security.getSecurityPatchFieldState(), security.getQueueObject(), security.getAttractors());
    }

    private double computeHeadingFromApexToAttractor(QueueingPatchField.PatchFieldState PatchFieldState, QueueObject queueObject, List<Amenity.AmenityBlock> attractors) {
        Patch apexLocation;
        double newHeading;

        apexLocation = queueObject.getPatchFields().get(PatchFieldState).getApices().get(0);
        // Then compute the heading from the apex to the turnstile attractor
        newHeading = Coordinates.headingTowards(apexLocation.getPatchCenterCoordinates(), attractors.get(0).getPatch().getPatchCenterCoordinates());

        return newHeading;
    }

    private double computeFirstStepHeading() {
        double newHeading;

        if (this.currentPatch.getAmenityBlock() != null && this.currentPatch.getAmenityBlock().getParent() instanceof Security) {
            newHeading = computeSecurityFirstStepHeading();
        }
        else {
            newHeading = this.previousHeading;
        }

        return newHeading;
    }

    private Coordinates computeFirstStepPosition() {
        double newHeading = computeFirstStepHeading();

        return this.getFuturePosition(this.position, newHeading, this.preferredWalkingDistance); // Compute for the proposed future position
    }

    public UniversityAgent getNearestAgentOnFirstStepPosition() {
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
    }

    public boolean isFirstStepPositionFree() {
        return hasNoAgent(this.university.getPatch(this.computeFirstStepPosition()));
    }

    private boolean hasNoAgent(Patch patch) {
        if (patch == null) {
            return true;
        }

        List<UniversityAgent> agentsOnPatchWithoutThisAgent = patch.getAgents();
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

    public void joinQueue() { // Register this agent to its queueable goal's queue
        this.goalQueueObject.getAgentsQueueing().addLast(this.parent);
    }

    public void stop() { // Have the agent stop
        this.currentWalkingDistance = 0.0;
    }

    public void leaveQueue() { // Unregister this agent to its queueable goal's queue
        this.goalQueueObject.getAgentsQueueing().remove(this.parent);
    }

    public boolean hasReachedQueueingPatchFieldApex() { // Check if this agent has reached an apex of its floor field
        // If the agent is in any of this floor field's apices, return true
        for (Patch apex : this.goalQueueingPatchField.getApices()) {
            if (isOnOrCloseToPatch(apex)) {
                return true;
            }
        }

        return false;
    }

    public void beginWaitingOnAmenity() { // Have this agent start waiting for an amenity to become vacant
        this.isWaitingOnAmenity = true;
    }

    public boolean isQueueableGoalFree() { // Check if the goal of this agent is currently not servicing anyone
        return this.getGoalAmenityAsQueueableGoal().isFree(this.goalQueueObject) && this.goalQueueObject.getPatch().getAgents().isEmpty();
    }

    public boolean isServicedByQueueableGoal() { // Check if this agent the one currently served by its goal
        UniversityAgent agentServiced = this.goalQueueObject.getAgentServiced();

        return agentServiced != null && agentServiced.equals(this.parent);
    }

    public boolean isAtQueueFront() { // Check if this agent is at the front of the queue
        LinkedList<UniversityAgent> agentsQueueing = this.goalQueueObject.getAgentsQueueing();
        if (agentsQueueing.isEmpty()) {
            return false;
        }

        return agentsQueueing.getFirst() == this.parent;
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
        this.goalQueueObject.setAgentServiced(this.parent);
    }

    public void endServicingThisAgent() { // Have this agent's goal finish serving this agent
        this.goalQueueObject.setAgentServiced(null);

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

    // Check if this agent is adequately close enough to a patch
    // In this case, a agent is close enough to a patch when the distance between this agent and the patch is less than the distance covered by the agent per second
    private boolean isOnOrCloseToPatch(Patch patch) {
        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
    }

    public boolean isAllowedPass() { // Check if this agent is allowed by its goal to pass
        boolean allowPassAsGoal = this.getGoalAmenityAsQueueableGoal().allowPass();

        BlockableAmenity blockable = this.getGoalAmenityAsBlockable();

        if (blockable != null) {
            return allowPassAsGoal && !blockable.blockEntry();
        }
        else {
            return allowPassAsGoal;
        }
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
        boolean willFaceQueueingPatch;
        Patch proposedGoalPatch;

        // iI the agent is already heading for a queueable, no need to seek its floor fields again, as
        // it has already done so, and is now just heading to the goal itself
        // If it has floor fields, get the heading towards the nearest floor field value
        // If it doesn't have floor fields, just get the heading towards the goal itself
        if (
                this.action != UniversityAction.HEADING_TO_QUEUEABLE
                        && this.action != UniversityAction.HEADING_TO_TRAIN_DOOR
                        && this.goalAmenity instanceof Queueable
        ) {
            // If a queueing patch has not yet been set for this goal, set it
            if (this.goalNearestQueueingPatch == null) {
                // If the next floor field has not yet been set for this queueing patch, set it
                if (this.goalQueueingPatchFieldState == null && this.goalQueueingPatchField == null) {
                    Queueable queueable = this.getGoalAmenityAsQueueable();

                    if (queueable instanceof Turnstile) {
                        this.goalQueueingPatchField = queueable.retrievePatchField(
                                this.goalQueueObject,
                                this.goalQueueingPatchFieldState
                        );
                    } else {
                        this.goalQueueingPatchField = queueable.retrievePatchField(
                                queueable.getQueueObject(),
                                this.goalQueueingPatchFieldState
                        );
                    }
                }

                if (this.goalQueueingPatchField == null) {
                    this.getGoalAmenityAsQueueable().retrievePatchField(
                            this.goalQueueObject,
                            this.goalQueueingPatchFieldState
                    );
                }

                this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
                proposedGoalPatch = this.goalNearestQueueingPatch;
            }

            // If this agent is in the "will queue" state, choose between facing the queueing patch, and facing the
            // agent at the back of the queue
            if (action == UniversityAction.WILL_QUEUE || action == UniversityAction.ASSEMBLING) {
                LinkedList<UniversityAgent> agentQueue
                        = this.goalQueueObject.getAgentsQueueing();

                // Check whether there are agents queueing for the goal
                if (agentQueue.isEmpty()) {
                    // If there are no agents queueing yet, simply compute the heading towards the nearest queueing
                    // patch
                    this.agentFollowedWhenAssembling = null;
                    this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
                    proposedGoalPatch = this.goalNearestQueueingPatch;

                    willFaceQueueingPatch = true;
                } else {
                    if (this.isNextAmenityTrainDoor()) {
                        this.agentFollowedWhenAssembling = null;
                        this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
                        proposedGoalPatch = this.goalNearestQueueingPatch;

                        willFaceQueueingPatch = true;
                    } else {
                        UniversityAgent agentFollowedCandidate;

                        // If there are agents queueing, join the queue and follow either the last person in the queue
                        // or the person before this
                        if (action == UniversityAction.WILL_QUEUE) {
                            agentFollowedCandidate = agentQueue.getLast();
                        } else {
                            int agentFollowedCandidateIndex = agentQueue.indexOf(this.parent) - 1;

                            if (agentFollowedCandidateIndex >= 0) {
                                agentFollowedCandidate
                                        = agentQueue.get(agentFollowedCandidateIndex);
                            } else {
                                agentFollowedCandidate = null;
                            }
                        }

                        // But if the person to be followed is this person itself, or is not assembling, or follows this
                        // person too (forming a cycle), disregard it, and just follow the queueing patch
                        // Otherwise, follow that agent
                        if (
                                agentFollowedCandidate == null
                                        || agentFollowedCandidate.equals(this.parent)
                                        || !agentFollowedCandidate.equals(this.parent)
                                        && agentFollowedCandidate.getAgentMovement()
                                        .getAgentFollowedWhenAssembling() != null
                                        && agentFollowedCandidate.getAgentMovement()
                                        .getAgentFollowedWhenAssembling().equals(this.parent)
                        ) {
                            this.agentFollowedWhenAssembling = null;
                            this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
                            proposedGoalPatch = this.goalNearestQueueingPatch;

                            willFaceQueueingPatch = true;
                        } else {
                            // But only follow agents who are nearer to this agent than to the chosen queueing
                            // patch and are within this agent's walking distance and have a clear line of sight to
                            // this agent
                            final double distanceToAgentFollowedCandidate = Coordinates.distance(
                                    this.position,
                                    agentFollowedCandidate.getAgentMovement().getPosition()
                            );

                            final double agentFollowedRange = this.preferredWalkingDistance * 3.0;

                            if (
                                    !hasClearLineOfSight(this.position, agentFollowedCandidate.getAgentMovement().getPosition(), true)
                                            || distanceToAgentFollowedCandidate > agentFollowedRange
                            ) {
                                this.agentFollowedWhenAssembling = null;
                                this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
                                proposedGoalPatch = this.goalNearestQueueingPatch;

                                willFaceQueueingPatch = true;
                            } else {
                                this.agentFollowedWhenAssembling = agentFollowedCandidate;
                                proposedGoalPatch = this.goalNearestQueueingPatch;

                                willFaceQueueingPatch = false;

                            }
                        }
                    }
                }
            } else {
                this.agentFollowedWhenAssembling = null;
                proposedGoalPatch = this.goalNearestQueueingPatch;

                willFaceQueueingPatch = true;
            }

            if (willFaceQueueingPatch) {
                newHeading = Coordinates.headingTowards(
                        this.position,
                        this.goalNearestQueueingPatch.getPatchCenterCoordinates()
                );
            } else {
                // Get the distance from here to both the proposed agent followed and the nearest queueing
                // patch
                double distanceToAgent = Coordinates.distance(
                        this.position,
                        this.agentFollowedWhenAssembling.getAgentMovement().getPosition()
                );

                double distanceToQueueingPatch = Coordinates.distance(
                        this.position,
                        this.goalNearestQueueingPatch.getPatchCenterCoordinates()
                );

                // Head towards whoever is nearer
                if (distanceToAgent > distanceToQueueingPatch) {
                    newHeading = Coordinates.headingTowards(
                            this.position,
                            this.goalNearestQueueingPatch.getPatchCenterCoordinates()
                    );
                } else {
                    newHeading = Coordinates.headingTowards(
                            this.position,
                            this.agentFollowedWhenAssembling.getAgentMovement().getPosition()
                    );
                }
            }
        } else {
            proposedGoalPatch = this.goalAttractor.getPatch();

            // Compute the heading towards the goal's attractor
            newHeading = Coordinates.headingTowards(
                    this.position,
                    this.goalAttractor.getPatch().getPatchCenterCoordinates()
            );
        }
//        }

        if (this.willPathfind || this.action == UniversityAction.REROUTING) {
            // Get the heading towards the goal patch, which was set as the next patch in the path
            newHeading = Coordinates.headingTowards(
                    this.position,
                    this.goalPatch.getPatchCenterCoordinates()
            );

//            this.proposedHeading = newHeading;
        } else {
            this.goalPatch = proposedGoalPatch;
        }

        // Then set the agent's proposed heading to it
        this.proposedHeading = newHeading;
    }

    public void chooseBestQueueingPatch() { // While the agent is already on a floor field, have the agent face the one with the highest value
        this.goalNearestQueueingPatch = this.getBestQueueingPatch(); // Retrieve the patch with the highest floor field value around the agent's vicinity
        this.goalPatch = this.goalNearestQueueingPatch;
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
                                    && lastAgent.getAgentMovement().getAction() == UniversityAction.ASSEMBLING
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

    // From a set of patches associated with a goal's floor field, get the nearest patch below a threshold
    public Patch getPatchWithNearestPatchFieldValue() {
        final double maximumPatchFieldValueThreshold = 0.8;

        // Get the patches associated with the current goal
        List<Patch> associatedPatches = this.goalQueueingPatchField.getAssociatedPatches();

        double minimumDistance = Double.MAX_VALUE;
        Patch nearestPatch = null;

        // Look for the nearest patch from the patches associated with the floor field
        double distanceFromAgent;

        for (Patch patch : associatedPatches) {
            double PatchFieldValue
                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable()).get(this.goalQueueingPatchFieldState);

            if (PatchFieldValue <= maximumPatchFieldValueThreshold) {
            // Get the distance of that patch from this agent
            distanceFromAgent = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());

            if (distanceFromAgent < minimumDistance) {
                minimumDistance = distanceFromAgent;
                nearestPatch = patch;
            }
            }
        }

        return nearestPatch;
    }

    private Patch computeBestQueueingPatchWeighted(List<Patch> PatchFieldList) {
        // Collect the patches with the highest floor field values
        List<Patch> PatchFieldCandidates = new ArrayList<>();
        List<Double> PatchFieldValueCandidates = new ArrayList<>();

        double valueSum = 0.0;

        for (Patch patch : PatchFieldList) {
            Map<QueueingPatchField.PatchFieldState, Double> PatchFieldStateDoubleMap
                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());

            if (
                    !patch.getPatchFieldValues().isEmpty()
                            && PatchFieldStateDoubleMap != null
                            && !PatchFieldStateDoubleMap.isEmpty()
                            && PatchFieldStateDoubleMap.get(
                            this.goalQueueingPatchFieldState
                    ) != null
            ) {
                double futurePatchFieldValue = patch.getPatchFieldValues()
                        .get(this.getGoalAmenityAsQueueable())
                        .get(this.goalQueueingPatchFieldState);

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

    // Get the next queueing patch in a floor field given the current floor field state
    private Patch computeBestQueueingPatch(List<Patch> PatchFieldList) {
        // Collect the patches with the highest floor field values
        List<Patch> highestPatches = new ArrayList<>();

        double maximumPatchFieldValue = 0.0;

        for (Patch patch : PatchFieldList) {
            Map<QueueingPatchField.PatchFieldState, Double> PatchFieldStateDoubleMap
                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());

            if (
                    !patch.getPatchFieldValues().isEmpty()
                            && PatchFieldStateDoubleMap != null
                            && !PatchFieldStateDoubleMap.isEmpty()
                            && PatchFieldStateDoubleMap.get(
                            this.goalQueueingPatchFieldState
                    ) != null
            ) {
                double PatchFieldValue = patch.getPatchFieldValues()
                        .get(this.getGoalAmenityAsQueueable())
                        .get(this.goalQueueingPatchFieldState);

                if (PatchFieldValue >= maximumPatchFieldValue) {
                    if (PatchFieldValue > maximumPatchFieldValue) {
                        maximumPatchFieldValue = PatchFieldValue;

                        highestPatches.clear();
                    }

                    highestPatches.add(patch);
                }
            }
        }

        // If it gets to this point without finding a floor field value greater than zero, return early
        if (maximumPatchFieldValue == 0.0) {
            return null;
        }

        // If there are more than one highest valued-patches, choose the one where it would take the least heading
        // difference
        Patch chosenPatch /*= highestPatches.get(0)*/ = null;

        List<Double> headingChanges = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        double headingToHighestPatch;
        double headingChangeRequired;

        double distance;

        for (Patch patch : highestPatches) {
            headingToHighestPatch = Coordinates.headingTowards(this.position, patch.getPatchCenterCoordinates());
            headingChangeRequired = Coordinates.headingDifference(this.proposedHeading, headingToHighestPatch);

            double headingChangeRequiredDegrees = Math.toDegrees(headingChangeRequired);

            headingChanges.add(headingChangeRequiredDegrees);

            distance = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
            distances.add(distance);
        }

        double minimumHeadingChange = Double.MAX_VALUE;

        for (int index = 0; index < highestPatches.size(); index++) {
            double individualScore = headingChanges.get(index) * 1.0/* + (distances.get(index) * 10.0) * 0.5*/;

            if (individualScore < minimumHeadingChange) {
                minimumHeadingChange = individualScore;
                chosenPatch = highestPatches.get(index);
            }
        }

        return chosenPatch;
    }

    private Patch getBestQueueingPatch() {
        List<Patch> patchesToExplore = this.get7x7Field(this.proposedHeading, false, this.fieldOfViewAngle);

        return this.computeBestQueueingPatch(patchesToExplore);
    }

    // Get the best queueing patch around the current patch of another agent given the current floor field state
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
    private boolean hasObstacle(Patch patch) {
        // If there is literally no patch there, then there is no obstacle
        if (patch.getAmenityBlock() == null || (patch.getPatchField() != null &&
                patch.getPatchField().getKey().getClass() != Wall.class))  {
            return true;
        }

        Amenity.AmenityBlock amenityBlock = patch.getAmenityBlock();

        if (amenityBlock == null) {
            return false;
        } else {
            Amenity parent = amenityBlock.getParent();

            if (parent instanceof NonObstacle && ((NonObstacle) parent).isEnabled()) {
                if (parent.equals(this.goalAmenity)) {
                    if (parent instanceof Queueable) {
                        UniversityAgent agentServiced = this.goalQueueObject.getAgentServiced();

                        if (agentServiced != null && agentServiced.equals(this.parent)) {
                            if (amenityBlock instanceof Gate.GateBlock) {
                                Gate.GateBlock gateBlock = ((Gate.GateBlock) amenityBlock);

                                return !amenityBlock.isAttractor() && !gateBlock.isSpawner();
                            } else {
                                return !amenityBlock.isAttractor();
                            }
                        } else {
                            return true;
                        }
                    } else {
                        if (amenityBlock instanceof Gate.GateBlock) {
                            Gate.GateBlock gateBlock = ((Gate.GateBlock) amenityBlock);

                            return !amenityBlock.isAttractor() && !gateBlock.isSpawner();
                        } else {
                            return !amenityBlock.isAttractor();
                        }
                    }
                } else {
                    if (parent instanceof Gate) {
                        if (amenityBlock instanceof Gate.GateBlock) {
                            Gate.GateBlock gateBlock = ((Gate.GateBlock) amenityBlock);

                            return !amenityBlock.isAttractor() && !gateBlock.isSpawner();
                        } else {
                            return !amenityBlock.isAttractor();
                        }
                    } else {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
    }

    // Check if there is a clear line of sight from one point to another
    private boolean hasClearLineOfSight(Coordinates sourceCoordinates, Coordinates targetCoordinates, boolean includeStartingPatch) {
        // First of all, check if the target has an obstacle
        // If it does, then no need to check what is between the two points
        if (hasObstacle(this.currentFloor.getPatch(targetCoordinates))) {
            return false;
        }

        final double resolution = 0.2;

        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.currentFloor.getPatch(sourceCoordinates);

        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        // Keep looking for blocks while there is still distance to cover
        while (distanceCovered <= distanceToTargetCoordinates) {
            if (includeStartingPatch || !this.currentFloor.getPatch(currentPosition).equals(startingPatch)) {
                // Check if there is an obstacle in the current position
                // If there is, return early
                if (hasObstacle(this.currentFloor.getPatch(currentPosition))) {
                    return false;
                }
            }

            // If there isn't any, move towards the target coordinates with the given increment
            currentPosition = this.getFuturePosition(
                    currentPosition,
                    headingToTargetCoordinates,
                    resolution
            );

            distanceCovered += resolution;
        }

        // The target has been reached without finding an obstacle, so there is a clear line of sight between the two
        // given points
        return true;
    }

    private boolean comesBefore(UniversityAgent agent) { // Check if this agent comes before the given agent
        if (this.goalQueueObject != null) {
            List<UniversityAgent> goalQueue = this.goalQueueObject.getAgentsQueueing();

            if (goalQueue.size() >= 2) {
                int thisAgentIndex = goalQueue.indexOf(this.parent);
                int otherAgentIndex = goalQueue.indexOf(agent);

                if (thisAgentIndex != -1 && otherAgentIndex != -1) {
                    return thisAgentIndex < otherAgentIndex;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // Update the agent's recent patches
    private void updateRecentPatches(Patch currentPatch, final int timeElapsedExpiration) {
        List<Patch> patchesToForget = new ArrayList<>();

        // Update the time elapsed in all of the recent patches
        for (Map.Entry<Patch, Integer> recentPatchesAndTimeElapsed : this.recentPatches.entrySet()) {
            this.recentPatches.put(recentPatchesAndTimeElapsed.getKey(), recentPatchesAndTimeElapsed.getValue() + 1);

            // Remove all patches that are equal to the expiration time given
            if (recentPatchesAndTimeElapsed.getValue() == timeElapsedExpiration) {
                patchesToForget.add(recentPatchesAndTimeElapsed.getKey());
            }
        }

        // If there is a new patch to add or update to the recent patch list, do so
        if (currentPatch != null) {
            // The time lapsed value of any patch added or updated will always be zero, as it means this patch has been
            // recently encountered by this agent
            this.recentPatches.put(currentPatch, 0);
        }

        // Remove all patches set to be forgotten
        for (Patch patchToForget : patchesToForget) {
            this.recentPatches.remove(patchToForget);
        }
    }

    public void prepareForExit() {
        this.isReadyToExit = true;
    }

    public int getTickEntered(){
        return tickEntered;
    }

/*
    public enum State {
        GOING_TO_SECURITY, WANDERING_AROUND, NEEDS_BATHROOM, NEEDS_DRINK,
        GOING_TO_STUDY, STUDYING, GOING_TO_CLASS_STUDENT, GOING_TO_CLASS_PROFESSOR,
        IN_CLASS_STUDENT, IN_CLASS_PROFESSOR, GOING_TO_LUNCH, EATING_LUNCH,
        GOING_HOME, GUARD, MAINTENANCE_BATHROOM, MAINTENANCE_FOUNTAIN
    }

    public enum Action {
        */
/* Walking actions *//*

        WILL_QUEUE, REROUTING, EXITING,
        */
/* In queue actions *//*

        ASSEMBLING, QUEUEING, HEADING_TO_QUEUEABLE,
        */
/* In queueable actions *//*

        SECURITY_CHECKING, FOUNTAIN_DRINKING,
        */
/* Standing actions *//*

        STANDING
    }*/
}