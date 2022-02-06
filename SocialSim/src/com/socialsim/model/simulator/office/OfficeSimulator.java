package com.socialsim.model.simulator.office;

import com.socialsim.controller.Main;
import com.socialsim.controller.office.controls.OfficeScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.office.*;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class OfficeSimulator extends Simulator {

    public static int defaultMaxClients = 6;
    public static int defaultMaxDrivers = 3;
    public static int defaultMaxVisitors = 1;

    private static Office office;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    private final int MAX_BOSSES = 1;
    private final int MAX_SECRETARIES = 1;
    public static List<Integer> MANAGERS_1 = new LinkedList<Integer>(List.of(11));
    public static List<Integer> MANAGERS_2 = new LinkedList<Integer>(List.of(19));
    public static List<Integer> MANAGERS_3 = new LinkedList<Integer>(List.of(27));
    public static List<Integer> MANAGERS_4 = new LinkedList<Integer>(List.of(35));
    public static List<Integer> BUSINESS_1 = new LinkedList<Integer>(List.of(0, 1, 2, 3, 4));
    public static List<Integer> BUSINESS_2 = new LinkedList<Integer>(List.of(36, 37, 38, 39, 40, 41, 42));
    public static List<Integer> BUSINESS_3 = new LinkedList<Integer>(List.of(44, 45, 46, 47, 48, 49, 50));
    public static List<Integer> BUSINESS_4 = new LinkedList<Integer>(List.of(52, 53, 54, 55, 56, 57, 58));
    public static List<Integer> RESEARCH_1 = new LinkedList<Integer>(List.of(5, 6, 7, 8, 9));
    public static List<Integer> RESEARCH_2 = new LinkedList<Integer>(List.of(12, 13, 14, 15, 16, 17, 18));
    public static List<Integer> RESEARCH_3 = new LinkedList<Integer>(List.of(20, 21, 22, 23, 24, 25, 26));
    public static List<Integer> RESEARCH_4 = new LinkedList<Integer>(List.of(28, 29, 30, 31, 32, 33, 34));
    public static List<Integer> TECHNICAL_1 = new LinkedList<Integer>(List.of(10));
    public static List<Integer> TECHNICAL_2 = new LinkedList<Integer>(List.of(43));
    public static List<Integer> TECHNICAL_3 = new LinkedList<Integer>(List.of(51));
    public static List<Integer> TECHNICAL_4 = new LinkedList<Integer>(List.of(59));


    public static int currentManagerCount = 0;
    public static int currentBusinessCount = 0;
    public static int currentResearchCount = 0;
    public static int currentTechnicalCount = 0;
    public static int currentSecretaryCount = 0;
    public static int currentClientCount = 0;
    public static int currentDriverCount = 0;
    public static int currentVisitorCount = 0;
    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;

    public static int averageNonverbalDuration = 0;
    public static int averageCooperativeDuration = 0;
    public static int averageExchangeDuration = 0;

    public static int currentTeam1Count = 0;
    public static int currentTeam2Count = 0;
    public static int currentTeam3Count = 0;
    public static int currentTeam4Count = 0;

    public static int currentBossManagerCount = 0;
    public static int currentBossBusinessCount = 0;
    public static int currentBossResearcherCount = 0;
    public static int currentBossTechnicalCount = 0;
    public static int currentBossJanitorCount = 0;
    public static int currentBossClientCount = 0;
    public static int currentBossDriverCount = 0;
    public static int currentBossVisitorCount = 0;
    public static int currentBossGuardCount = 0;
    public static int currentBossReceptionistCount = 0;
    public static int currentBossSecretaryCount = 0;

    public static int currentManagerManagerCount = 0;
    public static int currentManagerBusinessCount = 0;
    public static int currentManagerResearcherCount = 0;
    public static int currentManagerTechnicalCount = 0;
    public static int currentManagerJanitorCount = 0;
    public static int currentManagerClientCount = 0;
    public static int currentManagerDriverCount = 0;
    public static int currentManagerVisitorCount = 0;
    public static int currentManagerGuardCount = 0;
    public static int currentManagerReceptionistCount = 0;
    public static int currentManagerSecretaryCount = 0;

    public static int currentBusinessBusinessCount = 0;
    public static int currentBusinessResearcherCount = 0;
    public static int currentBusinessTechnicalCount = 0;
    public static int currentBusinessJanitorCount = 0;
    public static int currentBusinessClientCount = 0;
    public static int currentBusinessDriverCount = 0;
    public static int currentBusinessVisitorCount = 0;
    public static int currentBusinessGuardCount = 0;
    public static int currentBusinessReceptionistCount = 0;
    public static int currentBusinessSecretaryCount = 0;

    public static int currentResearcherResearcherCount = 0;
    public static int currentResearcherTechnicalCount = 0;
    public static int currentResearcherJanitorCount = 0;
    public static int currentResearcherClientCount = 0;
    public static int currentResearcherDriverCount = 0;
    public static int currentResearcherVisitorCount = 0;
    public static int currentResearcherGuardCount = 0;
    public static int currentResearcherReceptionistCount = 0;
    public static int currentResearcherSecretaryCount = 0;

    public static int currentTechnicalTechnicalCount = 0;
    public static int currentTechnicalJanitorCount = 0;
    public static int currentTechnicalClientCount = 0;
    public static int currentTechnicalDriverCount = 0;
    public static int currentTechnicalVisitorCount = 0;
    public static int currentTechnicalGuardCount = 0;
    public static int currentTechnicalReceptionistCount = 0;
    public static int currentTechnicalSecretaryCount = 0;

    public static int currentJanitorJanitorCount = 0;
    public static int currentJanitorClientCount = 0;
    public static int currentJanitorDriverCount = 0;
    public static int currentJanitorVisitorCount = 0;
    public static int currentJanitorGuardCount = 0;
    public static int currentJanitorReceptionistCount = 0;
    public static int currentJanitorSecretaryCount = 0;

    public static int currentClientClientCount = 0;
    public static int currentClientDriverCount = 0;
    public static int currentClientVisitorCount = 0;
    public static int currentClientGuardCount = 0;
    public static int currentClientReceptionistCount = 0;
    public static int currentClientSecretaryCount = 0;

    public static int currentDriverDriverCount = 0;
    public static int currentDriverVisitorCount = 0;
    public static int currentDriverGuardCount = 0;
    public static int currentDriverReceptionistCount = 0;
    public static int currentDriverSecretaryCount = 0;

    public static int currentVisitorVisitorCount = 0;
    public static int currentVisitorGuardCount = 0;
    public static int currentVisitorReceptionistCount = 0;
    public static int currentVisitorSecretaryCount = 0;

    public static int currentGuardGuardCount = 0;
    public static int currentGuardReceptionistCount = 0;
    public static int currentGuardSecretaryCount = 0;

    public static int currentReceptionistReceptionistCount = 0;
    public static int currentReceptionistSecretaryCount = 0;

    public static int currentSecretarySecretaryCount = 0;
    public static int[][] currentPatchCount;


    public OfficeSimulator() {
        this.office = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(9, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start(); // Start the simulation thread, but in reality it would be activated much later
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public AtomicBoolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public boolean isRunning() {
        return running.get();
    }

    public SimulationTime getSimulationTime() {
        return time;
    }

    public Semaphore getPlaySemaphore() {
        return playSemaphore;
    }

    public void resetToDefaultConfiguration(Office office) {
        this.office = office;
        this.time.reset();
        this.running.set(false);
        currentPatchCount = new int[office.getRows()][office.getColumns()];
    }

    public void spawnInitialAgents(Office office) {
        office.createInitialAgentDemographics(office.getMAX_CLIENTS(), getOffice().getMAX_DRIVERS(), getOffice().getMAX_VISITORS());
        OfficeAgent janitor = office.getAgents().get(0); // 0
        janitor.setAgentMovement(new OfficeAgentMovement(office.getPatch(6,23), janitor, 1.27, office.getPatch(6,23).getPatchCenterCoordinates(), -1, 0, null));
        office.getAgentPatchSet().add(janitor.getAgentMovement().getCurrentPatch());
        OfficeAgent.janitorCount++;
        OfficeAgent.agentCount++;

        OfficeAgent guard = office.getAgents().get(1); // 1
        guard.setAgentMovement(new OfficeAgentMovement(office.getPatch(57,35), guard, 1.27, office.getPatch(57,35).getPatchCenterCoordinates(), -1, 0, null));
        office.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
        OfficeAgent.guardCount++;
        OfficeAgent.agentCount++;

        OfficeAgent receptionist = office.getAgents().get(2); // 2
        receptionist.setAgentMovement(new OfficeAgentMovement(office.getPatch(46,37), receptionist, 1.27, office.getPatch(46,37).getPatchCenterCoordinates(), -1, 0, null));
        office.getAgentPatchSet().add(receptionist.getAgentMovement().getCurrentPatch());
        OfficeAgent.receptionistCount++;
        OfficeAgent.agentCount++;
    }

    public void reset() {
        this.time.reset();
    }

    private void start() {
        new Thread(() -> {
            final int speedAwarenessLimitMilliseconds = 10; // For times shorter than this, speed awareness will be implemented
            while (true) {
                try {
                    playSemaphore.acquire(); // Wait until the play button has been pressed

                    while (this.isRunning()) { // Keep looping until paused
                        long currentTick = this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5;
                        try {
                            updateAgentsInOffice(office, currentTick);
                            spawnAgent(office, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization
                        // If the refreshes are frequent enough, update the visualization in a speed-aware manner
                        ((OfficeScreenController) Main.mainScreenController).drawOfficeViewForeground(Main.officeSimulator.getOffice(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 6480) {
                            ((OfficeScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInOffice(Office office, long currentTick) throws InterruptedException { // Manage all agent-related updates
        moveAll(office, currentTick);
    }

    private static void moveAll(Office office, long currentTick) { // Make all agents move for one tick
        for (OfficeAgent agent : office.getMovableAgents()) {
            try {

                if (currentTick == 2160 && agent.getAgentMovement().getRoutePlan().getLUNCH_INSTANCE() != null) {
                    agent.getAgentMovement().setNextState(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().indexOf(
                            agent.getAgentMovement().getRoutePlan().getLUNCH_INSTANCE()
                    ) - 1);
                    agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().indexOf(
                            agent.getAgentMovement().getRoutePlan().getLUNCH_INSTANCE()
                    ));
                    agent.getAgentMovement().setActionIndex(0);
                    agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                    agent.getAgentMovement().resetGoal();
                }

                if (currentTick == 5760) {
                    agent.getAgentMovement().setNextState(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size()-2);
                    agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size()-1);
                    agent.getAgentMovement().setActionIndex(0);
                    agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                    agent.getAgentMovement().resetGoal();
                }

                if(agent.getAgentMovement() != null && agent.getTeam()>0 &&
                        currentTick == agent.getAgentMovement().getRoutePlan().getMeetingStart()){

                    // force agents to stop what they are doing and go to meeting
                    if(agent.getAgentMovement().getCurrentState().getName() == OfficeState.Name.WORKING){
                        agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() - 1);
                    }else if(agent.getAgentMovement().getCurrentState().getName() == OfficeState.Name.EATING_LUNCH){
                        agent.getAgentMovement().getRoutePlan().setLunchAmenity(null);
                        agent.getAgentMovement().getRoutePlan().setLunchAttractor(null);
                    }
                    // if current state is urgent, just consider it as finished

                    if(agent.getAgentMovement().getGoalAttractor() != null){
                        agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                    }
                    agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent.getAgentMovement().getStateIndex() + 1,
                            agent.getAgentMovement().getRoutePlan().addUrgentRoute("MEETING", agent));
                    agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                    agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                    agent.getAgentMovement().setActionIndex(0);
                    agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions()
                            .get(agent.getAgentMovement().getActionIndex()));
                    agent.getAgentMovement().resetGoal();
                }

                moveOne(agent, currentTick);
                agent.getAgentGraphic().change();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(OfficeAgent agent, long currentTick) throws Throwable {
        OfficeAgentMovement agentMovement = agent.getAgentMovement();

        OfficeAgent.Type type = agent.getType();
        OfficeAgent.Persona persona = agent.getPersona();
        OfficeState state = agentMovement.getCurrentState();
        OfficeAction action = agentMovement.getCurrentAction();
        Office officeInstance = agentMovement.getOffice();

        boolean isFull = false;

        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()){
            switch (type) {
            case JANITOR:
                if (state.getName() == OfficeState.Name.MAINTENANCE_BATHROOM) {
                    if (action.getName() == OfficeAction.Name.JANITOR_CLEAN_TOILET) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            agentMovement.checkIfStuck();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.MAINTENANCE_PLANT) {
                    if (action.getName() == OfficeAction.Name.JANITOR_WATER_PLANT) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            agentMovement.checkIfStuck();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setPreviousState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }

                break;

            case BOSS:
                if (state.getName() == OfficeState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == OfficeAction.Name.GOING_TO_SECURITY_QUEUE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.officeSimulator.getOffice().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.GO_THROUGH_SCANNER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setCanUrgent(-1);
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.WORKING) {
                    if (action.getName() == OfficeAction.Name.GO_TO_OFFICE_ROOM) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination()
                                    .getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if(agentMovement.hasAgentReachedFinalPatchInPath()){
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                }
                            }
                        }else if(currentTick >= 5760){
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.resetGoal();
                        }
                        else if ((currentTick < 2060 || (currentTick < 5660 && currentTick > 2520)) &&
                                agentMovement.getRoutePlan().getCanUrgent() <= 0){
                            double CHANCE = Simulator.roll();

                            if(CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_PM() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                        agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setFromBathPM(true);
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.EATING_LUNCH) {
                    if (action.getName() == OfficeAction.Name.GO_TO_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {

                            if(agentMovement.getRoutePlan().getLunchAmenity() == null){
                                double CHANCE = Simulator.roll();

                                if(persona == OfficeAgent.Persona.PROFESSIONAL_BOSS){
                                    if(CHANCE < OfficeRoutePlan.INT_LUNCH){
                                        if(!agentMovement.chooseBreakroomSeat()){
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        }
                                    }else{
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    }
                                }else{
                                    if(CHANCE < OfficeRoutePlan.APP_BOSS_LUNCH){
                                        if(!agentMovement.chooseBreakroomSeat()){
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        }
                                    }else{
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    }
                                }
                            }

                            else{
                                agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getLunchAmenity());
                                agentMovement.setGoalAttractor(agentMovement.getRoutePlan().getLunchAttractor());
                            }

                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if(agentMovement.getRoutePlan().getLastDuration() == -1){
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }else{
                                        agentMovement.setDuration(agentMovement.getRoutePlan().getLastDuration());
                                        agentMovement.getRoutePlan().setLastDuration(-1);
                                    }
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAttractor(agentMovement.getGoalAttractor());
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.EAT_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.resetGoal();
                            agentMovement.getRoutePlan().setLunchAmenity(null);
                            agentMovement.getRoutePlan().setLunchAttractor(null);
                        }
                        else if (agentMovement.getRoutePlan().getCanUrgent() <= 0){
                            double CHANCE = Simulator.roll();

                            if(CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                        agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.NEEDS_BATHROOM){
                    if (action.getName()== OfficeAction.Name.GO_TO_BATHROOM){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseGoal(Toilet.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                if(agentMovement.getRoutePlan().isFromBathPM()){
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                    agentMovement.getRoutePlan().setBATH_PM(1);
                                }else{
                                    agentMovement.getRoutePlan().setBATH_LUNCH(1);
                                }

                                agentMovement.getRoutePlan().resetCanUrgent();
                            }
                        }
                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.RELIEVE_IN_CUBICLE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.FIND_SINK){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseGoal(Sink.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.WASH_IN_SINK){
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.MEETING){
                    if (action.getName() == OfficeAction.Name.GO_MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseMeetingGoal(agentMovement.getRoutePlan().getMeetingRoom());
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                            get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                }
                            }
                            else {
                                if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                    while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                        agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                                    get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.WAIT_MEETING){
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.MEETING){
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        if (agentMovement.getRoutePlan().getMeetingEnd() <= currentTick) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                            agentMovement.getRoutePlan().setCanUrgent(-1);
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.GOING_HOME) {
                    if (action.getName() == OfficeAction.Name.LEAVE_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getOfficeGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.despawn();
                                }
                            }
                        }
                    }
                }
                break;

            case MANAGER: case BUSINESS: case RESEARCHER: case TECHNICAL:

                if (state.getName() == OfficeState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == OfficeAction.Name.GOING_TO_SECURITY_QUEUE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.officeSimulator.getOffice().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.GO_THROUGH_SCANNER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setCanUrgent(-1);
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.WORKING) {
                    if (action.getName() == OfficeAction.Name.GO_TO_STATION) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if(agentMovement.hasAgentReachedFinalPatchInPath()){
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                }
                            }
                        }else if((currentTick < 2060 ||
                                (currentTick < 5660 && currentTick > 2520)) && agentMovement.getRoutePlan().getCanUrgent()
                                <= 0){// if time is before 12pm and after 1230pm

                            double CHANCE = Simulator.roll();

                            if(CHANCE < OfficeRoutePlan.BATH_CHANCE){

                                if(currentTick < 2160 && agentMovement.getRoutePlan().getBATH_AM() > 0){

                                    agentMovement.getRoutePlan().setFromBathAM(true);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                            agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }else if(agentMovement.getRoutePlan().getBATH_PM() > 0){

                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                            agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromBathPM(true);
                                }
                            }
                            else{

                                int left = 0;
                                if(persona == OfficeAgent.Persona.EXT_BUSINESS || persona == OfficeAgent.Persona.INT_BUSINESS){
                                    left = agentMovement.getRoutePlan().getPRINT_BUSINESS();
                                }else if (persona == OfficeAgent.Persona.EXT_RESEARCHER || persona == OfficeAgent.Persona.INT_RESEARCHER){
                                    left = agentMovement.getRoutePlan().getPRINT_RESEARCH();
                                }

                                if(CHANCE < OfficeRoutePlan.BATH_CHANCE +
                                        OfficeRoutePlan.PRINT_CHANCE && left > 0){

                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                            agentMovement.getRoutePlan().addUrgentRoute("PRINT", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get
                                            (agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                                }

                                if(CHANCE < OfficeRoutePlan.BATH_CHANCE +
                                        OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.TECHNICAL_CUBICLE_CHANCE
                                        && agentMovement.getRoutePlan().getTECHNICAL_CUBICLE_COUNT() > 0){

                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                            agentMovement.getRoutePlan().addUrgentRoute(agent, officeInstance));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get
                                            (agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());

                                }else if(CHANCE < OfficeRoutePlan.TECHNICAL_PRINTER_CHANCE + OfficeRoutePlan.BATH_CHANCE +
                                        OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.TECHNICAL_CUBICLE_CHANCE &&
                                        agentMovement.getRoutePlan().getTECHNICAL_PRINTER_COUNT() > 0){

                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                            agentMovement.getRoutePlan().addUrgentRoute("TECHNICAL_PRINTER", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get
                                            (agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                                }else if((currentTick < 1660 || (currentTick < 5260 && currentTick > 2520)) &&
                                        agentMovement.getRoutePlan().getCOLLABORATE_COUNT() > 0 &&
                                        agentMovement.getRoutePlan().getCanUrgent() <= 0 &&
                                        CHANCE < agentMovement.getRoutePlan().getCooperate(persona) +
                                        OfficeRoutePlan.TECHNICAL_PRINTER_CHANCE + OfficeRoutePlan.BATH_CHANCE +
                                        OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.TECHNICAL_CUBICLE_CHANCE){

                                    ArrayList<OfficeAgent> agents = officeInstance.getTeamMembers(agent.getTeam());
                                    for(OfficeAgent agent1 : agents){
                                        if(agent1.getAgentMovement().getCurrentAction().getName() ==
                                                OfficeAction.Name.GO_TO_STATION){
                                            agent1.getAgentMovement().setStateIndex(agent1.getAgentMovement().getStateIndex() - 1);
                                            agent1.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent1.getAgentMovement().getStateIndex() + 1,
                                                    agent1.getAgentMovement().getRoutePlan().addUrgentRoute("COLLABORATION", agent));
                                            agent1.getAgentMovement().setNextState(agent1.getAgentMovement().getStateIndex());
                                            agent1.getAgentMovement().setStateIndex(agent1.getAgentMovement().getStateIndex() + 1);
                                            agent1.getAgentMovement().setActionIndex(0);
                                            agent1.getAgentMovement().setCurrentAction(agent1.getAgentMovement().getCurrentState().getActions().get
                                                    (agent1.getAgentMovement().getActionIndex()));
                                            agent1.getAgentMovement().resetGoal();
                                        }else{
                                            agent1.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent1.getAgentMovement().getStateIndex() + 1,
                                                    agent1.getAgentMovement().getRoutePlan().addUrgentRoute("COLLABORATION", agent));
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_COLLAB) {
                    if (action.getName() == OfficeAction.Name.GO_TO_COLLAB) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(agentMovement.chooseCollaborationChair()){
                                agentMovement.getRoutePlan().resetCanUrgent();
                                agentMovement.getRoutePlan().setCOLLABORATE_COUNT(-1);
                            }else{ // no vacant table
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }

                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    }
                                }
                                else { // snap to goal patch
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                            if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                            }
                                        }
                                    }
                                }
                            }else{ // waiting to start collab
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                            get(agentMovement.getActionIndex()));
                                    agentMovement.getRoutePlan().setCollaborationEnd(currentTick,
                                            agentMovement.getCurrentAction().getDuration());
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.COLLABORATE){
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        if (agentMovement.getRoutePlan().getCollaborationEnd() <= currentTick) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                            agentMovement.removeCollaborationTeam();
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.MEETING){
                    if (action.getName() == OfficeAction.Name.GO_MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseMeetingGoal(agentMovement.getRoutePlan().getMeetingRoom());
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                            get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                }
                            }
                            else {
                                if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                    while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                        agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                                    get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.WAIT_MEETING){
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.MEETING){
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        if (agentMovement.getRoutePlan().getMeetingEnd() <= currentTick) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                            agentMovement.getRoutePlan().setCanUrgent(-1);
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_PRINT){
                    if(action.getName() == OfficeAction.Name.GO_TO_PRINTER){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            if(!agentMovement.chooseGoal(Printer.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().resetCanUrgent();
                            }
                        }
                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        if(persona == OfficeAgent.Persona.EXT_BUSINESS || persona == OfficeAgent.Persona.INT_BUSINESS){
                                            agentMovement.getRoutePlan().setPRINT_BUSINESS();
                                        }else if (persona == OfficeAgent.Persona.EXT_RESEARCHER || persona == OfficeAgent.Persona.INT_RESEARCHER){
                                            agentMovement.getRoutePlan().setPRINT_RESEARCH();
                                        }
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState()
                                                .getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.PRINTING){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);

                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.EATING_LUNCH) {
                    if (action.getName() == OfficeAction.Name.GO_TO_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(agentMovement.getRoutePlan().getLunchAmenity() == null){
                                double CHANCE = Simulator.roll();

                                if(persona == OfficeAgent.Persona.EXT_TECHNICAL || persona == OfficeAgent.Persona.EXT_BUSINESS
                                        || persona == OfficeAgent.Persona.EXT_RESEARCHER || persona == OfficeAgent.Persona.MANAGER){
                                    if(CHANCE < OfficeRoutePlan.EXT_LUNCH){
                                        if(!agentMovement.chooseBreakroomSeat()){
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        }
                                    }else{
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    }
                                }else{
                                    if(CHANCE < OfficeRoutePlan.INT_LUNCH){
                                        if(!agentMovement.chooseBreakroomSeat()){
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        }
                                    }else{
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    }
                                }
                            }

                            else{
                                agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getLunchAmenity());
                                agentMovement.setGoalAttractor(agentMovement.getRoutePlan().getLunchAttractor());
                            }
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if(agentMovement.getRoutePlan().getLastDuration() == -1){
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }else{
                                        agentMovement.setDuration(agentMovement.getRoutePlan().getLastDuration());
                                        agentMovement.getRoutePlan().setLastDuration(-1);
                                    }
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAttractor(agentMovement.getGoalAttractor());
                                }
                            }else { // snap to goal patch
                                if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                    while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                        agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if(agentMovement.getRoutePlan().getLastDuration() == -1){
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }else{
                                                agentMovement.setDuration(agentMovement.getRoutePlan().getLastDuration());
                                                agentMovement.getRoutePlan().setLastDuration(-1);
                                            }
                                            agentMovement.getRoutePlan().setCanUrgent(-1);
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                            agentMovement.getRoutePlan().setLunchAmenity(agentMovement.getGoalAmenity());
                                            agentMovement.getRoutePlan().setLunchAttractor(agentMovement.getGoalAttractor());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.EAT_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setDuration(agentMovement.getDuration() - 1);

                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.resetGoal();
                            agentMovement.getRoutePlan().setLunchAmenity(null);
                            agentMovement.getRoutePlan().setLunchAttractor(null);
                        }
                        else if (agentMovement.getRoutePlan().getCanUrgent() <= 0){
                            double CHANCE = Simulator.roll();

                            if(CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1,
                                        agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.NEEDS_BATHROOM){
                    if (action.getName()== OfficeAction.Name.GO_TO_BATHROOM){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseGoal(Toilet.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                if(agentMovement.getRoutePlan().isFromBathAM()){
                                    agentMovement.getRoutePlan().setFromBathAM(false);
                                }else if(agentMovement.getRoutePlan().isFromBathPM()){
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                }
                            }else{
                                if(agentMovement.getRoutePlan().isFromBathAM()){
                                    agentMovement.getRoutePlan().setFromBathAM(false);
                                    agentMovement.getRoutePlan().setBATH_AM(1);
                                }else if(agentMovement.getRoutePlan().isFromBathPM()){
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                    agentMovement.getRoutePlan().setBATH_PM(1);
                                }else{
                                    agentMovement.getRoutePlan().setBATH_LUNCH(1);
                                }

                                agentMovement.getRoutePlan().resetCanUrgent();
                            }
                        }
                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.RELIEVE_IN_CUBICLE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.FIND_SINK){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseGoal(Sink.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if(action.getName()==OfficeAction.Name.WASH_IN_SINK){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_FIX_PRINTER) {
                    if (action.getName() == OfficeAction.Name.TECHNICAL_GO_PRINTER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseGoal(Printer.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                        get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().resetCanUrgent();
                                agentMovement.getRoutePlan().setTECHNICAL_PRINTER_COUNT(1);
                            }
                        }
                        if(isFull){
                            isFull = false;
                        }else{
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions()
                                                .get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.FIX_PRINTER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().
                                    get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_FIX_CUBICLE) {
                    if (action.getName()== OfficeAction.Name.FIX_CUBICLE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            agentMovement.getGoalAttractor().setIsReserved(true);
                            agentMovement.getRoutePlan().resetCanUrgent();
                            agentMovement.getRoutePlan().setTECHNICAL_CUBICLE_COUNT(1);
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else{
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.GOING_HOME) {
                    if (action.getName() == OfficeAction.Name.LEAVE_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getOfficeGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            System.out.println(agent.getPersona().name() + " " + agentMovement.getCurrentPath().getPath());
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.despawn();
                                }
                            }
                        }
                    }
                }
                    break;

            case SECRETARY:
                if (state.getName() == OfficeState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == OfficeAction.Name.GOING_TO_SECURITY_QUEUE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.officeSimulator.getOffice().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.GO_THROUGH_SCANNER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.SECRETARY) {
                    if (action.getName() == OfficeAction.Name.GO_TO_OFFICE_ROOM) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.SECRETARY_STAY_PUT || action.getName() == OfficeAction.Name.SECRETARY_CHECK_CABINET) {

                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (action.getName() == OfficeAction.Name.SECRETARY_STAY_PUT) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            else {
                                agentMovement.chooseGoal(Cabinet.class);
                            }
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if(action.getName() == OfficeAction.Name.SECRETARY_STAY_PUT) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                }else {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() - 1);
                                }
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.GOING_HOME) {
                    if (action.getName() == OfficeAction.Name.LEAVE_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getOfficeGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.despawn();
                                }
                            }
                        }
                    }
                }

                break;

            case CLIENT: case DRIVER:
                if (state.getName() == OfficeState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == OfficeAction.Name.GOING_TO_SECURITY_QUEUE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.officeSimulator.getOffice().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.GO_THROUGH_SCANNER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.DRIVER) {
                    if (action.getName() == OfficeAction.Name.DRIVER_GO_RECEPTIONIST
                            || action.getName() == OfficeAction.Name.DRIVER_GO_COUCH) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (action.getName() == OfficeAction.Name.DRIVER_GO_RECEPTIONIST) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            else {
                                agentMovement.chooseGoal(Couch.class);
                            }
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getActionIndex() >= 2) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.CLIENT) {
                    if (action.getName() == OfficeAction.Name.CLIENT_GO_RECEPTIONIST
                            || action.getName() == OfficeAction.Name.CLIENT_GO_COUCH
                            || action.getName() == OfficeAction.Name.CLIENT_GO_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (action.getName() == OfficeAction.Name.CLIENT_GO_RECEPTIONIST
                                    || action.getName() == OfficeAction.Name.CLIENT_GO_OFFICE) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                            else {
                                agentMovement.chooseGoal(Couch.class);
                            }
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getActionIndex() >= agentMovement.getCurrentState().getActions().size() - 1) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.GO_TO_OFFICE_ROOM) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.GOING_HOME) {
                    if (action.getName() == OfficeAction.Name.LEAVE_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getOfficeGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.despawn();
                                }
                            }
                        }
                    }
                }

                break;

            case VISITOR:
                if (state.getName() == OfficeState.Name.GOING_TO_SECURITY) {
                    if (action.getName() == OfficeAction.Name.GOING_TO_SECURITY_QUEUE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalQueueingPatchField() == null) {
                            agentMovement.setGoalQueueingPatchField(Main.officeSimulator.getOffice().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getSecurities().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.joinQueue();
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.GO_THROUGH_SCANNER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.VISITOR) {
                    if (action.getName() == OfficeAction.Name.VISITOR_GO_RECEPTIONIST
                            || action.getName() == OfficeAction.Name.VISITOR_GO_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                            }
                        }
                        else {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getActionIndex() >= agentMovement.getCurrentState().getActions().size() - 1) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                }else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                                agentMovement.getGoalAttractor().setIsReserved(false);
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.GOING_HOME) {
                    if (action.getName() == OfficeAction.Name.LEAVE_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getOfficeGates().get(0));
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.despawn();
                                }
                            }
                        }
                    }
                }

                break;
            }
        }

        
        
        if (agentMovement.isInteracting()){
            // cases: early termination of interaction
            //reducing of interaction duration
            // termination of interaction
            if (agentMovement.getDuration() == 0){
                agentMovement.setInteracting(false);
                agentMovement.setInteractionType(null);
            }
            else{
                agentMovement.interact();
            }

        }
        else{
            List<Patch> patches = agentMovement.get7x7Field(agentMovement.getHeading(), true, agentMovement.getFieldOfViewAngle());
            for (Patch patch: patches){
                for (Agent otherAgent: patch.getAgents()){
                    OfficeAgent officeAgent = (OfficeAgent) otherAgent;
                    if (!officeAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), officeAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(officeAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), officeAgent.getAgentMovement().getProposedHeading(), officeAgent.getAgentMovement().getFieldOfViewAngle()))
                                agentMovement.rollAgentInteraction(officeAgent);
                    if (agentMovement.isInteracting())
                        break;
                }
                if (agentMovement.isInteracting())
                    break;
            }
            patches = agentMovement.get3x3Field(agentMovement.getHeading(), true, Math.toRadians(270));
            for (Patch patch: patches){
                for (Agent otherAgent: patch.getAgents()){
                    OfficeAgent officeAgent = (OfficeAgent) otherAgent;
                    if (!officeAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), officeAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
                            if (Coordinates.isWithinFieldOfView(officeAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), officeAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
                                agentMovement.rollAgentInteraction(officeAgent);
                                if (agentMovement.isInteracting()){ // interaction was successful
                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
                                    currentPatchCount[officeAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][officeAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
                                }
                            }
                    if (agentMovement.isInteracting())
                        break;
                }

                if (agentMovement.isInteracting())
                    break;
            }
        }
    }

    private void spawnAgent(Office office, long currentTick) {
        OfficeGate gate = office.getOfficeGates().get(1);
        OfficeAgent agent = null;

        for (int i = 0; i < 4; i++) { // 4 gates
            Gate.GateBlock spawner = gate.getSpawners().get(i);
            int spawnChance = (int) gate.getChancePerTick();
            int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
            // int team = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;

            if (CHANCE > spawnChance) {
                if (office.getUnspawnedWorkingAgents().size() > 0){
                    agent = office.getUnspawnedWorkingAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(office.getUnspawnedWorkingAgents().size()));
                    int team = agent.getTeam();
                    if (agent.getType() == OfficeAgent.Type.BOSS && OfficeAgent.bossCount != MAX_BOSSES) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        OfficeAgent.bossCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 1 && MANAGERS_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_1.remove(0);
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 1 && BUSINESS_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_1.remove(0);
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 1 && RESEARCH_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_1.remove(0);
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 1 && TECHNICAL_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_1.remove(0);
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 2 && MANAGERS_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_2.remove(0);
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 2 && BUSINESS_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_2.remove(0);
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 2 && RESEARCH_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_2.remove(0);
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 2 && TECHNICAL_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_2.remove(0);
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 3 && MANAGERS_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_3.remove(0);
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 3 && BUSINESS_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_3.remove(0);
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 3 && RESEARCH_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_3.remove(0);
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 3 && TECHNICAL_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_3.remove(0);
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 4 && MANAGERS_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_4.remove(0);
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 4 && BUSINESS_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_4.remove(0);
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 4 && RESEARCH_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_4.remove(0);
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 4 && TECHNICAL_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_4.remove(0);
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.SECRETARY && OfficeAgent.secretaryCount != MAX_SECRETARIES) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        OfficeAgent.secretaryCount++;
                        OfficeAgent.agentCount++;
                    }
                }

            }
            else {
                if (office.getUnspawnedVisitingAgents().size() > 0){
                    agent = office.getUnspawnedVisitingAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(office.getUnspawnedVisitingAgents().size()));
                    if (agent.getType() == OfficeAgent.Type.CLIENT && ((currentTick >= 720 && currentTick < 1800) ||  (currentTick >= 2880 && currentTick < 4320)) && OfficeAgent.clientCount < office.getMAX_CLIENTS()) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        OfficeAgent.clientCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.DRIVER && OfficeAgent.driverCount < getOffice().getMAX_DRIVERS()) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        OfficeAgent.driverCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.VISITOR && currentTick >= 3600 && currentTick < 5040 && OfficeAgent.visitorCount < getOffice().getMAX_VISITORS()) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        OfficeAgent.visitorCount++;
                        OfficeAgent.agentCount++;
                    }
                }
            }
        }
    }

    public void replenishSeats() {
        MANAGERS_1 = new LinkedList<Integer>(List.of(11));
        MANAGERS_2 = new LinkedList<Integer>(List.of(19));
        MANAGERS_3 = new LinkedList<Integer>(List.of(27));
        MANAGERS_4 = new LinkedList<Integer>(List.of(35));
        BUSINESS_1 = new LinkedList<Integer>(List.of(0, 1, 2, 3, 4));
        BUSINESS_2 = new LinkedList<Integer>(List.of(36, 37, 38, 39, 40, 41, 42));
        BUSINESS_3 = new LinkedList<Integer>(List.of(44, 45, 46, 47, 48, 49, 50));
        BUSINESS_4 = new LinkedList<Integer>(List.of(52, 53, 54, 55, 56, 57, 58));
        RESEARCH_1 = new LinkedList<Integer>(List.of(5, 6, 7, 8, 9));
        RESEARCH_2 = new LinkedList<Integer>(List.of(12, 13, 14, 15, 16, 17, 18));
        RESEARCH_3 = new LinkedList<Integer>(List.of(20, 21, 22, 23, 24, 25, 26));
        RESEARCH_4 = new LinkedList<Integer>(List.of(28, 29, 30, 31, 32, 33, 34));
        TECHNICAL_1 = new LinkedList<Integer>(List.of(10));
        TECHNICAL_2 = new LinkedList<Integer>(List.of(43));
        TECHNICAL_3 = new LinkedList<Integer>(List.of(51));
        TECHNICAL_4 = new LinkedList<Integer>(List.of(59));
    }

}