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
import com.socialsim.model.simulator.grocery.GrocerySimulator;
import com.socialsim.model.simulator.mall.MallSimulator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MallAgentMovement extends AgentMovement {

    public static int defaultNonverbalMean = 1;
    public static int defaultNonverbalStdDev = 1;
    public static int defaultCooperativeMean = 24;
    public static int defaultCooperativeStdDev = 6;
    public static int defaultExchangeMean = 24;
    public static int defaultExchangeStdDev = 6;
    public static int defaultFieldOfView = 30;

    private final MallAgent parent;
    private final Coordinates position;
    private MallAgent leaderAgent;
    private List<MallAgent> followers;
    private final Mall mall;
    private final double baseWalkingDistance;
    private double preferredWalkingDistance;
    private double currentWalkingDistance;
    private double proposedHeading;
    private double heading;
    private int team;
    private KioskField chosenKioskField = null;
    private Digital chosenDigital = null;
    private Table chosenTable = null;
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
    private MallRoutePlan routePlan;
    private AgentPath currentPath;
    private int stateIndex;
    private int actionIndex;
    private int returnIndex;
    private MallState currentState;
    private MallAction currentAction;
    private boolean isWaitingOnAmenity;
    private boolean hasEncounteredAgentToFollow;
    private MallAgent agentFollowedWhenAssembling;
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
    private MallAgentMovement.InteractionType interactionType;
    private boolean isGate1;

    public enum InteractionType {
        NON_VERBAL, COOPERATIVE, EXCHANGE
    }

    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    public MallAgentMovement(Patch spawnPatch, MallAgent parent, MallAgent leaderAgent, double baseWalkingDistance, Coordinates coordinates, long tickEntered, int team) {
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
        this.team = team;
        this.leaderAgent = leaderAgent;
        this.followers = new ArrayList<>();

        final double interQuartileRange = 0.12;
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        this.currentPatch = spawnPatch;
        this.currentPatch.getAgents().add(parent);
        this.mall = (Mall) currentPatch.getEnvironment();

        if (parent.getInOnStart() && parent.getType() != MallAgent.Type.STAFF_STORE_CASHIER && parent.getType() != MallAgent.Type.CONCIERGER) {
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.fieldOfViewAngle = this.mall.getFieldOfView();
        }
        else if (parent.getInOnStart() && parent.getType() == MallAgent.Type.STAFF_STORE_CASHIER) {
            if (team == 3 || team == 4 || team == 10 || team == 11) {
                this.proposedHeading = Math.toRadians(90.0);
                this.heading = Math.toRadians(90.0);
                this.fieldOfViewAngle = this.mall.getFieldOfView();
            }
            else {
                this.proposedHeading = Math.toRadians(270.0);
                this.heading = Math.toRadians(270.0);
                this.fieldOfViewAngle = this.mall.getFieldOfView();
            }
        }
        else if (parent.getInOnStart() && parent.getType() == MallAgent.Type.CONCIERGER) {
            this.proposedHeading = Math.toRadians(180.0);
            this.heading = Math.toRadians(180.0);
            this.fieldOfViewAngle = this.mall.getFieldOfView();
        }
        else {
            this.proposedHeading = Math.toRadians(0);
            this.heading = Math.toRadians(0);
            this.fieldOfViewAngle = this.mall.getFieldOfView();
        }

        this.currentPatchField = null;
        this.tickEntered = (int) tickEntered;

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal();

        this.routePlan = new MallRoutePlan(parent, leaderAgent, mall, currentPatch, team);
        this.stateIndex = 0;
        this.actionIndex = 0;
        this.currentState = this.routePlan.getCurrentState();
        this.currentAction = this.routePlan.getCurrentState().getActions().get(actionIndex);
        if (!parent.getInOnStart()) {
            this.currentAmenity = mall.getMallGates().get(1);
        }
        if (this.currentAction.getDestination() != null) {
            this.goalAttractor = this.currentAction.getDestination().getAmenityBlock();
        }
        if (this.currentAction.getDuration() != 0) {
            this.duration = this.currentAction.getDuration();
        }

        isGate1 = false;
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

        Patch newPatch = this.mall.getPatch(new Coordinates(coordinates.getX(), coordinates.getY()));
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

    public Mall getMall() {
        return mall;
    }
    public Patch getWaitPatch() {
        return waitPatch;
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

    public boolean getIsGate1() {
        return isGate1;
    }

    public void setIsGate1(boolean isGate1) {
        this.isGate1 = isGate1;
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
                else if(goalAmenity.getClass() == Bench.class || goalAmenity.getClass() == Toilet.class || goalAmenity.getClass() == MallGate.class || goalAmenity.getClass() == Table.class) {
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

    public boolean chooseWaitPatch(){
        ArrayList<Patch> patchesToConsider = new ArrayList<>();
        if (this.getParent().getGender() == MallAgent.Gender.MALE){
            for (int i = 52; i < 59; i++) {
                for (int j = 8; j < 10; j++) {
                    patchesToConsider.add(mall.getPatch(i, j));
                }
            }
            for (int i = 45; i < 59; i++) {
                for (int j = 106; j < 108; j++) {
                    patchesToConsider.add(mall.getPatch(i, j));
                }
            }
        }
        else{
            for (int i = 1; i < 8; i++) {
                for (int j = 8; j < 10; j++) {
                    patchesToConsider.add(mall.getPatch(i, j));
                }
            }
            for (int i = 1; i < 15; i++) {
                for (int j = 119; j < 121; j++) {
                    patchesToConsider.add(mall.getPatch(i, j));
                }
            }
        }
        this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
        return true;
    }

    public boolean chooseGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.mall.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

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

    public void chooseRandomDigital() {
        if (this.goalAmenity == null) {
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

    public void chooseRandomTable() {
        if (this.goalAmenity == null) {
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

    public void chooseRandomTablePatron(String type) {
        if (this.goalAmenity == null) {
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
            ArrayList<Table> tables = new ArrayList<>();

            if (this.leaderAgent == null) {
                if (type == "RESTO") {
                    boolean isResto1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

                    if (isResto1) {
                        for (int i = 0; i < 16; i++) {
                            tables.add(Main.mallSimulator.getMall().getTables().get(i));
                        }
                        for (int i = 35; i < 40; i++) {
                            tables.add(Main.mallSimulator.getMall().getTables().get(i));
                        }
                    }
                    else {
                        for (int i = 16; i < 32; i++) {
                            tables.add(Main.mallSimulator.getMall().getTables().get(i));
                        }
                        for (int i = 40; i < 45; i++) {
                            tables.add(Main.mallSimulator.getMall().getTables().get(i));
                        }
                    }
                }
                else {
                    for (int i = 32; i < 35; i++) {
                        tables.add(Main.mallSimulator.getMall().getTables().get(i));
                    }
                    for (int i = 45; i < 49; i++) {
                        tables.add(Main.mallSimulator.getMall().getTables().get(i));
                    }
                }

                for (Amenity amenity : tables) {
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

    public void chooseRandomAisle() {
        if (this.goalAmenity == null) {
            ArrayList<StoreAisle> aisles1 = new ArrayList<>();
            ArrayList<StoreAisle> aisles2 = new ArrayList<>();
            ArrayList<StoreAisle> aisles3 = new ArrayList<>();
            ArrayList<StoreAisle> aisles4 = new ArrayList<>();
            ArrayList<StoreAisle> aisles5 = new ArrayList<>();
            ArrayList<StoreAisle> aisles6 = new ArrayList<>();
            ArrayList<StoreAisle> aisles7 = new ArrayList<>();
            ArrayList<StoreAisle> aisles8 = new ArrayList<>();
            ArrayList<StoreAisle> aisles9 = new ArrayList<>();

            if (this.team == 1) {
                aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(0));
                aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(1));
                for (int i = 27; i < 32; i++) {
                    aisles1.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
                }
                this.goalAmenity = aisles1.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles1.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 2) {
                aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(32));
                aisles2.add(Main.mallSimulator.getMall().getStoreAisles().get(33));
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
                aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(34));
                aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(35));
                for (int i = 12; i < 16; i++) {
                    aisles4.add(Main.mallSimulator.getMall().getStoreAisles().get(i));
                }
                this.goalAmenity = aisles4.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles4.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 5) {
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(16));
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(17));
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(36));
                aisles5.add(Main.mallSimulator.getMall().getStoreAisles().get(37));
                this.goalAmenity = aisles5.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles5.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 6) {
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(18));
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(19));
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(38));
                aisles6.add(Main.mallSimulator.getMall().getStoreAisles().get(39));
                this.goalAmenity = aisles6.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles6.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 7) {
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(20));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(21));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(22));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(40));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(41));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(42));
                aisles7.add(Main.mallSimulator.getMall().getStoreAisles().get(43));
                this.goalAmenity = aisles7.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles7.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 8) {
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(23));
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(24));
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(44));
                aisles8.add(Main.mallSimulator.getMall().getStoreAisles().get(45));
                this.goalAmenity = aisles8.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles8.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
            else if (this.team == 9) {
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(25));
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(26));
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(46));
                aisles9.add(Main.mallSimulator.getMall().getStoreAisles().get(47));
                this.goalAmenity = aisles9.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(aisles9.size()));
                this.goalAttractor = goalAmenity.getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(goalAmenity.getAttractors().size()));
            }
        }
    }

    public boolean chooseBathroomGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.mall.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (parent.getGender() == MallAgent.Gender.MALE) {
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

    public void chooseKiosk() {
        if (this.goalAmenity == null) {
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
        double minimumDistance = Double.MAX_VALUE;
        double distance;

        Amenity.AmenityBlock nearestAttractor = null;
        if(getWaitPatch() != null){
            distance = Coordinates.distance(this.position,getWaitPatch().getPatchCenterCoordinates());
            minimumDistance = distance;
        }
        else {
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

    public void moveSocialForce() { // Make the agent move in accordance with social forces
        final int noNewPatchesSeenTicksThreshold = 5; // If the agent has not seen new patches for more than this number of ticks, the agent will be considered stuck
        final int unstuckTicksThreshold = 60; // Stuck agent must move this no. of ticks
        final double noMovementThreshold = 0.01 * this.preferredWalkingDistance; // If distance the agent moves per tick is less than this distance, this agent is considered to not have moved
        final double noNewPatchesSeenThreshold = 5; // MallAgent hasn't moved if new patches seen are less than this
        final double slowdownStartDistance = 2.0; // The distance to another agent before this agent slows down
        int numberOfObstacles = 0; // Count the number of obstacles in the relevant patches
        final double minimumAgentStopDistance = 0.6; // The distance from the agent's center by which repulsive effects from agents are at a maximum
        final double minimumObstacleStopDistance = 0.6; // The distance from the agent's center by which repulsive effects from obstacles are at a maximum

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

            if (this.currentState.getName() != MallState.Name.GOING_TO_SECURITY && this.currentState.getName() != MallState.Name.GOING_HOME
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

                    if (!otherAgent.equals(this.getParent())) {
                        double distanceToOtherAgent = Coordinates.distance(this.position, mallAgent.getAgentMovement().getPosition());

                        if (distanceToOtherAgent <= slowdownStartDistance) {
                            final int maximumAgentCountTolerated = 5;

                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;

                            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumAgentCountTolerated, minimumAgentCount, maximumDistance, maximumAgentCount, minimumDistance);
                            Vector agentRepulsiveForce = computeSocialForceFromAgent(mallAgent, distanceToOtherAgent, computedMaximumDistance, minimumAgentStopDistance, this.preferredWalkingDistance);
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

                    if (this.isStuck &&
                            (((this.currentAction.getName() == MallAction.Name.GO_THROUGH_SCANNER || this.currentAction.getName() == MallAction.Name.QUEUE_KIOSK && this.movementCounter >= unstuckTicksThreshold)
                                    || this.currentAction.getName() != MallAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != MallAction.Name.QUEUE_KIOSK && this.newPatchesSeenCounter >= unstuckTicksThreshold                                    ))) {
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

    private Vector computeSocialForceFromAgent(MallAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
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

        double modifiedDistanceToObstacle;
        modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);
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
        for (int i = 1; i < this.goalQueueingPatchField.getAssociatedPatches().size(); i++) {
            path.push(this.goalQueueingPatchField.getAssociatedPatches().get(i));
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
            switch (this.getParent().getPersona()) {
                case LOITER_FAMILY, ERRAND_FAMILY -> {
                    if (this.getParent().isLeader())
                        MallSimulator.currentFamilyCount--;
                }
                case LOITER_FRIENDS, ERRAND_FRIENDS -> {
                    if (this.getParent().isLeader())
                        MallSimulator.currentFriendsCount--;
                }
                case LOITER_COUPLE -> {
                    if (this.getParent().isLeader())
                        MallSimulator.currentCoupleCount--;
                }
                case LOITER_ALONE, ERRAND_ALONE -> MallSimulator.currentAloneCount--;
            }

            this.currentPatch.getAgents().remove(this.parent);
            this.getMall().getAgents().remove(this.parent);

            SortedSet<Patch> currentPatchSet = this.getMall().getAgentPatchSet();
            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
                currentPatchSet.remove(this.currentPatch);
                MallSimulator.currentPatronCount--;
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
            if (patch.getAmenityBlock().getParent().getClass() == Security.class || patch.getAmenityBlock().getParent().getClass() == Table.class || patch.getAmenityBlock().getParent().getClass() == Toilet.class || patch.getAmenityBlock().getParent().getClass() == Bench.class || patch.getAmenityBlock().getParent().getClass() == StoreAisle.class) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

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

        while (distanceCovered <= distanceToTargetCoordinates) {
            if (includeStartingPatch || !this.mall.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.mall.getPatch(currentPosition), goalAmenity)) {
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
                MallSimulator.currentPatchCount[27][97]++;
            }
            else if (goalAmenity == mall.getKiosks().get(1)) {
                MallSimulator.currentPatchCount[29][28]++;
            }
            else if (goalAmenity == mall.getKiosks().get(2)) {
                MallSimulator.currentPatchCount[22][53]++;
            }
            else if (goalAmenity == mall.getKiosks().get(3)) {
                MallSimulator.currentPatchCount[22][70]++;
            }
            else if (goalAmenity == mall.getKiosks().get(4)) {
                MallSimulator.currentPatchCount[22][87]++;
            }
            else if (goalAmenity == mall.getKiosks().get(5)) {
                MallSimulator.currentPatchCount[33][53]++;
            }
            else if (goalAmenity == mall.getKiosks().get(6)) {
                MallSimulator.currentPatchCount[33][70]++;
            }
            else if (goalAmenity == mall.getKiosks().get(7)) {
                MallSimulator.currentPatchCount[33][87]++;
            }
            MallSimulator.currentPatronStaffKioskCount++;
            this.heading = 90;
            this.interactionDuration = this.duration;
        }
        else if (agentPersona == MallAgent.Persona.GUARD) {
            if (isGate1) {
                MallSimulator.currentPatchCount[33][2]++;
            }
            else {
                MallSimulator.currentPatchCount[41][2]++;
            }
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
            MallSimulator.currentCooperativeCount++;
            MallSimulator.averageCooperativeDuration = (MallSimulator.averageCooperativeDuration * (MallSimulator.currentCooperativeCount - 1) + this.interactionDuration) / MallSimulator.currentCooperativeCount;
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

    public void rollAgentInteraction(MallAgent agent){
        double IOS1 = mall.getIOS().get(this.getParent().getId()).get(agent.getId());
        double IOS2 = mall.getIOS().get(agent.getId()).get(this.getParent().getId());
        double CHANCE1 = Simulator.roll();
        double CHANCE2 = Simulator.roll();
        double interactionStdDeviation, interactionMean;
        if (CHANCE1 < IOS1 && CHANCE2 < IOS2){
            CHANCE1 = Simulator.roll() * IOS1;
            CHANCE2 = Simulator.roll() * IOS2;
            double CHANCE = (CHANCE1 + CHANCE2) / 2;
            double CHANCE_NONVERBAL1 = ((double) mall.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE1 = ((double) mall.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE1 = ((double) mall.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100,
                    CHANCE_NONVERBAL2 = ((double) mall.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE2 = ((double) mall.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE2 = ((double) mall.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100;
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
            this.isInteracting = true;
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
                    case CONCIERGER -> MallSimulator.currentPatronConciergerCount++;
                    case JANITOR -> MallSimulator.currentPatronJanitorCount++;
                }
            }
            else if (this.parent.getType() == MallAgent.Type.JANITOR){
                switch (agent.getType()){
                    case JANITOR -> MallSimulator.currentJanitorJanitorCount++;
                }
            }
            this.interactionDuration = (int) (Math.floor(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean));
            if (this.interactionDuration < 0)
                this.interactionDuration = 0;
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
        if (this.interactionDuration <= 0) {
            this.isInteracting = false;
            this.interactionType = null;
        }
        else {
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