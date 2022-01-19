package com.socialsim.model.simulator.mall;

import com.socialsim.controller.Main;
import com.socialsim.controller.mall.controls.MallScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.mall.MallAction;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.agent.mall.MallAgentMovement;
import com.socialsim.model.core.agent.mall.MallState;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MallSimulator extends Simulator {

    private Mall mall;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    public static final int MAX_FAMILY = 20;
    public static final int MAX_FRIENDS = 20;
    public static final int MAX_COUPLE = 20;
    public static final int MAX_ALONE = 20;

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
//        mall.createInitialAgentDemographics(MAX_FAMILY, MAX_ALONE);
//        MallAgent guard1 = mall.getAgents().get(0);
//        guard1.setAgentMovement(new MallAgentMovement(mall.getPatch(57,52), guard1, null, 1.27, mall.getPatch(57,52).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(guard1.getAgentMovement().getCurrentPatch());
//        MallAgent.guardCount++;
//        MallAgent.agentCount++;
//        MallAgent guard2 = mall.getAgents().get(1);
//        guard2.setAgentMovement(new MallAgentMovement(mall.getPatch(57,47), guard2, null, 1.27, mall.getPatch(57,47).getPatchCenterCoordinates(), -1));
//        mall.getAgents().add(guard2);
//        mall.getAgentPatchSet().add(guard2.getAgentMovement().getCurrentPatch());
//        MallAgent.guardCount++;
//        MallAgent.agentCount++;
//
//        MallAgent cashier1 = mall.getAgents().get(2);
//        cashier1.setAgentMovement(new MallAgentMovement(mall.getPatch(44,20), cashier1, null, 1.27, mall.getPatch(44,20).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier1.getAgentMovement().getCurrentPatch());
//        MallAgent.guardCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier2 = mall.getAgents().get(3);
//        cashier2.setAgentMovement(new MallAgentMovement(mall.getPatch(44,26), cashier2, null, 1.27, mall.getPatch(44,26).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier2.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier3 = mall.getAgents().get(4);
//        cashier3.setAgentMovement(new MallAgentMovement(mall.getPatch(44,32), cashier3, null, 1.27, mall.getPatch(44,32).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier3.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier4 = mall.getAgents().get(5);
//        cashier4.setAgentMovement(new MallAgentMovement(mall.getPatch(44,38), cashier4, null, 1.27, mall.getPatch(44,38).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier4.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier5 = mall.getAgents().get(6);
//        cashier5.setAgentMovement(new MallAgentMovement(mall.getPatch(44,44), cashier5, null, 1.27, mall.getPatch(44,44).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier5.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier6 = mall.getAgents().get(7);
//        cashier6.setAgentMovement(new MallAgentMovement(mall.getPatch(44,50), cashier6, null, 1.27, mall.getPatch(44,50).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier6.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier7 = mall.getAgents().get(8);
//        cashier7.setAgentMovement(new MallAgentMovement(mall.getPatch(44,56), cashier7, null, 1.27, mall.getPatch(44,56).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier7.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//        MallAgent cashier8 = mall.getAgents().get(9);
//        cashier8.setAgentMovement(new MallAgentMovement(mall.getPatch(44,62), cashier8, null, 1.27, mall.getPatch(44,62).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(cashier8.getAgentMovement().getCurrentPatch());
//        MallAgent.cashierCount++;
//        MallAgent.agentCount++;
//
//        MallAgent bagger1 = mall.getAgents().get(10);
//        bagger1.setAgentMovement(new MallAgentMovement(mall.getPatch(45,20), bagger1, null, 1.27, mall.getPatch(45,20).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger1.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger2 = mall.getAgents().get(11);
//        bagger2.setAgentMovement(new MallAgentMovement(mall.getPatch(45,26), bagger2, null, 1.27, mall.getPatch(45,26).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger2.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger3 = mall.getAgents().get(12);
//        bagger3.setAgentMovement(new MallAgentMovement(mall.getPatch(45,32), bagger3, null, 1.27, mall.getPatch(45,32).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger3.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger4 = mall.getAgents().get(13);
//        bagger4.setAgentMovement(new MallAgentMovement(mall.getPatch(45,38), bagger4, null, 1.27, mall.getPatch(45,38).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger4.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger5 = mall.getAgents().get(14);
//        bagger5.setAgentMovement(new MallAgentMovement(mall.getPatch(45,44), bagger5, null, 1.27, mall.getPatch(45,44).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger5.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger6 = mall.getAgents().get(15);
//        bagger6.setAgentMovement(new MallAgentMovement(mall.getPatch(45,50), bagger6, null, 1.27, mall.getPatch(45,50).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger6.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger7 = mall.getAgents().get(16);
//        bagger7.setAgentMovement(new MallAgentMovement(mall.getPatch(45,56), bagger7, null, 1.27, mall.getPatch(45,56).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger7.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//        MallAgent bagger8 = mall.getAgents().get(17);
//        bagger8.setAgentMovement(new MallAgentMovement(mall.getPatch(45,62), bagger8, null, 1.27, mall.getPatch(45,62).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(bagger8.getAgentMovement().getCurrentPatch());
//        MallAgent.baggerCount++;
//        MallAgent.agentCount++;
//
//        MallAgent service1 = mall.getAgents().get(18);
//        service1.setAgentMovement(new MallAgentMovement(mall.getPatch(44,4), service1, null, 1.27, mall.getPatch(44,4).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(service1.getAgentMovement().getCurrentPatch());
//        MallAgent.customerServiceCount++;
//        MallAgent.agentCount++;
//        MallAgent service2 = mall.getAgents().get(19);
//        service2.setAgentMovement(new MallAgentMovement(mall.getPatch(44,8), service2, null, 1.27, mall.getPatch(44,8).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(service2.getAgentMovement().getCurrentPatch());
//        MallAgent.customerServiceCount++;
//        MallAgent.agentCount++;
//        MallAgent service3 = mall.getAgents().get(20);
//        service3.setAgentMovement(new MallAgentMovement(mall.getPatch(44,12), service3, null, 1.27, mall.getPatch(44,12).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(service3.getAgentMovement().getCurrentPatch());
//        MallAgent.customerServiceCount++;
//        MallAgent.agentCount++;
//
//        MallAgent food1 = mall.getAgents().get(21);
//        food1.setAgentMovement(new MallAgentMovement(mall.getPatch(58,8), food1, null, 1.27, mall.getPatch(58,8).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food1.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food2 = mall.getAgents().get(22);
//        food2.setAgentMovement(new MallAgentMovement(mall.getPatch(58,17), food2, null, 1.27, mall.getPatch(58,17).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food2.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food3 = mall.getAgents().get(23);
//        food3.setAgentMovement(new MallAgentMovement(mall.getPatch(58,26), food3, null, 1.27, mall.getPatch(58,26).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food3.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food4 = mall.getAgents().get(24);
//        food4.setAgentMovement(new MallAgentMovement(mall.getPatch(58,35), food4, null, 1.27, mall.getPatch(58,35).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food4.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food5 = mall.getAgents().get(25);
//        food5.setAgentMovement(new MallAgentMovement(mall.getPatch(58,44), food5, null, 1.27, mall.getPatch(58,44).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food5.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food6 = mall.getAgents().get(26);
//        food6.setAgentMovement(new MallAgentMovement(mall.getPatch(58,53), food6, null, 1.27, mall.getPatch(58,53).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food6.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food7 = mall.getAgents().get(27);
//        food7.setAgentMovement(new MallAgentMovement(mall.getPatch(58,62), food7, null, 1.27, mall.getPatch(58,62).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food7.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//        MallAgent food8 = mall.getAgents().get(28);
//        food8.setAgentMovement(new MallAgentMovement(mall.getPatch(58,71), food8, null, 1.27, mall.getPatch(58,71).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(food8.getAgentMovement().getCurrentPatch());
//        MallAgent.staffFoodCount++;
//        MallAgent.agentCount++;
//
//        MallAgent butcher1 = mall.getAgents().get(29);
//        butcher1.setAgentMovement(new MallAgentMovement(mall.getPatch(29,1), butcher1, null, 1.27, mall.getPatch(29,1).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(butcher1.getAgentMovement().getCurrentPatch());
//        MallAgent.butcherCount++;
//        MallAgent.agentCount++;
//        MallAgent butcher2 = mall.getAgents().get(30);
//        butcher2.setAgentMovement(new MallAgentMovement(mall.getPatch(37,1), butcher2, null, 1.27, mall.getPatch(37,1).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(butcher2.getAgentMovement().getCurrentPatch());
//        MallAgent.butcherCount++;
//        MallAgent.agentCount++;
//
//        MallAgent aisle1 = mall.getAgents().get(31);
//        aisle1.setAgentMovement(new MallAgentMovement(mall.getPatch(3,15), aisle1, null, 1.27, mall.getPatch(3,15).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle1.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle2 = mall.getAgents().get(32);
//        aisle2.setAgentMovement(new MallAgentMovement(mall.getPatch(3,42), aisle2, null, 1.27, mall.getPatch(3,42).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle2.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle3 = mall.getAgents().get(33);
//        aisle3.setAgentMovement(new MallAgentMovement(mall.getPatch(3,69), aisle3, null, 1.27, mall.getPatch(3,69).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle3.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle4 = mall.getAgents().get(34);
//        aisle4.setAgentMovement(new MallAgentMovement(mall.getPatch(18,95), aisle4, null, 1.27, mall.getPatch(18,95).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle4.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle5 = mall.getAgents().get(35);
//        aisle5.setAgentMovement(new MallAgentMovement(mall.getPatch(44,95), aisle5, null, 1.27, mall.getPatch(44,95).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle5.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle6 = mall.getAgents().get(36);
//        aisle6.setAgentMovement(new MallAgentMovement(mall.getPatch(10,4), aisle6, null, 1.27, mall.getPatch(10,4).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle6.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle7 = mall.getAgents().get(37);
//        aisle7.setAgentMovement(new MallAgentMovement(mall.getPatch(12,29), aisle7, null, 1.27, mall.getPatch(12,29).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle7.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle8 = mall.getAgents().get(38);
//        aisle8.setAgentMovement(new MallAgentMovement(mall.getPatch(18,29), aisle8, null, 1.27, mall.getPatch(18,29).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle8.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
//        MallAgent aisle9 = mall.getAgents().get(39);
//        aisle9.setAgentMovement(new MallAgentMovement(mall.getPatch(24,29), aisle9, null, 1.27, mall.getPatch(24,29).getPatchCenterCoordinates(), -1));
//        mall.getAgentPatchSet().add(aisle9.getAgentMovement().getCurrentPatch());
//        MallAgent.staffAisleCount++;
//        MallAgent.agentCount++;
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
    }

    private void spawnAgent(Mall mall, long currentTick) {
//        MallGate gate = mall.getMallGates().get(1);
//        MallAgent agent1 = null;
//        MallAgent agent2 = null;
//        MallAgent agent3 = null;
//        MallAgent agent4 = null;
//
//        Gate.GateBlock spawner1 = gate.getSpawners().get(0);
//        Gate.GateBlock spawner2 = gate.getSpawners().get(1);
//        Gate.GateBlock spawner3 = gate.getSpawners().get(2);
//        Gate.GateBlock spawner4 = gate.getSpawners().get(3);
//
//        int spawnChance = (int) gate.getChancePerTick();
//        int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
//        boolean isFamily = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//
//        if (CHANCE > spawnChance) {
//            if (isFamily && totalAloneCustomerCount < MAX_FAMILY && currentFamilyCount < MAX_CURRENT_FAMILY) {
//                MallAgent leaderAgent = mall.getUnspawnedFamilyAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedFamilyAgents().size()));
//
//                if (leaderAgent.getPersona() == MallAgent.Persona.COMPLETE_FAMILY_CUSTOMER) { // Complete Family
//                    agent1 = leaderAgent;
//                    agent1.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent1, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent2 = mall.getAgents().get(mall.getAgents().indexOf(leaderAgent) + 1);
//                    agent2.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), agent2, agent1, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent3 = mall.getAgents().get(mall.getAgents().indexOf(leaderAgent) + 2);
//                    agent3.setAgentMovement(new MallAgentMovement(spawner3.getPatch(), agent3, agent1, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent4 = mall.getAgents().get(mall.getAgents().indexOf(leaderAgent) + 3);
//                    agent4.setAgentMovement(new MallAgentMovement(spawner4.getPatch(), agent4, agent1, 1.27, spawner4.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent4.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent1.getAgentMovement().getFollowers().add(agent2);
//                    agent1.getAgentMovement().getFollowers().add(agent3);
//                    agent1.getAgentMovement().getFollowers().add(agent4);
//                    agent1.getAgentMovement().setNextState();
//                }
//                else if (leaderAgent.getPersona() == MallAgent.Persona.HELP_FAMILY_CUSTOMER) { // Help Family
//                    agent1 = leaderAgent;
//                    agent1.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent1, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent2 = mall.getAgents().get(mall.getAgents().indexOf(leaderAgent) + 1);
//                    agent2.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), agent2, agent1, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent3 = mall.getAgents().get(mall.getAgents().indexOf(leaderAgent) + 2);
//                    agent3.setAgentMovement(new MallAgentMovement(spawner3.getPatch(), agent3, agent1, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent1.getAgentMovement().getFollowers().add(agent2);
//                    agent1.getAgentMovement().getFollowers().add(agent3);
//                    agent1.getAgentMovement().setNextState();
//                }
//                else { // Duo Family
//                    agent1 = leaderAgent;
//                    agent1.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), agent1, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent2 = mall.getAgents().get(mall.getAgents().indexOf(leaderAgent) + 1);
//                    agent2.setAgentMovement(new MallAgentMovement(spawner1.getPatch(), agent2, agent1, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick));
//                    mall.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
//                    MallAgent.customerCount++;
//                    MallAgent.agentCount++;
//
//                    agent1.getAgentMovement().getFollowers().add(agent2);
//                    agent1.getAgentMovement().setNextState();
//                }
//                currentFamilyCount++;
//                totalFamilyCount++;
//            }
//            else if (!isFamily && totalAloneCustomerCount < MAX_ALONE && currentAloneCustomerCount < MAX_CURRENT_ALONE) {
//                MallAgent aloneAgent = mall.getUnspawnedAloneAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(mall.getUnspawnedAloneAgents().size()));
//                aloneAgent.setAgentMovement(new MallAgentMovement(spawner2.getPatch(), aloneAgent, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
//                mall.getAgentPatchSet().add(aloneAgent.getAgentMovement().getCurrentPatch());
//                currentAloneCustomerCount++;
//                totalAloneCustomerCount++;
//                MallAgent.customerCount++;
//                MallAgent.agentCount++;
//            }
//        }
    }

}