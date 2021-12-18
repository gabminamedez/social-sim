package com.socialsim.model.core.agent.university;

import java.util.ArrayList;

public class State {

    public enum Name{
        GOING_TO_SECURITY, WANDERING_AROUND, NEEDS_BATHROOM, NEEDS_DRINK,
        GOING_TO_STUDY, STUDYING, GOING_TO_CLASS_STUDENT, GOING_TO_CLASS_PROFESSOR,
        IN_CLASS_STUDENT, IN_CLASS_PROFESSOR, GOING_TO_LUNCH, EATING_LUNCH,
        GOING_HOME, GUARD, MAINTENANCE_BATHROOM, MAINTENANCE_FOUNTAIN;
    }

    private Name name;
    private UniversityRoutePlan routePlan;
    private UniversityAgent agent;
    private ArrayList<Action> actions;

    // Class-specific attributes
        private int tickClassStart;
    private int classroomID;

    public State(Name a, UniversityRoutePlan routePlan, UniversityAgent agent){
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
    }

    public State(Name a, UniversityRoutePlan routePlan, UniversityAgent agent, int tickClassStart, int classroomID){ // Class state
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.tickClassStart = tickClassStart;
        this.classroomID = classroomID;
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

    public UniversityRoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(UniversityRoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public UniversityAgent getAgent() {
        return agent;
    }

    public void setAgent(UniversityAgent agent) {
        this.agent = agent;
    }
}
