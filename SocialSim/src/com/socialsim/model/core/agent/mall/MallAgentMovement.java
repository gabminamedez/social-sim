package com.socialsim.model.core.agent.mall;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.generic.pathfinding.AgentMovement;
import com.socialsim.model.core.agent.generic.pathfinding.AgentPath;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.Vector;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchfield.*;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.*;
import com.socialsim.model.simulator.Simulator;
import com.socialsim.model.simulator.mall.MallSimulator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MallAgentMovement extends AgentMovement {

    public static int defaultNonverbalMean = 2;
    public static int defaultNonverbalStdDev = 1;
    public static int defaultCooperativeMean = 24;
    public static int defaultCooperativeStdDev = 12;
    public static int defaultExchangeMean = 24;
    public static int defaultExchangeStdDev = 12;
    public static int defaultFieldOfView = 30;

    private final MallAgent parent;
    private final Coordinates position; // Denotes the position of the agent
    private MallAgent leaderAgent;
    private List<MallAgent> followers;
    private final Mall mall;
    private final double baseWalkingDistance; // Denotes the distance (m) the agent walks in one second
    private double preferredWalkingDistance;
    private double currentWalkingDistance;
    private double proposedHeading;// Denotes the proposed heading of the agent in degrees where E = 0 degrees, N = 90 degrees, W = 180 degrees, S = 270 degrees
    private double heading;
    private double previousHeading;
    private int team;
    private KioskField chosenKioskField = null;
    private Digital chosenDigital = null;
    private Table chosenTable = null;

    private Patch currentPatch;
    private Amenity currentAmenity;
    private PatchField currentPatchField;
    private Patch goalPatch;
    private Amenity.AmenityBlock goalAttractor;
    private Amenity goalAmenity;
    private PatchField goalPatchField;
    private QueueingPatchField goalQueueingPatchField; // Denotes the patch field of the agent goal
    private Patch goalNearestQueueingPatch; // Denotes the patch with the nearest queueing patch

    private MallRoutePlan routePlan;
    private AgentPath currentPath; // Denotes the current path followed by this agent, if any
    private int stateIndex;
    private int actionIndex;
    private int returnIndex;
    private MallState currentState;
    private MallAction currentAction; // Low-level description of what the agent is doing

    private boolean isWaitingOnAmenity; // Denotes whether the agent is temporarily waiting on an amenity to be vacant
    private boolean hasEncounteredAgentToFollow; // Denotes whether this agent has encountered the agent to be followed in the queue
    private MallAgent agentFollowedWhenAssembling; // Denotes the agent this agent is currently following while assembling
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

    // Interaction parameters
    private boolean isInteracting; // Denotes whether the agent is currently interacting with another agent
    private boolean isStationInteracting;
    private boolean isSimultaneousInteractionAllowed; // Denotes whether an interaction is allowed while an action is being done simultaneously
    private int interactionDuration;
    private MallAgentMovement.InteractionType interactionType;

    public enum InteractionType {
        NON_VERBAL,
        COOPERATIVE,
        EXCHANGE
    }

    // The vectors of this agent
    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    public MallAgentMovement(Patch spawnPatch, MallAgent parent, MallAgent leaderAgent, double baseWalkingDistance, Coordinates coordinates, long tickEntered, int team) { // For inOnStart agents
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
        this.team = team;
        this.leaderAgent = leaderAgent;
        this.followers = new ArrayList<>();

        final double interQuartileRange = 0.12; // The walking speed values shall be in m/s
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        this.currentPatch = spawnPatch; // Add this agent to the spawn patch
        this.currentPatch.getAgents().add(parent);
        this.mall = (Mall) currentPatch.getEnvironment();

        if (parent.getInOnStart() && parent.getType() != MallAgent.Type.STAFF_STORE_CASHIER) { // All inOnStart agents will face the south by default
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.previousHeading = Math.toRadians(270.0);
            this.fieldOfViewAngle = this.mall.getFieldOfView();
        }
        else if (parent.getInOnStart() && parent.getType() == MallAgent.Type.STAFF_STORE_CASHIER) {
            if (team == 3 || team == 4 || team == 10 || team == 11) {
                this.proposedHeading = Math.toRadians(90.0);
                this.heading = Math.toRadians(90.0);
                this.previousHeading = Math.toRadians(90.0);
                this.fieldOfViewAngle = this.mall.getFieldOfView();
            }
            else {
                this.proposedHeading = Math.toRadians(270.0);
                this.heading = Math.toRadians(270.0);
                this.previousHeading = Math.toRadians(270.0);
                this.fieldOfViewAngle = this.mall.getFieldOfView();
            }
        }
        else { // All newly generated agents will face the east by default
            this.proposedHeading = Math.toRadians(0);
            this.heading = Math.toRadians(0);
            this.previousHeading = Math.toRadians(0);
            this.fieldOfViewAngle = this.mall.getFieldOfView();
        }

        this.currentPatchField = null;
        this.tickEntered = (int) tickEntered;
        this.ticksUntilFullyAccelerated = 10; // Set the agent's time until it fully accelerates
        this.ticksAcceleratedOrMaintainedSpeed = 0;

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal(); // Set the agent goal

        this.routePlan = new MallRoutePlan(parent, leaderAgent, mall, currentPatch, team);
        this.stateIndex = 0;
        this.actionIndex = 0;
        this.currentState = this.routePlan.getCurrentState();
        this.currentAction = this.routePlan.getCurrentState().getActions().get(actionIndex);
        if (!parent.getInOnStart()) {
            this.currentAmenity = mall.getMallGates().get(1); // Getting Entrance Gate
        }
        if (this.currentAction.getDestination() != null) {
            this.goalAttractor = this.currentAction.getDestination().getAmenityBlock();
        }
        if (this.currentAction.getDuration() != 0) {
            this.duration = this.currentAction.getDuration();
        }
    }

    public MallAgent getParent() {
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

        Patch newPatch = this.mall.getPatch(new Coordinates(coordinates.getX(), coordinates.getY())); // Get the patch of the new position
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

    public Mall getMall() {
        return mall;
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

    public int getTeam() {
        return team;
    }

    public KioskField getChosenKioskField() {
        return chosenKioskField;
    }

    public void setChosenKioskField(KioskField chosenKioskField) {
        this.chosenKioskField = chosenKioskField;
    }

    public Digital getChosenDigital() {
        return chosenDigital;
    }

    public void setChosenDigital(Digital chosenDigital) {
        this.chosenDigital = chosenDigital;
    }

    public Table getChosenTable() {
        return chosenTable;
    }

    public void setChosenTable(Table chosenTable) {
        this.chosenTable = chosenTable;
    }

    public List<MallAgent> getFollowers() {
        return followers;
    }

    public MallAgent getLeaderAgent() {
        return leaderAgent;
    }

    public double getFieldOfViewAngle() {
        return fieldOfViewAngle;
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

    public int getReturnIndex() {
        return returnIndex;
    }

    public void setReturnIndex(int stateIndex) {
        this.returnIndex = stateIndex;
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

    public MallState getCurrentState() {
        return currentState;
    }

    public void setNextState(int i) {
        this.currentState = this.currentState.getRoutePlan().setNextState(i);
    }

    public void setPreviousState(int i) {
        this.currentState = this.currentState.getRoutePlan().setPreviousState(i);
    }

    public MallAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(MallAction currentAction) {
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

    public MallRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(MallRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public boolean isWaitingOnAmenity() {
        return isWaitingOnAmenity;
    }

    public MallAgent getAgentFollowedWhenAssembling() {
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

    // Use the A* algorithm (with Euclidean distance to compute the f-score) to find the shortest path to the given goal patch
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
            if (patchToExplore.equals(goalPatch)) {
                Stack<Patch> path = new Stack<>();
                if(goalAmenity.getClass() == Bench.class || goalAmenity.getClass() == Toilet.class || goalAmenity.getClass() == MallGate.class || goalAmenity.getClass() == Table.class) {
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
            List<? extends Amenity> amenityListInFloor = this.mall.getAmenityList(nextAmenityClass);
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

            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) { // Look for a vacant amenity
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) { // Break when first vacant amenity is found
                    chosenAmenity = candidateAttractor.getParent();
                    chosenAttractor = candidateAttractor;
                    candidateAttractor.getPatch().getAmenityBlock().setIsReserved(true);
                    break;
                }
            }

            if (chosenAmenity != null) {
                this.goalAmenity = chosenAmenity;
                this.goalAttractor = chosenAttractor;

                return true;
            }
            else {
                return false;
            }
        }

        return true;
    }

    public void chooseRandomDigital() { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            List<? extends Amenity> amenityListInFloor = this.mall.getDigitals();

            if (this.leaderAgent == null) {
                this.goalAmenity = amenityListInFloor.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(amenityListInFloor.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
                this.chosenDigital = (Digital) goalAmenity;
            }
            else {
                if (this.leaderAgent.getAgentMovement().getChosenDigital() != null) {
                    this.goalAmenity = this.leaderAgent.getAgentMovement().getChosenDigital();
                    this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
                }
            }
        }
    }

    public void chooseRandomTable() { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            ArrayList<Table> tables1 = new ArrayList<>();
            ArrayList<Table> tables2 = new ArrayList<>();

            if (this.team == 1) {
                for (int i = 0; i < 16; i++) {
                    tables1.add(Main.mallSimulator.getMall().getTables().get(i));
                }
                for (int i = 35; i < 40; i++) {
                    tables1.add(Main.mallSimulator.getMall().getTables().get(i));
                }
                this.goalAmenity = tables1.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(tables1.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else {
                for (int i = 16; i < 31; i++) {
                    tables2.add(Main.mallSimulator.getMall().getTables().get(i));
                }
                for (int i = 40; i < 44; i++) {
                    tables2.add(Main.mallSimulator.getMall().getTables().get(i));
                }
                this.goalAmenity = tables2.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(tables2.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
        }
    }

    public void chooseRandomTablePatron(String type) { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
            ArrayList<Table> tables = new ArrayList<>();

            if (this.leaderAgent == null) {
                if (type == "RESTO") {
                    for (int i = 0; i < 32; i++) {
                        tables.add(Main.mallSimulator.getMall().getTables().get(i));
                    }
                    for (int i = 35; i < 45; i++) {
                        tables.add(Main.mallSimulator.getMall().getTables().get(i));
                    }
                }
                else {
                    tables.add(Main.mallSimulator.getMall().getTables().get(32));
                    tables.add(Main.mallSimulator.getMall().getTables().get(33));
                    tables.add(Main.mallSimulator.getMall().getTables().get(34));
                    tables.add(Main.mallSimulator.getMall().getTables().get(45));
                    tables.add(Main.mallSimulator.getMall().getTables().get(46));
                    tables.add(Main.mallSimulator.getMall().getTables().get(47));
                    tables.add(Main.mallSimulator.getMall().getTables().get(48));
                }

                for (Amenity amenity : tables) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
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

                    if (!candidateAttractor.getParent().getAttractors().get(0).getIsReserved()) {
                        chosenAmenity = candidateAttractor.getParent();
                        chosenAttractor = candidateAttractor;
                        chosenAmenity.getAttractors().get(0).setIsReserved(true);
                        break;
                    }
                }

                this.goalAmenity = chosenAmenity;
                this.goalAttractor = chosenAttractor;
                this.chosenTable = (Table) chosenAmenity;
            }
            else {
                if (leaderAgent.getAgentMovement().getChosenTable() != null) {
                    this.goalAmenity = leaderAgent.getAgentMovement().getChosenTable();
                    this.goalAttractor = leaderAgent.getAgentMovement().getChosenTable().getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(leaderAgent.getAgentMovement().getChosenTable().getAttractors().size()));
                }
            }
        }
    }

    public void chooseRandomAisle() { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            ArrayList<StoreAisle> aisles1 = new ArrayList<>();
            ArrayList<StoreAisle> aisles2 = new ArrayList<>();
            ArrayList<StoreAisle> aisles3 = new ArrayList<>();
            ArrayList<StoreAisle> aisles4 = new ArrayList<>();
            ArrayList<StoreAisle> aisles5 = new ArrayList<>();
            ArrayList<StoreAisle> aisles6 = new ArrayList<>();
            ArrayList<StoreAisle> aisles7 = new ArrayList<>();
            ArrayList<StoreAisle> aisles8 = new ArrayList<>();
            ArrayList<StoreAisle> aisles9 = new ArrayList<>();
            ArrayList<StoreAisle> aisles10 = new ArrayList<>();
            ArrayList<StoreAisle> aisles11 = new ArrayList<>();

            if (this.team == 1) {
                aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(0));
                aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(1));
                for (int i = 31; i < 36; i++) {
                    aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
                }
                this.goalAmenity = aisles1.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles1.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 2) {
                aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(36));
                aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(37));
                for (int i = 2; i < 6; i++) {
                    aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
                }
                this.goalAmenity = aisles2.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles2.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 3) {
                for (int i = 6; i < 12; i++) {
                    aisles3.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
                }
                this.goalAmenity = aisles3.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles3.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 4) {
                aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(38));
                aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(39));
                for (int i = 12; i < 16; i++) {
                    aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
                }
                this.goalAmenity = aisles4.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles4.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 5) {
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(16));
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(17));
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(40));
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(41));
                this.goalAmenity = aisles5.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles5.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 6) {
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(18));
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(19));
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(42));
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(43));
                this.goalAmenity = aisles6.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles6.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 7) {
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(20));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(21));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(22));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(44));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(45));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(46));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(47));
                this.goalAmenity = aisles7.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles7.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 8) {
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(23));
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(24));
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(48));
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(49));
                this.goalAmenity = aisles8.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles8.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 9) {
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(25));
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(26));
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(50));
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(51));
                this.goalAmenity = aisles9.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles9.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 10) {
                aisles10.add(Main.mallSimulator.getMall().getStoreAisles().get(27));
                aisles10.add(Main.mallSimulator.getMall().getStoreAisles().get(28));
                aisles10.add(Main.mallSimulator.getMall().getStoreAisles().get(52));
                aisles10.add(Main.mallSimulator.getMall().getStoreAisles().get(53));
                this.goalAmenity = aisles10.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles10.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 11) {
                aisles11.add(Main.mallSimulator.getMall().getStoreAisles().get(29));
                aisles11.add(Main.mallSimulator.getMall().getStoreAisles().get(30));
                aisles11.add(Main.mallSimulator.getMall().getStoreAisles().get(54));
                aisles11.add(Main.mallSimulator.getMall().getStoreAisles().get(55));
                this.goalAmenity = aisles11.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles11.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
        }
    }

    public boolean chooseBathroomGoal(Class<? extends Amenity> nextAmenityClass) { // Set the nearest goal to this agent
        if (this.goalAmenity == null) { //Only set the goal if one hasn't been set yet
            List<? extends Amenity> amenityListInFloor = this.mall.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (parent.getGender() == MallAgent.Gender.MALE) {
                    if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getValue() == 2) {
                        for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                            double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                            distancesToAttractors.put(attractor, distanceToAttractor);
                        }
                    }
                }
                else {
                    if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getValue() == 1) {
                        for (Amenity.AmenityBlock attractor : amenity.getAttractors()) { // Compute the distance to each attractor
                            double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                            distancesToAttractors.put(attractor, distanceToAttractor);
                        }
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
                if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) { // Break when first vacant amenity is found
                    chosenAmenity = candidateAttractor.getParent();
                    chosenAttractor = candidateAttractor;
                    candidateAttractor.getPatch().getAmenityBlock().setIsReserved(true);
                    break;
                }
            }

            if (chosenAmenity != null) {
                this.goalAmenity = chosenAmenity;
                this.goalAttractor = chosenAttractor;

                return true;
            }
            else {
                return false;
            }
        }

        return true;
    }

    public void chooseKiosk() {
        if (this.goalAmenity == null) { // Only set the goal if one hasn't been set yet
            List<KioskField> kioskFields = this.mall.getKioskFields();
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            if (this.leaderAgent == null) {
                HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
                for (KioskField kioskField : kioskFields) {
                    Amenity.AmenityBlock attractor = kioskField.getAssociatedPatches().get(0).getAmenityBlock();
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
                this.goalQueueingPatchField = chosenAttractor.getPatch().getQueueingPatchField().getKey();
                this.chosenKioskField = (KioskField) chosenAttractor.getPatch().getQueueingPatchField().getKey();
            }
            else {
                if (leaderAgent.getAgentMovement().getChosenKioskField() != null) {
                    this.goalAmenity = leaderAgent.getAgentMovement().getChosenKioskField().getTarget();
                    this.goalAttractor = leaderAgent.getAgentMovement().getChosenKioskField().getAssociatedPatches().get(0).getAmenityBlock();
                    this.goalQueueingPatchField = leaderAgent.getAgentMovement().getChosenKioskField();
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
        final double noNewPatchesSeenThreshold = 5; // MallAgent hasn't moved if new patches seen are less than this
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
//        if (this.currentState.getName() == MallState.Name.GOING_TO_SECURITY ||
//                this.currentState.getName() == MallState.Name.GOING_TO_LUNCH ||
//                this.currentState.getName() == MallState.Name.NEEDS_DRINK) {
//            // looking for queue
//            /*if (this.currentAction.getName() == MallAction.Name.GO_THROUGH_SCANNER ||
//                    this.currentAction.getName() == MallAction.Name.QUEUE_VENDOR ||
//                    this.currentAction.getName() == MallAction.Name.QUEUE_FOUNTAIN) {
//                Patch nextQueuePatch = this.currentPatch.getQueueingPatchField().getKey().getNextQueuePatch(currentPatch);
//                System.out.println("queueingPatchField: " + this.currentPatch.getQueueingPatchField()
//                + " key: " + this.currentPatch.getQueueingPatchField().getKey());
//                if (!nextQueuePatch.getAgents().isEmpty()) {
//                    this.stop();
//                }
//            }else*/ if (this.currentAction.getName() == MallAction.Name.CHECKOUT || this.currentAction.getName() == MallAction.Name.DRINK_FOUNTAIN ||
//                    this.currentAction.getName() == MallAction.Name.CLASSROOM_STAY_PUT || this.currentAction.getName() == MallAction.Name.STUDY_AREA_STAY_PUT ||
//                    this.currentAction.getName() == MallAction.Name.LUNCH_STAY_PUT || this.currentAction.getName() == MallAction.Name.RELIEVE_IN_CUBICLE ||
//                    this.currentAction.getName() == MallAction.Name.VIEW_BULLETIN || this.currentAction.getName() == MallAction.Name.SIT_ON_BENCH ||
//                    this.currentAction.getName() == MallAction.Name.GUARD_STAY_PUT || this.currentAction.getName() == MallAction.Name.JANITOR_CLEAN_TOILET || this.currentAction.getName() == MallAction.Name.JANITOR_CHECK_FOUNTAIN) {
//                this.stop();
//            }else { // Not in queue and not staying put
//                // TODO: Calculate which queue to go to for cafeteria gogo julian
//                if (this.isStuck || this.isServicedByQueueableGoal() && this.noMovementCounter > noMovementTicksThreshold) {
//                    this.isStuck = true;
//                    this.stuckCounter++;
//                }
//
//                TreeMap<Double, MallAgent> agentsWithinFieldOfView = new TreeMap<>(); // Count agents within FOV
//
//                for (Patch patch : patchesToExplore) { // Look around the patches that fall on the agent's field of view
//                    if (this.currentAction.getName() != MallAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != MallAction.Name.QUEUE_VENDOR && this.currentAction.getName() != MallAction.Name.QUEUE_FOUNTAIN) { // If not in queue, count obstacles
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
//                            MallAgent mallAgent = (MallAgent) otherAgent;
//                            if (!otherAgent.equals(this.getParent())) { // Make sure that the agent discovered isn't itself
//                                numberOfAgents++; // Take note of the agent density in this area
//
//                                // Check if this agent is within the field of view and within the slowdown distance
//                                double distanceToAgent = Coordinates.distance(this.position, mallAgent.getAgentMovement().getPosition());
//                                if (Coordinates.isWithinFieldOfView(this.position, mallAgent.getAgentMovement().getPosition(), this.proposedHeading, this.fieldOfViewAngle) && distanceToAgent <= slowdownStartDistance) {
//                                    agentsWithinFieldOfView.put(distanceToAgent, mallAgent);
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
//                Map.Entry<Double, MallAgent> nearestAgentEntry = agentsWithinFieldOfView.firstEntry(); // For each agent found within the slowdown distance, get the nearest one, if there is any
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
//                            for (Map.Entry<Double, MallAgent> otherAgentEntry : agentsWithinFieldOfView.entrySet()) {
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

            if(this.currentState.getName() != MallState.Name.GOING_TO_SECURITY && this.currentState.getName() != MallState.Name.GOING_HOME
                    && this.currentState.getName() != MallState.Name.GOING_TO_STORE && this.currentState.getName() != MallState.Name.IN_STORE
                    && this.currentAction.getName() != MallAction.Name.QUEUE_KIOSK && this.currentAction.getName() != MallAction.Name.CHECKOUT_KIOSK
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Bathroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Restaurant.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Store.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Dining.class)) {
                for (Agent otherAgent : patch.getAgents()) { // Inspect each agent in each patch in the patches in the field of view
                    if (this.getLeaderAgent() == null && this.followers.contains(otherAgent)) {
                        continue;
                    }
                    else if (this.getLeaderAgent() != null && otherAgent == this.getLeaderAgent()) {
                        continue;
                    }

                    MallAgent mallAgent = (MallAgent) otherAgent;
                    if (agentsProcessed == agentsProcessedLimit) {
                        break;
                    }

                    if (!otherAgent.equals(this.getParent())) { // Make sure that the agent discovered isn't itself
                        numberOfAgents++; // Take note of the agent density in this area

                        // Get the distance between this agent and the other agent
                        double distanceToOtherAgent = Coordinates.distance(this.position, mallAgent.getAgentMovement().getPosition());

                        if (distanceToOtherAgent <= slowdownStartDistance) { // If the distance is less than or equal to the distance when repulsion is supposed to kick in, compute for the magnitude of that repulsion force
                            final int maximumAgentCountTolerated = 5;

                            // The distance by which the repulsion starts to kick in will depend on the density of the agent's surroundings
                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;

                            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumAgentCountTolerated, minimumAgentCount, maximumDistance, maximumAgentCount, minimumDistance);
                            Vector agentRepulsiveForce = computeSocialForceFromAgent(mallAgent, distanceToOtherAgent, computedMaximumDistance, minimumAgentStopDistance, this.preferredWalkingDistance);
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
                    if (this.currentAction.getName() != MallAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != MallAction.Name.QUEUE_KIOSK) {
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
                    if (this.isStuck &&
                            (((this.currentAction.getName() == MallAction.Name.GO_THROUGH_SCANNER || this.currentAction.getName() == MallAction.Name.QUEUE_KIOSK && this.movementCounter >= unstuckTicksThreshold)
                                    || this.currentAction.getName() != MallAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != MallAction.Name.QUEUE_KIOSK && this.newPatchesSeenCounter >= unstuckTicksThreshold                                    ))) {
                        this.isReadyToFree = true;
                    }
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

    public void checkIfStuck() {
        if (this.currentAmenity == null) {
            if (this.currentPatch.getAmenityBlock() != null && this.currentPatch.getAmenityBlock().getParent() != this.goalAmenity) {
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
        /*if(currentPatch.getPatchField() != null && currentPatch.getPatchField().getKey().getClass() == Classroom.class
                || this.currentPatch.getAmenityBlock() != null && !this.currentPatch.getAmenityBlock().getParent()
                .equals(Door.class)){
            return 2;
        }else{*/
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
        //}
    }

    private Vector computeSocialForceFromAgent(MallAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
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
        for (int i = 1; i < this.goalQueueingPatchField.getAssociatedPatches().size(); i++) {
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
        MallAgent agentServiced = (MallAgent) this.goalQueueingPatchField.getCurrentAgent();

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

//    public boolean hasReachedFinalGoal() { // Check if this agent has reached its final goal
//        return !this.routePlan.getCurrentRoutePlan().hasNext();
//    }

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
            this.getMall().getAgents().remove(this.parent);

            SortedSet<Patch> currentPatchSet = this.getMall().getAgentPatchSet();
            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
                currentPatchSet.remove(this.currentPatch);
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
//                LinkedList<MallAgent> agentsQueueing
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
//                    MallAgent lastAgent = agentsQueueing.getLast();
//
//                    if (
//                            !(this.getGoalAmenityAsQueueable() instanceof TrainDoor)
//                                    && !(this.getGoalAmenityAsQueueable() instanceof Turnstile)
//                                    && lastAgent.getAgentMovement().getMallAction() == MallAction.ASSEMBLING
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
                if (!(this.currentPath.getPath().size() > 1/* && nextPatchInPath.getAmenityBlocksAround() == 0*/
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

    // TODO Change implementation based on the queues for cafeteria etc
//    private Patch computeBestQueueingPatchWeighted(List<Patch> PatchFieldList) {
//        // Collect the patches with the highest floor field values
//        List<Patch> PatchFieldCandidates = new ArrayList<>();
//        List<Double> PatchFieldValueCandidates = new ArrayList<>();
//
//        double valueSum = 0.0;
//
//        for (Patch patch : PatchFieldList) {
//            Map<QueueingPatchField.PatchFieldMallState, Double> PatchFieldMallStateDoubleMap
//                    = patch.getPatchFieldValues().get(this.getGoalAmenityAsQueueable());
//
//            if (
//                    !patch.getPatchFieldValues().isEmpty()
//                            && PatchFieldMallStateDoubleMap != null
//                            && !PatchFieldMallStateDoubleMap.isEmpty()
//                            && PatchFieldMallStateDoubleMap.get(
//                            this.goalQueueingPatchFieldMallState
//                    ) != null
//            ) {
//                double futurePatchFieldValue = patch.getPatchFieldValues()
//                        .get(this.getGoalAmenityAsQueueable())
//                        .get(this.goalQueueingPatchFieldMallState);
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

    // Get the best queueing patch around the current patch of another agent given the current floor field state
    // TODO Change implementation based on the queues for cafeteria etc
//    private Patch getBestQueueingPatchAroundAgent(MallAgent otherAgent) {
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

    private Patch getBestQueueingPatchAroundAgent(MallAgent agent) {
        return this.currentPatch;
    }

    private boolean hasObstacle(Patch patch, Amenity amenity) { // Check if the given patch has an obstacle
        if (patch.getPatchField() != null && patch.getPatchField().getKey().getClass() == Wall.class) {
            return true;
        }
        else if (patch.getAmenityBlock() != null && !patch.getAmenityBlock().getParent().equals(amenity)) {
            if (patch.getAmenityBlock().getParent().getClass() == Security.class || patch.getAmenityBlock().getParent().getClass() == Table.class || patch.getAmenityBlock().getParent().getClass() == Toilet.class || patch.getAmenityBlock().getParent().getClass() == Bench.class || patch.getAmenityBlock().getParent().getClass() == StoreAisle.class) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

    // Check if there is a clear line of sight from one point to another
    private boolean hasClearLineOfSight(Coordinates sourceCoordinates, Coordinates targetCoordinates, boolean includeStartingPatch) {
        if (hasObstacle(this.mall.getPatch(targetCoordinates), goalAmenity)) {
            return false;
        }

        final double resolution = 0.2;
        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.mall.getPatch(sourceCoordinates);
        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        while (distanceCovered <= distanceToTargetCoordinates) { // Keep looking for blocks while there is still distance to cover
            if (includeStartingPatch || !this.mall.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.mall.getPatch(currentPosition), goalAmenity)) {
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

    public void forceStationedInteraction(MallAgent.Persona agentPersona) {
        MallSimulator.currentPatchCount[currentPatch.getMatrixPosition().getRow()][currentPatch.getMatrixPosition().getColumn()]++;
        if (agentPersona == MallAgent.Persona.STAFF_STORE_CASHIER) {
            if (goalAmenity == mall.getStoreCounters().get(0)) {
                MallSimulator.currentPatchCount[10][19]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(1)) {
                MallSimulator.currentPatchCount[5][41]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(2)) {
                MallSimulator.currentPatchCount[49][19]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(3)) {
                MallSimulator.currentPatchCount[54][41]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(4)) {
                MallSimulator.currentPatchCount[0][55]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(5)) {
                MallSimulator.currentPatchCount[0][66]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(6)) {
                MallSimulator.currentPatchCount[0][84]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(7)) {
                MallSimulator.currentPatchCount[0][102]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(8)) {
                MallSimulator.currentPatchCount[0][113]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(9)) {
                MallSimulator.currentPatchCount[59][100]++;
            }
            else if (goalAmenity == mall.getStoreCounters().get(10)) {
                MallSimulator.currentPatchCount[59][112]++;
            }
            MallSimulator.currentPatronStaffStoreCount++;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == MallAgent.Persona.STAFF_KIOSK) {
            if (goalAmenity == mall.getKiosks().get(0)) {
                MallSimulator.currentPatchCount[22][53]++;
            }
            else if (goalAmenity == mall.getKiosks().get(1)) {
                MallSimulator.currentPatchCount[22][70]++;
            }
            else if (goalAmenity == mall.getKiosks().get(2)) {
                MallSimulator.currentPatchCount[22][87]++;
            }
            else if (goalAmenity == mall.getKiosks().get(3)) {
                MallSimulator.currentPatchCount[33][53]++;
            }
            else if (goalAmenity == mall.getKiosks().get(4)) {
                MallSimulator.currentPatchCount[33][70]++;
            }
            else if (goalAmenity == mall.getKiosks().get(5)) {
                MallSimulator.currentPatchCount[33][87]++;
            }
            else if (goalAmenity == mall.getKiosks().get(6)) {
                MallSimulator.currentPatchCount[27][97]++;
            }
            MallSimulator.currentPatronStaffKioskCount++;
            this.heading = 90;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == MallAgent.Persona.GUARD) {
            MallSimulator.currentPatchCount[33][2]++;
            MallSimulator.currentPatronGuardCount++;
            this.heading = 0;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == MallAgent.Persona.STAFF_RESTO) {
            MallSimulator.currentPatchCount[goalAmenity.getAttractors().get(0).getPatch().getMatrixPosition().getRow() - 1][goalAmenity.getAttractors().get(0).getPatch().getMatrixPosition().getColumn()]++;
            MallSimulator.currentPatronStaffRestoCount++;
            this.interactionDuration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(25);
        }
        else if (agentPersona == MallAgent.Persona.STAFF_STORE_SALES) {
            MallSimulator.currentPatronStaffStoreCount++;
            this.interactionDuration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(7);
        }

        if (agentPersona != MallAgent.Persona.GUARD) {
            MallSimulator.currentExchangeCount++;
            MallSimulator.averageExchangeDuration = (MallSimulator.averageExchangeDuration * (MallSimulator.currentExchangeCount - 1) + this.interactionDuration) / MallSimulator.currentExchangeCount;
        }
        else {
            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < MallRoutePlan.CHANCE_GUARD_VERBAL) {
                MallSimulator.currentExchangeCount++;
                MallSimulator.averageExchangeDuration = (MallSimulator.averageExchangeDuration * (MallSimulator.currentExchangeCount - 1) + this.interactionDuration) / MallSimulator.currentExchangeCount;
            }
            else {
                MallSimulator.currentNonverbalCount++;
                MallSimulator.averageNonverbalDuration = (MallSimulator.averageNonverbalDuration * (MallSimulator.currentNonverbalCount - 1) + this.interactionDuration) / MallSimulator.currentNonverbalCount;
            }
        }

        this.isStationInteracting = true;
        this.interactionDuration = 0;
    }

    public void forceActionInteraction(MallAgent agent, MallAgentMovement.InteractionType interactionType, int duration, MallSimulator MallSimulator){
        //TODO: Statistics in interaction

        // set own agent interaction parameters
        this.isInteracting = true;
        this.interactionType = interactionType;
        // set other agent interaction parameters
        agent.getAgentMovement().setInteracting(true);
        agent.getAgentMovement().setInteractionType(interactionType);
        double interactionStdDeviation, interactionMean;

        if (interactionType == MallAgentMovement.InteractionType.NON_VERBAL){
            interactionStdDeviation = 1;
            interactionMean = 2;
        }
        else if (interactionType == MallAgentMovement.InteractionType.COOPERATIVE){

            interactionStdDeviation = 5;
            interactionMean = 19;
        }
        else if (interactionType == MallAgentMovement.InteractionType.EXCHANGE){

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
    public void rollAgentInteraction(MallAgent agent){
        //TODO: Statistics in interaction

        double IOS1 = mall.getIOS().get(this.getParent().getPersona().ordinal()).get(agent.getPersona().ordinal());
        double IOS2 = mall.getIOS().get(agent.getPersona().ordinal()).get(this.getParent().getPersona().ordinal());
        // roll if possible interaction
        double CHANCE1 = Simulator.roll();
        double CHANCE2 = Simulator.roll();
        double interactionStdDeviation, interactionMean;
        if (CHANCE1 < IOS1 && CHANCE2 < IOS2){
            // roll if what kind of interaction
            CHANCE1 = Simulator.roll() * IOS1;
            CHANCE2 = Simulator.roll() * IOS2;
            double CHANCE = (CHANCE1 + CHANCE2) / 2;
            double CHANCE_NONVERBAL1 = MallAgent.chancePerActionInteractionType[this.getParent().getPersona().getID()][this.getParent().getAgentMovement().getCurrentAction().getName().getID()][0],
                    CHANCE_COOPERATIVE1 = MallAgent.chancePerActionInteractionType[this.getParent().getPersona().getID()][this.getParent().getAgentMovement().getCurrentAction().getName().getID()][1],
                    CHANCE_EXCHANGE1 = MallAgent.chancePerActionInteractionType[this.getParent().getPersona().getID()][this.getParent().getAgentMovement().getCurrentAction().getName().getID()][2],
                    CHANCE_NONVERBAL2 = MallAgent.chancePerActionInteractionType[agent.getPersona().getID()][agent.getAgentMovement().getCurrentAction().getName().getID()][0],
                    CHANCE_COOPERATIVE2 = MallAgent.chancePerActionInteractionType[agent.getPersona().getID()][agent.getAgentMovement().getCurrentAction().getName().getID()][1],
                    CHANCE_EXCHANGE2 = MallAgent.chancePerActionInteractionType[agent.getPersona().getID()][agent.getAgentMovement().getCurrentAction().getName().getID()][2];
            if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2) / 2){
                MallSimulator.currentNonverbalCount++;
                this.getParent().getAgentMovement().setInteractionType(MallAgentMovement.InteractionType.NON_VERBAL);
                agent.getAgentMovement().setInteractionType(MallAgentMovement.InteractionType.NON_VERBAL);
                interactionMean = getMall().getNonverbalMean();
                interactionStdDeviation = getMall().getNonverbalStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2) / 2){
                MallSimulator.currentCooperativeCount++;
                this.getParent().getAgentMovement().setInteractionType(MallAgentMovement.InteractionType.COOPERATIVE);
                agent.getAgentMovement().setInteractionType(MallAgentMovement.InteractionType.COOPERATIVE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getMall().getCooperativeMean();
                interactionStdDeviation = getMall().getCooperativeStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2 + CHANCE_EXCHANGE1 + CHANCE_EXCHANGE2) / 2){
                MallSimulator.currentExchangeCount++;
                this.getParent().getAgentMovement().setInteractionType(MallAgentMovement.InteractionType.EXCHANGE);
                agent.getAgentMovement().setInteractionType(MallAgentMovement.InteractionType.EXCHANGE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getMall().getExchangeMean();
                interactionStdDeviation = getMall().getExchangeStdDev();
            }
            else{
                return;
            }
            // set own agent interaction parameters
            this.isInteracting = true;
            // set other agent interaction parameters
            agent.getAgentMovement().setInteracting(true);

            if (this.parent.getType() == MallAgent.Type.STAFF_STORE_SALES){
                switch (agent.getType()){
                    case STAFF_STORE_SALES -> MallSimulator.currentStaffStoreStaffStoreCount++;
                    case STAFF_STORE_CASHIER -> MallSimulator.currentStaffStoreStaffStoreCount++;
                    case STAFF_RESTO -> MallSimulator.currentStaffStoreStaffRestoCount++;
                    case STAFF_KIOSK -> MallSimulator.currentStaffStoreStaffKioskCount++;
                    case GUARD -> MallSimulator.currentStaffStoreGuardCount++;
                    case PATRON -> MallSimulator.currentPatronStaffStoreCount++;
                }
            }
            else if (this.parent.getType() == MallAgent.Type.STAFF_STORE_CASHIER){
                switch (agent.getType()){
                    case STAFF_STORE_SALES -> MallSimulator.currentStaffStoreStaffStoreCount++;
                    case STAFF_STORE_CASHIER -> MallSimulator.currentStaffStoreStaffStoreCount++;
                    case STAFF_RESTO -> MallSimulator.currentStaffStoreStaffRestoCount++;
                    case STAFF_KIOSK -> MallSimulator.currentStaffStoreStaffKioskCount++;
                    case GUARD -> MallSimulator.currentStaffStoreGuardCount++;
                    case PATRON -> MallSimulator.currentPatronStaffStoreCount++;
                }
            }
            else if (this.parent.getType() == MallAgent.Type.STAFF_RESTO){
                switch (agent.getType()){
                    case STAFF_STORE_SALES -> MallSimulator.currentStaffStoreStaffRestoCount++;
                    case STAFF_STORE_CASHIER -> MallSimulator.currentStaffStoreStaffRestoCount++;
                    case STAFF_RESTO -> MallSimulator.currentStaffRestoStaffRestoCount++;
                    case STAFF_KIOSK -> MallSimulator.currentStaffRestoStaffKioskCount++;
                    case GUARD -> MallSimulator.currentStaffRestoGuardCount++;
                    case PATRON -> MallSimulator.currentPatronStaffRestoCount++;
                }
            }
            else if (this.parent.getType() == MallAgent.Type.STAFF_KIOSK){
                switch (agent.getType()){
                    case STAFF_STORE_SALES -> MallSimulator.currentStaffStoreStaffKioskCount++;
                    case STAFF_STORE_CASHIER -> MallSimulator.currentStaffStoreStaffKioskCount++;
                    case STAFF_RESTO -> MallSimulator.currentStaffStoreStaffKioskCount++;
                    case STAFF_KIOSK -> MallSimulator.currentStaffKioskStaffKioskCount++;
                    case GUARD -> MallSimulator.currentStaffKioskGuardCount++;
                    case PATRON -> MallSimulator.currentPatronStaffKioskCount++;
                }
            }
            else if (this.parent.getType() == MallAgent.Type.GUARD){
                switch (agent.getType()){
                    case STAFF_STORE_SALES -> MallSimulator.currentStaffStoreGuardCount++;
                    case STAFF_STORE_CASHIER -> MallSimulator.currentStaffStoreGuardCount++;
                    case STAFF_RESTO -> MallSimulator.currentStaffRestoGuardCount++;
                    case STAFF_KIOSK -> MallSimulator.currentStaffKioskGuardCount++;
                    case PATRON -> MallSimulator.currentPatronGuardCount++;
                }
            }
            else if (this.parent.getType() == MallAgent.Type.PATRON){
                switch (agent.getType()){
                    case STAFF_STORE_SALES -> MallSimulator.currentPatronStaffStoreCount++;
                    case STAFF_STORE_CASHIER -> MallSimulator.currentPatronStaffStoreCount++;
                    case STAFF_RESTO -> MallSimulator.currentPatronStaffRestoCount++;
                    case STAFF_KIOSK -> MallSimulator.currentPatronStaffKioskCount++;
                    case GUARD -> MallSimulator.currentPatronGuardCount++;
                    case PATRON -> MallSimulator.currentPatronPatronCount++;
                }
            }
            // roll duration (NOTE GAUSSIAN)
            this.interactionDuration = (int) (Math.floor((Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean) * (CHANCE1 + CHANCE2) / 2));
            agent.getAgentMovement().setInteractionDuration(this.interactionDuration);
            if (agent.getAgentMovement().getInteractionType() == MallAgentMovement.InteractionType.NON_VERBAL)
                MallSimulator.averageNonverbalDuration = (MallSimulator.averageNonverbalDuration * (MallSimulator.currentNonverbalCount - 1) + this.interactionDuration) / MallSimulator.currentNonverbalCount;
            else if (agent.getAgentMovement().getInteractionType() == MallAgentMovement.InteractionType.COOPERATIVE)
                MallSimulator.averageCooperativeDuration = (MallSimulator.averageCooperativeDuration * (MallSimulator.currentCooperativeCount - 1) + this.interactionDuration) / MallSimulator.currentCooperativeCount;
            else if (agent.getAgentMovement().getInteractionType() == MallAgentMovement.InteractionType.EXCHANGE)
                MallSimulator.averageExchangeDuration = (MallSimulator.averageExchangeDuration * (MallSimulator.currentExchangeCount - 1) + this.interactionDuration) / MallSimulator.currentExchangeCount;
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

    public void decrementDuration(){
        this.duration = getDuration() - 1;
    }

    public boolean isInteracting() {
        return isInteracting;
    }

    public void setInteracting(boolean interacting) {
        isInteracting = interacting;
    }

    public boolean isStationInteracting() {
        return isStationInteracting;
    }

    public void setStationInteracting(boolean interacting) {
        isStationInteracting = interacting;
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

    public MallAgentMovement.InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(MallAgentMovement.InteractionType interactionType) {
        this.interactionType = interactionType;
    }

}