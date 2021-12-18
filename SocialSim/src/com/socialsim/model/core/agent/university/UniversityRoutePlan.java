package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Fountain;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Security;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Staircase;
import com.socialsim.model.simulator.Simulator;

import java.util.*;

public class UniversityRoutePlan {

    private ListIterator<State> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private State currentState; // Denotes the current class of the amenity/patchfield in the route plan

    //TODO: Maybe move this into another class that is static
    private static final int MAX_CLASSES = 6;
    private static final int MAX_CLASSROOMS = 6;
    private static final int MAX_JANITOR_ROUNDS = 6;

    public UniversityRoutePlan(UniversityAgent agent) {
        List<State> routePlan = new ArrayList<>();


        if (agent.getPersona() == UniversityAgent.Persona.GUARD){
            routePlan.add(new State(State.Name.GUARD, this, agent));
        }
        else if (agent.getPersona() == UniversityAgent.Persona.JANITOR){
            for(int i = 0; i < Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_JANITOR_ROUNDS); i++){
                routePlan.add(new State(State.Name.MAINTENANCE_BATHROOM, this, agent));
                routePlan.add(new State(State.Name.MAINTENANCE_FOUNTAIN, this, agent));
            }
        }
        else {
            int CALCULATED_CLASSES, LUNCH_TIME;
            ArrayList<Integer> classes = new ArrayList<>();
            if (agent.getAgentMovement().getTickEntered() < 60){ // based on 1 tick = 1 minute
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES);
                LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES);
                    if (!classes.contains(x) && x != LUNCH_TIME){
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            }
            else if (agent.getAgentMovement().getTickEntered() < 165){
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 1);
                LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 1) + 1;
                    if (!classes.contains(x) && x != LUNCH_TIME){
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            }
            else if (agent.getAgentMovement().getTickEntered() < 270){
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2);
                LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2) + 2;
                    if (!classes.contains(x) && x != LUNCH_TIME){
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            }
            else if (agent.getAgentMovement().getTickEntered() < 375){
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2);
                if (CALCULATED_CLASSES == MAX_CLASSES - 2 - 1)
                    LUNCH_TIME = -1;
                else
                    LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 3;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 3) + 3;
                    if (!classes.contains(x) && x != LUNCH_TIME){
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            }
            else if (agent.getAgentMovement().getTickEntered() < 480){
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 3);
                if (CALCULATED_CLASSES == MAX_CLASSES - 3 - 1)
                    LUNCH_TIME = -1;
                else
                    LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1) + 4;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 4) + 4;
                    if (!classes.contains(x) && x != LUNCH_TIME){
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            }
            else{
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 4);
                LUNCH_TIME = -1;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 5) + 5;
                    classes.add(x);
                    ctrClasses--;
                }
            }
            if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){

                //Chances of INT Y1-Y4
                final int CHANCE_WANDERING_AROUND = 22, CHANCE_GOING_TO_STUDY = 58,
                        NEED_BATHROOM_NO_CLASSES = 10, NEEDS_DRINK_NO_CLASSES = 10,
                        CHANCE_NEEDS_BATHROOM_STUDYING = 5, CHANCE_NEEDS_DRINK_STUDYING = 5;

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++){
                    for (int j = 0; j < 5; j++){
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < CHANCE_WANDERING_AROUND)
                            routePlan.add(new State(State.Name.WANDERING_AROUND, this, agent));
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY){
                            routePlan.add(new State(State.Name.GOING_TO_STUDY, this, agent));
                        }
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY + NEED_BATHROOM_NO_CLASSES){
                            routePlan.add(new State(State.Name.NEEDS_BATHROOM, this, agent));
                        }
                        else{
                            routePlan.add(new State(State.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    State newClass;
                    State newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 60, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 165, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 270, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 375, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 480, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 585, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME){
                        routePlan.add(new State(State.Name.GOING_TO_LUNCH, this, agent));
                        routePlan.add(new State(State.Name.EATING_LUNCH, this, agent));
                    }
                }
            }
            else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                //Chances of INT ORG Y1-Y4
                final int CHANCE_WANDERING_AROUND = 22, CHANCE_GOING_TO_STUDY = 58,
                        NEED_BATHROOM_NO_CLASSES = 10, NEEDS_DRINK_NO_CLASSES = 10,
                        CHANCE_NEEDS_BATHROOM_STUDYING = 5, CHANCE_NEEDS_DRINK_STUDYING = 5;

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++){
                    for (int j = 0; j < 5; j++){
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < CHANCE_WANDERING_AROUND)
                            routePlan.add(new State(State.Name.WANDERING_AROUND, this, agent));
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY){
                            routePlan.add(new State(State.Name.GOING_TO_STUDY, this, agent));
                        }
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY + NEED_BATHROOM_NO_CLASSES){
                            routePlan.add(new State(State.Name.NEEDS_BATHROOM, this, agent));
                        }
                        else{
                            routePlan.add(new State(State.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    State newClass;
                    State newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 60, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 165, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 270, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 375, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 480, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 585, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME){
                        routePlan.add(new State(State.Name.GOING_TO_LUNCH, this, agent));
                        routePlan.add(new State(State.Name.EATING_LUNCH, this, agent));
                    }
                }
            }
            else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT){
                //Chances of EXT Y1-Y4
                final int CHANCE_WANDERING_AROUND = 40, CHANCE_GOING_TO_STUDY = 40,
                        NEED_BATHROOM_NO_CLASSES = 10, NEEDS_DRINK_NO_CLASSES = 10,
                        CHANCE_NEEDS_BATHROOM_STUDYING = 5, CHANCE_NEEDS_DRINK_STUDYING = 5;

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++){
                    for (int j = 0; j < 5; j++){
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < CHANCE_WANDERING_AROUND)
                            routePlan.add(new State(State.Name.WANDERING_AROUND, this, agent));
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY){
                            routePlan.add(new State(State.Name.GOING_TO_STUDY, this, agent));
                        }
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY + NEED_BATHROOM_NO_CLASSES){
                            routePlan.add(new State(State.Name.NEEDS_BATHROOM, this, agent));
                        }
                        else{
                            routePlan.add(new State(State.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    State newClass;
                    State newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 60, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 165, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 270, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 375, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 480, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 585, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME){
                        routePlan.add(new State(State.Name.GOING_TO_LUNCH, this, agent));
                        routePlan.add(new State(State.Name.EATING_LUNCH, this, agent));
                    }
                }
            }
            else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                //Chances of EXT ORG Y1-Y4
                final int CHANCE_WANDERING_AROUND = 48, CHANCE_GOING_TO_STUDY = 32,
                        NEED_BATHROOM_NO_CLASSES = 10, NEEDS_DRINK_NO_CLASSES = 10,
                        CHANCE_NEEDS_BATHROOM_STUDYING = 5, CHANCE_NEEDS_DRINK_STUDYING = 5;

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++){
                    for (int j = 0; j < 5; j++){
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < CHANCE_WANDERING_AROUND)
                            routePlan.add(new State(State.Name.WANDERING_AROUND, this, agent));
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY){
                            routePlan.add(new State(State.Name.GOING_TO_STUDY, this, agent));
                        }
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY + NEED_BATHROOM_NO_CLASSES){
                            routePlan.add(new State(State.Name.NEEDS_BATHROOM, this, agent));
                        }
                        else{
                            routePlan.add(new State(State.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    State newClass;
                    State newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 60, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 165, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 270, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 375, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 480, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_STUDENT, this, agent, 585, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME){
                        routePlan.add(new State(State.Name.GOING_TO_LUNCH, this, agent));
                        routePlan.add(new State(State.Name.EATING_LUNCH, this, agent));
                    }
                }
            }
            else{
                //Chances of PROF
                final int CHANCE_WANDERING_AROUND = 80, CHANCE_GOING_TO_STUDY = 10,
                        NEED_BATHROOM_NO_CLASSES = 10, NEEDS_DRINK_NO_CLASSES = 0,
                        CHANCE_NEEDS_BATHROOM_STUDYING = 5, CHANCE_NEEDS_DRINK_STUDYING = 5;

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++){
                    for (int j = 0; j < 5; j++){
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < CHANCE_WANDERING_AROUND)
                            routePlan.add(new State(State.Name.WANDERING_AROUND, this, agent));
                        else if (x < CHANCE_WANDERING_AROUND + CHANCE_GOING_TO_STUDY){
                            routePlan.add(new State(State.Name.GOING_TO_STUDY, this, agent));
                        }
                        else{
                            routePlan.add(new State(State.Name.NEEDS_BATHROOM, this, agent));
                        }
                    }
                    State newClass;
                    State newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 60, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 165, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 270, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 375, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 480, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            newClass = new State(State.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 585, Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS));
                            newInClass = new State(State.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME){
                        routePlan.add(new State(State.Name.GOING_TO_LUNCH, this, agent));
                        routePlan.add(new State(State.Name.EATING_LUNCH, this, agent));
                    }
                }
            }
        }

        routePlan.add(new State(State.Name.GOING_HOME, this, agent));
        this.currentRoutePlan = routePlan.listIterator();
    }

    public void setNextState() { // Set the next class in the route plan
        this.currentState = this.currentRoutePlan.next();
    }
    public void setPreviousState(){
        this.currentState = this.currentRoutePlan.previous();
    }

    public ListIterator<State> getCurrentRoutePlan() {
        return currentRoutePlan;
    }

    public State getCurrentClass() {
        return currentState;
    }
    public void addUrgentRoute(State s){
        this.currentState = s;
    }

}