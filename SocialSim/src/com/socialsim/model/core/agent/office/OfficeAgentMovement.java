package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.generic.pathfinding.AgentMovement;
import com.socialsim.model.core.agent.generic.pathfinding.AgentPath;
import com.socialsim.model.core.agent.university.UniversityAgent;
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

    public static int defaultNonverbalMean = 1;
    public static int defaultNonverbalStdDev = 1;
    public static int defaultCooperativeMean = 24;
    public static int defaultCooperativeStdDev = 6;
    public static int defaultExchangeMean = 24;
    public static int defaultExchangeStdDev = 6;
    public static int defaultFieldOfView = 30;

    private final OfficeAgent parent;
    private final Coordinates position;
    private final Office office;
    private final double baseWalkingDistance;
    private double preferredWalkingDistance;
    private double currentWalkingDistance;
    private double proposedHeading;
    private double heading;
    private int team;
    private Cubicle assignedCubicle;
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
    private OfficeRoutePlan routePlan;
    private AgentPath currentPath;
    private int stateIndex;
    private int actionIndex;
    private OfficeState currentState;
    private OfficeAction currentAction;
    private boolean isWaitingOnAmenity;
    private boolean hasEncounteredAgentToFollow;
    private OfficeAgent agentFollowedWhenAssembling;
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
    private final List<Vector> repulsiveForceFromAgents;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;
    private boolean isInteracting;
    private boolean isSimultaneousInteractionAllowed;
    private int interactionDuration;
    private OfficeAgentMovement.InteractionType interactionType;
    private Patch collabTablePatch;

    public enum InteractionType {
        NON_VERBAL, COOPERATIVE, EXCHANGE
    }

    public OfficeAgentMovement(Patch spawnPatch, OfficeAgent parent, double baseWalkingDistance, Coordinates coordinates, long tickEntered, int team, Cubicle assignedCubicle) { // For inOnStart agents
        this.parent = parent;
        this.position = new Coordinates(coordinates.getX(), coordinates.getY());
        this.team = team;
        this.assignedCubicle = assignedCubicle;

        final double interQuartileRange = 0.12;
        this.baseWalkingDistance = baseWalkingDistance + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interQuartileRange;
        this.preferredWalkingDistance = this.baseWalkingDistance;
        this.currentWalkingDistance = preferredWalkingDistance;

        this.currentPatch = spawnPatch;
        this.currentPatch.getAgents().add(parent);
        this.office = (Office) currentPatch.getEnvironment();

        if (parent.getInOnStart()) {
            this.proposedHeading = Math.toRadians(270.0);
            this.heading = Math.toRadians(270.0);
            this.fieldOfViewAngle = this.office.getFieldOfView();
        }
        else {
            this.proposedHeading = Math.toRadians(90.0);
            this.heading = Math.toRadians(90.0);
            this.fieldOfViewAngle = this.office.getFieldOfView();
        }

        this.currentPatchField = null;
        this.tickEntered = (int) tickEntered;

        this.recentPatches = new ConcurrentHashMap<>();
        repulsiveForceFromAgents = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();
        resetGoal();

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
    }

    public Patch getWaitPatch() {
        return waitPatch;
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

        Patch newPatch = this.office.getPatch(new Coordinates(coordinates.getX(), coordinates.getY()));
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
            if (patchToExplore != null && patchToExplore.equals(goalPatch)) {
                Stack<Patch> path = new Stack<>();
                if(getWaitPatch() != null){
                    path.push(goalPatch);
                }
                else if(goalAmenity.getClass() == Chair.class ||
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

    public boolean chooseWaitPatch(){
        ArrayList<Patch> patchesToConsider = new ArrayList<>();
        //TODO: Change according to available patchesToConsider for Bathrooms
        for (int i = 12; i < 44; i++){
            for (int j = 19; j < 21; j++){
                patchesToConsider.add(office.getPatch(i, j));
            }
        }
        this.waitPatch = patchesToConsider.get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(patchesToConsider.size()));
        return true;
    }

    public boolean chooseGoal(Class<? extends Amenity> nextAmenityClass) {
        if (this.goalAmenity == null && this.office.getAmenityList(nextAmenityClass) != null) {
            List<? extends Amenity> amenityListInFloor = this.office.getAmenityList(nextAmenityClass);
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

    public boolean chooseBreakroomSeat() {
        if (this.goalAmenity == null) {
            List<? extends Amenity> tables = this.office.getAmenityList(Table.class);
            List<? extends Amenity> couches = this.office.getAmenityList(Couch.class);
            List<? extends Amenity> amenityListInFloor = Stream.concat(tables.stream(), couches.stream()).collect(Collectors.toList());

            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (amenity.getAmenityBlocks().get(0).getPatch().getPatchField() != null && amenity.getAmenityBlocks().get(0).getPatch().getPatchField().getKey() == this.office.getBreakrooms().get(0)) {
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

    public boolean chooseCollaborationChair(){
        if(this.goalAmenity == null){
            List<Amenity> temp = new ArrayList<>();
            int start1 = 0, start2 = 0;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();
            int count = 1;
            int table = -1;

            List<? extends Amenity> amenityListInFloor = this.office.getAmenityList(CollabDesk.class);
            for (Amenity amenity : amenityListInFloor) {
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

                for (int i = start1; i < start1 + 51; i += 10) {
                    temp.add(this.office.getChairs().get(i));
                }
                for (int i = start2; i < start2 + 51; i += 10) {
                    temp.add(this.office.getChairs().get(i));
                }

                for (Amenity amenity : temp) {
                    for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                        double distanceToAttractor = Coordinates.distance(this.currentPatch, attractor.getPatch());
                        distancesToAttractors.put(attractor, distanceToAttractor);
                    }
                }

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

    public boolean chooseBathroomGoal(Class<? extends Amenity> nextAmenityClass){
        if(this.goalAmenity == null){
            List<? extends Amenity> amenityListInFloor = this.office.getAmenityList(nextAmenityClass);
            Amenity chosenAmenity = null;
            Amenity.AmenityBlock chosenAttractor = null;
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (parent.getGender() == OfficeAgent.Gender.MALE) {
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

        return false;
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
        if(getWaitPatch() != null){
            distance = Coordinates.distance(this.position,getWaitPatch().getPatchCenterCoordinates());
            minimumDistance = distance;
        }
        else{
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

            if(this.currentState.getName() != OfficeState.Name.GOING_TO_SECURITY && this.currentState.getName() != OfficeState.Name.GOING_HOME
                    && this.currentAction.getName() != OfficeAction.Name.QUEUE_PRINTER && this.currentAction.getName() != OfficeAction.Name.PRINTING
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Bathroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != OfficeRoom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != Breakroom.class)
                    && (this.currentPatch.getPatchField() != null && this.currentPatch.getPatchField().getKey().getClass() != MeetingRoom.class)) {
                for (Agent otherAgent : patch.getAgents()) {
                    OfficeAgent universityAgent = (OfficeAgent) otherAgent;
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

    private Vector computeSocialForceFromAgent(OfficeAgent agent, final double distanceToOtherAgent, final double maximumDistance, final double minimumDistance, final double maximumMagnitude) {
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

        double headingFromOtherObstacle = Coordinates.headingTowards(repulsionVectorStartingPosition, this.position); // Compute the heading from that origin point to this agent
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(repulsionVectorStartingPosition, headingFromOtherObstacle, repulsionMagnitude); // Then compute for a future position given the obstacle's position, the heading, and the magnitude

        return new Vector(repulsionVectorStartingPosition, headingFromOtherObstacle, obstacleRepulsionVectorFuturePosition, repulsionMagnitude); // Finally, given the current position, heading, and future position, create the vector from the obstacle to the current agent
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
        for (int i = 0; i < this.goalQueueingPatchField.getAssociatedPatches().size(); i++) {
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
                if (!(this.currentPath.getPath().size() > 1 && nextPatchInPath.getAmenityBlocksAround() == 0
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

        while (distanceCovered <= distanceToTargetCoordinates) {
            if (includeStartingPatch || !this.office.getPatch(currentPosition).equals(startingPatch)) {
                if (hasObstacle(this.office.getPatch(currentPosition), goalAmenity)) {
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

    public void rollAgentInteraction(OfficeAgent agent){
        double IOS1 = office.getIOS().get(this.getParent().getId()).get(agent.getId());
        double IOS2 = office.getIOS().get(agent.getId()).get(this.getParent().getId());
        double CHANCE1 = Simulator.roll();
        double CHANCE2 = Simulator.roll();
        double interactionStdDeviation, interactionMean;
        if (CHANCE1 < IOS1 && CHANCE2 < IOS2){
            CHANCE1 = Simulator.roll() * IOS1;
            CHANCE2 = Simulator.roll() * IOS2;
            double CHANCE = (CHANCE1 + CHANCE2) / 2;
            double CHANCE_NONVERBAL1 = ((double) office.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE1 = ((double) office.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE1 = ((double) office.getInteractionTypeChances().get(this.getParent().getPersonaActionGroup().getID()).get(this.getParent().getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100,
                    CHANCE_NONVERBAL2 = ((double) office.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(0)) / 100,
                    CHANCE_COOPERATIVE2 = ((double) office.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(1)) / 100,
                    CHANCE_EXCHANGE2 = ((double) office.getInteractionTypeChances().get(agent.getPersonaActionGroup().getID()).get(agent.getAgentMovement().getCurrentAction().getName().getID()).get(2)) / 100;
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

            this.isInteracting = true;
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
            this.interactionDuration = (int) (Math.floor(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * interactionStdDeviation + interactionMean));
            if (this.interactionDuration < 0)
                this.interactionDuration = 0;
            agent.getAgentMovement().setInteractionDuration(this.interactionDuration);
            if (agent.getAgentMovement().getInteractionType() == OfficeAgentMovement.InteractionType.NON_VERBAL)
                OfficeSimulator.averageNonverbalDuration = (OfficeSimulator.averageNonverbalDuration * (OfficeSimulator.currentNonverbalCount - 1) + this.interactionDuration) / OfficeSimulator.currentNonverbalCount;
            else if (agent.getAgentMovement().getInteractionType() == OfficeAgentMovement.InteractionType.COOPERATIVE)
                OfficeSimulator.averageCooperativeDuration = (OfficeSimulator.averageCooperativeDuration * (OfficeSimulator.currentCooperativeCount - 1) + this.interactionDuration) / OfficeSimulator.currentCooperativeCount;
            else if (agent.getAgentMovement().getInteractionType() == OfficeAgentMovement.InteractionType.EXCHANGE)
                OfficeSimulator.averageExchangeDuration = (OfficeSimulator.averageExchangeDuration * (OfficeSimulator.currentExchangeCount - 1) + this.interactionDuration) / OfficeSimulator.currentExchangeCount;
        }
    }

    public void interact(){
        if (this.interactionDuration <= 0){
            this.isInteracting = false;
            this.interactionType = null;
        }
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