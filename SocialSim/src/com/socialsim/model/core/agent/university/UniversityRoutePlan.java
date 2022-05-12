package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.simulator.Simulator;
import java.util.*;

public class UniversityRoutePlan {

    private UniversityState currentState;
    private ArrayList<UniversityState> routePlan;
    private boolean fromStudying, fromClass, fromLunch;
    private static final int MAX_CLASSES = 6;
    private static final int MAX_CLASSROOMS = 6;
    private double UrgentCtr = -2;
    public int MAX_CLASS_ASKS = 2;

    private int[] CLASS_SCHEDULES;

    public static final double INT_CHANCE_WANDERING_AROUND = 0.21, INT_CHANCE_GOING_TO_STUDY = 0.53,
            INT_NEED_BATHROOM_NO_CLASSES = 0.05, INT_NEEDS_DRINK_NO_CLASSES = 0.05,
            INT_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, INT_CHANCE_NEEDS_DRINK_STUDYING = 0.05,
            INT_CHANCE_SNACKS = 0.21;
    public static final double INT_ORG_CHANCE_WANDERING_AROUND = 0.21, INT_ORG_CHANCE_GOING_TO_STUDY = 0.53,
            INT_ORG_NEED_BATHROOM_NO_CLASSES = 0.05, INT_ORG_NEEDS_DRINK_NO_CLASSES = 0.05,
            INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, INT_ORG_CHANCE_NEEDS_DRINK_STUDYING = 0.05,
            INT_ORG_CHANCE_SNACKS = 0.21;
    public static final double EXT_CHANCE_WANDERING_AROUND = 0.30, EXT_CHANCE_GOING_TO_STUDY = 0.40,
            EXT_NEED_BATHROOM_NO_CLASSES = 0.05, EXT_NEEDS_DRINK_NO_CLASSES = 0.05,
            EXT_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, EXT_CHANCE_NEEDS_DRINK_STUDYING = 0.05,
            EXT_CHANCE_SNACKS = 0.25;
    public static final double EXT_ORG_CHANCE_WANDERING_AROUND = 0.34, EXT_ORG_CHANCE_GOING_TO_STUDY = 0.37,
            EXT_ORG_NEED_BATHROOM_NO_CLASSES = 0.05, EXT_ORG_NEEDS_DRINK_NO_CLASSES = 0.05,
            EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING = 0.05,
            EXT_ORG_CHANCE_SNACKS = 0.24;
    public static final double PROF_CHANCE_WANDERING_AROUND = 0.45, PROF_CHANCE_GOING_TO_STUDY = 0.20,
            PROF_NEED_BATHROOM_NO_CLASSES = 0.05, PROF_NEEDS_DRINK_NO_CLASSES = 0,
            PROF_CHANCE_NEEDS_BATHROOM_STUDYING = 0.05, PROF_CHANCE_NEEDS_DRINK_STUDYING = 0.05,
            PROF_CHANCE_SNACKS = 0.30;
    public static final double CHANCE_STAFF_ROOM = 0.05, THROW_CHANCE = 0.02, BATHROOM_CHANCE = 0.05, DRINK_CHANCE = 0.05, LEAVE_EARLY_CHANCE = 0.01, ARRIVE_EARLY_CHANCE = 0.30, CHANCE_NEED_CLASS_MULTIPLIER = 0.3;
    public static final int CHANCE_INT_GUARD_INTERACT = 10, CHANCE_EXT_GUARD_INTERACT = 30, CHANCE_INTORG_GUARD_INTERACT = 20, CHANCE_EXTORG_GUARD_INTERACT = 40, CHANCE_SPROF_GUARD_INTERACT = 20, CHANCE_APROF_GUARD_INTERACT = 50, CHANCE_GUARD_VERBAL = 10;
    public static final int CHANCE_INT_ASK = 0, CHANCE_EXT_ASK = 10, CHANCE_INTORG_ASK = 5, CHANCE_EXTORG_ASK = 15;

    public UniversityRoutePlan(UniversityAgent agent, University university, Patch spawnPatch, int tickEntered) {
        this.routePlan = new ArrayList<>();
        this.CLASS_SCHEDULES = new int[]{};
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
        else if (agent.getPersona() == UniversityAgent.Persona.STAFF){
            //TODO: Tentative durations
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_TABLE));
            actions.add(new UniversityAction(UniversityAction.Name.CHECK_TABLE, 20,150));
            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CABINET));
            actions.add(new UniversityAction(UniversityAction.Name.CHECK_CABINET, 20,150));
            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
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
//            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_WAIT_AREA));
//            actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_CLASS,12,40));
//            routePlan.add(new UniversityState(UniversityState.Name.WAIT_INFRONT_OF_CLASS,this,agent,720,Simulator.rollIntIN(6),actions));
            int CALCULATED_CLASSES, LUNCH_TIME;
            ArrayList<Integer> classes = new ArrayList<>();
            if (tickEntered < 720) {
//                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES);
                CALCULATED_CLASSES = (int) Math.floor(Math.abs(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 2.5) + 2.5);
                if (CALCULATED_CLASSES > MAX_CLASSES - 1)
                    CALCULATED_CLASSES = MAX_CLASSES - 1;
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
//                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 1);
                CALCULATED_CLASSES = (int) Math.floor(Math.abs(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 2) + 2);
                if (CALCULATED_CLASSES > MAX_CLASSES - 2)
                    CALCULATED_CLASSES = MAX_CLASSES - 2;
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
//                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2);
                CALCULATED_CLASSES = (int) Math.floor(Math.abs(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 1.5) + 1.5);
                if (CALCULATED_CLASSES > MAX_CLASSES - 3)
                    CALCULATED_CLASSES = MAX_CLASSES - 3;
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
//                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 2);
                CALCULATED_CLASSES = (int) Math.floor(Math.abs(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 1.5) + 1.5);
                if (CALCULATED_CLASSES > MAX_CLASSES - 3)
                    CALCULATED_CLASSES = MAX_CLASSES - 3;
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
//                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 3);
                CALCULATED_CLASSES = (int) Math.floor(Math.abs(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 1) + 1);
                if (CALCULATED_CLASSES > MAX_CLASSES - 4)
                    CALCULATED_CLASSES = MAX_CLASSES - 4;
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
//                CALCULATED_CLASSES = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 4);
                CALCULATED_CLASSES = (int) Math.floor(Math.abs(Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * 0.5) + 0.5);
                if (CALCULATED_CLASSES > MAX_CLASSES - 5)
                    CALCULATED_CLASSES = MAX_CLASSES - 5;
                LUNCH_TIME = -1;
                int ctrClasses = CALCULATED_CLASSES;
                while (ctrClasses > 0) {
                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSES - 5) + 5;
                    classes.add(x);
                    ctrClasses--;
                }
            }
            Collections.sort(classes);
            CLASS_SCHEDULES = classes.stream().mapToInt(Integer::intValue).toArray();
            if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT) {
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 8; j++) {
                        double x = Simulator.roll();
                        if (x < INT_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BULLETIN));
                            actions.add(new UniversityAction(UniversityAction.Name.VIEW_BULLETIN,3,12));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < INT_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STAFF));
                            actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_STAFF, 30));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
                        }
                        else if (x < INT_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + INT_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 720));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < INT_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + INT_CHANCE_GOING_TO_STUDY + INT_CHANCE_SNACKS) {
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

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (university.getClassroomSizesStudent()[classes.get(i)][classroomID] == 0) {
                        classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    university.getClassroomSizesStudent()[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 180));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 1080));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));

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
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 8; j++) {
                        double x = Simulator.roll();
                        if (x < INT_ORG_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,120,360));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < INT_ORG_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STAFF));
                            actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_STAFF, 30));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
                        }
                        else if (x < INT_ORG_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + INT_ORG_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 720));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < INT_ORG_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + INT_ORG_CHANCE_GOING_TO_STUDY + INT_ORG_CHANCE_SNACKS) {
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

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (university.getClassroomSizesStudent()[classes.get(i)][classroomID] == 0) {
                        classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    university.getClassroomSizesStudent()[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 180));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 1080));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));

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
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 8; j++) {
                        double x = Simulator.roll();
                        if (x < EXT_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,120,360));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < EXT_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STAFF));
                            actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_STAFF, 30));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
                        }
                        else if (x < EXT_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + EXT_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 720));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < EXT_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + EXT_CHANCE_GOING_TO_STUDY + EXT_CHANCE_SNACKS) {
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

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (university.getClassroomSizesStudent()[classes.get(i)][classroomID] == 0) {
                        classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    university.getClassroomSizesStudent()[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 180));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 1080));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));

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
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 8; j++) {
                        double x = Simulator.roll();
                        if (x < EXT_ORG_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BULLETIN));
                            actions.add(new UniversityAction(UniversityAction.Name.VIEW_BULLETIN,3,12));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STAFF));
                            actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_STAFF, 30));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
                        }
                        else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + EXT_ORG_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 720));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < EXT_ORG_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + EXT_ORG_CHANCE_GOING_TO_STUDY + EXT_ORG_CHANCE_SNACKS) {
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

                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                    int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    while (university.getClassroomSizesStudent()[classes.get(i)][classroomID] == 0) {
                        classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                    }
                    university.getClassroomSizesStudent()[classes.get(i)][classroomID]--;
                    int tickClassStart = switch (classes.get(i)) {
                        case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                    };
                    routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 180));
                    routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));
                    actions = new ArrayList<>();
                    actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 1080));
                    routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_STUDENT, this, agent, tickClassStart, classroomID, actions));

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
                for (int i = 0; i < CALCULATED_CLASSES; i++) {
                    for (int j = 0; j < 8; j++) {
                        double x = Simulator.roll();
                        if (x < PROF_CHANCE_WANDERING_AROUND) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.FIND_BENCH));
                            actions.add(new UniversityAction(UniversityAction.Name.SIT_ON_BENCH,120,360));
                            routePlan.add(new UniversityState(UniversityState.Name.WANDERING_AROUND, this, agent, actions));
                        }
                        else if (x < PROF_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STAFF));
                            actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_STAFF, 30));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STAFF, this, agent, actions));
                        }
                        else if (x < PROF_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + PROF_CHANCE_GOING_TO_STUDY) {
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_STUDY_ROOM));
                            routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_STUDY, this, agent, actions));
                            actions = new ArrayList<>();
                            actions.add(new UniversityAction(UniversityAction.Name.STUDY_AREA_STAY_PUT, 120, 720));
                            routePlan.add(new UniversityState(UniversityState.Name.STUDYING, this, agent, actions));
                        }
                        else if (x < PROF_CHANCE_WANDERING_AROUND + CHANCE_STAFF_ROOM + PROF_CHANCE_GOING_TO_STUDY + PROF_CHANCE_SNACKS) {
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
                    int availableClass = 6;
                    for(int ctr = 0; ctr < MAX_CLASSES; ctr++)
                    {
                        if(university.getClassroomSizesProf()[classes.get(i)][ctr] == 0){
                            availableClass--;
                        }
                    }
                    if(availableClass > 0){
                        int classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                        while (university.getClassroomSizesProf()[classes.get(i)][classroomID] == 0) {
                            classroomID = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(MAX_CLASSROOMS);
                        }
                        university.getClassroomSizesProf()[classes.get(i)][classroomID]--;
                        university.getProfsPerSchedule()[classes.get(i)][classroomID] = agent;
                        int tickClassStart = switch (classes.get(i)) {
                            case 0 -> 720; case 1 -> 1980; case 2 -> 3240; case 3 -> 4500; case 4 -> 5760; default -> 7020;
                        };
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_CLASSROOM));
                        routePlan.add(new UniversityState(UniversityState.Name.GOING_TO_CLASS_PROFESSOR, this, agent, tickClassStart, classroomID, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.SIT_PROFESSOR_TABLE, 180));
                        routePlan.add(new UniversityState(UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR, this, agent, tickClassStart, classroomID, actions));
                        actions = new ArrayList<>();
                        actions.add(new UniversityAction(UniversityAction.Name.CLASSROOM_STAY_PUT, 1080));
                        routePlan.add(new UniversityState(UniversityState.Name.IN_CLASS_PROFESSOR, this, agent, tickClassStart, classroomID, actions));
                    }

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

    public UniversityState setNextState(int i) {
        this.UrgentCtr = this.UrgentCtr + 0.5;
        this.currentState = this.routePlan.get(i+1);
        return this.currentState;
    }

    public UniversityState setPreviousState(int i) {
        this.currentState = this.routePlan.get(i-1);
        return this.currentState;
    }
    public double getUrgentCtr(){
        return this.UrgentCtr;
    }
    public void setUrgentCtr(double ctr){
        this.UrgentCtr = ctr;
    }
    public boolean isFromStudying() {return fromStudying;  }
    public boolean isFromLunch() {return fromLunch;    }
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
        ArrayList<UniversityAction> actions;
        if(s.equals("BATHROOM")){
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_BATHROOM));
            actions.add(new UniversityAction(UniversityAction.Name.RELIEVE_IN_CUBICLE,12,36));
            actions.add(new UniversityAction(UniversityAction.Name.WASH_IN_SINK,12));
            return new UniversityState(UniversityState.Name.NEEDS_BATHROOM,this,agent,actions);
        }
        else if(s.equals("TRASH")){
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_TRASH,3));
            return new UniversityState(UniversityState.Name.THROW_TRASH, this, agent, actions);
        }
        else {
            actions = new ArrayList<>();
            actions.add(new UniversityAction(UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN));
            actions.add(new UniversityAction(UniversityAction.Name.QUEUE_FOUNTAIN));
            actions.add(new UniversityAction(UniversityAction.Name.DRINK_FOUNTAIN, 6,12));
            return new UniversityState(UniversityState.Name.NEEDS_DRINK,this,agent,actions);
        }
    }

    public int[] getCLASS_SCHEDULES() {
        return CLASS_SCHEDULES;
    }

    public UniversityState addWaitingRoute(int classKey, int classTickStart, UniversityAgent agent){
        ArrayList<UniversityAction> actions;
        actions = new ArrayList<>();
        actions.add(new UniversityAction(UniversityAction.Name.GO_TO_WAIT_AREA));
        actions.add(new UniversityAction(UniversityAction.Name.WAIT_FOR_CLASS,5,20));
        return new UniversityState(UniversityState.Name.WAIT_INFRONT_OF_CLASS,this, agent, classTickStart, classKey, actions);
    }

}