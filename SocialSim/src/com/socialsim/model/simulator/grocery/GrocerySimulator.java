package com.socialsim.model.simulator.grocery;

import com.socialsim.controller.Main;
import com.socialsim.controller.grocery.controls.GroceryScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.grocery.GroceryAction;
import com.socialsim.model.core.agent.grocery.GroceryAgentMovement;
import com.socialsim.model.core.agent.grocery.GroceryState;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class GrocerySimulator extends Simulator {

    private Grocery grocery;

    // Simulator variables
    private final AtomicBoolean running;
    private final SimulationTime time; // Denotes the current time in the simulation
    private final Semaphore playSemaphore;

    public static final int MAX_FAMILY = 20; //250
    public static final int MAX_ALONE = 20;
    public static final int MAX_CURRENT_FAMILY = 20; //250
    public static final int MAX_CURRENT_ALONE = 20;

    public static int currentFamilyCount = 0;
    public static int currentAloneCustomerCount = 0;
    public static int totalFamilyCount = 0;
    public static int totalAloneCustomerCount = 0;
    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;

    public static int averageNonverbalDuration = 0;
    public static int averageCooperativeDuration = 0;
    public static int averageExchangeDuration = 0;

    //TODO: monitors for type-type interactions
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
    public static int[][] currentPatchCount;

    public GrocerySimulator() {
        this.grocery = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(6, 0, 0);
        this.playSemaphore = new Semaphore(0);
        this.start(); // Start the simulation thread, but in reality it would be activated much later
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
        this.time.reset();
        this.running.set(false);
        currentPatchCount = new int[grocery.getRows()][grocery.getColumns()];
    }

    public void spawnInitialAgents(Grocery grocery) {
        grocery.createInitialAgentDemographics(MAX_FAMILY, MAX_ALONE);
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
        service1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,4), service1, null, 1.27, grocery.getPatch(44,4).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(service1.getAgentMovement().getCurrentPatch());
        GroceryAgent.customerServiceCount++;
        GroceryAgent.agentCount++;
        GroceryAgent service2 = grocery.getAgents().get(19);
        service2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,8), service2, null, 1.27, grocery.getPatch(44,8).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(service2.getAgentMovement().getCurrentPatch());
        GroceryAgent.customerServiceCount++;
        GroceryAgent.agentCount++;
        GroceryAgent service3 = grocery.getAgents().get(20);
        service3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,12), service3, null, 1.27, grocery.getPatch(44,12).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(service3.getAgentMovement().getCurrentPatch());
        GroceryAgent.customerServiceCount++;
        GroceryAgent.agentCount++;

        GroceryAgent food1 = grocery.getAgents().get(21);
        food1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,8), food1, null, 1.27, grocery.getPatch(58,8).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food1.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food2 = grocery.getAgents().get(22);
        food2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,17), food2, null, 1.27, grocery.getPatch(58,17).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food2.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food3 = grocery.getAgents().get(23);
        food3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,26), food3, null, 1.27, grocery.getPatch(58,26).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food3.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food4 = grocery.getAgents().get(24);
        food4.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,35), food4, null, 1.27, grocery.getPatch(58,35).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food4.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food5 = grocery.getAgents().get(25);
        food5.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,63), food5, null, 1.27, grocery.getPatch(58,63).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food5.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food6 = grocery.getAgents().get(26);
        food6.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,72), food6, null, 1.27, grocery.getPatch(58,72).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food6.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food7 = grocery.getAgents().get(27);
        food7.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,81), food7, null, 1.27, grocery.getPatch(58,81).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food7.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;
        GroceryAgent food8 = grocery.getAgents().get(28);
        food8.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(58,90), food8, null, 1.27, grocery.getPatch(58,90).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(food8.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffFoodCount++;
        GroceryAgent.agentCount++;

        GroceryAgent butcher1 = grocery.getAgents().get(29);
        butcher1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(29,1), butcher1, null, 1.27, grocery.getPatch(29,1).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(butcher1.getAgentMovement().getCurrentPatch());
        GroceryAgent.butcherCount++;
        GroceryAgent.agentCount++;
        GroceryAgent butcher2 = grocery.getAgents().get(30);
        butcher2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(37,1), butcher2, null, 1.27, grocery.getPatch(37,1).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(butcher2.getAgentMovement().getCurrentPatch());
        GroceryAgent.butcherCount++;
        GroceryAgent.agentCount++;

        GroceryAgent aisle1 = grocery.getAgents().get(31);
        aisle1.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(3,15), aisle1, null, 1.27, grocery.getPatch(3,15).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle1.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle2 = grocery.getAgents().get(32);
        aisle2.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(3,42), aisle2, null, 1.27, grocery.getPatch(3,42).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle2.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle3 = grocery.getAgents().get(33);
        aisle3.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(3,69), aisle3, null, 1.27, grocery.getPatch(3,69).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle3.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle4 = grocery.getAgents().get(34);
        aisle4.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(18,95), aisle4, null, 1.27, grocery.getPatch(18,95).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle4.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle5 = grocery.getAgents().get(35);
        aisle5.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(44,95), aisle5, null, 1.27, grocery.getPatch(44,95).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle5.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle6 = grocery.getAgents().get(36);
        aisle6.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(10,4), aisle6, null, 1.27, grocery.getPatch(10,4).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle6.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle7 = grocery.getAgents().get(37);
        aisle7.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(12,29), aisle7, null, 1.27, grocery.getPatch(12,29).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle7.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle8 = grocery.getAgents().get(38);
        aisle8.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(18,29), aisle8, null, 1.27, grocery.getPatch(18,29).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle8.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
        GroceryAgent aisle9 = grocery.getAgents().get(39);
        aisle9.setAgentMovement(new GroceryAgentMovement(grocery.getPatch(24,29), aisle9, null, 1.27, grocery.getPatch(24,29).getPatchCenterCoordinates(), -1));
        grocery.getAgentPatchSet().add(aisle9.getAgentMovement().getCurrentPatch());
        GroceryAgent.staffAisleCount++;
        GroceryAgent.agentCount++;
//        GroceryAgent aisle11 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(30,29), true, null, -1);
//        grocery.getAgents().add(aisle11);
//        grocery.getAgentPatchSet().add(aisle11.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle12 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(32,31), true, null, -1);
//        grocery.getAgents().add(aisle12);
//        grocery.getAgentPatchSet().add(aisle12.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle13 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(32,70), true, null, -1);
//        grocery.getAgents().add(aisle13);
//        grocery.getAgentPatchSet().add(aisle13.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle14 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(14,14), true, null, -1);
//        grocery.getAgents().add(aisle14);
//        grocery.getAgentPatchSet().add(aisle14.getAgentMovement().getCurrentPatch());
//        GroceryAgent aisle15 = GroceryAgent.GroceryAgentFactory.create(GroceryAgent.Type.STAFF_AISLE, null, null, null, grocery.getPatch(26,14), true, null, -1);
//        grocery.getAgents().add(aisle15);
//        grocery.getAgentPatchSet().add(aisle15.getAgentMovement().getCurrentPatch());
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
                            updateAgentsInGrocery(grocery);
                            spawnAgent(grocery, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Redraw the visualization
                        // If the refreshes are frequent enough, update the visualization in a speed-aware manner
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

    public static void updateAgentsInGrocery(Grocery grocery) throws InterruptedException { // Manage all agent-related updates
        moveAll(grocery);
    }

    private static void moveAll(Grocery grocery) { // Make all agents move for one tick
        for (GroceryAgent agent : grocery.getMovableAgents()) {
            try {
                moveOne(agent);
                agent.getAgentGraphic().change();
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

        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()){
            switch (type) {
                case STAFF_AISLE:
                    if (state.getName() == GroceryState.Name.STAFF_AISLE) {
                        if (action.getName() == GroceryAction.Name.STAFF_AISLE_ORGANIZE) {
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

                    break;

                case CUSTOMER:
                    if (state.getName() == GroceryState.Name.GOING_TO_SECURITY) {
                        if (action.getName() == GroceryAction.Name.GOING_TO_SECURITY_QUEUE) {
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
                    else if (state.getName() == GroceryState.Name.GOING_CART) {
                        if (action.getName() == GroceryAction.Name.GET_CART) {
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
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
                        if (action.getName() == GroceryAction.Name.GO_TO_PRODUCT_WALL || action.getName() == GroceryAction.Name.GO_TO_AISLE || action.getName() == GroceryAction.Name.GO_TO_FROZEN || action.getName() == GroceryAction.Name.GO_TO_FRESH || action.getName() == GroceryAction.Name.GO_TO_MEAT) {
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
                    else if (state.getName() == GroceryState.Name.GOING_TO_PAY || state.getName() == GroceryState.Name.GOING_TO_SERVICE || state.getName() == GroceryState.Name.GOING_TO_EAT) {
                        if (action.getName() == GroceryAction.Name.GO_TO_CHECKOUT || action.getName() == GroceryAction.Name.GO_TO_CUSTOMER_SERVICE || action.getName() == GroceryAction.Name.GO_TO_FOOD_STALL) {
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
                            if (agentMovement.getGoalAmenity() != null) {
//                                if (agentMovement.getLeaderAgent() == null && !agentMovement.isInteracting()) {
//                                    agentMovement.forceActionInteraction(someAgent, GroceryAgentMovement.InteractionType.EXCHANGE, agentMovement.getDuration());
//                                }

                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                if (agentMovement.getCurrentAction().getDuration() <= 0) {
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
                    else if (state.getName() == GroceryState.Name.EATING) {
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
                    else if (state.getName() == GroceryState.Name.GOING_HOME) {
                        if (action.getName() == GroceryAction.Name.GO_TO_RECEIPT) {
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
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                        else if (action.getName() == GroceryAction.Name.LEAVE_BUILDING) {
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
                    GroceryAgent groceryAgent = (GroceryAgent) otherAgent;
                    if (!groceryAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), groceryAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(groceryAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), groceryAgent.getAgentMovement().getProposedHeading(), groceryAgent.getAgentMovement().getFieldOfViewAngle())){
                                agentMovement.rollAgentInteraction(groceryAgent);
                                if (agentMovement.isInteracting()){ // interaction was successful
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
            patches = agentMovement.get3x3Field(agentMovement.getHeading(), true, Math.toRadians(270));
            for (Patch patch: patches){
                for (Agent otherAgent: patch.getAgents()){
                    GroceryAgent groceryAgent = (GroceryAgent) otherAgent;
                    if (!groceryAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), groceryAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
                            if (Coordinates.isWithinFieldOfView(groceryAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), groceryAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
                                agentMovement.rollAgentInteraction(groceryAgent);
                                if (agentMovement.isInteracting()){ // interaction was successful
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
        }
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
            if (isFamily && totalAloneCustomerCount < MAX_FAMILY && currentFamilyCount < MAX_CURRENT_FAMILY) {
                if (grocery.getUnspawnedFamilyAgents().size() > 0){
                    int randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(grocery.getUnspawnedFamilyAgents().size());
                    GroceryAgent leaderAgent = grocery.getUnspawnedFamilyAgents().get(randNum);
                    while(!leaderAgent.isLeader()) {
                        randNum = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(grocery.getUnspawnedFamilyAgents().size());
                        leaderAgent = grocery.getUnspawnedFamilyAgents().get(randNum);
                    }

                    if (leaderAgent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER) { // Complete Family
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
                    else if (leaderAgent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER) { // Help Family
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
                    else { // Duo Family
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
            else if (!isFamily && totalAloneCustomerCount < MAX_ALONE && currentAloneCustomerCount < MAX_CURRENT_ALONE) {
                if (grocery.getUnspawnedAloneAgents().size() > 0){
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
}