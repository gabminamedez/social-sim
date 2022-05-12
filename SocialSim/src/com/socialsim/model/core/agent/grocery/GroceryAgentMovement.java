package com.socialsim.model.core.agent.grocery;

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
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchfield.CashierCounterField;
import com.socialsim.model.core.environment.grocery.patchfield.ServiceCounterField;
import com.socialsim.model.core.environment.grocery.patchfield.StallField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.*;
import com.socialsim.model.core.environment.mall.patchfield.Bathroom;
import com.socialsim.model.simulator.Simulator;
import com.socialsim.model.simulator.grocery.GrocerySimulator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroceryAgentMovement extends AgentMovement {

    public static int defaultNonverbalMean = 1;
    public static int defaultNonverbalStdDev = 1;
    public static int defaultCooperativeMean = 24;
    public static int defaultCooperativeStdDev = 6;
    public static int defaultExchangeMean = 24;
    public static int defaultExchangeStdDev = 6;
    public static int defaultFieldOfView = 30;

    private final GroceryAgent parent;
    private final Coordinates position;
    private GroceryAgent leaderAgent;
    private List<GroceryAgent> followers;
    private final Grocery grocery;
    private final double baseWalkingDistance;
    private double preferredWalkingDistance;
    private double currentWalkingDistance;
    private double proposedHeading;
    private double heading;
    private Patch currentPatch;
    private Amenity currentAmenity;
    private PatchField currentPatchField;
    private Patch goalPatch;
    private Patch waitPatch;
    private Amenity.AmenityBlock goalAttractor;
    private Amenity goalAmenity;
    private PatchField goalPatchField;
    private QueueingPatchField goalQueueingPatchField;
    private Patch goalNearestQueueingPatch;
    private CashierCounterField chosenCashierField = null;
    private ServiceCounterField chosenServiceField = null;
    private StallField chosenStallField = null;
    private Table chosenEatTable = null;
    private GroceryRoutePlan routePlan;
    private AgentPath currentPath;
    private int stateIndex;
    private int actionIndex;
    private int returnIndex;
    private GroceryState currentState;
    private GroceryAction currentAction;
    private boolean isWaitingOnAmenity;
    private boolean hasEncounteredAgentToFollow;
    private GroceryAgent agentFollowedWhenAssembling;
    private double distanceMovedInTick;
    private int tickEntered;
    private int duration;
    private int noMovementCounter;
    private int movementCounter;
    private int noNewPatchesSeenCounter;
    private int newPatchesSeenCounter;
    private boolean isStuck;
    private int stuckCounter;
    private int timeSinceLeftPreviousGoal;
    private final double fieldOfViewAngle;
    private boolean isReadyToFree;
    private final ConcurrentHashMap<Patch, Integer> recentPatches;
    private boolean isInteracting;
    private boolean isStationInteracting;
    private boolean isSimultaneousInteractionAllowed;
    private int interactionDuration;
    private GroceryAgentMovement.InteractionType interactionType;

    public enum InteractionType {
        NON_VERBAL, COOPERATIVE, EXCHANGE
    }

    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    public GroceryAgentMovement(Patch spawnPatch, GroceryAgent parent, GroceryAgent leaderAgent, double baseWalkingDistance, Coordinates coordinates, long tickEntered) { // For inOnStart agents
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
        this.leaderAgent = leaderAgent;
        this.followers = new ArrayList<>();

        final double interQuartileRange = 0.12;
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        this.currentPatch = spawnPatch;
        this.currentPatch.getAgents().add(parent);
        this.grocery = (Grocery) currentPatch.getEnvironment();

        if (parent.getInOnStart()) {
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.fieldOfViewAngle = this.grocery.getFieldOfView();
        }
        else {
            this.proposedHeading = Math.toRadians(90.0);
            this.heading = Math.toRadians(90.0);
            this.fieldOfViewAngle = this.grocery.getFieldOfView();
        }

        this.currentPatchField = null;
        this.tickEntered = (int) tickEntered;

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal();

        this.routePlan = new GroceryRoutePlan(parent, leaderAgent, grocery, currentPatch);
        this.stateIndex = 0;
        this.actionIndex = 0;
        this.currentState = this.routePlan.getCurrentState();
        this.currentAction = this.routePlan.getCurrentState().getActions().get(actionIndex);
        if (!parent.getInOnStart()) {
            this.currentAmenity = grocery.getGroceryGates().get(1); // Getting Entrance Gate
        }
        if (this.currentAction.getDestination() != null) {
            this.goalAttractor = this.currentAction.getDestination().getAmenityBlock();
        }
        if (this.currentAction.getDuration() != 0) {
            this.duration = this.currentAction.getDuration();
        }

        this.isInteracting = false;
    }

    public GroceryAgent getParent() {
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

        Patch newPatch = this.grocery.getPatch(new Coordinates(coordinates.getX(), coordinates.getY()));
        if (!previousPatch.equals(newPatch)) {
            previousPatch.removeAgent(this.parent);
            newPatch.addAgent(this.parent);

            SortedSet<Patch> previousPatchSet = previousPatch.getEnvironment().getAgentPatchSet();
            SortedSet<Patch> newPatchSet = newPatch.getEnvironment().getAgentPatchSet();

            if (previousPatchSet.contains(previousPatch) && hasNoAgent(previousPatch)) {
                previousPatchSet.remove(previousPatch);
            }

            newPatchSet.add(newPatch);
            this.currentPatch = newPatch;
            updateRecentPatches(this.currentPatch, timeElapsedExpiration);
        }
        else {
            updateRecentPatches(null, timeElapsedExpiration);
        }
    }

    public Patch getWaitPatch() {
        return waitPatch;
    }

    public GroceryAgent getLeaderAgent() {
        return leaderAgent;
    }

    public List<GroceryAgent> getFollowers() {
        return followers;
    }

    public Grocery getGrocery() {
        return grocery;
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

    public GroceryState getCurrentState() {
        return currentState;
    }

    public void setNextState(int i) {
        this.currentState = this.currentState.getRoutePlan().setNextState(i);
    }

    public void setPreviousState(int i) {
        this.currentState = this.currentState.getRoutePlan().setPreviousState(i);
    }

    public GroceryAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(GroceryAction currentAction) {
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

    public CashierCounterField getChosenCashierField() {
        return chosenCashierField;
    }

    public ServiceCounterField getChosenServiceField() {
        return chosenServiceField;
    }

    public StallField getChosenStallField() {
        return chosenStallField;
    }

    public Table getChosenEatTable() {
        return chosenEatTable;
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

    public GroceryRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(GroceryRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public boolean isWaitingOnAmenity() {
        return isWaitingOnAmenity;
    }

    public GroceryAgent getAgentFollowedWhenAssembling() {
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

    public void resetGoal() {
        this.goalPatch = null;
        this.waitPatch = null;
        this.goalAmenity = null;
        this.goalAttractor = null;
        this.goalPatchField = null;
        this.currentPath = null;
        this.currentAmenity = null;
        this.goalQueueingPatchField = null;
        this.goalNearestQueueingPatch = null;
        this.hasEncounteredAgentToFollow = false;
        this.isWaitingOnAmenity = false;
        this.agentFollowedWhenAssembling = null;
        this.distanceMovedInTick = 0.0;
        this.noMovementCounter = 0;
        this.movementCounter = 0;
        this.noNewPatchesSeenCounter = 0;
        this.newPatchesSeenCounter = 0;
        this.timeSinceLeftPreviousGoal = 0;
        this.recentPatches.clear();
        this.free();
    }

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
                if(getWaitPatch() != null){
                    path.push(goalPatch);
                }
                else if (goalAmenity.getClass() == Table.class || goalAmenity.getClass() == GroceryGate.class) {
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

    public void chooseGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.grocery.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                    double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                    distancesToAttractors.put(attractor, distanceToAttractor);
                }
            }

            List<Map.Entry<Amenity.AmenityBlock, Double> > list =
                    new LinkedList<Map.Entry<Amenity.AmenityBlock, Double> >(distancesToAttractors.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<Amenity.AmenityBlock, Double> >() {
                public int compare(Map.Entry<Amenity.AmenityBlock, Double> o1, Map.Entry<Amenity.AmenityBlock, Double> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            HashMap<Amenity.AmenityBlock, Double> sortedDistances = new LinkedHashMap<Amenity.AmenityBlock, Double>();
            for (Map.Entry<Amenity.AmenityBlock, Double> aa : list) {
                sortedDistances.put(aa.getKey(), aa.getValue());
            }

            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                if (candidateAttractor.getPatch().getAgents().isEmpty()) {
                    chosenAmenity =  candidateAttractor.getParent();
                    chosenAttractor = candidateAttractor;
                    candidateAttractor.getPatch().getAgents().add(this.parent);
                    break;
                }
            }

            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
        }
    }

    public boolean chooseBathroomGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.grocery.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (parent.getGender() == GroceryAgent.Gender.MALE) {
                    if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getValue() == 2) {
                        for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                            double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                            distancesToAttractors.put(attractor, distanceToAttractor);
                        }
                    }
                }
                else {
                    if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getValue() == 1) {
                        for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                            double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                            distancesToAttractors.put(attractor, distanceToAttractor);
                        }
                    }
                }
            }

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

            for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                if (!candidateAttractor.getPatch().getAmenityBlock().getIsReserved()) {
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

    public boolean chooseWaitPatch(){
        ArrayList<Patch> patchesToConsider = new ArrayList<>();
        for (int i = 46; i < 59; i++){
            for (int j = 16; j < 18; j++){
                patchesToConsider.add(grocery.getPatch(i, j));
            }
        }
        this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
        return true;
    }

    public void chooseRandomAisle() {
        if (this.goalAmenity == null) {
            List<? extends Amenity> freshProducts = this.grocery.getAmenityList(FreshProducts.class);
            List<? extends Amenity> frozenProducts = this.grocery.getAmenityList(FrozenProducts.class);
            List<? extends Amenity> frozenWall = this.grocery.getAmenityList(FrozenWall.class);
            List<? extends Amenity> productAisle = this.grocery.getAmenityList(ProductAisle.class);
            List<? extends Amenity> productShelf = this.grocery.getAmenityList(ProductShelf.class);
            List<? extends Amenity> productWall = this.grocery.getAmenityList(ProductWall.class);
            List<? extends Amenity> amenities1 = Stream.concat(freshProducts.stream(), frozenProducts.stream()).collect(Collectors.toList());
            List<? extends Amenity> amenities2 = Stream.concat(amenities1.stream(), frozenWall.stream()).collect(Collectors.toList());
            List<? extends Amenity> amenities3 = Stream.concat(amenities2.stream(), productAisle.stream()).collect(Collectors.toList());
            List<? extends Amenity> amenities4 = Stream.concat(amenities3.stream(), productShelf.stream()).collect(Collectors.toList());
            List<? extends Amenity> amenityListInFloor = Stream.concat(amenities4.stream(), productWall.stream()).collect(Collectors.toList());

            this.goalAmenity = amenityListInFloor.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(amenityListInFloor.size()));
            this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
        }
    }

    public void chooseCashierCounter() {
        if (this.goalAmenity == null) {
            List<CashierCounterField> cashierCounterFields = this.grocery.getCashierCounterFields();
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            if (this.leaderAgent == null) {
                HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
                for (CashierCounterField cashierCounterField : cashierCounterFields) {
                    Amenity.AmenityBlock attractor = cashierCounterField.getAssociatedPatches().get(0).getAmenityBlock();
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
                this.chosenCashierField = (CashierCounterField) chosenAttractor.getPatch().getQueueingPatchField().getKey();
            }
            else {
                if (leaderAgent.getAgentMovement().getChosenCashierField() != null) {
                    this.goalAmenity = leaderAgent.getAgentMovement().getChosenCashierField().getTarget();
                    this.goalAttractor = leaderAgent.getAgentMovement().getChosenCashierField().getAssociatedPatches().get(0).getAmenityBlock();
                    this.goalQueueingPatchField = leaderAgent.getAgentMovement().getChosenCashierField();
                }
            }
        }
    }

    public void chooseServiceCounter() {
        if (this.goalAmenity == null) {
            List<ServiceCounterField> serviceCounterFields = this.grocery.getServiceCounterFields();
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            if (this.leaderAgent == null) {
                HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
                for (ServiceCounterField serviceCounterField : serviceCounterFields) {
                    Amenity.AmenityBlock attractor = serviceCounterField.getAssociatedPatches().get(0).getAmenityBlock();
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
                this.chosenServiceField = (ServiceCounterField) chosenAttractor.getPatch().getQueueingPatchField().getKey();
            }
            else {
                if (leaderAgent.getAgentMovement().getChosenServiceField() != null) {
                    this.goalAmenity = leaderAgent.getAgentMovement().getChosenServiceField().getTarget();
                    this.goalAttractor = leaderAgent.getAgentMovement().getChosenServiceField().getAssociatedPatches().get(0).getAmenityBlock();
                    this.goalQueueingPatchField = leaderAgent.getAgentMovement().getChosenServiceField();
                }
            }
        }
    }

    public void chooseStall() {
        if (this.goalAmenity == null) {
            List<StallField> stallFields = this.grocery.getStallFields();
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;

            if (this.leaderAgent == null) {
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
                this.goalQueueingPatchField = chosenAttractor.getPatch().getQueueingPatchField().getKey();
                this.chosenStallField = (StallField) chosenAttractor.getPatch().getQueueingPatchField().getKey();
            }
            else {
                if (leaderAgent.getAgentMovement().getChosenStallField() != null) {
                    this.goalAmenity = leaderAgent.getAgentMovement().getChosenStallField().getTarget();
                    this.goalAttractor = leaderAgent.getAgentMovement().getChosenStallField().getAssociatedPatches().get(0).getAmenityBlock();
                    this.goalQueueingPatchField = leaderAgent.getAgentMovement().getChosenStallField();
                }
            }
        }
    }

    public void chooseEatTable() {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.grocery.getTables();
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            if (this.leaderAgent == null) {
                for (Amenity amenity : amenityListInFloor) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
                    }
                }

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

                for (Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry : sortedDistances.entrySet()) {
                    Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                    List<Amenity.AmenityBlock> candidateAmenity = candidateAttractor.getParent().getAttractors();
                    boolean isEmpty = true;
                    for (Amenity.AmenityBlock anAttractor : candidateAmenity) {
                        if (!anAttractor.getPatch().getAgents().isEmpty()) {
                            isEmpty = false;
                            break;
                        }
                    }

                    if (isEmpty) {
                        chosenAmenity =  candidateAttractor.getParent();
                        chosenAttractor = candidateAttractor;
                        candidateAttractor.getPatch().getAgents().add(this.parent);
                        break;
                    }
                }

                this.goalAmenity = chosenAmenity;
                this.goalAttractor = chosenAttractor;
                this.chosenEatTable = (Table) chosenAmenity;
            }
            else {
                if (leaderAgent.getAgentMovement().getChosenEatTable() != null) {
                    this.goalAmenity = leaderAgent.getAgentMovement().getChosenEatTable();
                    this.goalAttractor = leaderAgent.getAgentMovement().getChosenEatTable().getAttractors().get(0);
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
        double minimumDistance = Double.MAX_VALUE;
        double distance;

        Amenity.AmenityBlock nearestAttractor = null;
        if(getWaitPatch() != null){
            distance = Coordinates.distance(this.position,getWaitPatch().getPatchCenterCoordinates());
            minimumDistance = distance;
        }
        else if (goal.getAttractors() != null){
            for (Amenity.AmenityBlock attractor : goal.getAttractors()) {
                distance = Coordinates.distance(this.position, attractor.getPatch().getPatchCenterCoordinates());
                if (distance < minimumDistance) {
                    minimumDistance = distance;
                    nearestAttractor = attractor;
                }
            }
        }


        assert nearestAttractor != null;

        if (minimumDistance < walkingDistance) {
            if(getWaitPatch() != null){
                return new Coordinates(getWaitPatch().getPatchCenterCoordinates().getX(), getWaitPatch().getPatchCenterCoordinates().getY());
            }
            else{
                return new Coordinates(nearestAttractor.getPatch().getPatchCenterCoordinates().getX(), nearestAttractor.getPatch().getPatchCenterCoordinates().getY());
            }
        }
        else {
            Coordinates futurePosition = this.getFuturePosition(this.position, heading, walkingDistance);
            double newX = futurePosition.getX();
            double newY = futurePosition.getY();

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

    public void moveSocialForce() {
        final int noNewPatchesSeenTicksThreshold = 5;
        final int unstuckTicksThreshold = 60;
        final double noMovementThreshold = 0.01 * this.preferredWalkingDistance;
        final double noNewPatchesSeenThreshold = 5;
        final double slowdownStartDistance = 2.0;
        int numberOfObstacles = 0;
        final double minimumAgentStopDistance = 0.6;
        final double minimumObstacleStopDistance = 0.6;

        List<Patch> patchesToExplore = this.get7x7Field(this.proposedHeading, true, Math.toRadians(360.0));

        this.repulsiveForceFromAgents.clear();
        this.repulsiveForcesFromObstacles.clear();
        this.attractiveForce = null;
        this.motivationForce = null;

        TreeMap<Double, Patch> obstaclesEncountered = new TreeMap<>();

        List<Vector> vectorsToAdd = new ArrayList<>();

        Coordinates proposedNewPosition = this.getFuturePosition(this.preferredWalkingDistance);
        this.preferredWalkingDistance = this.baseWalkingDistance;

        final double distanceSlowdownStart = 5.0;
        final double speedDecreaseFactor = 0.5;

        double distanceToGoal;
        if(getWaitPatch() != null){
            distanceToGoal = Coordinates.distance(this.currentPatch, getWaitPatch());
            if (distanceToGoal < distanceSlowdownStart && this.hasClearLineOfSight(this.position, getWaitPatch().getPatchCenterCoordinates(), true)) {
                this.preferredWalkingDistance *= speedDecreaseFactor;
            }
        }
        else{
            distanceToGoal = Coordinates.distance(this.currentPatch, this.getGoalAmenity().getAttractors().get(0).getPatch());
            if (distanceToGoal < distanceSlowdownStart && this.hasClearLineOfSight(this.position, this.goalAmenity.getAttractors().get(0).getPatch().getPatchCenterCoordinates(), true)) {
                this.preferredWalkingDistance *= speedDecreaseFactor;
            }
        }

        if (this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold) {
            this.isStuck = true;
            this.stuckCounter++;
        }

        int agentsProcessed = 0;
        final int agentsProcessedLimit = 5;

        for (Patch patch : patchesToExplore) {
            if (hasObstacle(patch, goalAmenity)) {
                numberOfObstacles++;

                double distanceToObstacle = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());
                if (distanceToObstacle <= slowdownStartDistance) {
                    obstaclesEncountered.put(distanceToObstacle, patch);
                }
            }

            if (this.currentState.getName() != GroceryState.Name.GOING_TO_SECURITY && this.currentState.getName() != GroceryState.Name.GOING_HOME
                    && this.currentAction.getName() != GroceryAction.Name.QUEUE_SERVICE && this.currentAction.getName() != GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE
                    && this.currentAction.getName() != GroceryAction.Name.QUEUE_FOOD && this.currentAction.getName() != GroceryAction.Name.BUY_FOOD
                    && this.currentAction.getName() != GroceryAction.Name.QUEUE_CHECKOUT && this.currentAction.getName() != GroceryAction.Name.CHECKOUT
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Bathroom.class)) {
                for (Agent otherAgent : patch.getAgents()) {
                    if (this.getLeaderAgent() == null && this.followers.contains(otherAgent)) {
                        continue;
                    }
                    else if (this.getLeaderAgent() != null && otherAgent == this.getLeaderAgent()) {
                        continue;
                    }

                    GroceryAgent groceryAgent = (GroceryAgent) otherAgent;
                    if (agentsProcessed == agentsProcessedLimit) {
                        break;
                    }

                    if (!otherAgent.equals(this.getParent())) {
                        double distanceToOtherAgent = Coordinates.distance(this.position, groceryAgent.getAgentMovement().getPosition());

                        if (distanceToOtherAgent <= slowdownStartDistance) {
                            final int maximumAgentCountTolerated = 5;
                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;
                            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumAgentCountTolerated, minimumAgentCount, maximumDistance, maximumAgentCount, minimumDistance);
                            Vector agentRepulsiveForce = computeSocialForceFromAgent(groceryAgent, distanceToOtherAgent, computedMaximumDistance, minimumAgentStopDistance, this.preferredWalkingDistance);
                            this.repulsiveForceFromAgents.add(agentRepulsiveForce);
                            agentsProcessed++;
                        }
                    }
                }
            }
        }

        this.attractiveForce = this.computeAttractiveForce(new Coordinates(this.position), this.proposedHeading, proposedNewPosition, this.preferredWalkingDistance);
        vectorsToAdd.add(attractiveForce);

        double previousWalkingDistance = this.currentWalkingDistance;
        vectorsToAdd.addAll(this.repulsiveForceFromAgents);
        Vector partialMotivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd);
        if (partialMotivationForce != null) {
            final int minimumObstacleCount = 1;
            final double maximumDistance = 2.0;
            final int maximumObstacleCount = 2;
            final double minimumDistance = 0.7;
            final int maximumObstacleCountTolerated = 2;
            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumObstacleCountTolerated, minimumObstacleCount, maximumDistance, maximumObstacleCount, minimumDistance);
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
            this.motivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd);

            if (this.motivationForce != null) {
                if (this.motivationForce.getMagnitude() > this.preferredWalkingDistance) {
                    this.motivationForce.adjustMagnitude(this.preferredWalkingDistance);
                }

                this.motivationForce.adjustHeading(this.motivationForce.getHeading() + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(5));

                try {
                    double newHeading = motivationForce.getHeading();
                    Coordinates candidatePosition = this.motivationForce.getFuturePosition();
                    if (hasClearLineOfSight(this.position, candidatePosition, false)) {
                        this.move(candidatePosition);
                    }
                    else {
                        double revisedHeading;
                        Coordinates newFuturePosition;
                        int attempts = 0;
                        final int attemptLimit = 5;
                        boolean freeSpaceFound;

                        do {
                            revisedHeading = (motivationForce.getHeading() + Math.toRadians(180)) % Math.toRadians(360);
                            revisedHeading += Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(90);
                            revisedHeading %= Math.toRadians(360);
                            newFuturePosition = this.getFuturePosition(this.position, revisedHeading, this.preferredWalkingDistance * 0.25);
                            freeSpaceFound = hasClearLineOfSight(this.position, newFuturePosition, false);
                            attempts++;
                        } while (attempts < attemptLimit && !freeSpaceFound);

                        if (attempts != attemptLimit || freeSpaceFound) {
                            this.move(newFuturePosition);
                        }
                    }

                    if (!this.isStuck || Coordinates.headingDifference(this.heading, newHeading) <= Math.toDegrees(90.0) || this.currentWalkingDistance > noMovementThreshold) {
                        this.heading = newHeading;
                    }
                    this.currentWalkingDistance = motivationForce.getMagnitude();
                    this.distanceMovedInTick = motivationForce.getMagnitude();

                    if (this.currentAction.getName() != GroceryAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != GroceryAction.Name.GO_TO_CUSTOMER_SERVICE && this.currentAction.getName() != GroceryAction.Name.GO_TO_FOOD_STALL && this.currentAction.getName() != GroceryAction.Name.GO_TO_CHECKOUT) {
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

                    if (this.isStuck &&
                            (((this.currentAction.getName() != GroceryAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != GroceryAction.Name.GO_TO_CUSTOMER_SERVICE && this.currentAction.getName() != GroceryAction.Name.GO_TO_FOOD_STALL && this.currentAction.getName() != GroceryAction.Name.GO_TO_CHECKOUT && this.movementCounter >= unstuckTicksThreshold)
                                    || this.currentAction.getName() != GroceryAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != GroceryAction.Name.GO_TO_CUSTOMER_SERVICE && this.currentAction.getName() != GroceryAction.Name.GO_TO_FOOD_STALL && this.currentAction.getName() != GroceryAction.Name.GO_TO_CHECKOUT && this.newPatchesSeenCounter >= unstuckTicksThreshold                                    ))) {
                        this.isReadyToFree = true;
                    }
                    this.timeSinceLeftPreviousGoal++;

                    return;
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        this.hasEncounteredAgentToFollow = this.agentFollowedWhenAssembling != null;
        this.stop();
        this.distanceMovedInTick = 0.0;
        this.noMovementCounter++;
        this.movementCounter = 0;
        this.timeSinceLeftPreviousGoal++;
    }

    public void checkIfStuck() {
        if (this.currentAmenity == null) {
            if (this.currentPatch.getAmenityBlock() != null) {
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
        double differenceOfSquaredDistances = Math.pow(maximumDistance, 2.0) - Math.pow(minimumDistance, 2.0);
        double productOfMaximumRepulsionAndMinimumDistance = Math.pow(maximumRepulsionFactor, 2.0) * Math.pow(minimumDistance, 2.0);

        double a = (Math.pow(maximumDistance, 2.0) * (minimumRepulsionFactor * Math.pow(maximumDistance, 2.0) - minimumRepulsionFactor * Math.pow(minimumDistance, 2.0) + productOfMaximumRepulsionAndMinimumDistance)) / differenceOfSquaredDistances;
        double b = -(productOfMaximumRepulsionAndMinimumDistance / differenceOfSquaredDistances);

        double repulsion = a / Math.pow(distance, 2.0) + b;
        if (repulsion <= 0.0) {
            repulsion = 0.0;
        }

        return repulsion;
    }

    private Vector computeSocialForceFromAgent(GroceryAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        double modifiedDistanceToObstacle = Math.max(distanceToOtherAgent, minimumDistance);
        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        if (this.isStuck) {
            final double factor = 0.05;
            repulsionMagnitude -= this.stuckCounter * factor;
            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        double headingFromOtherAgent = Coordinates.headingTowards(agentPosition, this.position);
        Coordinates agentRepulsionVectorFuturePosition = this.getFuturePosition(agentPosition, headingFromOtherAgent, repulsionMagnitude);

        return new Vector(agentPosition, headingFromOtherAgent, agentRepulsionVectorFuturePosition, repulsionMagnitude);
    }

    private Vector computeSocialForceFromObstacle(Patch wallPatch, final double distanceToObstacle, final double maximumDistance, double minimumDistance, final double maximumMagnitude) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates repulsionVectorStartingPosition = wallPatch.getPatchCenterCoordinates();

        double modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(modifiedDistanceToObstacle, maximumDistance, minimumRepulsionFactor, minimumDistance, maximumRepulsionFactor);
        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        if (this.isStuck) {
            final double factor = 0.05;
            repulsionMagnitude -= this.stuckCounter * factor;
            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position);
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude);

        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude);
    }

    private void move(Coordinates futurePosition) { // Make the agent move given the future position
        this.setPosition(futurePosition);
    }

    public void stop() { // Have the agent stop
        this.currentWalkingDistance = 0.0;
    }

    public boolean hasReachedNextPatchInPath() { // Check if this agent is on the next patch of its path
        return isOnOrCloseToPatch(this.currentPath.getPath().peek());
    }

    public void joinQueue() {
        this.goalQueueingPatchField.getQueueingAgents().add(this.parent);

        Stack<Patch> path = new Stack<>();
        if (this.goalQueueingPatchField.getClass() == CashierCounterField.class) {
            for (int i = 1; i < this.goalQueueingPatchField.getAssociatedPatches().size(); i++) {
                path.push(this.goalQueueingPatchField.getAssociatedPatches().get(i));
            }
        }
        else {
            for (int i = 1; i < this.goalQueueingPatchField.getAssociatedPatches().size(); i++) {
                path.push(this.goalQueueingPatchField.getAssociatedPatches().get(i));
            }
        }
        this.currentPath = new AgentPath(0, path);
        this.duration = currentAction.getDuration();
    }

    public void leaveQueue() {
        this.goalQueueingPatchField.getQueueingAgents().remove(this.parent);
        this.goalQueueingPatchField = null;
    }

    public void reachPatchInPath() {
        Patch nextPatch;

        do {
            this.currentPath.getPath().pop();

            if (!this.currentPath.getPath().isEmpty()) {
                nextPatch = this.currentPath.getPath().peek();
            }
            else {
                break;
            }
        } while (!this.currentPath.getPath().isEmpty() && nextPatch.getAmenityBlocksAround() == 0
                && this.isOnOrCloseToPatch(nextPatch) && this.hasClearLineOfSight(this.position, nextPatch.getPatchCenterCoordinates(), true));
    }

    public boolean hasAgentReachedFinalPatchInPath() { // Check if this agent has reached the final patch in its current path
        return this.currentPath.getPath().isEmpty();
    }

    private boolean isOnOrCloseToPatch(Patch patch) {
        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
    }

    public void despawn() {
        if (this.currentPatch != null) {
            switch (this.getParent().getPersona()){
                case COMPLETE_FAMILY_CUSTOMER, HELP_FAMILY_CUSTOMER, DUO_FAMILY_CUSTOMER -> {
                    if (this.getParent().isLeader())
                        GrocerySimulator.currentFamilyCount--;
                }
                case STTP_ALONE_CUSTOMER, MODERATE_ALONE_CUSTOMER -> GrocerySimulator.currentAloneCustomerCount--;
            }

            this.currentPatch.getAgents().remove(this.parent);
            this.getGrocery().getAgents().remove(this.parent);

            SortedSet<Patch> currentPatchSet = this.getGrocery().getAgentPatchSet();
            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
                currentPatchSet.remove(this.currentPatch);
            }
        }
    }

    public void faceNextPosition() {
        this.proposedHeading = Coordinates.headingTowards(this.position, this.goalPatch.getPatchCenterCoordinates());
    }

    public void free() {
        this.isStuck = false;
        this.stuckCounter = 0;
        this.noMovementCounter = 0;
        this.noNewPatchesSeenCounter = 0;
        this.currentPath = null;
        this.isReadyToFree = false;
    }

    public boolean chooseNextPatchInPath() {
        boolean wasPathJustGenerated = false;
        final int recomputeThreshold = 10;

        if (this.currentPath == null || this.isStuck && this.noNewPatchesSeenCounter > recomputeThreshold) {
            AgentPath agentPath = null;

            if (goalQueueingPatchField == null && waitPatch == null) {
                agentPath = computePath(this.currentPatch, this.goalAttractor.getPatch(), true, true);
            }
            else if (this.waitPatch != null){
                agentPath = computePath(this.currentPatch,this.waitPatch , true, true);
            }
            else {
                agentPath = computePath(this.currentPatch, this.goalQueueingPatchField.getLastQueuePatch(), true, true);
            }

            if (agentPath != null) {
                this.currentPath = new AgentPath(agentPath);
                wasPathJustGenerated = true;
            }
        }

        if (this.currentPath == null || this.currentPath.getPath().isEmpty()) {
            return false;
        }

        if (wasPathJustGenerated) {
            Patch nextPatchInPath;

            while (true) {
                nextPatchInPath = this.currentPath.getPath().peek();
                if (!(this.currentPath.getPath().size() > 1
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

    private boolean hasObstacle(Patch patch, Amenity amenity) {
        if (patch.getPatchField() != null && patch.getPatchField().getKey().getClass() == Wall.class) {
            return true;
        }
        else if (patch.getAmenityBlock() != null && !patch.getAmenityBlock().getParent().equals(amenity)) {
            if (patch.getAmenityBlock().getParent().getClass() == Security.class || patch.getAmenityBlock().getParent().getClass() == Toilet.class) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

    private boolean hasClearLineOfSight(Coordinates sourceCoordinates, Coordinates targetCoordinates, boolean includeStartingPatch) {
        if (hasObstacle(this.grocery.getPatch(targetCoordinates), goalAmenity)) {
            return false;
        }

        final double resolution = 0.2;
        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.grocery.getPatch(sourceCoordinates);
        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        while (distanceCovered <= distanceToTargetCoordinates) {
            if (includeStartingPatch || !this.grocery.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.grocery.getPatch(currentPosition), goalAmenity)) {
                    return false;
                }
            }

            currentPosition = this.getFuturePosition(currentPosition, headingToTargetCoordinates, resolution);
            distanceCovered += resolution;
        }

        return true;
    }

    private void updateRecentPatches(Patch currentPatch, final int timeElapsedExpiration) {
        List<Patch> patchesToForget = new ArrayList<>();

        for (Map.Entry<Patch, Integer> recentPatchesAndTimeElapsed : this.recentPatches.entrySet()) {
            this.recentPatches.put(recentPatchesAndTimeElapsed.getKey(), recentPatchesAndTimeElapsed.getValue() + 1);
            if (recentPatchesAndTimeElapsed.getValue() == timeElapsedExpiration) {
                patchesToForget.add(recentPatchesAndTimeElapsed.getKey());
            }
        }

        if (currentPatch != null) {
            this.recentPatches.put(currentPatch, 0);
        }

        for (Patch patchToForget : patchesToForget) {
            this.recentPatches.remove(patchToForget);
        }
    }

    public void forceStationedInteraction(GroceryAgent.Persona agentPersona) {
        GrocerySimulator.currentPatchCount[currentPatch.getMatrixPosition().getRow()][currentPatch.getMatrixPosition().getColumn()]++;
        if (agentPersona == GroceryAgent.Persona.CASHIER) {
            if (goalAmenity == grocery.getCashierCounters().get(0)) {
                GrocerySimulator.currentPatchCount[44][20]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(1)) {
                GrocerySimulator.currentPatchCount[44][26]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(2)) {
                GrocerySimulator.currentPatchCount[44][32]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(3)) {
                GrocerySimulator.currentPatchCount[44][38]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(4)) {
                GrocerySimulator.currentPatchCount[44][44]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(5)) {
                GrocerySimulator.currentPatchCount[44][50]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(6)) {
                GrocerySimulator.currentPatchCount[44][56]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(7)) {
                GrocerySimulator.currentPatchCount[44][62]++;
            }
            GrocerySimulator.currentCustomerCashierCount++;
            this.heading = 0;
            this.interactionDuration = this.duration * 2 / 3;
        }
        else if (agentPersona == GroceryAgent.Persona.BAGGER) {
            if (goalAmenity == grocery.getCashierCounters().get(0)) {
                GrocerySimulator.currentPatchCount[45][20]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(1)) {
                GrocerySimulator.currentPatchCount[45][26]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(2)) {
                GrocerySimulator.currentPatchCount[45][32]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(3)) {
                GrocerySimulator.currentPatchCount[45][38]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(4)) {
                GrocerySimulator.currentPatchCount[45][44]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(5)) {
                GrocerySimulator.currentPatchCount[45][50]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(6)) {
                GrocerySimulator.currentPatchCount[45][56]++;
            }
            else if (goalAmenity == grocery.getCashierCounters().get(7)) {
                GrocerySimulator.currentPatchCount[45][62]++;
            }
            GrocerySimulator.currentCustomerBaggerCount++;
            this.heading = 0;
            this.interactionDuration = this.duration / 3;
        }
        else if (agentPersona == GroceryAgent.Persona.CUSTOMER_SERVICE) {
            if (goalAmenity == grocery.getServiceCounters().get(0)) {
                GrocerySimulator.currentPatchCount[52][23]++;
            }
            else if (goalAmenity == grocery.getServiceCounters().get(1)) {
                GrocerySimulator.currentPatchCount[52][27]++;
            }
            else if (goalAmenity == grocery.getServiceCounters().get(2)) {
                GrocerySimulator.currentPatchCount[52][31]++;
            }
            GrocerySimulator.currentCustomerServiceCount++;
            this.heading = 90;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == GroceryAgent.Persona.STAFF_FOOD) {
            if (goalAmenity == grocery.getStalls().get(0)) {
                GrocerySimulator.currentPatchCount[58][84]++;
            }
            else if (goalAmenity == grocery.getStalls().get(1)) {
                GrocerySimulator.currentPatchCount[58][87]++;
            }
            else if (goalAmenity == grocery.getStalls().get(2)) {
                GrocerySimulator.currentPatchCount[58][90]++;
            }
            else if (goalAmenity == grocery.getStalls().get(3)) {
                GrocerySimulator.currentPatchCount[58][93]++;
            }
            else if (goalAmenity == grocery.getStalls().get(4)) {
                GrocerySimulator.currentPatchCount[58][96]++;
            }
            GrocerySimulator.currentCustomerFoodCount++;
            this.heading = 270;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == GroceryAgent.Persona.BUTCHER) {
            if (goalAmenity == grocery.getMeatSections().get(0)) {
                GrocerySimulator.currentPatchCount[29][1]++;
            }
            else if (goalAmenity == grocery.getMeatSections().get(1)) {
                GrocerySimulator.currentPatchCount[37][1]++;
            }
            GrocerySimulator.currentCustomerButcherCount++;
            this.heading = 180;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == GroceryAgent.Persona.GUARD_ENTRANCE) {
            GrocerySimulator.currentPatchCount[57][52]++;
            GrocerySimulator.currentCustomerGuardCount++;
            this.heading = 0;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == GroceryAgent.Persona.GUARD_EXIT) {
            GrocerySimulator.currentPatchCount[57][47]++;
            GrocerySimulator.currentCustomerGuardCount++;
            this.heading = 90;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == GroceryAgent.Persona.STAFF_AISLE) {
            GrocerySimulator.currentCustomerAisleCount++;
            this.interactionDuration = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(7);
        }

        if (agentPersona != GroceryAgent.Persona.GUARD_ENTRANCE) {
            GrocerySimulator.currentCooperativeCount++;
            GrocerySimulator.averageCooperativeDuration = (GrocerySimulator.averageCooperativeDuration * (GrocerySimulator.currentCooperativeCount - 1) + this.interactionDuration) / GrocerySimulator.currentCooperativeCount;
        }
        else {
            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < GroceryRoutePlan.CHANCE_GUARD_VERBAL) {
                GrocerySimulator.currentExchangeCount++;
                GrocerySimulator.averageExchangeDuration = (GrocerySimulator.averageExchangeDuration * (GrocerySimulator.currentExchangeCount - 1) + this.interactionDuration) / GrocerySimulator.currentExchangeCount;
            }
            else {
                GrocerySimulator.currentNonverbalCount++;
                GrocerySimulator.averageNonverbalDuration = (GrocerySimulator.averageNonverbalDuration * (GrocerySimulator.currentNonverbalCount - 1) + this.interactionDuration) / GrocerySimulator.currentNonverbalCount;
            }
        }

        this.isStationInteracting = true;
        this.interactionDuration = 0;
    }

    public void rollAgentInteraction(GroceryAgent agent){
        double IOS1 = grocery.getIOS().get(this.getParent().getId()).get(agent.getId());
        double IOS2 = grocery.getIOS().get(agent.getId()).get(this.getParent().getId());
        double CHANCE1 = Simulator.roll();
        double CHANCE2 = Simulator.roll();
        double interactionStdDeviation, interactionMean;
        if (CHANCE1 < IOS1 && CHANCE2 < IOS2) {
            CHANCE1 = Simulator.roll() * IOS1;
            CHANCE2 = Simulator.roll() * IOS2;
            double CHANCE = (CHANCE1 + CHANCE2) / 2;
            double CHANCE_NONVERBAL1 = ((double) grocery.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE1 = ((double) grocery.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE1 = ((double) grocery.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100,
                    CHANCE_NONVERBAL2 = ((double) grocery.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE2 = ((double) grocery.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE2 = ((double) grocery.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100;
            if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2) / 2) {
                GrocerySimulator.currentNonverbalCount++;
                this.getParent().getAgentMovement().setInteractionType(GroceryAgentMovement.InteractionType.NON_VERBAL);
                agent.getAgentMovement().setInteractionType(GroceryAgentMovement.InteractionType.NON_VERBAL);
                interactionMean = getGrocery().getNonverbalMean();
                interactionStdDeviation = getGrocery().getNonverbalStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2) / 2) {
                GrocerySimulator.currentCooperativeCount++;
                this.getParent().getAgentMovement().setInteractionType(GroceryAgentMovement.InteractionType.COOPERATIVE);
                agent.getAgentMovement().setInteractionType(GroceryAgentMovement.InteractionType.COOPERATIVE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getGrocery().getCooperativeMean();
                interactionStdDeviation = getGrocery().getCooperativeStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2 + CHANCE_EXCHANGE1 + CHANCE_EXCHANGE2) / 2) {
                GrocerySimulator.currentExchangeCount++;
                this.getParent().getAgentMovement().setInteractionType(GroceryAgentMovement.InteractionType.EXCHANGE);
                agent.getAgentMovement().setInteractionType(GroceryAgentMovement.InteractionType.EXCHANGE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getGrocery().getExchangeMean();
                interactionStdDeviation = getGrocery().getExchangeStdDev();
            }
            else {
                return;
            }
            this.isInteracting = true;
            agent.getAgentMovement().setInteracting(true);
            if (this.parent.getType() == GroceryAgent.Type.CUSTOMER) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerCustomerCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentCustomerAisleCount++;
                    case CASHIER -> GrocerySimulator.currentCustomerCashierCount++;
                    case BAGGER -> GrocerySimulator.currentCustomerBaggerCount++;
                    case GUARD -> GrocerySimulator.currentCustomerGuardCount++;
                    case BUTCHER -> GrocerySimulator.currentCustomerButcherCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentCustomerServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentCustomerFoodCount++;
                }
                if (this.parent.isLeader() && agent.getAgentMovement().getLeaderAgent() == this.parent) {
                    GrocerySimulator.currentFamilyToFamilyCount++;
                }
                else if (!this.parent.isLeader() && agent.getAgentMovement().getLeaderAgent() == this.parent.getAgentMovement().getLeaderAgent()) {
                    GrocerySimulator.currentFamilyToFamilyCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.STAFF_AISLE) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerAisleCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleAisleCount++;
                    case CASHIER -> GrocerySimulator.currentAisleCashierCount++;
                    case BAGGER -> GrocerySimulator.currentAisleBaggerCount++;
                    case GUARD -> GrocerySimulator.currentAisleGuardCount++;
                    case BUTCHER -> GrocerySimulator.currentAisleButcherCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentAisleServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentAisleFoodCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.CASHIER) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerCashierCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleCashierCount++;
                    case CASHIER -> GrocerySimulator.currentCashierCashierCount++;
                    case BAGGER -> GrocerySimulator.currentCashierBaggerCount++;
                    case GUARD -> GrocerySimulator.currentCashierGuardCount++;
                    case BUTCHER -> GrocerySimulator.currentCashierButcherCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentCashierServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentCashierFoodCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.BAGGER) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerBaggerCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleBaggerCount++;
                    case CASHIER -> GrocerySimulator.currentCashierBaggerCount++;
                    case BAGGER -> GrocerySimulator.currentBaggerBaggerCount++;
                    case GUARD -> GrocerySimulator.currentBaggerGuardCount++;
                    case BUTCHER -> GrocerySimulator.currentBaggerButcherCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentBaggerServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentBaggerFoodCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.GUARD) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerGuardCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleGuardCount++;
                    case CASHIER -> GrocerySimulator.currentCashierGuardCount++;
                    case BAGGER -> GrocerySimulator.currentBaggerGuardCount++;
                    case GUARD -> GrocerySimulator.currentGuardGuardCount++;
                    case BUTCHER -> GrocerySimulator.currentGuardButcherCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentGuardServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentGuardFoodCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.BUTCHER) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerButcherCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleButcherCount++;
                    case CASHIER -> GrocerySimulator.currentCashierButcherCount++;
                    case BAGGER -> GrocerySimulator.currentBaggerButcherCount++;
                    case GUARD -> GrocerySimulator.currentGuardButcherCount++;
                    case BUTCHER -> GrocerySimulator.currentButcherButcherCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentButcherServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentButcherFoodCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.CUSTOMER_SERVICE) {
                switch (agent.getType()) {
                    case CUSTOMER -> GrocerySimulator.currentCustomerServiceCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleServiceCount++;
                    case CASHIER -> GrocerySimulator.currentCashierServiceCount++;
                    case BAGGER -> GrocerySimulator.currentBaggerServiceCount++;
                    case GUARD -> GrocerySimulator.currentGuardServiceCount++;
                    case BUTCHER -> GrocerySimulator.currentButcherServiceCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentServiceServiceCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentServiceFoodCount++;
                }
            }
            else if (this.parent.getType() == GroceryAgent.Type.STAFF_FOOD) {
                switch (agent.getType()){
                    case CUSTOMER -> GrocerySimulator.currentCustomerFoodCount++;
                    case STAFF_AISLE -> GrocerySimulator.currentAisleFoodCount++;
                    case CASHIER -> GrocerySimulator.currentCashierFoodCount++;
                    case BAGGER -> GrocerySimulator.currentBaggerFoodCount++;
                    case GUARD -> GrocerySimulator.currentGuardFoodCount++;
                    case BUTCHER -> GrocerySimulator.currentButcherFoodCount++;
                    case CUSTOMER_SERVICE -> GrocerySimulator.currentServiceFoodCount++;
                    case STAFF_FOOD -> GrocerySimulator.currentFoodFoodCount++;
                }
            }
            this.interactionDuration = (int) (Math.floor(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean));
            if (this.interactionDuration < 0)
                this.interactionDuration = 0;
            agent.getAgentMovement().setInteractionDuration(this.interactionDuration);
            if (agent.getAgentMovement().getInteractionType() == GroceryAgentMovement.InteractionType.NON_VERBAL)
                GrocerySimulator.averageNonverbalDuration = (GrocerySimulator.averageNonverbalDuration * (GrocerySimulator.currentNonverbalCount - 1) + this.interactionDuration) / GrocerySimulator.currentNonverbalCount;
            else if (agent.getAgentMovement().getInteractionType() == GroceryAgentMovement.InteractionType.COOPERATIVE)
                GrocerySimulator.averageCooperativeDuration = (GrocerySimulator.averageCooperativeDuration * (GrocerySimulator.currentCooperativeCount - 1) + this.interactionDuration) / GrocerySimulator.currentCooperativeCount;
            else if (agent.getAgentMovement().getInteractionType() == GroceryAgentMovement.InteractionType.EXCHANGE)
                GrocerySimulator.averageExchangeDuration = (GrocerySimulator.averageExchangeDuration * (GrocerySimulator.currentExchangeCount - 1) + this.interactionDuration) / GrocerySimulator.currentExchangeCount;
        }
    }
    public void interact(){
        if (this.interactionDuration <= 0) {
            this.isInteracting = false;
            this.interactionType = null;
        }
        else {
            this.interactionDuration--;
        }
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

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

}