package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.simulator.Simulator;

import java.util.*;

public class UniversityRoutePlan {

    private ListIterator<UniversityState> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private UniversityState currentState; // Denotes the current class of the amenity/patchfield in the route plan

    //TODO: Maybe move this into another class that is static
    private static final int MAX_CLASSES = 6;
    private static final int MAX_CLASSROOMS = 6;
    private static final int MAX_JANITOR_ROUNDS = 6;
    private static int CLASSROOM_SIZES_STUDENT[][] = new int[][]{{40 ,48, 40, 40, 40, 40},{40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}};
    private static int CLASSROOM_SIZES_PROF[][] = new int[][]{{1, 1, 1, 1, 1, 1},{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};


    //Chances of INT Y1-Y4
    public static final int INT_CHANCE_WANDERING_AROUND = 22, INT_CHANCE_GOING_TO_STUDY = 58,
            INT_NEED_BATHROOM_NO_CLASSES = 10, INT_NEEDS_DRINK_NO_CLASSES = 10,
            INT_CHANCE_NEEDS_BATHROOM_STUDYING = 5, INT_CHANCE_NEEDS_DRINK_STUDYING = 5;
    //Chances of INT ORG Y1-Y4
    public static final int INT_ORG_CHANCE_WANDERING_AROUND = 22, INT_ORG_CHANCE_GOING_TO_STUDY = 58,
            INT_ORG_NEED_BATHROOM_NO_CLASSES = 10, INT_ORG_NEEDS_DRINK_NO_CLASSES = 10,
            INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING = 5, INT_ORG_CHANCE_NEEDS_DRINK_STUDYING = 5;
    //Chances of EXT Y1-Y4
    public static final int EXT_CHANCE_WANDERING_AROUND = 40, EXT_CHANCE_GOING_TO_STUDY = 40,
            EXT_NEED_BATHROOM_NO_CLASSES = 10, EXT_NEEDS_DRINK_NO_CLASSES = 10,
            EXT_CHANCE_NEEDS_BATHROOM_STUDYING = 5, EXT_CHANCE_NEEDS_DRINK_STUDYING = 5;
    //Chances of EXT ORG Y1-Y4
    public static final int EXT_ORG_CHANCE_WANDERING_AROUND = 48, EXT_ORG_CHANCE_GOING_TO_STUDY = 32,
            EXT_ORG_NEED_BATHROOM_NO_CLASSES = 10, EXT_ORG_NEEDS_DRINK_NO_CLASSES = 10,
            EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING = 5, EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING = 5;

    //Chances of PROF
    public static final int PROF_CHANCE_WANDERING_AROUND = 80, PROF_CHANCE_GOING_TO_STUDY = 10,
            PROF_NEED_BATHROOM_NO_CLASSES = 10, PROF_NEEDS_DRINK_NO_CLASSES = 0,
            PROF_CHANCE_NEEDS_BATHROOM_STUDYING = 5, PROF_CHANCE_NEEDS_DRINK_STUDYING = 5;

    public UniversityRoutePlan(UniversityAgent agent, University university, Patch spawnPatch) {
        List<UniversityState> routePlan = new ArrayList<>();
        ArrayList<UniversityAction> actions;

        if (agent.getPersona() == UniversityAgent.Persona.GUARD) {
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GUARD_STAY_PUT, spawnPatch, 9000));
            routePlan.add(new UniversityState(UniversityState.Name.GUARD, this, agent, actions));
        }
        else if (agent.getPersona() == UniversityAgent.Persona.JANITOR) {
            for(int i = 0; i < Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_JANITOR_ROUNDS); i++){
                actions = new ArrayList<>();
                Patch randomToilet1 = university.getToilets().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getAmenityBlocks().get(0).getPatch();
                Patch randomToilet2 = university.getToilets().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getAmenityBlocks().get(0).getPatch();
                actions.add(new UniversityAction(UniversityAction.Name.CLEAN_STAY_PUT, randomToilet1, 180)); // TODO: Maybe loop this instead across 6 toilets for each janitor
                actions.add(new UniversityAction(UniversityAction.Name.JANITOR_MOVE_SPOT, randomToilet2)); // TODO: Maybe remove this should the comment above push thru
                routePlan.add(new UniversityState(UniversityState.Name.MAINTENANCE_BATHROOM, this, agent, actions));
                actions = new ArrayList<>();
                actions.add(new UniversityAction(UniversityAction.Name.JANITOR_CHECK_FOUNTAIN, university.getFountains().get(0).getAmenityBlocks().get(0).getPatch(), 180));
                routePlan.add(new UniversityState(UniversityState.Name.MAINTENANCE_FOUNTAIN, this, agent));
            }
        }
        else {
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GREET_GUARD, null, 0)); //TODO: Maybe remove this since interaction
            actions.add(new UniversityAction(UniversityAction.Name.GO_THROUGH_SCANNER, 2)); //TODO: Change patch destination and duration
            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_SECURITY, this, agent));
            int CALCULATED_CLASSES, LUNCH_TIME;
            ArrayList<Integer> classes = new ArrayList<>();
            if (agent.getAgentMovement().getTickEntered() < 720) { // based on 1 tick = 5 seconds
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES);
                LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES);
                    if (!classes.contains(x) && x != LUNCH_TIME) {
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            } else if (agent.getAgentMovement().getTickEntered() < 1980) {
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 1);
                LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 1) + 1;
                    if (!classes.contains(x) && x != LUNCH_TIME) {
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            } else if (agent.getAgentMovement().getTickEntered() < 3240) {
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2);
                LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(3) + 2;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2) + 2;
                    if (!classes.contains(x) && x != LUNCH_TIME) {
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            } else if (agent.getAgentMovement().getTickEntered() < 4500) {
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2);
                if (CALCULATED_CLASSES == MAX_CLASSES - 2 - 1)
                    LUNCH_TIME = -1;
                else
                    LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(2) + 3;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 3) + 3;
                    if (!classes.contains(x) && x != LUNCH_TIME) {
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            } else if (agent.getAgentMovement().getTickEntered() < 5760) {
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 3);
                if (CALCULATED_CLASSES == MAX_CLASSES - 3 - 1)
                    LUNCH_TIME = -1;
                else
                    LUNCH_TIME = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(1) + 4;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 4) + 4;
                    if (!classes.contains(x) && x != LUNCH_TIME) {
                        classes.add(x);
                        ctrClasses--;
                    }
                }
            } else {
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
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT) {

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < INT_CHANCE_WANDERING_AROUND) {
                            //TODO: Randomize actions
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent));
                        }
                        else if (x < INT_CHANCE_WANDERING_AROUND + INT_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        } else if (x < INT_CHANCE_WANDERING_AROUND + INT_CHANCE_GOING_TO_STUDY + INT_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_CUBICLE));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        } else {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    UniversityState newClass, newWaitClass, newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[0][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[0][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 720, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[1][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[1][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 1980, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[2][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[2][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 3240, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[3][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[3][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 4500, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[4][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[4][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 5760, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[5][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[5][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 7020, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CAFETERIA, null, 0)); //TODO: Change patch destination and duration
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR, null, 0)); //TODO: Change patch destination and duration
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR, null, 0)); //TODO: Change patch destination and duration
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, null, 0)); //TODO: Change patch destination and duration
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA, null, 0)); //TODO: Change patch destination and duration
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, null, 0)); //TODO: Change patch destination and duration
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            } else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT) {

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < INT_ORG_CHANCE_WANDERING_AROUND) {
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent));
                        }
                        else if (x < INT_ORG_CHANCE_WANDERING_AROUND + INT_ORG_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        } else if (x < INT_ORG_CHANCE_WANDERING_AROUND + INT_ORG_CHANCE_GOING_TO_STUDY + INT_ORG_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_CUBICLE));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        } else {
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    UniversityState newClass, newWaitClass, newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[0][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[0][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 720, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[1][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[1][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 1980, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[2][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[2][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 3240, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[3][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[3][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 4500, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[4][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[4][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 5760, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[5][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[5][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 7020, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 6, 12));
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 120, 720));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            } else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < EXT_CHANCE_WANDERING_AROUND) {
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent));
                        }
                        else if (x < EXT_CHANCE_WANDERING_AROUND + EXT_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        } else if (x < EXT_CHANCE_WANDERING_AROUND + EXT_CHANCE_GOING_TO_STUDY + EXT_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_CUBICLE));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        } else {
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    UniversityState newClass, newWaitClass, newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[0][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[0][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 720, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[1][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[1][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 1980, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[2][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[2][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 3240, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[3][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[3][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 4500, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[4][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[4][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 5760, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[5][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[5][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 7020, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 6, 12));
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 120, 720));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            } else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT
                    || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT) {

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < EXT_ORG_CHANCE_WANDERING_AROUND) {
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent));
                        }
                        else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + EXT_ORG_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        } else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + EXT_ORG_CHANCE_GOING_TO_STUDY + EXT_ORG_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_CUBICLE));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        } else {
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent));
                        }
                    }
                    UniversityState newClass, newWaitClass, newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[0][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[0][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 720, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[1][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[1][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 1980, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[2][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[2][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 3240, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[3][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[3][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 4500, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[4][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[4][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 5760, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CLASSROOM, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_STUDENT[5][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_STUDENT[5][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 7020, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 6, 12));
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 120, 720));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            } else {

                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                        if (x < PROF_CHANCE_WANDERING_AROUND) {
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent));
                        }
                        else if (x < PROF_CHANCE_WANDERING_AROUND + PROF_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        } else if (x < PROF_CHANCE_WANDERING_AROUND + PROF_CHANCE_GOING_TO_STUDY + PROF_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_CUBICLE));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        }
                    }
                    UniversityState newClass, newWaitClass, newInClass;
                    switch (classes.get(i)) {
                        case 0 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_PROF[0][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_PROF[0][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 720, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 1 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_PROF[1][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_PROF[1][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 1980, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 2 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_PROF[2][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_PROF[2][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 3240, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 3 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_PROF[3][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_PROF[3][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 4500, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        case 4 -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_PROF[4][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_PROF[4][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 5760, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                        default -> {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM, null, 0));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, null, 0));
                            int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            while (CLASSROOM_SIZES_PROF[5][classroomID] == 0){
                                classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                            }
                            CLASSROOM_SIZES_PROF[5][classroomID]--;
                            newClass = new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, 7020, classroomID);
                            //TODO: Randomized actions
                            newWaitClass = new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent);
                            newInClass = new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent);
                            routePlan.add(newClass);
                            routePlan.add(newWaitClass);
                            routePlan.add(newInClass);
                        }
                    }
                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 6, 12));
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 120, 720));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            }
        }
        actions = new ArrayList<>();
        actions.add(new UniversityAction(UniversityAction.Name.LEAVE_BUILDING));
        routePlan.add(new UniversityState(UniversityState.Name.GOING_HOME, this, agent, actions));

        this.currentRoutePlan = routePlan.listIterator();
    }

    public void setNextState() { // Set the next class in the route plan
        this.currentState = this.currentRoutePlan.next();
    }
    public void setPreviousState(){
        this.currentState = this.currentRoutePlan.previous();
    }

    public ListIterator<UniversityState> getCurrentRoutePlan() {
        return currentRoutePlan;
    }

    public UniversityState getCurrentClass() {
        return currentState;
    }
    public void addUrgentRoute(UniversityState s){
        this.currentState = s;
    }

}