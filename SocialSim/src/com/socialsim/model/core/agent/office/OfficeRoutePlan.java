package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Cubicle;
import com.socialsim.model.simulator.Simulator;
import java.util.*;

public class OfficeRoutePlan {

    private OfficeState currentState;
    private ArrayList<OfficeState> routePlan;
    private boolean fromBathPM, fromBathAM, isAtDesk;
    private int lastDuration = -1;
    private int canUrgent = 2;
    private long collaborationEnd = 0, meetingStart = -1, meetingEnd, meetingRoom;

    private int BATH_AM = 2, BATH_PM = 2, BATH_LUNCH = 1;
    private int PRINT_BUSINESS = 5, PRINT_RESEARCH = 2;
    private int TECHNICAL_PRINTER_COUNT = 0, TECHNICAL_CUBICLE_COUNT = 0;
    private int COLLABORATE_COUNT = 0, BREAK_COUNT = 0;
    private int DISPENSER_LUNCH = 1, DISPENSER_PM = 1;
    private int REFRIGERATOR_LUNCH = 1, REFRIGERATOR_PM = 1;
    private Cubicle agentCubicle;

    private Amenity.AmenityBlock lunchAttractor;
    private Amenity lunchAmenity;

    OfficeState LUNCH_INSTANCE = null;

    public static final double APP_BOSS_LUNCH = 0.7;
    public static final double INT_LUNCH = 0.3;
    public static final double EXT_LUNCH = 1.0;
    public static final double INT_BUSINESS_COOPERATE = 0.6;
    public static final double EXT_BUSINESS_COOPERATE = 0.9;
    public static final double INT_RESEARCHER_COOPERATE = 0.6;
    public static final double EXT_RESEARCHER_COOPERATE = 0.9;
    public static final double BATH_CHANCE = 0.15, PRINT_CHANCE = 0.1,
                               TECHNICAL_CUBICLE_CHANCE = 0.1, TECHNICAL_PRINTER_CHANCE = 0.1,
                               DISPENSER_CHANCE = 0.1, REFRIGERATOR_CHANCE = 0.3, BREAK_CHANCE = 0.1;

    public static ArrayList<ArrayList<Long>> meetingTimes = new ArrayList<>();

    public OfficeRoutePlan(OfficeAgent agent, Office office, Patch spawnPatch, int tickEntered, int team, Cubicle assignedCubicle) {
        this.routePlan = new ArrayList<>();
        ArrayList<OfficeAction> actions;

        if(meetingTimes.size() == 0){
            long start = 0, end = 0;

            start = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(600 - 300 + 1) + 300;
            end = (Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1440 - 720 + 1) + 720) + start;

            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,1L)));
            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,2L)));
            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,3L)));

            start = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2690 - 2550 + 1) + 2550;
            end = (Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1440 - 720 + 1) + 720) + start;

            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,1L)));
            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,2L)));
            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,3L)));

            start = end + 60;
            end = (Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1440 - 720 + 1) + 720) + start;

            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,1L)));
            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,2L)));
            meetingTimes.add(new ArrayList<Long>(Arrays.asList(start,end,3L)));

            Collections.shuffle(meetingTimes, new Random());
        }

        if(team > 0){
            meetingStart = meetingTimes.get(team-1).get(0);
            meetingEnd = meetingTimes.get(team-1).get(1);
            meetingRoom = meetingTimes.get(team-1).get(2);
        }

        if (agent.getPersona() == OfficeAgent.Persona.GUARD) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GUARD_STAY_PUT, spawnPatch, 5760));
            routePlan.add(new OfficeState(OfficeState.Name.GUARD, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.JANITOR) {
            actions = new ArrayList<>();
            Patch randomToilet = office.getToilets().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAmenityBlocks().get(0).getPatch();
            actions.add(new OfficeAction(OfficeAction.Name.JANITOR_CLEAN_TOILET, randomToilet, 10));
            routePlan.add(new OfficeState(OfficeState.Name.MAINTENANCE_BATHROOM, this, agent, actions));
            actions = new ArrayList<>();
            Patch randomPlant = office.getPlants().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(9)).getAmenityBlocks().get(0).getPatch();
            actions.add(new OfficeAction(OfficeAction.Name.JANITOR_WATER_PLANT, randomPlant, 10));
            routePlan.add(new OfficeState(OfficeState.Name.MAINTENANCE_PLANT, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.CLIENT) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_COUCH, 60, 180));
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_OFFICE, office.getChairs().get(1).getAmenityBlocks().get(0).getPatch(),360, 720));
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            routePlan.add(new OfficeState(OfficeState.Name.CLIENT, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.DRIVER) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.DRIVER_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            actions.add(new OfficeAction(OfficeAction.Name.DRIVER_GO_COUCH, 60, 180));
            actions.add(new OfficeAction(OfficeAction.Name.DRIVER_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            routePlan.add(new OfficeState(OfficeState.Name.DRIVER, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.VISITOR) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.VISITOR_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            actions.add(new OfficeAction(OfficeAction.Name.VISITOR_GO_OFFICE, office.getChairs().get(5).getAmenityBlocks().get(0).getPatch(),360, 2160));
            routePlan.add(new OfficeState(OfficeState.Name.VISITOR, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.RECEPTIONIST) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.RECEPTIONIST_STAY_PUT, office.getChairs().get(2).getAmenityBlocks().get(0).getPatch(), 5760));
            routePlan.add(new OfficeState(OfficeState.Name.RECEPTIONIST, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.SECRETARY) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getChairs().get(3).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.SECRETARY_STAY_PUT, office.getChairs().get(3).getAttractors().get(0).getPatch(), 360, 720));
            actions.add(new OfficeAction(OfficeAction.Name.SECRETARY_CHECK_CABINET, 12, 36));
            routePlan.add(new OfficeState(OfficeState.Name.SECRETARY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, office.getChairs().get(3).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            actions.add(new OfficeAction(OfficeAction.Name.EXIT_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            this.LUNCH_INSTANCE = routePlan.get(routePlan.size()-1);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getChairs().get(3).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.SECRETARY_STAY_PUT, office.getChairs().get(3).getAttractors().get(0).getPatch(), 360, 720));
            actions.add(new OfficeAction(OfficeAction.Name.SECRETARY_CHECK_CABINET, 12, 36));
            routePlan.add(new OfficeState(OfficeState.Name.SECRETARY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.PROFESSIONAL_BOSS || agent.getPersona() == OfficeAgent.Persona.APPROACHABLE_BOSS) {
            setFromBathAM(false);
            setFromBathPM(false);
            setAtDesk(false);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.
                    getChairs().get(4).getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, office.
                    getChairs().get(4).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.
                    getChairs().get(4).getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.INT_BUSINESS || agent.getPersona() == OfficeAgent.Persona.EXT_BUSINESS ||
                agent.getPersona() == OfficeAgent.Persona.INT_RESEARCHER || agent.getPersona() == OfficeAgent.Persona.EXT_RESEARCHER) {
            setFromBathAM(false);
            setFromBathPM(false);
            setCOLLABORATE_COUNT(2);
            setAtDesk(false);
            setAgentCubicle(assignedCubicle);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, assignedCubicle.getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));
            this.LUNCH_INSTANCE = routePlan.get(routePlan.size()-1);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch(),120,360));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.INT_TECHNICAL || agent.getPersona() == OfficeAgent.Persona.EXT_TECHNICAL) {
            setFromBathAM(false);
            setFromBathPM(false);
            setTECHNICAL_PRINTER_COUNT(-1);
            setTECHNICAL_CUBICLE_COUNT(-1);
            setAtDesk(false);
            setAgentCubicle(assignedCubicle);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, assignedCubicle.getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));
            this.LUNCH_INSTANCE = routePlan.get(routePlan.size()-1);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch(),120,360));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.MANAGER) {
            setFromBathAM(false);
            setFromBathPM(false);
            setCOLLABORATE_COUNT(2);
            setAtDesk(false);
            setAgentCubicle(assignedCubicle);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, assignedCubicle.getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            actions.add(new OfficeAction(OfficeAction.Name.EXIT_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));
            this.LUNCH_INSTANCE = routePlan.get(routePlan.size()-1);

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch(),120,360));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));
        }

        setNextState(-1);
    }

    public OfficeState setNextState(int i) {
        this.currentState = this.routePlan.get(i+1);
        return this.currentState;
    }

    public OfficeState setPreviousState(int i){
        this.currentState = this.routePlan.get(i-1);
        return this.currentState;
    }

    public ArrayList<OfficeState> getCurrentRoutePlan() {
        return routePlan;
    }

    public OfficeState getCurrentState() {
        return currentState;
    }

    public Cubicle getAgentCubicle() {
        return agentCubicle;
    }

    public void setAgentCubicle(Cubicle agentCubicle) {
        this.agentCubicle = agentCubicle;
    }

    public OfficeState addUrgentRoute(String s, OfficeAgent agent){
        ArrayList<OfficeAction> actions;
        OfficeState officeState;

        switch (s) {
            case "BATHROOM" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_BATHROOM));
                actions.add(new OfficeAction(OfficeAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                actions.add(new OfficeAction(OfficeAction.Name.FIND_SINK));
                actions.add(new OfficeAction(OfficeAction.Name.WASH_IN_SINK, 12));
                officeState = new OfficeState(OfficeState.Name.NEEDS_BATHROOM, this, agent, actions);
            }
            case "COLLABORATION" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_COLLAB, 60));
                actions.add(new OfficeAction(OfficeAction.Name.COLLABORATE, 60, 300));
                officeState = new OfficeState(OfficeState.Name.NEEDS_COLLAB, this, agent, actions);
            }
            case "PRINT" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_PRINTER));
                //actions.add(new OfficeAction(OfficeAction.Name.QUEUE_PRINTER));
                actions.add(new OfficeAction(OfficeAction.Name.PRINTING, 4, 36));
                officeState = new OfficeState(OfficeState.Name.NEEDS_PRINT, this, agent, actions);
            }
            case "INQUIRE_BOSS" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_BOSS));
                actions.add(new OfficeAction(OfficeAction.Name.ASK_BOSS));
                officeState = new OfficeState(OfficeState.Name.INQUIRE_BOSS, this, agent, actions);
            }
            case "INQUIRE_WORKER" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_WORKER));
                actions.add(new OfficeAction(OfficeAction.Name.ASK_WORKER));
                officeState = new OfficeState(OfficeState.Name.INQUIRE_WORKER, this, agent, actions);
            }
            case "INQUIRE_MANAGER" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_MANAGER));
                actions.add(new OfficeAction(OfficeAction.Name.ASK_MANAGER));
                officeState = new OfficeState(OfficeState.Name.INQUIRE_MANAGER, this, agent, actions);
            }
            case "TECHNICAL_PRINTER" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.TECHNICAL_GO_PRINTER, 12, 120));
                actions.add(new OfficeAction(OfficeAction.Name.FIX_PRINTER));
                officeState = new OfficeState(OfficeState.Name.NEEDS_FIX_PRINTER, this, agent, actions);
            }
            case "DISPENSER" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GOING_DISPENSER));
                actions.add(new OfficeAction(OfficeAction.Name.GETTING_WATER, 2, 8));
                officeState = new OfficeState(OfficeState.Name.DISPENSER, this, agent, actions);
            }
            case "REFRIGERATOR" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GOING_FRIDGE));
                actions.add(new OfficeAction(OfficeAction.Name.GETTING_FOOD, 2, 10));
                officeState = new OfficeState(OfficeState.Name.REFRIGERATOR, this, agent, actions);
            }
            case "BREAK" -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH));
                actions.add(new OfficeAction(OfficeAction.Name.TAKING_BREAK, getAgentCubicle().getAttractors().get(0).getPatch(), 120, 240));
                officeState = new OfficeState(OfficeState.Name.BREAK_TIME, this, agent, actions);
            }
            default -> {
                actions = new ArrayList<>();
                actions.add(new OfficeAction(OfficeAction.Name.GO_MEETING));
                actions.add(new OfficeAction(OfficeAction.Name.WAIT_MEETING, 60));
                actions.add(new OfficeAction(OfficeAction.Name.MEETING));
                officeState = new OfficeState(OfficeState.Name.MEETING, this, agent, actions);
            }
        }

        return officeState;
    }

    public OfficeState addUrgentRoute(OfficeAgent agent, Office office) {
        ArrayList<OfficeAction> actions;

        actions = new ArrayList<>();
        Patch randomCubicle = office.getCubicles().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(60)).
                getAmenityBlocks().get(0).getPatch();
        actions.add(new OfficeAction(OfficeAction.Name.FIX_CUBICLE, randomCubicle, 120));
        return new OfficeState(OfficeState.Name.NEEDS_FIX_CUBICLE, this, agent, actions);
    }

    public boolean isFromBathPM() {
        return fromBathPM;
    }

    public void setFromBathPM(boolean fromBathPM) {
        this.fromBathPM = fromBathPM;
    }

    public boolean isFromBathAM() {
        return fromBathAM;
    }

    public void setFromBathAM(boolean fromBathAM) {
        this.fromBathAM = fromBathAM;
    }

    public int getBATH_AM() {
        return BATH_AM;
    }

    public void setBATH_AM(int BATH_AM) {
        this.BATH_AM -= BATH_AM;
    }

    public int getBATH_PM() {
        return BATH_PM;
    }

    public void setBATH_PM(int BATH_PM) {
        this.BATH_PM -= BATH_PM;
    }

    public int getBATH_LUNCH() {
        return BATH_LUNCH;
    }

    public void setBATH_LUNCH(int BATH_LUNCH) {
        this.BATH_LUNCH -= BATH_LUNCH;
    }

    public int getPRINT_BUSINESS() {
        return PRINT_BUSINESS;
    }

    public void setPRINT_BUSINESS() {
        this.PRINT_BUSINESS -= 1;
    }

    public int getPRINT_RESEARCH() {
        return PRINT_RESEARCH;
    }

    public void setPRINT_RESEARCH() {
        this.PRINT_RESEARCH -= 1;
    }

    public int getDISPENSER_LUNCH(){return this.DISPENSER_LUNCH;}

    public int getDISPENSER_PM(){return this.DISPENSER_PM;}

    public int getREFRIGERATOR_LUNCH(){return this.REFRIGERATOR_LUNCH;}

    public int getREFRIGERATOR_PM(){return this.REFRIGERATOR_PM;}

    public void setDISPENSER_LUNCH(int DISPENSER_LUNCH) {
        this.DISPENSER_LUNCH = DISPENSER_LUNCH;
    }

    public void setDISPENSER_PM(int DISPENSER_PM) {
        this.DISPENSER_PM = DISPENSER_PM;
    }

    public void setREFRIGERATOR_LUNCH(int REFRIGERATOR_LUNCH) {
        this.REFRIGERATOR_LUNCH = REFRIGERATOR_LUNCH;
    }

    public void setREFRIGERATOR_PM(int REFRIGERATOR_PM) {
        this.REFRIGERATOR_PM = REFRIGERATOR_PM;
    }

    public int getLastDuration() {
        return lastDuration;
    }

    public void setLastDuration(int lastDuration) {
        this.lastDuration = lastDuration;
    }

    public int getCanUrgent() {
        return canUrgent;
    }

    public void setCanUrgent(int canUrgent) {
        this.canUrgent += canUrgent;
    }

    public void resetCanUrgent(){this.canUrgent = 2;}

    public int getTECHNICAL_PRINTER_COUNT() {
        return TECHNICAL_PRINTER_COUNT;
    }

    public void setTECHNICAL_PRINTER_COUNT(int num) {
        this.TECHNICAL_PRINTER_COUNT -= num;
    }

    public int getTECHNICAL_CUBICLE_COUNT() {
        return TECHNICAL_CUBICLE_COUNT;
    }

    public void setTECHNICAL_CUBICLE_COUNT(int num) {
        this.TECHNICAL_CUBICLE_COUNT -= num;
    }

    public int getCOLLABORATE_COUNT() {
        return COLLABORATE_COUNT;
    }

    public void setCOLLABORATE_COUNT(int count) {
        this.COLLABORATE_COUNT += count;
    }

    public long getCollaborationEnd() {
        return collaborationEnd;
    }

    public void setCollaborationEnd(long tick, int duration) {
        this.collaborationEnd = tick + duration;
    }

    public long getMeetingStart() {
        return meetingStart;
    }

    public long getMeetingEnd() {
        return meetingEnd;
    }

    public int getMeetingRoom() {
        return (int) meetingRoom;
    }

    public Amenity.AmenityBlock getLunchAttractor() {
        return lunchAttractor;
    }

    public void setLunchAttractor(Amenity.AmenityBlock lunchAttractor) {
        this.lunchAttractor = lunchAttractor;
    }

    public Amenity getLunchAmenity() {
        return lunchAmenity;
    }

    public void setLunchAmenity(Amenity lunchAmenity) {
        this.lunchAmenity = lunchAmenity;
    }

    public OfficeState getLUNCH_INSTANCE() {
        return LUNCH_INSTANCE;
    }

    public boolean isAtDesk() {
        return isAtDesk;
    }

    public void setAtDesk(boolean atDesk) {
        isAtDesk = atDesk;
    }

    public int getBREAK_COUNT() {
        return BREAK_COUNT;
    }

    public void setBREAK_COUNT(int BREAK_COUNT) {
        this.BREAK_COUNT -= BREAK_COUNT;
    }

    public double getCooperate(OfficeAgent.Persona persona){

        double chance = 0;

        switch (persona){
            case EXT_BUSINESS -> chance = EXT_BUSINESS_COOPERATE;
            case MANAGER -> chance = 1.0;
            case INT_BUSINESS -> chance = INT_BUSINESS_COOPERATE;
            case EXT_RESEARCHER -> chance = EXT_RESEARCHER_COOPERATE;
            case INT_RESEARCHER -> chance = INT_RESEARCHER_COOPERATE;
            default -> chance = 0;
        }

        return chance;
    }
    public OfficeState addWaitingRoute(OfficeAgent agent){
        ArrayList<OfficeAction> actions;
        actions = new ArrayList<>();
        actions.add(new OfficeAction(OfficeAction.Name.GO_TO_WAIT_AREA));
        actions.add(new OfficeAction(OfficeAction.Name.WAIT_FOR_VACANT,5,20));
        return new OfficeState(OfficeState.Name.WAIT_INFRONT_OF_BATHROOM,this, agent, actions);
    }

}