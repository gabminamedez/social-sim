//package com.socialsim.model.core.agent.university;
//
//import com.socialsim.model.core.agent.Agent;
//import com.socialsim.model.core.agent.generic.pathfinding.AgentMovement;
//import com.socialsim.model.core.agent.generic.pathfinding.AgentPath;
//import com.socialsim.model.core.environment.generic.BaseObject;
//import com.socialsim.model.core.environment.generic.Patch;
//import com.socialsim.model.core.environment.generic.patchfield.headful.QueueObject;
//import com.socialsim.model.core.environment.generic.patchfield.headful.QueueingPatchField;
//import com.socialsim.model.core.environment.generic.patchobject.Amenity;
//import com.socialsim.model.core.environment.generic.patchobject.passable.Queueable;
//import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
//import com.socialsim.model.core.environment.generic.patchobject.passable.goal.BlockableAmenity;
//import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
//import com.socialsim.model.core.environment.generic.position.Coordinates;
//import com.socialsim.model.core.environment.generic.position.Vector;
//import com.socialsim.model.core.environment.university.University;
//import com.socialsim.model.core.environment.university.patchobject.passable.goal.Security;
//import com.socialsim.model.simulator.Simulator;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class UniversityAgentMovement extends AgentMovement {
//
//    private final Agent parent;
//    private final Coordinates position; // Denotes the position of the agent
//    private final University university;
//    private final double baseWalkingDistance; // Denotes the distance (m) the agent walks in one second
//    private double preferredWalkingDistance;
//    private double currentWalkingDistance;
//    private double proposedHeading;// Denotes the proposed heading of the agent in degrees where E = 0 degrees, N = 90 degrees, W = 180 degrees, S = 270 degrees
//    private double heading;
//    private double previousHeading;
//
//    private Patch currentPatch;
//    private Amenity currentAmenity;
//    private Patch goalPatch;
//    private Amenity goalAmenity;
//    private QueueObject goalQueueObject;
//    private Amenity.AmenityBlock goalAttractor;
//    private QueueingPatchField.PatchFieldState goalPatchFieldState; // Denotes the state of this agent's floor field
//    private QueueingPatchField goalPatchField; // Denotes the patch field of the agent goal
//    private Patch goalNearestQueueingPatch; // Denotes the patch with the nearest queueing patch
//
//    private UniversityRoutePlan routePlan;
//    private AgentPath currentPath; // Denotes the current path followed by this agent, if any
//    private State state;
//    private Action action; // Low-level description of what the agent is doing
//
//    private boolean isWaitingOnAmenity; // Denotes whether the agent is temporarily waiting on an amenity to be vacant
//    private boolean hasEncounteredAgentToFollow; // Denotes whether this agent has encountered the agent to be followed in the queue
//    private Agent agentFollowedWhenAssembling; // Denotes the agent this agent is currently following while assembling
//    private double distanceMovedInTick; // Denotes the distance moved by this agent in the previous tick
//    private int noMovementCounter; // Counts the ticks this agent moved a distance under a certain threshold
//    private int movementCounter; // Counts the ticks this agent has spent moving - this will reset when stopping
//    private int noNewPatchesSeenCounter; // Counts the ticks this agent has seen less than the defined number of patches
//    private int newPatchesSeenCounter; // Counts the ticks this agent has spent seeing new patches - this will reset otherwise
//    private boolean isStuck; // Denotes whether the agent is stuck
//    private int stuckCounter; // Counts the ticks this agent has spent being stuck - this will reset when a condition is reached
//    private int timeSinceLeftPreviousGoal; // Denotes the time since the agent left its previous goal
//    private final int ticksUntilFullyAccelerated; // Denotes the time until the agent accelerates fully from non-movement
//    private int ticksAcceleratedOrMaintainedSpeed; // Denotes the time the agent has spent accelerating or moving at a constant speed so far without slowing down or stopping
//    private final double fieldOfViewAngle; // Denotes the field of view angle of the agent
//    private boolean isReadyToFree; // Denotes whether the agent is ready to be freed from being stuck
//    private boolean willPathfind; // Denotes whether the agent is ready to pathfind
//    private boolean hasPathfound; // Denotes whether this agent has already pathfound
//    private boolean shouldStepForward; // Denotes whether this agent should take a step forward after it left its goal
//    private boolean isReadyToExit; // Denotes whether this agent is ready to exit the environment immediately
//    private final ConcurrentHashMap<Patch, Integer> recentPatches; // Denotes the recent patches this agent has been in
//
//    // The vectors of this agent
//    private final List<Vector> repulsiveForceFromAgents;
//    private final List<Vector> repulsiveForcesFromObstacles;
//    private Vector attractiveForce;
//    private Vector motivationForce;
//
//    public UniversityAgentMovement(Gate gate, Agent parent, double baseWalkingDistance, Coordinates coordinates) {
//        this.parent = parent;
//        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
//
//        final double interQuartileRange = 0.12; // The walking speed values shall be in m/s
//        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
//        this.preferredWalkingDistance = this.baseWalkingDistance;
//        this.currentWalkingDistance = preferredWalkingDistance;
//
//        // All newly generated agents will face the north by default
//        this.proposedHeading = Math.toRadians(90.0);
//        this.heading = Math.toRadians(90.0);
//        this.previousHeading = Math.toRadians(90.0);
//        this.fieldOfViewAngle = Math.toRadians(90.0);
//
//        // Add this agent to the start patch
//        this.currentPatch = gate.getAmenityBlocks().get(0).getPatch();
//        this.currentPatch.getAgents().add(parent);
//        this.university = (University) currentPatch.getEnvironment();
//
//        // Set the agent's time until it fully accelerates
//        this.ticksUntilFullyAccelerated = 10;
//        this.ticksAcceleratedOrMaintainedSpeed = 0;
//
//        this.currentAmenity = gate; // Take note of the amenity where this agent was spawned
//
//        this.routePlan = new UniversityRoutePlan((UniversityAgent) parent);
//        this.state = State.WALKING;
//        this.action = Action.WILL_QUEUE;
//
//        this.recentPatches = new ConcurrentHashMap<>();
//        repulsiveForceFromAgents = new ArrayList<>();
//        repulsiveForcesFromObstacles = new ArrayList<>();
//        this.isReadyToExit = false; // This agent will not exit yet
//        resetGoal(false); // Set the agent goal
//    }
//
//    public UniversityAgentMovement(Patch spawnPatch, Agent parent, double baseWalkingDistance, Coordinates coordinates) {
//        this.parent = parent;
//        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
//
//        final double interQuartileRange = 0.12; // The walking speed values shall be in m/s
//        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
//        this.preferredWalkingDistance = this.baseWalkingDistance;
//        this.currentWalkingDistance = preferredWalkingDistance;
//
//        // All newly generated agents will face the north by default
//        this.proposedHeading = Math.toRadians(90.0);
//        this.heading = Math.toRadians(90.0);
//        this.previousHeading = Math.toRadians(90.0);
//        this.fieldOfViewAngle = Math.toRadians(90.0);
//
//        // Add this agent to the start patch
//        this.currentPatch = spawnPatch;
//        this.currentPatch.getAgents().add(parent);
//        this.university = (University) currentPatch.getEnvironment();
//
//        // Set the agent's time until it fully accelerates
//        this.ticksUntilFullyAccelerated = 10;
//        this.ticksAcceleratedOrMaintainedSpeed = 0;
//
//        this.routePlan = new UniversityRoutePlan((UniversityAgent) parent);
//        this.state = State.WALKING;
//        this.action = Action.WILL_QUEUE;
//
//        this.recentPatches = new ConcurrentHashMap<>();
//
//        repulsiveForceFromAgents = new ArrayList<>();
//        repulsiveForcesFromObstacles = new ArrayList<>();
//        this.isReadyToExit = false; // This agent will not exit yet
//        resetGoal(false); // Set the agent goal
//    }
//
//    public Agent getParent() {
//        return parent;
//    }
//
//    public Coordinates getPosition() {
//        return position;
//    }
//
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
//
//    public University getUniversity() {
//        return university;
//    }
//
//    public double getCurrentWalkingDistance() {
//        return currentWalkingDistance;
//    }
//
//    public double getProposedHeading() {
//        return proposedHeading;
//    }
//
//    public double getHeading() {
//        return heading;
//    }
//
//    public Patch getCurrentPatch() {
//        return currentPatch;
//    }
//
//    public void setCurrentPatch(Patch currentPatch) {
//        this.currentPatch = currentPatch;
//    }
//
//    public Amenity getCurrentAmenity() {
//        return currentAmenity;
//    }
//
//    public Amenity.AmenityBlock getGoalAttractor() {
//        return goalAttractor;
//    }
//
//    public Patch getGoalPatch() {
//        return goalPatch;
//    }
//
//    public Amenity getGoalAmenity() {
//        return goalAmenity;
//    }
//
//    public QueueingPatchField.PatchFieldState getGoalPatchFieldState() {
//        return goalPatchFieldState;
//    }
//
//    public QueueingPatchField getGoalPatchField() {
//        return goalPatchField;
//    }
//
//    public Patch getGoalNearestQueueingPatch() {
//        return goalNearestQueueingPatch;
//    }
//
//    public UniversityRoutePlan getRoutePlan() {
//        return routePlan;
//    }
//
//    public void setRoutePlan(UniversityRoutePlan routePlan) {
//        this.routePlan = routePlan;
//    }
//
//    public AgentPath getCurrentPath() {
//        return currentPath;
//    }
//
//    public State getState() {
//        return state;
//    }
//
//    public void setState(State state) {
//        this.state = state;
//    }
//
//    public Action getAction() {
//        return action;
//    }
//
//    public void setAction(Action action) {
//        this.action = action;
//    }
//
//    public boolean isWaitingOnAmenity() {
//        return isWaitingOnAmenity;
//    }
//
//    public Agent getAgentFollowedWhenAssembling() {
//        return agentFollowedWhenAssembling;
//    }
//
//    public boolean hasEncounteredAgentToFollow() {
//        return hasEncounteredAgentToFollow;
//    }
//
//    public ConcurrentHashMap<Patch, Integer> getRecentPatches() {
//        return recentPatches;
//    }
//
//    public double getDistanceMovedInTick() {
//        return distanceMovedInTick;
//    }
//
//    public int getNoMovementCounter() {
//        return noMovementCounter;
//    }
//
//    public int getMovementCounter() {
//        return movementCounter;
//    }
//
//    public int getNoNewPatchesSeenCounter() {
//        return noNewPatchesSeenCounter;
//    }
//
//    public int getNewPatchesSeenCounter() {
//        return newPatchesSeenCounter;
//    }
//
//    public int getStuckCounter() {
//        return stuckCounter;
//    }
//
//    public int getTimeSinceLeftPreviousGoal() {
//        return timeSinceLeftPreviousGoal;
//    }
//
//    public boolean isStuck() {
//        return isStuck;
//    }
//
//    public boolean isReadyToFree() {
//        return isReadyToFree;
//    }
//
//    public boolean willPathFind() {
//        return willPathfind;
//    }
//
//    public boolean isReadyToExit() {
//        return isReadyToExit;
//    }
//
//    public List<Vector> getRepulsiveForceFromAgents() {
//        return repulsiveForceFromAgents;
//    }
//
//    public List<Vector> getRepulsiveForcesFromObstacles() {
//        return repulsiveForcesFromObstacles;
//    }
//
//    public Vector getAttractiveForce() {
//        return attractiveForce;
//    }
//
//    public Vector getMotivationForce() {
//        return motivationForce;
//    }
//
//    public Queueable getGoalAmenityAsQueueable() {
//        return Queueable.toQueueable(this.goalAmenity);
//    }
//
//    public Goal getGoalAmenityAsGoal() {
//        return Goal.toGoal(this.goalAmenity);
//    }
//
//    public BlockableAmenity getGoalAmenityAsBlockable() {
//        return BlockableAmenity.asBlockable(this.goalAmenity);
//    }
//
//    // Use the A* algorithm (with Euclidean distance to compute the f-score) to find the shortest path to the given goal patch
//    public static AgentPath computePathWithinFloor(Patch startingPatch, Patch goalPatch, boolean includeStartingPatch, boolean includeGoalPatch, boolean passThroughBlockables) {
//        HashSet<Patch> openSet = new HashSet<>();
//        HashMap<Patch, Double> gScores = new HashMap<>();
//        HashMap<Patch, Double> fScores = new HashMap<>();
//        HashMap<Patch, Patch> cameFrom = new HashMap<>();
//
//        for (Patch[] patchRow : startingPatch.getEnvironment().getPatches()) {
//            for (Patch patch : patchRow) {
//                gScores.put(patch, Double.MAX_VALUE);
//                fScores.put(patch, Double.MAX_VALUE);
//            }
//        }
//
//        gScores.put(startingPatch, 0.0);
//        fScores.put(startingPatch, Coordinates.distance(startingPatch, goalPatch));
//
//        openSet.add(startingPatch);
//        while (!openSet.isEmpty()) {
//            Patch patchToExplore;
//
//            double minimumDistance = Double.MAX_VALUE;
//            Patch patchWithMinimumDistance = null;
//
//            for (Patch patchInQueue : openSet) {
//                double fScore = fScores.get(patchInQueue);
//                if (fScore < minimumDistance) {
//                    minimumDistance = fScore;
//                    patchWithMinimumDistance = patchInQueue;
//                }
//            }
//
//            patchToExplore = patchWithMinimumDistance;
//            if (patchToExplore.equals(goalPatch)) {
//                Stack<Patch> path = new Stack<>();
//                double length = 0.0;
//
//                Patch currentPatch = goalPatch;
//
//                while (cameFrom.containsKey(currentPatch)) {
//                    Patch previousPatch = cameFrom.get(currentPatch);
//                    length += Coordinates.distance(previousPatch.getPatchCenterCoordinates(), currentPatch.getPatchCenterCoordinates());
//                    currentPatch = previousPatch;
//                    path.push(currentPatch);
//                }
//
//                AgentPath agentPath = new AgentPath(length, path);
//
//                return agentPath;
//            }
//
//            openSet.remove(patchToExplore);
//
//            List<Patch> patchToExploreNeighbors = patchToExplore.getNeighbors();
//
//            for (Patch patchToExploreNeighbor : patchToExploreNeighbors) {
//                if (patchToExploreNeighbor.getAmenityBlock() == null  || patchToExploreNeighbor.getAmenityBlock() != null
//                        && ((passThroughBlockables && patchToExploreNeighbor.getAmenityBlock().getParent() instanceof BlockableAmenity)
//                        || (!includeStartingPatch && patchToExplore.equals(startingPatch) || !includeGoalPatch && patchToExploreNeighbor.equals(goalPatch)))) {
//                    // Avoid patches that are close to amenity blocks, unless absolutely necessary
//                    double obstacleClosenessPenalty = patchToExploreNeighbor.getAmenityBlocksAround() * 2.0;
//                    double tentativeGScore = gScores.get(patchToExplore) + Coordinates.distance(patchToExplore, patchToExploreNeighbor) + obstacleClosenessPenalty;
//
//                    if (tentativeGScore < gScores.get(patchToExploreNeighbor)) {
//                        cameFrom.put(patchToExploreNeighbor, patchToExplore);
//                        gScores.put(patchToExploreNeighbor, tentativeGScore);
//                        fScores.put(patchToExploreNeighbor, gScores.get(patchToExploreNeighbor) + Coordinates.distance(patchToExploreNeighbor, goalPatch);
//                        openSet.add(patchToExploreNeighbor);
//                    }
//                }
//            }
//        }
//
//        return null;
//    }
//
//    public boolean isNextAmenityQueueable() { // Check whether the current goal amenity is a queueable or not
//        return Queueable.isQueueable(this.goalAmenity);
//    }
//
//    public boolean isNextAmenityGoal() { // Check whether the current goal amenity is a goal or not
//        return Goal.isGoal(this.goalAmenity);
//    }
//
//    public boolean hasJustLeftGoal() { // Check whether the agent has just left the goal (if the agent is at a certain number of ticks since leaving the goal)
//        final int hasJustLeftGoalThreshold = 3;
//
//        return this.timeSinceLeftPreviousGoal <= hasJustLeftGoalThreshold;
//    }
//
//    public void resetGoal(boolean shouldStepForwardFirst) { // Reset the agent's goal
//        this.goalPatch = null;
//        this.goalAmenity = null;
//        this.goalQueueObject = null;
//        this.goalAttractor = null;
//        this.goalPatchFieldState = null; // Take note of the patch field state of this agent
//        this.goalPatchField = null; // Take note of the patch field of the agent's goal
//        this.goalNearestQueueingPatch = null; // Take note of the agent's nearest queueing patch
//
//        this.hasEncounteredAgentToFollow = false; // No agents have been encountered yet
//        this.isWaitingOnAmenity = false; // This agent is not yet waiting
//        this.shouldStepForward = shouldStepForwardFirst;
//        this.agentFollowedWhenAssembling = null; // This agent is not following anyone yet
//
//        this.distanceMovedInTick = 0.0; // This agent hasn't moved yet
//        this.noMovementCounter = 0;
//        this.movementCounter = 0;
//        this.noNewPatchesSeenCounter = 0;
//        this.newPatchesSeenCounter = 0;
//        this.timeSinceLeftPreviousGoal = 0;
//
//        this.willPathfind = false;
//        this.hasPathfound = false;
//        this.recentPatches.clear(); // This agent has no recent patches yet
//        this.free(); // This agent is not yet stuck
//    }
//
//    // Set the nearest goal to this agent; That goal should also have the fewer agents queueing for it
//    // To determine this, for each two agents in the queue (or fraction thereof), a penalty of one tile is added to the distance to this goal
//    public void chooseGoal() {
//        if (this.goalAmenity == null) { // Only set the goal if one hasn't been set yet
//            // Set the next amenity class
//            Class<? extends BaseObject> nextAmenityClass = this.routePlan.getCurrentClass();
//
//            // Get the floors in this station which have the next amenity
//            Station currentStation = this.currentFloor.getStation();
//            Set<Floor> floorsWithNextAmenityClass = currentStation.getAmenityFloorIndex().get(nextAmenityClass);
//
//            // Get the amenity cluster where this agent came from
//            Station.AmenityCluster originAmenityCluster
//                    = currentStation.getAmenityClusterByAmenityAssorted().get(this.currentAmenity);
//
//            // Get the amenity list in this floor
//            List<? extends Amenity> amenityListInFloor = this.currentFloor.getAmenityList(nextAmenityClass);
//
//            // If this floor does not contain the next amenity class, consult the directory of each portal that serves
//            // this floor and see which portals should be entered to reach the floor with the desired amenity class
//            boolean willSeekPortal = false;
//
//            if (!floorsWithNextAmenityClass.contains(this.currentFloor)) {
//                willSeekPortal = true;
//            } else {
//                // If there are no goal portals to be followed, simply have the agent choose its goal
//                Amenity chosenAmenity = null;
//                QueueObject chosenQueueObject = null;
//                Amenity.AmenityBlock chosenAttractor = null;
//                TrainDoor.TrainDoorEntranceLocation chosenTrainDoorEntranceLocation = null;
//
//                // Compile all attractors from each amenity in the amenity list
//                HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
//
//                for (Amenity amenity : amenityListInFloor) {
//                    // Only consider amenities which are in the same assorted cluster as the amenity where the agent
//                    // came from
//                    Station.AmenityCluster amenityCluster
//                            = currentStation.getAmenityClusterByAmenityAssorted().get(amenity);
//
//                    if (amenityCluster.equals(originAmenityCluster)) {
//                        // Only considered enabled amenities
//                        NonObstacle nonObstacle = ((NonObstacle) amenity);
//
//                        // Only consider enabled amenities
//                        if (!nonObstacle.isEnabled()) {
//                            continue;
//                        }
//
//                        // Filter the amenity search space only to what is compatible with this agent
//                        if (amenity instanceof StationGate) {
//                            // If the goal of the agent is a station gate, this means the agent is leaving
//                            // So only consider station gates which allow exits and accepts the agent's direction
//                            StationGate stationGateExit = ((StationGate) amenity);
//
//                            if (stationGateExit.getStationGateMode() == StationGate.StationGateMode.ENTRANCE) {
//                                continue;
//                            } else {
//                                if (
//                                        !stationGateExit.getStationGateAgentTravelDirections().contains(
//                                                this.travelDirection
//                                        )
//                                ) {
//                                    continue;
//                                }
//                            }
//                        } else if (amenity instanceof TrainDoor) {
//                            // Only consider train doors which match this agent's travel direction
//                            TrainDoor trainDoor = ((TrainDoor) amenity);
//
//                            if (trainDoor.getPlatformDirection() != this.travelDirection) {
//                                continue;
//                            }
//
//                            // Also, if the train door has a female-only restriction, make sure this agent is also
//                            // female
//                            if (trainDoor.isFemaleOnly() && this.parent.getGender() != Agent.AgentInformation.Gender.FEMALE) {
//                                continue;
//                            }
//                        }
//
//                        // Compute the distance to each attractor
//                        for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
//                            double distanceToAttractor = Coordinates.distance(
//                                    currentStation,
//                                    this.currentPatch,
//                                    attractor.getPatch()
//                            );
//
//                            distancesToAttractors.put(attractor, distanceToAttractor);
//                        }
//                    }
//                }
//
//                double minimumAttractorScore = Double.MAX_VALUE;
//
//                // Then for each compiled amenity and their distance from this agent, see which has the smallest
//                // distance while taking into account the agents queueing for that amenity, if any
//                for (
//                        Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry
//                        : distancesToAttractors.entrySet()
//                ) {
//                    Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
//                    Double candidateDistance = distancesToAttractorEntry.getValue();
//
//                    Amenity currentAmenity;
//                    QueueObject currentQueueObject;
//                    TrainDoor.TrainDoorEntranceLocation currentTrainDoorEntranceLocation = null;
//
//                    List<QueueObject> turnstileQueueObjects = new ArrayList<>();
//
//                    currentAmenity = candidateAttractor.getParent();
//
//                    // Only collect queue objects from queueables
//                    if (currentAmenity instanceof Queueable) {
//                        if (currentAmenity instanceof Turnstile) {
//                            Turnstile turnstile = ((Turnstile) currentAmenity);
//
////                            currentQueueObject
////                                    = turnstile.getQueueObjects().get(this.disposition);
//
//                            turnstileQueueObjects.addAll(turnstile.getQueueObjects().values());
//
////                            turnstileQueueObjects = (List<QueueObject>) turnstile.getQueueObjects().values();
//                        } else if (currentAmenity instanceof TrainDoor) {
//                            TrainDoor trainDoor = ((TrainDoor) currentAmenity);
//
//                            currentTrainDoorEntranceLocation
//                                    = trainDoor.getTrainDoorEntranceLocationFromAttractor(candidateAttractor);
//                            currentQueueObject
//                                    = trainDoor.getQueueObjectFromTrainDoorEntranceLocation(
//                                    currentTrainDoorEntranceLocation
//                            );
//                        } else {
//                            Queueable queueable = ((Queueable) currentAmenity);
//
//                            currentQueueObject = queueable.getQueueObject();
//                        }
//                    } else {
//                        currentQueueObject = null;
//                    }
//
//                    // If this is a queueable, take into account the agents queueing (except if it is a security gate)
//                    // If this is not a queueable (or if it's a security gate), the distance will suffice
//                    double attractorScore;
//
//                    if (currentQueueObject != null) {
//                        if (!(currentAmenity instanceof Security)) {
//                            // Avoid queueing to long lines
//                            double agentPenalty = (currentAmenity instanceof TrainDoor) ? 50.0 : 25.0;
//
//                            if (currentAmenity instanceof Turnstile) {
//                                double agentsQueueingForTurnstile = 0.0;
//
//                                for (QueueObject queueObject : turnstileQueueObjects) {
//                                    agentsQueueingForTurnstile += queueObject.getAgentsQueueing().size();
//                                }
//
//                                double modifiedCandidateDistance = candidateDistance;
//
//                                final double candidateDistanceLimit = 15.0;
//                                final double distantCandidatePenalty = 100.0;
//
//                                if (modifiedCandidateDistance > candidateDistanceLimit) {
//                                    modifiedCandidateDistance = candidateDistance * distantCandidatePenalty;
//                                }
//
//                                attractorScore
//                                        = modifiedCandidateDistance
//                                        + agentsQueueingForTurnstile * agentPenalty;
//                            } else {
//                                attractorScore
//                                        = candidateDistance
//                                        + currentQueueObject.getAgentsQueueing().size() * agentPenalty;
//                            }
//                        } else {
//                            attractorScore = candidateDistance;
//                        }
//                    } else {
//                        attractorScore = candidateDistance;
//                    }
//
//                    if (attractorScore < minimumAttractorScore) {
//                        minimumAttractorScore = attractorScore;
//
//                        chosenAmenity = currentAmenity;
//                        chosenQueueObject = currentQueueObject;
//                        chosenAttractor = candidateAttractor;
//                        chosenTrainDoorEntranceLocation = currentTrainDoorEntranceLocation;
//                    }
//                }
//
//                // If no amenities in this floor were found to have a path from this agent, seek the portals instead
//                if (chosenAmenity == null) {
//                    willSeekPortal = true;
//                } else {
//                    // Set the goal nearest to this agent
//                    this.goalAmenity = chosenAmenity;
//                    this.goalQueueObject = chosenQueueObject;
//                    this.goalAttractor = chosenAttractor;
//                    this.goalPatch = chosenAttractor.getPatch();
//                    this.goalTrainDoorEntranceLocation = chosenTrainDoorEntranceLocation;
//                }
//            }
//
//            if (willSeekPortal) {
//                // Get the nearest relevant portal to this agent
//                Portal.Directory.DirectoryItem directoryItemOfAgent = new Portal.Directory.DirectoryItem(
//                        this.travelDirection,
//                        nextAmenityClass,
//                        currentStation.getAmenityClusterByAmenity().get(this.currentAmenity),
//                        null,
//                        0.0
//                );
//
//                TreeMap<Double, Portal> relevantPortals = new TreeMap<>();
//
//                // TODO: Consider other portals
//
//                // Compile the stair portals that serve this floor
//                List<StairShaft> stairShafts = currentStation.getStairShafts();
//
//                double distanceToPortal;
//                double distanceToTraverse;
//                double peopleInPortalScore;
//
//                double portalScore;
//
//                for (StairShaft stairShaft : stairShafts) {
//                    StairPortal lowerStairPortal = (StairPortal) stairShaft.getLowerPortal();
//                    StairPortal upperStairPortal = (StairPortal) stairShaft.getUpperPortal();
//
//                    // Only consider amenities which are in the same assorted cluster as the amenity where the agent
//                    // came from
//                    Station.AmenityCluster lowerAmenityCluster
//                            = currentStation.getAmenityClusterByAmenityAssorted().get(lowerStairPortal);
//
//                    Station.AmenityCluster upperAmenityCluster
//                            = currentStation.getAmenityClusterByAmenityAssorted().get(upperStairPortal);
//
//                    if (lowerStairPortal.getFloorServed().equals(currentFloor)) {
//                        if (lowerAmenityCluster.equals(originAmenityCluster)) {
//                            Portal.Directory.DirectoryItem directoryItemInPortal
//                                    = lowerStairPortal.getDirectory().get(directoryItemOfAgent);
//
//                            if (directoryItemInPortal != null) {
////                                // Only consider attractors in amenities which are accessible from the current position
////                                AgentPath path = computePathWithinFloor(
////                                        this.currentPatch,
////                                        lowerStairPortal.getAttractors().get(0).getPatch(),
////                                        true,
////                                        false
////                                );
////
////                                if (path != null) {
//                                // Other than merely considering the distance to the portal, also take into account the
//                                // distance it would take to reach the goal through the portal, as well as the number of
//                                // people already in that portal
//                                distanceToPortal = Coordinates.distance(
//                                        currentStation,
//                                        this.currentPatch,
//                                        lowerStairPortal.getAttractors().get(0).getPatch()
//                                );
//
//                                distanceToTraverse = directoryItemInPortal.getDistance();
//
//                                final double peopleInPortalPenalty = 10.0;
//                                peopleInPortalScore
//                                        = lowerStairPortal.getStairShaft().getAgentsAscending()
//                                        * peopleInPortalPenalty;
//
//                                portalScore = distanceToPortal + distanceToTraverse + peopleInPortalScore;
//
//                                relevantPortals.put(
//                                        portalScore,
//                                        lowerStairPortal
//                                );
//                            }
////                            }
//                        }
//                    }
//
//                    if (upperStairPortal.getFloorServed().equals(currentFloor)) {
//                        if (upperAmenityCluster.equals(originAmenityCluster)) {
//                            Portal.Directory.DirectoryItem directoryItemInPortal
//                                    = upperStairPortal.getDirectory().get(directoryItemOfAgent);
//
//                            if (directoryItemInPortal != null) {
////                                // Only consider attractors in amenities which are accessible from the current position
////                                AgentPath path = computePathWithinFloor(
////                                        this.currentPatch,
////                                        upperStairPortal.getAttractors().get(0).getPatch(),
////                                        true,
////                                        false
////                                );
////
////                                if (path != null) {
//                                // Other than merely considering the distance to the portal, also take into account the
//                                // distance it would take to reach the goal through the portal
//                                distanceToPortal = Coordinates.distance(
//                                        currentStation,
//                                        this.currentPatch,
//                                        upperStairPortal.getAttractors().get(0).getPatch()
//                                );
//
//                                distanceToTraverse = directoryItemInPortal.getDistance();
//
//                                final double peopleInPortalPenalty = 10.0;
//                                peopleInPortalScore
//                                        = upperStairPortal.getStairShaft().getAgentsDescending()
//                                        * peopleInPortalPenalty;
//
//                                portalScore = distanceToPortal + distanceToTraverse + peopleInPortalScore;
//
//                                relevantPortals.put(
//                                        portalScore,
//                                        upperStairPortal
//                                );
////                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private Coordinates getFuturePosition() {
//        return getFuturePosition(this.goalAmenity, this.proposedHeading, this.preferredWalkingDistance);
//    }
//
//    private Coordinates getFuturePosition(double walkingDistance) {
//        return getFuturePosition(this.goalAmenity, this.proposedHeading, walkingDistance);
//    }
//
//    public Coordinates getFuturePosition(Coordinates startingPosition, double heading, double magnitude) {
//        return Coordinates.computeFuturePosition(startingPosition, heading, magnitude);
//    }
//
//    public Coordinates getFuturePosition(Amenity goal, double heading, double walkingDistance) {
//        double minimumDistance = Double.MAX_VALUE; // Get the nearest attractor to this agent
//        double distance;
//
//        Amenity.AmenityBlock nearestAttractor = null;
//
//        for (Amenity.AmenityBlock attractor : goal.getAttractors()) {
//            distance = Coordinates.distance(this.position, attractor.getPatch().getPatchCenterCoordinates());
//
//            if (distance < minimumDistance) {
//                minimumDistance = distance;
//                nearestAttractor = attractor;
//            }
//        }
//
//        assert nearestAttractor != null;
//
//        // If distance between agent and goal is less than distance agent covers every time it walks, "snap" the position of agent to center of goal immediately to avoid overshooting its target
//        if (minimumDistance < walkingDistance) {
//            return new Coordinates(nearestAttractor.getPatch().getPatchCenterCoordinates().getX(), nearestAttractor.getPatch().getPatchCenterCoordinates().getY());
//        }
//        else { // If not, compute the next coordinates normally
//            Coordinates futurePosition = this.getFuturePosition(this.position, heading, walkingDistance);
//
//            double newX = futurePosition.getX();
//            double newY = futurePosition.getY();
//
//            // Check if the new coordinates are out of bounds; If they are, adjust them such that they stay within bounds
//            if (newX < 0) {
//                newX = 0.0;
//            }
//            else if (newX > 120 - 1) {
//                newX = 120 - 0.5;
//            }
//
//            if (newY < 0) {
//                newY = 0.0;
//            }
//            else if (newY > 60 - 1) {
//                newY = 60 - 0.5;
//            }
//
//            return new Coordinates(newX, newY);
//        }
//    }
//
//    public boolean moveSocialForce() { // Make the agent move in accordance with social forces
//        // The smallest repulsion an agent may inflict on another
//        final double minimumAgentRepulsion = 0.01 * this.preferredWalkingDistance;
//
//        // If the agent has not moved a sufficient distance for more than this number of ticks, the agent
//        // will be considered stuck
//        final int noMovementTicksThreshold = (this.getGoalAmenityAsGoal() != null) ? this.getGoalAmenityAsGoal().getWaitingTime() : 5;
//
//        // If the agent has not seen new patches for more than this number of ticks, the agent will be considered
//        // stuck
//        final int noNewPatchesSeenTicksThreshold = 5;
//
//        // If the agent has been moving a sufficient distance for at least this number of ticks, this agent will
//        // be out of the stuck state, if it was
//        final int unstuckTicksThreshold = 60;
//
//        // If the distance the agent moves per tick is less than this distance, this agent is considered to not
//        // have moved
//        final double noMovementThreshold = 0.01 * this.preferredWalkingDistance;
//
//        // If the size of the agent's memory of recent patches is less than this number, the agent is considered
//        // to not have moved
//        final double noNewPatchesSeenThreshold = 5;
//
//        // The distance to another agent before this agent slows down
//        final double slowdownStartDistance = 2.0;
//
//        // The minimum allowable distance from another agent at its front before this agent stops
//        final double minimumStopDistance = 0.6;
//
//        // The maximum allowable distance from another agent at its front before this agent stops
//        double maximumStopDistance = 1.0;
//
//        // Count the number of agents and obstacles in the the relevant patches
//        int numberOfAgents = 0;
//        int numberOfObstacles = 0;
//
//        // The distance from the agent's center by which repulsive effects from agents start to occur
//        double maximumAgentStopDistance = 1.0;
//
//        // The distance from the agent's center by which repulsive effects from agents are at a maximum
//        final double minimumAgentStopDistance = 0.6;
//
//        // The distance from the agent's center by which repulsive effects from obstacles start to occur
//        double maximumObstacleStopDistance = 1.0;
//
//        // The distance from the agent's center by which repulsive effects from obstacles are at a maximum
//        final double minimumObstacleStopDistance = 0.6;
//
//        List<Patch> patchesToExplore = this.get7x7Field(
//                this.proposedHeading,
//                true,
//                Math.toRadians(360.0)
//        );
//
////        this.toExplore = patchesToExplore;
//
//        // Clear vectors from the previous computations
//        this.repulsiveForceFromAgents.clear();
//        this.repulsiveForcesFromObstacles.clear();
//        this.attractiveForce = null;
//        this.motivationForce = null;
//
//        // Add the repulsive effects from nearby agents and obstacles
//        TreeMap<Double, Amenity.AmenityBlock> obstaclesEncountered = new TreeMap<>();
//
//        // This will contain the final motivation vector
//        List<Vector> vectorsToAdd = new ArrayList<>();
//
//        // Get the current heading, which will be the previous heading later
//        this.previousHeading = this.heading;
//
//        // Compute the proposed future position
//        Coordinates proposedNewPosition;
//
//        // Check if the agent is set to take one initial step forward
//        if (!this.shouldStepForward) {
//            // Compute for the proposed future position
//            proposedNewPosition = this.getFuturePosition(this.preferredWalkingDistance);
//
//            boolean willEnterTrain = this.isNextAmenityTrainDoor() && this.willEnterTrain();
//
//            if (willEnterTrain && this.shouldStopAtPlatform) {
//                this.shouldStopAtPlatform = false;
//            }
//
//            if (!this.shouldStopAtPlatform) {
//                // If the goal is a train door, and it is open, and the agent is not waiting yet, walk faster
//                if (
//                        this.getGoalAmenityAsTrainDoor() != null
//                                && this.getGoalAmenityAsTrainDoor().isOpen()
//                                && this.action != Action.WAITING_FOR_TRAIN
//                ) {
//                    final double speedIncreaseFactor = 1.25;
//
//                    this.preferredWalkingDistance = this.baseWalkingDistance * speedIncreaseFactor;
//                } else {
//                    // If the goal is not a train door, or it is, but it's closed, walk normally
//                    this.preferredWalkingDistance = this.baseWalkingDistance;
//                }
//
//                // If the agent is near its goal that is not a train door, and this agent has a clear line of
//                // sight to that goal, walk slower
//                final double distanceSlowdownStart = 5.0;
//                final double speedDecreaseFactor = 0.5;
//
//                double distanceToGoal = Coordinates.distance(
//                        this.currentFloor.getStation(),
//                        this.currentPatch,
//                        this.getGoalAmenity().getAttractors().get(0).getPatch()
//                );
//
//                if (
//                        this.getGoalAmenityAsTrainDoor() == null
//                                && distanceToGoal < distanceSlowdownStart
//                                && this.hasClearLineOfSight(
//                                this.position,
//                                this.goalAmenity.getAttractors().get(0).getPatch().getPatchCenterCoordinates(),
//                                true
//                        )
//                ) {
//                    this.preferredWalkingDistance *= speedDecreaseFactor;
//                }
//
//                // If this agent is queueing, the only social forces that apply are attractive forces to agents
//                // and obstacles (if not in queueing action)
//                if (
//                        !willEnterTrain && this.state == State.IN_QUEUE || this.isWaitingOnPortal
//                ) {
//                    // Do not check for stuckness when already heading to the queueable
//                    if (this.action != Action.HEADING_TO_QUEUEABLE && !this.isWaitingOnPortal) {
//                        // If the agent hasn't already been moving for a while, consider the agent stuck, and implement some
//                        // measures to free this agent
//                        if (
//                                this.isStuck
//                                        || (
//                                        this.action != Action.WAITING_FOR_TRAIN
//                                                && this.hasNoAgent(
//                                                this.goalAttractor.getPatch()
//                                        ) && (
//                                                this.isAtQueueFront() || this.isServicedByGoal()
//                                        )
//                                ) && this.noMovementCounter > noMovementTicksThreshold
//                        ) {
//                            this.isStuck = true;
//                            this.stuckCounter++;
//                        }
//                    }
//
//                    // Get the agents within the current field of view in these patches
//                    // If there are any other agents within this field of view, this agent is at least guaranteed to
//                    // slow down
//                    TreeMap<Double, Agent> agentsWithinFieldOfView = new TreeMap<>();
//
//                    // Look around the patches that fall on the agent's field of view
//                    for (Patch patch : patchesToExplore) {
//                        // Do not apply social forces from obstacles if the agent is in the queueing action, i.e., when the
//                        // agent is following a floor field
//                        // If this patch has an obstacle, take note of it to add a repulsive force from it later
//                        if (this.action != Action.QUEUEING) {
//                            Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
//
//                            // Get the distance between this agent and the obstacle on this patch
//                            if (hasObstacle(patch)) {
//                                // Take note of the obstacle density in this area
//                                numberOfObstacles++;
//
//                                // If the distance is less than or equal to the specified minimum repulsion distance, compute
//                                // for the magnitude of the repulsion force
//                                double distanceToObstacle = Coordinates.distance(
//                                        this.position,
//                                        patchAmenityBlock.getPatch().getPatchCenterCoordinates()
//                                );
//
//                                if (distanceToObstacle <= slowdownStartDistance) {
//                                    obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
//                                }
//                            }
//                        }
//
//                        if (!this.isStuck) {
//                            for (Agent otherAgent : patch.getAgents()) {
//                                // Make sure that the agent discovered isn't itself
//                                if (!otherAgent.equals(this.getParent())) {
//                                    if (allowRepulsionFrom(otherAgent)) {
//                                        // Take note of the agent density in this area
//                                        numberOfAgents++;
//
//                                        // Check if this agent is within the field of view and within the slowdown
//                                        // distance
//                                        double distanceToAgent = Coordinates.distance(
//                                                this.position,
//                                                otherAgent.getAgentMovement().getPosition()
//                                        );
//
//                                        if (Coordinates.isWithinFieldOfView(
//                                                this.position,
//                                                otherAgent.getAgentMovement().getPosition(),
//                                                this.proposedHeading,
//                                                this.fieldOfViewAngle)
//                                                && distanceToAgent <= slowdownStartDistance) {
//                                            agentsWithinFieldOfView.put(distanceToAgent, otherAgent);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    // Compute the perceived density of the agents
//                    // Assuming the maximum density a agent sees within its environment is 3 before it thinks the crowd
//                    // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
//                    // maximum tolerated number of agents
//                    final double maximumDensityTolerated = 3.0;
//                    final double agentDensity
//                            = (numberOfAgents > maximumDensityTolerated ? maximumDensityTolerated : numberOfAgents)
//                            / maximumDensityTolerated;
//
//                    // For each agent found within the slowdown distance, get the nearest one, if there is any
//                    Map.Entry<Double, Agent> nearestAgentEntry = agentsWithinFieldOfView.firstEntry();
//
//                    // If there are no agents within the field of view, good - move normally
//                    if (nearestAgentEntry == null/*|| nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity() != null && !nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity().equals(this.goalAmenity)*/) {
//                        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//                        // Get the attractive force of this agent to the new position
//                        this.attractiveForce = this.computeAttractiveForce(
//                                new Coordinates(this.position),
//                                this.proposedHeading,
//                                proposedNewPosition,
//                                this.preferredWalkingDistance
//                        );
//
//                        vectorsToAdd.add(attractiveForce);
//                    } else {
//                        // Get a random (but weighted) floor field value around the other agent
//                        Patch PatchFieldPatch = this.getBestQueueingPatchAroundAgent(
//                                nearestAgentEntry.getValue()
//                        );
////                        this.chosenQueueingPatch = PatchFieldPatch;
//
//                        // Check the distance of that nearest agent to this agent
//                        double distanceToNearestAgent = nearestAgentEntry.getKey();
//
//                        // Modify the maximum stopping distance depending on the density of the environment
//                        // That is, the denser the surroundings, the less space this agent will allow between other
//                        // agents
//                        maximumStopDistance -= (maximumStopDistance - minimumStopDistance) * agentDensity;
//
//                        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//                        // Else, just slow down and move towards the direction of that agent in front
//                        // The slowdown factor linearly depends on the distance between this agent and the other
//                        final double slowdownFactor
//                                = (distanceToNearestAgent - maximumStopDistance)
//                                / (slowdownStartDistance - maximumStopDistance);
//
//                        double computedWalkingDistance = slowdownFactor * this.preferredWalkingDistance;
//
//                        if (this.isNextAmenityTrainDoor() && PatchFieldPatch != null) {
//                            Double PatchFieldValue = null;
//                            Map<QueueingPatchField.PatchFieldState, Double> PatchFieldValues
//                                    = PatchFieldPatch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());
//
//                            if (PatchFieldValues != null) {
//                                PatchFieldValue = PatchFieldValues.get(this.goalPatchFieldState);
//                            }
//
//                            if (
//                                    PatchFieldValue != null
//                                            && Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() < PatchFieldValue
//                            ) {
//                                this.shouldStopAtPlatform = true;
//                            } else {
//                                // Only head towards that patch if the distance from that patch to the goal is further than the
//                                // distance from this agent to the goal
//                                double distanceFromChosenPatchToGoal = Coordinates.distance(
//                                        this.currentFloor.getStation(),
//                                        PatchFieldPatch,
//                                        this.goalAttractor.getPatch()
//                                );
//
//                                double distanceFromThisAgentToGoal = Coordinates.distance(
//                                        this.currentFloor.getStation(),
//                                        this.currentPatch,
//                                        this.goalAttractor.getPatch()
//                                );
//
//                                double revisedHeading;
//                                Coordinates revisedPosition;
//
//                                if (distanceFromChosenPatchToGoal < distanceFromThisAgentToGoal) {
//                                    if (!this.getGoalAmenityAsTrainDoor().isOpen()) {
//                                        // Get the heading towards that patch
//                                        revisedHeading = Coordinates.headingTowards(
//                                                this.position,
//                                                PatchFieldPatch.getPatchCenterCoordinates()
//                                        );
//                                    } else {
//                                        revisedHeading = Coordinates.headingTowards(
//                                                this.position,
//                                                this.goalAttractor.getPatch().getPatchCenterCoordinates()
//                                        );
//                                    }
//
//                                    revisedPosition = this.getFuturePosition(
//                                            this.position,
//                                            revisedHeading,
//                                            computedWalkingDistance
//                                    );
//
//                                    // Get the attractive force of this agent to the new position
//                                    this.attractiveForce = this.computeAttractiveForce(
//                                            new Coordinates(this.position),
//                                            revisedHeading,
//                                            revisedPosition,
//                                            computedWalkingDistance
//                                    );
//
//                                    vectorsToAdd.add(attractiveForce);
//
//                                    for (
//                                            Map.Entry<Double, Agent> otherAgentEntry
//                                            : agentsWithinFieldOfView.entrySet()
//                                    ) {
//                                        // Then compute the repulsive force from this agent
//                                        // Compute the perceived density of the agents
//                                        // Assuming the maximum density a agent sees within its environment is 5 before it thinks the crowd
//                                        // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
//                                        // maximum tolerated number of agents
//                                        final int maximumAgentCountTolerated = 5;
//
//                                        // The distance by which the repulsion starts to kick in will depend on the density of the agent's
//                                        // surroundings
//                                        final int minimumAgentCount = 1;
//                                        final double maximumDistance = 2.0;
//                                        final int maximumAgentCount = 5;
//                                        final double minimumDistance = 0.7;
//
//                                        double computedMaximumDistance = computeMaximumRepulsionDistance(
//                                                numberOfObstacles,
//                                                maximumAgentCountTolerated,
//                                                minimumAgentCount,
//                                                maximumDistance,
//                                                maximumAgentCount,
//                                                minimumDistance
//                                        );
//
//                                        Vector agentRepulsiveForce = computeSocialForceFromAgent(
//                                                otherAgentEntry.getValue(),
//                                                otherAgentEntry.getKey(),
//                                                computedMaximumDistance,
//                                                minimumAgentStopDistance,
//                                                this.preferredWalkingDistance
//                                        );
//
//                                        // Add the computed vector to the list of vectors
//                                        this.repulsiveForceFromAgents.add(agentRepulsiveForce);
//                                    }
//                                }
//                            }
//                        } else {
//                            Coordinates revisedPosition = this.getFuturePosition(computedWalkingDistance);
//
//                            // Get the attractive force of this agent to the new position
//                            this.attractiveForce = this.computeAttractiveForce(
//                                    new Coordinates(this.position),
//                                    this.proposedHeading,
//                                    revisedPosition,
//                                    computedWalkingDistance
//                            );
//
//                            vectorsToAdd.add(attractiveForce);
//                        }
//                    }
//                } else {
//                    // If the agent hasn't already been moving for a while, consider the agent stuck, and implement some
//                    // measures to free this agent
//                    if (
//                            this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold
//                    ) {
//                        this.isStuck = true;
//                        this.stuckCounter++;
//                    }
//
//                    boolean hasEncounteredQueueingAgentInLoop = false;
//                    boolean hasEncounteredPortalWaitingAgentInLoop = false;
//
//                    // Only apply the social forces of a set number of agents and obstacles
//                    int agentsProcessed = 0;
//                    final int agentsProcessedLimit = 5;
//
//                    // Look around the patches that fall on the agent's field of view
//                    for (Patch patch : patchesToExplore) {
//                        // If this patch has an obstacle, take note of it to add a repulsive force from it later
//                        Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
//
//                        // Get the distance between this agent and the obstacle on this patch
//                        if (hasObstacle(patch)) {
//                            // Take note of the obstacle density in this area
//                            numberOfObstacles++;
//
//                            // If the distance is less than or equal to the specified minimum repulsion distance, compute
//                            // for the magnitude of the repulsion force
//                            double distanceToObstacle = Coordinates.distance(
//                                    this.position,
//                                    patchAmenityBlock.getPatch().getPatchCenterCoordinates()
//                            );
//
//                            if (distanceToObstacle <= slowdownStartDistance) {
//                                obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
//                            }
//                        }
//
//                        // Inspect each agent in each patch in the patches in the field of view
//                        for (Agent otherAgent : patch.getAgents()) {
//                            if (agentsProcessed == agentsProcessedLimit) {
//                                break;
//                            }
//
//                            // Make sure that the agent discovered isn't itself
//                            if (!otherAgent.equals(this.getParent())) {
//                                // Take note of the agent density in this area
//                                numberOfAgents++;
//
//                                // Get the distance between this agent and the other agent
//                                double distanceToOtherAgent = Coordinates.distance(
//                                        this.position,
//                                        otherAgent.getAgentMovement().getPosition()
//                                );
//
//                                // If the distance is less than or equal to the distance when repulsion is supposed to kick in,
//                                // compute for the magnitude of that repulsion force
//                                if (distanceToOtherAgent <= slowdownStartDistance) {
//                                    // Compute the perceived density of the agents
//                                    // Assuming the maximum density a agent sees within its environment is 3 before it thinks the crowd
//                                    // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
//                                    // maximum tolerated number of agents
//                                    final int maximumAgentCountTolerated = 5;
//
//                                    // The distance by which the repulsion starts to kick in will depend on the density of the agent's
//                                    // surroundings
//                                    final int minimumAgentCount = 1;
//                                    final double maximumDistance = 2.0;
//                                    final int maximumAgentCount = 5;
//                                    final double minimumDistance = 0.7;
//
//                                    double computedMaximumDistance = computeMaximumRepulsionDistance(
//                                            numberOfObstacles,
//                                            maximumAgentCountTolerated,
//                                            minimumAgentCount,
//                                            maximumDistance,
//                                            maximumAgentCount,
//                                            minimumDistance
//                                    );
//
//                                    Vector agentRepulsiveForce = computeSocialForceFromAgent(
//                                            otherAgent,
//                                            distanceToOtherAgent,
//                                            computedMaximumDistance,
//                                            minimumAgentStopDistance,
//                                            this.preferredWalkingDistance
//                                    );
//
//                                    // Add the computed vector to the list of vectors
//                                    this.repulsiveForceFromAgents.add(agentRepulsiveForce);
//
//                                    // Also, check this agent's state
//                                    // If this agent is queueing, set the relevant variable - it will stay true even if just
//                                    // one nearby agent has activated it
//                                    if (!hasEncounteredQueueingAgentInLoop) {
//                                        // Check if the other agent is in a queueing or assembling with the same goal as
//                                        // this agent
//                                        if (this.agentFollowedWhenAssembling == null) {
//                                            this.hasEncounteredAgentToFollow = false;
//                                        } else {
//                                            if (this.agentFollowedWhenAssembling.equals(otherAgent)) {
//                                                // If the other agent encountered is already assembling, decide whether this
//                                                // agent will assemble too depending on whether the other agent was selected
//                                                // to be followed by this one
//                                                this.hasEncounteredAgentToFollow
//                                                        = (otherAgent.getAgentMovement().getAction() == Action.ASSEMBLING
//                                                        || otherAgent.getAgentMovement().getAction() == Action.QUEUEING)
//                                                        && otherAgent.getAgentMovement().getGoalAmenity().equals(this.goalAmenity);
//                                            } else {
//                                                this.hasEncounteredAgentToFollow = false;
//                                            }
//                                        }
//                                    }
//
//                                    hasEncounteredQueueingAgentInLoop
//                                            = this.hasEncounteredAgentToFollow;
//
//                                    // Check if this agent has encountered a agent waiting for the same portal
//                                    if (!hasEncounteredPortalWaitingAgentInLoop) {
//                                        // If the other agent encountered is already assembling, decide whether this
//                                        // agent will assemble too depending on whether the other agent was selected
//                                        // to be followed by this one
//                                        this.hasEncounteredPortalWaitingAgent
//                                                = otherAgent.getAgentMovement().isWaitingOnPortal()
//                                                && otherAgent.getAgentMovement().getGoalAmenity().equals(this.goalAmenity);
//                                    }
//
//                                    hasEncounteredPortalWaitingAgentInLoop
//                                            = this.hasEncounteredPortalWaitingAgent;
//
////                                    this.isWaitingOnPortal
////                                            = this.isWaitingOnPortal || hasEncounteredPortalWaitingAgentInLoop;
//
//                                    // If a queueing agent has been encountered, do not pathfind anymore for this
//                                    // goal
//                                    if (
//                                            this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
//                                                    && this.hasEncounteredAgentToFollow
//                                    ) {
//                                        this.hasPathfound = true;
//                                    }
//
//                                    agentsProcessed++;
//                                }
//                            }
//                        }
//                    }
//
//                    // Get the attractive force of this agent to the new position
//                    this.attractiveForce = this.computeAttractiveForce(
//                            new Coordinates(this.position),
//                            this.proposedHeading,
//                            proposedNewPosition,
//                            this.preferredWalkingDistance
//                    );
//
//                    vectorsToAdd.add(attractiveForce);
//                }
//            }
//        } else {
//            proposedNewPosition = this.computeFirstStepPosition();
//
//            // Check if the patch representing the future position has someone on it
//            // Only proceed when there is no one there
////            if (
////                    this.hasNoAgent(this.currentFloor.getPatch(proposedNewPosition))
////                            || this.getCurrentTurnstileGate() != null
////            ) {
////                this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//            // Get the attractive force of this agent to the new position
//            this.attractiveForce = this.computeAttractiveForce(
//                    new Coordinates(this.position),
//                    Coordinates.headingTowards(
//                            this.position,
//                            proposedNewPosition
//                    ),
//                    proposedNewPosition,
//                    this.preferredWalkingDistance
//            );
//
//            vectorsToAdd.add(attractiveForce);
////            }
//
//            this.shouldStepForward = false;
//        }
//
//        // Here ends the few ticks of grace period for the agent to leave its starting patch
//        if (
//                !this.willPathfind
//                        && !this.hasPathfound
//                        && this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
//                        && !hasJustLeftGoal()
//        ) {
//            this.beginStoredValuePathfinding();
//        }
//
//        // Take note of the previous walking distance of this agent
//        double previousWalkingDistance = this.currentWalkingDistance;
//
//        vectorsToAdd.addAll(this.repulsiveForceFromAgents);
//
//        // Then compute the partial motivation force of the agent
//        Vector partialMotivationForce = Vector.computeResultantVector(
//                new Coordinates(this.position),
//                vectorsToAdd
//        );
//
//        // If the resultant vector is null (i.e., no change in position), simply don't move at all
//        if (!this.shouldStopAtPlatform && partialMotivationForce != null) {
//            // The distance by which the repulsion starts to kick in will depend on the density of the agent's
//            // surroundings
//            final int minimumObstacleCount = 1;
//            final double maximumDistance = 2.0;
//            final int maximumObstacleCount = 2;
//            final double minimumDistance = 0.7;
//
//            final int maximumObstacleCountTolerated = 2;
//
//            double computedMaximumDistance = computeMaximumRepulsionDistance(
//                    numberOfObstacles,
//                    maximumObstacleCountTolerated,
//                    minimumObstacleCount,
//                    maximumDistance,
//                    maximumObstacleCount,
//                    minimumDistance
//            );
//
//            // Only apply the social forces on a set number of obstacles
//            int obstaclesProcessed = 0;
//            final int obstaclesProcessedLimit = 4;
//
//            for (Map.Entry<Double, Amenity.AmenityBlock> obstacleEntry : obstaclesEncountered.entrySet()) {
//                if (obstaclesProcessed == obstaclesProcessedLimit) {
//                    break;
//                }
//
//                this.repulsiveForcesFromObstacles.add(
//                        computeSocialForceFromObstacle(
//                                obstacleEntry.getValue(),
//                                obstacleEntry.getKey(),
//                                computedMaximumDistance,
//                                minimumObstacleStopDistance,
//                                partialMotivationForce.getMagnitude()
//                        )
//                );
//
//                obstaclesProcessed++;
//            }
//
//            vectorsToAdd.clear();
//
//            vectorsToAdd.add(partialMotivationForce);
//            vectorsToAdd.addAll(this.repulsiveForcesFromObstacles);
//
//            // Finally, compute the final motivation force
//            this.motivationForce = Vector.computeResultantVector(
//                    new Coordinates(this.position),
//                    vectorsToAdd
//            );
//
//            if (this.motivationForce != null) {
//                // Cap the magnitude of the motivation force to the agent's preferred walking distance
//                if (this.motivationForce.getMagnitude() > this.preferredWalkingDistance) {
//                    this.motivationForce.adjustMagnitude(this.preferredWalkingDistance);
//                }
//
//                // Then adjust its heading with minor stochastic deviations
//                this.motivationForce.adjustHeading(
//                        this.motivationForce.getHeading()
//                                + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(5)
//                );
//
//                try {
//                    // Set the new heading
//                    double newHeading = motivationForce.getHeading();
//
//                    Coordinates candidatePosition = this.motivationForce.getFuturePosition();
//
//                    if (hasClearLineOfSight(this.position, candidatePosition, false)) {
//                        this.move(candidatePosition);
//                    } else {
//                        double revisedHeading;
//                        Coordinates newFuturePosition;
//
//                        int attempts = 0;
//                        final int attemptLimit = 2;
//
//                        boolean freeSpaceFound;
//
//                        do {
//                            // Go back with the same magnitude as the original motivation force, but at a different
//                            // heading
//                            revisedHeading
//                                    = (motivationForce.getHeading() + Math.toRadians(180)) % Math.toRadians(360);
//
//                            // Add some stochasticity to this revised heading
//                            revisedHeading += Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(90);
//                            revisedHeading %= Math.toRadians(360);
//
//                            // Then calculate the future position from the current position
//                            newFuturePosition = this.getFuturePosition(
//                                    this.position,
//                                    revisedHeading,
//                                    this.preferredWalkingDistance * 0.25
//                            );
//
//                            freeSpaceFound
//                                    = hasClearLineOfSight(this.position, newFuturePosition, false);
//
//                            attempts++;
//                        } while (attempts < attemptLimit && !freeSpaceFound);
//
//                        // If all the attempts are used and no free space has been found, don't move at all
//                        if (attempts != attemptLimit || freeSpaceFound) {
//                            this.move(newFuturePosition);
//                        }
//                    }
//
//                    if (
//                            !this.isStuck
//                                    || Coordinates.headingDifference(
//                                    this.heading,
//                                    newHeading
//                            ) <= Math.toDegrees(90.0)
//                                    || this.currentWalkingDistance > noMovementThreshold
//                    ) {
////                         Take note of the new heading
//                        this.heading = newHeading;
//                    }
//
//                    // Also take note of the new speed
//                    this.currentWalkingDistance = motivationForce.getMagnitude();
//
//                    // Finally, take note of the distance travelled by this agent
//                    this.distanceMovedInTick = motivationForce.getMagnitude();
//
//                    // If this agent's distance covered falls under the threshold, increment the counter denoting the ticks
//                    // spent not moving
//                    // Otherwise, reset the counter
//                    // Do not count for movements/non-movements when the agent is in the "in queue" state
//                    if (this.state != State.IN_QUEUE) {
//                        if (this.recentPatches.size() <= noNewPatchesSeenThreshold) {
//                            this.noNewPatchesSeenCounter++;
//                            this.newPatchesSeenCounter = 0;
//                        } else {
//                            this.noNewPatchesSeenCounter = 0;
//                            this.newPatchesSeenCounter++;
//                        }
//                    } else {
//                        if (
//                                this.distanceMovedInTick < noMovementThreshold
//                        ) {
//                            this.noMovementCounter++;
//                            this.movementCounter = 0;
//                        } else {
//                            this.noMovementCounter = 0;
//                            this.movementCounter++;
//                        }
//                    }
//
//                    // If the agent has moved above the no-movement threshold for at least this number of ticks,
//                    // remove the agent from its stuck state
//                    if (
//                            this.isStuck
//                                    && (
//                                    (
//                                            this.state == State.IN_QUEUE
//                                                    && this.movementCounter >= unstuckTicksThreshold
//                                                    || this.state != State.IN_QUEUE
//                                                    && this.newPatchesSeenCounter >= unstuckTicksThreshold/*
//                                            || this.agentFollowedWhenAssembling != null*/
//                                    )
//                            )
//                    ) {
//                        this.isReadyToFree = true;
//                    }
//
//                    this.timeSinceLeftPreviousGoal++;
//
//                    // Check if the agent has slowed down since the last tick
//                    // If it did, reset the time spent accelerating counter
//                    if (this.currentWalkingDistance < previousWalkingDistance) {
//                        this.ticksAcceleratedOrMaintainedSpeed = 0;
//                    } else {
//                        this.ticksAcceleratedOrMaintainedSpeed++;
//                    }
//
//                    return true;
//                } catch (ArrayIndexOutOfBoundsException ignored) {
//                }
//            }
//        }
//
//        // If it reaches this point, there is no movement to be made
//        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//        this.stop();
//
//        // There was no movement by this agent, so increment the pertinent counter
//        this.distanceMovedInTick = 0.0;
//
//        this.noMovementCounter++;
//        this.movementCounter = 0;
//
//        this.timeSinceLeftPreviousGoal++;
//
//        this.ticksAcceleratedOrMaintainedSpeed = 0;
//
//        return false;
//    }
//
//    private boolean allowRepulsionFrom(Agent otherAgent) { // Checks if another agent should apply a repulsive force on this agent, taking into account parameters other than this agent
//        boolean isNotHeadingToQueueable = this.action != Action.HEADING_TO_QUEUEABLE;
//        boolean isNotInOppositeStatesWithOtherAgent
//                = !(this.state == State.IN_QUEUE && otherAgent.getAgentMovement().getState() != State.IN_QUEUE);
//
//        boolean otherAgentComesBefore;
//
//        if (this.state == State.IN_QUEUE) {
//            Queueable queueable = this.getGoalAmenityAsQueueable();
//
//            if (!(queueable instanceof TrainDoor) && !(queueable instanceof Turnstile)) {
//                QueueObject queueObject = queueable.getQueueObject();
//                List<Agent> queueingAgents = queueObject.getAgentsQueueing();
//
//                if (queueingAgents.contains(this.parent) && queueingAgents.contains(otherAgent)) {
//                    otherAgentComesBefore = otherAgent.getAgentMovement().comesBefore(this.parent);
//                } else {
//                    otherAgentComesBefore = true;
//                }
//            } else {
//                otherAgentComesBefore = true;
//            }
//        } else {
//            otherAgentComesBefore = true;
//        }
//
//        boolean otherAgentInSameGoal;
//
//        Queueable thisAgentQueueable = this.getGoalAmenityAsQueueable();
//        Queueable otherAgentQueueable = otherAgent.getAgentMovement().getGoalAmenityAsQueueable();
//
//        if (thisAgentQueueable != null && otherAgentQueueable != null) {
//            otherAgentInSameGoal = thisAgentQueueable.equals(otherAgentQueueable);
//        } else {
//            otherAgentInSameGoal = true;
//        }
//
//        return
//                isNotHeadingToQueueable
//                        && isNotInOppositeStatesWithOtherAgent
//                        && otherAgentComesBefore
//                        && otherAgentInSameGoal;
//    }
//
//    private double computeSecurityFirstStepHeading() {
//        Security security = (Security) this.currentPatch.getAmenityBlock().getParent(); // First, get the apex of the floor field with the state of the agent
//
//        return computeHeadingFromApexToAttractor(security.getSecurityPatchFieldState(), security.getQueueObject(), security.getAttractors());
//    }
//
//    private double computeHeadingFromApexToAttractor(QueueingPatchField.PatchFieldState PatchFieldState, QueueObject queueObject, List<Amenity.AmenityBlock> attractors) {
//        Patch apexLocation;
//        double newHeading;
//
//        apexLocation = queueObject.getPatchFields().get(PatchFieldState).getApices().get(0);
//        // Then compute the heading from the apex to the turnstile attractor
//        newHeading = Coordinates.headingTowards(apexLocation.getPatchCenterCoordinates(), attractors.get(0).getPatch().getPatchCenterCoordinates());
//
//        return newHeading;
//    }
//
//    private double computeFirstStepHeading() {
//        double newHeading;
//
//        if (this.currentPatch.getAmenityBlock() != null && this.currentPatch.getAmenityBlock().getParent() instanceof Security) {
//            newHeading = computeSecurityFirstStepHeading();
//        }
//        else {
//            newHeading = this.previousHeading;
//        }
//
//        return newHeading;
//    }
//
//    private Coordinates computeFirstStepPosition() {
//        double newHeading = computeFirstStepHeading();
//
//        return this.getFuturePosition(this.position, newHeading, this.preferredWalkingDistance); // Compute for the proposed future position
//    }
//
//    public Agent getNearestAgentOnFirstStepPosition() {
//        Patch firstStepPosition = this.university.getPatch(this.computeFirstStepPosition());
//
//        Agent nearestAgent = null;
//        double nearestDistance = Double.MAX_VALUE;
//
//        for (Agent agent : firstStepPosition.getAgents()) {
//            UniversityAgent universityAgent = (UniversityAgent) agent;
//            double distanceFromAgent = Coordinates.distance(this.position, universityAgent.getAgentMovement().getPosition());
//
//            if (distanceFromAgent < nearestDistance) {
//                nearestAgent = agent;
//                nearestDistance = distanceFromAgent;
//            }
//        }
//
//        return nearestAgent;
//    }
//
//    public boolean isFirstStepPositionFree() {
//        return hasNoAgent(this.university.getPatch(this.computeFirstStepPosition()));
//    }
//
//    private boolean hasNoAgent(Patch patch) {
//        if (patch == null) {
//            return true;
//        }
//
//        List<Agent> agentsOnPatchWithoutThisAgent = patch.getAgents();
//        agentsOnPatchWithoutThisAgent.remove(this.parent);
//
//        return agentsOnPatchWithoutThisAgent.isEmpty();
//    }
//
//    public List<Patch> get7x7Field(double heading, boolean includeCenterPatch, double fieldOfViewAngle) {
//        Patch centerPatch = this.currentPatch;
//        List<Patch> patchesToExplore = new ArrayList<>();
//        boolean isCenterPatch;
//
//        for (Patch patch : centerPatch.get7x7Neighbors(includeCenterPatch)) {
//            // Make sure that the patch to be added is within the field of view of the agent which invoked this method
//            isCenterPatch = patch.equals(centerPatch);
//            if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(centerPatch.getPatchCenterCoordinates(), patch.getPatchCenterCoordinates(), heading, fieldOfViewAngle)) {
//                patchesToExplore.add(patch);
//            }
//        }
//
//        return patchesToExplore;
//    }
//
//    private Vector computeAttractiveForce(final Coordinates startingPosition, final double proposedHeading, final Coordinates proposedNewPosition, final double preferredWalkingDistance) {
//
//        return new Vector(startingPosition, proposedHeading, proposedNewPosition, preferredWalkingDistance);
//    }
//
//    private double computeMaximumRepulsionDistance(int objectCount, final int maximumObjectCountTolerated, final int minimumObjectCount, final double maximumDistance, final int maximumObjectCount, final double minimumDistance) {
//        if (objectCount > maximumObjectCountTolerated) {
//            objectCount = maximumObjectCountTolerated;
//        }
//
//        final double a = (maximumDistance - minimumDistance) / (minimumObjectCount - maximumDistance);
//        final double b = minimumDistance - a * maximumObjectCount;
//
//        return a * objectCount + b;
//    }
//
//    private double computeRepulsionMagnitudeFactor(final double distance, final double maximumDistance, final double minimumRepulsionFactor, final double minimumDistance, final double maximumRepulsionFactor) {
//        // Formula: for the inverse square law equation y = a / x ^ 2 + b,
//        // a = (d_max ^ 2 * (r_min * d_max ^ 2 - r_min * d_min ^ 2 + r_max ^ 2 * d_min ^ 2)) / (d_max ^ 2 - d_min ^ 2)
//        // and
//        // b = -((r_max ^ 2 * d_min ^ 2) / (d_max ^ 2 - d_min ^ 2))
//        double differenceOfSquaredDistances = Math.pow(maximumDistance, 2.0) - Math.pow(minimumDistance, 2.0);
//        double productOfMaximumRepulsionAndMinimumDistance = Math.pow(maximumRepulsionFactor, 2.0) * Math.pow(minimumDistance, 2.0);
//
//        double a = (Math.pow(maximumDistance, 2.0) * (minimumRepulsionFactor * Math.pow(maximumDistance, 2.0) - minimumRepulsionFactor * Math.pow(minimumDistance, 2.0) + productOfMaximumRepulsionAndMinimumDistance)) / differenceOfSquaredDistances;
//        double b = -(productOfMaximumRepulsionAndMinimumDistance / differenceOfSquaredDistances);
//
//        double repulsion = a / Math.pow(distance, 2.0) + b;
//
//        if (repulsion <= 0.0) { // The repulsion value should always be greater or equal to zero
//            repulsion = 0.0;
//        }
//
//        return repulsion;
//    }
//
//    private Vector computeSocialForceFromAgent(UniversityAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
//        final double maximumRepulsionFactor = 1.0;
//        final double minimumRepulsionFactor = 0.0;
//
//        Coordinates agentPosition = agent.getAgentMovement().getPosition();
//
//        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at that minimum
//        double modifiedDistanceToObstacle = Math.max(distanceToOtherAgent, minimumDistance);
//        double repulsionMagnitudeCoefficient;
//        double repulsionMagnitude;
//
//        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
//        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;
//
//        if (this.isStuck) { // If a agent is stuck, do not exert much force from this agent
//            final double factor = 0.05;
//
//            repulsionMagnitude -= this.stuckCounter * factor;
//
//            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
//                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
//            }
//        }
//
//        // Then compute the heading from that other agent to this agent
//        double headingFromOtherAgent = Coordinates.headingTowards(agentPosition, this.position);
//
//        // Then compute for a future position given the other agent's position, the heading, and the magnitude; This will be used as the endpoint of the repulsion vector from this obstacle
//        Coordinates agentRepulsionVectorFuturePosition = this.getFuturePosition(agentPosition, headingFromOtherAgent, repulsionMagnitude);
//
//        // Finally, given the current position, heading, and future position, create the vector from the other agent to the current agent
//        return new Vector(agentPosition, headingFromOtherAgent, agentRepulsionVectorFuturePosition, repulsionMagnitude);
//    }
//
//    private Vector computeSocialForceFromObstacle(Amenity.AmenityBlock amenityBlock, final double distanceToObstacle, final double maximumDistance, double minimumDistance, final double maximumMagnitude) {
//        final double maximumRepulsionFactor = 1.0;
//        final double minimumRepulsionFactor = 0.0;
//
//        Coordinates repulsionVectorStartingPosition = amenityBlock.getPatch().getPatchCenterCoordinates();
//
//        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at that minimum
//        double modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
//
//        double repulsionMagnitudeCoefficient;
//        double repulsionMagnitude;
//
//        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
//
//        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;
//
//        // If an agent is stuck, do not exert much force from this obstacle
//        if (this.isStuck) {
//            final double factor = 0.05;
//
//            repulsionMagnitude -= this.stuckCounter * factor;
//
//            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
//                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
//            }
//        }
//
//        // Compute the heading from that origin point to this agent
//        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position);
//
//        // Then compute for a future position given the obstacle's position, the heading, and the magnitude
//        // This will be used as the endpoint of the repulsion vector from this obstacle
//        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude);
//
//        // Finally, given the current position, heading, and future position, create the vector from the obstacle to the current agent
//        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude);
//    }
//
//    private void move(double walkingDistance) { // Make the agent move given a walking distance
//        this.setPosition(this.getFuturePosition(walkingDistance));
//    }
//
//    private void move(Coordinates futurePosition) { // Make the agent move given the future position
//        this.setPosition(futurePosition);
//    }
//
//    public boolean hasReachedQueueingPatchField() { // Check if this agent has reached its goal's queueing patch field
//        for (Patch patch : this.goalPatchField.getAssociatedPatches()) {
//            if (isOnOrCloseToPatch(patch) && hasClearLineOfSight(this.position, patch.getPatchCenterCoordinates(), true)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public boolean hasPath() { // Check if this agent has a path to follow
//        return this.currentPath != null;
//    }
//
//    public boolean hasReachedNextPatchInPath() { // Check if this agent is on the next patch of its path
//        return isOnOrCloseToPatch(this.currentPath.getPath().peek());
//    }
//
//    public void joinQueue() { // Register this agent to its queueable goal's queue
//        this.goalQueueObject.getAgentsQueueing().addLast(this.parent);
//    }
//
//    public void stop() { // Have the agent stop
//        this.currentWalkingDistance = 0.0;
//    }
//
//    public void leaveQueue() { // Unregister this agent to its queueable goal's queue
//        this.goalQueueObject.getAgentsQueueing().remove(this.parent);
//    }
//
//    public boolean hasReachedQueueingPatchFieldApex() { // Check if this agent has reached an apex of its floor field
//        // If the agent is in any of this floor field's apices, return true
//        for (Patch apex : this.goalPatchField.getApices()) {
//            if (isOnOrCloseToPatch(apex)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public void beginWaitingOnAmenity() { // Have this agent start waiting for an amenity to become vacant
//        this.isWaitingOnAmenity = true;
//    }
//
//    // Check if the goal of this agent is currently not servicing anyone
//    public boolean isGoalFree() {
//        return this.getGoalAmenityAsGoal().isFree(this.goalQueueObject)
//                && this.goalQueueObject.getPatch().getAgents().isEmpty();
//    }
//
//    // Check if this agent the one currently served by its goal
//    public boolean isServicedByGoal() {
//        Agent agentServiced = this.goalQueueObject.getAgentServiced();
//
//        return agentServiced != null && agentServiced.equals(this.parent);
//    }
//
//    // Check if this agent is at the front of the queue
//    public boolean isAtQueueFront() {
//        LinkedList<Agent> agentsQueueing
//                = this.goalQueueObject.getAgentsQueueing();
//
//        if (agentsQueueing.isEmpty()) {
//            return false;
//        }
//
//        return agentsQueueing.getFirst() == this.parent;
//    }
//
//    // Have this agent stop waiting for an amenity to become vacant
//    public void endWaitingOnAmenity() {
//        this.isWaitingOnAmenity = false;
//    }
//
//    // Have this agent stop waiting for a portal to become vacant
//    public void endWaitingOnPortal() {
//        this.isWaitingOnPortal = false;
//    }
//
//    // Enable pathfinding for stored value card agents
//    public void beginStoredValuePathfinding() {
//        this.willPathfind = true;
//    }
//
//    // Disable pathfinding for stored value card agents
//    public void endStoredValuePathfinding() {
//        this.currentPath = null;
//
//        this.willPathfind = false;
//        this.hasPathfound = true;
//    }
//
//    // Check if this agent has reached its goal
//    public boolean hasReachedGoal() {
//        // If the agent is still waiting for an amenity to be vacant, it hasn't reached the goal yet
//        if (this.isWaitingOnAmenity) {
//            return false;
//        }
//
//        return isOnOrCloseToPatch(this.goalAttractor.getPatch());
//    }
//
//    // Set the agent's current amenity and position as it reaches the next goal
//    public void reachGoal() {
//        // Just in case the agent isn't actually on its goal, but is adequately close to it, just move the agent
//        // there
//        // Make sure to offset the agent from the center a little so a force will be applied to this agent
//        Coordinates patchCenter = this.goalAttractor.getPatch().getPatchCenterCoordinates();
//        Coordinates offsetPatchCenter = this.getFuturePosition(
//                patchCenter,
//                this.previousHeading,
//                Patch.PATCH_SIZE_IN_SQUARE_METERS * 0.1
//        );
//
//        this.setPosition(offsetPatchCenter);
//
//        // Set the current amenity
//        this.currentAmenity = this.goalAmenity;
//
//        // If this goal is a portal, add it to the list of visited portals
//        if (this.currentAmenity instanceof Portal) {
//            this.visitedPortals.add((Portal) this.currentAmenity);
//        } else if (this.currentAmenity instanceof Turnstile) {
//            // If this goal is a turnstile, set the heading
//            this.heading = this.computeFirstStepHeading();
//        }
//    }
//
//    // Set the agent's next patch in its current path as it reaches it
//    public void reachPatchInPath() {
//        // Keep popping while there are still patches from the path to pop and these patches are still close enough to
//        // the agent
//        Patch nextPatch;
//
//        do {
//            this.currentPath.getPath().pop();
//
//            // If there are no more next patches, terminate the loop
//            if (!this.currentPath.getPath().isEmpty()) {
//                nextPatch = this.currentPath.getPath().peek();
//            } else {
//                break;
//            }
//        } while (
//                !this.currentPath.getPath().isEmpty()
//                        && nextPatch.getAmenityBlocksAround() == 0
//                        && this.isOnOrCloseToPatch(nextPatch)
//                        && this.hasClearLineOfSight(
//                        this.position,
//                        nextPatch.getPatchCenterCoordinates(),
//                        true
//                )
//        );
//    }
//
//    // Have this agent's goal service this agent
//    public void beginServicingThisAgent() {
//        // This agent will now be the one to be served next
//        this.goalQueueObject.setAgentServiced(this.parent);
//    }
//
//    // Have this agent's goal finish serving this agent
//    public void endServicingThisAgent() {
//        // This agent is done being serviced by this goal
//        this.goalQueueObject.setAgentServiced(null);
//
//        // Reset the goal's waiting time counter
//        if (this.getGoalAmenityAsGoal() != null) {
//            this.getGoalAmenityAsGoal().resetWaitingTime();
//        }
//    }
//
//    // Check if this agent has reached its final goal
//    public boolean hasReachedFinalGoal() {
//        return !this.routePlan.getCurrentRoutePlan().hasNext();
//    }
//
//    // Check if this agent has reached the final patch in its current path
//    public boolean hasAgentReachedFinalPatchInPath() {
//        return this.currentPath.getPath().isEmpty();
//    }
//
//    // Check if this agent has reached the specified patch
//    private boolean isOnPatch(Patch patch) {
//        return ((int) (this.position.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getColumn()
//                && ((int) (this.position.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getRow();
//    }
//
//    // Check if this agent is adequately close enough to a patch
//    // In this case, a agent is close enough to a patch when the distance between this agent and the patch is
//    // less than the distance covered by the agent per second
//    private boolean isOnOrCloseToPatch(Patch patch) {
//        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
//    }
//
//    // Check if this agent is allowed by its goal to pass
//    public boolean isAllowedPass() {
//        boolean allowPassAsGoal = this.getGoalAmenityAsGoal().allowPass();
//
//        Blockable blockable = this.getGoalAmenityAsBlockable();
//
//        if (blockable != null) {
//            return allowPassAsGoal && !blockable.blockEntry();
//        } else {
//            return allowPassAsGoal;
//        }
//    }
//
//    // Check if this agent will enter the train
//    public boolean willEnterTrain() {
//        TrainDoor closestTrainDoor = getGoalAmenityAsTrainDoor();
//
//        if (closestTrainDoor != null) {
//            // If the goal train door is open, check if the agent is willing to ride the train
//            if (isTrainDoorOpen(closestTrainDoor)) {
//                // TODO: Have a better way of checking whether this agent has necessary information, or not
//                //  perhaps insert a different agent information object to a agent when at train simulation and
//                //  at station editing?
//                if (this.getRoutePlan().getOriginStation() != null) {
//                    // TODO: Get station in a better way
//                    com.trainsimulation.model.core.environment.trainservice.agentservice.stationset.Station station
//                            = this.getRoutePlan().getOriginStation();
//
//                    final double loadLimit = 0.7;
//                    final double carriageLoadLimit = closestTrainDoor.getTrainCarriage(station).getLoadFactor();
//
//                    // Carriage full/overfull, don't ride
//                    if (carriageLoadLimit >= 1.0) {
//                        return false;
//                    } else if (carriageLoadLimit > loadLimit) {
//                        // Carriage almost full, ride (or don't) based on a probability
//                        final double loadLimitLeft = 1.0 - loadLimit;
//                        final double probability = (1 - carriageLoadLimit) / loadLimitLeft;
//
//                        return Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() < probability;
//                    } else {
//                        // A lot of room in the carriages, ride
//                        return true;
//                    }
//                } else {
//                    return true;
//                }
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    // Check whether this agent's goal as a train door is open
//    private boolean isTrainDoorOpen(TrainDoor trainDoor) {
//        return trainDoor.isOpen();
//    }
//
//    // Check if this agent will use a portal
//    public boolean willHeadToPortal() {
//        return this.goalFloor != null && this.goalPortal != null;
//    }
//
//    // Check if this agent's next floor is below the current floor
//    public boolean isGoalFloorLower() {
//        if (!willHeadToPortal()) {
//            return false;
//        } else {
//            List<Floor> floorsInThisStation = this.currentFloor.getStation().getFloors();
//
//            // Get the index of the current and goal floors
//            int currentFloorIndex = floorsInThisStation.indexOf(this.currentFloor);
//            int goalFloorIndex = floorsInThisStation.indexOf(this.goalFloor);
//
//            assert currentFloorIndex != goalFloorIndex;
//
//            return goalFloorIndex < currentFloorIndex;
//        }
//    }
//
//    // Check if this agent will enter the portal
//    public boolean willEnterPortal() {
//        Portal closestPortal = getGoalAmenityAsPortal();
//
//        if (closestPortal != null) {
//            if (closestPortal instanceof StairPortal) {
//                StairPortal stairPortal = ((StairPortal) closestPortal);
//
//                if (this.isGoalFloorLower()) {
//                    return !stairPortal.getStairShaft().isDescendingQueueAtCapacity();
//                } else {
//                    return !stairPortal.getStairShaft().isAscendingQueueAtCapacity();
//                }
//            } else if (closestPortal instanceof EscalatorPortal) {
//                EscalatorPortal escalatorPortal = ((EscalatorPortal) closestPortal);
//
//                return !escalatorPortal.getEscalatorShaft().isQueueAtCapacity();
//            } else if (closestPortal instanceof ElevatorPortal) {
//                return false;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    // Have this agent enter its portal
//    public void enterPortal() {
//        // Remove the agent from its patch
//        this.currentPatch.getAgents().remove(this.parent);
//
//        // Remove this agent from this floor
//        this.currentFloor.getAgentsInFloor().remove(this.parent);
//
//        // Remove this agent from its current floor's patch set, if necessary
//        SortedSet<Patch> currentPatchSet = this.currentPatch.getFloor().getAgentPatchSet();
//
//        if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
//            currentPatchSet.remove(this.currentPatch);
//        }
//
//        // Set the agent's patch to null
//        this.currentPatch = null;
//    }
//
//    // Have this agent try exiting its portal
//    public boolean exitPortal() {
//        // Move towards the other end of the portal
//        Portal portal = (Portal) this.currentAmenity;
//        portal = portal.getPair();
//
//        // Try to emit a agent
//        Patch spawnPatch = portal.emit();
//
//        // Only proceed is a agent can be emitted
//        if (spawnPatch != null) {
//            // Get the patch of the spawner which released this agent
//            Patch spawnerPatch = spawnPatch;
//
//            // Set the current patch, floor
//            this.currentPatch = spawnerPatch;
//
//            this.currentFloor = portal.getFloorServed();
//            this.currentAmenity = portal;
//
//            // Set the agent's position
//            this.position.setX(spawnerPatch.getPatchCenterCoordinates().getX());
//            this.position.setY(spawnerPatch.getPatchCenterCoordinates().getY());
//
//            // Set the new state and action
//            this.state = State.WALKING;
//
//            if (this.isReadyToExit) {
//                this.action = Action.EXITING_STATION;
//            } else {
//                this.action = Action.WILL_QUEUE;
//            }
//
//            // Add the newly created agent to the list of agents in the floor
//            this.currentFloor.getAgentsInFloor().add(this.parent);
//
//            // Add the agent's patch position to its current floor's patch set as well
//            this.currentFloor.getAgentPatchSet().add(spawnerPatch);
//
//            return true;
//        } else {
//            // No agent emitted, return false
//            return false;
//        }
//    }
//
//    // Board train
//    public void boardTrain(TrainDoor trainDoor) {
//        // Remove the agent from its current station
//        trainDoor.despawnAgent(this.parent);
//
//        // Set the agent's new state and action
//        this.state = State.IN_TRAIN;
//        this.action = Action.RIDING_TRAIN;
//
//        // If this agent is female and has chosen a female only carriage, mark as such
//        Agent.AgentInformation agentInformation = this.getParent().getAgentInformation();
//
//        if (agentInformation.getGender() == Agent.AgentInformation.Gender.FEMALE) {
//            if (trainDoor.isFemaleOnly()) {
//                agentInformation.setChosenFemaleOnlyCarriage(true);
//            }
//        }
//
//        // The agent is not at any patch
//        this.currentPatch = null;
//    }
//
//    // Alight train
//    public void alightTrain(Gate.GateBlock spawner) {
//        // Before anything else, reset the agent movement to avoid residual values from the previous station
//        this.resetGoal(false);
//
//        // Get the patch of the spawner which released this agent
//        Patch spawnerPatch = spawner.getPatch();
//
//        // Set the new state and action of the alighted agent
//        this.state = State.WALKING;
//        this.action = Action.WILL_QUEUE;
//
//        // Set the current patch, floor
//        this.currentPatch = spawnerPatch;
//
//        this.currentFloor = spawnerPatch.getFloor();
//        this.currentAmenity = spawner.getParent();
//
//        // Set the agent's position
//        this.position.setX(spawner.getPatch().getPatchCenterCoordinates().getX());
//        this.position.setY(spawner.getPatch().getPatchCenterCoordinates().getY());
//
//        // Add the newly created agent to the list of agents in the floor
//        this.currentFloor.getAgentsInFloor().add(this.parent);
//
//        // Add the agent's patch position to its current floor's patch set as well
//        this.currentFloor.getAgentPatchSet().add(spawner.getPatch());
//    }
//
//    // Despawn this agent
//    public void despawn() {
//        if (this.currentPatch != null) {
//            // Remove the agent from its patch
//            this.currentPatch.getAgents().remove(this.parent);
//
//            // Remove this agent from this floor
//            this.currentFloor.getAgentsInFloor().remove(this.parent);
//
//            // Remove this agent from this station
//            this.currentFloor.getStation().getAgentsInStation().remove(this.parent);
//
//            // Remove this agent from its current floor's patch set, if necessary
//            SortedSet<Patch> currentPatchSet = this.currentPatch.getFloor().getAgentPatchSet();
//
//            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
//                currentPatchSet.remove(this.currentPatch);
//            }
//        }
//    }
//
//    // Have the agent face its current goal, or its queueing area, or the agent at the end of the queue
//    public void faceNextPosition() {
//        double newHeading;
//        boolean willFaceQueueingPatch;
//        Patch proposedGoalPatch;
//
//        // iI the agent is already heading for a queueable, no need to seek its floor fields again, as
//        // it has already done so, and is now just heading to the goal itself
//        // If it has floor fields, get the heading towards the nearest floor field value
//        // If it doesn't have floor fields, just get the heading towards the goal itself
//        if (
//                this.action != Action.HEADING_TO_QUEUEABLE
//                        && this.action != Action.HEADING_TO_TRAIN_DOOR
//                        && this.goalAmenity instanceof Queueable
//        ) {
//            // If a queueing patch has not yet been set for this goal, set it
//            if (this.goalNearestQueueingPatch == null) {
//                // If the next floor field has not yet been set for this queueing patch, set it
//                if (this.goalPatchFieldState == null && this.goalPatchField == null) {
//                    Queueable queueable = this.getGoalAmenityAsQueueable();
//
//                    if (queueable instanceof Turnstile) {
////                        this.goalPatchFieldState = new QueueingPatchField.PatchFieldState(
////                                this.disposition,
////                                State.IN_QUEUE,
////                                this.getGoalAmenityAsQueueable()
////                        );
//
//                        this.goalPatchField = queueable.retrievePatchField(
//                                this.goalQueueObject,
//                                this.goalPatchFieldState
//                        );
//                    } else if (queueable instanceof TrainDoor) {
////                        this.goalPatchFieldState = new PlatformPatchField.PlatformPatchFieldState(
////                                this.disposition,
////                                State.IN_QUEUE,
////                                this.getGoalAmenityAsQueueable(),
////                                this.goalTrainDoorEntranceLocation
////                        );
//
//                        this.goalPatchField = queueable.retrievePatchField(
//                                this.goalQueueObject,
//                                this.goalPatchFieldState
//                        );
//                    } else {
////                        this.goalPatchFieldState = new QueueingPatchField.PatchFieldState(
////                                this.disposition,
////                                State.IN_QUEUE,
////                                this.getGoalAmenityAsQueueable()
////                        );
//
//                        this.goalPatchField = queueable.retrievePatchField(
//                                queueable.getQueueObject(),
//                                this.goalPatchFieldState
//                        );
//                    }
//                }
//
//                if (this.goalPatchField == null) {
//                    this.getGoalAmenityAsQueueable().retrievePatchField(
//                            this.goalQueueObject,
//                            this.goalPatchFieldState
//                    );
//                }
//
//                this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
//                proposedGoalPatch = this.goalNearestQueueingPatch;
//            }
//
//            // If this agent is in the "will queue" state, choose between facing the queueing patch, and facing the
//            // agent at the back of the queue
//            if (action == Action.WILL_QUEUE || action == Action.ASSEMBLING) {
//                LinkedList<Agent> agentQueue
//                        = this.goalQueueObject.getAgentsQueueing();
//
//                // Check whether there are agents queueing for the goal
//                if (agentQueue.isEmpty()) {
//                    // If there are no agents queueing yet, simply compute the heading towards the nearest queueing
//                    // patch
//                    this.agentFollowedWhenAssembling = null;
//                    this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
//                    proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                    willFaceQueueingPatch = true;
//                } else {
//                    if (this.isNextAmenityTrainDoor()) {
//                        this.agentFollowedWhenAssembling = null;
//                        this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
//                        proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                        willFaceQueueingPatch = true;
//                    } else {
//                        Agent agentFollowedCandidate;
//
//                        // If there are agents queueing, join the queue and follow either the last person in the queue
//                        // or the person before this
//                        if (action == Action.WILL_QUEUE) {
//                            agentFollowedCandidate = agentQueue.getLast();
//                        } else {
//                            int agentFollowedCandidateIndex = agentQueue.indexOf(this.parent) - 1;
//
//                            if (agentFollowedCandidateIndex >= 0) {
//                                agentFollowedCandidate
//                                        = agentQueue.get(agentFollowedCandidateIndex);
//                            } else {
//                                agentFollowedCandidate = null;
//                            }
//                        }
//
//                        // But if the person to be followed is this person itself, or is not assembling, or follows this
//                        // person too (forming a cycle), disregard it, and just follow the queueing patch
//                        // Otherwise, follow that agent
//                        if (
//                                agentFollowedCandidate == null
//                                        || agentFollowedCandidate.equals(this.parent)
//                                        || !agentFollowedCandidate.equals(this.parent)
//                                        && agentFollowedCandidate.getAgentMovement()
//                                        .getAgentFollowedWhenAssembling() != null
//                                        && agentFollowedCandidate.getAgentMovement()
//                                        .getAgentFollowedWhenAssembling().equals(this.parent)
//                        ) {
//                            this.agentFollowedWhenAssembling = null;
//                            this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
//                            proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                            willFaceQueueingPatch = true;
//                        } else {
//                            // But only follow agents who are nearer to this agent than to the chosen queueing
//                            // patch and are within this agent's walking distance and have a clear line of sight to
//                            // this agent
//                            final double distanceToAgentFollowedCandidate = Coordinates.distance(
//                                    this.position,
//                                    agentFollowedCandidate.getAgentMovement().getPosition()
//                            );
//
//                            final double agentFollowedRange = this.preferredWalkingDistance * 3.0;
//
//                            if (
//                                    !hasClearLineOfSight(this.position, agentFollowedCandidate.getAgentMovement().getPosition(), true)
//                                            || distanceToAgentFollowedCandidate > agentFollowedRange
//                            ) {
//                                this.agentFollowedWhenAssembling = null;
//                                this.goalNearestQueueingPatch = this.getPatchWithNearestPatchFieldValue();
//                                proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                                willFaceQueueingPatch = true;
//                            } else {
//                                this.agentFollowedWhenAssembling = agentFollowedCandidate;
//                                proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                                willFaceQueueingPatch = false;
//
//                            }
//                        }
//                    }
//                }
//            } else {
//                this.agentFollowedWhenAssembling = null;
//                proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                willFaceQueueingPatch = true;
//            }
//
//            if (willFaceQueueingPatch) {
//                newHeading = Coordinates.headingTowards(
//                        this.position,
//                        this.goalNearestQueueingPatch.getPatchCenterCoordinates()
//                );
//            } else {
//                // Get the distance from here to both the proposed agent followed and the nearest queueing
//                // patch
//                double distanceToAgent = Coordinates.distance(
//                        this.position,
//                        this.agentFollowedWhenAssembling.getAgentMovement().getPosition()
//                );
//
//                double distanceToQueueingPatch = Coordinates.distance(
//                        this.position,
//                        this.goalNearestQueueingPatch.getPatchCenterCoordinates()
//                );
//
//                // Head towards whoever is nearer
//                if (distanceToAgent > distanceToQueueingPatch) {
//                    newHeading = Coordinates.headingTowards(
//                            this.position,
//                            this.goalNearestQueueingPatch.getPatchCenterCoordinates()
//                    );
//                } else {
//                    newHeading = Coordinates.headingTowards(
//                            this.position,
//                            this.agentFollowedWhenAssembling.getAgentMovement().getPosition()
//                    );
//                }
//            }
//        } else {
//            proposedGoalPatch = this.goalAttractor.getPatch();
//
//            // Compute the heading towards the goal's attractor
//            newHeading = Coordinates.headingTowards(
//                    this.position,
//                    this.goalAttractor.getPatch().getPatchCenterCoordinates()
//            );
//        }
////        }
//
//        if (this.willPathfind || this.action == Action.REROUTING) {
//            // Get the heading towards the goal patch, which was set as the next patch in the path
//            newHeading = Coordinates.headingTowards(
//                    this.position,
//                    this.goalPatch.getPatchCenterCoordinates()
//            );
//
////            this.proposedHeading = newHeading;
//        } else {
//            this.goalPatch = proposedGoalPatch;
//        }
//
//        // Then set the agent's proposed heading to it
//        this.proposedHeading = newHeading;
//    }
//
//    // While the agent is already on a floor field, have the agent face the one with the highest value
//    public void chooseBestQueueingPatch() {
//        // Retrieve the patch with the highest floor field value around the agent's vicinity
//        this.goalNearestQueueingPatch = this.getBestQueueingPatch();
//        this.goalPatch = this.goalNearestQueueingPatch;
//    }
//
//    // If the agent is following a path, have the agent face the next one, if any
//    public boolean chooseNextPatchInPath() {
//        // Generate a path, if one hasn't been generated yet
//        boolean wasPathJustGenerated = false;
//
//        final int recomputeThreshold = 10;
//
//        if (
//                this.currentPath == null
//                        || this.isStuck
//                        && this.noNewPatchesSeenCounter > recomputeThreshold
//        ) {
//            AgentPath agentPath;
//
//            if (this.getGoalAmenityAsQueueable() != null) {
//                // Head towards the queue of the goal
//                LinkedList<Agent> agentsQueueing
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
//                    Agent lastAgent = agentsQueueing.getLast();
//
//                    if (
//                            !(this.getGoalAmenityAsQueueable() instanceof TrainDoor)
//                                    && !(this.getGoalAmenityAsQueueable() instanceof Turnstile)
//                                    && lastAgent.getAgentMovement().getAction() == Action.ASSEMBLING
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
//
//            if (agentPath != null) {
//                // Create a copy of the object, to avoid using up the path directly from the cache
//                this.currentPath = new AgentPath(agentPath);
//
//                wasPathJustGenerated = true;
//            }
//        }
//
//        // Get the first patch still unvisited in the path
//        if (this.currentPath == null || this.currentPath.getPath().isEmpty()) {
//            return false;
//        }
//
//        // If a path was just generated, determine the first patch to visit
//        if (wasPathJustGenerated) {
//            Patch nextPatchInPath;
//
//            while (true) {
//                nextPatchInPath = this.currentPath.getPath().peek();
//
//                if (
//                        !(
//                                this.currentPath.getPath().size() > 1
//                                        && nextPatchInPath.getAmenityBlocksAround() == 0
//                                        && this.isOnOrCloseToPatch(nextPatchInPath)
//                                        && this.hasClearLineOfSight(
//                                        this.position,
//                                        nextPatchInPath.getPatchCenterCoordinates(),
//                                        true
//                                )
//                        )
//                ) {
//                    break;
//                }
//
//                this.currentPath.getPath().pop();
//            }
//
//            this.goalPatch = nextPatchInPath;
//
////            ///
////
////            do {
////                goalPatchInPath = this.currentPath.getPath().pop();
////            } while (
////                    !this.currentPath.getPath().isEmpty()
////                            && goalPatchInPath.getAmenityBlocksAround() == 0
////                            && this.isOnOrCloseToPatch(goalPatchInPath)
////                            && this.hasClearLineOfSight(
////                            this.position,
////                            goalPatchInPath.getPatchCenterCoordinates(),
////                            true
////                    )
////            );
////
//////            if (goalPatchInPath == null) {
//////                return false;
//////            } else {
////            this.goalPatch = goalPatchInPath;
//////            }
//        } else {
//            this.goalPatch = this.currentPath.getPath().peek();
//        }
//
//        return true;
//    }
//
//    // Make this agent free from being stuck
//    public void free() {
//        this.isStuck = false;
//
//        this.stuckCounter = 0;
//        this.noMovementCounter = 0;
//        this.noNewPatchesSeenCounter = 0;
//
//        this.currentPath = null;
//
//        this.isReadyToFree = false;
//    }
//
//    // From a set of patches associated with a goal's floor field, get the nearest patch below a threshold
//    public Patch getPatchWithNearestPatchFieldValue() {
//        final double maximumPatchFieldValueThreshold = 0.8;
//
//        // Get the patches associated with the current goal
//        List<Patch> associatedPatches = this.goalPatchField.getAssociatedPatches();
//
//        double minimumDistance = Double.MAX_VALUE;
//        Patch nearestPatch = null;
//
//        // Look for the nearest patch from the patches associated with the floor field
//        double distanceFromAgent;
//
//        for (Patch patch : associatedPatches) {
//            double PatchFieldValue
//                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable()).get(this.goalPatchFieldState);
//
////            if (PatchFieldValue <= maximumPatchFieldValueThreshold) {
//            // Get the distance of that patch from this agent
//            distanceFromAgent = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
//
//            if (distanceFromAgent < minimumDistance) {
//                minimumDistance = distanceFromAgent;
//                nearestPatch = patch;
//            }
////            }
//        }
//
//        return nearestPatch;
//    }
//
//    private Patch computeBestQueueingPatchWeighted(List<Patch> PatchFieldList) {
//        // Collect the patches with the highest floor field values
//        List<Patch> PatchFieldCandidates = new ArrayList<>();
//        List<Double> PatchFieldValueCandidates = new ArrayList<>();
//
//        double valueSum = 0.0;
//
//        for (Patch patch : PatchFieldList) {
//            Map<QueueingPatchField.PatchFieldState, Double> PatchFieldStateDoubleMap
//                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());
//
//            if (
//                    !patch.getPatchFieldValues().isEmpty()
//                            && PatchFieldStateDoubleMap != null
//                            && !PatchFieldStateDoubleMap.isEmpty()
//                            && PatchFieldStateDoubleMap.get(
//                            this.goalPatchFieldState
//                    ) != null
//            ) {
//                double futurePatchFieldValue = patch.getPatchFieldValues()
//                        .get(this.getGoalAmenityAsQueueable())
//                        .get(this.goalPatchFieldState);
//
////                if (currentPatchFieldValue == null) {
//                valueSum += futurePatchFieldValue;
//
//                PatchFieldCandidates.add(patch);
//                PatchFieldValueCandidates.add(futurePatchFieldValue);
////                }
//            }
//        }
//
//        // If it gets to this point without finding a floor field value greater than zero, return early
//        if (PatchFieldCandidates.isEmpty()) {
////            if (this.getGoalAmenityAsTrainDoor() != null) {
////                this.computeBestQueueingPatchWeighted(PatchFieldList);
////            }
//
//            return null;
//        }
//
//        Patch chosenPatch;
//        int choiceIndex = 0;
//
//        // Use the floor field values as weights to choose among patches
//        for (
//                double randomNumber = Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() * valueSum;
//                choiceIndex < PatchFieldValueCandidates.size() - 1;
//                choiceIndex++) {
//            randomNumber -= PatchFieldValueCandidates.get(choiceIndex);
//
//            if (randomNumber <= 0.0) {
//                break;
//            }
//        }
//
//        chosenPatch = PatchFieldCandidates.get(choiceIndex);
//        return chosenPatch;
//    }
//
//    // Get the next queueing patch in a floor field given the current floor field state
//    private Patch computeBestQueueingPatch(List<Patch> PatchFieldList) {
//        // Collect the patches with the highest floor field values
//        List<Patch> highestPatches = new ArrayList<>();
//
//        double maximumPatchFieldValue = 0.0;
//
//        for (Patch patch : PatchFieldList) {
//            Map<QueueingPatchField.PatchFieldState, Double> PatchFieldStateDoubleMap
//                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());
//
//            if (
//                    !patch.getPatchFieldValues().isEmpty()
//                            && PatchFieldStateDoubleMap != null
//                            && !PatchFieldStateDoubleMap.isEmpty()
//                            && PatchFieldStateDoubleMap.get(
//                            this.goalPatchFieldState
//                    ) != null
//            ) {
//                double PatchFieldValue = patch.getPatchFieldValues()
//                        .get(this.getGoalAmenityAsQueueable())
//                        .get(this.goalPatchFieldState);
//
//                if (PatchFieldValue >= maximumPatchFieldValue) {
//                    if (PatchFieldValue > maximumPatchFieldValue) {
//                        maximumPatchFieldValue = PatchFieldValue;
//
//                        highestPatches.clear();
//                    }
//
//                    highestPatches.add(patch);
//                }
//            }
//        }
//
//        // If it gets to this point without finding a floor field value greater than zero, return early
//        if (maximumPatchFieldValue == 0.0) {
//            return null;
//        }
//
//        // If there are more than one highest valued-patches, choose the one where it would take the least heading
//        // difference
//        Patch chosenPatch /*= highestPatches.get(0)*/ = null;
//
//        List<Double> headingChanges = new ArrayList<>();
//        List<Double> distances = new ArrayList<>();
//
//        double headingToHighestPatch;
//        double headingChangeRequired;
//
//        double distance;
//
//        for (Patch patch : highestPatches) {
//            headingToHighestPatch = Coordinates.headingTowards(this.position, patch.getPatchCenterCoordinates());
//            headingChangeRequired = Coordinates.headingDifference(this.proposedHeading, headingToHighestPatch);
//
//            double headingChangeRequiredDegrees = Math.toDegrees(headingChangeRequired);
//
//            headingChanges.add(headingChangeRequiredDegrees);
//
//            distance = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
//            distances.add(distance);
//        }
//
//        double minimumHeadingChange = Double.MAX_VALUE;
//
//        for (int index = 0; index < highestPatches.size(); index++) {
//            double individualScore = headingChanges.get(index) * 1.0/* + (distances.get(index) * 10.0) * 0.5*/;
//
//            if (individualScore < minimumHeadingChange) {
//                minimumHeadingChange = individualScore;
//                chosenPatch = highestPatches.get(index);
//            }
//        }
//
//        return chosenPatch;
//    }
//
//    private Patch getBestQueueingPatch() {
//        // Get the patches to explore
////        List<Patch> patchesToExplore
////                = Floor.get7x7Field(
////                this.currentFloor,
////                this.currentPatch,
////                this.proposedHeading,
////                false,
////                this.fieldOfViewAngle
////        );
//
//        List<Patch> patchesToExplore = this.get7x7Field(
//                this.proposedHeading,
//                false,
//                this.fieldOfViewAngle
//        );
//
////        this.toExplore = patchesToExplore;
//
//        return this.computeBestQueueingPatch(patchesToExplore);
//    }
//
//    // Get the best queueing patch around the current patch of another agent given the current floor field state
//    private Patch getBestQueueingPatchAroundAgent(Agent otherAgent) {
//        // Get the other agent's patch
//        Patch otherAgentPatch = otherAgent.getAgentMovement().getCurrentPatch();
//
//        // Get the neighboring patches of that patch
//        List<Patch> neighboringPatches = otherAgentPatch.getNeighbors();
//
//        // Remove the patch containing this agent
//        neighboringPatches.remove(this.currentPatch);
//
//        // Only add patches with the fewest agents
//        List<Patch> neighboringPatchesWithFewestAgents = new ArrayList<>();
//        int minimumAgentCount = Integer.MAX_VALUE;
//
//        for (Patch neighboringPatch : neighboringPatches) {
//            int neighboringPatchAgentCount = neighboringPatch.getAgents().size();
//
//            if (neighboringPatchAgentCount < minimumAgentCount) {
//                neighboringPatchesWithFewestAgents.clear();
//
//                minimumAgentCount = neighboringPatchAgentCount;
//            }
//
//            if (neighboringPatchAgentCount == minimumAgentCount) {
//                neighboringPatchesWithFewestAgents.add(neighboringPatch);
//            }
//        }
//
//        // Choose a floor field patch from this
//        Patch chosenPatch = this.computeBestQueueingPatchWeighted(neighboringPatchesWithFewestAgents);
//
//        return chosenPatch;
//    }
//
//    // Check if the given patch has an obstacle
//    private boolean hasObstacle(Patch patch) {
//        // If there is literally no patch there, then there is no obstacle
//        if (patch == null) {
//            return true;
//        }
//
//        Amenity.AmenityBlock amenityBlock = patch.getAmenityBlock();
//
//        if (amenityBlock == null) {
//            return false;
//        } else {
//            Amenity parent = amenityBlock.getParent();
//
//            if (parent instanceof NonObstacle && ((NonObstacle) parent).isEnabled()) {
//                if (parent.equals(this.goalAmenity)) {
//                    if (parent instanceof Queueable) {
//                        Agent agentServiced = this.goalQueueObject.getAgentServiced();
//
//                        if (agentServiced != null && agentServiced.equals(this.parent)) {
//                            if (amenityBlock instanceof Gate.GateBlock) {
//                                Gate.GateBlock gateBlock = ((Gate.GateBlock) amenityBlock);
//
//                                return !amenityBlock.isAttractor() && !gateBlock.isSpawner();
//                            } else {
//                                return !amenityBlock.isAttractor();
//                            }
//                        } else {
//                            return true;
//                        }
//                    } else {
//                        if (amenityBlock instanceof Gate.GateBlock) {
//                            Gate.GateBlock gateBlock = ((Gate.GateBlock) amenityBlock);
//
//                            return !amenityBlock.isAttractor() && !gateBlock.isSpawner();
//                        } else {
//                            return !amenityBlock.isAttractor();
//                        }
//                    }
//                } else {
//                    if (parent instanceof Gate) {
//                        if (amenityBlock instanceof Gate.GateBlock) {
//                            Gate.GateBlock gateBlock = ((Gate.GateBlock) amenityBlock);
//
//                            return !amenityBlock.isAttractor() && !gateBlock.isSpawner();
//                        } else {
//                            return !amenityBlock.isAttractor();
//                        }
//                    } else {
//                        return true;
//                    }
//                }
//            } else {
//                return true;
//            }
//        }
//    }
//
//    // Check if there is a clear line of sight from one point to another
//    private boolean hasClearLineOfSight(
//            Coordinates sourceCoordinates,
//            Coordinates targetCoordinates,
//            boolean includeStartingPatch
//    ) {
//        // First of all, check if the target has an obstacle
//        // If it does, then no need to check what is between the two points
//        if (hasObstacle(this.currentFloor.getPatch(targetCoordinates))) {
//            return false;
//        }
//
//        final double resolution = 0.2;
//
//        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
//        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);
//
//        Patch startingPatch = this.currentFloor.getPatch(sourceCoordinates);
//
//        Coordinates currentPosition = new Coordinates(sourceCoordinates);
//        double distanceCovered = 0.0;
//
//        // Keep looking for blocks while there is still distance to cover
//        while (distanceCovered <= distanceToTargetCoordinates) {
//            if (includeStartingPatch || !this.currentFloor.getPatch(currentPosition).equals(startingPatch)) {
//                // Check if there is an obstacle in the current position
//                // If there is, return early
//                if (hasObstacle(this.currentFloor.getPatch(currentPosition))) {
//                    return false;
//                }
//            }
//
//            // If there isn't any, move towards the target coordinates with the given increment
//            currentPosition = this.getFuturePosition(
//                    currentPosition,
//                    headingToTargetCoordinates,
//                    resolution
//            );
//
//            distanceCovered += resolution;
//        }
//
//        // The target has been reached without finding an obstacle, so there is a clear line of sight between the two
//        // given points
//        return true;
//    }
//
//    // Check if this agent comes before the given agent
//    private boolean comesBefore(Agent agent) {
//        if (this.goalQueueObject != null) {
//            List<Agent> goalQueue = this.goalQueueObject.getAgentsQueueing();
//
//            if (goalQueue.size() >= 2) {
//                int thisAgentIndex = goalQueue.indexOf(this.parent);
//                int otherAgentIndex = goalQueue.indexOf(agent);
//
//                if (thisAgentIndex != -1 && otherAgentIndex != -1) {
//                    return thisAgentIndex < otherAgentIndex;
//                } else {
//                    return false;
//                }
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    // Update the agent's recent patches
//    private void updateRecentPatches(Patch currentPatch, final int timeElapsedExpiration) {
//        List<Patch> patchesToForget = new ArrayList<>();
//
//        // Update the time elapsed in all of the recent patches
//        for (Map.Entry<Patch, Integer> recentPatchesAndTimeElapsed : this.recentPatches.entrySet()) {
//            this.recentPatches.put(recentPatchesAndTimeElapsed.getKey(), recentPatchesAndTimeElapsed.getValue() + 1);
//
//            // Remove all patches that are equal to the expiration time given
//            if (recentPatchesAndTimeElapsed.getValue() == timeElapsedExpiration) {
//                patchesToForget.add(recentPatchesAndTimeElapsed.getKey());
//            }
//        }
//
//        // If there is a new patch to add or update to the recent patch list, do so
//        if (currentPatch != null) {
//            // The time lapsed value of any patch added or updated will always be zero, as it means this patch has been
//            // recently encountered by this agent
//            this.recentPatches.put(currentPatch, 0);
//        }
//
//        // Remove all patches set to be forgotten
//        for (Patch patchToForget : patchesToForget) {
//            this.recentPatches.remove(patchToForget);
//        }
//    }
//
//    public void prepareForExit() {
//        this.isReadyToExit = true;
//    }
//
//    public enum State {
//        WALKING, IN_QUEUEABLE, IN_QUEUE, IN_NONQUEUEABLE, IN_CLASS, IN_BATHROOM
//    }
//
//    public enum Action {
//        /* Walking actions */
//        WILL_QUEUE, REROUTING, EXITING,
//        /* In queue actions */
//        ASSEMBLING, QUEUEING, HEADING_TO_QUEUEABLE,
//        /* In queueable actions */
//        SECURITY_CHECKING, FOUNTAIN_DRINKING
//    }
//
//}