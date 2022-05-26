package com.socialsim.model.simulator.office;

import com.socialsim.controller.Main;
import com.socialsim.controller.office.controls.OfficeScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.office.OfficeState;
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
import java.io.PrintWriter;

public class OfficeSimulator extends Simulator {

    public static int defaultMaxClients = 6;
    public static int defaultMaxDrivers = 3;
    public static int defaultMaxVisitors = 1;
    public static int defaultMaxCurrentClients = 2;
    public static int defaultMaxCurrentDrivers = 1;
    public static int defaultMaxCurrentVisitors = 1;

    private Office office;
    private final AtomicBoolean running;
    private final SimulationTime time;
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
    public static float averageNonverbalDuration = 0;
    public static float averageCooperativeDuration = 0;
    public static float averageExchangeDuration = 0;
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
    public static int[] compiledCurrentManagerCount;
    public static int[] compiledCurrentBusinessCount;
    public static int[] compiledCurrentResearchCount;
    public static int[] compiledCurrentTechnicalCount;
    public static int[] compiledCurrentSecretaryCount;
    public static int[] compiledCurrentClientCount;
    public static int[] compiledCurrentDriverCount;
    public static int[] compiledCurrentVisitorCount;
    public static int[] compiledCurrentNonverbalCount;
    public static int[] compiledCurrentCooperativeCount;
    public static int[] compiledCurrentExchangeCount;
    public static float[] compiledAverageNonverbalDuration;
    public static float[] compiledAverageCooperativeDuration;
    public static float[] compiledAverageExchangeDuration;
    public static int[] compiledCurrentTeam1Count;
    public static int[] compiledCurrentTeam2Count;
    public static int[] compiledCurrentTeam3Count;
    public static int[] compiledCurrentTeam4Count;
    public static int[] compiledCurrentBossManagerCount;
    public static int[] compiledCurrentBossBusinessCount;
    public static int[] compiledCurrentBossResearcherCount;
    public static int[] compiledCurrentBossTechnicalCount;
    public static int[] compiledCurrentBossJanitorCount;
    public static int[] compiledCurrentBossClientCount;
    public static int[] compiledCurrentBossDriverCount;
    public static int[] compiledCurrentBossVisitorCount;
    public static int[] compiledCurrentBossGuardCount;
    public static int[] compiledCurrentBossReceptionistCount;
    public static int[] compiledCurrentBossSecretaryCount;
    public static int[] compiledCurrentManagerManagerCount;
    public static int[] compiledCurrentManagerBusinessCount;
    public static int[] compiledCurrentManagerResearcherCount;
    public static int[] compiledCurrentManagerTechnicalCount;
    public static int[] compiledCurrentManagerJanitorCount;
    public static int[] compiledCurrentManagerClientCount;
    public static int[] compiledCurrentManagerDriverCount;
    public static int[] compiledCurrentManagerVisitorCount;
    public static int[] compiledCurrentManagerGuardCount;
    public static int[] compiledCurrentManagerReceptionistCount;
    public static int[] compiledCurrentManagerSecretaryCount;
    public static int[] compiledCurrentBusinessBusinessCount;
    public static int[] compiledCurrentBusinessResearcherCount;
    public static int[] compiledCurrentBusinessTechnicalCount;
    public static int[] compiledCurrentBusinessJanitorCount;
    public static int[] compiledCurrentBusinessClientCount;
    public static int[] compiledCurrentBusinessDriverCount;
    public static int[] compiledCurrentBusinessVisitorCount;
    public static int[] compiledCurrentBusinessGuardCount;
    public static int[] compiledCurrentBusinessReceptionistCount;
    public static int[] compiledCurrentBusinessSecretaryCount;
    public static int[] compiledCurrentResearcherResearcherCount;
    public static int[] compiledCurrentResearcherTechnicalCount;
    public static int[] compiledCurrentResearcherJanitorCount;
    public static int[] compiledCurrentResearcherClientCount;
    public static int[] compiledCurrentResearcherDriverCount;
    public static int[] compiledCurrentResearcherVisitorCount;
    public static int[] compiledCurrentResearcherGuardCount;
    public static int[] compiledCurrentResearcherReceptionistCount;
    public static int[] compiledCurrentResearcherSecretaryCount;
    public static int[] compiledCurrentTechnicalTechnicalCount;
    public static int[] compiledCurrentTechnicalJanitorCount;
    public static int[] compiledCurrentTechnicalClientCount;
    public static int[] compiledCurrentTechnicalDriverCount;
    public static int[] compiledCurrentTechnicalVisitorCount;
    public static int[] compiledCurrentTechnicalGuardCount;
    public static int[] compiledCurrentTechnicalReceptionistCount;
    public static int[] compiledCurrentTechnicalSecretaryCount;
    public static int[] compiledCurrentJanitorJanitorCount;
    public static int[] compiledCurrentJanitorClientCount;
    public static int[] compiledCurrentJanitorDriverCount;
    public static int[] compiledCurrentJanitorVisitorCount;
    public static int[] compiledCurrentJanitorGuardCount;
    public static int[] compiledCurrentJanitorReceptionistCount;
    public static int[] compiledCurrentJanitorSecretaryCount;
    public static int[] compiledCurrentClientClientCount;
    public static int[] compiledCurrentClientDriverCount;
    public static int[] compiledCurrentClientVisitorCount;
    public static int[] compiledCurrentClientGuardCount;
    public static int[] compiledCurrentClientReceptionistCount;
    public static int[] compiledCurrentClientSecretaryCount;
    public static int[] compiledCurrentDriverDriverCount;
    public static int[] compiledCurrentDriverVisitorCount;
    public static int[] compiledCurrentDriverGuardCount;
    public static int[] compiledCurrentDriverReceptionistCount;
    public static int[] compiledCurrentDriverSecretaryCount;
    public static int[] compiledCurrentVisitorVisitorCount;
    public static int[] compiledCurrentVisitorGuardCount;
    public static int[] compiledCurrentVisitorReceptionistCount;
    public static int[] compiledCurrentVisitorSecretaryCount;
    public static int[] compiledCurrentGuardGuardCount;
    public static int[] compiledCurrentGuardReceptionistCount;
    public static int[] compiledCurrentGuardSecretaryCount;
    public static int[] compiledCurrentReceptionistReceptionistCount;
    public static int[] compiledCurrentReceptionistSecretaryCount;
    public static int[] compiledCurrentSecretarySecretaryCount;
    public static int[][] currentPatchCount;

    public OfficeSimulator() {
        this.office = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(9, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start();
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
        replenishStaticVars();
        OfficeAgent.clearOfficeAgentCounts();
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(Office office) {
        office.createInitialAgentDemographics(office.getMAX_CLIENTS(), getOffice().getMAX_DRIVERS(), getOffice().getMAX_VISITORS());
        OfficeAgent janitor = office.getAgents().get(0);
        janitor.setAgentMovement(new OfficeAgentMovement(office.getPatch(6,23), janitor, 1.27, office.getPatch(6,23).getPatchCenterCoordinates(), -1, 0, null));
        office.getAgentPatchSet().add(janitor.getAgentMovement().getCurrentPatch());
        OfficeAgent.janitorCount++;
        OfficeAgent.agentCount++;

        OfficeAgent guard = office.getAgents().get(1);
        guard.setAgentMovement(new OfficeAgentMovement(office.getPatch(57,25), guard, 1.27, office.getPatch(57,25).getPatchCenterCoordinates(), -1, 0, null));
        office.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
        OfficeAgent.guardCount++;
        OfficeAgent.agentCount++;

        OfficeAgent receptionist = office.getAgents().get(2);
        receptionist.setAgentMovement(new OfficeAgentMovement(office.getPatch(46,31), receptionist, 1.27, office.getPatch(46,32).getPatchCenterCoordinates(), -1, 0, null));
        office.getAgentPatchSet().add(receptionist.getAgentMovement().getCurrentPatch());
        OfficeAgent.receptionistCount++;
        OfficeAgent.agentCount++;
    }

    public void reset() {
        this.time.reset();
    }

    private void start() {
        new Thread(() -> {
            final int speedAwarenessLimitMilliseconds = 10;

            while (true) {
                try {
                    playSemaphore.acquire();

                    while (this.isRunning()) {
                        long currentTick = this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5;
                        try {
                            updateAgentsInOffice(office, currentTick);
                            spawnAgent(office, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

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

    public static void updateAgentsInOffice(Office office, long currentTick) throws InterruptedException {
        moveAll(office, currentTick);
        compiledCurrentManagerCount[(int) currentTick] = currentManagerCount;
        compiledCurrentBusinessCount[(int) currentTick] = currentBusinessCount;
        compiledCurrentResearchCount[(int) currentTick] = currentResearchCount;
        compiledCurrentTechnicalCount[(int) currentTick] = currentTechnicalCount;
        compiledCurrentSecretaryCount[(int) currentTick] = currentSecretaryCount;
        compiledCurrentClientCount[(int) currentTick] = currentClientCount;
        compiledCurrentDriverCount[(int) currentTick] = currentDriverCount;
        compiledCurrentVisitorCount[(int) currentTick] = currentVisitorCount;
        compiledCurrentNonverbalCount[(int) currentTick] = currentNonverbalCount;
        compiledCurrentCooperativeCount[(int) currentTick] = currentCooperativeCount;
        compiledCurrentExchangeCount[(int) currentTick] = currentExchangeCount;
        compiledAverageNonverbalDuration[(int) currentTick] = averageNonverbalDuration;
        compiledAverageCooperativeDuration[(int) currentTick] = averageCooperativeDuration;
        compiledAverageExchangeDuration[(int) currentTick] = averageExchangeDuration;
        compiledCurrentTeam1Count[(int) currentTick] = currentTeam1Count;
        compiledCurrentTeam2Count[(int) currentTick] = currentTeam2Count;
        compiledCurrentTeam3Count[(int) currentTick] = currentTeam3Count;
        compiledCurrentTeam4Count[(int) currentTick] = currentTeam4Count;
        compiledCurrentBossManagerCount[(int) currentTick] = currentBossManagerCount;
        compiledCurrentBossBusinessCount[(int) currentTick] = currentBossBusinessCount;
        compiledCurrentBossResearcherCount[(int) currentTick] = currentBossResearcherCount;
        compiledCurrentBossTechnicalCount[(int) currentTick] = currentBossTechnicalCount;
        compiledCurrentBossJanitorCount[(int) currentTick] = currentBossJanitorCount;
        compiledCurrentBossClientCount[(int) currentTick] = currentBossClientCount;
        compiledCurrentBossDriverCount[(int) currentTick] = currentBossDriverCount;
        compiledCurrentBossVisitorCount[(int) currentTick] = currentBossVisitorCount;
        compiledCurrentBossGuardCount[(int) currentTick] = currentBossGuardCount;
        compiledCurrentBossReceptionistCount[(int) currentTick] = currentBossReceptionistCount;
        compiledCurrentBossSecretaryCount[(int) currentTick] = currentBossSecretaryCount;
        compiledCurrentManagerManagerCount[(int) currentTick] = currentManagerManagerCount;
        compiledCurrentManagerBusinessCount[(int) currentTick] = currentManagerBusinessCount;
        compiledCurrentManagerResearcherCount[(int) currentTick] = currentManagerResearcherCount;
        compiledCurrentManagerTechnicalCount[(int) currentTick] = currentManagerTechnicalCount;
        compiledCurrentManagerJanitorCount[(int) currentTick] = currentManagerJanitorCount;
        compiledCurrentManagerClientCount[(int) currentTick] = currentManagerClientCount;
        compiledCurrentManagerDriverCount[(int) currentTick] = currentManagerDriverCount;
        compiledCurrentManagerVisitorCount[(int) currentTick] = currentManagerVisitorCount;
        compiledCurrentManagerGuardCount[(int) currentTick] = currentManagerGuardCount;
        compiledCurrentManagerReceptionistCount[(int) currentTick] = currentManagerReceptionistCount;
        compiledCurrentManagerSecretaryCount[(int) currentTick] = currentManagerSecretaryCount;
        compiledCurrentBusinessBusinessCount[(int) currentTick] = currentBusinessBusinessCount;
        compiledCurrentBusinessResearcherCount[(int) currentTick] = currentBusinessResearcherCount;
        compiledCurrentBusinessTechnicalCount[(int) currentTick] = currentBusinessTechnicalCount;
        compiledCurrentBusinessJanitorCount[(int) currentTick] = currentBusinessJanitorCount;
        compiledCurrentBusinessClientCount[(int) currentTick] = currentBusinessClientCount;
        compiledCurrentBusinessDriverCount[(int) currentTick] = currentBusinessDriverCount;
        compiledCurrentBusinessVisitorCount[(int) currentTick] = currentBusinessVisitorCount;
        compiledCurrentBusinessGuardCount[(int) currentTick] = currentBusinessGuardCount;
        compiledCurrentBusinessReceptionistCount[(int) currentTick] = currentBusinessReceptionistCount;
        compiledCurrentBusinessSecretaryCount[(int) currentTick] = currentBusinessSecretaryCount;
        compiledCurrentResearcherResearcherCount[(int) currentTick] = currentResearcherResearcherCount;
        compiledCurrentResearcherTechnicalCount[(int) currentTick] = currentResearcherTechnicalCount;
        compiledCurrentResearcherJanitorCount[(int) currentTick] = currentResearcherJanitorCount;
        compiledCurrentResearcherClientCount[(int) currentTick] = currentResearcherClientCount;
        compiledCurrentResearcherDriverCount[(int) currentTick] = currentResearcherDriverCount;
        compiledCurrentResearcherVisitorCount[(int) currentTick] = currentResearcherVisitorCount;
        compiledCurrentResearcherGuardCount[(int) currentTick] = currentResearcherGuardCount;
        compiledCurrentResearcherReceptionistCount[(int) currentTick] = currentResearcherReceptionistCount;
        compiledCurrentResearcherSecretaryCount[(int) currentTick] = currentResearcherSecretaryCount;
        compiledCurrentTechnicalTechnicalCount[(int) currentTick] = currentTechnicalTechnicalCount;
        compiledCurrentTechnicalJanitorCount[(int) currentTick] = currentTechnicalJanitorCount;
        compiledCurrentTechnicalClientCount[(int) currentTick] = currentTechnicalClientCount;
        compiledCurrentTechnicalDriverCount[(int) currentTick] = currentTechnicalDriverCount;
        compiledCurrentTechnicalVisitorCount[(int) currentTick] = currentTechnicalVisitorCount;
        compiledCurrentTechnicalGuardCount[(int) currentTick] = currentTechnicalGuardCount;
        compiledCurrentTechnicalReceptionistCount[(int) currentTick] = currentTechnicalReceptionistCount;
        compiledCurrentTechnicalSecretaryCount[(int) currentTick] = currentTechnicalSecretaryCount;
        compiledCurrentJanitorJanitorCount[(int) currentTick] = currentJanitorJanitorCount;
        compiledCurrentJanitorClientCount[(int) currentTick] = currentJanitorClientCount;
        compiledCurrentJanitorDriverCount[(int) currentTick] = currentJanitorDriverCount;
        compiledCurrentJanitorVisitorCount[(int) currentTick] = currentJanitorVisitorCount;
        compiledCurrentJanitorGuardCount[(int) currentTick] = currentJanitorGuardCount;
        compiledCurrentJanitorReceptionistCount[(int) currentTick] = currentJanitorReceptionistCount;
        compiledCurrentJanitorSecretaryCount[(int) currentTick] = currentJanitorSecretaryCount;
        compiledCurrentClientClientCount[(int) currentTick] = currentClientClientCount;
        compiledCurrentClientDriverCount[(int) currentTick] = currentClientDriverCount;
        compiledCurrentClientVisitorCount[(int) currentTick] = currentClientVisitorCount;
        compiledCurrentClientGuardCount[(int) currentTick] = currentClientGuardCount;
        compiledCurrentClientReceptionistCount[(int) currentTick] = currentClientReceptionistCount;
        compiledCurrentClientSecretaryCount[(int) currentTick] = currentClientSecretaryCount;
        compiledCurrentDriverDriverCount[(int) currentTick] = currentDriverDriverCount;
        compiledCurrentDriverVisitorCount[(int) currentTick] = currentDriverVisitorCount;
        compiledCurrentDriverGuardCount[(int) currentTick] = currentDriverGuardCount;
        compiledCurrentDriverReceptionistCount[(int) currentTick] = currentDriverReceptionistCount;
        compiledCurrentDriverSecretaryCount[(int) currentTick] = currentDriverSecretaryCount;
        compiledCurrentVisitorVisitorCount[(int) currentTick] = currentVisitorVisitorCount;
        compiledCurrentVisitorGuardCount[(int) currentTick] = currentVisitorGuardCount;
        compiledCurrentVisitorReceptionistCount[(int) currentTick] = currentVisitorReceptionistCount;
        compiledCurrentVisitorSecretaryCount[(int) currentTick] = currentVisitorSecretaryCount;
        compiledCurrentGuardGuardCount[(int) currentTick] = currentGuardGuardCount;
        compiledCurrentGuardReceptionistCount[(int) currentTick] = currentGuardReceptionistCount;
        compiledCurrentGuardSecretaryCount[(int) currentTick] = currentGuardSecretaryCount;
        compiledCurrentReceptionistReceptionistCount[(int) currentTick] = currentReceptionistReceptionistCount;
        compiledCurrentReceptionistSecretaryCount[(int) currentTick] = currentReceptionistSecretaryCount;
        compiledCurrentSecretarySecretaryCount[(int) currentTick] = currentSecretarySecretaryCount;
    }

    private static void moveAll(Office office, long currentTick) {
        int bathroomReserves = office.numBathroomsFree();
        for (OfficeAgent agent : office.getMovableAgents()) {
            try {
                if (currentTick == 2160 && agent.getAgentMovement().getRoutePlan().getLUNCH_INSTANCE() != null) {
                    agent.getAgentMovement().setNextState(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().indexOf(agent.getAgentMovement().getRoutePlan().getLUNCH_INSTANCE()) - 1);
                    agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().indexOf(agent.getAgentMovement().getRoutePlan().getLUNCH_INSTANCE()));
                    agent.getAgentMovement().setActionIndex(0);
                    agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                    agent.getAgentMovement().resetGoal();
                }

                if(currentTick == 4320){
                    agent.getAgentMovement().getRoutePlan().setBATH_LUNCH(-1);
                    agent.getAgentMovement().getRoutePlan().setBREAK_COUNT(-1);
                }

                if (currentTick == 5760) {
                    agent.getAgentMovement().getRoutePlan().setBATH_PM(-1);
                    agent.getAgentMovement().setNextState(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size()-2);
                    agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size()-1);
                    agent.getAgentMovement().setActionIndex(0);
                    agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                    agent.getAgentMovement().resetGoal();
                }

                if(agent.getAgentMovement() != null && agent.getTeam() > 0 && currentTick == agent.getAgentMovement().getRoutePlan().getMeetingStart()){
                    if (agent.getAgentMovement().getCurrentState().getName() == OfficeState.Name.WORKING) {
                        agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() - 1);
                    }
                    else if (agent.getAgentMovement().getCurrentState().getName() == OfficeState.Name.EATING_LUNCH) {
                        agent.getAgentMovement().getRoutePlan().setLunchAmenity(null);
                        agent.getAgentMovement().getRoutePlan().setLunchAttractor(null);
                    }

                    if (agent.getAgentMovement().getGoalAttractor() != null) {
                        agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                    }
                    agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent.getAgentMovement().getStateIndex() + 1, agent.getAgentMovement().getRoutePlan().addUrgentRoute("MEETING", agent));
                    agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                    agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                    agent.getAgentMovement().setActionIndex(0);
                    agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                    agent.getAgentMovement().resetGoal();
                }
                if (agent.getAgentMovement().getCurrentState().getName() == OfficeState.Name.WAIT_INFRONT_OF_BATHROOM){
                    if (bathroomReserves > 0){
                        agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                        agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                        agent.getAgentMovement().setActionIndex(0);
                        agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                        if (agent.getAgentMovement().getGoalAttractor() != null) {
                            agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                        }
                        agent.getAgentMovement().resetGoal();
                        bathroomReserves--;
                    }
                }

                moveOne(agent, currentTick);
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

        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()) {
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
                        }
                        else {
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
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                    agentMovement.getRoutePlan().setAtDesk(true);
                                }
                            }
                        }
                        else if (currentTick >= 5760) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.resetGoal();
                        }
                        else if ((currentTick < 2060 || (currentTick < 5660 && currentTick > 2520)) && agentMovement.getRoutePlan().getCanUrgent() <= 0) {
                            double CHANCE = Simulator.roll();

                            if (CHANCE < OfficeRoutePlan.BATH_CHANCE) {
                                if (currentTick < 2160 && agentMovement.getRoutePlan().getBATH_AM() > 0) {
                                    agentMovement.getRoutePlan().setFromBathAM(true);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else if(agentMovement.getRoutePlan().getBATH_PM() > 0) {
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
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
                }
                else if (state.getName() == OfficeState.Name.EATING_LUNCH) {
                    if (action.getName() == OfficeAction.Name.GO_TO_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (agentMovement.getRoutePlan().getLunchAmenity() == null) {
                                double CHANCE = Simulator.roll();

                                if (persona == OfficeAgent.Persona.PROFESSIONAL_BOSS) {
                                    if (CHANCE < OfficeRoutePlan.INT_LUNCH) {
                                        if (!agentMovement.chooseBreakroomSeat()) {
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        }
                                    }
                                    else {
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    }
                                }
                                else {
                                    if (CHANCE < OfficeRoutePlan.APP_BOSS_LUNCH) {
                                        if (!agentMovement.chooseBreakroomSeat()) {
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        }
                                    }
                                    else {
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    }
                                }
                            }
                            else {
                                agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getLunchAmenity());
                                agentMovement.setGoalAttractor(agentMovement.getRoutePlan().getLunchAttractor());
                            }

                        }

                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.getRoutePlan().setAtDesk(false);
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getRoutePlan().getLastDuration() == -1) {
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                    else {
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
                        else if (agentMovement.getRoutePlan().getCanUrgent() <= 0) {
                            double CHANCE = Simulator.roll();

                            if (CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_LUNCH() > 0) {
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE && agentMovement.getRoutePlan().getDISPENSER_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DISPENSER", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE + OfficeRoutePlan.REFRIGERATOR_CHANCE && agentMovement.getRoutePlan().getREFRIGERATOR_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("REFRIGERATOR", agent));
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
                else if(state.getName() == OfficeState.Name.DISPENSER){
                    if(action.getName() == OfficeAction.Name.GOING_DISPENSER){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if(agentMovement.getGoalAmenity() == null){
                            if(!agentMovement.chooseGoal(WaterDispenser.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if(currentTick < 4320){
                                    agentMovement.getRoutePlan().setDISPENSER_LUNCH(0);
                                }else{
                                    agentMovement.getRoutePlan().setDISPENSER_PM(0);
                                }
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.GETTING_WATER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.REFRIGERATOR){
                    if(action.getName() == OfficeAction.Name.GOING_FRIDGE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if(agentMovement.getGoalAmenity() == null){
                            if(!agentMovement.chooseGoal(Fridge.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if(currentTick < 4320){
                                    agentMovement.getRoutePlan().setREFRIGERATOR_LUNCH(0);
                                }else{
                                    agentMovement.getRoutePlan().setREFRIGERATOR_PM(0);
                                }
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.GETTING_FOOD) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_BATHROOM) {
                    if (action.getName()== OfficeAction.Name.GO_TO_BATHROOM) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() - 1, agentMovement.getRoutePlan().addWaitingRoute(agent));
                                agentMovement.setPreviousState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() -1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if(agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                if (agentMovement.getRoutePlan().isFromBathAM()) {
                                    agentMovement.getRoutePlan().setFromBathAM(false);
                                }
                                else if (agentMovement.getRoutePlan().isFromBathPM()) {
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                }
                            }
                            else {
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if (agentMovement.getRoutePlan().isFromBathAM()) {
                                    agentMovement.getRoutePlan().setFromBathAM(false);
                                    agentMovement.getRoutePlan().setBATH_AM(1);
                                }
                                else if (agentMovement.getRoutePlan().isFromBathPM()) {
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                    agentMovement.getRoutePlan().setBATH_PM(1);
                                }
                                else {
                                    agentMovement.getRoutePlan().setBATH_LUNCH(1);
                                }
                                agentMovement.getRoutePlan().resetCanUrgent();
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName()==OfficeAction.Name.RELIEVE_IN_CUBICLE) {
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
                    else if (action.getName() == OfficeAction.Name.FIND_SINK) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.WASH_IN_SINK) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.MEETING) {
                    if (action.getName() == OfficeAction.Name.GO_MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseMeetingGoal(agentMovement.getRoutePlan().getMeetingRoom());
                            agentMovement.getRoutePlan().setAtDesk(false);
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
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
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.WAIT_MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        if (agentMovement.getRoutePlan().getMeetingEnd() <= currentTick) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
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
                            agentMovement.getRoutePlan().setAtDesk(false);
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
                else if(state.getName() == OfficeState.Name.WAIT_INFRONT_OF_BATHROOM){
                    if (action.getName() == OfficeAction.Name.GO_TO_WAIT_AREA) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseWaitPatch()){
                                System.out.println("False wait patch");
                            }
                        }
                        else {
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else{
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    }
                    else if(action.getName() == OfficeAction.Name.WAIT_FOR_VACANT){
                        agentMovement.setSimultaneousInteractionAllowed(true);
//                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        if (agentMovement.getCurrentAction().getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            if (agentMovement.getGoalAttractor() != null) {
                                agentMovement.getGoalAttractor().setIsReserved(false);
                            }
                            agentMovement.resetGoal();
                        }
                        else {
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
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
                                    agentMovement.getRoutePlan().setAtDesk(true);
                                }
                            }
                        }
                        else if ((currentTick < 2060 || (currentTick < 5660 && currentTick > 2520)) && agentMovement.getRoutePlan().getCanUrgent() <= 0) {
                            double CHANCE = Simulator.roll();

                            if (CHANCE < OfficeRoutePlan.BATH_CHANCE) {
                                if (currentTick < 2160 && agentMovement.getRoutePlan().getBATH_AM() > 0) {
                                    agentMovement.getRoutePlan().setFromBathAM(true);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else if (agentMovement.getRoutePlan().getBATH_PM() > 0) {
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromBathPM(true);
                                }
                            }
                            else {
                                int left = 0;
                                if (persona == OfficeAgent.Persona.EXT_BUSINESS || persona == OfficeAgent.Persona.INT_BUSINESS) {
                                    left = agentMovement.getRoutePlan().getPRINT_BUSINESS();
                                }
                                else if (persona == OfficeAgent.Persona.EXT_RESEARCHER || persona == OfficeAgent.Persona.INT_RESEARCHER) {
                                    left = agentMovement.getRoutePlan().getPRINT_RESEARCH();
                                }

                                if (CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.PRINT_CHANCE && left > 0) {
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("PRINT", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.BREAK_CHANCE && agentMovement.getRoutePlan().getBREAK_COUNT() > 0){
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BREAK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }

                                if (CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.TECHNICAL_CUBICLE_CHANCE && agentMovement.getRoutePlan().getTECHNICAL_CUBICLE_COUNT() > 0) {
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute(agent, officeInstance));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else if (CHANCE < OfficeRoutePlan.TECHNICAL_PRINTER_CHANCE + OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.TECHNICAL_CUBICLE_CHANCE && agentMovement.getRoutePlan().getTECHNICAL_PRINTER_COUNT() > 0) {
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TECHNICAL_PRINTER", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else if ((currentTick < 1660 || (currentTick < 5260 && currentTick > 2520)) &&
                                        agentMovement.getRoutePlan().getCOLLABORATE_COUNT() > 0 &&
                                        agentMovement.getRoutePlan().getCanUrgent() <= 0 &&
                                        CHANCE < agentMovement.getRoutePlan().getCooperate(persona) +
                                        OfficeRoutePlan.TECHNICAL_PRINTER_CHANCE + OfficeRoutePlan.BATH_CHANCE +
                                        OfficeRoutePlan.PRINT_CHANCE + OfficeRoutePlan.TECHNICAL_CUBICLE_CHANCE) {
                                    ArrayList<OfficeAgent> agents = officeInstance.getTeamMembers(agent.getTeam());
                                    for(OfficeAgent agent1 : agents) {
                                        if (agent1.getAgentMovement().getCurrentAction() != null && agent1.getAgentMovement().getCurrentAction().getName() == OfficeAction.Name.GO_TO_STATION) {
                                            agent1.getAgentMovement().setStateIndex(agent1.getAgentMovement().getStateIndex() - 1);
                                            agent1.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent1.getAgentMovement().getStateIndex() + 1, agent1.getAgentMovement().getRoutePlan().addUrgentRoute("COLLABORATION", agent));
                                            agent1.getAgentMovement().setNextState(agent1.getAgentMovement().getStateIndex());
                                            agent1.getAgentMovement().setStateIndex(agent1.getAgentMovement().getStateIndex() + 1);
                                            agent1.getAgentMovement().setActionIndex(0);
                                            agent1.getAgentMovement().setCurrentAction(agent1.getAgentMovement().getCurrentState().getActions().get(agent1.getAgentMovement().getActionIndex()));
                                            agent1.getAgentMovement().resetGoal();
                                        }
                                        else {
                                            agent1.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent1.getAgentMovement().getStateIndex() + 1, agent1.getAgentMovement().getRoutePlan().addUrgentRoute("COLLABORATION", agent));
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
                            if (agentMovement.chooseCollaborationChair()) {
                                agentMovement.getRoutePlan().resetCanUrgent();
                                agentMovement.getRoutePlan().setCOLLABORATE_COUNT(-1);
                                agentMovement.getRoutePlan().setAtDesk(false);
                            }
                            else {
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }

                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                                else {
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
                            }
                            else {
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getRoutePlan().setCollaborationEnd(currentTick, agentMovement.getCurrentAction().getDuration());
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.COLLABORATE) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        if (agentMovement.getRoutePlan().getCollaborationEnd() <= currentTick) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                            agentMovement.removeCollaborationTeam();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.MEETING) {
                    if (action.getName() == OfficeAction.Name.GO_MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.chooseMeetingGoal(agentMovement.getRoutePlan().getMeetingRoom());
                            agentMovement.getRoutePlan().setAtDesk(false);
                        }
                        if (agentMovement.chooseNextPatchInPath()) {
                            agentMovement.faceNextPosition();
                            agentMovement.moveSocialForce();
                            if (agentMovement.hasReachedNextPatchInPath()) {
                                agentMovement.reachPatchInPath();
                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
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
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.WAIT_MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.MEETING) {
                        agentMovement.setSimultaneousInteractionAllowed(true);
                        if (agentMovement.getRoutePlan().getMeetingEnd() <= currentTick) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                            agentMovement.getRoutePlan().setCanUrgent(-1);
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_PRINT) {
                    if (action.getName() == OfficeAction.Name.GO_TO_PRINTER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (!agentMovement.chooseGoal(Printer.class)) {
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getRoutePlan().resetCanUrgent();
                                agentMovement.getRoutePlan().setAtDesk(false);
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
                            //System.out.println(agent.getId() + " Patch: " + agentMovement.getCurrentPatch());
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        if (persona == OfficeAgent.Persona.EXT_BUSINESS || persona == OfficeAgent.Persona.INT_BUSINESS) {
                                            agentMovement.getRoutePlan().setPRINT_BUSINESS();
                                        } else if (persona == OfficeAgent.Persona.EXT_RESEARCHER || persona == OfficeAgent.Persona.INT_RESEARCHER) {
                                            agentMovement.getRoutePlan().setPRINT_RESEARCH();
                                        }
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.PRINTING) {
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
                            if (agentMovement.getRoutePlan().getLunchAmenity() == null) {
                                double CHANCE = Simulator.roll();

                                if (persona == OfficeAgent.Persona.EXT_TECHNICAL || persona == OfficeAgent.Persona.EXT_BUSINESS || persona == OfficeAgent.Persona.EXT_RESEARCHER || persona == OfficeAgent.Persona.MANAGER) {
                                    if (CHANCE < OfficeRoutePlan.EXT_LUNCH) {
                                        if (!agentMovement.chooseBreakroomSeat()) {
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                            agentMovement.getRoutePlan().setAtDesk(true);
                                        }else{
                                            agentMovement.getRoutePlan().setAtDesk(false);
                                        }
                                    }
                                    else {
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        agentMovement.getRoutePlan().setAtDesk(true);
                                    }
                                }
                                else {
                                    if (CHANCE < OfficeRoutePlan.INT_LUNCH) {
                                        if (!agentMovement.chooseBreakroomSeat()) {
                                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                            agentMovement.getRoutePlan().setAtDesk(true);
                                        }else{
                                            agentMovement.getRoutePlan().setAtDesk(false);
                                        }
                                    }
                                    else {
                                        agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                        agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                        agentMovement.getRoutePlan().setAtDesk(true);
                                    }
                                }
                            }
                            else {
                                agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getLunchAmenity());
                                agentMovement.setGoalAttractor(agentMovement.getRoutePlan().getLunchAttractor());
                                agentMovement.getRoutePlan().setAtDesk(true);
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
                                    if (agentMovement.getRoutePlan().getLastDuration() == -1) {
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                    else {
                                        agentMovement.setDuration(agentMovement.getRoutePlan().getLastDuration());
                                        agentMovement.getRoutePlan().setLastDuration(-1);
                                    }
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAttractor(agentMovement.getGoalAttractor());
                                }
                            }
                            else {
                                if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                    while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                        agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getRoutePlan().getLastDuration() == -1) {
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }
                                            else {
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
                        else if (agentMovement.getRoutePlan().getCanUrgent() <= 0) {
                            double CHANCE = Simulator.roll();

                            if (CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_LUNCH() > 0) {
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE && agentMovement.getRoutePlan().getDISPENSER_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DISPENSER", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE + OfficeRoutePlan.REFRIGERATOR_CHANCE && agentMovement.getRoutePlan().getREFRIGERATOR_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("REFRIGERATOR", agent));
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
                else if(state.getName() == OfficeState.Name.BREAK_TIME){
                    if (action.getName() == OfficeAction.Name.GO_TO_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (agentMovement.getRoutePlan().getLunchAmenity() == null) {
                                if(agentMovement.chooseBreakroomSeat()){
                                    agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getAgentCubicle().getAttractors().get(0).getPatch().getAmenityBlock().getParent());
                                    agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                    agentMovement.getRoutePlan().setAtDesk(true);
                                }else{
                                    agentMovement.getRoutePlan().setAtDesk(false);
                                }
                            }
                            else {
                                agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getLunchAmenity());
                                agentMovement.setGoalAttractor(agentMovement.getRoutePlan().getLunchAttractor());
                                agentMovement.getRoutePlan().setAtDesk(true);
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
                                    if (agentMovement.getRoutePlan().getLastDuration() == -1) {
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                    else {
                                        agentMovement.setDuration(agentMovement.getRoutePlan().getLastDuration());
                                        agentMovement.getRoutePlan().setLastDuration(-1);
                                    }
                                    agentMovement.getRoutePlan().setCanUrgent(-1);
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.getRoutePlan().setLunchAttractor(agentMovement.getGoalAttractor());
                                }
                            }
                            else {
                                if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                    while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                        agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getRoutePlan().getLastDuration() == -1) {
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }
                                            else {
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
                    else if (action.getName() == OfficeAction.Name.TAKING_BREAK) {
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
                        else if (agentMovement.getRoutePlan().getCanUrgent() <= 0) {
                            double CHANCE = Simulator.roll();

                            if (CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_LUNCH() > 0) {
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE && agentMovement.getRoutePlan().getDISPENSER_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DISPENSER", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE + OfficeRoutePlan.REFRIGERATOR_CHANCE && agentMovement.getRoutePlan().getREFRIGERATOR_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("REFRIGERATOR", agent));
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
                else if (state.getName() == OfficeState.Name.NEEDS_BATHROOM) {
                    if (action.getName() == OfficeAction.Name.GO_TO_BATHROOM) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() - 1, agentMovement.getRoutePlan().addWaitingRoute(agent));
                                agentMovement.setPreviousState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() -1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if(agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                if (agentMovement.getRoutePlan().isFromBathAM()) {
                                    agentMovement.getRoutePlan().setFromBathAM(false);
                                }
                                else if (agentMovement.getRoutePlan().isFromBathPM()) {
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                }
                            }
                            else {
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if (agentMovement.getRoutePlan().isFromBathAM()) {
                                    agentMovement.getRoutePlan().setFromBathAM(false);
                                    agentMovement.getRoutePlan().setBATH_AM(1);
                                }
                                else if (agentMovement.getRoutePlan().isFromBathPM()) {
                                    agentMovement.getRoutePlan().setFromBathPM(false);
                                    agentMovement.getRoutePlan().setBATH_PM(1);
                                }
                                else {
                                    agentMovement.getRoutePlan().setBATH_LUNCH(1);
                                }
                                agentMovement.getRoutePlan().resetCanUrgent();
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.RELIEVE_IN_CUBICLE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            try{
                                agentMovement.getGoalAttractor().setIsReserved(false);
                            }catch (NullPointerException e){
                                System.out.print("");
                            }
                            agentMovement.resetGoal();
                        }
                    }
                    else if (action.getName() == OfficeAction.Name.FIND_SINK) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.WASH_IN_SINK) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_FIX_PRINTER) {
                    if (action.getName() == OfficeAction.Name.TECHNICAL_GO_PRINTER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (!agentMovement.chooseGoal(Printer.class)) {
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getRoutePlan().setAtDesk(false);
                                agentMovement.getRoutePlan().resetCanUrgent();
                                agentMovement.getRoutePlan().setTECHNICAL_PRINTER_COUNT(1);
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.NEEDS_FIX_CUBICLE) {
                    if (action.getName() == OfficeAction.Name.FIX_CUBICLE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                            agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            agentMovement.getGoalAttractor().setIsReserved(true);
                            agentMovement.getRoutePlan().resetCanUrgent();
                            agentMovement.getRoutePlan().setTECHNICAL_CUBICLE_COUNT(1);
                            agentMovement.getRoutePlan().setAtDesk(false);
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
                        else {
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
                else if(state.getName() == OfficeState.Name.DISPENSER){
                    if(action.getName() == OfficeAction.Name.GOING_DISPENSER){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if(agentMovement.getGoalAmenity() == null){
                            if(!agentMovement.chooseGoal(WaterDispenser.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if(currentTick < 4320){
                                    agentMovement.getRoutePlan().setDISPENSER_LUNCH(0);
                                }else{
                                    agentMovement.getRoutePlan().setDISPENSER_PM(0);
                                }
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.GETTING_WATER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.REFRIGERATOR){
                    if(action.getName() == OfficeAction.Name.GOING_FRIDGE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if(agentMovement.getGoalAmenity() == null){
                            if(!agentMovement.chooseGoal(Fridge.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if(currentTick < 4320){
                                    agentMovement.getRoutePlan().setREFRIGERATOR_LUNCH(0);
                                }else{
                                    agentMovement.getRoutePlan().setREFRIGERATOR_PM(0);
                                }
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.GETTING_FOOD) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if (state.getName() == OfficeState.Name.GOING_HOME) {
                    if (action.getName() == OfficeAction.Name.LEAVE_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(agentMovement.getRoutePlan().getBATH_PM() > 0){
                                double CHANCE = Simulator.roll();
                                if(CHANCE < OfficeRoutePlan.BATH_CHANCE){
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromBathPM(true);
                                }
                            }
                            if(!agentMovement.getRoutePlan().isAtDesk()){
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.setGoalAmenity(Main.officeSimulator.getOffice().getOfficeGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }
                        }else if (agentMovement.getGoalAmenity() == Main.officeSimulator.getOffice().getOfficeGates().get(0)
                        && agentMovement.chooseNextPatchInPath()) {
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
                    else if(action.getName() == OfficeAction.Name.GO_TO_STATION){
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
                                    agentMovement.getRoutePlan().setAtDesk(true);
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                }
                            }
                        }else{
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() - 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.WAIT_INFRONT_OF_BATHROOM){
                    if (action.getName() == OfficeAction.Name.GO_TO_WAIT_AREA) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(!agentMovement.chooseWaitPatch()){
                                System.out.println("False wait patch");
                            }
                        }
                        else {
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else{
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    }
                    else if(action.getName() == OfficeAction.Name.WAIT_FOR_VACANT){
                        agentMovement.setSimultaneousInteractionAllowed(true);
//                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        if (agentMovement.getCurrentAction().getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            if (agentMovement.getGoalAttractor() != null) {
                                agentMovement.getGoalAttractor().setIsReserved(false);
                            }
                            agentMovement.resetGoal();
                        }
                        else {
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
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
                                agentMovement.reachPatchInPath();
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
                                if (action.getName() == OfficeAction.Name.SECRETARY_STAY_PUT) {
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                }
                                else {
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
                else if (state.getName() == OfficeState.Name.EATING_LUNCH) {
                    if (action.getName() == OfficeAction.Name.GO_TO_LUNCH) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if(agentMovement.chooseBreakroomSeat()){
                                agentMovement.setGoalAmenity(agentMovement.getRoutePlan().getLunchAmenity());
                                agentMovement.setGoalAttractor(agentMovement.getRoutePlan().getLunchAttractor());
                            }else{
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
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
                                    if (agentMovement.getRoutePlan().getLastDuration() == -1) {
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                    else {
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
                        else if (agentMovement.getRoutePlan().getCanUrgent() <= 0) {
                            double CHANCE = Simulator.roll();

                            if (CHANCE < OfficeRoutePlan.BATH_CHANCE && agentMovement.getRoutePlan().getBATH_LUNCH() > 0) {
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE && agentMovement.getRoutePlan().getDISPENSER_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DISPENSER", agent));
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.getRoutePlan().setLastDuration(agentMovement.getDuration());
                            }
                            else if(CHANCE < OfficeRoutePlan.BATH_CHANCE + OfficeRoutePlan.DISPENSER_CHANCE + OfficeRoutePlan.REFRIGERATOR_CHANCE && agentMovement.getRoutePlan().getREFRIGERATOR_LUNCH() > 0){
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("REFRIGERATOR", agent));
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
                else if(state.getName() == OfficeState.Name.DISPENSER){
                    if(action.getName() == OfficeAction.Name.GOING_DISPENSER){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if(agentMovement.getGoalAmenity() == null){
                            if(!agentMovement.chooseGoal(WaterDispenser.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if(currentTick < 4320){
                                    agentMovement.getRoutePlan().setDISPENSER_LUNCH(0);
                                }else{
                                    agentMovement.getRoutePlan().setDISPENSER_PM(0);
                                }
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.GETTING_WATER) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
                        }
                    }
                }
                else if(state.getName() == OfficeState.Name.REFRIGERATOR){
                    if(action.getName() == OfficeAction.Name.GOING_FRIDGE){
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if(agentMovement.getGoalAmenity() == null){
                            if(!agentMovement.chooseGoal(Fridge.class)){
                                isFull = true;
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }else{
                                agentMovement.getRoutePlan().setAtDesk(false);
                                if(currentTick < 4320){
                                    agentMovement.getRoutePlan().setREFRIGERATOR_LUNCH(0);
                                }else{
                                    agentMovement.getRoutePlan().setREFRIGERATOR_PM(0);
                                }
                            }
                        }
                        if (isFull) {
                            isFull = false;
                        }
                        else {
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
                    else if (action.getName() == OfficeAction.Name.GETTING_FOOD) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                        agentMovement.setDuration(agentMovement.getDuration() - 1);
                        if (agentMovement.getDuration() <= 0) {
                            agentMovement.setNextState(agentMovement.getStateIndex());
                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                            agentMovement.setActionIndex(0);
                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                            agentMovement.getGoalAttractor().setIsReserved(false);
                            agentMovement.resetGoal();
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
                    if (action.getName() == OfficeAction.Name.DRIVER_GO_RECEPTIONIST || action.getName() == OfficeAction.Name.DRIVER_GO_COUCH) {
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
                                agentMovement.reachPatchInPath();
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
                                }
                                else {
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
                    if (action.getName() == OfficeAction.Name.CLIENT_GO_RECEPTIONIST || action.getName() == OfficeAction.Name.CLIENT_GO_COUCH || action.getName() == OfficeAction.Name.CLIENT_GO_OFFICE) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (agentMovement.getGoalAmenity() == null) {
                            if (action.getName() == OfficeAction.Name.CLIENT_GO_RECEPTIONIST || action.getName() == OfficeAction.Name.CLIENT_GO_OFFICE) {
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
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else {
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
                                agentMovement.reachPatchInPath();
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
                                agentMovement.reachPatchInPath();
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
                    if (action.getName() == OfficeAction.Name.VISITOR_GO_RECEPTIONIST || action.getName() == OfficeAction.Name.VISITOR_GO_OFFICE) {
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
                                }
                                else {
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

        if (agentMovement.isInteracting()) {
            agentMovement.interact();
        }
        else {
            List<Patch> patches = agentMovement.get7x7Field(agentMovement.getHeading(), true, agentMovement.getFieldOfViewAngle());
            OfficeAgent agent2 = null;
            for (Patch patch: patches) {
                for (Agent otherAgent: patch.getAgents()) {
                    OfficeAgent officeAgent = (OfficeAgent) otherAgent;
                    if (!officeAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), officeAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(officeAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), officeAgent.getAgentMovement().getProposedHeading(), officeAgent.getAgentMovement().getFieldOfViewAngle())){
                                agentMovement.rollAgentInteraction(officeAgent);
                                if (agentMovement.isInteracting()) {
                                    agent2 = officeAgent;
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
//            patches = agentMovement.get3x3Field(agentMovement.getHeading(), true, Math.toRadians(270));
//            for (Patch patch: patches) {
//                for (Agent otherAgent: patch.getAgents()) {
//                    OfficeAgent officeAgent = (OfficeAgent) otherAgent;
//                    if (!officeAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
//                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), officeAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
//                            if (Coordinates.isWithinFieldOfView(officeAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), officeAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
//                                agentMovement.rollAgentInteraction(officeAgent);
//                                if (agentMovement.isInteracting()) {
//                                    agent2 = officeAgent;
//                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
//                                    currentPatchCount[officeAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][officeAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
//                                }
//                            }
//                    if (agentMovement.isInteracting())
//                        break;
//                }
//
//                if (agentMovement.isInteracting())
//                    break;
//            }
            if (agentMovement.isInteracting() && agentMovement.getInteractionDuration() == 0) {
                agentMovement.setInteracting(false);
                agentMovement.setInteractionType(null);
            }
            if (agent2 != null && agent2.getAgentMovement().isInteracting() && agent2.getAgentMovement().getInteractionDuration() == 0){
                agent2.getAgentMovement().setInteracting(false);
                agent2.getAgentMovement().setInteractionType(null);
            }
        }
        agent.getAgentGraphic().change();
    }

    private void spawnAgent(Office office, long currentTick) {
        OfficeGate gate = office.getOfficeGates().get(1);
        OfficeAgent agent = null;

        for (int i = 0; i < 2; i++) {
            Gate.GateBlock spawner = gate.getSpawners().get(i);
            int spawnChance = (int) gate.getChancePerTick();
            int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);

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
                        currentManagerCount++;
                        currentTeam1Count++;
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 1 && BUSINESS_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_1.remove(0);
                        currentBusinessCount++;
                        currentTeam1Count++;
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 1 && RESEARCH_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_1.remove(0);
                        currentResearchCount++;
                        currentTeam1Count++;
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 1 && TECHNICAL_1.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_1.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_1.remove(0);
                        currentTechnicalCount++;
                        currentTeam1Count++;
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 2 && MANAGERS_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_2.remove(0);
                        currentManagerCount++;
                        currentTeam2Count++;
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 2 && BUSINESS_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_2.remove(0);
                        currentBusinessCount++;
                        currentTeam2Count++;
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 2 && RESEARCH_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_2.remove(0);
                        currentResearchCount++;
                        currentTeam2Count++;
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 2 && TECHNICAL_2.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_2.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_2.remove(0);
                        currentTechnicalCount++;
                        currentTeam2Count++;
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 3 && MANAGERS_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_3.remove(0);
                        currentManagerCount++;
                        currentTeam3Count++;
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 3 && BUSINESS_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_3.remove(0);
                        currentBusinessCount++;
                        currentTeam3Count++;
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 3 && RESEARCH_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_3.remove(0);
                        currentResearchCount++;
                        currentTeam3Count++;
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 3 && TECHNICAL_3.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_3.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_3.remove(0);
                        currentTechnicalCount++;
                        currentTeam3Count++;
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.MANAGER && team == 4 && MANAGERS_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(MANAGERS_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        MANAGERS_4.remove(0);
                        currentManagerCount++;
                        currentTeam4Count++;
                        OfficeAgent.managerCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.BUSINESS && team == 4 && BUSINESS_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(BUSINESS_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        BUSINESS_4.remove(0);
                        currentBusinessCount++;
                        currentTeam4Count++;
                        OfficeAgent.businessCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.RESEARCHER && team == 4 && RESEARCH_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(RESEARCH_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        RESEARCH_4.remove(0);
                        currentResearchCount++;
                        currentTeam4Count++;
                        OfficeAgent.researcherCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.TECHNICAL && team == 4 && TECHNICAL_4.size() != 0) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, team, office.getCubicles().get(TECHNICAL_4.get(0))));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        TECHNICAL_4.remove(0);
                        currentTechnicalCount++;
                        currentTeam4Count++;
                        OfficeAgent.technicalCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.SECRETARY && OfficeAgent.secretaryCount != MAX_SECRETARIES) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        currentSecretaryCount++;
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
                        currentClientCount++;
                        OfficeAgent.clientCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.DRIVER && OfficeAgent.driverCount < getOffice().getMAX_DRIVERS()) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        currentDriverCount++;
                        OfficeAgent.driverCount++;
                        OfficeAgent.agentCount++;
                    }
                    else if (agent.getType() == OfficeAgent.Type.VISITOR && currentTick >= 3600 && currentTick < 5040 && OfficeAgent.visitorCount < getOffice().getMAX_VISITORS()) {
                        agent.setAgentMovement(new OfficeAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick, 0, null));
                        office.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        currentVisitorCount++;
                        OfficeAgent.visitorCount++;
                        OfficeAgent.agentCount++;
                    }
                }
            }
        }
    }

    public void replenishStaticVars() {
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
        currentManagerCount = 0;
        currentBusinessCount = 0;
        currentResearchCount = 0;
        currentTechnicalCount = 0;
        currentSecretaryCount = 0;
        currentClientCount = 0;
        currentDriverCount = 0;
        currentVisitorCount = 0;
        currentNonverbalCount = 0;
        currentCooperativeCount = 0;
        currentExchangeCount = 0;
        averageNonverbalDuration = 0;
        averageCooperativeDuration = 0;
        averageExchangeDuration = 0;
        currentTeam1Count = 0;
        currentTeam2Count = 0;
        currentTeam3Count = 0;
        currentTeam4Count = 0;
        currentBossManagerCount = 0;
        currentBossBusinessCount = 0;
        currentBossResearcherCount = 0;
        currentBossTechnicalCount = 0;
        currentBossJanitorCount = 0;
        currentBossClientCount = 0;
        currentBossDriverCount = 0;
        currentBossVisitorCount = 0;
        currentBossGuardCount = 0;
        currentBossReceptionistCount = 0;
        currentBossSecretaryCount = 0;
        currentManagerManagerCount = 0;
        currentManagerBusinessCount = 0;
        currentManagerResearcherCount = 0;
        currentManagerTechnicalCount = 0;
        currentManagerJanitorCount = 0;
        currentManagerClientCount = 0;
        currentManagerDriverCount = 0;
        currentManagerVisitorCount = 0;
        currentManagerGuardCount = 0;
        currentManagerReceptionistCount = 0;
        currentManagerSecretaryCount = 0;
        currentBusinessBusinessCount = 0;
        currentBusinessResearcherCount = 0;
        currentBusinessTechnicalCount = 0;
        currentBusinessJanitorCount = 0;
        currentBusinessClientCount = 0;
        currentBusinessDriverCount = 0;
        currentBusinessVisitorCount = 0;
        currentBusinessGuardCount = 0;
        currentBusinessReceptionistCount = 0;
        currentBusinessSecretaryCount = 0;
        currentResearcherResearcherCount = 0;
        currentResearcherTechnicalCount = 0;
        currentResearcherJanitorCount = 0;
        currentResearcherClientCount = 0;
        currentResearcherDriverCount = 0;
        currentResearcherVisitorCount = 0;
        currentResearcherGuardCount = 0;
        currentResearcherReceptionistCount = 0;
        currentResearcherSecretaryCount = 0;
        currentTechnicalTechnicalCount = 0;
        currentTechnicalJanitorCount = 0;
        currentTechnicalClientCount = 0;
        currentTechnicalDriverCount = 0;
        currentTechnicalVisitorCount = 0;
        currentTechnicalGuardCount = 0;
        currentTechnicalReceptionistCount = 0;
        currentTechnicalSecretaryCount = 0;
        currentJanitorJanitorCount = 0;
        currentJanitorClientCount = 0;
        currentJanitorDriverCount = 0;
        currentJanitorVisitorCount = 0;
        currentJanitorGuardCount = 0;
        currentJanitorReceptionistCount = 0;
        currentJanitorSecretaryCount = 0;
        currentClientClientCount = 0;
        currentClientDriverCount = 0;
        currentClientVisitorCount = 0;
        currentClientGuardCount = 0;
        currentClientReceptionistCount = 0;
        currentClientSecretaryCount = 0;
        currentDriverDriverCount = 0;
        currentDriverVisitorCount = 0;
        currentDriverGuardCount = 0;
        currentDriverReceptionistCount = 0;
        currentDriverSecretaryCount = 0;
        currentVisitorVisitorCount = 0;
        currentVisitorGuardCount = 0;
        currentVisitorReceptionistCount = 0;
        currentVisitorSecretaryCount = 0;
        currentGuardGuardCount = 0;
        currentGuardReceptionistCount = 0;
        currentGuardSecretaryCount = 0;
        currentReceptionistReceptionistCount = 0;
        currentReceptionistSecretaryCount = 0;
        currentSecretarySecretaryCount = 0;
        currentPatchCount = new int[office.getRows()][office.getColumns()];
        compiledCurrentManagerCount = new int[6481];
        compiledCurrentBusinessCount = new int[6481];
        compiledCurrentResearchCount = new int[6481];
        compiledCurrentTechnicalCount = new int[6481];
        compiledCurrentSecretaryCount = new int[6481];
        compiledCurrentClientCount = new int[6481];
        compiledCurrentDriverCount = new int[6481];
        compiledCurrentVisitorCount = new int[6481];
        compiledCurrentNonverbalCount = new int[6481];
        compiledCurrentCooperativeCount = new int[6481];
        compiledCurrentExchangeCount = new int[6481];
        compiledAverageNonverbalDuration = new float[6481];
        compiledAverageCooperativeDuration = new float[6481];
        compiledAverageExchangeDuration = new float[6481];
        compiledCurrentTeam1Count = new int[6481];
        compiledCurrentTeam2Count = new int[6481];
        compiledCurrentTeam3Count = new int[6481];
        compiledCurrentTeam4Count = new int[6481];
        compiledCurrentBossManagerCount = new int[6481];
        compiledCurrentBossBusinessCount = new int[6481];
        compiledCurrentBossResearcherCount = new int[6481];
        compiledCurrentBossTechnicalCount = new int[6481];
        compiledCurrentBossJanitorCount = new int[6481];
        compiledCurrentBossClientCount = new int[6481];
        compiledCurrentBossDriverCount = new int[6481];
        compiledCurrentBossVisitorCount = new int[6481];
        compiledCurrentBossGuardCount = new int[6481];
        compiledCurrentBossReceptionistCount = new int[6481];
        compiledCurrentBossSecretaryCount = new int[6481];
        compiledCurrentManagerManagerCount = new int[6481];
        compiledCurrentManagerBusinessCount = new int[6481];
        compiledCurrentManagerResearcherCount = new int[6481];
        compiledCurrentManagerTechnicalCount = new int[6481];
        compiledCurrentManagerJanitorCount = new int[6481];
        compiledCurrentManagerClientCount = new int[6481];
        compiledCurrentManagerDriverCount = new int[6481];
        compiledCurrentManagerVisitorCount = new int[6481];
        compiledCurrentManagerGuardCount = new int[6481];
        compiledCurrentManagerReceptionistCount = new int[6481];
        compiledCurrentManagerSecretaryCount = new int[6481];
        compiledCurrentBusinessBusinessCount = new int[6481];
        compiledCurrentBusinessResearcherCount = new int[6481];
        compiledCurrentBusinessTechnicalCount = new int[6481];
        compiledCurrentBusinessJanitorCount = new int[6481];
        compiledCurrentBusinessClientCount = new int[6481];
        compiledCurrentBusinessDriverCount = new int[6481];
        compiledCurrentBusinessVisitorCount = new int[6481];
        compiledCurrentBusinessGuardCount = new int[6481];
        compiledCurrentBusinessReceptionistCount = new int[6481];
        compiledCurrentBusinessSecretaryCount = new int[6481];
        compiledCurrentResearcherResearcherCount = new int[6481];
        compiledCurrentResearcherTechnicalCount = new int[6481];
        compiledCurrentResearcherJanitorCount = new int[6481];
        compiledCurrentResearcherClientCount = new int[6481];
        compiledCurrentResearcherDriverCount = new int[6481];
        compiledCurrentResearcherVisitorCount = new int[6481];
        compiledCurrentResearcherGuardCount = new int[6481];
        compiledCurrentResearcherReceptionistCount = new int[6481];
        compiledCurrentResearcherSecretaryCount = new int[6481];
        compiledCurrentTechnicalTechnicalCount = new int[6481];
        compiledCurrentTechnicalJanitorCount = new int[6481];
        compiledCurrentTechnicalClientCount = new int[6481];
        compiledCurrentTechnicalDriverCount = new int[6481];
        compiledCurrentTechnicalVisitorCount = new int[6481];
        compiledCurrentTechnicalGuardCount = new int[6481];
        compiledCurrentTechnicalReceptionistCount = new int[6481];
        compiledCurrentTechnicalSecretaryCount = new int[6481];
        compiledCurrentJanitorJanitorCount = new int[6481];
        compiledCurrentJanitorClientCount = new int[6481];
        compiledCurrentJanitorDriverCount = new int[6481];
        compiledCurrentJanitorVisitorCount = new int[6481];
        compiledCurrentJanitorGuardCount = new int[6481];
        compiledCurrentJanitorReceptionistCount = new int[6481];
        compiledCurrentJanitorSecretaryCount = new int[6481];
        compiledCurrentClientClientCount = new int[6481];
        compiledCurrentClientDriverCount = new int[6481];
        compiledCurrentClientVisitorCount = new int[6481];
        compiledCurrentClientGuardCount = new int[6481];
        compiledCurrentClientReceptionistCount = new int[6481];
        compiledCurrentClientSecretaryCount = new int[6481];
        compiledCurrentDriverDriverCount = new int[6481];
        compiledCurrentDriverVisitorCount = new int[6481];
        compiledCurrentDriverGuardCount = new int[6481];
        compiledCurrentDriverReceptionistCount = new int[6481];
        compiledCurrentDriverSecretaryCount = new int[6481];
        compiledCurrentVisitorVisitorCount = new int[6481];
        compiledCurrentVisitorGuardCount = new int[6481];
        compiledCurrentVisitorReceptionistCount = new int[6481];
        compiledCurrentVisitorSecretaryCount = new int[6481];
        compiledCurrentGuardGuardCount = new int[6481];
        compiledCurrentGuardReceptionistCount = new int[6481];
        compiledCurrentGuardSecretaryCount = new int[6481];
        compiledCurrentReceptionistReceptionistCount = new int[6481];
        compiledCurrentReceptionistSecretaryCount = new int[6481];
        compiledCurrentSecretarySecretaryCount = new int[6481];
    }

    public static void exportToCSV() throws Exception{
        PrintWriter writer = new PrintWriter("Office SocialSim Statistics.csv");
        StringBuilder sb = new StringBuilder();
        sb.append("Current Manager Count");
        sb.append(",");
        sb.append("Current Business Count");
        sb.append(",");
        sb.append("Current Research Count");
        sb.append(",");
        sb.append("Current Technical Count");
        sb.append(",");
        sb.append("Current Secretary Count");
        sb.append(",");
        sb.append("Current Client Count");
        sb.append(",");
        sb.append("Current Driver Count");
        sb.append(",");
        sb.append("Current Visitor Count");
        sb.append(",");
        sb.append("Current Nonverbal Count");
        sb.append(",");
        sb.append("Current Cooperative Count");
        sb.append(",");
        sb.append("Current Exchange Count");
        sb.append(",");
        sb.append("Average Nonverbal Duration");
        sb.append(",");
        sb.append("Average Cooperative Duration");
        sb.append(",");
        sb.append("Average Exchange Duration");
        sb.append(",");
        sb.append("Current Team 1 Count");
        sb.append(",");
        sb.append("Current Team 2 Count");
        sb.append(",");
        sb.append("Current Team 3 Count");
        sb.append(",");
        sb.append("Current Team 4 Count");
        sb.append(",");
        sb.append("Current Boss Manager Count");
        sb.append(",");
        sb.append("Current Boss Business Count");
        sb.append(",");
        sb.append("Current Boss Researcher Count");
        sb.append(",");
        sb.append("Current Boss Technical Count");
        sb.append(",");
        sb.append("Current Boss Janitor Count");
        sb.append(",");
        sb.append("Current Boss Client Count");
        sb.append(",");
        sb.append("Current Boss Driver Count");
        sb.append(",");
        sb.append("Current Boss Visitor Count");
        sb.append(",");
        sb.append("Current Boss Guard Count");
        sb.append(",");
        sb.append("Current Boss Receptionist Count");
        sb.append(",");
        sb.append("Current Boss Secretary Count");
        sb.append(",");
        sb.append("Current Manager Manager Count");
        sb.append(",");
        sb.append("Current Manager Business Count");
        sb.append(",");
        sb.append("Current Manager Researcher Count");
        sb.append(",");
        sb.append("Current Manager Technical Count");
        sb.append(",");
        sb.append("Current Manager Janitor Count");
        sb.append(",");
        sb.append("Current Manager Client Count");
        sb.append(",");
        sb.append("Current Manager Driver Count");
        sb.append(",");
        sb.append("Current Manager Visitor Count");
        sb.append(",");
        sb.append("Current Manager Guard Count");
        sb.append(",");
        sb.append("Current Manager Receptionist Count");
        sb.append(",");
        sb.append("Current Manager Secretary Count");
        sb.append(",");
        sb.append("Current Business Business Count");
        sb.append(",");
        sb.append("Current Business Researcher Count");
        sb.append(",");
        sb.append("Current Business Technical Count");
        sb.append(",");
        sb.append("Current Business Janitor Count");
        sb.append(",");
        sb.append("Current Business Client Count");
        sb.append(",");
        sb.append("Current Business Driver Count");
        sb.append(",");
        sb.append("Current Business Visitor Count");
        sb.append(",");
        sb.append("Current Business Guard Count");
        sb.append(",");
        sb.append("Current Business Receptionist Count");
        sb.append(",");
        sb.append("Current Business Secretary Count");
        sb.append(",");
        sb.append("Current Researcher Researcher Count");
        sb.append(",");
        sb.append("Current Researcher Technical Count");
        sb.append(",");
        sb.append("Current Researcher Janitor Count");
        sb.append(",");
        sb.append("Current Researcher Client Count");
        sb.append(",");
        sb.append("Current Researcher Driver Count");
        sb.append(",");
        sb.append("Current Researcher Visitor Count");
        sb.append(",");
        sb.append("Current Researcher Guard Count");
        sb.append(",");
        sb.append("Current Researcher Receptionist Count");
        sb.append(",");
        sb.append("Current Researcher Secretary Count");
        sb.append(",");
        sb.append("Current Technical Technical Count");
        sb.append(",");
        sb.append("Current Technical Janitor Count");
        sb.append(",");
        sb.append("Current Technical Client Count");
        sb.append(",");
        sb.append("Current Technical Driver Count");
        sb.append(",");
        sb.append("Current Technical Visitor Count");
        sb.append(",");
        sb.append("Current Technical Guard Count");
        sb.append(",");
        sb.append("Current Technical Receptionist Count");
        sb.append(",");
        sb.append("Current Technical Secretary Count");
        sb.append(",");
        sb.append("Current Janitor Janitor Count");
        sb.append(",");
        sb.append("Current Janitor Client Count");
        sb.append(",");
        sb.append("Current Janitor Driver Count");
        sb.append(",");
        sb.append("Current Janitor Visitor Count");
        sb.append(",");
        sb.append("Current Janitor Guard Count");
        sb.append(",");
        sb.append("Current Janitor Receptionist Count");
        sb.append(",");
        sb.append("Current Janitor Secretary Count");
        sb.append(",");
        sb.append("Current Client Client Count");
        sb.append(",");
        sb.append("Current Client Driver Count");
        sb.append(",");
        sb.append("Current Client Visitor Count");
        sb.append(",");
        sb.append("Current Client Guard Count");
        sb.append(",");
        sb.append("Current Client Receptionist Count");
        sb.append(",");
        sb.append("Current Client Secretary Count");
        sb.append(",");
        sb.append("Current Driver Driver Count");
        sb.append(",");
        sb.append("Current Driver Visitor Count");
        sb.append(",");
        sb.append("Current Driver Guard Count");
        sb.append(",");
        sb.append("Current Driver Receptionist Count");
        sb.append(",");
        sb.append("Current Driver Secretary Count");
        sb.append(",");
        sb.append("Current Visitor Visitor Count");
        sb.append(",");
        sb.append("Current Visitor Guard Count");
        sb.append(",");
        sb.append("Current Visitor Receptionist Count");
        sb.append(",");
        sb.append("Current Visitor Secretary Count");
        sb.append(",");
        sb.append("Current Guard Guard Count");
        sb.append(",");
        sb.append("Current Guard Receptionist Count");
        sb.append(",");
        sb.append("Current Guard Secretary Count");
        sb.append(",");
        sb.append("Current Receptionist Receptionist Count");
        sb.append(",");
        sb.append("Current Receptionist Secretary Count");
        sb.append(",");
        sb.append("Current Secretary Secretary Count");
        sb.append("\n");
        for (int i = 0; i < 6481; i++){
            sb.append(compiledCurrentManagerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearchCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentNonverbalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCooperativeCount[i]);
            sb.append(",");
            sb.append(compiledCurrentExchangeCount[i]);
            sb.append(",");
            sb.append(compiledAverageNonverbalDuration[i]);
            sb.append(",");
            sb.append(compiledAverageCooperativeDuration[i]);
            sb.append(",");
            sb.append(compiledAverageExchangeDuration[i]);
            sb.append(",");
            sb.append(compiledCurrentTeam1Count[i]);
            sb.append(",");
            sb.append(compiledCurrentTeam2Count[i]);
            sb.append(",");
            sb.append(compiledCurrentTeam3Count[i]);
            sb.append(",");
            sb.append(compiledCurrentTeam4Count[i]);
            sb.append(",");
            sb.append(compiledCurrentBossManagerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossBusinessCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossResearcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossTechnicalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBossSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerManagerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerBusinessCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerResearcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerTechnicalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentManagerSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessBusinessCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessResearcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessTechnicalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBusinessSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherResearcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherTechnicalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentResearcherSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalTechnicalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentTechnicalSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientClientCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentClientSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentDriverDriverCount[i]);
            sb.append(",");
            sb.append(compiledCurrentDriverVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentDriverGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentDriverReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentDriverSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentVisitorVisitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentVisitorGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentVisitorReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentVisitorSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentReceptionistReceptionistCount[i]);
            sb.append(",");
            sb.append(compiledCurrentReceptionistSecretaryCount[i]);
            sb.append(",");
            sb.append(compiledCurrentSecretarySecretaryCount[i]);
            sb.append("\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    public static void exportHeatMap() throws Exception {
        PrintWriter writer = new PrintWriter("Office SocialSim Heat Map.csv");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < currentPatchCount.length; i++){
            for (int j = 0 ; j < currentPatchCount[i].length; j++){
                sb.append(currentPatchCount[i][j]);
                if (j != currentPatchCount[i].length - 1)
                    sb.append(",");
            }
            sb.append("\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }
}