package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.agent.university.UniversityAction;

import java.util.ArrayList;

public class OfficeState {

    public enum Name {
        GOING_TO_SECURITY, NEEDS_BATHROOM, NEEDS_PRINT, NEEDS_COLLAB, NEEDS_FIX_PRINTER, NEEDS_FIX_CUBICLE,
        GOING_TO_MEETING, MEETING, GOING_TO_WORK, WORKING, GOING_TO_LUNCH, EATING_LUNCH, GOING_HOME,
        GUARD, RECEPTIONIST, SECRETARY, MAINTENANCE_BATHROOM, MAINTENANCE_PLANT, CLIENT, DRIVER, VISITOR,
        INQUIRE_BOSS, INQUIRE_WORKER, INQUIRE_MANAGER, ANSWER_BOSS, ANSWER_WORKER, ANSWER_MANAGER
    }

    private Name name;
    private OfficeRoutePlan routePlan;
    private OfficeAgent agent;
    private ArrayList<OfficeAction> actions;

    // Class-specific attributes
    private int tickClassStart;
    private int classroomID;

    public OfficeState(Name a, OfficeRoutePlan routePlan, OfficeAgent agent) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = new ArrayList<>();
    }

    public OfficeState(Name a, OfficeRoutePlan routePlan, OfficeAgent agent, ArrayList<OfficeAction> actions) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = actions;
    }

    public OfficeState(Name a, OfficeRoutePlan routePlan, OfficeAgent agent, int tickClassStart, int classroomID) { // Class state
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.tickClassStart = tickClassStart;
        this.classroomID = classroomID;
        this.actions = new ArrayList<>();
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public int getTickStart() {
        return tickClassStart;
    }

    public void setTickStart(int tickStart) {
        this.tickClassStart = tickStart;
    }

    public OfficeRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(OfficeRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public OfficeAgent getAgent() {
        return agent;
    }

    public void setAgent(OfficeAgent agent) {
        this.agent = agent;
    }

    public ArrayList<OfficeAction> getActions() {
        return this.actions;
    }

    public void addAction(OfficeAction a){
        actions.add(a);
    }
}
