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

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MallSimulator extends Simulator {

    public static int defaultMaxFamily = 10;
    public static int defaultMaxFriends = 10;
    public static int defaultMaxCouple = 10;
    public static int defaultMaxAlone = 10;

    private Mall mall;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;


    public static int currentPatronCount = 0;
    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;

    public static int totalFamilyCount = 0;
    public static int totalFriendsCount = 0;
    public static int totalAloneCount = 0;
    public static int totalCoupleCount = 0;

    public static int averageNonverbalDuration = 0;
    public static int averageCooperativeDuration = 0;
    public static int averageExchangeDuration = 0;

    public static int currentPatronPatronCount = 0;
    public static int currentPatronStaffStoreCount = 0;
    public static int currentPatronStaffRestoCount = 0;
    public static int currentPatronStaffKioskCount = 0;
    public static int currentPatronGuardCount = 0;
    public static int currentStaffStoreStaffStoreCount = 0;
    public static int currentStaffStoreStaffRestoCount = 0;
    public static int currentStaffStoreStaffKioskCount = 0;
    public static int currentStaffStoreGuardCount = 0;
    public static int currentStaffRestoStaffRestoCount = 0;
    public static int currentStaffRestoStaffKioskCount = 0;
    public static int currentStaffRestoGuardCount = 0;
    public static int currentStaffKioskStaffKioskCount = 0;
    public static int currentStaffKioskGuardCount = 0;
    public static int[][] currentPatchCount;


    public MallSimulator() {
        this.mall = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(10, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start(); // Start the simulation thread, but in reality it would be activated much later
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
        this.time.reset();
        this.running.set(false);
        currentPatchCount = new int[mall.getRows()][mall.getColumns()];
    }

    public void spawnInitialAgents(Mall mall) {
        mall.createInitialAgentDemographics(MAX_FAMILY, MAX_FRIENDS, MAX_COUPLE, MAX_ALONE);

        MallAgent guard = mall.getAgents().get(0);
        guard.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 2), guard, null, 1.27, mall.getPatch(33, 2).getPatchCenterCoordinates(), -1, guard.getTeam()));
        mall.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
        MallAgent.guardCount++;
        MallAgent.agentCount++;

        MallAgent kiosk1 = mall.getAgents().get(1);
        kiosk1.setAgentMovement(new MallAgentMovement(mall.getPatch(22, 53), kiosk1, null, 1.27, mall.getPatch(22, 53).getPatchCenterCoordinates(), -1, kiosk1.getTeam()));
        mall.getAgentPatchSet().add(kiosk1.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk2 = mall.getAgents().get(2);
        kiosk2.setAgentMovement(new MallAgentMovement(mall.getPatch(22, 70), kiosk2, null, 1.27, mall.getPatch(22, 70).getPatchCenterCoordinates(), -1, kiosk2.getTeam()));
        mall.getAgentPatchSet().add(kiosk2.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk3 = mall.getAgents().get(3);
        kiosk3.setAgentMovement(new MallAgentMovement(mall.getPatch(22, 87), kiosk3, null, 1.27, mall.getPatch(22, 87).getPatchCenterCoordinates(), -1, kiosk3.getTeam()));
        mall.getAgentPatchSet().add(kiosk3.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk4 = mall.getAgents().get(4);
        kiosk4.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 53), kiosk4, null, 1.27, mall.getPatch(33, 53).getPatchCenterCoordinates(), -1, kiosk4.getTeam()));
        mall.getAgentPatchSet().add(kiosk4.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk5 = mall.getAgents().get(5);
        kiosk5.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 70), kiosk5, null, 1.27, mall.getPatch(33, 70).getPatchCenterCoordinates(), -1, kiosk5.getTeam()));
        mall.getAgentPatchSet().add(kiosk5.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk6 = mall.getAgents().get(6);
        kiosk6.setAgentMovement(new MallAgentMovement(mall.getPatch(33, 87), kiosk6, null, 1.27, mall.getPatch(33, 87).getPatchCenterCoordinates(), -1, kiosk6.getTeam()));
        mall.getAgentPatchSet().add(kiosk6.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;
        MallAgent kiosk7 = mall.getAgents().get(7);
        kiosk7.setAgentMovement(new MallAgentMovement(mall.getPatch(27, 97), kiosk7, null, 1.27, mall.getPatch(27, 97).getPatchCenterCoordinates(), -1, kiosk7.getTeam()));
        mall.getAgentPatchSet().add(kiosk7.getAgentMovement().getCurrentPatch());
        MallAgent.staffKioskCount++;
        MallAgent.agentCount++;

        MallAgent resto1 = mall.getAgents().get(8);
        resto1.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 52), resto1, null, 1.27, mall.getPatch(59, 52).getPatchCenterCoordinates(), -1, resto1.getTeam()));
        mall.getAgentPatchSet().add(resto1.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto2 = mall.getAgents().get(9);
        resto2.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 58), resto2, null, 1.27, mall.getPatch(59, 58).getPatchCenterCoordinates(), -1, resto2.getTeam()));
        mall.getAgentPatchSet().add(resto2.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto3 = mall.getAgents().get(10);
        resto3.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 64), resto3, null, 1.27, mall.getPatch(59, 64).getPatchCenterCoordinates(), -1, resto3.getTeam()));
        mall.getAgentPatchSet().add(resto3.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto4 = mall.getAgents().get(11);
        resto4.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 70), resto4, null, 1.27, mall.getPatch(59, 70).getPatchCenterCoordinates(), -1, resto4.getTeam()));
        mall.getAgentPatchSet().add(resto4.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto5 = mall.getAgents().get(12);
        resto5.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 74), resto5, null, 1.27, mall.getPatch(59, 74).getPatchCenterCoordinates(), -1, resto5.getTeam()));
        mall.getAgentPatchSet().add(resto5.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto6 = mall.getAgents().get(13);
        resto6.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 80), resto6, null, 1.27, mall.getPatch(59, 80).getPatchCenterCoordinates(), -1, resto6.getTeam()));
        mall.getAgentPatchSet().add(resto6.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto7 = mall.getAgents().get(14);
        resto7.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 86), resto7, null, 1.27, mall.getPatch(59, 86).getPatchCenterCoordinates(), -1, resto7.getTeam()));
        mall.getAgentPatchSet().add(resto7.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;
        MallAgent resto8 = mall.getAgents().get(15);
        resto8.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 92), resto8, null, 1.27, mall.getPatch(59, 92).getPatchCenterCoordinates(), -1, resto8.getTeam()));
        mall.getAgentPatchSet().add(resto8.getAgentMovement().getCurrentPatch());
        MallAgent.staffRestoCount++;
        MallAgent.agentCount++;

        MallAgent cashier1 = mall.getAgents().get(16);
        cashier1.setAgentMovement(new MallAgentMovement(mall.getPatch(10, 19), cashier1, null, 1.27, mall.getPatch(10, 19).getPatchCenterCoordinates(), -1, cashier1.getTeam()));
        mall.getAgentPatchSet().add(cashier1.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier2 = mall.getAgents().get(17);
        cashier2.setAgentMovement(new MallAgentMovement(mall.getPatch(5, 41), cashier2, null, 1.27, mall.getPatch(5, 41).getPatchCenterCoordinates(), -1, cashier2.getTeam()));
        mall.getAgentPatchSet().add(cashier2.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier3 = mall.getAgents().get(18);
        cashier3.setAgentMovement(new MallAgentMovement(mall.getPatch(49, 19), cashier3, null, 1.27, mall.getPatch(49, 19).getPatchCenterCoordinates(), -1, cashier3.getTeam()));
        mall.getAgentPatchSet().add(cashier3.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier4 = mall.getAgents().get(19);
        cashier4.setAgentMovement(new MallAgentMovement(mall.getPatch(54, 41), cashier4, null, 1.27, mall.getPatch(54, 41).getPatchCenterCoordinates(), -1, cashier4.getTeam()));
        mall.getAgentPatchSet().add(cashier4.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier5 = mall.getAgents().get(20);
        cashier5.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 55), cashier5, null, 1.27, mall.getPatch(0, 55).getPatchCenterCoordinates(), -1, cashier5.getTeam()));
        mall.getAgentPatchSet().add(cashier5.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier6 = mall.getAgents().get(21);
        cashier6.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 66), cashier6, null, 1.27, mall.getPatch(0, 66).getPatchCenterCoordinates(), -1, cashier6.getTeam()));
        mall.getAgentPatchSet().add(cashier6.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier7 = mall.getAgents().get(22);
        cashier7.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 84), cashier7, null, 1.27, mall.getPatch(0, 84).getPatchCenterCoordinates(), -1, cashier7.getTeam()));
        mall.getAgentPatchSet().add(cashier7.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier8 = mall.getAgents().get(23);
        cashier8.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 102), cashier8, null, 1.27, mall.getPatch(0, 102).getPatchCenterCoordinates(), -1, cashier8.getTeam()));
        mall.getAgentPatchSet().add(cashier8.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier9 = mall.getAgents().get(24);
        cashier9.setAgentMovement(new MallAgentMovement(mall.getPatch(0, 113), cashier9, null, 1.27, mall.getPatch(0, 113).getPatchCenterCoordinates(), -1, cashier9.getTeam()));
        mall.getAgentPatchSet().add(cashier9.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier10 = mall.getAgents().get(25);
        cashier10.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 100), cashier10, null, 1.27, mall.getPatch(59, 100).getPatchCenterCoordinates(), -1, cashier10.getTeam()));
        mall.getAgentPatchSet().add(cashier10.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreCashierCount++;
        MallAgent.agentCount++;
        MallAgent cashier11 = mall.getAgents().get(26);
        cashier11.setAgentMovement(new MallAgentMovement(mall.getPatch(59, 112), cashier11, null, 1.27, mall.getPatch(59, 112).getPatchCenterCoordinates(), -1, cashier11.getTeam()));
        mall.getAgentPatchSet().add(cashier11.getAgentMovement().getCurrentPatch());
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
        MallAgent sales15 = mall.getAgents().get(35);
        sales15.setAgentMovement(new MallAgentMovement(mall.getPatch(4, 110), sales15, null, 1.27, mall.getPatch(4, 110).getPatchCenterCoordinates(), -1, sales15.getTeam()));
        mall.getAgentPatchSet().add(sales15.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales17 = mall.getAgents().get(36);
        sales17.setAgentMovement(new MallAgentMovement(mall.getPatch(56, 97), sales17, null, 1.27, mall.getPatch(56, 97).getPatchCenterCoordinates(), -1, sales17.getTeam()));
        mall.getAgentPatchSet().add(sales17.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
        MallAgent sales19 = mall.getAgents().get(37);
        sales19.setAgentMovement(new MallAgentMovement(mall.getPatch(56, 109), sales19, null, 1.27, mall.getPatch(56, 109).getPatchCenterCoordinates(), -1, sales19.getTeam()));
        mall.getAgentPatchSet().add(sales19.getAgentMovement().getCurrentPatch());
        MallAgent.staffStoreSalesCount++;
        MallAgent.agentCount++;
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
                            updateAgentsInMall(mall);
                            spawnAgent(mall, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization
                        // If the refreshes are frequent enough, update the visualization in a speed-aware manner
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

    public static void updateAgentsInMall(Mall mall) throws InterruptedException { // Manage all agent-related updates
        moveAll(mall);
    }

    private static void moveAll(Mall mall) { // Make all agents move for one tick
        for (MallAgent agent : mall.getMovableAgents()) {
            try {
                moveOne(agent);
                agent.getAgentGraphic().change();
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
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.mallSimulator.getMall().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getSecurities().get(0));
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
                        if (action.getName() == MallAction.Name.FIND_BENCH || action.getName() == MallAction.Name.FIND_DIRECTORY) {
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
                        else if (action.getName() == MallAction.Name.SIT_ON_BENCH || action.getName() == MallAction.Name.VIEW_DIRECTORY) {
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
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
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
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                agentMovement.resetGoal();
                            }
                        }
                        else if (action.getName() == MallAction.Name.WASH_IN_SINK) {
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
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
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
                            if (agentMovement.getGoalAmenity() != null) {
                                if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                    agentMovement.forceStationedInteraction(MallAgent.Persona.STAFF_RESTO);
                                }

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
                                }
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_TO_DINING) {
                        if (action.getName() == MallAction.Name.GO_TO_KIOSK) {
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.mallSimulator.getMall().getKioskFields().get(6));
                                agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getKiosks().get(6));
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
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseRandomTablePatron("DINING");
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
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
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName() == MallState.Name.GOING_TO_STORE) {
                        agentMovement.getRoutePlan().MAX_STORE_HELP = 1;
                        if (action.getName() == MallAction.Name.GO_TO_STORE) {
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
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
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
                                        agentMovement.reachPatchInPath(); // The passenger has reached the next patch in the path, so remove this from this passenger's current path
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
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.mallSimulator.getMall().getMallGates().get(0));
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
                    MallAgent mallAgent = (MallAgent) otherAgent;
                    if (!mallAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), mallAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(mallAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), mallAgent.getAgentMovement().getProposedHeading(), mallAgent.getAgentMovement().getFieldOfViewAngle()))
                                agentMovement.rollAgentInteraction(mallAgent);
                    if (agentMovement.isInteracting())
                        break;
                }
                if (agentMovement.isInteracting())
                    break;
            }
            patches = agentMovement.get3x3Field(agentMovement.getHeading(), true, Math.toRadians(270));
            for (Patch patch: patches){
                for (Agent otherAgent: patch.getAgents()){
                    MallAgent mallAgent = (MallAgent) otherAgent;
                    if (!mallAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), mallAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
                            if (Coordinates.isWithinFieldOfView(mallAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), mallAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
                                agentMovement.rollAgentInteraction(mallAgent);
                                if (agentMovement.isInteracting()){ // interaction was successful
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
        }
    }

    private void spawnAgent(Mall mall, long currentTick) {
        MallGate gate = mall.getMallGates().get(1);
        MallAgent agent1 = null;
        MallAgent agent2 = null;
        MallAgent agent3 = null;
        MallAgent agent4 = null;

        Gate.GateBlock spawner1 = gate.getSpawners().get(0);
        Gate.GateBlock spawner2 = gate.getSpawners().get(1);
        Gate.GateBlock spawner3 = gate.getSpawners().get(2);
        Gate.GateBlock spawner4 = gate.getSpawners().get(3);

        int spawnChance = (int) gate.getChancePerTick();
        int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        int type = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5);

        if (CHANCE > spawnChance) {
            if (type == 0 && totalFamilyCount < MAX_FAMILY) {
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
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent2.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent2, leaderAgent, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick, agent2.getTeam()));
                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent3.setAgentMovement(new MallAgentMovement(spawner3.getPatch(), agent3, leaderAgent, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick, agent3.getTeam()));
                    mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent4.setAgentMovement(new MallAgentMovement(spawner4.getPatch(), agent4, leaderAgent, 1.27, spawner4.getPatch().getPatchCenterCoordinates(), currentTick, agent4.getTeam()));
                    mall.getAgentPatchSet().add(agent4.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    leaderAgent.getAgentMovement().getFollowers().add(agent2);
                    leaderAgent.getAgentMovement().getFollowers().add(agent3);
                    leaderAgent.getAgentMovement().getFollowers().add(agent4);

                    totalFamilyCount++;
                }
            }
            else if (type == 1 && totalFriendsCount < MAX_FRIENDS) {
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
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent2.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent2, leaderAgent, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick, agent2.getTeam()));
                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent3.setAgentMovement(new MallAgentMovement(spawner3.getPatch(), agent3, leaderAgent, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick, agent3.getTeam()));
                    mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    leaderAgent.getAgentMovement().getFollowers().add(agent2);
                    leaderAgent.getAgentMovement().getFollowers().add(agent3);

                    totalFriendsCount++;
                }

            }
            else if (type == 2 && totalCoupleCount < MAX_COUPLE) {
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
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    agent2.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent2, leaderAgent, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick, agent2.getTeam()));
                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    leaderAgent.getAgentMovement().getFollowers().add(agent2);

                    totalCoupleCount++;
                }
            }
            else if (type == 3 && totalAloneCount < MAX_ALONE) {
                if (mall.getUnspawnedAloneAgents().size() > 0){
                    MallAgent leaderAgent = mall.getUnspawnedAloneAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedAloneAgents().size()));
                    leaderAgent.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), leaderAgent, null, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick, leaderAgent.getTeam()));
                    mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());
                    MallAgent.patronCount++;
                    MallAgent.agentCount++;

                    totalAloneCount++;
                }
            }
        }
    }

}