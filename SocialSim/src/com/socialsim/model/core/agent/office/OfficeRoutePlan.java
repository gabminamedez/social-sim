package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Cubicle;
import com.socialsim.model.simulator.Simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class OfficeRoutePlan {

    private ListIterator<OfficeState> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private OfficeState currentState; // Denotes the current class of the amenity/patchfield in the route plan

    public OfficeRoutePlan(OfficeAgent agent, Office office, Patch spawnPatch, int tickEntered, int team, Cubicle assignedCubicle) {
        List<OfficeState> routePlan = new ArrayList<>();
        ArrayList<OfficeAction> actions;

        if (agent.getPersona() == OfficeAgent.Persona.GUARD) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GUARD_STAY_PUT, spawnPatch, 5760));
            routePlan.add(new OfficeState(OfficeState.Name.GUARD, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.JANITOR) {
            actions = new ArrayList<>();
            Patch randomToilet = office.getToilets().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3)).getAmenityBlocks().get(0).getPatch();
            Patch doorPatch = office.getDoors().get(0).getAmenityBlocks().get(0).getPatch();
            actions.add(new OfficeAction(OfficeAction.Name.JANITOR_GO_TOILET, doorPatch));
            actions.add(new OfficeAction(OfficeAction.Name.JANITOR_CLEAN_TOILET, randomToilet, 10));
            routePlan.add(new OfficeState(OfficeState.Name.MAINTENANCE_BATHROOM, this, agent, actions));
            actions = new ArrayList<>();
            Patch randomPlant = office.getPlants().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(9)).getAmenityBlocks().get(0).getPatch();
            actions.add(new OfficeAction(OfficeAction.Name.JANITOR_GO_PLANT, doorPatch));
            actions.add(new OfficeAction(OfficeAction.Name.JANITOR_WATER_PLANT, randomPlant, 10));
            routePlan.add(new OfficeState(OfficeState.Name.MAINTENANCE_PLANT, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.CLIENT) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_COUCH, 60, 180));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_OFFICE, office.getChairs().get(1).getAmenityBlocks().get(0).getPatch(),360, 720));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.CLIENT_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            routePlan.add(new OfficeState(OfficeState.Name.CLIENT, this, agent, actions));
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
        }
        else if (agent.getPersona() == OfficeAgent.Persona.VISITOR) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.VISITOR_GO_RECEPTIONIST, office.getReceptionTables().get(0).getAmenityBlocks().get(2).getPatch(), 12, 24));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.VISITOR_GO_OFFICE, office.getChairs().get(5).getAmenityBlocks().get(0).getPatch(),360, 2160));
            routePlan.add(new OfficeState(OfficeState.Name.VISITOR, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.RECEPTIONIST) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.RECEPTIONIST_STAY_PUT, office.getChairs().get(2).getAmenityBlocks().get(0).getPatch(), 5760));
            routePlan.add(new OfficeState(OfficeState.Name.RECEPTIONIST, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.SECRETARY) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.SECRETARY_STAY_PUT, office.getChairs().get(3).getAttractors().get(0).getPatch(), 360, 720));
            actions.add(new OfficeAction(OfficeAction.Name.SECRETARY_CHECK_CABINET, 12, 36));
            routePlan.add(new OfficeState(OfficeState.Name.SECRETARY, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.INT_BUSINESS || agent.getPersona() == OfficeAgent.Persona.EXT_BUSINESS || agent.getPersona() == OfficeAgent.Persona.INT_RESEARCHER || agent.getPersona() == OfficeAgent.Persona.EXT_RESEARCHER) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            if (agent.getPersona() == OfficeAgent.Persona.EXT_BUSINESS || agent.getPersona() == OfficeAgent.Persona.EXT_RESEARCHER) {
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            }
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            if (agent.getPersona() == OfficeAgent.Persona.EXT_BUSINESS || agent.getPersona() == OfficeAgent.Persona.EXT_RESEARCHER) {
                actions.add(new OfficeAction(OfficeAction.Name.EXIT_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            }
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.INT_TECHNICAL || agent.getPersona() == OfficeAgent.Persona.EXT_TECHNICAL) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            if (agent.getPersona() == OfficeAgent.Persona.EXT_TECHNICAL) {
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            }
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            if (agent.getPersona() == OfficeAgent.Persona.EXT_TECHNICAL) {
                actions.add(new OfficeAction(OfficeAction.Name.EXIT_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            }
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.PROFESSIONAL_BOSS || agent.getPersona() == OfficeAgent.Persona.APPROACHABLE_BOSS) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, office.getChairs().get(4).getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            if (agent.getPersona() == OfficeAgent.Persona.APPROACHABLE_BOSS) {
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
                actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            }
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            if (agent.getPersona() == OfficeAgent.Persona.APPROACHABLE_BOSS) {
                actions.add(new OfficeAction(OfficeAction.Name.EXIT_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            }
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, office.getChairs().get(4).getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));
        }
        else if (agent.getPersona() == OfficeAgent.Persona.MANAGER) {
            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new OfficeAction(OfficeAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new OfficeState(OfficeState.Name.GOING_TO_SECURITY, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.EAT_LUNCH, 180, 360));
            actions.add(new OfficeAction(OfficeAction.Name.EXIT_LUNCH, office.getDoors().get(1).getAttractors().get(1).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.EATING_LUNCH, this, agent, actions));

            actions = new ArrayList<>();
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_OFFICE_ROOM, office.getDoors().get(5).getAttractors().get(0).getPatch()));
            actions.add(new OfficeAction(OfficeAction.Name.GO_TO_STATION, assignedCubicle.getAttractors().get(0).getPatch()));
            routePlan.add(new OfficeState(OfficeState.Name.WORKING, this, agent, actions));
        }
        actions = new ArrayList<>();
        actions.add(new OfficeAction(OfficeAction.Name.LEAVE_OFFICE, office.getOfficeGates().get(0).getAmenityBlocks().get(0).getPatch()));
        routePlan.add(new OfficeState(OfficeState.Name.GOING_HOME, this, agent, actions));

        this.currentRoutePlan = routePlan.listIterator();
        setNextState();
    }

    public OfficeState setNextState() { // Set the next class in the route plan
        this.currentState = this.currentRoutePlan.next();

        return this.currentState;
    }

    public OfficeState setPreviousState(){
        this.currentState = this.currentRoutePlan.previous();

        return this.currentState;
    }

    public ListIterator<OfficeState> getCurrentRoutePlan() {
        return currentRoutePlan;
    }

    public OfficeState getCurrentState() {
        return currentState;
    }

    public void addUrgentRoute(OfficeState s){
        this.currentState = s;
    }

}