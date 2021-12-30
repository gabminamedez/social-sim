package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.agent.university.UniversityAgent;

import java.util.ArrayList;

public class OfficeState {

    public enum Name{
        GOING_TO_SECURITY, WANDERING_AROUND, NEEDS_BATHROOM, NEEDS_DRINK,
        GOING_TO_STUDY, STUDYING, GOING_TO_CLASS_STUDENT, GOING_TO_CLASS_PROFESSOR,
        WAIT_FOR_CLASS_STUDENT, WAIT_FOR_CLASS_PROFESSOR,
        IN_CLASS_STUDENT, IN_CLASS_PROFESSOR, GOING_TO_LUNCH, EATING_LUNCH,
        GOING_HOME, GUARD, MAINTENANCE_BATHROOM, MAINTENANCE_FOUNTAIN;
    }

    private Name name;
    private OfficeRoutePlan routePlan;
    private UniversityAgent agent;
    private ArrayList<OfficeAction> actions;

    // Class-specific attributes
    private int tickClassStart;
    private int classroomID;

    public OfficeState(Name a, OfficeRoutePlan routePlan, UniversityAgent agent){
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = new ArrayList<>();
    }
    public OfficeState(Name a, OfficeRoutePlan routePlan, UniversityAgent agent, ArrayList<OfficeAction> actions){
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = actions;
    }

    public OfficeState(Name a, OfficeRoutePlan routePlan, UniversityAgent agent, int tickClassStart, int classroomID){ // Class state
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

    public UniversityAgent getAgent() {
        return agent;
    }

    public void setAgent(UniversityAgent agent) {
        this.agent = agent;
    }

    public void addAction(OfficeAction a){
        actions.add(a);
    }
}
