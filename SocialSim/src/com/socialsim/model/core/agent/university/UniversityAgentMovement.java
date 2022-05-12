package com.socialsim.model.core.agent.university;

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
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchfield.*;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;
import com.socialsim.model.simulator.Simulator;
import com.socialsim.model.simulator.university.UniversitySimulator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UniversityAgentMovement extends AgentMovement {

    public static int defaultNonverbalMean = 1;
    public static int defaultNonverbalStdDev = 1;
    public static int defaultCooperativeMean = 24;
    public static int defaultCooperativeStdDev = 6;
    public static int defaultExchangeMean = 24;
    public static int defaultExchangeStdDev = 6;
    public static int defaultFieldOfView = 30;

    private final UniversityAgent parent;
    private final Coordinates position;
    private final University university;
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
    private UniversityRoutePlan routePlan;
    private AgentPath currentPath;
    private int stateIndex;
    private int actionIndex;
    private int returnIndex;
    private UniversityState currentState;
    private UniversityAction currentAction;
    private boolean isWaitingOnAmenity;
    private boolean hasEncounteredAgentToFollow;
    private UniversityAgent agentFollowedWhenAssembling;
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
    private InteractionType interactionType;


    public enum InteractionType {
        NON_VERBAL, COOPERATIVE, EXCHANGE
    }

    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    public UniversityAgentMovement(Patch spawnPatch, UniversityAgent parent, double baseWalkingDistance, Coordinates coordinates, long tickEntered) { // For inOnStart agents
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());

        final double interQuartileRange = 0.12;
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        this.currentPatch = spawnPatch;
        this.currentPatch.getAgents().add(parent);
        this.university = (University) currentPatch.getEnvironment();

        if (parent.getInOnStart()) {
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.fieldOfViewAngle = this.university.getFieldOfView();
        }
        else {
            this.proposedHeading = Math.toRadians(90.0);
            this.heading = Math.toRadians(90.0);
            this.fieldOfViewAngle = this.university.getFieldOfView();
        }

        this.currentPatchField = null;
        this.tickEntered = (int) tickEntered;

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal();

        this.routePlan = new UniversityRoutePlan(parent, university, currentPatch, (int) tickEntered);
        this.stateIndex = 0;
        this.actionIndex = 0;
        this.currentState = this.routePlan.getCurrentState();
        this.currentAction = this.routePlan.getCurrentState().getActions().get(actionIndex);
        if (!parent.getInOnStart()) {
            this.currentAmenity = university.getUniversityGates().get(1);
        }
        if (this.currentAction.getDestination() != null) {
            this.goalAttractor = this.currentAction.getDestination().getAmenityBlock();
        }
        if (this.currentAction.getDuration() != 0) {
            this.duration = this.currentAction.getDuration();
        }

        this.isInteracting = false;

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

        Patch newPatch = this.university.getPatch(new Coordinates(coordinates.getX(), coordinates.getY()));
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

    public UniversityState getCurrentState() {
        return currentState;
    }

    public void setNextState(int i) {
        this.currentState = this.currentState.getRoutePlan().setNextState(i);
    }
    public void setPreviousState(int i) {
        this.currentState = this.currentState.getRoutePlan().setPreviousState(i);
    }

    public UniversityAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(UniversityAction currentAction) {
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
    public Patch getWaitPatch() {
        return waitPatch;
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

    public UniversityRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(UniversityRoutePlan routePlan) {
        this.routePlan = routePlan;
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
                else if(goalAmenity.getClass() == Bench.class || goalAmenity.getClass() == Chair.class || goalAmenity.getClass() == Door.class || goalAmenity.getClass() == Toilet.class || goalAmenity.getClass() == UniversityGate.class || goalAmenity.getClass() == StudyTable.class || goalAmenity.getClass() == EatTable.class || goalAmenity.getClass() == LabTable.class) {
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

    public boolean chooseGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.university.getAmenityList(nextAmenityClass);
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
    public boolean chooseWaitPatch(int classKey){
        if (classKey == 0){
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 26; i < 28; i++){
                for (int j = 5; j < 17; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }
        else if(classKey == 1){
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 26; i < 28; i++){
                for (int j = 25; j < 41; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }
        else if(classKey == 2){
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 32; i < 34; i++){
                for (int j = 23; j < 35; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }
        else if(classKey == 3){
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 32; i < 34; i++){
                for (int j = 42; j < 54; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }
        else if(classKey == 4){
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 32; i < 34; i++){
                for (int j = 61; j < 73; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }
        else if(classKey == 5){
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 32; i < 34; i++){
                for (int j = 80; j < 92; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }
        else if(classKey == 6){ // 6 for bathroom specifically
            ArrayList<Patch> patchesToConsider = new ArrayList<>();
            for (int i = 1; i < 18; i++){
                for (int j = 63; j < 65; j++){
                    patchesToConsider.add(university.getPatch(i, j));
                }
            }
            this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
            return true;
        }

        return false;
    }
    public boolean chooseStaffroomGoal(Class<? extends Amenity> nextAmenityClass) {
        boolean isNotReserved = false;
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.university.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getKey().getClass() == StaffOffice.class) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
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
                    isNotReserved = true;
                    break;
                }
            }

            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
        }
        return isNotReserved;
    }

    public boolean chooseClassroomGoal(Class<? extends Amenity> nextAmenityClass, int classKey) {
        boolean allChairsReserved = true;
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.university.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getValue() == classKey) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
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
                    allChairsReserved = false;
                    break;
                }
            }

            this.goalAmenity = chosenAmenity;
            this.goalAttractor = chosenAttractor;
        }
        return allChairsReserved;
    }

    public boolean chooseBathroomGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null) {
            List<? extends Amenity> amenityListInFloor = this.university.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (parent.getGender() == UniversityAgent.Gender.MALE) {
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


    public void chooseStall() {
        if (this.goalAmenity == null) {
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
            this.goalQueueingPatchField = chosenAttractor.getPatch().getQueueingPatchField().getKey();
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

            if(this.currentState.getName() != UniversityState.Name.GOING_TO_SECURITY && this.currentState.getName() != UniversityState.Name.GOING_HOME
                    && this.currentAction.getName() != UniversityAction.Name.QUEUE_VENDOR && this.currentAction.getName() != UniversityAction.Name.CHECKOUT
                    && this.currentAction.getName() != UniversityAction.Name.QUEUE_FOUNTAIN && this.currentAction.getName() != UniversityAction.Name.DRINK_FOUNTAIN
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Bathroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != StudyArea.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Cafeteria.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Classroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Laboratory.class)) {
                for (Agent otherAgent : patch.getAgents()) {
                    UniversityAgent universityAgent = (UniversityAgent) otherAgent;
                    if (agentsProcessed == agentsProcessedLimit) {
                        break;
                    }

                    if (!otherAgent.equals(this.getParent())) {
                        double distanceToOtherAgent = Coordinates.distance(this.position, universityAgent.getAgentMovement().getPosition());

                        if (distanceToOtherAgent <= slowdownStartDistance) {
                            final int maximumAgentCountTolerated = 5;
                            final int minimumAgentCount = 1;
                            final double maximumDistance = 2.0;
                            final int maximumAgentCount = 5;
                            final double minimumDistance = 0.7;

                            double computedMaximumDistance = computeMaximumRepulsionDistance(numberOfObstacles, maximumAgentCountTolerated, minimumAgentCount, maximumDistance, maximumAgentCount, minimumDistance);
                            Vector agentRepulsiveForce = computeSocialForceFromAgent(universityAgent, distanceToOtherAgent, computedMaximumDistance, minimumAgentStopDistance, this.preferredWalkingDistance);
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
            this.motivationForce = Vector.computeResultantVector(new Coordinates(this.position), vectorsToAdd); // Finally, compute the final motivation force

            if (this.motivationForce != null) {
                if (this.motivationForce.getMagnitude() > this.preferredWalkingDistance) {
                    this.motivationForce.adjustMagnitude(this.preferredWalkingDistance);
                }

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

                    if (this.currentAction.getName() != UniversityAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != UniversityAction.Name.QUEUE_VENDOR && this.currentAction.getName() != UniversityAction.Name.QUEUE_FOUNTAIN) {
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
                            (((this.currentAction.getName() == UniversityAction.Name.GO_THROUGH_SCANNER || this.currentAction.getName() == UniversityAction.Name.QUEUE_VENDOR || this.currentAction.getName() == UniversityAction.Name.QUEUE_FOUNTAIN && this.movementCounter >= unstuckTicksThreshold)
                                    || this.currentAction.getName() != UniversityAction.Name.GO_THROUGH_SCANNER && this.currentAction.getName() != UniversityAction.Name.QUEUE_VENDOR && this.currentAction.getName() != UniversityAction.Name.QUEUE_FOUNTAIN && this.newPatchesSeenCounter >= unstuckTicksThreshold                                    ))) {
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

    private Vector computeSocialForceFromAgent(UniversityAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
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
            this.currentPatch.getAgents().remove(this.parent);
            this.getUniversity().getAgents().remove(this.parent);

            SortedSet<Patch> currentPatchSet = this.getUniversity().getAgentPatchSet();
            if (currentPatchSet.contains(this.currentPatch) && hasNoAgent(this.currentPatch)) {
                currentPatchSet.remove(this.currentPatch);
            }
            switch (this.getParent().getType()){
                case STUDENT -> UniversitySimulator.currentStudentCount--;
                case PROFESSOR -> UniversitySimulator.currentProfessorCount--;
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
            else{
                agentPath = computePath(this.currentPatch,this.goalQueueingPatchField.getLastQueuePatch(), true, true);
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
            if (patch.getAmenityBlock().getParent().getClass() == Door.class || patch.getAmenityBlock().getParent().getClass() == Security.class || patch.getAmenityBlock().getParent().getClass() == Chair.class || patch.getAmenityBlock().getParent().getClass() == Toilet.class || patch.getAmenityBlock().getParent().getClass() == StudyTable.class || patch.getAmenityBlock().getParent().getClass() == EatTable.class || patch.getAmenityBlock().getParent().getClass() == Bench.class) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

    private boolean hasClearLineOfSight(Coordinates sourceCoordinates, Coordinates targetCoordinates, boolean includeStartingPatch) {
        if (hasObstacle(this.university.getPatch(targetCoordinates), goalAmenity)) {
            return false;
        }

        final double resolution = 0.2;
        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.university.getPatch(sourceCoordinates);
        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        while (distanceCovered <= distanceToTargetCoordinates) {
            if (includeStartingPatch || !this.university.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.university.getPatch(currentPosition), goalAmenity)) {
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

    public void forceStationedInteraction(UniversityAgent.Type agentType) {
        UniversitySimulator.currentPatchCount[currentPatch.getMatrixPosition().getRow()][currentPatch.getMatrixPosition().getColumn()]++;
        if (agentType == UniversityAgent.Type.PROFESSOR) {
            UniversitySimulator.currentStudentProfCount++;
            this.interactionDuration = (int) (Math.floor((Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 5 + 12)));
        }
        else if (agentType == UniversityAgent.Type.GUARD) {
            UniversitySimulator.currentPatchCount[33][2]++;
            if (parent.getType() == UniversityAgent.Type.PROFESSOR) {
                UniversitySimulator.currentProfGuardCount++;
            }
            else {
                UniversitySimulator.currentStudentGuardCount++;
            }
            this.interactionDuration = this.duration;
        }
        else if (agentType == UniversityAgent.Type.STAFF){
            if (parent.getType() == UniversityAgent.Type.PROFESSOR) {
                UniversitySimulator.currentProfStaffCount++;
            }
            else {
                UniversitySimulator.currentStudentStaffCount++;
            }
            this.interactionDuration = this.duration;
        }

        if (agentType != UniversityAgent.Type.GUARD) {
            UniversitySimulator.currentCooperativeCount++;
            UniversitySimulator.averageCooperativeDuration = (UniversitySimulator.averageCooperativeDuration * (UniversitySimulator.currentCooperativeCount - 1) + this.interactionDuration) / UniversitySimulator.currentCooperativeCount;
        }
        else {
            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            if (x < UniversityRoutePlan.CHANCE_GUARD_VERBAL) {
                UniversitySimulator.currentExchangeCount++;
                UniversitySimulator.averageExchangeDuration = (UniversitySimulator.averageExchangeDuration * (UniversitySimulator.currentExchangeCount - 1) + this.interactionDuration) / UniversitySimulator.currentExchangeCount;
            }
            else {
                UniversitySimulator.currentNonverbalCount++;
                UniversitySimulator.averageNonverbalDuration = (UniversitySimulator.averageNonverbalDuration * (UniversitySimulator.currentNonverbalCount - 1) + this.interactionDuration) / UniversitySimulator.currentNonverbalCount;
            }
        }

        this.isStationInteracting = true;
        this.interactionDuration = 0;
    }

    public void rollAgentInteraction(UniversityAgent agent){
        double CLASS_MULTIPLER_1 = 1, CLASS_MULTIPLIER_2 = 1;
        if (this.currentState.getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT || this.currentState.getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR){
            CLASS_MULTIPLER_1 = UniversityRoutePlan.CHANCE_NEED_CLASS_MULTIPLIER;
        }
        if (agent.getAgentMovement().getCurrentState().getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT || agent.getAgentMovement().getCurrentState().getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR){
            CLASS_MULTIPLIER_2 = UniversityRoutePlan.CHANCE_NEED_CLASS_MULTIPLIER;
        }
        double IOS1 = university.getIOS().get(this.getParent().getId()).get(agent.getId());
        double IOS2 = university.getIOS().get(agent.getId()).get(this.getParent().getId());
        double CHANCE1 = Simulator.roll() * CLASS_MULTIPLER_1;
        double CHANCE2 = Simulator.roll() * CLASS_MULTIPLIER_2;
        double interactionStdDeviation, interactionMean;
        if (CHANCE1 < IOS1 && CHANCE2 < IOS2){
            CHANCE1 = Simulator.roll() * IOS1;
            CHANCE2 = Simulator.roll() * IOS2;
            double CHANCE = (CHANCE1 + CHANCE2) / 2;
            double CHANCE_NONVERBAL1 = ((double) university.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE1 = ((double) university.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE1 = ((double) university.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100,
                    CHANCE_NONVERBAL2 = ((double) university.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE2 = ((double) university.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE2 = ((double) university.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100;

            if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2) / 2){
                UniversitySimulator.currentNonverbalCount++;
                this.getParent().getAgentMovement().setInteractionType(InteractionType.NON_VERBAL);
                agent.getAgentMovement().setInteractionType(InteractionType.NON_VERBAL);
                interactionMean = getUniversity().getNonverbalMean();
                interactionStdDeviation = getUniversity().getNonverbalStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2) / 2){
                UniversitySimulator.currentCooperativeCount++;
                this.getParent().getAgentMovement().setInteractionType(InteractionType.COOPERATIVE);
                agent.getAgentMovement().setInteractionType(InteractionType.COOPERATIVE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getUniversity().getCooperativeMean();
                interactionStdDeviation = getUniversity().getCooperativeStdDev();
            }
            else if (CHANCE < (CHANCE_NONVERBAL1 + CHANCE_NONVERBAL2 + CHANCE_COOPERATIVE1 + CHANCE_COOPERATIVE2 + CHANCE_EXCHANGE1 + CHANCE_EXCHANGE2) / 2){
                UniversitySimulator.currentExchangeCount++;
                this.getParent().getAgentMovement().setInteractionType(InteractionType.EXCHANGE);
                agent.getAgentMovement().setInteractionType(InteractionType.EXCHANGE);
                CHANCE1 = Simulator.roll() * IOS1;
                CHANCE2 = Simulator.roll() * IOS2;
                interactionMean = getUniversity().getExchangeMean();
                interactionStdDeviation = getUniversity().getExchangeStdDev();
            }
            else{
                return;
            }
            this.isInteracting = true;
            agent.getAgentMovement().setInteracting(true);

            if (this.parent.getType() == UniversityAgent.Type.STUDENT){
                switch (agent.getType()){
                    case STUDENT -> UniversitySimulator.currentStudentStudentCount++;
                    case PROFESSOR -> UniversitySimulator.currentStudentProfCount++;
                    case GUARD -> UniversitySimulator.currentStudentGuardCount++;
                    case JANITOR -> UniversitySimulator.currentStudentJanitorCount++;
                }
            }
            else if (this.parent.getType() == UniversityAgent.Type.PROFESSOR){
                switch (agent.getType()){
                    case STUDENT -> UniversitySimulator.currentStudentProfCount++;
                    case PROFESSOR -> UniversitySimulator.currentProfProfCount++;
                    case GUARD -> UniversitySimulator.currentProfGuardCount++;
                    case JANITOR -> UniversitySimulator.currentProfJanitorCount++;
                }
            }
            else if (this.parent.getType() == UniversityAgent.Type.GUARD){
                switch (agent.getType()){
                    case STUDENT -> UniversitySimulator.currentStudentGuardCount++;
                    case PROFESSOR -> UniversitySimulator.currentProfGuardCount++;
                    case JANITOR -> UniversitySimulator.currentGuardJanitorCount++;
                }
            }
            else if (this.parent.getType() == UniversityAgent.Type.JANITOR){
                switch (agent.getType()){
                    case STUDENT -> UniversitySimulator.currentStudentJanitorCount++;
                    case PROFESSOR -> UniversitySimulator.currentProfJanitorCount++;
                    case GUARD -> UniversitySimulator.currentGuardJanitorCount++;
                    case JANITOR -> UniversitySimulator.currentJanitorJanitorCount++;
                }
            }
            this.interactionDuration = (int) (Math.floor(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean));
            if (this.interactionDuration < 0)
                this.interactionDuration = 0;
            agent.getAgentMovement().setInteractionDuration(this.interactionDuration);
            if (agent.getAgentMovement().getInteractionType() == InteractionType.NON_VERBAL)
                UniversitySimulator.averageNonverbalDuration = (UniversitySimulator.averageNonverbalDuration * (UniversitySimulator.currentNonverbalCount - 1) + this.interactionDuration) / UniversitySimulator.currentNonverbalCount;
            else if (agent.getAgentMovement().getInteractionType() == InteractionType.COOPERATIVE)
                UniversitySimulator.averageCooperativeDuration = (UniversitySimulator.averageCooperativeDuration * (UniversitySimulator.currentCooperativeCount - 1) + this.interactionDuration) / UniversitySimulator.currentCooperativeCount;
            else if (agent.getAgentMovement().getInteractionType() == InteractionType.EXCHANGE)
                UniversitySimulator.averageExchangeDuration = (UniversitySimulator.averageExchangeDuration * (UniversitySimulator.currentExchangeCount - 1) + this.interactionDuration) / UniversitySimulator.currentExchangeCount;
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