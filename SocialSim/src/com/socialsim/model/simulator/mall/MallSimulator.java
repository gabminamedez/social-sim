package com.socialsim.model.simulator.mall;

import com.socialsim.controller.Main;
import com.socialsim.controller.mall.controls.MallScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.mall.*;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;
import java.io.PrintWriter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MallSimulator extends Simulator {

    public static int defaultMaxFamily = 10;
    public static int defaultMaxFriends = 10;
    public static int defaultMaxCouple = 10;
    public static int defaultMaxAlone = 10;
    public static int defaultMaxCurrentFamily = 10;
    public static int defaultMaxCurrentFriends = 10;
    public static int defaultMaxCurrentCouple = 10;
    public static int defaultMaxCurrentAlone = 10;

    private Mall mall;
    private final AtomicBoolean running;
    private final SimulationTime time;
    private final Semaphore playSemaphore;

    public static int currentPatronCount = 0;

    public static int currentFamilyCount = 0;
    public static int currentFriendsCount = 0;
    public static int currentCoupleCount = 0;
    public static int currentAloneCount = 0;

    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;
    public static int totalFamilyCount = 0;
    public static int totalFriendsCount = 0;
    public static int totalAloneCount = 0;
    public static int totalCoupleCount = 0;
    public static float averageNonverbalDuration = 0;
    public static float averageCooperativeDuration = 0;
    public static float averageExchangeDuration = 0;
    public static int currentPatronPatronCount = 0;
    public static int currentPatronStaffStoreCount = 0;
    public static int currentPatronStaffRestoCount = 0;
    public static int currentPatronStaffKioskCount = 0;
    public static int currentPatronGuardCount = 0;
    public static int currentPatronConciergerCount = 0;
    public static int currentPatronJanitorCount = 0;
    public static int currentJanitorJanitorCount = 0;
    public static int currentStaffStoreStaffStoreCount = 0;
    public static int currentStaffStoreStaffRestoCount = 0;
    public static int currentStaffStoreStaffKioskCount = 0;
    public static int currentStaffStoreGuardCount = 0;
    public static int currentStaffRestoStaffRestoCount = 0;
    public static int currentStaffRestoStaffKioskCount = 0;
    public static int currentStaffRestoGuardCount = 0;
    public static int currentStaffKioskStaffKioskCount = 0;
    public static int currentStaffKioskGuardCount = 0;
    public static int[] compiledCurrentPatronCount;

    public static int[] compiledCurrentFamilyCount;
    public static int[] compiledCurrentFriendsCount;
    public static int[] compiledCurrentCoupleCount;
    public static int[] compiledCurrentAloneCount;

    public static int[] compiledCurrentNonverbalCount;
    public static int[] compiledCurrentCooperativeCount;
    public static int[] compiledCurrentExchangeCount;
    public static int[] compiledTotalFamilyCount;
    public static int[] compiledTotalFriendsCount;
    public static int[] compiledTotalAloneCount;
    public static int[] compiledTotalCoupleCount;
    public static float[] compiledAverageNonverbalDuration;
    public static float[] compiledAverageCooperativeDuration;
    public static float[] compiledAverageExchangeDuration;
    public static int[] compiledCurrentPatronPatronCount;
    public static int[] compiledCurrentPatronStaffStoreCount;
    public static int[] compiledCurrentPatronStaffRestoCount;
    public static int[] compiledCurrentPatronStaffKioskCount;
    public static int[] compiledCurrentPatronGuardCount;
    public static int[] compiledCurrentPatronConciergerCount;
    public static int[] compiledCurrentPatronJanitorCount;
    public static int[] compiledCurrentJanitorJanitorCount;
    public static int[] compiledCurrentStaffStoreStaffStoreCount;
    public static int[] compiledCurrentStaffStoreStaffRestoCount;
    public static int[] compiledCurrentStaffStoreStaffKioskCount;
    public static int[] compiledCurrentStaffStoreGuardCount;
    public static int[] compiledCurrentStaffRestoStaffRestoCount;
    public static int[] compiledCurrentStaffRestoStaffKioskCount;
    public static int[] compiledCurrentStaffRestoGuardCount;
    public static int[] compiledCurrentStaffKioskStaffKioskCount;
    public static int[] compiledCurrentStaffKioskGuardCount;
    public static int[][] currentPatchCount;

    public MallSimulator() {
        this.mall = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(10, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start();
    }

    public Mall getMall() {
        return mall;
    }

    public void setMall(Mall mall) {
        this.mall = mall;
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

    public void resetToDefaultConfiguration(Mall mall) {
        this.mall = mall;
        replenishStaticVars();
        MallAgent.clearMallAgentCounts();
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(Mall mall) {
        mall.createInitialAgentDemographics(getMall().getMAX_FAMILY(), mall.getMAX_FRIENDS(), getMall().getMAX_COUPLE(), getMall().getMAX_ALONE());

        MallAgent guard = mall.getAgents().get(0);
        guard.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 2), guard, null, 1.27, mall.getPatch(33, 2).getPatchCenterCoordinates(), -1, guard.getTeam()));
        mall.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
        MallAgent.guardCount++;
        MallAgent.agentCount++;

        MallAgent guard1 = mall.getAgents().get(1);
        guard1.setAgentMovement(new MallAgentMovement(mall.getPatch(41, 2), guard1, null, 1.27, mall.getPatch(41, 2).getPatchCenterCoordinates(), -1, guard1.getTeam()));
        mall.getAgentPatchSet().add(guard1.getAgentMovement().getCurrentPatch());
        MallAgent.guardCount++;
        MallAgent.agentCount++;

        MallAgent kiosk1 = mall.getAgents().get(2);
        kiosk1.setAgentMovement(new MallAgentMovement(mall.getPatch(22, 53), kiosk1, null, 1.27, mall.getPatch(22, 53).getPatchCenterCoordinates(), -1, kiosk1.getTeam()));
        mall.getAgentPatchSet().add(kiosk1.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk2 = mall.getAgents().get(3);
        kiosk2.setAgentMovement(new MallAgentMovement(mall.getPatch(22, 70), kiosk2, null, 1.27, mall.getPatch(22, 70).getPatchCenterCoordinates(), -1, kiosk2.getTeam()));
        mall.getAgentPatchSet().add(kiosk2.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk3 = mall.getAgents().get(4);
        kiosk3.setAgentMovement(new MallAgentMovement(mall.getPatch(22, 87), kiosk3, null, 1.27, mall.getPatch(22, 87).getPatchCenterCoordinates(), -1, kiosk3.getTeam()));
        mall.getAgentPatchSet().add(kiosk3.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk4 = mall.getAgents().get(5);
        kiosk4.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 53), kiosk4, null, 1.27, mall.getPatch(33, 53).getPatchCenterCoordinates(), -1, kiosk4.getTeam()));
        mall.getAgentPatchSet().add(kiosk4.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk5 = mall.getAgents().get(6);
        kiosk5.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 70), kiosk5, null, 1.27, mall.getPatch(33, 70).getPatchCenterCoordinates(), -1, kiosk5.getTeam()));
        mall.getAgentPatchSet().add(kiosk5.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk6 = mall.getAgents().get(7);
        kiosk6.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 87), kiosk6, null, 1.27, mall.getPatch(33, 87).getPatchCenterCoordinates(), -1, kiosk6.getTeam()));
        mall.getAgentPatchSet().add(kiosk6.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk7 = mall.getAgents().get(8);
        kiosk7.setAgentMovement(new MallAgentMovement(mall.getPatch(27, 97), kiosk7, null, 1.27, mall.getPatch(27, 97).getPatchCenterCoordinates(), -1, kiosk7.getTeam()));
        mall.getAgentPatchSet().add(kiosk7.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk8 = mall.getAgents().get(9);
        kiosk8.setAgentMovement(new MallAgentMovement(mall.getPatch(29, 28), kiosk8, null, 1.27, mall.getPatch(29, 28).getPatchCenterCoordinates(), -1, kiosk8.getTeam()));
        mall.getAgentPatchSet().add(kiosk8.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;

        MallAgent resto1 = mall.getAgents().get(10);
        resto1.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 52), resto1, null, 1.27, mall.getPatch(59, 52).getPatchCenterCoordinates(), -1, resto1.getTeam()));
        mall.getAgentPatchSet().add(resto1.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto2 = mall.getAgents().get(11);
        resto2.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 58), resto2, null, 1.27, mall.getPatch(59, 58).getPatchCenterCoordinates(), -1, resto2.getTeam()));
        mall.getAgentPatchSet().add(resto2.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto3 = mall.getAgents().get(12);
        resto3.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 64), resto3, null, 1.27, mall.getPatch(59, 64).getPatchCenterCoordinates(), -1, resto3.getTeam()));
        mall.getAgentPatchSet().add(resto3.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto4 = mall.getAgents().get(13);
        resto4.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 70), resto4, null, 1.27, mall.getPatch(59, 70).getPatchCenterCoordinates(), -1, resto4.getTeam()));
        mall.getAgentPatchSet().add(resto4.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto5 = mall.getAgents().get(14);
        resto5.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 74), resto5, null, 1.27, mall.getPatch(59, 74).getPatchCenterCoordinates(), -1, resto5.getTeam()));
        mall.getAgentPatchSet().add(resto5.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto6 = mall.getAgents().get(15);
        resto6.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 80), resto6, null, 1.27, mall.getPatch(59, 80).getPatchCenterCoordinates(), -1, resto6.getTeam()));
        mall.getAgentPatchSet().add(resto6.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto7 = mall.getAgents().get(16);
        resto7.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 86), resto7, null, 1.27, mall.getPatch(59, 86).getPatchCenterCoordinates(), -1, resto7.getTeam()));
        mall.getAgentPatchSet().add(resto7.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto8 = mall.getAgents().get(17);
        resto8.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 92), resto8, null, 1.27, mall.getPatch(59, 92).getPatchCenterCoordinates(), -1, resto8.getTeam()));
        mall.getAgentPatchSet().add(resto8.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;

        MallAgent cashier1 = mall.getAgents().get(18);
        cashier1.setAgentMovement(new MallAgentMovement(mall.getPatch(10, 19), cashier1, null, 1.27, mall.getPatch(10, 19).getPatchCenterCoordinates(), -1, cashier1.getTeam()));
        mall.getAgentPatchSet().add(cashier1.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier2 = mall.getAgents().get(19);
        cashier2.setAgentMovement(new MallAgentMovement(mall.getPatch(5, 41), cashier2, null, 1.27, mall.getPatch(5, 41).getPatchCenterCoordinates(), -1, cashier2.getTeam()));
        mall.getAgentPatchSet().add(cashier2.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier3 = mall.getAgents().get(20);
        cashier3.setAgentMovement(new MallAgentMovement(mall.getPatch(49, 19), cashier3, null, 1.27, mall.getPatch(49, 19).getPatchCenterCoordinates(), -1, cashier3.getTeam()));
        mall.getAgentPatchSet().add(cashier3.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier4 = mall.getAgents().get(21);
        cashier4.setAgentMovement(new MallAgentMovement(mall.getPatch(54, 41), cashier4, null, 1.27, mall.getPatch(54, 41).getPatchCenterCoordinates(), -1, cashier4.getTeam()));
        mall.getAgentPatchSet().add(cashier4.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier5 = mall.getAgents().get(22);
        cashier5.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 55), cashier5, null, 1.27, mall.getPatch(0, 55).getPatchCenterCoordinates(), -1, cashier5.getTeam()));
        mall.getAgentPatchSet().add(cashier5.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier6 = mall.getAgents().get(23);
        cashier6.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 66), cashier6, null, 1.27, mall.getPatch(0, 66).getPatchCenterCoordinates(), -1, cashier6.getTeam()));
        mall.getAgentPatchSet().add(cashier6.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier7 = mall.getAgents().get(24);
        cashier7.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 84), cashier7, null, 1.27, mall.getPatch(0, 84).getPatchCenterCoordinates(), -1, cashier7.getTeam()));
        mall.getAgentPatchSet().add(cashier7.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier8 = mall.getAgents().get(25);
        cashier8.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 102), cashier8, null, 1.27, mall.getPatch(0, 102).getPatchCenterCoordinates(), -1, cashier8.getTeam()));
        mall.getAgentPatchSet().add(cashier8.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier10 = mall.getAgents().get(26);
        cashier10.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 100), cashier10, null, 1.27, mall.getPatch(59, 100).getPatchCenterCoordinates(), -1, cashier10.getTeam()));
        mall.getAgentPatchSet().add(cashier10.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;

        MallAgent sales = mall.getAgents().get(27);
        sales.setAgentMovement(new MallAgentMovement(mall.getPatch(14, 14), sales, null, 1.27, mall.getPatch(14, 14).getPatchCenterCoordinates(), -1, sales.getTeam()));
        mall.getAgentPatchSet().add(sales.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales1 = mall.getAgents().get(28);
        sales1.setAgentMovement(new MallAgentMovement(mall.getPatch(7, 36), sales1, null, 1.27, mall.getPatch(7, 36).getPatchCenterCoordinates(), -1, sales1.getTeam()));
        mall.getAgentPatchSet().add(sales1.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales3 = mall.getAgents().get(29);
        sales3.setAgentMovement(new MallAgentMovement(mall.getPatch(46, 14), sales3, null, 1.27, mall.getPatch(46, 14).getPatchCenterCoordinates(), -1, sales3.getTeam()));
        mall.getAgentPatchSet().add(sales3.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales5 = mall.getAgents().get(30);
        sales5.setAgentMovement(new MallAgentMovement(mall.getPatch(51, 36), sales5, null, 1.27, mall.getPatch(51, 36).getPatchCenterCoordinates(), -1, sales5.getTeam()));
        mall.getAgentPatchSet().add(sales5.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales7 = mall.getAgents().get(31);
        sales7.setAgentMovement(new MallAgentMovement(mall.getPatch(4, 52), sales7, null, 1.27, mall.getPatch(4, 52).getPatchCenterCoordinates(), -1, sales7.getTeam()));
        mall.getAgentPatchSet().add(sales7.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales9 = mall.getAgents().get(32);
        sales9.setAgentMovement(new MallAgentMovement(mall.getPatch(4, 63), sales9, null, 1.27, mall.getPatch(4, 63).getPatchCenterCoordinates(), -1, sales9.getTeam()));
        mall.getAgentPatchSet().add(sales9.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales11 = mall.getAgents().get(33);
        sales11.setAgentMovement(new MallAgentMovement(mall.getPatch(4, 81), sales11, null, 1.27, mall.getPatch(4, 81).getPatchCenterCoordinates(), -1, sales11.getTeam()));
        mall.getAgentPatchSet().add(sales11.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales13 = mall.getAgents().get(34);
        sales13.setAgentMovement(new MallAgentMovement(mall.getPatch(4, 99), sales13, null, 1.27, mall.getPatch(4, 99).getPatchCenterCoordinates(), -1, sales13.getTeam()));
        mall.getAgentPatchSet().add(sales13.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales17 = mall.getAgents().get(35);
        sales17.setAgentMovement(new MallAgentMovement(mall.getPatch(56, 97), sales17, null, 1.27, mall.getPatch(56, 97).getPatchCenterCoordinates(), -1, sales17.getTeam()));
        mall.getAgentPatchSet().add(sales17.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;

        MallAgent concierger = mall.getAgents().get(36);
        concierger.setAgentMovement(new MallAgentMovement(mall.getPatch(29, 11), concierger, null, 1.27, mall.getPatch(29, 11).getPatchCenterCoordinates(), -1, concierger.getTeam()));
        mall.getAgentPatchSet().add(concierger.getAgentMovement().getCurrentPatch());
        MallAgent.conciergerCount++;
        MallAgent.agentCount++;
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
                            updateAgentsInMall(mall, currentTick);
                            spawnAgent(mall, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        ((MallScreenController) Main.mainScreenController).drawMallViewForeground(Main.mallSimulator.getMall(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 8640) {
                            ((MallScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInMall(Mall mall, long currentTick) throws InterruptedException {
        moveAll(mall);
        compiledCurrentPatronCount[(int) currentTick] = currentPatronCount;
        compiledCurrentNonverbalCount[(int) currentTick] = currentNonverbalCount;
        compiledCurrentCooperativeCount[(int) currentTick] = currentCooperativeCount;
        compiledCurrentExchangeCount[(int) currentTick] = currentExchangeCount;
        compiledTotalFamilyCount[(int) currentTick] = totalFamilyCount;
        compiledTotalFriendsCount[(int) currentTick] = totalFriendsCount;
        compiledTotalAloneCount[(int) currentTick] = totalAloneCount;
        compiledTotalCoupleCount[(int) currentTick] = totalCoupleCount;
        compiledAverageNonverbalDuration[(int) currentTick] = averageNonverbalDuration;
        compiledAverageCooperativeDuration[(int) currentTick] = averageCooperativeDuration;
        compiledAverageExchangeDuration[(int) currentTick] = averageExchangeDuration;
        compiledCurrentPatronPatronCount[(int) currentTick] = currentPatronPatronCount;
        compiledCurrentPatronStaffStoreCount[(int) currentTick] = currentPatronStaffStoreCount;
        compiledCurrentPatronStaffRestoCount[(int) currentTick] = currentPatronStaffRestoCount;
        compiledCurrentPatronStaffKioskCount[(int) currentTick] = currentPatronStaffKioskCount;
        compiledCurrentPatronGuardCount[(int) currentTick] = currentPatronGuardCount;
        compiledCurrentPatronConciergerCount[(int) currentTick] = currentPatronConciergerCount;
        compiledCurrentPatronJanitorCount[(int) currentTick] = currentPatronJanitorCount;
        compiledCurrentJanitorJanitorCount[(int) currentTick] = currentJanitorJanitorCount;
        compiledCurrentStaffStoreStaffStoreCount[(int) currentTick] = currentStaffStoreStaffStoreCount;
        compiledCurrentStaffStoreStaffRestoCount[(int) currentTick] = currentStaffStoreStaffRestoCount;
        compiledCurrentStaffStoreStaffKioskCount[(int) currentTick] = currentStaffStoreStaffKioskCount;
        compiledCurrentStaffStoreGuardCount[(int) currentTick] = currentStaffStoreGuardCount;
        compiledCurrentStaffRestoStaffRestoCount[(int) currentTick] = currentStaffRestoStaffRestoCount;
        compiledCurrentStaffRestoStaffKioskCount[(int) currentTick] = currentStaffRestoStaffKioskCount;
        compiledCurrentStaffRestoGuardCount[(int) currentTick] = currentStaffRestoGuardCount;
        compiledCurrentStaffKioskStaffKioskCount[(int) currentTick] = currentStaffKioskStaffKioskCount;
        compiledCurrentStaffKioskGuardCount[(int) currentTick] = currentStaffKioskGuardCount;
    }

    private static void moveAll(Mall mall) {
        int bathroomReserves = mall.numBathroomsFree();
        for (MallAgent agent : mall.getMovableAgents()) {
            try {
                if (agent.getAgentMovement().getCurrentState().getName() == MallState.Name.WAIT_INFRONT_OF_BATHROOM){
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
                moveOne(agent);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(MallAgent agent) throws Throwable {
        MallAgentMovement agentMovement = agent.getAgentMovement();

        MallAgent.Type type = agent.getType();
        MallAgent.Persona persona = agent.getPersona();
        MallState state = agentMovement.getCurrentState();
        MallAction action = agentMovement.getCurrentAction();

        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()){
            switch (type) {
                case STAFF_RESTO:
                    if (state.getName() == MallState.Name.STAFF_RESTO) {
                        if (action.getName() == MallAction.Name.STAFF_RESTO_SERVE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseRandomTable();
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                                else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                        }
                                    }
                                }
                            }
                            else {
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }

                    break;

                case STAFF_STORE_SALES:
                    if (state.getName() == MallState.Name.STAFF_STORE_SALES) {
                        if (action.getName() == MallAction.Name.STAFF_STORE_STATION) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseRandomAisle();
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                                else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                        }
                                    }
                                }
                            }
                            else {
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }

                    break;

                case PATRON:
                    if (state.getName() == MallState.Name.GOING_TO_SECURITY) {
                        if (action.getName() == MallAction.Name.GOING_TO_SECURITY_QUEUE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                if (agentMovement.getIsGate1()) {
                                    agentMovement.setGoalQueueingPatchField(Main.mallSimulator.getMall().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                    agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getSecurities().get(0));
                                }
                                else {
                                    agentMovement.setGoalQueueingPatchField(Main.mallSimulator.getMall().getSecurities().get(1).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                    agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getSecurities().get(1));
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
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == MallAction.Name.GO_THROUGH_SCANNER) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                if (!agentMovement.isStationInteracting()) {
                                    if (persona == MallAgent.Persona.LOITER_ALONE || persona == MallAgent.Persona.LOITER_COUPLE || persona == MallAgent.Persona.LOITER_FAMILY || persona == MallAgent.Persona.LOITER_FRIENDS) {
                                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                        if (x < MallRoutePlan.CHANCE_GUARD_INTERACT) {
                                            agentMovement.forceStationedInteraction(MallAgent.Persona.GUARD);
                                        }
                                    }
                                }

                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.setStationInteracting(false);
                                }
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.WANDERING_AROUND) {
                        if (action.getName() == MallAction.Name.FIND_BENCH || action.getName() == MallAction.Name.FIND_DIRECTORY || action.getName() == MallAction.Name.GO_CONCIERGE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (action.getName() == MallAction.Name.FIND_BENCH) {
                                    if (!agentMovement.chooseGoal(Bench.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else if(action.getName() == MallAction.Name.GO_CONCIERGE){
                                    if (!agentMovement.chooseGoal(Concierge.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    agentMovement.chooseRandomDigital();
                                }
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
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
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
                                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == MallAction.Name.SIT_ON_BENCH || action.getName() == MallAction.Name.VIEW_DIRECTORY || action.getName() == MallAction.Name.ASK_CONCIERGE) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
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
                    else if (state.getName() == MallState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == MallAction.Name.GO_TO_BATHROOM) {
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
//                                    agentMovement.setNextState(agentMovement.getStateIndex());
//                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
//                                    agentMovement.setActionIndex(0);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    agentMovement.resetGoal();
                                }
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
                        else if (action.getName() == MallAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                if(agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                        }
                        else if (action.getName() == MallAction.Name.WASH_IN_SINK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                                else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
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
                                else {
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
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_TO_SHOWCASE) {
                        if (action.getName() == MallAction.Name.GO_TO_KIOSK) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseKiosk();
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.joinQueue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.IN_SHOWCASE) {
                        if (action.getName() == MallAction.Name.QUEUE_KIOSK) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == MallAction.Name.CHECKOUT_KIOSK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_KIOSK);
                            }

                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.setStationInteracting(false);
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_TO_RESTO) {
                        if (action.getName() == MallAction.Name.GO_TO_RESTAURANT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseRandomTablePatron("RESTO");
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                    else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                            }
                                        }
                                    }
                                }
                                else {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.IN_RESTO) {
                        if (action.getName() == MallAction.Name.RESTAURANT_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() != null) {
                                if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                    agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_RESTO);
                                }
                                agentMovement.setSimultaneousInteractionAllowed(true);
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    if (agentMovement.getLeaderAgent() == null) {
                                        agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_RESTO);
                                    }
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.getGoalAmenity().getAttractors().get(0).setIsReserved(false);
                                    agentMovement.resetGoal();
                                    agentMovement.setStationInteracting(false);
                                    agentMovement.setSimultaneousInteractionAllowed(false);
                                }
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_TO_DINING) {
                        if (action.getName() == MallAction.Name.GO_TO_KIOSK) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.mallSimulator.getMall().getKioskFields().get(0));
                                agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getKiosks().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalQueueingPatchField().getAssociatedPatches().get(0).getAmenityBlock());
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
                        else if (action.getName() == MallAction.Name.QUEUE_KIOSK) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == MallAction.Name.CHECKOUT_KIOSK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_KIOSK);
                            }

                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.setStationInteracting(false);
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.IN_DINING) {
                        if (action.getName() == MallAction.Name.GO_TO_DINING_AREA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseRandomTablePatron("DINING");
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
                        else if (action.getName() == MallAction.Name.DINING_AREA_STAY_PUT) {
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.getGoalAmenity().getAttractors().get(0).setIsReserved(false);
                                agentMovement.resetGoal();
                                agentMovement.setStationInteracting(false);
                                agentMovement.setSimultaneousInteractionAllowed(false);
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_TO_STORE) {
                        agentMovement.getRoutePlan().MAX_STORE_HELP = 1;
                        if (action.getName() == MallAction.Name.GO_TO_STORE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                if (agentMovement.getLeaderAgent() == null) {
                                    agentMovement.setGoalAttractor(agentMovement.getCurrentAction().getDestination().getAmenityBlock());
                                }
                                else {
                                    agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(agentMovement.getGoalAmenity().getAttractors().size())));
                                }
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                                else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                        }
                                    }
                                }
                            }
                            else {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.IN_STORE) {
                        if (action.getName() == MallAction.Name.CHECK_AISLE) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                                agentMovement.setSimultaneousInteractionAllowed(false);
                            }
                            else {
                                int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                if (persona == MallAgent.Persona.LOITER_ALONE || persona == MallAgent.Persona.LOITER_FRIENDS || persona == MallAgent.Persona.LOITER_FAMILY || persona == MallAgent.Persona.LOITER_COUPLE) {
                                    if (x < MallRoutePlan.STORE_HELP_CHANCE_LOITER && agentMovement.getRoutePlan().MAX_STORE_HELP > 0) {
                                        agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_STORE_SALES);
                                        agentMovement.setStationInteracting(false);
                                        agentMovement.getRoutePlan().MAX_STORE_HELP--;
                                    }
                                }
                                else {
                                    if (x < MallRoutePlan.STORE_HELP_CHANCE_ERRAND && agentMovement.getRoutePlan().MAX_STORE_HELP > 0) {
                                        agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_STORE_SALES);
                                        agentMovement.setStationInteracting(false);
                                        agentMovement.getRoutePlan().MAX_STORE_HELP--;
                                    }
                                }
                            }
                        }
                        else if (action.getName() == MallAction.Name.GO_TO_AISLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                if (agentMovement.getLeaderAgent() == null) {
                                    agentMovement.setGoalAttractor(agentMovement.getCurrentAction().getDestination().getAmenityBlock());
                                }
                                else {
                                    agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(agentMovement.getGoalAmenity().getAttractors().size())));
                                }
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                                else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                        }
                                    }
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == MallAction.Name.CHECKOUT_STORE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getStoreCounters().get(agentMovement.getCurrentState().getStoreNum()));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(agentMovement.getGoalAmenity().getAttractors().size())));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                    else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                        agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_STORE_CASHIER);
                                    }

                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.setStationInteracting(false);
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_HOME) {
                        if (action.getName() == MallAction.Name.LEAVE_BUILDING) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                boolean isGate1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
                                if (isGate1) {
                                    agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getMallGates().get(0));
                                }
                                else {
                                    agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getMallGates().get(1));
                                }
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
                    else if(state.getName() == MallState.Name.WAIT_INFRONT_OF_BATHROOM){
                        if (action.getName() == MallAction.Name.GO_TO_WAIT_AREA) {
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
                        else if(action.getName() == MallAction.Name.WAIT_FOR_VACANT){
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
            }
        }

        if (agentMovement.isInteracting()) {
            agentMovement.interact();
        }
        else {
            List<Patch> patches = agentMovement.get7x7Field(agentMovement.getHeading(), true, agentMovement.getFieldOfViewAngle());
            MallAgent agent2 = null;
            for (Patch patch: patches) {
                for (Agent otherAgent: patch.getAgents()) {
                    MallAgent mallAgent = (MallAgent) otherAgent;
                    if (!mallAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), mallAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(mallAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), mallAgent.getAgentMovement().getProposedHeading(), mallAgent.getAgentMovement().getFieldOfViewAngle())){
                                agentMovement.rollAgentInteraction(mallAgent);
                                if (agentMovement.isInteracting()) {
                                    agent2 = mallAgent;
                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
                                    currentPatchCount[mallAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][mallAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
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
//                    MallAgent mallAgent = (MallAgent) otherAgent;
//                    if (!mallAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
//                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), mallAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
//                            if (Coordinates.isWithinFieldOfView(mallAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), mallAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
//                                agentMovement.rollAgentInteraction(mallAgent);
//                                if (agentMovement.isInteracting()) {
//                                    agent2 = mallAgent;
//                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
//                                    currentPatchCount[mallAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][mallAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
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

    private void spawnAgent(Mall mall, long currentTick) {
        MallGate gate = null;
        MallGate gate1 = mall.getMallGates().get(2);
        MallGate gate2 = mall.getMallGates().get(3);

        MallAgent agent2 = null;
        MallAgent agent3 = null;
        MallAgent agent4 = null;

        boolean isGate1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
        if (isGate1) {
            gate = gate1;
        }
        else {
            gate = gate2;
        }

        Gate.GateBlock spawner1 = gate.getSpawners().get(0);
        Gate.GateBlock spawner2 = gate.getSpawners().get(1);
        Gate.GateBlock spawner3 = gate.getSpawners().get(2);
        Gate.GateBlock spawner4 = gate.getSpawners().get(3);

        int spawnChance = (int) gate.getChancePerTick();
        int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        int type = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5);

        if (CHANCE > spawnChance) {
            if (type == 0 && totalFamilyCount < getMall().getMAX_FAMILY() && currentFamilyCount < mall.getMAX_CURRENT_FAMILY()) {
                if (mall.getUnspawnedFamilyAgents().size() > 0){
                    int randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedFamilyAgents().size());
                    MallAgent leaderAgent = mall.getUnspawnedFamilyAgents().get(randNum);
                    while(!leaderAgent.isLeader()) {
                        randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedFamilyAgents().size());
                        leaderAgent = mall.getUnspawnedFamilyAgents().get(randNum);
                    }
                    agent2 = mall.getUnspawnedFamilyAgents().get(randNum + 1);
                    agent3 = mall.getUnspawnedFamilyAgents().get(randNum + 2);
                    agent4 = mall.getUnspawnedFamilyAgents().get(randNum + 3);

                    leaderAgent.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), leaderAgent, null, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick, leaderAgent.getTeam()));
                    mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());
                    leaderAgent.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent2.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent2, leaderAgent, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick, agent2.getTeam()));
                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                    agent2.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent3.setAgentMovement(new MallAgentMovement(spawner3.getPatch(), agent3, leaderAgent, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick, agent3.getTeam()));
                    mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
                    agent3.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent4.setAgentMovement(new MallAgentMovement(spawner4.getPatch(), agent4, leaderAgent, 1.27, spawner4.getPatch().getPatchCenterCoordinates(), currentTick, agent4.getTeam()));
                    mall.getAgentPatchSet().add(agent4.getAgentMovement().getCurrentPatch());
                    agent4.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    leaderAgent.getAgentMovement().getFollowers().add(agent2);
                    leaderAgent.getAgentMovement().getFollowers().add(agent3);
                    leaderAgent.getAgentMovement().getFollowers().add(agent4);

                    totalFamilyCount++;
                    currentFamilyCount++;
                }
            }
            else if (type == 1 && totalFriendsCount < mall.getMAX_FRIENDS() && currentFriendsCount < mall.getMAX_CURRENT_FRIENDS()) {
                if (mall.getUnspawnedFriendsAgents().size() > 0){
                    int randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedFriendsAgents().size());
                    MallAgent leaderAgent = mall.getUnspawnedFriendsAgents().get(randNum);
                    while(!leaderAgent.isLeader()) {
                        randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedFriendsAgents().size());
                        leaderAgent = mall.getUnspawnedFriendsAgents().get(randNum);
                    }
                    agent2 = mall.getUnspawnedFriendsAgents().get(randNum + 1);
                    agent3 = mall.getUnspawnedFriendsAgents().get(randNum + 2);

                    leaderAgent.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), leaderAgent, null, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick, leaderAgent.getTeam()));
                    mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());
                    leaderAgent.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent2.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent2, leaderAgent, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick, agent2.getTeam()));
                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                    agent2.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent3.setAgentMovement(new MallAgentMovement(spawner3.getPatch(), agent3, leaderAgent, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick, agent3.getTeam()));
                    mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
                    agent3.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    leaderAgent.getAgentMovement().getFollowers().add(agent2);
                    leaderAgent.getAgentMovement().getFollowers().add(agent3);

                    totalFriendsCount++;
                    currentFriendsCount++;
                }

            }
            else if (type == 2 && totalCoupleCount < getMall().getMAX_COUPLE() && currentCoupleCount < mall.getMAX_CURRENT_COUPLE()) {
                if (mall.getUnspawnedCoupleAgents().size() > 0){
                    int randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedCoupleAgents().size());
                    MallAgent leaderAgent = mall.getUnspawnedCoupleAgents().get(randNum);
                    while(!leaderAgent.isLeader()) {
                        randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedCoupleAgents().size());
                        leaderAgent = mall.getUnspawnedCoupleAgents().get(randNum);
                    }
                    agent2 = mall.getUnspawnedCoupleAgents().get(randNum + 1);

                    leaderAgent.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), leaderAgent, null, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick, leaderAgent.getTeam()));
                    mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());
                    leaderAgent.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent2.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent2, leaderAgent, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick, agent2.getTeam()));
                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                    agent2.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    leaderAgent.getAgentMovement().getFollowers().add(agent2);

                    totalCoupleCount++;
                    currentCoupleCount++;
                }
            }
            else if (type == 3 && totalAloneCount < getMall().getMAX_ALONE() && currentAloneCount < mall.getMAX_CURRENT_ALONE()) {
                if (mall.getUnspawnedAloneAgents().size() > 0){
                    MallAgent leaderAgent = mall.getUnspawnedAloneAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedAloneAgents().size()));
                    leaderAgent.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), leaderAgent, null, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick, leaderAgent.getTeam()));
                    mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());
                    leaderAgent.getAgentMovement().setIsGate1(isGate1);
                    currentPatronCount++;
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    totalAloneCount++;
                    currentAloneCount++;
                }
            }
        }
    }

    public void replenishStaticVars() {
        currentPatronCount = 0;

        currentFamilyCount = 0;
        currentFriendsCount = 0;
        currentCoupleCount = 0;
        currentAloneCount = 0;

        currentNonverbalCount = 0;
        currentCooperativeCount = 0;
        currentExchangeCount = 0;
        totalFamilyCount = 0;
        totalFriendsCount = 0;
        totalAloneCount = 0;
        totalCoupleCount = 0;
        averageNonverbalDuration = 0;
        averageCooperativeDuration = 0;
        averageExchangeDuration = 0;
        currentPatronPatronCount = 0;
        currentPatronStaffStoreCount = 0;
        currentPatronStaffRestoCount = 0;
        currentPatronStaffKioskCount = 0;
        currentPatronGuardCount = 0;
        currentPatronConciergerCount = 0;
        currentPatronJanitorCount = 0;
        currentJanitorJanitorCount = 0;
        currentStaffStoreStaffStoreCount = 0;
        currentStaffStoreStaffRestoCount = 0;
        currentStaffStoreStaffKioskCount = 0;
        currentStaffStoreGuardCount = 0;
        currentStaffRestoStaffRestoCount = 0;
        currentStaffRestoStaffKioskCount = 0;
        currentStaffRestoGuardCount = 0;
        currentStaffKioskStaffKioskCount = 0;
        currentStaffKioskGuardCount = 0;
        currentPatchCount = new int[mall.getRows()][mall.getColumns()];
        compiledCurrentPatronCount = new int[8641];

        compiledCurrentFamilyCount = new int[8641];
        compiledCurrentFriendsCount = new int[8641];
        compiledCurrentCoupleCount = new int[8641];
        compiledCurrentAloneCount = new int[8641];

        compiledCurrentNonverbalCount = new int[8641];
        compiledCurrentCooperativeCount = new int[8641];
        compiledCurrentExchangeCount = new int[8641];
        compiledTotalFamilyCount = new int[8641];
        compiledTotalFriendsCount = new int[8641];
        compiledTotalAloneCount = new int[8641];
        compiledTotalCoupleCount = new int[8641];
        compiledAverageNonverbalDuration = new float[8641];
        compiledAverageCooperativeDuration = new float[8641];
        compiledAverageExchangeDuration = new float[8641];
        compiledCurrentPatronPatronCount = new int[8641];
        compiledCurrentPatronStaffStoreCount = new int[8641];
        compiledCurrentPatronStaffRestoCount = new int[8641];
        compiledCurrentPatronStaffKioskCount = new int[8641];
        compiledCurrentPatronGuardCount = new int[8641];
        compiledCurrentPatronConciergerCount = new int[8641];
        compiledCurrentPatronJanitorCount = new int[8641];
        compiledCurrentJanitorJanitorCount = new int[8641];
        compiledCurrentStaffStoreStaffStoreCount = new int[8641];
        compiledCurrentStaffStoreStaffRestoCount = new int[8641];
        compiledCurrentStaffStoreStaffKioskCount = new int[8641];
        compiledCurrentStaffStoreGuardCount = new int[8641];
        compiledCurrentStaffRestoStaffRestoCount = new int[8641];
        compiledCurrentStaffRestoStaffKioskCount = new int[8641];
        compiledCurrentStaffRestoGuardCount = new int[8641];
        compiledCurrentStaffKioskStaffKioskCount = new int[8641];
        compiledCurrentStaffKioskGuardCount = new int[8641];
    }

    public static void exportToCSV() throws Exception{
        PrintWriter writer = new PrintWriter("Mall SocialSim Statistics.csv");
        StringBuilder sb = new StringBuilder();
        sb.append("Current Patron Count");
        sb.append(",");

        sb.append("Current Family Count");
        sb.append(",");
        sb.append("Current Friends Count");
        sb.append(",");
        sb.append("Current Couple Count");
        sb.append(",");
        sb.append("Current Alone Count");
        sb.append(",");

        sb.append("Current Nonverbal Count");
        sb.append(",");
        sb.append("Current Cooperative Count");
        sb.append(",");
        sb.append("Current Exchange Count");
        sb.append(",");
        sb.append("Total Family Count");
        sb.append(",");
        sb.append("Total Friends Count");
        sb.append(",");
        sb.append("Total Alone Count");
        sb.append(",");
        sb.append("Total Couple Count");
        sb.append(",");
        sb.append("Average Nonverbal Duration");
        sb.append(",");
        sb.append("Average Cooperative Duration");
        sb.append(",");
        sb.append("Average Exchange Duration");
        sb.append(",");
        sb.append("Current Patron Patron Count");
        sb.append(",");
        sb.append("Current Patron StaffStore Count");
        sb.append(",");
        sb.append("Current Patron StaffResto Count");
        sb.append(",");
        sb.append("Current Patron StaffKiosk Count");
        sb.append(",");
        sb.append("Current Patron Guard Count");
        sb.append(",");
        sb.append("Current Patron Concierger Count");
        sb.append(",");
        sb.append("Current Patron Janitor Count");
        sb.append(",");
        sb.append("Current Janitor Janitor Count");
        sb.append(",");
        sb.append("Current StaffStore StaffStore Count");
        sb.append(",");
        sb.append("Current StaffStore StaffResto Count");
        sb.append(",");
        sb.append("Current StaffStore StaffKiosk Count");
        sb.append(",");
        sb.append("Current StaffStore Guard Count");
        sb.append(",");
        sb.append("Current StaffResto StaffResto Count");
        sb.append(",");
        sb.append("Current StaffResto StaffKiosk Count");
        sb.append(",");
        sb.append("Current StaffResto Guard Count");
        sb.append(",");
        sb.append("Current StaffKiosk StaffKiosk Count");
        sb.append(",");
        sb.append("Current StaffKiosk Guard Count");
        sb.append("\n");
        for (int i = 0; i < 8641; i++){
            sb.append(compiledCurrentPatronCount[i]);
            sb.append(",");

            sb.append(compiledCurrentFamilyCount[i]);
            sb.append(",");
            sb.append(compiledCurrentFriendsCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCoupleCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAloneCount[i]);
            sb.append(",");

            sb.append(compiledCurrentNonverbalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCooperativeCount[i]);
            sb.append(",");
            sb.append(compiledCurrentExchangeCount[i]);
            sb.append(",");
            sb.append(compiledTotalFamilyCount[i]);
            sb.append(",");
            sb.append(compiledTotalFriendsCount[i]);
            sb.append(",");
            sb.append(compiledTotalAloneCount[i]);
            sb.append(",");
            sb.append(compiledTotalCoupleCount[i]);
            sb.append(",");
            sb.append(compiledAverageNonverbalDuration[i]);
            sb.append(",");
            sb.append(compiledAverageCooperativeDuration[i]);
            sb.append(",");
            sb.append(compiledAverageExchangeDuration[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronPatronCount[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronStaffStoreCount[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronStaffRestoCount[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronStaffKioskCount[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronConciergerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentPatronJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffStoreStaffStoreCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffStoreStaffRestoCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffStoreStaffKioskCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffStoreGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffRestoStaffRestoCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffRestoStaffKioskCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffRestoGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffKioskStaffKioskCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffKioskGuardCount[i]);
            sb.append("\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    public static void exportHeatMap() throws Exception {
        PrintWriter writer = new PrintWriter("Mall SocialSim Heat Map.csv");
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