package com.socialsim.model.simulator.mall;

import com.socialsim.controller.Main;
import com.socialsim.controller.mall.controls.MallScreenController;
import com.socialsim.model.core.agent.mall.MallAction;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.agent.mall.MallAgentMovement;
import com.socialsim.model.core.agent.mall.MallState;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MallSimulator extends Simulator {

    private Mall mall;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    public static int MAX_FAMILY = 1;
    public static int MAX_FRIENDS = 1;
    public static int MAX_COUPLE = 1;
    public static int MAX_ALONE = 1;

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
    }

    public void spawnInitialAgents(Mall mall) {
        MallAgent guard = MallAgent.MallAgentFactory.create(MallAgent.Type.GUARD, MallAgent.Persona.GUARD, null, null, mall.getPatch(33, 2), true, null, -1, 0);
        mall.getAgents().add(guard);
        mall.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());

        MallAgent kiosk1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(22, 53), true, null, -1, 0);
        mall.getAgents().add(kiosk1);
        mall.getAgentPatchSet().add(kiosk1.getAgentMovement().getCurrentPatch());
        MallAgent kiosk2 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(22, 70), true, null, -1, 0);
        mall.getAgents().add(kiosk2);
        mall.getAgentPatchSet().add(kiosk2.getAgentMovement().getCurrentPatch());
        MallAgent kiosk3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(22, 87), true, null, -1, 0);
        mall.getAgents().add(kiosk3);
        mall.getAgentPatchSet().add(kiosk3.getAgentMovement().getCurrentPatch());
        MallAgent kiosk4 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(33, 53), true, null, -1, 0);
        mall.getAgents().add(kiosk4);
        mall.getAgentPatchSet().add(kiosk4.getAgentMovement().getCurrentPatch());
        MallAgent kiosk5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(33, 70), true, null, -1, 0);
        mall.getAgents().add(kiosk5);
        mall.getAgentPatchSet().add(kiosk5.getAgentMovement().getCurrentPatch());
        MallAgent kiosk6 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(33, 87), true, null, -1, 0);
        mall.getAgents().add(kiosk6);
        mall.getAgentPatchSet().add(kiosk6.getAgentMovement().getCurrentPatch());
        MallAgent kiosk7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_KIOSK, MallAgent.Persona.STAFF_KIOSK, null, null, mall.getPatch(27, 97), true, null, -1, 0);
        mall.getAgents().add(kiosk7);
        mall.getAgentPatchSet().add(kiosk7.getAgentMovement().getCurrentPatch());

        MallAgent resto1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 52), true, null, -1, 1);
        mall.getAgents().add(resto1);
        mall.getAgentPatchSet().add(resto1.getAgentMovement().getCurrentPatch());
        MallAgent resto2 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 58), true, null, -1, 1);
        mall.getAgents().add(resto2);
        mall.getAgentPatchSet().add(resto2.getAgentMovement().getCurrentPatch());
        MallAgent resto3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 64), true, null, -1, 1);
        mall.getAgents().add(resto3);
        mall.getAgentPatchSet().add(resto3.getAgentMovement().getCurrentPatch());
        MallAgent resto4 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 70), true, null, -1, 1);
        mall.getAgents().add(resto4);
        mall.getAgentPatchSet().add(resto4.getAgentMovement().getCurrentPatch());
        MallAgent resto5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 74), true, null, -1, 2);
        mall.getAgents().add(resto5);
        mall.getAgentPatchSet().add(resto5.getAgentMovement().getCurrentPatch());
        MallAgent resto6 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 80), true, null, -1, 2);
        mall.getAgents().add(resto6);
        mall.getAgentPatchSet().add(resto6.getAgentMovement().getCurrentPatch());
        MallAgent resto7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 86), true, null, -1, 2);
        mall.getAgents().add(resto7);
        mall.getAgentPatchSet().add(resto7.getAgentMovement().getCurrentPatch());
        MallAgent resto8 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_RESTO, MallAgent.Persona.STAFF_RESTO, null, null, mall.getPatch(59, 92), true, null, -1, 2);
        mall.getAgents().add(resto8);
        mall.getAgentPatchSet().add(resto8.getAgentMovement().getCurrentPatch());

        MallAgent cashier1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(10, 19), true, null, -1, 1);
        mall.getAgents().add(cashier1);
        mall.getAgentPatchSet().add(cashier1.getAgentMovement().getCurrentPatch());
        MallAgent cashier2 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(5, 41), true, null, -1, 2);
        mall.getAgents().add(cashier2);
        mall.getAgentPatchSet().add(cashier2.getAgentMovement().getCurrentPatch());
        MallAgent cashier3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(49, 19), true, null, -1, 3);
        mall.getAgents().add(cashier3);
        mall.getAgentPatchSet().add(cashier3.getAgentMovement().getCurrentPatch());
        MallAgent cashier4 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(54, 41), true, null, -1, 4);
        mall.getAgents().add(cashier4);
        mall.getAgentPatchSet().add(cashier4.getAgentMovement().getCurrentPatch());
        MallAgent cashier5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(0, 55), true, null, -1, 5);
        mall.getAgents().add(cashier5);
        mall.getAgentPatchSet().add(cashier5.getAgentMovement().getCurrentPatch());
        MallAgent cashier6 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(0, 66), true, null, -1, 6);
        mall.getAgents().add(cashier6);
        mall.getAgentPatchSet().add(cashier6.getAgentMovement().getCurrentPatch());
        MallAgent cashier7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(0, 84), true, null, -1, 7);
        mall.getAgents().add(cashier7);
        mall.getAgentPatchSet().add(cashier7.getAgentMovement().getCurrentPatch());
        MallAgent cashier8 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(0, 102), true, null, -1, 8);
        mall.getAgents().add(cashier8);
        mall.getAgentPatchSet().add(cashier8.getAgentMovement().getCurrentPatch());
        MallAgent cashier9 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(0, 113), true, null, -1, 9);
        mall.getAgents().add(cashier9);
        mall.getAgentPatchSet().add(cashier9.getAgentMovement().getCurrentPatch());
        MallAgent cashier10 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(59, 100), true, null, -1, 10);
        mall.getAgents().add(cashier10);
        mall.getAgentPatchSet().add(cashier10.getAgentMovement().getCurrentPatch());
        MallAgent cashier11 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_CASHIER, MallAgent.Persona.STAFF_STORE_CASHIER, null, null, mall.getPatch(59, 112), true, null, -1, 11);
        mall.getAgents().add(cashier11);
        mall.getAgentPatchSet().add(cashier11.getAgentMovement().getCurrentPatch());

        MallAgent sales = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(14, 14), true, null, -1, 1);
        mall.getAgents().add(sales);
        mall.getAgentPatchSet().add(sales.getAgentMovement().getCurrentPatch());

        MallAgent sales1 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(7, 36), true, null, -1, 2);
        mall.getAgents().add(sales1);
        mall.getAgentPatchSet().add(sales1.getAgentMovement().getCurrentPatch());

        MallAgent sales3 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(46, 14), true, null, -1, 3);
        mall.getAgents().add(sales3);
        mall.getAgentPatchSet().add(sales3.getAgentMovement().getCurrentPatch());

        MallAgent sales5 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(51, 36), true, null, -1, 4);
        mall.getAgents().add(sales5);
        mall.getAgentPatchSet().add(sales5.getAgentMovement().getCurrentPatch());

        MallAgent sales7 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(4, 52), true, null, -1, 5);
        mall.getAgents().add(sales7);
        mall.getAgentPatchSet().add(sales7.getAgentMovement().getCurrentPatch());

        MallAgent sales9 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(4, 63), true, null, -1, 6);
        mall.getAgents().add(sales9);
        mall.getAgentPatchSet().add(sales9.getAgentMovement().getCurrentPatch());

        MallAgent sales11 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(4, 81), true, null, -1, 7);
        mall.getAgents().add(sales11);
        mall.getAgentPatchSet().add(sales11.getAgentMovement().getCurrentPatch());

        MallAgent sales13 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(4, 99), true, null, -1, 8);
        mall.getAgents().add(sales13);
        mall.getAgentPatchSet().add(sales13.getAgentMovement().getCurrentPatch());

        MallAgent sales15 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(4, 110), true, null, -1, 9);
        mall.getAgents().add(sales15);
        mall.getAgentPatchSet().add(sales15.getAgentMovement().getCurrentPatch());

        MallAgent sales17 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(56, 97), true, null, -1, 10);
        mall.getAgents().add(sales17);
        mall.getAgentPatchSet().add(sales17.getAgentMovement().getCurrentPatch());

        MallAgent sales19 = MallAgent.MallAgentFactory.create(MallAgent.Type.STAFF_STORE_SALES, MallAgent.Persona.STAFF_STORE_SALES, null, null, mall.getPatch(56, 109), true, null, -1, 11);
        mall.getAgents().add(sales19);
        mall.getAgentPatchSet().add(sales19.getAgentMovement().getCurrentPatch());
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
        for (MallAgent agent : mall.getAgents()) {
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

    private void spawnAgent(Mall mall, long currentTick) {
        MallGate gate = mall.getMallGates().get(1);

        Gate.GateBlock spawner1 = gate.getSpawners().get(0);
        Gate.GateBlock spawner2 = gate.getSpawners().get(1);
        Gate.GateBlock spawner3 = gate.getSpawners().get(2);
        Gate.GateBlock spawner4 = gate.getSpawners().get(3);

        int spawnChance = (int) gate.getChancePerTick();
        int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        int type = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(5);
        boolean isErrand = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

        if (CHANCE > spawnChance) {
            if (type == 0 && MAX_FAMILY > 0) {
                MallAgent.Persona thisType = null;
                if (isErrand) {
                    thisType = MallAgent.Persona.ERRAND_FAMILY;
                }
                else {
                    thisType = MallAgent.Persona.LOITER_FAMILY;
                }

                MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.Gender gender2 = null;
                if (gender1 == MallAgent.Gender.MALE) {
                    gender2 = MallAgent.Gender.FEMALE;
                }
                else {
                    gender2 = MallAgent.Gender.MALE;
                }
                MallAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.Gender gender4 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;

                MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, MallAgent.AgeGroup.FROM_25_TO_54, spawner1.getPatch(), true, null, (int) currentTick, 0);
                mall.getAgents().add(leaderAgent);
                mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());

                MallAgent agent2 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender2, MallAgent.AgeGroup.FROM_25_TO_54, spawner2.getPatch(), true, leaderAgent, (int) currentTick, 0);
                mall.getAgents().add(agent2);
                mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());

                MallAgent agent3 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender3, MallAgent.AgeGroup.FROM_15_TO_24, spawner3.getPatch(), true, leaderAgent, (int) currentTick, 0);
                mall.getAgents().add(agent3);
                mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());

                MallAgent agent4 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender4, MallAgent.AgeGroup.FROM_15_TO_24, spawner3.getPatch(), true, leaderAgent, (int) currentTick, 0);
                mall.getAgents().add(agent4);
                mall.getAgentPatchSet().add(agent4.getAgentMovement().getCurrentPatch());

                leaderAgent.getAgentMovement().getFollowers().add(agent2);
                leaderAgent.getAgentMovement().getFollowers().add(agent3);
                leaderAgent.getAgentMovement().getFollowers().add(agent4);

                MAX_FAMILY -= 1;
            }
            else if (type == 1 && MAX_FRIENDS > 0) {
                MallAgent.Persona thisType = null;
                if (isErrand) {
                    thisType = MallAgent.Persona.ERRAND_FRIENDS;
                }
                else {
                    thisType = MallAgent.Persona.LOITER_FRIENDS;
                }

                MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.Gender gender3 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;

                MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, MallAgent.AgeGroup.FROM_15_TO_24, spawner1.getPatch(), true, null, (int) currentTick, 0);
                mall.getAgents().add(leaderAgent);
                mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());

                MallAgent agent2 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender2, MallAgent.AgeGroup.FROM_15_TO_24, spawner2.getPatch(), true, leaderAgent, (int) currentTick, 0);
                mall.getAgents().add(agent2);
                mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());

                MallAgent agent3 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender3, MallAgent.AgeGroup.FROM_15_TO_24, spawner3.getPatch(), true, leaderAgent, (int) currentTick, 0);
                mall.getAgents().add(agent3);
                mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());

                leaderAgent.getAgentMovement().getFollowers().add(agent2);
                leaderAgent.getAgentMovement().getFollowers().add(agent3);

                MAX_FRIENDS -= 1;
            }
            else if (type == 2 && MAX_COUPLE > 0) {
                MallAgent.Persona thisType = null;
                if (isErrand) {
                    thisType = MallAgent.Persona.ERRAND_COUPLE;
                }
                else {
                    thisType = MallAgent.Persona.LOITER_COUPLE;
                }

                MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.Gender gender2 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.AgeGroup age1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_15_TO_24 : MallAgent.AgeGroup.FROM_25_TO_54;

                MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, age1, spawner1.getPatch(), true, null, (int) currentTick, 0);
                mall.getAgents().add(leaderAgent);
                mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());

                MallAgent agent2 = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender2, age1, spawner2.getPatch(), true, leaderAgent, (int) currentTick, 0);
                mall.getAgents().add(agent2);
                mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());

                leaderAgent.getAgentMovement().getFollowers().add(agent2);

                MAX_COUPLE -= 1;
            }
            else if (type == 3 && MAX_ALONE > 0) {
                MallAgent.Persona thisType = null;
                if (isErrand) {
                    thisType = MallAgent.Persona.ERRAND_ALONE;
                }
                else {
                    thisType = MallAgent.Persona.LOITER_ALONE;
                }

                MallAgent.Gender gender1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.MALE : MallAgent.Gender.FEMALE;
                MallAgent.AgeGroup age1 = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_15_TO_24 : MallAgent.AgeGroup.FROM_25_TO_54;

                MallAgent leaderAgent = MallAgent.MallAgentFactory.create(MallAgent.Type.PATRON, thisType, gender1, age1, spawner1.getPatch(), true, null, (int) currentTick, 0);
                mall.getAgents().add(leaderAgent);
                mall.getAgentPatchSet().add(leaderAgent.getAgentMovement().getCurrentPatch());

                MAX_ALONE -= 1;
            }
        }
    }

}