package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchfield.StudyArea;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Door;
import com.socialsim.model.simulator.Simulator;

import java.util.*;

public class UniversityRoutePlan {

    private ListIterator<UniversityState> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private UniversityState currentState; // Denotes the state in the route plan
    private ArrayList<UniversityState> routePlan;
    private boolean fromStudying, fromClass, fromLunch;
    private static final int MAX_CLASSES = 6;
    private static final int MAX_CLASSROOMS = 5;
    private static final int MAX_JANITOR_ROUNDS = 6;
    private static int CLASSROOM_SIZES_STUDENT[][] = new int[][]{{40 ,48, 40, 40, 40, 40},{40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}, {40 ,48, 40, 40, 40, 40}};
    private static int CLASSROOM_SIZES_PROF[][] = new int[][]{{1, 1, 1, 1, 1, 1},{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
    private int UrgentCtr = 1;
    //Chances of INT Y1-Y4
    public static final double INT_CHANCE_WANDERING_AROUND = 0.22, INT_CHANCE_GOING_TO_STUDY = 0.58,
            INT_NEED_BATHROOM_NO_CLASSES = 0.10, INT_NEEDS_DRINK_NO_CLASSES = 0.10,
            INT_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, INT_CHANCE_NEEDS_DRINK_STUDYING = 0.05;
    //Chances of INT ORG Y1-Y4
    public static final double INT_ORG_CHANCE_WANDERING_AROUND = 0.22, INT_ORG_CHANCE_GOING_TO_STUDY = 0.58,
            INT_ORG_NEED_BATHROOM_NO_CLASSES = 0.10, INT_ORG_NEEDS_DRINK_NO_CLASSES = 0.10,
            INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, INT_ORG_CHANCE_NEEDS_DRINK_STUDYING = 0.05;
    //Chances of EXT Y1-Y4
    public static final double EXT_CHANCE_WANDERING_AROUND = 0.40, EXT_CHANCE_GOING_TO_STUDY = 0.40,
            EXT_NEED_BATHROOM_NO_CLASSES = 0.10, EXT_NEEDS_DRINK_NO_CLASSES = 0.10,
            EXT_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, EXT_CHANCE_NEEDS_DRINK_STUDYING = 0.05;
    //Chances of EXT ORG Y1-Y4
    public static final double EXT_ORG_CHANCE_WANDERING_AROUND = 0.48, EXT_ORG_CHANCE_GOING_TO_STUDY = 0.32,
            EXT_ORG_NEED_BATHROOM_NO_CLASSES = 0.10, EXT_ORG_NEEDS_DRINK_NO_CLASSES = 0.10,
            EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING = 0.05;

    //Chances of PROF
    public static final double PROF_CHANCE_WANDERING_AROUND = 0.80, PROF_CHANCE_GOING_TO_STUDY = 0.10,
            PROF_NEED_BATHROOM_NO_CLASSES = 0.10, PROF_NEEDS_DRINK_NO_CLASSES = 0,
            PROF_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, PROF_CHANCE_NEEDS_DRINK_STUDYING = 0.05;
  
    public UniversityRoutePlan(UniversityAgent agent, University university, Patch spawnPatch, int tickEntered) {
        this.routePlan = new ArrayList<>();
        //List<UniversityState> routePlan = new ArrayList<>();
        ArrayList<UniversityAction> actions;

        if (agent.getPersona() == UniversityAgent.Persona.GUARD) {
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GUARD_STAY_PUT, spawnPatch, 9000));
            routePlan.add(new UniversityState(UniversityState.Name.GUARD, this, agent, actions));
        }
        else if (agent.getPersona() == UniversityAgent.Persona.JANITOR) {
            actions = new ArrayList<>();
            Patch randomToilet = university.getToilets().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(12)).getAmenityBlocks().get(0).getPatch();
            actions.add(new UniversityAction(UniversityAction.Name.JANITOR_CLEAN_TOILET, randomToilet, 60));
            routePlan.add(new UniversityState(UniversityState.Name.MAINTENANCE_BATHROOM, this, agent, actions));
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.JANITOR_CHECK_FOUNTAIN, university.getFountains().get(0).getAmenityBlocks().get(0).getPatch(), 60));
            routePlan.add(new UniversityState(UniversityState.Name.MAINTENANCE_FOUNTAIN, this, agent, actions));
        }
        else {
            setFromClass(false);
            setFromLunch(false);
            setFromStudying(false);
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GOING_TO_SECURITY_QUEUE));
            actions.add(new UniversityAction(UniversityAction.Name.GO_THROUGH_SCANNER, 2));
            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_SECURITY, this, agent, actions));

//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
//            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
//            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
//            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
//            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
//            actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 12, 36));
//            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
//            actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 180, 360));
//            routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
//
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
//            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 100));
//            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
//
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
//            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
//            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 6, 12));
//            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent,actions));
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.FIND_BULLETIN));
//            actions.add(new UniversityAction(UniversityAction.Name.VIEW_BULLETIN,15));
//            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
//            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,15));
//            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
//            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, 720, 2, actions));
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 200));
//            routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, actions));
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 200));
//            routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, actions));
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
//            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
//            actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 12, 36));
//            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
//            actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 180, 360));
//            routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
//
//            actions = new ArrayList<>();
//            actions.add(new UniversityAction(UniversityAction.Name.LEAVE_BUILDING));
//            routePlan.add(new UniversityState(UniversityState.Name.GOING_HOME, this, agent, actions));

            int CALCULATED_CLASSES, LUNCH_TIME;
            ArrayList<Integer> classes = new ArrayList<>();
            if (tickEntered < 720) { // based on 1 tick = 5 seconds
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
            }
            else if (tickEntered < 1980) {
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
            }
            else if (tickEntered < 3240) {
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
            }
            else if (tickEntered < 4500) {
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
            }
            else if (tickEntered < 5760) {
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
            }
            else {
                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 4);
                LUNCH_TIME = -1;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 5) + 5;
                    classes.add(x);
                    ctrClasses--;
                }
            }

            if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT) {
                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        double x = Simulator.roll();
                        if (x < INT_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BULLETIN));
                            actions.add(new UniversityAction(UniversityAction.Name.VIEW_BULLETIN,3,12));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < INT_CHANCE_WANDERING_AROUND + INT_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < INT_CHANCE_WANDERING_AROUND + INT_CHANCE_GOING_TO_STUDY + INT_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        }
                        else {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 6, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent,actions));
                        }
                    }

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID] == 0) {
                        classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, actions));

                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT,  12, 36));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT,120,720));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH,this,agent,actions));
                    }
                }
            }
            else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT) {
                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        double x = Simulator.roll();
                        if (x < INT_ORG_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,120,360));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < INT_ORG_CHANCE_WANDERING_AROUND + INT_ORG_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < INT_ORG_CHANCE_WANDERING_AROUND + INT_ORG_CHANCE_GOING_TO_STUDY + INT_ORG_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        }
                        else {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 5));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK, this, agent, actions));
                        }
                    }

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID] == 0) {
                        classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, actions));

                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 12, 36));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 180, 360));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        double x = Simulator.roll();
                        if (x < EXT_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,120,360));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < EXT_CHANCE_WANDERING_AROUND + EXT_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < EXT_CHANCE_WANDERING_AROUND + EXT_CHANCE_GOING_TO_STUDY + EXT_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        }
                        else {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 6, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK,this,agent, actions));
                        }
                    }

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = 1 +Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID] == 0) {
                        classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, actions));

                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 12, 36));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 180, 360));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            }
            else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT) {
                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        double x = Simulator.roll();
                        if (x < EXT_ORG_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BULLETIN));
                            actions.add(new UniversityAction(UniversityAction.Name.VIEW_BULLETIN,3,12));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + EXT_ORG_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + EXT_ORG_CHANCE_GOING_TO_STUDY + EXT_ORG_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        }
                        else {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
                            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 6, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_DRINK,this, agent, actions));
                        }
                    }

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID] == 0) {
                        classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, actions));

                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 12, 360));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 180, 360));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            }
            else {
                Collections.sort(classes);
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 5; j++) {
                        double x = Simulator.roll();
                        if (x < PROF_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,120,360));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < PROF_CHANCE_WANDERING_AROUND + PROF_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 1440));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < PROF_CHANCE_WANDERING_AROUND + PROF_CHANCE_GOING_TO_STUDY + PROF_NEED_BATHROOM_NO_CLASSES) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
                            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE, 12, 60));
                            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK, 12));
                            routePlan.add(new UniversityState(UniversityState.Name.NEEDS_BATHROOM, this, agent, actions));
                        }
                    }

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID] == 0) {
                        classroomID = 1 + Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    CLASSROOM_SIZES_STUDENT[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 50));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent, actions));

                    if (i == LUNCH_TIME) {
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.QUEUE_VENDOR));
                        actions.add(new UniversityAction(UniversityAction.Name.CHECKOUT, 12, 36));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_LUNCH, this, agent, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.FIND_SEAT_CAFETERIA));
                        actions.add(new UniversityAction(UniversityAction.Name.LUNCH_STAY_PUT, 180, 360));
                        routePlan.add(new UniversityState(UniversityState.Name.EATING_LUNCH, this, agent, actions));
                    }
                }
            }
        }

        actions = new ArrayList<>();
        actions.add(new UniversityAction(UniversityAction.Name.LEAVE_BUILDING));
        routePlan.add(new UniversityState(UniversityState.Name.GOING_HOME, this, agent, actions));
        setNextState(-1);
    }

    public void resetClassroomSizes() {
        CLASSROOM_SIZES_STUDENT = new int[][]{{40, 48, 40, 40, 40, 40}, {40, 48, 40, 40, 40, 40}, {40, 48, 40, 40, 40, 40}, {40, 48, 40, 40, 40, 40}, {40, 48, 40, 40, 40, 40}, {40, 48, 40, 40, 40, 40}};
        CLASSROOM_SIZES_PROF = new int[][]{{1, 1, 1, 1, 1, 1},{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
    }

    public UniversityState setNextState(int i) { // Set the next class in the route plan
        //this.currentState = this.currentRoutePlan.next();
        this.currentState = this.routePlan.get(i+1);
        return this.currentState;
    }

    public UniversityState setPreviousState(int i) {
        //this.currentState = this.currentRoutePlan.previous();
        this.currentState = this.routePlan.get(i-1);
        return this.currentState;
    }
    public int getUrgentCtr(){
        return this.UrgentCtr;
    }
    public void setUrgentCtr(int ctr){
        this.UrgentCtr = ctr;
    }
    public boolean isFromStudying()
    {
        return fromStudying;
    }
    public boolean isFromLunch()
    {
        return fromLunch;
    }
    public boolean isFromClass()
    {
        return fromClass;
    }
    public void setFromStudying(boolean b){
        this.fromStudying  = b;
    }
    public void setFromClass(boolean b){
        this.fromClass = b;
    }
    public void setFromLunch(boolean b){
        this.fromLunch = b;
    }

    public ArrayList<UniversityState> getCurrentRoutePlan() {
        return routePlan;
    }

    public UniversityState getCurrentState() {
        return currentState;
    }

    public UniversityState addUrgentRoute(String s, UniversityAgent agent){
        System.out.println("Adding urgent route");
        ArrayList<UniversityAction> actions;
        if(s.equals("BATHROOM")){
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE,5));
            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK,5));
            return new UniversityState(UniversityState.Name.NEEDS_BATHROOM,this,agent,actions);
        }
        else
        {
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 5));
            return new UniversityState(UniversityState.Name.NEEDS_DRINK,this,agent,actions);
        }
    }

}