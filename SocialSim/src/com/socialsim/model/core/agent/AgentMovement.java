//package com.socialsim.model.core.agent;
//
//import com.socialsim.model.core.environment.patch.patchobject.passable.gate.Gate;
//import com.socialsim.model.core.environment.patch.position.Vector;
//import com.socialsim.model.core.environment.university.University;
//import com.socialsim.model.core.environment.university.UniversityPatch;
//import com.socialsim.model.core.environment.patch.patchobject.Amenity;
//import com.socialsim.model.core.environment.patch.position.Coordinates;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Stack;
//
//public class AgentMovement {
//
//    private final Agent parent;
//    private final Coordinates position;
//    private final double preferredWalkingDistance; // Denotes the distance (m) the agent walks in one second
//    private double currentWalkingDistance;
//    private double proposedHeading; // Denotes the proposed heading of the agent in degrees where E = 0 degrees, N = 90, W = 180, Se = 270
//    private double heading;
//    private double previousHeading;
//    private UniversityPatch currentPatch;
//    private Amenity currentAmenity;
//    private UniversityPatch goalPatch;
//    private Amenity goalAmenity;
//    private Amenity.AmenityBlock goalAttractor;
//
//    // Denotes the state of this agent's floor field
//    private QueueingFloorField.FloorFieldState goalFloorFieldState;
//
//    // Denotes the floor field of the agent goal
//    private QueueingFloorField goalFloorField;
//
//    // Denotes the patch with the nearest queueing patch
//    private UniversityPatch goalNearestQueueingPatch;
//
//    // Denotes the route plan of this agent
//    private RoutePlan routePlan;
//
//    // Denotes the current path followed by this agent, if any
//    private Stack<UniversityPatch> currentPath;
//
//    // Get the floor where this agent currently is
//    private University university;
//
//    // Denotes whether the agent is temporarily waiting on an amenity to be vacant
//    private boolean isWaitingOnAmenity;
//
//    // Denotes whether this agent has encountered the agent to be followed in the queue
//    private boolean hasEncounteredAgentToFollow;
//
//    // Denotes whether this agent has encountered any queueing agent
//    private boolean hasEncounteredAnyQueueingAgent;
//
//    // Denotes the agent this agent is currently following while assembling
//    private Agent agentFollowedWhenAssembling;
//
//    // Denotes the distance moved by this agent in the previous tick
//    private double distanceMovedInTick;
//
//    // Counts the ticks this agent moved a distance under a certain threshold
//    private int noMovementCounter;
//
//    // Counts the ticks this agent has spent moving - this will reset when stopping
//    private int movementCounter;
//
//    // Counts the ticks this agent has seen less than the defined number of patches
//    private int noNewPatchesSeenCounter;
//
//    // Counts the ticks this agent has spent seeing new patches - this will reset otherwise
//    private int newPatchesSeenCounter;
//
//    // Denotes whether the agent is stuck
//    private boolean isStuck;
//
//    // Counts the ticks this agent has spent being stuck - this will reset when a condition is reached
//    private int stuckCounter;
//
//    // Denotes the time since the agent left its previous goal
//    private int timeSinceLeftPreviousGoal;
//
//    // Denotes the time until the agent accelerates fully from non-movement
//    final int ticksUntilFullyAccelerated;
//
//    // Denotes the time the agent has spent accelerating or moving at a constant speed so far without slowing down
//    // or stopping
//    private int ticksAcceleratedOrMaintainedSpeed;
//
//    // Denotes the field of view angle of the agent
//    private final double fieldOfViewAngle;
//
//    // Denotes whether the agent is ready to be freed from being stuck
//    private boolean isReadyToFree;
//
//    // Denotes whether the agent as a stored value card holder is ready to pathfind
//    private boolean willPathfind;
//
//    // Denotes whether this agent as a stored value card holder has already pathfound
//    private boolean hasPathfound;
//
//    // Denotes whether this agent should take a step forward after it left its goal
//    private boolean shouldStepForward;
//
//    // Denotes the patches to explore for obstacles or agents
//    private List<UniversityPatch> toExplore;
//
//    // Denotes the recent patches this agent has been in
//    private final HashMap<UniversityPatch, Integer> recentPatches;
//
//    // The vectors of this agent
//    private final List<Vector> repulsiveForceFromAgents;
//    private final List<Vector> repulsiveForcesFromObstacles;
//    private Vector attractiveForce;
//    private Vector motivationForce;
//
//    public AgentMovement(Gate gate, Agent parent, Coordinates coordinates) {
//        this.parent = parent;
//
//        this.position = new Coordinates(
//                coordinates.getX(),
//                coordinates.getY()
//        );
//
//        // TODO: Walking speed should depend on the agent's age
//        // TODO: Adjust to actual, realistic values
//        // The walking speed values shall be in m/s
//        this.preferredWalkingDistance = 0.6;
//        this.currentWalkingDistance = preferredWalkingDistance;
//
//        // All newly generated agents will face the north by default
//        // The heading values shall be in degrees, but have to be converted to radians for the math libraries to process
//        // East: 0 degrees
//        // North: 90 degrees
//        // West: 180 degrees
//        // South: 270 degrees
//        this.proposedHeading = Math.toRadians(90.0);
//        this.heading = Math.toRadians(90.0);
//
//        this.previousHeading = Math.toRadians(90.0);
//
//        // Set the agent's field of view
//        this.fieldOfViewAngle = Math.toRadians(90.0);
//
//        // Add this agent to the start patch
//        this.currentPatch = Main.simulator.getCurrentFloor().getPatch(coordinates);
//        this.currentPatch.getAgents().add(parent);
//
//        // Set the agent's time until it fully accelerates
//        this.ticksUntilFullyAccelerated = 10;
//        this.ticksAcceleratedOrMaintainedSpeed = 0;
//
//        // Take note of the amenity where this agent was spawned
//        this.currentAmenity = gate;
//
//        // Assign the route plan of this agent
//        this.routePlan = new RoutePlan(
//                this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
//        );
//
//        // Assign the floor of this agent
//        this.currentFloor = gate.getAmenityBlocks().get(0).getPatch().getFloor();
//
//        // Assign the initial direction, state, action of this agent
//        this.direction = Direction.BOARDING;
//        this.state = State.WALKING;
//        this.action = Action.WILL_QUEUE;
//
//        this.toExplore = new ArrayList<>();
//        this.recentPatches = new HashMap<>();
//
//        repulsiveForceFromAgents = new ArrayList<>();
//        repulsiveForcesFromObstacles = new ArrayList<>();
//
//        // Set the agent goal
//        resetGoal(false);
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
//
//        UniversityPatch previousPatch = this.currentPatch;
//
//        this.position.setX(coordinates.getX());
//        this.position.setY(coordinates.getY());
//
//        // Get the patch of the new position
//        UniversityPatch newPatch = this.currentFloor.getPatch(new Coordinates(coordinates.getX(), coordinates.getY()));
//
//        // If the newer position is on a different patch, remove the agent from its old patch, then
//        // add it to the new patch
//        if (!previousPatch.equals(newPatch)) {
//            previousPatch.getAgents().remove(this.parent);
//            newPatch.getAgents().add(this.parent);
//
//            // Remove this agent from the patch set of the previous patch
//            SortedSet<UniversityPatch> previousPatchSet = previousPatch.getFloor().getAgentPatchSet();
//            SortedSet<UniversityPatch> newPatchSet = newPatch.getFloor().getAgentPatchSet();
//
//            if (
//                    previousPatchSet.contains(previousPatch)
//                            && previousPatch.getAgents().isEmpty()
//            ) {
//                previousPatchSet.remove(previousPatch);
//            }
//
//            // Then add this agent to the patch set of the next patch
//            newPatchSet.add(newPatch);
//
//            // Then set the new current patch
//            this.currentPatch = newPatch;
//
//            // Update the recent patch list
//            updateRecentPatches(this.currentPatch, timeElapsedExpiration);
//        } else {
//            // Update the recent patch list
//            updateRecentPatches(null, timeElapsedExpiration);
//        }
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
//    public UniversityPatch getCurrentPatch() {
//        return currentPatch;
//    }
//
//    public void setCurrentPatch(UniversityPatch currentPatch) {
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
//    public UniversityPatch getGoalPatch() {
//        return goalPatch;
//    }
//
//    public Amenity getGoalAmenity() {
//        return goalAmenity;
//    }
//
//    public QueueingFloorField.FloorFieldState getGoalFloorFieldState() {
//        return goalFloorFieldState;
//    }
//
//    public QueueingFloorField getGoalFloorField() {
//        return goalFloorField;
//    }
//
//    public UniversityPatch getGoalNearestQueueingPatch() {
//        return goalNearestQueueingPatch;
//    }
//
//    public RoutePlan getRoutePlan() {
//        return routePlan;
//    }
//
//    public void setRoutePlan(RoutePlan routePlan) {
//        this.routePlan = routePlan;
//    }
//
//    public Stack<UniversityPatch> getCurrentPath() {
//        return currentPath;
//    }
//
//    public Floor getCurrentFloor() {
//        return currentFloor;
//    }
//
//    public Direction getDirection() {
//        return direction;
//    }
//
//    public void setDirection(Direction direction) {
//        this.direction = direction;
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
//    public boolean hasEncounteredAnyQueueingAgent() {
//        return hasEncounteredAnyQueueingAgent;
//    }
//
//    public List<UniversityPatch> getToExplore() {
//        return toExplore;
//    }
//
//    public HashMap<UniversityPatch, Integer> getRecentPatches() {
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
//    //
//
//    public Queueable getGoalAmenityAsQueueable() {
//        return Queueable.toQueueable(this.goalAmenity);
//    }
//
//    public Goal getGoalAmenityAsGoal() {
//        return Goal.toGoal(this.goalAmenity);
//    }
//
//    public TrainDoor getGoalAmenityAsTrainDoor() {
//        return TrainDoor.asTrainDoor(this.goalAmenity);
//    }
//
//    // Use the A* algorithm (with Euclidian distance to compute the f-score) to find the shortest path to the given goal
//    // patch
//    public Stack<UniversityPatch> computePath(
//            UniversityPatch startingPatch,
//            UniversityPatch goalPatch
//    ) {
//        HashSet<UniversityPatch> openSet = new HashSet<>();
//
//        HashMap<UniversityPatch, Double> gScores = new HashMap<>();
//        HashMap<UniversityPatch, Double> fScores = new HashMap<>();
//
//        HashMap<UniversityPatch, UniversityPatch> cameFrom = new HashMap<>();
//
//        for (UniversityPatch[] patchRow : startingPatch.getFloor().getPatches()) {
//            for (UniversityPatch patch : patchRow) {
//                gScores.put(patch, Double.MAX_VALUE);
//                fScores.put(patch, Double.MAX_VALUE);
//            }
//        }
//
//        gScores.put(startingPatch, 0.0);
//        fScores.put(
//                startingPatch,
//                Coordinates.distance(
//                        startingPatch,
//                        goalPatch
//                )
//        );
//
//        openSet.add(startingPatch);
//
//        while (!openSet.isEmpty()) {
//            UniversityPatch patchToExplore;
//
//            double minimumDistance = Double.MAX_VALUE;
//            UniversityPatch patchWithMinimumDistance = null;
//
//            for (UniversityPatch patchInQueue : openSet) {
//                double fScore = fScores.get(patchInQueue);
//
//                if (fScore < minimumDistance) {
//                    minimumDistance = fScore;
//                    patchWithMinimumDistance = patchInQueue;
//                }
//            }
//
//            patchToExplore = patchWithMinimumDistance;
//
//            if (patchToExplore.equals(goalPatch)) {
//                Stack<UniversityPatch> path = new Stack<>();
//                UniversityPatch currentPatch = goalPatch;
//
////                path.push(currentPatch);
//
//                while (cameFrom.containsKey(currentPatch)) {
//                    currentPatch = cameFrom.get(currentPatch);
//                    path.push(currentPatch);
//                }
//
////                path.pop();
//
//                return path;
//            }
//
//            openSet.remove(patchToExplore);
//
//            List<UniversityPatch> patchToExploreNeighbors = patchToExplore.getNeighbors();
//
//            for (UniversityPatch patchToExploreNeighbor : patchToExploreNeighbors) {
//                if (
//                        patchToExploreNeighbor.getAmenityBlock() == null
//                ) {
//                    double additionalWeights = 0;
//
///*                    // Add weights when passing through floor fields
//                    final double floorFieldWeight = 0.5;
//
//                    if (
//                            !(patchToExploreNeighbor.getFloorFieldValues().isEmpty()
//                                    || patchToExploreNeighbor.getFloorFieldValues().get(getGoalAmenityAsQueueable()) != null
//                                    && !patchToExploreNeighbor.getFloorFieldValues().get(getGoalAmenityAsQueueable()).isEmpty())
//                    ) {
//                        additionalWeights += floorFieldWeight;
//                    }*/
//
///*                    // Add weights when passing through patches with agents
//                    final double agentWeight = 1;
//
//                    additionalWeights += patchToExploreNeighbor.getAgents().size() * agentWeight;*/
//
//                    double tentativeGScore
//                            = gScores.get(patchToExplore)
//                            + Coordinates.distance(
//                            patchToExplore,
//                            patchToExploreNeighbor
//                    )
//                            + additionalWeights;
//
//                    if (tentativeGScore < gScores.get(patchToExploreNeighbor)) {
//                        cameFrom.put(patchToExploreNeighbor, patchToExplore);
//
//                        gScores.put(patchToExploreNeighbor, tentativeGScore);
//                        fScores.put(
//                                patchToExploreNeighbor,
//                                gScores.get(patchToExploreNeighbor)
//                                        + Coordinates.distance(
//                                        patchToExploreNeighbor,
//                                        goalPatch)
//                        );
//
//                        openSet.add(patchToExploreNeighbor);
//                    }
//                }
//            }
//        }
//
//        return null;
//    }
//
//    // Check whether the current goal amenity is a queueable or not
//    public boolean isNextAmenityQueueable() {
//        return Queueable.isQueueable(this.goalAmenity);
//    }
//
//    // Check whether the current goal amenity is a goal or not
//    public boolean isNextAmenityGoal() {
//        return Goal.isGoal(this.goalAmenity);
//    }
//
//    // Check whether the agent has just left the goal (if the agent is at a certain number of ticks since
//    // leaving the goal)
//    public boolean hasJustLeftGoal() {
//        final int hasJustLeftGoalThreshold = 3;
//
//        return this.timeSinceLeftPreviousGoal <= hasJustLeftGoalThreshold;
//    }
//
//    // Reset the agent's goal
//    public void resetGoal(boolean shouldStepForwardFirst) {
//        // Take note of the agent's goal patch, amenity (on that goal patch), and that amenity's attractor
//        this.goalPatch = null;
//        this.goalAmenity = null;
//        this.goalAttractor = null;
//
//        // Take note of the floor field state of this agent
//        this.goalFloorFieldState = null;
//
//        // Take note of the floor field of the agent's goal
//        this.goalFloorField = null;
//
//        // Take note of the agent's nearest queueing patch
//        this.goalNearestQueueingPatch = null;
//
//        // No agents have been encountered yet
//        this.hasEncounteredAgentToFollow = false;
//        this.hasEncounteredAnyQueueingAgent = false;
//
//        // This agent is not yet waiting
//        this.isWaitingOnAmenity = false;
//
//        // Set whether this agent is set to step forward
//        this.shouldStepForward = shouldStepForwardFirst;
//
//        // This agent is not following anyone yet
//        this.agentFollowedWhenAssembling = null;
//
//        // This agent hasn't moved yet
//        this.distanceMovedInTick = 0.0;
//
//        this.noMovementCounter = 0;
//        this.movementCounter = 0;
//
//        this.noNewPatchesSeenCounter = 0;
//        this.newPatchesSeenCounter = 0;
//
//        this.timeSinceLeftPreviousGoal = 0;
//
//        // This agent hasn't pathfound yet
//        this.willPathfind = false;
//        this.hasPathfound = false;
//
//        // This agent has no recent patches yet
//        this.recentPatches.clear();
//
//        // This agent is not yet stuck
//        this.free();
//    }
//
//    // Set the nearest goal to this agent
//    // That goal should also have the fewer agents queueing for it
//    // To determine this, for each two agents in the queue (or fraction thereof), a penalty of one tile is added to
//    // the distance to this goal
//    public void chooseGoal() {
//        // Only check the queue a certain percentage of the time
//        // Proceed anyway if no goals have been set yet
//        if (this.goalAmenity == null && this.goalAttractor == null) {
//            // TODO: consider amenities in next floor
//            // Based on the agent's current direction and route plan, get the next amenity class to be sought
//            Class<? extends Amenity> nextAmenityClass = this.routePlan.getCurrentAmenityClass();
//            List<? extends Amenity> amenityListInFloor = this.currentFloor.getAmenityList(nextAmenityClass);
//
//            double minimumScore = Double.MAX_VALUE;
//            Amenity chosenAmenity = null;
//            Amenity.AmenityBlock chosenAmenityBlock = null;
//
//            int agentsQueueing;
//            double score;
//
//            // From the amenity list, look for the nearest one to this agent
//            for (Amenity amenity : amenityListInFloor) {
//                // Within the amenity itself, see which attractor is closer to this agent
//                double minimumAttractorDistance = Double.MAX_VALUE;
//                Amenity.AmenityBlock nearestAttractor = null;
//
//                double attractorDistance;
//
//                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
//                    attractorDistance = Coordinates.distance(
//                            this.position,
//                            attractor.getPatch().getPatchCenterCoordinates()
//                    );
//
//                    if (attractorDistance < minimumAttractorDistance) {
//                        minimumAttractorDistance = attractorDistance;
//                        nearestAttractor = attractor;
//                    }
//                }
//
//                // Then measure the distance from the nearest attractor to this agent
//                if (amenity instanceof Queueable) {
//                    agentsQueueing
//                            = ((Queueable) amenity).getQueueObject().getAgentsQueueing().size();
//
//                    score = minimumAttractorDistance;
//
//                    if (
//                            amenity instanceof TicketBooth
//                                    || amenity instanceof Turnstile
//                    ) {
//                        score += agentsQueueing * 1.5;
//                    }
//                } else {
//                    score = minimumAttractorDistance;
//                }
//
//                if (score < minimumScore) {
//                    minimumScore = score;
//
//                    chosenAmenity = amenity;
//                    chosenAmenityBlock = nearestAttractor;
//                }
//            }
//
//            // Set the goal nearest to this agent
//            this.goalAmenity = chosenAmenity;
//            this.goalAttractor = chosenAmenityBlock;
//            this.goalPatch = chosenAmenityBlock.getPatch();
//        }
//    }
//
//    // Get the future position of this agent given the current goal, current heading, and the current walking
//    // distance
//    private Coordinates getFuturePosition() {
//        return getFuturePosition(this.goalAmenity, this.proposedHeading, this.preferredWalkingDistance);
//    }
//
//    // Get the future position of this agent given the current goal, current heading, and a given walking distance
//    private Coordinates getFuturePosition(double walkingDistance) {
//        return getFuturePosition(this.goalAmenity, this.proposedHeading, walkingDistance);
//    }
//
//    public Coordinates getFuturePosition(Coordinates startingPosition, double heading, double magnitude) {
//        return Coordinates.computeFuturePosition(startingPosition, heading, magnitude);
//    }
//
//    // Get the future position of this agent given a goal and a heading
//    public Coordinates getFuturePosition(Amenity goal, double heading, double walkingDistance) {
//        // Get the goal's floor
//        Floor goalFloor = goal.getAmenityBlocks().get(0).getPatch().getFloor();
//
//        // Get the nearest attractor to this agent
//        double minimumDistance = Double.MAX_VALUE;
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
//        // If the distance between this agent and the goal is less than the distance this agent covers every
//        // time it walks, "snap" the position of the agent to the center of the goal immediately, to avoid
//        // overshooting its target
//        // If not, compute the next coordinates normally
//        if (minimumDistance < walkingDistance) {
//            return new Coordinates(
//                    nearestAttractor.getPatch().getPatchCenterCoordinates().getX(),
//                    nearestAttractor.getPatch().getPatchCenterCoordinates().getY()
//            );
//        } else {
//            Coordinates futurePosition = this.getFuturePosition(
//                    this.position,
//                    heading,
//                    walkingDistance
//            );
//
//            double newX = futurePosition.getX();
//            double newY = futurePosition.getY();
//
//            // Check if the new coordinates are out of bounds
//            // If they are, adjust them such that they stay within bounds
//            if (newX < 0) {
//                newX = 0.0;
//            } else if (newX > goalFloor.getColumns() - 1) {
//                newX = goalFloor.getColumns() - 0.5;
//            }
//
//            if (newY < 0) {
//                newY = 0.0;
//            } else if (newY > goalFloor.getRows() - 1) {
//                newY = goalFloor.getRows() - 0.5;
//            }
//
//            return new Coordinates(newX, newY);
//        }
//    }
//
//    // Make the agent move in accordance with social forces
//    public boolean moveSocialForce() {
//        // The smallest repulsion a agent may inflict on another
//        final double minimumAgentRepulsion = 0.01 * this.preferredWalkingDistance;
//
//        // The smallest repulsion an obstacle may inflict to a agent
////        final double minimumObstacleRepulsion = 0.01 * this.preferredWalkingDistance;
//
//        // If the agent has not moved a sufficient distance for more than this number of ticks, the agent
//        // will be considered stuck
//        final int noMovementTicksThreshold = (this.getGoalAmenityAsGoal() != null) ? this.getGoalAmenityAsGoal().getWaitingTime() : 10;
//
//        // If the agent has not seen new patches for more than this number of ticks, the agent will be considered
//        // stuck
//        final int noNewPatchesSeenTicksThreshold = 10;
//
//        // If the agent has been moving a sufficient distance for at least this number of ticks, this agent will
//        // be out of the stuck state, if it was
//        final int unstuckTicksThreshold = 10;
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
//        // Get the relevant patches
//        List<UniversityPatch> patchesToExplore
//                = Floor.get7x7Field(this.currentPatch, this.proposedHeading, true, Math.toRadians(360.0));
//
//        this.toExplore = patchesToExplore;
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
///*        double accelerationFactor;
//
////        if (this.timeSinceLeftPreviousGoal <= this.ticksUntilFullyAccelerated) {
//        accelerationFactor = Math.sqrt(this.ticksAcceleratedOrMaintainedSpeed + 1) / (Math.sqrt(this.ticksUntilFullyAccelerated));
//*//*        } else {
//            accelerationFactor = Math.sqrt(this.newPatchesSeenCounter + 1) / (Math.sqrt(ticksUntilFullyAccelerated));
//        }*//*
//
//        System.out.println("this.currentWalkingDistance < previousWalkingDistance: " + this.currentWalkingDistance + ", " + this.ticksAcceleratedOrMaintainedSpeed + ": " + accelerationFactor);
//
//        accelerationFactor = Math.min(accelerationFactor, 1.0);
//
//        proposedNewPosition = this.getFuturePosition(
//                accelerationFactor
//                        * this.preferredWalkingDistance
//        );*/
//
//        // Check if the agent is set to take one initial step forward
//        if (!this.shouldStepForward) {
//            // Compute for the proposed future position
//            proposedNewPosition = this.getFuturePosition(this.preferredWalkingDistance);
//
//            // If this agent is queueing, the only social forces that apply are attractive forces to agents
//            // and obstacles (if not in queueing action)
//            if (this.state == State.IN_QUEUE) {
//                // Do not check for stuckness when already heading to the queueable
//                if (this.action != Action.HEADING_TO_QUEUEABLE) {
//                    // If the agent hasn't already been moving for a while, consider the agent stuck, and implement some
//                    // measures to free this agent
//                    if (
//                            this.isStuck
//                                    || (this.goalAttractor.getPatch().getAgents().isEmpty() && (this.isAtQueueFront() || this.isServicedByGoal())) && this.noMovementCounter > noMovementTicksThreshold
//                        /*&& this.parent.getTicketType() != TicketBooth.TicketType.STORED_VALUE*/
//                    ) {
//                        this.isStuck = true;
//                        this.stuckCounter++;
//                    }/* else {
//                        this.isReadyToFree = true;
//                    }*/
//                }
//
//                // Get the agents within the current field of view in these patches
//                // If there are any other agents within this field of view, this agent is at least guaranteed to
//                // slow down
//                TreeMap<Double, Agent> agentsWithinFieldOfView = new TreeMap<>();
//
//                // Look around the patches that fall on the agent's field of view
//                for (UniversityPatch patch : patchesToExplore) {
//                    // Do not apply social forces from obstacles if the agent is in the queueing action, i.e., when the
//                    // agent is following a floor field
//                    // If this patch has an obstacle, take note of it to add a repulsive force from it later
//                    if (this.action != Action.QUEUEING) {
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
//                            if (
//                                    distanceToObstacle <= slowdownStartDistance/*
//                                            && !patchAmenityBlock.isAttractor()*/
//                            ) {
//                                obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
//                            }
//                        }
//                    }
//
//                    if (!this.isStuck) {
//                        for (Agent otherAgent : patch.getAgents()) {
//                            // Make sure that the agent discovered isn't itself
//                            if (!otherAgent.equals(this.getParent())) {
//                                if (!this.hasEncounteredAnyQueueingAgent && otherAgent.getAgentMovement().getState() == State.IN_QUEUE) {
//                                    this.hasEncounteredAnyQueueingAgent = true;
//                                }
//
//                                if (
//                                        otherAgent.getAgentMovement().getState() == State.WALKING
//                                                || this.action != Action.HEADING_TO_QUEUEABLE
//                                                && otherAgent.getAgentMovement().getGoalAmenity() != null && otherAgent.getAgentMovement().getGoalAmenity().equals(this.getGoalAmenity())
//                                                && (this.agentFollowedWhenAssembling == null || this.agentFollowedWhenAssembling.equals(otherAgent))
//                                ) {
//                                    // Take note of the agent density in this area
//                                    numberOfAgents++;
//
//                                    // Check if this agent is within the field of view and within the slowdown distance
//                                    double distanceToAgent = Coordinates.distance(
//                                            this.position,
//                                            otherAgent.getAgentMovement().getPosition()
//                                    );
//
//                                    if (Coordinates.isWithinFieldOfView(
//                                            this.position,
//                                            otherAgent.getAgentMovement().getPosition(),
//                                            this.proposedHeading,
//                                            this.fieldOfViewAngle)
//                                            && distanceToAgent <= slowdownStartDistance) {
//                                        agentsWithinFieldOfView.put(distanceToAgent, otherAgent);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // Compute the perceived density of the agents
//                // Assuming the maximum density a agent sees within its environment is 3 before it thinks the crowd
//                // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
//                // maximum tolerated number of agents
//                final double maximumDensityTolerated = 3.0;
//                final double agentDensity
//                        = (numberOfAgents > maximumDensityTolerated ? maximumDensityTolerated : numberOfAgents)
//                        / maximumDensityTolerated;
//
//                // For each agent found within the slowdown distance, get the nearest one, if there is any
//                Map.Entry<Double, Agent> nearestAgentEntry = agentsWithinFieldOfView.firstEntry();
//
//                // If there are no agents within the field of view, good - move normally
//                if (nearestAgentEntry == null/*|| nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity() != null && !nearestAgentEntry.getValue().getAgentMovement().getGoalAmenity().equals(this.goalAmenity)*/) {
//                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
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
//                } else {
//                    // Check the distance of that nearest agent to this agent
//                    double distanceToNearestAgent = nearestAgentEntry.getKey();
//
//                    // Modify the maximum stopping distance depending on the density of the environment
//                    // That is, the denser the surroundings, the less space this agent will allow between other
//                    // agents
//                    maximumStopDistance -= (maximumStopDistance - minimumStopDistance) * agentDensity;
//
//                    this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//                    // Else, just slow down and move towards the direction of that agent in front
//                    // The slowdown factor linearly depends on the distance between this agent and the other
//                    final double slowdownFactor
//                            = (distanceToNearestAgent - maximumStopDistance)
//                            / (slowdownStartDistance - maximumStopDistance);
//
//                    double computedWalkingDistance = slowdownFactor * this.preferredWalkingDistance;
//
//                    Coordinates revisedPosition = this.getFuturePosition(computedWalkingDistance);
//
//                    // Get the attractive force of this agent to the new position
//                    this.attractiveForce = this.computeAttractiveForce(
//                            new Coordinates(this.position),
//                            this.proposedHeading,
//                            revisedPosition,
//                            computedWalkingDistance
//                    );
//
//                    vectorsToAdd.add(attractiveForce);
//                }
//            } else {
//                // If the agent hasn't already been moving for a while, consider the agent stuck, and implement some
//                // measures to free this agent
//                if (this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold) {
//                    this.isStuck = true;
//                    this.stuckCounter++;
//                }
//
//                boolean hasEncounteredQueueingAgentInLoop = false;
//
//                // Only apply the social forces of a set number of agents and obstacles
//                int agentsProcessed = 0;
//                final int agentsProcessedLimit = 5;
//
//                // Look around the patches that fall on the agent's field of view
//                for (UniversityPatch patch : patchesToExplore) {
//                    // If this patch has an obstacle, take note of it to add a repulsive force from it later
//                    Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();
//
//                    // Get the distance between this agent and the obstacle on this patch
//                    if (hasObstacle(patch)) {
//                        // Take note of the obstacle density in this area
//                        numberOfObstacles++;
//
//                        // If the distance is less than or equal to the specified minimum repulsion distance, compute
//                        // for the magnitude of the repulsion force
//                        double distanceToObstacle = Coordinates.distance(
//                                this.position,
//                                patchAmenityBlock.getPatch().getPatchCenterCoordinates()
//                        );
//
//                        if (
//                            /*Coordinates.isWithinFieldOfView(
//                                    this.position,
//                                    patchAmenityBlock.getPatch().getPatchCenterCoordinates(),
//                                    this.proposedHeading,
//                                    Math.toRadians(fieldOfViewAngleDegrees))
//                                    && */distanceToObstacle <= slowdownStartDistance/*
//                                && !patchAmenityBlock.isAttractor()*/
//                        ) {
//                            obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
//                        }
//                    }
//
//                    // Inspect each agent in each patch in the patches in the field of view
//                    for (Agent otherAgent : patch.getAgents()) {
//                        if (agentsProcessed == agentsProcessedLimit) {
//                            break;
//                        }
//
//                        // Make sure that the agent discovered isn't itself
//                        if (!otherAgent.equals(this.getParent())) {
//                            // Take note of the agent density in this area
//                            numberOfAgents++;
//
//                            // Get the distance between this agent and the other agent
//                            double distanceToOtherAgent = Coordinates.distance(
//                                    this.position,
//                                    otherAgent.getAgentMovement().getPosition()
//                            );
//
//                            // If the distance is less than or equal to the distance when repulsion is supposed to kick in,
//                            // compute for the magnitude of that repulsion force
//                            if (distanceToOtherAgent <= slowdownStartDistance) {
//                                // Compute the perceived density of the agents
//                                // Assuming the maximum density a agent sees within its environment is 3 before it thinks the crowd
//                                // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
//                                // maximum tolerated number of agents
//                                final int maximumAgentCountTolerated = 5;
//
//                                // The distance by which the repulsion starts to kick in will depend on the density of the agent's
//                                // surroundings
//                                final int minimumObstacleCount = 1;
//                                final double maximumDistance = 2.0;
//                                final int maximumObstacleCount = 2;
//                                final double minimumDistance = 0.7;
//
//                                double computedMaximumDistance = computeMaximumRepulsionDistance(
//                                        numberOfObstacles,
//                                        maximumAgentCountTolerated,
//                                        minimumObstacleCount,
//                                        maximumDistance,
//                                        maximumObstacleCount,
//                                        minimumDistance
//                                );
//
//                                Vector agentRepulsiveForce = computeSocialForceFromAgent(
//                                        otherAgent,
//                                        distanceToOtherAgent,
//                                        computedMaximumDistance,
//                                        minimumAgentStopDistance,
//                                        this.preferredWalkingDistance
//                                );
//
//                                // Add the computed vector to the list of vectors
//                                this.repulsiveForceFromAgents.add(agentRepulsiveForce);
//
//                                // Also, check this agent's state
//                                // If this agent is queueing, set the relevant variable - it will stay true even if just
//                                // one nearby agent has activated it
//                                if (!hasEncounteredQueueingAgentInLoop) {
//                                    // Check if the other agent is in a queueing or assembling with the same goal as
//                                    // this agent
//                                    if (this.agentFollowedWhenAssembling == null) {
//                                        this.hasEncounteredAgentToFollow = false;
//                                    } else {
//                                        if (this.agentFollowedWhenAssembling.equals(otherAgent)) {
//                                            // If the other agent encountered is already assembling, decide whether this
//                                            // agent will assemble too depending on whether the other agent was selected
//                                            // to be followed by this one
//                                            this.hasEncounteredAgentToFollow
//                                                    = (otherAgent.getAgentMovement().getAction() == Action.ASSEMBLING
//                                                    || otherAgent.getAgentMovement().getAction() == Action.QUEUEING)
//                                                    && otherAgent.getAgentMovement().getGoalAmenity().equals(this.goalAmenity);
//                                        } else {
//                                            this.hasEncounteredAgentToFollow = false;
//                                        }
//                                    }
//                                }
//
//                                // If a queueing agent has been encountered, do not pathfind anymore for for this
//                                // goal
//                                if (
//                                        this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
//                                                && this.hasEncounteredAgentToFollow
//                                ) {
//                                    this.hasPathfound = true;
//                                }
//
//                                hasEncounteredQueueingAgentInLoop
//                                        = this.hasEncounteredAgentToFollow;
//
//                                agentsProcessed++;
//                            }
//                        }
//                    }
//                }
//
//                // Get the attractive force of this agent to the new position
//                this.attractiveForce = this.computeAttractiveForce(
//                        new Coordinates(this.position),
//                        this.proposedHeading,
//                        proposedNewPosition,
//                        this.preferredWalkingDistance
//                );
//
//                vectorsToAdd.add(attractiveForce);
//            }
//        } else {
//            double newHeading;
//
//            if (this.currentPatch.getAmenityBlock() != null && this.currentPatch.getAmenityBlock().getParent() instanceof Turnstile) {
//                // First, get the apex of the floor field with the state of the agent
//                Turnstile turnstile = (Turnstile) this.currentPatch.getAmenityBlock().getParent();
//
//                QueueingFloorField.FloorFieldState floorFieldState;
//                UniversityPatch apexLocation;
//
//                if (this.direction == Direction.BOARDING) {
//                    floorFieldState = turnstile.getTurnstileFloorFieldStateBoarding();
//                } else {
//                    floorFieldState = turnstile.getTurnstileFloorFieldStateAlighting();
//                }
//
//                apexLocation = turnstile.getQueueObject().getFloorFields().get(floorFieldState).getApices().get(0);
//
//                // Then compute the heading from the apex to the turnstile attractor
//                newHeading = Coordinates.headingTowards(
//                        apexLocation.getPatchCenterCoordinates(),
//                        turnstile.getAttractors().get(0).getPatch().getPatchCenterCoordinates()
//                );
//            } else {
//                newHeading = this.previousHeading;
//            }
//
//            // Compute for the proposed future position
//            proposedNewPosition = this.getFuturePosition(
//                    this.position,
//                    newHeading,
//                    this.preferredWalkingDistance
//            );
//
//            this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
//
//            // Get the attractive force of this agent to the new position
//            this.attractiveForce = this.computeAttractiveForce(
//                    new Coordinates(this.position),
//                    newHeading,
//                    proposedNewPosition,
//                    this.preferredWalkingDistance
//            );
//
//            vectorsToAdd.add(attractiveForce);
//
//            // Do not automatically (without influence from social forces of surrounding agents and obstacles step
//            // forward again for now
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
//        if (partialMotivationForce != null) {
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
//            final int obstaclesProcessedLimit = 5;
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
////                            System.out.println(this.parent.getIdentifier() + " activated");
//
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
//                            freeSpaceFound = hasClearLineOfSight(this.position, newFuturePosition, false);
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
//                    // Do not not count for movements/non-movements when the agent is in the "in queue" state
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
///*                    if (this.isStuck && !((this.goalAttractor.getPatch().getAgents().isEmpty() && (this.isAtQueueFront() || this.isServicedByGoal())) && this.noMovementCounter > noMovementTicksThreshold)) {
//                        this.isReadyToFree = true;
//                    }*/
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
//    private Vector computeAttractiveForce(
//            final Coordinates startingPosition,
//            final double proposedHeading,
//            final Coordinates proposedNewPosition,
//            final double preferredWalkingDistance
//    ) {
//        // Compute for the attractive force
//        Vector attractiveForce = new Vector(
//                startingPosition,
//                proposedHeading,
//                proposedNewPosition,
//                preferredWalkingDistance
//        );
//
//        return attractiveForce;
//    }
//
//    private double computeMaximumRepulsionDistance(
//            int objectCount,
//            final int maximumObjectCountTolerated,
//            final int minimumObjectCount,
//            final double maximumDistance,
//            final int maximumObjectCount,
//            final double minimumDistance
//    ) {
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
//    private double computeRepulsionMagnitudeFactor(
//            final double distance,
//            final double maximumDistance,
//            final double minimumRepulsionFactor,
//            final double minimumDistance,
//            final double maximumRepulsionFactor
//    ) {
//        // Formula: for the inverse square law equation y = a / x ^ 2 + b,
//        // a = (d_max ^ 2 * (r_min * d_max ^ 2 - r_min * d_min ^ 2 + r_max ^ 2 * d_min ^ 2)) / (d_max ^ 2 - d_min ^ 2)
//        // and
//        // b = -((r_max ^ 2 * d_min ^ 2) / (d_max ^ 2 - d_min ^ 2))
//        double differenceOfSquaredDistances = Math.pow(maximumDistance, 2.0) - Math.pow(minimumDistance, 2.0);
//        double productOfMaximumRepulsionAndMinimumDistance
//                = Math.pow(maximumRepulsionFactor, 2.0) * Math.pow(minimumDistance, 2.0);
//
//        double a
//                = (
//                Math.pow(maximumDistance, 2.0) * (minimumRepulsionFactor * Math.pow(maximumDistance, 2.0)
//                        - minimumRepulsionFactor * Math.pow(minimumDistance, 2.0)
//                        + productOfMaximumRepulsionAndMinimumDistance
//                )) / differenceOfSquaredDistances;
//
//        double b = -(productOfMaximumRepulsionAndMinimumDistance / differenceOfSquaredDistances);
//
//        double repulsion = a / Math.pow(distance, 2.0) + b;
//
//        // The repulsion value should always be greater or equal to zero
//        if (repulsion <= 0.0) {
//            repulsion = 0.0;
//        }
//
//        return repulsion;
//    }
//
//    private Vector computeSocialForceFromAgent(
//            Agent agent,
//            final double distanceToOtherAgent,
//            final double maximumDistance,
//            final double minimumDistance,
//            final double maximumMagnitude
//    ) {
//        final double maximumRepulsionFactor = 1.0;
//        final double minimumRepulsionFactor = 0.0;
//
//        Coordinates agentPosition = agent.getAgentMovement().getPosition();
//
//        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at
//        // that minimum
//        double modifiedDistanceToObstacle = Math.max(distanceToOtherAgent, minimumDistance);
//
//        double repulsionMagnitudeCoefficient;
//        double repulsionMagnitude;
//
//        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(
//                modifiedDistanceToObstacle,
//                maximumDistance,
//                minimumRepulsionFactor,
//                minimumDistance,
//                maximumRepulsionFactor
//        );
//
//        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;
//
//        // If a agent is stuck, do not exert much force from this agent
//        if (this.isStuck) {
//            final double factor = 0.01;
//
//            repulsionMagnitude -= this.stuckCounter * factor;
//
//            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
//                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
//            }
//        }
//
//        // Then compute the heading from that other agent to this agent
//        double headingFromOtherAgent = Coordinates.headingTowards(
//                agentPosition,
//                this.position
//        );
//
//        // Then compute for a future position given the other agent's position, the heading, and the
//        // magnitude
//        // This will be used as the endpoint of the repulsion vector from this obstacle
//        Coordinates agentRepulsionVectorFuturePosition = this.getFuturePosition(
//                agentPosition,
//                headingFromOtherAgent,
//                repulsionMagnitude
//        );
//
//        // Finally, given the current position, heading, and future position, create the vector from
//        // the other agent to the current agent
//        return new Vector(
//                agentPosition,
//                headingFromOtherAgent,
//                agentRepulsionVectorFuturePosition,
//                repulsionMagnitude
//        );
//    }
//
//    private Vector computeSocialForceFromObstacle(
//            Amenity.AmenityBlock amenityBlock,
//            final double distanceToObstacle,
//            final double maximumDistance,
//            double minimumDistance,
//            final double maximumMagnitude
//    ) {
//        final double maximumRepulsionFactor = 1.0;
//        final double minimumRepulsionFactor = 0.0;
//
//        Coordinates repulsionVectorStartingPosition = amenityBlock.getPatch().getPatchCenterCoordinates();
//
//        // If this agent is closer than the minimum distance specified, apply a force as if the distance is just at
//        // that minimum
//        double modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
//
//        double repulsionMagnitudeCoefficient;
//        double repulsionMagnitude;
//
//        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(
//                modifiedDistanceToObstacle,
//                maximumDistance,
//                minimumRepulsionFactor,
//                minimumDistance,
//                maximumRepulsionFactor
//        );
//
//        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;
//
//        // If a agent is stuck, do not exert much force from this obstacle
//        if (this.isStuck) {
//            final double factor = 0.01;
//
//            repulsionMagnitude -= this.stuckCounter * factor;
//
//            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
//                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
//            }
//        }
//
///*        // Get the potential origins of the two repulsion vectors
//        Coordinates xAxisOrigin
//                = new Coordinates(
//                this.position.getX(),
//                amenityBlock.getPatch().getPatchCenterCoordinates().getY()
//        );
//
//        Coordinates yAxisOrigin
//                = new Coordinates(amenityBlock.getPatch().getPatchCenterCoordinates().getX(), this.position.getY());
//
//        // Get the distances between these origins and this agent's position
//        double xAxisOriginDistance = Math.abs(xAxisOrigin.getY() - this.position.getY());
//        double yAxisOriginDistance = Math.abs(yAxisOrigin.getX() - this.position.getX());
//
//        // Get whichever is the larger of these two distances - this will be the starting position of the vector
//        Coordinates repulsionVectorStartingPosition;
//
//        if (xAxisOriginDistance >= yAxisOriginDistance) {
//            repulsionVectorStartingPosition = xAxisOrigin;
//        } else {
//            repulsionVectorStartingPosition = yAxisOrigin;
//        }*/
//
//        // Compute the heading from that origin point to this agent
//        double headingFromOtherObstacle = Coordinates.headingTowards(
//                repulsionVectorStartingPosition,
//                this.position
//        );
//
//        // Then compute for a future position given the obstacle's position, the heading, and the
//        // magnitude
//        // This will be used as the endpoint of the repulsion vector from this obstacle
//        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(
//                repulsionVectorStartingPosition,
//                headingFromOtherObstacle,
//                repulsionMagnitude
//        );
//
//        // Finally, given the current position, heading, and future position, create the vector from
//        // the obstacle to the current agent
//        return new Vector(
//                repulsionVectorStartingPosition,
//                headingFromOtherObstacle,
//                obstacleRepulsionVectorFuturePosition,
//                repulsionMagnitude
//        );
//    }
//
//    // Make the agent move given a walking distance
//    private void move(double walkingDistance) {
//        this.setPosition(this.getFuturePosition(walkingDistance));
//    }
//
//    // Make the agent move given the future position
//    private void move(Coordinates futurePosition) {
//        this.setPosition(futurePosition);
//    }
//
//    // Check if this agent has reached its goal's queueing floor field
//    public boolean hasReachedQueueingFloorField() {
//        for (UniversityPatch patch : this.goalFloorField.getAssociatedPatches()) {
//            if (isOnOrCloseToPatch(patch)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    // Check if this agent has a path to follow
//    public boolean hasPath() {
//        return this.currentPath != null;
//    }
//
//    // Check if this agent is on the next patch of its path
//    public boolean hasReachedNextPatchInPath() {
//        return isOnOrCloseToPatch(this.currentPath.peek());
//    }
//
//    // Register this agent to its queueable goal's queue
//    public void joinQueue() {
//        this.getGoalAmenityAsQueueable().getQueueObject().getAgentsQueueing().addLast(this.parent);
//    }
//
//    // Have the agent stop
//    public void stop() {
//        this.currentWalkingDistance = 0.0;
//    }
//
//    // Unregister this agent to its queueable goal's queue
//    public void leaveQueue() {
//        this.getGoalAmenityAsQueueable().getQueueObject().getAgentsQueueing().remove(this.parent);
//    }
//
//    // Check if this agent has reached an apex of its floor field
//    public boolean hasReachedQueueingFloorFieldApex() {
//        // If the agent is in any of this floor field's apices, return true
//        for (UniversityPatch apex : this.goalFloorField.getApices()) {
//            if (isOnOrCloseToPatch(apex)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    // Have this agent start waiting for an amenity to become vacant
//    public void beginWaitingOnAmenity() {
//        this.isWaitingOnAmenity = true;
//    }
//
//    // Check if the goal of this agent is currently not servicing anyone
//    public boolean isGoalFree() {
//        return this.getGoalAmenityAsQueueable().getQueueObject().getAgentServiced() == null;
//    }
//
//    // Check if this agent the one currently served by its goal
//    public boolean isServicedByGoal() {
//        Agent agentServiced = this.getGoalAmenityAsQueueable().getQueueObject().getAgentServiced();
//
//        return agentServiced != null && agentServiced.equals(this.parent);
//    }
//
//    // Check if this agent is at the front of the queue
//    public boolean isAtQueueFront() {
//        LinkedList<Agent> agentsQueueing
//                = this.getGoalAmenityAsQueueable().getQueueObject().getAgentsQueueing();
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
//                UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS * 0.1
//        );
//
//        this.setPosition(offsetPatchCenter);
//
//        this.currentAmenity = this.goalAmenity;
//    }
//
//    // Set the agent's next patch in its current path as it reaches it
//    public void reachPatchInPath() {
//        this.currentPath.pop();
//    }
//
//    // Have this agent's goal service this agent
//    public void beginServicingThisAgent() {
//        // This agent will now be the one to be served next
//        this.getGoalAmenityAsQueueable().getQueueObject().setAgentServiced(this.parent);
//    }
//
//    // Have this agent's goal finish serving this agent
//    public void endServicingThisAgent() {
//        // This agent is done being serviced by this goal
//        this.getGoalAmenityAsQueueable().getQueueObject().setAgentServiced(null);
//    }
//
//    // Check if this agent has reached its final goal
//    public boolean hasReachedFinalGoal() {
//        return !this.routePlan.getCurrentRoutePlan().hasNext();
//    }
//
//    // Check if this agent has reached the final patch in its current path
//    public boolean hasAgentReachedFinalPatchInPath() {
//        return this.currentPath.isEmpty();
//    }
//
//    // Check if this agent has reached the specified patch
//    private boolean isOnPatch(UniversityPatch patch) {
//        return ((int) (this.position.getX() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getColumn()
//                && ((int) (this.position.getY() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getRow();
//    }
//
//    // Check if this agent is adequately close enough to a patch
//    // In this case, a agent is close enough to a patch when the distance between this agent and the patch is
//    // less than the distance covered by the agent per second
//    private boolean isOnOrCloseToPatch(UniversityPatch patch) {
//        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
//    }
//
//    // Check if this agent is allowed by its goal to pass
//    public boolean isAllowedPass() {
//        return this.getGoalAmenityAsGoal().allowPass();
//    }
//
//    // Check if this agent will enter the train
//    public boolean willEnterTrain() {
//        TrainDoor closestTrainDoor = getGoalAmenityAsTrainDoor();
//
//        if (closestTrainDoor != null) {
//            return isTrainDoorOpen(closestTrainDoor);
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
//    // Despawn this agent
//    public void despawnAgent() {
//        // Remove the agent from its patch
//        this.currentPatch.getAgents().remove(this.parent);
//
//        // Remove this agent from this floor
//        this.currentFloor.getAgentsInFloor().remove(this.parent);
//
//        // Remove this agent from this station
//        this.currentFloor.getStation().getAgentsInStation().remove(this.parent);
//
//        // Remove this agent from its current floor's patch set, if necessary
//        SortedSet<UniversityPatch> currentPatchSet = this.currentPatch.getFloor().getAgentPatchSet();
//
//        if (
//                currentPatchSet.contains(this.currentPatch)
//                        && this.currentPatch.getAgents().isEmpty()
//        ) {
//            currentPatchSet.remove(this.currentPatch);
//        }
//    }
//
//    // Have the agent face its current goal, or its queueing area, or the agent at the end of the queue
//    public void faceNextPosition() {
//        double newHeading;
//        boolean willFaceQueueingPatch;
//        UniversityPatch proposedGoalPatch;
//
//        // iI the agent is already heading for a queueable, no need to seek its floor fields again, as
//        // it has already done so, and is now just heading to the goal itself
//        // If it has floor fields, get the heading towards the nearest floor field value
//        // If it doesn't have floor fields, just get the heading towards the goal itself
//        if (this.action != Action.HEADING_TO_QUEUEABLE && this.goalAmenity instanceof Queueable) {
//            // If a queueing patch has not yet been set for this goal, set it
//            if (this.goalNearestQueueingPatch == null) {
//                // If the next floor field has not yet been set for this queueing patch, set it
//                if (this.goalFloorFieldState == null && this.goalFloorField == null) {
//                    Queueable queueable = this.getGoalAmenityAsQueueable();
//
//                    if (queueable instanceof TrainDoor) {
//                        TrainDoor trainDoor = (TrainDoor) queueable;
//
//                        // If the next goal is a train door, pick one of the left and right entrances
//                        // Choose whichever is closest and has the least agents queueing
//                        QueueObject chosenEntrance;
//                        TrainDoor.TrainDoorEntranceLocation chosenLocation;
//
//                        QueueObject leftEntrance
//                                = ((TrainDoor) queueable)
//                                .getQueueObjects().get(TrainDoor.TrainDoorEntranceLocation.LEFT);
//
//                        QueueObject rightEntrance
//                                = ((TrainDoor) queueable)
//                                .getQueueObjects().get(TrainDoor.TrainDoorEntranceLocation.RIGHT);
//
//                        double leftEntranceAgentsQueueing = leftEntrance.getAgentsQueueing().size();
//                        double rightEntranceAgentsQueueing = rightEntrance.getAgentsQueueing().size();
//
//                        // Choose the entrance with the least agents
//                        if (leftEntranceAgentsQueueing < rightEntranceAgentsQueueing) {
//                            chosenEntrance = leftEntrance;
//                            chosenLocation = TrainDoor.TrainDoorEntranceLocation.LEFT;
//
//                            this.goalAttractor = trainDoor.getAttractors().get(0);
//                        } else if (leftEntranceAgentsQueueing > rightEntranceAgentsQueueing) {
//                            chosenEntrance = rightEntrance;
//                            chosenLocation = TrainDoor.TrainDoorEntranceLocation.RIGHT;
//
//                            this.goalAttractor = trainDoor.getAttractors().get(0);
//                        } else {
//                            // If thw queue lengths are equal, pick one randomly
//                            if (Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean()) {
//                                chosenEntrance = leftEntrance;
//                                chosenLocation = TrainDoor.TrainDoorEntranceLocation.LEFT;
//
//                                this.goalAttractor = trainDoor.getAttractors().get(0);
//                            } else {
//                                chosenEntrance = rightEntrance;
//                                chosenLocation = TrainDoor.TrainDoorEntranceLocation.RIGHT;
//
//                                this.goalAttractor = trainDoor.getAttractors().get(1);
//                            }
//                        }
//
//                        this.goalFloorFieldState = new PlatformFloorField.PlatformFloorFieldState(
//                                this.direction,
//                                State.IN_QUEUE,
//                                this.getGoalAmenityAsQueueable(),
//                                chosenLocation
//                        );
//
//                        this.goalFloorField = queueable.retrieveFloorField(
//                                chosenEntrance,
//                                this.goalFloorFieldState
//                        );
//                    } else {
//                        this.goalFloorFieldState = new QueueingFloorField.FloorFieldState(
//                                this.direction,
//                                State.IN_QUEUE,
//                                this.getGoalAmenityAsQueueable()
//                        );
//
//                        this.goalFloorField = queueable.retrieveFloorField(
//                                queueable.getQueueObject(),
//                                this.goalFloorFieldState
//                        );
//                    }
//                }
//
//                this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
//                proposedGoalPatch = this.goalNearestQueueingPatch;
//            }
//
//            // If this agent is in the "will queue" state, choose between facing the queueing patch, and facing the
//            // agent at the back of the queue
//            if (action == Action.WILL_QUEUE || action == Action.ASSEMBLING) {
//                LinkedList<Agent> agentQueue
//                        = ((Queueable) this.goalAmenity).getQueueObject().getAgentsQueueing();
//
//                // Check whether there are agents queueing for the goal
//                if (agentQueue.isEmpty()) {
//                    // If there are no agents queueing yet, simply compute the heading towards the nearest queueing
//                    // patch
//                    this.agentFollowedWhenAssembling = null;
//                    this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
//                    proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                    willFaceQueueingPatch = true;
//                } else {
//                    Agent agentFollowedCandidate;
//
//                    // If there are agents queueing, join the queue and follow either the last person in the queue
//                    // or the person before this
//                    if (action == Action.WILL_QUEUE) {
//                        agentFollowedCandidate = agentQueue.getLast();
//                    } else {
//                        int agentFollowedCandidateIndex = agentQueue.indexOf(this.parent) - 1;
//
//                        if (agentFollowedCandidateIndex >= 0) {
//                            agentFollowedCandidate
//                                    = agentQueue.get(agentFollowedCandidateIndex);
//                        } else {
//                            agentFollowedCandidate = null;
//                        }
//                    }
//
//                    // But if the person to be followed is this person itself, or is not assembling, or follows this
//                    // person too (forming a cycle), disregard it, and just follow the queueing patch
//                    // Otherwise, follow that agent
//                    if (
//                            agentFollowedCandidate == null
//                                    || agentFollowedCandidate.equals(this.parent)
//                                    || !agentFollowedCandidate.equals(this.parent)
//                                    && agentFollowedCandidate.getAgentMovement()
//                                    .getAgentFollowedWhenAssembling() != null
//                                    && agentFollowedCandidate.getAgentMovement()
//                                    .getAgentFollowedWhenAssembling().equals(this.parent)
//                    ) {
//                        this.agentFollowedWhenAssembling = null;
//                        this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
//                        proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                        willFaceQueueingPatch = true;
//                    } else {
//                        // But only follow agents who are nearer to this agent than to the chosen queueing
//                        // patch and are within this agent's walking distance and have a clear line of sight to
//                        // this agent
//                        if (
//                                !hasClearLineOfSight(this.position, agentFollowedCandidate.getAgentMovement().getPosition(), true)
//                        ) {
//                            this.agentFollowedWhenAssembling = null;
//                            this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
//                            proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                            willFaceQueueingPatch = true;
//                        } else {
//                            this.agentFollowedWhenAssembling = agentFollowedCandidate;
//                            proposedGoalPatch = this.goalNearestQueueingPatch;
//
//                            willFaceQueueingPatch = false;
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
//        if (this.currentPath == null) {
//            // Head towards the queue of the goal
//            LinkedList<Agent> agentsQueueing
//                    = this.getGoalAmenityAsQueueable().getQueueObject().getAgentsQueueing();
//
//            // If there are no agents in that queue at all, simply head for the goal patch
//            if (agentsQueueing.isEmpty()) {
//                this.currentPath = computePath(
//                        this.currentPatch,
//                        this.goalPatch
//                );
//            } else {
//                // If there are agents in the queue, this agent should only follow the last agent in that
//                // queue if that agent is assembling
//                // If the last agent is not assembling, simply head for the goal patch instead
//                Agent lastAgent = agentsQueueing.getLast();
//
//                if (lastAgent.getAgentMovement().getAction() == Action.ASSEMBLING) {
//                    this.currentPath = computePath(
//                            this.currentPatch,
//                            lastAgent.getAgentMovement().getCurrentPatch()
//                    );
//                } else {
//                    this.currentPath = computePath(
//                            this.currentPatch,
//                            this.goalPatch
//                    );
//                }
//            }
//        }
//
//        // Get the first patch still unvisited in the path
//        if (this.currentPath == null || this.currentPath.isEmpty()) {
//            return false;
//        }
//
//        this.goalPatch = this.currentPath.peek();
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
//    public UniversityPatch getPatchWithNearestFloorFieldValue() {
//        final double maximumFloorFieldValueThreshold = 0.8;
//
//        // Get the patches associated with the current goal
//        List<UniversityPatch> associatedPatches = this.goalFloorField.getAssociatedPatches();
//
//        double minimumDistance = Double.MAX_VALUE;
//        UniversityPatch nearestPatch = null;
//
//        // Look for the nearest patch from the patches associated with the floor field
//        double distanceFromAgent;
//
//        for (UniversityPatch patch : associatedPatches) {
//            double floorFieldValue
//                    = patch.getFloorFieldValues().get(this.getGoalAmenityAsQueueable()).get(this.goalFloorFieldState);
//
////            if (floorFieldValue <= maximumFloorFieldValueThreshold) {
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
//    // Get the next queueing patch in a floor field given the current floor field state
//    private UniversityPatch getBestQueueingPatch() {
//        // Get the patches to explore
//        List<UniversityPatch> patchesToExplore
//                = Floor.get7x7Field(this.currentPatch, this.proposedHeading, true, this.fieldOfViewAngle);
//
//        this.toExplore = patchesToExplore;
//
//        // Collect the patches with the highest floor field values
//        List<UniversityPatch> highestPatches = new ArrayList<>();
//
//        double maximumFloorFieldValue = 0.0;
//
//        for (UniversityPatch patch : patchesToExplore) {
//            Map<QueueingFloorField.FloorFieldState, Double> floorFieldStateDoubleMap
//                    = patch.getFloorFieldValues().get(this.getGoalAmenityAsQueueable());
//
//            if (
//                    !patch.getFloorFieldValues().isEmpty()
//                            && floorFieldStateDoubleMap != null
//                            && !floorFieldStateDoubleMap.isEmpty()
//                            && floorFieldStateDoubleMap.get(
//                            this.goalFloorFieldState
//                    ) != null
//            ) {
//                double floorFieldValue = patch.getFloorFieldValues()
//                        .get(this.getGoalAmenityAsQueueable())
//                        .get(this.goalFloorFieldState);
//
//                if (floorFieldValue >= maximumFloorFieldValue) {
//                    if (floorFieldValue > maximumFloorFieldValue) {
//                        maximumFloorFieldValue = floorFieldValue;
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
//        if (maximumFloorFieldValue == 0.0) {
//            return null;
//        }
//
//        // If there are more than one highest valued-patches, choose the one where it would take the least heading
//        // difference
//        UniversityPatch chosenPatch = highestPatches.get(0)/* = null*/;
//
//        List<Double> headingChanges = new ArrayList<>();
////        List<Double> distances = new ArrayList<>();
//
//        double headingToHighestPatch;
//        double headingChangeRequired;
//
////        double distance;
//
//        for (UniversityPatch patch : highestPatches) {
//            headingToHighestPatch = Coordinates.headingTowards(this.position, patch.getPatchCenterCoordinates());
//            headingChangeRequired = Coordinates.headingDifference(this.proposedHeading, headingToHighestPatch);
//
//            double headingChangeRequiredDegrees = Math.toDegrees(headingChangeRequired);
//
//            headingChanges.add(headingChangeRequiredDegrees);
//
///*            distance = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
//
//            distances.add(distance);*/
//        }
//
//        double minimumHeadingChange = Double.MAX_VALUE;
//
//        for (int index = 0; index < highestPatches.size(); index++) {
////            double individualScore = headingChanges.get(index) * 1.0 + (distances.get(index) * 10.0) * 0.0;
//            double individualScore = headingChanges.get(index);
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
//    // Check if the given patch has an obstacle
//    private boolean hasObstacle(UniversityPatch patch) {
//        Amenity.AmenityBlock amenityBlock = patch.getAmenityBlock();
//
//        if (amenityBlock == null) {
//            return false;
//        } else {
//            Amenity parent = amenityBlock.getParent();
//
//            if (parent.equals(this.goalAmenity)) {
//                return !amenityBlock.isAttractor();
//            } else {
//                if (parent instanceof Gate) {
//                    return !amenityBlock.isAttractor();
//                } else {
//                    return true;
//                }
//            }
//        }
//
///*        return amenityBlock != null
//                && (
//                !amenityBlock.getParent().equals(this.goalAmenity)
//                        && (
//                        !(amenityBlock.getParent() instanceof Gate)
//                                || (amenityBlock.getParent() instanceof Gate) && !amenityBlock.isAttractor()
//                )
//        );*/
//
////        return amenityBlock != null && amenityBlock.getParent() instanceof Obstacle;
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
//        UniversityPatch startingPatch = this.currentFloor.getPatch(sourceCoordinates);
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
//    // Update the agent's recent patches
//    private void updateRecentPatches(UniversityPatch currentPatch, final int timeElapsedExpiration) {
//        List<UniversityPatch> patchesToForget = new ArrayList<>();
//
//        // Update the time elapsed in all of the recent patches
//        for (Map.Entry<UniversityPatch, Integer> recentPatchesAndTimeElapsed : this.recentPatches.entrySet()) {
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
//        for (UniversityPatch patchToForget : patchesToForget) {
//            this.recentPatches.remove(patchToForget);
//        }
//    }
//
//}