package com.socialsim.model.simulator.grocery;

import com.socialsim.controller.Main;
import com.socialsim.controller.grocery.controls.GroceryScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.grocery.*;
import com.socialsim.model.core.agent.grocery.GroceryState;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;
import java.io.PrintWriter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class GrocerySimulator extends Simulator {

    public static int defaultMaxFamily = 20;
    public static int defaultMaxAlone = 20;
    public static int defaultMaxCurrentFamily = 20;
    public static int defaultMaxCurrentAlone = 20;

    private Grocery grocery;
    private final AtomicBoolean running;
    private final SimulationTime time;
    private final Semaphore playSemaphore;

    public static int currentFamilyCount = 0;
    public static int currentAloneCustomerCount = 0;
    public static int totalFamilyCount = 0;
    public static int totalAloneCustomerCount = 0;
    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;
    public static float averageNonverbalDuration = 0;
    public static float averageCooperativeDuration = 0;
    public static float averageExchangeDuration = 0;
    public static int currentFamilyToFamilyCount = 0;
    public static int currentCustomerCustomerCount = 0;
    public static int currentCustomerAisleCount = 0;
    public static int currentCustomerCashierCount = 0;
    public static int currentCustomerBaggerCount = 0;
    public static int currentCustomerGuardCount = 0;
    public static int currentCustomerButcherCount = 0;
    public static int currentCustomerServiceCount = 0;
    public static int currentCustomerFoodCount = 0;
    public static int currentAisleAisleCount = 0;
    public static int currentAisleCashierCount = 0;
    public static int currentAisleBaggerCount = 0;
    public static int currentAisleGuardCount = 0;
    public static int currentAisleButcherCount = 0;
    public static int currentAisleServiceCount = 0;
    public static int currentAisleFoodCount = 0;
    public static int currentCashierCashierCount = 0;
    public static int currentCashierBaggerCount = 0;
    public static int currentCashierGuardCount = 0;
    public static int currentCashierButcherCount = 0;
    public static int currentCashierServiceCount = 0;
    public static int currentCashierFoodCount = 0;
    public static int currentBaggerBaggerCount = 0;
    public static int currentBaggerGuardCount = 0;
    public static int currentBaggerButcherCount = 0;
    public static int currentBaggerServiceCount = 0;
    public static int currentBaggerFoodCount = 0;
    public static int currentGuardGuardCount = 0;
    public static int currentGuardButcherCount = 0;
    public static int currentGuardServiceCount = 0;
    public static int currentGuardFoodCount = 0;
    public static int currentButcherButcherCount = 0;
    public static int currentButcherServiceCount = 0;
    public static int currentButcherFoodCount = 0;
    public static int currentServiceServiceCount = 0;
    public static int currentServiceFoodCount = 0;
    public static int currentFoodFoodCount = 0;
    public static int[] compiledCurrentFamilyCount;
    public static int[] compiledCurrentAloneCustomerCount;
    public static int[] compiledTotalFamilyCount;
    public static int[] compiledTotalAloneCustomerCount;
    public static int[] compiledCurrentNonverbalCount;
    public static int[] compiledCurrentCooperativeCount;
    public static int[] compiledCurrentExchangeCount;
    public static float[] compiledAverageNonverbalDuration;
    public static float[] compiledAverageCooperativeDuration;
    public static float[] compiledAverageExchangeDuration;
    public static int[] compiledCurrentFamilyToFamilyCount;
    public static int[] compiledCurrentCustomerCustomerCount;
    public static int[] compiledCurrentCustomerAisleCount;
    public static int[] compiledCurrentCustomerCashierCount;
    public static int[] compiledCurrentCustomerBaggerCount;
    public static int[] compiledCurrentCustomerGuardCount;
    public static int[] compiledCurrentCustomerButcherCount;
    public static int[] compiledCurrentCustomerServiceCount;
    public static int[] compiledCurrentCustomerFoodCount;
    public static int[] compiledCurrentAisleAisleCount;
    public static int[] compiledCurrentAisleCashierCount;
    public static int[] compiledCurrentAisleBaggerCount;
    public static int[] compiledCurrentAisleGuardCount;
    public static int[] compiledCurrentAisleButcherCount;
    public static int[] compiledCurrentAisleServiceCount;
    public static int[] compiledCurrentAisleFoodCount;
    public static int[] compiledCurrentCashierCashierCount;
    public static int[] compiledCurrentCashierBaggerCount;
    public static int[] compiledCurrentCashierGuardCount;
    public static int[] compiledCurrentCashierButcherCount;
    public static int[] compiledCurrentCashierServiceCount;
    public static int[] compiledCurrentCashierFoodCount;
    public static int[] compiledCurrentBaggerBaggerCount;
    public static int[] compiledCurrentBaggerGuardCount;
    public static int[] compiledCurrentBaggerButcherCount;
    public static int[] compiledCurrentBaggerServiceCount;
    public static int[] compiledCurrentBaggerFoodCount;
    public static int[] compiledCurrentGuardGuardCount;
    public static int[] compiledCurrentGuardButcherCount;
    public static int[] compiledCurrentGuardServiceCount;
    public static int[] compiledCurrentGuardFoodCount;
    public static int[] compiledCurrentButcherButcherCount;
    public static int[] compiledCurrentButcherServiceCount;
    public static int[] compiledCurrentButcherFoodCount;
    public static int[] compiledCurrentServiceServiceCount;
    public static int[] compiledCurrentServiceFoodCount;
    public static int[] compiledCurrentFoodFoodCount;
    public static int[][] currentPatchCount;

    public GrocerySimulator() {
        this.grocery = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(6, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start();
    }

    public Grocery getGrocery() {
        return grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
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

    public void resetToDefaultConfiguration(Grocery grocery) {
        this.grocery = grocery;
        replenishStaticVars();
        GroceryAgent.clearGroceryAgentCounts();
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(Grocery grocery) {
        grocery.createInitialAgentDemographics(grocery.getMAX_FAMILY(), grocery.getMAX_ALONE());
        GroceryAgent guard1 = grocery.getAgents().get(0);
        guard1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(57,52), guard1, null, 1.27, grocery.getPatch(57,52).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(guard1.getAgentMovement().getCurrentPatch());
        GroceryAgent.guardCount++;
        GroceryAgent.agentCount++;
        GroceryAgent guard2 = grocery.getAgents().get(1);
        guard2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(57,47), guard2, null, 1.27, grocery.getPatch(57,47).getPatchCenterCoordinates(), -1));
        grocery.getAgents().add(guard2);
        grocery.getAgentPatchSet().add(guard2.getAgentMovement().getCurrentPatch());
        GroceryAgent.guardCount++;
        GroceryAgent.agentCount++;

        GroceryAgent cashier1 = grocery.getAgents().get(2);
        cashier1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,20), cashier1, null, 1.27, grocery.getPatch(44,20).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier1.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier2 = grocery.getAgents().get(3);
        cashier2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,26), cashier2, null, 1.27, grocery.getPatch(44,26).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier2.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier3 = grocery.getAgents().get(4);
        cashier3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,32), cashier3, null, 1.27, grocery.getPatch(44,32).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier3.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier4 = grocery.getAgents().get(5);
        cashier4.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,38), cashier4, null, 1.27, grocery.getPatch(44,38).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier4.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier5 = grocery.getAgents().get(6);
        cashier5.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,44), cashier5, null, 1.27, grocery.getPatch(44,44).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier5.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier6 = grocery.getAgents().get(7);
        cashier6.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,50), cashier6, null, 1.27, grocery.getPatch(44,50).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier6.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier7 = grocery.getAgents().get(8);
        cashier7.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,56), cashier7, null, 1.27, grocery.getPatch(44,56).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier7.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;
        GroceryAgent cashier8 = grocery.getAgents().get(9);
        cashier8.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,62), cashier8, null, 1.27, grocery.getPatch(44,62).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(cashier8.getAgentMovement().getCurrentPatch());
        GroceryAgent.cashierCount++;
        GroceryAgent.agentCount++;

        GroceryAgent bagger1 = grocery.getAgents().get(10);
        bagger1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,20), bagger1, null, 1.27, grocery.getPatch(45,20).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger1.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger2 = grocery.getAgents().get(11);
        bagger2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,26), bagger2, null, 1.27, grocery.getPatch(45,26).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger2.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger3 = grocery.getAgents().get(12);
        bagger3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,32), bagger3, null, 1.27, grocery.getPatch(45,32).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger3.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger4 = grocery.getAgents().get(13);
        bagger4.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,38), bagger4, null, 1.27, grocery.getPatch(45,38).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger4.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger5 = grocery.getAgents().get(14);
        bagger5.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,44), bagger5, null, 1.27, grocery.getPatch(45,44).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger5.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger6 = grocery.getAgents().get(15);
        bagger6.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,50), bagger6, null, 1.27, grocery.getPatch(45,50).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger6.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger7 = grocery.getAgents().get(16);
        bagger7.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,56), bagger7, null, 1.27, grocery.getPatch(45,56).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger7.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;
        GroceryAgent bagger8 = grocery.getAgents().get(17);
        bagger8.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(45,62), bagger8, null, 1.27, grocery.getPatch(45,62).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(bagger8.getAgentMovement().getCurrentPatch());
        GroceryAgent.baggerCount++;
        GroceryAgent.agentCount++;

        GroceryAgent service1 = grocery.getAgents().get(18);
        service1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(52,23), service1, null, 1.27, grocery.getPatch(52,23).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(service1.getAgentMovement().getCurrentPatch());
        GroceryAgent.customerServiceCount++;
        GroceryAgent.agentCount++;
        GroceryAgent service2 = grocery.getAgents().get(19);
        service2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(52,27), service2, null, 1.27, grocery.getPatch(52,27).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(service2.getAgentMovement().getCurrentPatch());
        GroceryAgent.customerServiceCount++;
        GroceryAgent.agentCount++;
        GroceryAgent service3 = grocery.getAgents().get(20);
        service3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(52,31), service3, null, 1.27, grocery.getPatch(52,31).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(service3.getAgentMovement().getCurrentPatch());
        GroceryAgent.customerServiceCount++;
        GroceryAgent.agentCount++;

        GroceryAgent food1 = grocery.getAgents().get(21);
        food1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,84), food1, null, 1.27, grocery.getPatch(58,84).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food1.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food2 = grocery.getAgents().get(22);
        food2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,87), food2, null, 1.27, grocery.getPatch(58,87).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food2.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food3 = grocery.getAgents().get(23);
        food3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,90), food3, null, 1.27, grocery.getPatch(58,90).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food3.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food4 = grocery.getAgents().get(24);
        food4.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,93), food4, null, 1.27, grocery.getPatch(58,93).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food4.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food5 = grocery.getAgents().get(25);
        food5.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,96), food5, null, 1.27, grocery.getPatch(58,96).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food5.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;

        GroceryAgent butcher1 = grocery.getAgents().get(26);
        butcher1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(29,1), butcher1, null, 1.27, grocery.getPatch(29,1).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(butcher1.getAgentMovement().getCurrentPatch());
        GroceryAgent.butcherCount++;
        GroceryAgent.agentCount++;
        GroceryAgent butcher2 = grocery.getAgents().get(27);
        butcher2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(37,1), butcher2, null, 1.27, grocery.getPatch(37,1).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(butcher2.getAgentMovement().getCurrentPatch());
        GroceryAgent.butcherCount++;
        GroceryAgent.agentCount++;

        GroceryAgent aisle1 = grocery.getAgents().get(28);
        aisle1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(3,15), aisle1, null, 1.27, grocery.getPatch(3,15).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle1.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle2 = grocery.getAgents().get(29);
        aisle2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(3,42), aisle2, null, 1.27, grocery.getPatch(3,42).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle2.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle3 = grocery.getAgents().get(30);
        aisle3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(3,69), aisle3, null, 1.27, grocery.getPatch(3,69).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle3.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle4 = grocery.getAgents().get(31);
        aisle4.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(18,95), aisle4, null, 1.27, grocery.getPatch(18,95).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle4.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle5 = grocery.getAgents().get(32);
        aisle5.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,95), aisle5, null, 1.27, grocery.getPatch(44,95).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle5.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle6 = grocery.getAgents().get(33);
        aisle6.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(10,4), aisle6, null, 1.27, grocery.getPatch(10,4).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle6.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle7 = grocery.getAgents().get(34);
        aisle7.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(12,29), aisle7, null, 1.27, grocery.getPatch(12,29).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle7.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle8 = grocery.getAgents().get(35);
        aisle8.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(18,29), aisle8, null, 1.27, grocery.getPatch(18,29).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle8.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle9 = grocery.getAgents().get(36);
        aisle9.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(24,29), aisle9, null, 1.27, grocery.getPatch(24,29).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle9.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
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
                            updateAgentsInGrocery(grocery, currentTick);
                            spawnAgent(grocery, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        ((GroceryScreenController) Main.mainScreenController).drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 10800) {
                            ((GroceryScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInGrocery(Grocery grocery, long currentTick) throws InterruptedException {
        moveAll(grocery);
        compiledCurrentFamilyCount[(int) currentTick] = currentFamilyCount;
        compiledCurrentAloneCustomerCount[(int) currentTick] = currentAloneCustomerCount;
        compiledTotalFamilyCount[(int) currentTick] = totalFamilyCount;
        compiledTotalAloneCustomerCount[(int) currentTick] = totalAloneCustomerCount;
        compiledCurrentNonverbalCount[(int) currentTick] = currentNonverbalCount;
        compiledCurrentCooperativeCount[(int) currentTick] = currentCooperativeCount;
        compiledCurrentExchangeCount[(int) currentTick] = currentExchangeCount;
        compiledAverageNonverbalDuration[(int) currentTick] = averageNonverbalDuration;
        compiledAverageCooperativeDuration[(int) currentTick] = averageCooperativeDuration;
        compiledAverageExchangeDuration[(int) currentTick] = averageExchangeDuration;
        compiledCurrentFamilyToFamilyCount[(int) currentTick] = currentFamilyToFamilyCount;
        compiledCurrentCustomerCustomerCount[(int) currentTick] = currentCustomerCustomerCount;
        compiledCurrentCustomerAisleCount[(int) currentTick] = currentCustomerAisleCount;
        compiledCurrentCustomerCashierCount[(int) currentTick] = currentCustomerCashierCount;
        compiledCurrentCustomerBaggerCount[(int) currentTick] = currentCustomerBaggerCount;
        compiledCurrentCustomerGuardCount[(int) currentTick] = currentCustomerGuardCount;
        compiledCurrentCustomerButcherCount[(int) currentTick] = currentCustomerButcherCount;
        compiledCurrentCustomerServiceCount[(int) currentTick] = currentCustomerServiceCount;
        compiledCurrentCustomerFoodCount[(int) currentTick] = currentCustomerFoodCount;
        compiledCurrentAisleAisleCount[(int) currentTick] = currentAisleAisleCount;
        compiledCurrentAisleCashierCount[(int) currentTick] = currentAisleCashierCount;
        compiledCurrentAisleBaggerCount[(int) currentTick] = currentAisleBaggerCount;
        compiledCurrentAisleGuardCount[(int) currentTick] = currentAisleGuardCount;
        compiledCurrentAisleButcherCount[(int) currentTick] = currentAisleButcherCount;
        compiledCurrentAisleServiceCount[(int) currentTick] = currentAisleServiceCount;
        compiledCurrentAisleFoodCount[(int) currentTick] = currentAisleFoodCount;
        compiledCurrentCashierCashierCount[(int) currentTick] = currentCashierCashierCount;
        compiledCurrentCashierBaggerCount[(int) currentTick] = currentCashierBaggerCount;
        compiledCurrentCashierGuardCount[(int) currentTick] = currentCashierGuardCount;
        compiledCurrentCashierButcherCount[(int) currentTick] = currentCashierButcherCount;
        compiledCurrentCashierServiceCount[(int) currentTick] = currentCashierServiceCount;
        compiledCurrentCashierFoodCount[(int) currentTick] = currentCashierFoodCount;
        compiledCurrentBaggerBaggerCount[(int) currentTick] = currentBaggerBaggerCount;
        compiledCurrentBaggerGuardCount[(int) currentTick] = currentBaggerGuardCount;
        compiledCurrentBaggerButcherCount[(int) currentTick] = currentBaggerButcherCount;
        compiledCurrentBaggerServiceCount[(int) currentTick] = currentBaggerServiceCount;
        compiledCurrentBaggerFoodCount[(int) currentTick] = currentBaggerFoodCount;
        compiledCurrentGuardGuardCount[(int) currentTick] = currentGuardGuardCount;
        compiledCurrentGuardButcherCount[(int) currentTick] = currentGuardButcherCount;
        compiledCurrentGuardServiceCount[(int) currentTick] = currentGuardServiceCount;
        compiledCurrentGuardFoodCount[(int) currentTick] = currentGuardFoodCount;
        compiledCurrentButcherButcherCount[(int) currentTick] = currentButcherButcherCount;
        compiledCurrentButcherServiceCount[(int) currentTick] = currentButcherServiceCount;
        compiledCurrentButcherFoodCount[(int) currentTick] = currentButcherFoodCount;
        compiledCurrentServiceServiceCount[(int) currentTick] = currentServiceServiceCount;
        compiledCurrentServiceFoodCount[(int) currentTick] = currentServiceFoodCount;
        compiledCurrentFoodFoodCount[(int) currentTick] = currentFoodFoodCount;
    }

    private static void moveAll(Grocery grocery) {
        int bathroomReserves = grocery.numBathroomsFree();
        for (GroceryAgent agent : grocery.getMovableAgents()) {
            try {
                if (agent.getAgentMovement().getCurrentState().getName() == GroceryState.Name.WAIT_INFRONT_OF_BATHROOM){
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

    private static void moveOne(GroceryAgent agent) throws Throwable {
        GroceryAgentMovement agentMovement = agent.getAgentMovement();

        GroceryAgent.Type type = agent.getType();
        GroceryAgent.Persona persona = agent.getPersona();
        GroceryState state = agentMovement.getCurrentState();
        GroceryAction action = agentMovement.getCurrentAction();

        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()) {
            switch (type) {
                case STAFF_AISLE:
                    if (state.getName() == GroceryState.Name.STAFF_AISLE) {
                        if (action.getName() == GroceryAction.Name.STAFF_AISLE_ORGANIZE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseRandomAisle();
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
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (state.getName() == GroceryState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == GroceryAction.Name.GO_TO_BATHROOM) {
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
//                                        agentMovement.setNextState(agentMovement.getStateIndex());
//                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
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
                        else if (action.getName() == GroceryAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                if(agentMovement.getGoalAttractor()!=null){
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.WASH_IN_SINK) {
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
//                                        if (agentMovement.getRoutePlan().isFromStudying()) {
//                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                            agentMovement.setNextState(agentMovement.getReturnIndex());
//                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
//                                            agentMovement.setActionIndex(0);
//                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                            if(agentMovement.getGoalAttractor()!=null){
//                                                agentMovement.getGoalAttractor().setIsReserved(false);
//                                            }
//                                            agentMovement.resetGoal();
//                                            agentMovement.getRoutePlan().setFromStudying(false);
//                                        }
//                                        else if (agentMovement.getRoutePlan().isFromClass()) {
//                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                            agentMovement.setNextState(agentMovement.getReturnIndex());
//                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
//                                            agentMovement.setActionIndex(0);
//                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                            if (agentMovement.getGoalAttractor()!=null) {
//                                                agentMovement.getGoalAttractor().setIsReserved(false);
//                                            }
//                                            agentMovement.resetGoal();
//                                            agentMovement.getRoutePlan().setFromClass(false);
//                                        }
//                                        else if (agentMovement.getRoutePlan().isFromLunch()) {
//                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                            agentMovement.setNextState(agentMovement.getReturnIndex());
//                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
//                                            agentMovement.setActionIndex(0);
//                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                            if (agentMovement.getGoalAttractor()!=null) {
//                                                agentMovement.getGoalAttractor().setIsReserved(false);
//                                            }
//                                            agentMovement.resetGoal();
//                                            agentMovement.getRoutePlan().setFromLunch(false);
//                                        }
//                                        else {
//                                            agentMovement.setNextState(agentMovement.getStateIndex());
//                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
//                                            agentMovement.setActionIndex(0);
//                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                            if(agentMovement.getGoalAttractor()!=null){
//                                                agentMovement.getGoalAttractor().setIsReserved(false);
//                                            }
//                                            agentMovement.resetGoal();
//                                        }
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        if(agentMovement.getGoalAttractor()!=null){
                                            agentMovement.getGoalAttractor().setIsReserved(false);
                                        }
                                        agentMovement.resetGoal();
                                    }
                                }
                            }
                        }
                    }
                    else if(state.getName() == GroceryState.Name.WAIT_INFRONT_OF_BATHROOM){
                        if (action.getName() == GroceryAction.Name.GO_TO_WAIT_AREA) {
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
                        else if(action.getName() == GroceryAction.Name.WAIT_FOR_VACANT){
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

                case CUSTOMER:
                    if (state.getName() == GroceryState.Name.GOING_TO_SECURITY) {
                        if (action.getName() == GroceryAction.Name.GOING_TO_SECURITY_QUEUE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.grocerySimulator.getGrocery().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getSecurities().get(0));
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
                        else if (action.getName() == GroceryAction.Name.GO_THROUGH_SCANNER) {
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
                                    if (persona == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER || persona == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER || persona == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER || persona == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
                                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                        if (x < GroceryRoutePlan.CHANCE_GUARD_INTERACT) {
                                            agentMovement.forceStationedInteraction(GroceryAgent.Persona.GUARD_ENTRANCE);
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
                    else if (state.getName() == GroceryState.Name.GOING_CART) {
                        if (action.getName() == GroceryAction.Name.GET_CART) {
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
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (state.getName() == GroceryState.Name.GOING_TO_PRODUCTS) {
                        agentMovement.getRoutePlan().MAX_AISLE_HELP = 1;
                        if (action.getName() == GroceryAction.Name.GO_TO_PRODUCT_WALL || action.getName() == GroceryAction.Name.GO_TO_AISLE || action.getName() == GroceryAction.Name.GO_TO_FROZEN || action.getName() == GroceryAction.Name.GO_TO_FRESH || action.getName() == GroceryAction.Name.GO_TO_MEAT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getCurrentAction().getDestination().getAmenityBlock());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                agentMovement.checkIfStuck();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == GroceryState.Name.IN_PRODUCTS_WALL || state.getName() == GroceryState.Name.IN_PRODUCTS_AISLE || state.getName() == GroceryState.Name.IN_PRODUCTS_FROZEN || state.getName() == GroceryState.Name.IN_PRODUCTS_FRESH || state.getName() == GroceryState.Name.IN_PRODUCTS_MEAT) {
                        if (action.getName() == GroceryAction.Name.CHECK_PRODUCTS) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() != null) {
                                if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting() && state.getName() == GroceryState.Name.IN_PRODUCTS_MEAT) {
                                    agentMovement.forceStationedInteraction(GroceryAgent.Persona.BUTCHER);
                                }

                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.setStationInteracting(false);
                                }
                                else {
                                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                    if (persona == GroceryAgent.Persona.STTP_ALONE_CUSTOMER) {
                                        if (x < GroceryRoutePlan.AISLE_HELP_CHANCE_STTP && agentMovement.getRoutePlan().MAX_AISLE_HELP > 0) {
                                            agentMovement.forceStationedInteraction(GroceryAgent.Persona.STAFF_AISLE);
                                            agentMovement.setStationInteracting(false);
                                            agentMovement.getRoutePlan().MAX_AISLE_HELP--;
                                        }
                                    }
                                    else if (persona == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
                                        if (x < GroceryRoutePlan.AISLE_HELP_CHANCE_MODERATE && agentMovement.getRoutePlan().MAX_AISLE_HELP > 0) {
                                            agentMovement.forceStationedInteraction(GroceryAgent.Persona.STAFF_AISLE);
                                            agentMovement.setStationInteracting(false);
                                            agentMovement.getRoutePlan().MAX_AISLE_HELP--;
                                        }
                                    }
                                    else if (persona == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER) {
                                        if (x < GroceryRoutePlan.AISLE_HELP_CHANCE_COMPLETE && agentMovement.getRoutePlan().MAX_AISLE_HELP > 0) {
                                            agentMovement.forceStationedInteraction(GroceryAgent.Persona.STAFF_AISLE);
                                            agentMovement.setStationInteracting(false);
                                            agentMovement.getRoutePlan().MAX_AISLE_HELP--;
                                        }
                                    }
                                    else if (persona == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER) {
                                        if (x < GroceryRoutePlan.AISLE_HELP_CHANCE_HELP && agentMovement.getRoutePlan().MAX_AISLE_HELP > 0) {
                                            agentMovement.forceStationedInteraction(GroceryAgent.Persona.STAFF_AISLE);
                                            agentMovement.setStationInteracting(false);
                                            agentMovement.getRoutePlan().MAX_AISLE_HELP--;
                                        }
                                    }
                                    else if (persona == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER) {
                                        if (x < GroceryRoutePlan.AISLE_HELP_CHANCE_DUO && agentMovement.getRoutePlan().MAX_AISLE_HELP > 0) {
                                            agentMovement.forceStationedInteraction(GroceryAgent.Persona.STAFF_AISLE);
                                            agentMovement.setStationInteracting(false);
                                            agentMovement.getRoutePlan().MAX_AISLE_HELP--;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == GroceryState.Name.GOING_TO_PAY || state.getName() == GroceryState.Name.GOING_TO_SERVICE || state.getName() == GroceryState.Name.GOING_TO_EAT) {
                        if (action.getName() == GroceryAction.Name.GO_TO_CHECKOUT || action.getName() == GroceryAction.Name.GO_TO_CUSTOMER_SERVICE || action.getName() == GroceryAction.Name.GO_TO_FOOD_STALL) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                if (action.getName() == GroceryAction.Name.GO_TO_CHECKOUT) {
                                    agentMovement.chooseCashierCounter();
                                }
                                else if (action.getName() == GroceryAction.Name.GO_TO_CUSTOMER_SERVICE) {
                                    agentMovement.chooseServiceCounter();
                                }
                                else if (action.getName() == GroceryAction.Name.GO_TO_FOOD_STALL) {
                                    agentMovement.chooseStall();
                                }
                            }
                            if (agentMovement.getGoalQueueingPatchField() != null) {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    agentMovement.checkIfStuck();
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
                        }
                        else if (action.getName() == GroceryAction.Name.QUEUE_CHECKOUT || action.getName() == GroceryAction.Name.QUEUE_SERVICE || action.getName() == GroceryAction.Name.QUEUE_FOOD) {
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
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.CHECKOUT || action.getName() == GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE || action.getName() == GroceryAction.Name.BUY_FOOD) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() != null) {
                                if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                    if (action.getName() == GroceryAction.Name.CHECKOUT) {
                                        agentMovement.forceStationedInteraction(GroceryAgent.Persona.CASHIER);
                                        if (persona == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER || persona == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER || persona == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER || persona == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
                                            int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                            if (x < GroceryRoutePlan.CHANCE_BAGGER_INTERACT) {
                                                agentMovement.forceStationedInteraction(GroceryAgent.Persona.BAGGER);
                                            }
                                        }
                                    }
                                    else if (action.getName() == GroceryAction.Name.WAIT_FOR_CUSTOMER_SERVICE) {
                                        agentMovement.forceStationedInteraction(GroceryAgent.Persona.CUSTOMER_SERVICE);
                                    }
                                    else if (action.getName() == GroceryAction.Name.BUY_FOOD) {
                                        agentMovement.forceStationedInteraction(GroceryAgent.Persona.STAFF_FOOD);
                                    }
                                }

                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                if (agentMovement.getCurrentAction().getDuration() <= 0) {
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
                    else if (state.getName() == GroceryState.Name.EATING) {
                        agentMovement.setSimultaneousInteractionAllowed(false);
                        if (action.getName() == GroceryAction.Name.FIND_SEAT_FOOD_COURT) {
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseEatTable();
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
                                }
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.EATING_FOOD) {
                            if (agentMovement.getGoalAmenity() != null) {
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
                            }
                        }
                    }
                    else if (state.getName() == GroceryState.Name.GOING_HOME) {
                        if (action.getName() == GroceryAction.Name.GO_TO_RECEIPT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.grocerySimulator.getGrocery().getGroceryGateFields().get(0));
                                agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getGroceryGates().get(0));
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
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.CHECKOUT_GROCERIES_CUSTOMER) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                if (agentMovement.getLeaderAgent() == null && !agentMovement.isStationInteracting()) {
                                    agentMovement.forceStationedInteraction(GroceryAgent.Persona.GUARD_EXIT);
                                }

                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.leaveQueue();
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.setStationInteracting(false);
                                }
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.LEAVE_BUILDING) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.grocerySimulator.getGrocery().getGroceryGates().get(0));
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
                    else if (state.getName() == GroceryState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == GroceryAction.Name.GO_TO_BATHROOM) {
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
                        else if (action.getName() == GroceryAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                if(agentMovement.getGoalAttractor()!=null){
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.WASH_IN_SINK) {
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
                    else if(state.getName() == GroceryState.Name.WAIT_INFRONT_OF_BATHROOM){
                        if (action.getName() == GroceryAction.Name.GO_TO_WAIT_AREA) {
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
                        else if(action.getName() == GroceryAction.Name.WAIT_FOR_VACANT){
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
            GroceryAgent agent2 = null;
            for (Patch patch: patches) {
                for (Agent otherAgent: patch.getAgents()) {
                    GroceryAgent groceryAgent = (GroceryAgent) otherAgent;
                    if (!groceryAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), groceryAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(groceryAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), groceryAgent.getAgentMovement().getProposedHeading(), groceryAgent.getAgentMovement().getFieldOfViewAngle())){
                                agentMovement.rollAgentInteraction(groceryAgent);
                                if (agentMovement.isInteracting()){
                                    agent2 = groceryAgent;
                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
                                    currentPatchCount[groceryAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][groceryAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
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
//                    GroceryAgent groceryAgent = (GroceryAgent) otherAgent;
//                    if (!groceryAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
//                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), groceryAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
//                            if (Coordinates.isWithinFieldOfView(groceryAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), groceryAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
//                                agentMovement.rollAgentInteraction(groceryAgent);
//                                if (agentMovement.isInteracting()) {
//                                    agent2 = groceryAgent;
//                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
//                                    currentPatchCount[groceryAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][groceryAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
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

    private void spawnAgent(Grocery grocery, long currentTick) {
        GroceryGate gate = grocery.getGroceryGates().get(1);
        GroceryAgent agent1 = null;
        GroceryAgent agent2 = null;
        GroceryAgent agent3 = null;
        GroceryAgent agent4 = null;

        Gate.GateBlock spawner1 = gate.getSpawners().get(0);
        Gate.GateBlock spawner2 = gate.getSpawners().get(1);
        Gate.GateBlock spawner3 = gate.getSpawners().get(2);

        int spawnChance = (int) gate.getChancePerTick();
        int CHANCE = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
        boolean isFamily = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

        if (CHANCE > spawnChance) {
            if (isFamily && totalAloneCustomerCount < grocery.getMAX_FAMILY() && currentFamilyCount < grocery.getMAX_CURRENT_FAMILY()) {
                if (grocery.getUnspawnedFamilyAgents().size() > 0) {
                    int randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(grocery.getUnspawnedFamilyAgents().size());
                    GroceryAgent leaderAgent = grocery.getUnspawnedFamilyAgents().get(randNum);
                    while(!leaderAgent.isLeader()) {
                        randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(grocery.getUnspawnedFamilyAgents().size());
                        leaderAgent = grocery.getUnspawnedFamilyAgents().get(randNum);
                    }

                    if (leaderAgent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER) {
                        agent2 = grocery.getUnspawnedFamilyAgents().get(randNum + 1);
                        agent3 = grocery.getUnspawnedFamilyAgents().get(randNum + 2);
                        agent4 = grocery.getUnspawnedFamilyAgents().get(randNum + 3);

                        agent1 = leaderAgent;
                        agent1.setAgentMovement(new GroceryAgentMovement(spawner2.getPatch(), agent1, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent2.setAgentMovement(new GroceryAgentMovement(spawner1.getPatch(), agent2, agent1, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent3.setAgentMovement(new GroceryAgentMovement(spawner3.getPatch(), agent3, agent1, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent4.setAgentMovement(new GroceryAgentMovement(spawner3.getPatch(), agent4, agent1, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent4.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent1.getAgentMovement().getFollowers().add(agent2);
                        agent1.getAgentMovement().getFollowers().add(agent3);
                        agent1.getAgentMovement().getFollowers().add(agent4);
                    }
                    else if (leaderAgent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER) {
                        agent2 = grocery.getUnspawnedFamilyAgents().get(randNum + 1);
                        agent3 = grocery.getUnspawnedFamilyAgents().get(randNum + 2);

                        agent1 = leaderAgent;
                        agent1.setAgentMovement(new GroceryAgentMovement(spawner2.getPatch(), agent1, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent2.setAgentMovement(new GroceryAgentMovement(spawner1.getPatch(), agent2, agent1, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent3.setAgentMovement(new GroceryAgentMovement(spawner3.getPatch(), agent3, agent1, 1.27, spawner3.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent3.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent1.getAgentMovement().getFollowers().add(agent2);
                        agent1.getAgentMovement().getFollowers().add(agent3);
                    }
                    else {
                        agent2 = grocery.getUnspawnedFamilyAgents().get(randNum + 1);

                        agent1 = leaderAgent;
                        agent1.setAgentMovement(new GroceryAgentMovement(spawner2.getPatch(), agent1, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent1.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent2.setAgentMovement(new GroceryAgentMovement(spawner1.getPatch(), agent2, agent1, 1.27, spawner1.getPatch().getPatchCenterCoordinates(), currentTick));
                        grocery.getAgentPatchSet().add(agent2.getAgentMovement().getCurrentPatch());
                        GroceryAgent.customerCount++;
                        GroceryAgent.agentCount++;

                        agent1.getAgentMovement().getFollowers().add(agent2);
                    }
                    currentFamilyCount++;
                    totalFamilyCount++;
                }
            }
            else if (!isFamily && totalAloneCustomerCount < grocery.getMAX_ALONE() && currentAloneCustomerCount < grocery.getMAX_CURRENT_ALONE()) {
                if (grocery.getUnspawnedAloneAgents().size() > 0) {
                    GroceryAgent aloneAgent = grocery.getUnspawnedAloneAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(grocery.getUnspawnedAloneAgents().size()));
                    aloneAgent.setAgentMovement(new GroceryAgentMovement(spawner2.getPatch(), aloneAgent, null, 1.27, spawner2.getPatch().getPatchCenterCoordinates(), currentTick));
                    grocery.getAgentPatchSet().add(aloneAgent.getAgentMovement().getCurrentPatch());
                    currentAloneCustomerCount++;
                    totalAloneCustomerCount++;
                    GroceryAgent.customerCount++;
                    GroceryAgent.agentCount++;
                }
            }
        }
    }

    public void replenishStaticVars() {
        currentFamilyCount = 0;
        currentAloneCustomerCount = 0;
        totalFamilyCount = 0;
        totalAloneCustomerCount = 0;
        currentNonverbalCount = 0;
        currentCooperativeCount = 0;
        currentExchangeCount = 0;
        averageNonverbalDuration = 0;
        averageCooperativeDuration = 0;
        averageExchangeDuration = 0;
        currentFamilyToFamilyCount = 0;
        currentCustomerCustomerCount = 0;
        currentCustomerAisleCount = 0;
        currentCustomerCashierCount = 0;
        currentCustomerBaggerCount = 0;
        currentCustomerGuardCount = 0;
        currentCustomerButcherCount = 0;
        currentCustomerServiceCount = 0;
        currentCustomerFoodCount = 0;
        currentAisleAisleCount = 0;
        currentAisleCashierCount = 0;
        currentAisleBaggerCount = 0;
        currentAisleGuardCount = 0;
        currentAisleButcherCount = 0;
        currentAisleServiceCount = 0;
        currentAisleFoodCount = 0;
        currentCashierCashierCount = 0;
        currentCashierBaggerCount = 0;
        currentCashierGuardCount = 0;
        currentCashierButcherCount = 0;
        currentCashierServiceCount = 0;
        currentCashierFoodCount = 0;
        currentBaggerBaggerCount = 0;
        currentBaggerGuardCount = 0;
        currentBaggerButcherCount = 0;
        currentBaggerServiceCount = 0;
        currentBaggerFoodCount = 0;
        currentGuardGuardCount = 0;
        currentGuardButcherCount = 0;
        currentGuardServiceCount = 0;
        currentGuardFoodCount = 0;
        currentButcherButcherCount = 0;
        currentButcherServiceCount = 0;
        currentButcherFoodCount = 0;
        currentServiceServiceCount = 0;
        currentServiceFoodCount = 0;
        currentFoodFoodCount = 0;
        compiledCurrentFamilyCount = new int[10801];
        compiledCurrentAloneCustomerCount = new int[10801];
        compiledTotalFamilyCount = new int[10801];
        compiledTotalAloneCustomerCount = new int[10801];
        compiledCurrentNonverbalCount = new int[10801];
        compiledCurrentCooperativeCount = new int[10801];
        compiledCurrentExchangeCount = new int[10801];
        compiledAverageNonverbalDuration = new float[10801];
        compiledAverageCooperativeDuration = new float[10801];
        compiledAverageExchangeDuration = new float[10801];
        compiledCurrentFamilyToFamilyCount = new int[10801];
        compiledCurrentCustomerCustomerCount = new int[10801];
        compiledCurrentCustomerAisleCount = new int[10801];
        compiledCurrentCustomerCashierCount = new int[10801];
        compiledCurrentCustomerBaggerCount = new int[10801];
        compiledCurrentCustomerGuardCount = new int[10801];
        compiledCurrentCustomerButcherCount = new int[10801];
        compiledCurrentCustomerServiceCount = new int[10801];
        compiledCurrentCustomerFoodCount = new int[10801];
        compiledCurrentAisleAisleCount = new int[10801];
        compiledCurrentAisleCashierCount = new int[10801];
        compiledCurrentAisleBaggerCount = new int[10801];
        compiledCurrentAisleGuardCount = new int[10801];
        compiledCurrentAisleButcherCount = new int[10801];
        compiledCurrentAisleServiceCount = new int[10801];
        compiledCurrentAisleFoodCount = new int[10801];
        compiledCurrentCashierCashierCount = new int[10801];
        compiledCurrentCashierBaggerCount = new int[10801];
        compiledCurrentCashierGuardCount = new int[10801];
        compiledCurrentCashierButcherCount = new int[10801];
        compiledCurrentCashierServiceCount = new int[10801];
        compiledCurrentCashierFoodCount = new int[10801];
        compiledCurrentBaggerBaggerCount = new int[10801];
        compiledCurrentBaggerGuardCount = new int[10801];
        compiledCurrentBaggerButcherCount = new int[10801];
        compiledCurrentBaggerServiceCount = new int[10801];
        compiledCurrentBaggerFoodCount = new int[10801];
        compiledCurrentGuardGuardCount = new int[10801];
        compiledCurrentGuardButcherCount = new int[10801];
        compiledCurrentGuardServiceCount = new int[10801];
        compiledCurrentGuardFoodCount = new int[10801];
        compiledCurrentButcherButcherCount = new int[10801];
        compiledCurrentButcherServiceCount = new int[10801];
        compiledCurrentButcherFoodCount = new int[10801];
        compiledCurrentServiceServiceCount = new int[10801];
        compiledCurrentServiceFoodCount = new int[10801];
        compiledCurrentFoodFoodCount = new int[10801];
        currentPatchCount = new int[grocery.getRows()][grocery.getColumns()];
    }

    public static void exportToCSV() throws Exception{
        PrintWriter writer = new PrintWriter("Grocery SocialSim Statistics.csv");
        StringBuilder sb = new StringBuilder();
        sb.append("Current Family Count");
        sb.append(",");
        sb.append("Current Alone Customer Count");
        sb.append(",");
        sb.append("Total Family Count");
        sb.append(",");
        sb.append("Total AloneCustomer Count");
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
        sb.append("Current Family To Family Count");
        sb.append(",");
        sb.append("Current Customer Customer Count");
        sb.append(",");
        sb.append("Current Customer Aisle Count");
        sb.append(",");
        sb.append("Current Customer Cashier Count");
        sb.append(",");
        sb.append("Current Customer Bagger Count");
        sb.append(",");
        sb.append("Current Customer Guard Count");
        sb.append(",");
        sb.append("Current Customer Butcher Count");
        sb.append(",");
        sb.append("Current Customer Service Count");
        sb.append(",");
        sb.append("Current Customer Food Count");
        sb.append(",");
        sb.append("Current Aisle Aisle Count");
        sb.append(",");
        sb.append("Current Aisle Cashier Count");
        sb.append(",");
        sb.append("Current Aisle Bagger Count");
        sb.append(",");
        sb.append("Current Aisle Guard Count");
        sb.append(",");
        sb.append("Current Aisle Butcher Count");
        sb.append(",");
        sb.append("Current Aisle Service Count");
        sb.append(",");
        sb.append("Current Aisle Food Count");
        sb.append(",");
        sb.append("Current Cashier Cashier Count");
        sb.append(",");
        sb.append("Current Cashier Bagger Count");
        sb.append(",");
        sb.append("Current Cashier Guard Count");
        sb.append(",");
        sb.append("Current Cashier Butcher Count");
        sb.append(",");
        sb.append("Current Cashier Service Count");
        sb.append(",");
        sb.append("Current Cashier Food Count");
        sb.append(",");
        sb.append("Current Bagger Bagger Count");
        sb.append(",");
        sb.append("Current Bagger Guard Count");
        sb.append(",");
        sb.append("Current Bagger Butcher Count");
        sb.append(",");
        sb.append("Current Bagger Service Count");
        sb.append(",");
        sb.append("Current Bagger Food Count");
        sb.append(",");
        sb.append("Current Guard Guard Count");
        sb.append(",");
        sb.append("Current Guard Butcher Count");
        sb.append(",");
        sb.append("Current Guard Service Count");
        sb.append(",");
        sb.append("Current Guard Food Count");
        sb.append(",");
        sb.append("Current Butcher Butcher Count");
        sb.append(",");
        sb.append("Current Butcher Service Count");
        sb.append(",");
        sb.append("Current Butcher Food Count");
        sb.append(",");
        sb.append("Current Service Service Count");
        sb.append(",");
        sb.append("Current Service Food Count");
        sb.append(",");
        sb.append("Current Food Food Count");
        sb.append("\n");
        for (int i = 0; i < 10801; i++){
            sb.append(compiledCurrentFamilyCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAloneCustomerCount[i]);
            sb.append(",");
            sb.append(compiledTotalFamilyCount[i]);
            sb.append(",");
            sb.append(compiledTotalAloneCustomerCount[i]);
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
            sb.append(compiledCurrentFamilyToFamilyCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerCustomerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerAisleCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerCashierCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerBaggerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerButcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCustomerFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleAisleCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleCashierCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleBaggerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleButcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentAisleFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCashierCashierCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCashierBaggerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCashierGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCashierButcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCashierServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCashierFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBaggerBaggerCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBaggerGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBaggerButcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBaggerServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentBaggerFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardButcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentButcherButcherCount[i]);
            sb.append(",");
            sb.append(compiledCurrentButcherServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentButcherFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentServiceServiceCount[i]);
            sb.append(",");
            sb.append(compiledCurrentServiceFoodCount[i]);
            sb.append(",");
            sb.append(compiledCurrentFoodFoodCount[i]);
            sb.append("\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    public static void exportHeatMap() throws Exception {
        PrintWriter writer = new PrintWriter("Grocery SocialSim Heat Map.csv");
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