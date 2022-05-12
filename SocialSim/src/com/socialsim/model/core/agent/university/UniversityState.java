package com.socialsim.model.core.agent.university;

import java.util.ArrayList;

public class UniversityState {

    public enum Name {
        GOING_TO_SECURITY, WANDERING_AROUND, NEEDS_BATHROOM, NEEDS_DRINK,
        GOING_TO_STUDY, STUDYING, GOING_TO_CLASS_STUDENT, GOING_TO_CLASS_PROFESSOR,
        WAIT_FOR_CLASS_STUDENT, WAIT_FOR_CLASS_PROFESSOR,
        IN_CLASS_STUDENT, IN_CLASS_PROFESSOR, GOING_TO_LUNCH, EATING_LUNCH,
        GOING_HOME, GUARD, MAINTENANCE_BATHROOM, MAINTENANCE_FOUNTAIN,THROW_TRASH,
        GOING_TO_STAFF,WAIT_INFRONT_OF_CLASS
    }

    private Name name;
    private UniversityRoutePlan routePlan;
    private UniversityAgent agent;
    private ArrayList<UniversityAction> actions;
    private int tickClassStart;
    private int classroomID;

    public UniversityState(Name a, UniversityRoutePlan routePlan, UniversityAgent agent, ArrayList<UniversityAction> actions) {
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.actions = actions;
    }

    public UniversityState(Name a, UniversityRoutePlan routePlan, UniversityAgent agent, int tickClassStart, int classroomID, ArrayList<UniversityAction> actions) { // Class state
        this.name = a;
        this.routePlan = routePlan;
        this.agent = agent;
        this.tickClassStart = tickClassStart;
        this.classroomID = classroomID;
        this.actions = actions;
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

    public void setActions(ArrayList<UniversityAction> actions) {
        this.actions = actions;
    }

    public void setTickClassStart(int tickClassStart) {
        this.tickClassStart = tickClassStart;
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

    public ArrayList<UniversityAction> getActions() {
        return this.actions;
    }

    public void addAction(UniversityAction a){
        actions.add(a);
    }

    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
    }

    public int getClassroomID() {
        return classroomID;
    }

    public int getTickClassStart() {
        return tickClassStart;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("State " + name.name() + "\n");
        for (UniversityAction action: actions)
            s.append(action.getName().name() + "\n");
        return s.toString();
    }

}