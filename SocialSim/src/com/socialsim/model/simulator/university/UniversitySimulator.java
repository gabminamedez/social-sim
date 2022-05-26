package com.socialsim.model.simulator.university;

import com.socialsim.controller.Main;
import com.socialsim.controller.university.controls.UniversityScreenController;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.university.*;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.gate.Gate;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.Simulator;

import java.io.PrintWriter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class UniversitySimulator extends Simulator {

    public static int defaultMaxStudents = 500;
    public static int defaultMaxProfessors = 50;
    public static int defaultMaxCurrentStudents = 250;
    public static int defaultMaxCurrentProfessors = 20;

    private University university;
    private final AtomicBoolean running;
    private final SimulationTime time;
    private final Semaphore playSemaphore;

    public static int currentProfessorCount = 0;
    public static int currentStudentCount = 0;
    public static int currentNonverbalCount = 0;
    public static int currentCooperativeCount = 0;
    public static int currentExchangeCount = 0;
    public static float averageNonverbalDuration = 0;
    public static float averageCooperativeDuration = 0;
    public static float averageExchangeDuration = 0;
    public static int currentStudentStudentCount = 0;
    public static int currentStudentProfCount = 0;
    public static int currentStudentGuardCount = 0;
    public static int currentStudentJanitorCount = 0;
    public static int currentStudentStaffCount = 0;
    public static int currentProfProfCount = 0;
    public static int currentProfGuardCount = 0;
    public static int currentProfJanitorCount = 0;
    public static int currentProfStaffCount = 0;
    public static int currentGuardJanitorCount = 0;
    public static int currentJanitorJanitorCount = 0;
    public static int currentStaffStaffCount = 0;
    public static int[] compiledCurrentProfessorCount;
    public static int[] compiledCurrentStudentCount;
    public static int[] compiledCurrentNonverbalCount;
    public static int[] compiledCurrentCooperativeCount;
    public static int[] compiledCurrentExchangeCount;
    public static float[] compiledAverageNonverbalDuration;
    public static float[] compiledAverageCooperativeDuration;
    public static float[] compiledAverageExchangeDuration;
    public static int[] compiledCurrentStudentStudentCount;
    public static int[] compiledCurrentStudentProfCount;
    public static int[] compiledCurrentStudentGuardCount;
    public static int[] compiledCurrentStudentJanitorCount;
    public static int[] compiledCurrentStudentStaffCount;
    public static int[] compiledCurrentProfProfCount;
    public static int[] compiledCurrentProfGuardCount;
    public static int[] compiledCurrentProfJanitorCount;
    public static int[] compiledCurrentProfStaffCount;
    public static int[] compiledCurrentGuardJanitorCount;
    public static int[] compiledCurrentJanitorJanitorCount;
    public static int[] compiledCurrentStaffStaffCount;
    public static int[][] currentPatchCount;

    public UniversitySimulator() {
        this.university = null;
        this.running = new AtomicBoolean(false);
        this.time = new SimulationTime(6, 30, 0);
        this.playSemaphore = new Semaphore(0);
        this.start();
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public AtomicBoolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public boolean isRunning() {
        return running.get();
    }

    public SimulationTime getSimulationTime() {
        return time;
    }

    public Semaphore getPlaySemaphore() {
        return playSemaphore;
    }

    public void resetToDefaultConfiguration(University university) {
        this.university = university;
        replenishStaticVars();
        UniversityAgent.clearUniversityAgentCounts();
        university.resetClassroomSizes();
        this.time.reset();
        this.running.set(false);
    }

    public void spawnInitialAgents(University university) {
        university.createInitialAgentDemographics(university.getMAX_STUDENTS(), university.getMAX_PROFESSORS());

        UniversityAgent guard = university.getAgents().get(0);
        guard.setAgentMovement(new UniversityAgentMovement(university.getPatch(57,12), guard, 1.27, university.getPatch(57,12).getPatchCenterCoordinates(), -1));
        university.getAgentPatchSet().add(guard.getAgentMovement().getCurrentPatch());
        UniversityAgent.guardCount++;
        UniversityAgent.agentCount++;

        UniversityAgent janitor1 = university.getAgents().get(1);
        janitor1.setAgentMovement(new UniversityAgentMovement(university.getPatch(6,65), janitor1, 1.27, university.getPatch(6,65).getPatchCenterCoordinates(), -1));
        university.getAgentPatchSet().add(janitor1.getAgentMovement().getCurrentPatch());
        UniversityAgent.janitorCount++;
        UniversityAgent.agentCount++;

        UniversityAgent janitor2 = university.getAgents().get(2);
        janitor2.setAgentMovement(new UniversityAgentMovement(university.getPatch(7,66), janitor2, 1.27, university.getPatch(7,66).getPatchCenterCoordinates(), -1));
        university.getAgentPatchSet().add(janitor2.getAgentMovement().getCurrentPatch());
        UniversityAgent.janitorCount++;
        UniversityAgent.agentCount++;

        UniversityAgent staff1 = university.getAgents().get(3);
        staff1.setAgentMovement(new UniversityAgentMovement(university.getPatch(10,77), staff1, 1.27, university.getPatch(10,77).getPatchCenterCoordinates(), -1));
        university.getAgentPatchSet().add(staff1.getAgentMovement().getCurrentPatch());
        UniversityAgent.staffCount++;
        UniversityAgent.agentCount++;

        UniversityAgent staff2 = university.getAgents().get(4);
        staff2.setAgentMovement(new UniversityAgentMovement(university.getPatch(10,82), staff2, 1.27, university.getPatch(10,82).getPatchCenterCoordinates(), -1));
        university.getAgentPatchSet().add(staff2.getAgentMovement().getCurrentPatch());
        UniversityAgent.staffCount++;
        UniversityAgent.agentCount++;

        UniversityAgent staff3 = university.getAgents().get(5);
        staff3.setAgentMovement(new UniversityAgentMovement(university.getPatch(10,87), staff3, 1.27, university.getPatch(10,87).getPatchCenterCoordinates(), -1));
        university.getAgentPatchSet().add(staff3.getAgentMovement().getCurrentPatch());
        UniversityAgent.staffCount++;
        UniversityAgent.agentCount++;
    }

    public void reset() {
        this.time.reset();
    }

    private void start() {
        new Thread(() -> {
            final int speedAwarenessLimitMilliseconds = 10;

            while (true) {
                try {
                    playSemaphore.acquire();
                    while (this.isRunning()) {
                        long currentTick = this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5;
                        try {
                            updateAgentsInUniversity(university,currentTick);
                            spawnAgent(university, currentTick);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        ((UniversityScreenController) Main.mainScreenController).drawUniversityViewForeground(Main.universitySimulator.getUniversity(), SimulationTime.SLEEP_TIME_MILLISECONDS.get() < speedAwarenessLimitMilliseconds);

                        this.time.tick();
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());

                        if ((this.time.getStartTime().until(this.time.getTime(), ChronoUnit.SECONDS) / 5) == 9000) {
                            ((UniversityScreenController) Main.mainScreenController).playAction();
                            break;
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateAgentsInUniversity(University university, long currentTick) throws InterruptedException {
        moveAll(university,currentTick);
        compiledCurrentProfessorCount[(int) currentTick] = currentProfessorCount;
        compiledCurrentStudentCount[(int) currentTick] = currentStudentCount;
        compiledCurrentNonverbalCount[(int) currentTick] = currentNonverbalCount;
        compiledCurrentCooperativeCount[(int) currentTick] = currentCooperativeCount;
        compiledCurrentExchangeCount[(int) currentTick] = currentExchangeCount;
        compiledAverageNonverbalDuration[(int) currentTick] = averageNonverbalDuration;
        compiledAverageCooperativeDuration[(int) currentTick] = averageCooperativeDuration;
        compiledAverageExchangeDuration[(int) currentTick] = averageExchangeDuration;
        compiledCurrentStudentStudentCount[(int) currentTick] = currentStudentStudentCount;
        compiledCurrentStudentProfCount[(int) currentTick] = currentStudentProfCount;
        compiledCurrentStudentGuardCount[(int) currentTick] = currentStudentGuardCount;
        compiledCurrentStudentJanitorCount[(int) currentTick] = currentStudentJanitorCount;
        compiledCurrentStudentStaffCount[(int) currentTick] = currentStudentStaffCount;
        compiledCurrentProfProfCount[(int) currentTick] = currentProfProfCount;
        compiledCurrentProfGuardCount[(int) currentTick] = currentProfGuardCount;
        compiledCurrentProfJanitorCount[(int) currentTick] = currentProfJanitorCount;
        compiledCurrentProfStaffCount[(int) currentTick] = currentProfStaffCount;
        compiledCurrentGuardJanitorCount[(int) currentTick] = currentGuardJanitorCount;
        compiledCurrentJanitorJanitorCount[(int) currentTick] = currentJanitorJanitorCount;
        compiledCurrentStaffStaffCount[(int) currentTick] = currentStaffStaffCount;
    }

    private static void moveAll(University university,long currentTick) {
        int bathroomReserves = university.numBathroomsFree();
        for (UniversityAgent agent : university.getMovableAgents()) {
            int randomizerTick = Simulator.rollIntIN(50);
            //TODO: Ask what range of the randomizer would be
            boolean hasClass = false;
            boolean inClass = false;
            int i = agent.getAgentMovement().getStateIndex();
            int classIndex = -1;
            if(agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.IN_CLASS_STUDENT ||agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.IN_CLASS_PROFESSOR){
                inClass = true;
            }
            try {
                if((agent.getType() == UniversityAgent.Type.PROFESSOR || agent.getType() == UniversityAgent.Type.STUDENT)) {
                    if (inClass) {
                        if (currentTick >= 1740 && currentTick < 1800 && Simulator.roll() < UniversityRoutePlan.LEAVE_EARLY_CHANCE) {
//                            if (currentTick == 1790 || currentTick == 1790 - randomizerTick) {
                            agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                            agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                            agent.getAgentMovement().setActionIndex(0);
                            agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                            if (agent.getAgentMovement().getGoalAttractor() != null) {
                                agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                            }
                            agent.getAgentMovement().resetGoal();
                        } else if (currentTick >= 3000 && currentTick < 3060 && Simulator.roll() < UniversityRoutePlan.LEAVE_EARLY_CHANCE) {
//                        } else if (currentTick == 3050 || currentTick == 3050 - randomizerTick) {
                            agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                            agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                            agent.getAgentMovement().setActionIndex(0);
                            agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                            if (agent.getAgentMovement().getGoalAttractor() != null) {
                                agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                            }
                            agent.getAgentMovement().resetGoal();
                        } else if (currentTick >= 4260 && currentTick < 4320 && Simulator.roll() < UniversityRoutePlan.LEAVE_EARLY_CHANCE) {
//                        } else if (currentTick == 4310 || currentTick == 4310 - randomizerTick) {
                            agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                            agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                            agent.getAgentMovement().setActionIndex(0);
                            agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                            if (agent.getAgentMovement().getGoalAttractor() != null) {
                                agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                            }
                            agent.getAgentMovement().resetGoal();
                        } else if (currentTick >= 5520 && currentTick < 5580 && Simulator.roll() < UniversityRoutePlan.LEAVE_EARLY_CHANCE) {
//                        } else if (currentTick == 5570 || currentTick == 5570 - randomizerTick) {
                            agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                            agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                            agent.getAgentMovement().setActionIndex(0);
                            agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                            if (agent.getAgentMovement().getGoalAttractor() != null) {
                                agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                            }
                            agent.getAgentMovement().resetGoal();
                        } else if (currentTick >= 6780 && currentTick < 6840 && Simulator.roll() < UniversityRoutePlan.LEAVE_EARLY_CHANCE) {
//                        } else if (currentTick == 6840 || currentTick == 6840 - randomizerTick) {
                            agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                            agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                            agent.getAgentMovement().setActionIndex(0);
                            agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                            if (agent.getAgentMovement().getGoalAttractor() != null) {
                                agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                            }
                            agent.getAgentMovement().resetGoal();
                        }
                    } else {
                        if (agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT || agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR){
                            //TODO: If a class is ongoing, wait for class
                            // Find the prof for the respective class and check if still IN_CLASS and different tickClassStart; use PROFS_PER_SCHEDULE
                            double tickStart = agent.getAgentMovement().getRoutePlan().getCurrentState().getTickStart();
                            int classroomSchedule = switch ((int) tickStart) {
                                case 720 -> 0; case 1980 -> 1; case 3240 -> 2; case 4500 -> 3; case 5760 -> 4; default -> 5;
                            };
                            int classroomIndex = agent.getAgentMovement().getCurrentState().getClassroomID();
                            if (classroomSchedule > 0 && university.getProfsPerSchedule()[classroomSchedule - 1][classroomIndex] != null && university.getProfsPerSchedule()[classroomSchedule - 1][classroomIndex].getAgentMovement().getCurrentState().getName() == UniversityState.Name.IN_CLASS_PROFESSOR){
                                agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().add(agent.getAgentMovement().getStateIndex() - 1, agent.getAgentMovement().getRoutePlan().addWaitingRoute(agent.getAgentMovement().getCurrentState().getClassroomID(),agent.getAgentMovement().getCurrentState().getTickClassStart(),agent));
                                agent.getAgentMovement().setPreviousState(agent.getAgentMovement().getStateIndex());
                                agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() -1);
                                agent.getAgentMovement().setActionIndex(0);
                                agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                                if(agent.getAgentMovement().getGoalAttractor() != null) {
                                    agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                                }
                                agent.getAgentMovement().resetGoal();
                            }
                        }
                        else if (agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.WAIT_INFRONT_OF_CLASS && agent.getAgentMovement().getRoutePlan().getCurrentState().getClassroomID() != 6){
                            //TODO: If prof not holding classes anymore, move to next state
                            double tickStart = agent.getAgentMovement().getRoutePlan().getCurrentState().getTickStart();
                            int classroomSchedule = switch ((int) tickStart) {
                                case 720 -> 0; case 1980 -> 1; case 3240 -> 2; case 4500 -> 3; case 5760 -> 4; default -> 5;
                            };
                            int classroomIndex = agent.getAgentMovement().getCurrentState().getClassroomID();
                            if (classroomSchedule > 0 && university.getProfsPerSchedule()[classroomSchedule][classroomIndex] != null && university.getProfsPerSchedule()[classroomSchedule][classroomIndex].getAgentMovement().getCurrentState().getName() == UniversityState.Name.IN_CLASS_PROFESSOR){
                                agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                                agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                                agent.getAgentMovement().setActionIndex(0);
                                agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                                if (agent.getAgentMovement().getGoalAttractor() != null) {
                                    agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                                }
                                agent.getAgentMovement().resetGoal();
                            }
                            // If current class is over 30 mins and still waiting
                            else if (currentTick - tickStart >= 360){
                                agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                                agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 3);
                                agent.getAgentMovement().setActionIndex(0);
                                agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                                if (agent.getAgentMovement().getGoalAttractor() != null) {
                                    agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                                }
                                agent.getAgentMovement().resetGoal();
                            }
                        }
                        else if (agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.WAIT_FOR_CLASS_STUDENT || agent.getAgentMovement().getRoutePlan().getCurrentState().getName() == UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR){
                            if (currentTick == 720 || currentTick == 1980 || currentTick == 3240 || currentTick == 4500 || currentTick == 5760 || currentTick == 7020){
                                agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                                agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                                agent.getAgentMovement().setActionIndex(0);
                                agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                                if (agent.getAgentMovement().getGoalAttractor() != null) {
                                    agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                                }
                                agent.getAgentMovement().resetGoal();
                            }
                        }
                        else{
                            if (currentTick >= 540 && currentTick < 720 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE){
//                        if (currentTick == 560 || currentTick == 560 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 720) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 720) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            } else if (currentTick >= 1800 && currentTick < 1980 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE) {
//                        } else if (currentTick == 1820 || currentTick == 1820 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 1980) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 1980) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            } else if (currentTick >= 3060 && currentTick < 3240 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE) {
//                        } else if (currentTick == 3080 || currentTick == 3080 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 3240) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 3240) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            } else if (currentTick >= 4320 && currentTick < 4500 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE) {
//                        } else if (currentTick == 4340 || currentTick == 4340 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 4500) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 4500) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            } else if (currentTick >= 5580 && currentTick < 5760 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE) {
//                        } else if (currentTick == 5600 || currentTick == 5600 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 5760) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 5760) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            } else if (currentTick >= 6840 && currentTick < 7020 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE) {
//                        } else if (currentTick == 6860 || currentTick == 6860 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            } else if (currentTick >= 8520 && currentTick < 9000 && Simulator.roll() < UniversityRoutePlan.ARRIVE_EARLY_CHANCE) {
//                        } else if (currentTick == 8520 || currentTick == 8520 - randomizerTick) {
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_HOME) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            else if (currentTick >= 720 && currentTick < 1800 && IntStream.of(agent.getAgentMovement().getRoutePlan().getCLASS_SCHEDULES()).anyMatch(x -> x == 0)){
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 720) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 720) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            else if (currentTick >= 1980 && currentTick < 3060 && IntStream.of(agent.getAgentMovement().getRoutePlan().getCLASS_SCHEDULES()).anyMatch(x -> x == 1)){
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 1980) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 1980) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            else if (currentTick >= 3240 && currentTick < 4320 && IntStream.of(agent.getAgentMovement().getRoutePlan().getCLASS_SCHEDULES()).anyMatch(x -> x == 2)){
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 3240) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 3240) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            else if (currentTick >= 4500 && currentTick < 5580 && IntStream.of(agent.getAgentMovement().getRoutePlan().getCLASS_SCHEDULES()).anyMatch(x -> x == 3)){
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 4500) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 4500) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            else if (currentTick >= 5760 && currentTick < 6840 && IntStream.of(agent.getAgentMovement().getRoutePlan().getCLASS_SCHEDULES()).anyMatch(x -> x == 4)){
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 5760) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 5760) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            else if (currentTick >= 7020 && currentTick < 8100 && IntStream.of(agent.getAgentMovement().getRoutePlan().getCLASS_SCHEDULES()).anyMatch(x -> x == 5)){
                                while (i < agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().size() && !hasClass) {
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    if (agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR && agent.getAgentMovement().getRoutePlan().getCurrentRoutePlan().get(i).getTickClassStart() == 7020) {
                                        hasClass = true;
                                        classIndex = i;
                                    }
                                    i++;
                                }
                            }
                            if (hasClass) {
                                agent.getAgentMovement().setNextState(classIndex - 1);
                                agent.getAgentMovement().setStateIndex(classIndex);
                                agent.getAgentMovement().setActionIndex(0);
                                agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(0));
                                if (agent.getAgentMovement().getGoalAttractor() != null) {
                                    agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                                }
                                agent.getAgentMovement().resetGoal();
                            }
                        }
                    }
                    // Logic for Bathroom Wait
                    if (agent.getAgentMovement().getCurrentState().getName() == UniversityState.Name.WAIT_INFRONT_OF_CLASS && agent.getAgentMovement().getCurrentState().getClassroomID() == 6){
                        if (bathroomReserves > 0){
                            agent.getAgentMovement().setNextState(agent.getAgentMovement().getStateIndex());
                            agent.getAgentMovement().setStateIndex(agent.getAgentMovement().getStateIndex() + 1);
                            agent.getAgentMovement().setActionIndex(0);
                            agent.getAgentMovement().setCurrentAction(agent.getAgentMovement().getCurrentState().getActions().get(agent.getAgentMovement().getActionIndex()));
                            if (agent.getAgentMovement().getGoalAttractor() != null) {
                                agent.getAgentMovement().getGoalAttractor().setIsReserved(false);
                            }
                            agent.getAgentMovement().resetGoal();
                            bathroomReserves--;
                        }
                    }
                }
                moveOne(agent);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void moveOne(UniversityAgent agent) throws Throwable {
        UniversityAgentMovement agentMovement = agent.getAgentMovement();

        UniversityAgent.Type type = agent.getType();
        UniversityAgent.Persona persona = agent.getPersona();
        UniversityState state = agentMovement.getCurrentState();
        UniversityAction action = agentMovement.getCurrentAction();
        if (!agentMovement.isInteracting() || agentMovement.isSimultaneousInteractionAllowed()) {
            switch (type) {
                case JANITOR:
                    if (state.getName() == UniversityState.Name.MAINTENANCE_BATHROOM) {
                        if (action.getName() == UniversityAction.Name.JANITOR_CLEAN_TOILET) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                agentMovement.checkIfStuck();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.MAINTENANCE_FOUNTAIN) {
                        if (action.getName() == UniversityAction.Name.JANITOR_CHECK_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(agentMovement.getCurrentAction().getDestination().getAmenityBlock().getParent());
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                agentMovement.checkIfStuck();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.setPreviousState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }

                    break;
                case STAFF:
                    if (state.getName() == UniversityState.Name.GOING_TO_STAFF){
                        if (action.getName() == UniversityAction.Name.GO_TO_CABINET) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseStaffroomGoal(Cabinet.class);
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.CHECK_CABINET){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setPreviousState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() - 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_TO_TABLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseStaffroomGoal(OfficeTable.class);
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.CHECK_TABLE){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_TO_STAFF) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseStaffroomGoal(Chair.class)){
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.WAIT_FOR_STAFF){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                    }

                    break;
                case STUDENT:
                    //System.out.println(agentMovement.getCurrentPath().toString());
                    if (state.getName() == UniversityState.Name.GOING_TO_SECURITY) {
                        if (action.getName() == UniversityAction.Name.GOING_TO_SECURITY_QUEUE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getSecurities().get(0));
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                if (!agentMovement.isStationInteracting()) {
                                    int CHANCE_GUARD_INTERACT = 0;
                                    if (persona == UniversityAgent.Persona.INT_Y1_STUDENT || persona == UniversityAgent.Persona.INT_Y2_STUDENT || persona == UniversityAgent.Persona.INT_Y3_STUDENT || persona == UniversityAgent.Persona.INT_Y4_STUDENT) {
                                        CHANCE_GUARD_INTERACT = UniversityRoutePlan.CHANCE_INT_GUARD_INTERACT;
                                    }
                                    else if (persona == UniversityAgent.Persona.EXT_Y1_STUDENT || persona == UniversityAgent.Persona.EXT_Y2_STUDENT || persona == UniversityAgent.Persona.EXT_Y3_STUDENT || persona == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                        CHANCE_GUARD_INTERACT = UniversityRoutePlan.CHANCE_EXT_GUARD_INTERACT;
                                    }
                                    else if (persona == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || persona == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || persona == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || persona == UniversityAgent.Persona.INT_Y4_ORG_STUDENT) {
                                        CHANCE_GUARD_INTERACT = UniversityRoutePlan.CHANCE_INTORG_GUARD_INTERACT;
                                    }
                                    else {
                                        CHANCE_GUARD_INTERACT = UniversityRoutePlan.CHANCE_EXTORG_GUARD_INTERACT;
                                    }
                                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                    if (x < CHANCE_GUARD_INTERACT) {
                                        agentMovement.forceStationedInteraction(UniversityAgent.Type.GUARD);
                                    }
                                }

                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.setStationInteracting(false);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.WANDERING_AROUND) {
                        if (action.getName() == UniversityAction.Name.FIND_BENCH || action.getName() == UniversityAction.Name.FIND_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (action.getName() == UniversityAction.Name.FIND_BENCH) {
                                    if (!agentMovement.chooseGoal(Bench.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    if (!agentMovement.chooseGoal(Bulletin.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                        else{
                                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                            double CHANCE = Simulator.roll();
                                            double MAX = UniversityRoutePlan.BATHROOM_CHANCE;
                                            double MAX_DRINK = UniversityRoutePlan.DRINK_CHANCE;
                                            double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;

                                            if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                                agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                if (agentMovement.getGoalAttractor() != null) {
                                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                                }
                                                agentMovement.resetGoal();
                                                agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                            }
                                            else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                                agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                if (agentMovement.getGoalAttractor() != null) {
                                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                                }
                                                agentMovement.resetGoal();
                                                agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                            }
                                            else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                                agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                                agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                if (agentMovement.getGoalAttractor() != null) {
                                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                                }
                                                agentMovement.resetGoal();
                                                agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                            }
                                        }
                                    }
                                    else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.SIT_ON_BENCH || action.getName() == UniversityAction.Name.VIEW_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor()!=null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double MAX = UniversityRoutePlan.BATHROOM_CHANCE;
                                double MAX_DRINK = UniversityRoutePlan.DRINK_CHANCE;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;

                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == UniversityAction.Name.GO_TO_BATHROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() - 1, agentMovement.getRoutePlan().addWaitingRoute(6,0,agent));
                                    agentMovement.setPreviousState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() -1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if(agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
//                                    if (agentMovement.getRoutePlan().isFromStudying()) {
//                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
//                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        agentMovement.getRoutePlan().setFromStudying(false);
//                                    }
//                                    else if (agentMovement.getRoutePlan().isFromClass()) {
//                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
//                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        agentMovement.getRoutePlan().setFromClass(false);
//                                    }
//                                    else if (agentMovement.getRoutePlan().isFromLunch()) {
//                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
//                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        agentMovement.getRoutePlan().setFromLunch(false);
//                                    }
//                                    else {
//                                        agentMovement.setNextState(agentMovement.getStateIndex());
//                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                    }
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                if(agentMovement.getGoalAttractor()!=null){
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.WASH_IN_SINK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    }
                                    else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else {
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        if (agentMovement.getRoutePlan().isFromStudying()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if(agentMovement.getGoalAttractor()!=null){
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromStudying(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromClass()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor()!=null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromClass(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromLunch()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor()!=null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromLunch(false);
                                        }
                                        else {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if(agentMovement.getGoalAttractor()!=null){
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.NEEDS_DRINK) {
                        if (action.getName() == UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                int fountainIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(Main.universitySimulator.getUniversity().getFountains().size());
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getFountains().get(fountainIndex).getAmenityBlocks().get(0).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getFountains().get(fountainIndex));
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.QUEUE_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.DRINK_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getRoutePlan().isFromStudying()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(false);
                                }
                                else if (agentMovement.getRoutePlan().isFromClass()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(false);
                                }
                                else if (agentMovement.getRoutePlan().isFromLunch()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(false);
                                }
                                else {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_STUDY) {
                        if (action.getName() == UniversityAction.Name.GO_TO_STUDY_ROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(StudyTable.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex() + 1);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 2);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                    else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                    agentMovement.setActionIndex(0);
                                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.STUDYING) {
                        if (action.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);

                                double CHANCE = Simulator.roll();
                                double MAX = 0;
                                double MAX_DRINK = 0;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;
                                if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                    MAX = UniversityRoutePlan.EXT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){
                                    MAX = UniversityRoutePlan.INT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                                    MAX = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                                    MAX = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if(agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if(agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_CLASS_STUDENT) {
                        if (action.getName() == UniversityAction.Name.GO_TO_CLASSROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if(agentMovement.chooseClassroomGoal(Chair.class, agentMovement.getCurrentState().getClassroomID()))
                                {
//                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() - 1, agentMovement.getRoutePlan().addWaitingRoute(agentMovement.getCurrentState().getClassroomID(),agentMovement.getCurrentState().getTickClassStart(),agent));
//                                    agentMovement.setPreviousState(agentMovement.getStateIndex());
//                                    agentMovement.setStateIndex(agentMovement.getStateIndex() -1);
//                                    agentMovement.setActionIndex(0);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    if(agentMovement.getGoalAttractor() != null) {
//                                        agentMovement.getGoalAttractor().setIsReserved(false);
//                                    }
//                                    agentMovement.resetGoal();
                                }
                            }
                            else if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                                else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                            if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.WAIT_FOR_CLASS_STUDENT) {
                        agentMovement.getRoutePlan().MAX_CLASS_ASKS = 2;
                        if (action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.IN_CLASS_STUDENT) {
                        if (action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if(agentMovement.getGoalAttractor()!=null){
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double MAX = 0;
                                double MAX_DRINK = 0;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;
                                if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                    MAX = UniversityRoutePlan.EXT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){
                                    MAX = UniversityRoutePlan.INT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                                    MAX = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                                    MAX = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else {
                                    int CHANCE_ASKS = 0;
                                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);

                                    if (persona == UniversityAgent.Persona.INT_Y1_STUDENT || persona == UniversityAgent.Persona.INT_Y2_STUDENT || persona == UniversityAgent.Persona.INT_Y3_STUDENT || persona == UniversityAgent.Persona.INT_Y4_STUDENT) {
                                        CHANCE_ASKS = UniversityRoutePlan.CHANCE_INT_ASK;
                                    }
                                    else if (persona == UniversityAgent.Persona.EXT_Y1_STUDENT || persona == UniversityAgent.Persona.EXT_Y2_STUDENT || persona == UniversityAgent.Persona.EXT_Y3_STUDENT || persona == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                        CHANCE_ASKS = UniversityRoutePlan.CHANCE_EXT_ASK;
                                    }
                                    else if (persona == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || persona == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || persona == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || persona == UniversityAgent.Persona.INT_Y4_ORG_STUDENT) {
                                        CHANCE_ASKS = UniversityRoutePlan.CHANCE_INTORG_ASK;
                                    }
                                    else {
                                        CHANCE_ASKS = UniversityRoutePlan.CHANCE_EXTORG_ASK;
                                    }

                                    if (x < CHANCE_ASKS && agentMovement.getRoutePlan().MAX_CLASS_ASKS > 0) {
                                        agentMovement.forceStationedInteraction(UniversityAgent.Type.PROFESSOR);
                                        agentMovement.setStationInteracting(false);
                                        agentMovement.getRoutePlan().MAX_CLASS_ASKS--;
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.THROW_TRASH){
                        if (action.getName() == UniversityAction.Name.GO_TO_TRASH) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(Trash.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    }
                                    else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else {
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        if (agentMovement.getRoutePlan().isFromStudying()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromStudying(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromClass()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromClass(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromLunch()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromLunch(false);
                                        }
                                        else {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_LUNCH) {
                        if (action.getName() == UniversityAction.Name.GO_TO_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseStall();
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.QUEUE_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.CHECKOUT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.EATING_LUNCH) {
                        if (action.getName() == UniversityAction.Name.FIND_SEAT_CAFETERIA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(EatTable.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.LUNCH_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double MAX = 0;
                                double MAX_DRINK = 0;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;
                                if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT) {
                                    MAX = UniversityRoutePlan.EXT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){
                                    MAX = UniversityRoutePlan.INT_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){
                                    MAX = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.INT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){
                                    MAX = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_BATHROOM_STUDYING;
                                    MAX_DRINK = UniversityRoutePlan.EXT_ORG_CHANCE_NEEDS_DRINK_STUDYING;
                                }
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor()!=null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_HOME) {
                        if (action.getName() == UniversityAction.Name.LEAVE_BUILDING) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.despawn();
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_STAFF){
                        if (action.getName() == UniversityAction.Name.GO_TO_CABINET) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseStaffroomGoal(Cabinet.class);
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.CHECK_CABINET){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_TO_TABLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseStaffroomGoal(OfficeTable.class);
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.CHECK_TABLE){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_TO_STAFF) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseStaffroomGoal(Chair.class)){
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.WAIT_FOR_STAFF){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                    }
                    else if(state.getName() == UniversityState.Name.WAIT_INFRONT_OF_CLASS){
                        if (action.getName() == UniversityAction.Name.GO_TO_WAIT_AREA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getWaitPatch() == null) {
                                if(!agentMovement.chooseWaitPatch(agentMovement.getCurrentState().getClassroomID())){
                                    System.out.println("False wait patch");
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.WAIT_FOR_CLASS){
                            agentMovement.setSimultaneousInteractionAllowed(true);
//                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                    }

                    break;

                case PROFESSOR:
                    if (state.getName() == UniversityState.Name.GOING_TO_SECURITY) {
                        if (action.getName() == UniversityAction.Name.GOING_TO_SECURITY_QUEUE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getSecurities().get(0).getAmenityBlocks().get(1).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getSecurities().get(0));
                            }
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_THROUGH_SCANNER) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                if (!agentMovement.isStationInteracting()) {
                                    int CHANCE_GUARD_INTERACT = 0;
                                    if (persona == UniversityAgent.Persona.STRICT_PROFESSOR) {
                                        CHANCE_GUARD_INTERACT = UniversityRoutePlan.CHANCE_SPROF_GUARD_INTERACT;
                                    }
                                    else {
                                        CHANCE_GUARD_INTERACT = UniversityRoutePlan.CHANCE_APROF_GUARD_INTERACT;
                                    }
                                    int x = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(100);
                                    if (x < CHANCE_GUARD_INTERACT) {
                                        agentMovement.forceStationedInteraction(UniversityAgent.Type.GUARD);
                                    }
                                }

                                agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                agentMovement.setDuration(agentMovement.getDuration() - 1);
                                if (agentMovement.getDuration() <= 0) {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.setStationInteracting(false);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.WANDERING_AROUND) {
                        if (action.getName() == UniversityAction.Name.FIND_BENCH || action.getName() == UniversityAction.Name.FIND_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (action.getName() == UniversityAction.Name.FIND_BENCH) {
                                    if (!agentMovement.chooseGoal(Bench.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    if (!agentMovement.chooseGoal(Bulletin.class)) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                    else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.SIT_ON_BENCH || action.getName() == UniversityAction.Name.VIEW_BULLETIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.NEEDS_BATHROOM) {
                        if (action.getName() == UniversityAction.Name.GO_TO_BATHROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Toilet.class)) {
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() - 1, agentMovement.getRoutePlan().addWaitingRoute(6,0,agent));
                                    agentMovement.setPreviousState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() -1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if(agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
//                                    if (agentMovement.getRoutePlan().isFromStudying()) {
//                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
//                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        agentMovement.getRoutePlan().setFromStudying(false);
//                                    }
//                                    else if (agentMovement.getRoutePlan().isFromClass()) {
//                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
//                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        agentMovement.getRoutePlan().setFromClass(false);
//                                    }
//                                    else if (agentMovement.getRoutePlan().isFromLunch()) {
//                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
//                                        agentMovement.setNextState(agentMovement.getReturnIndex() - 1);
//                                        agentMovement.setStateIndex(agentMovement.getReturnIndex());
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                        agentMovement.getRoutePlan().setFromLunch(false);
//                                    }
//                                    else {
//                                        agentMovement.setNextState(agentMovement.getStateIndex());
//                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
//                                        agentMovement.setActionIndex(0);
//                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                        agentMovement.resetGoal();
//                                    }
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.RELIEVE_IN_CUBICLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agent.getAgentMovement().getDuration());
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.WASH_IN_SINK) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseBathroomGoal(Sink.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    }
                                    else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else {
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        if (agentMovement.getRoutePlan().isFromStudying()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if(agentMovement.getGoalAttractor()!=null){
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromStudying(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromClass()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromClass(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromLunch()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromLunch(false);
                                        }
                                        else {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.NEEDS_DRINK) {
                        if (action.getName() == UniversityAction.Name.GO_TO_DRINKING_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                int fountainIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(Main.universitySimulator.getUniversity().getFountains().size());
                                agentMovement.setGoalQueueingPatchField(Main.universitySimulator.getUniversity().getFountains().get(fountainIndex).getAmenityBlocks().get(0).getPatch().getQueueingPatchField().getKey());
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getFountains().get(fountainIndex));
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.QUEUE_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.DRINK_FOUNTAIN) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                if (agentMovement.getRoutePlan().isFromStudying()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(false);
                                }
                                else if (agentMovement.getRoutePlan().isFromClass()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(false);
                                }
                                else if (agentMovement.getRoutePlan().isFromLunch()) {
                                    agentMovement.leaveQueue();
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                    agentMovement.setNextState(agentMovement.getReturnIndex());
                                    agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(false);
                                }
                                else {
                                    agentMovement.leaveQueue();
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_STUDY) {
                        if (action.getName() == UniversityAction.Name.GO_TO_STUDY_ROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(StudyTable.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex() + 1);
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 2);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                    else {
                                        if (agentMovement.getCurrentPath().getPath().size() <= 3) {
                                            while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                                agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                                agentMovement.reachPatchInPath();
                                                if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                    agentMovement.setActionIndex(0);
                                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.STUDYING) {
                        if (action.getName() == UniversityAction.Name.STUDY_AREA_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double MAX = UniversityRoutePlan.PROF_CHANCE_NEEDS_BATHROOM_STUDYING;
                                double MAX_DRINK = UniversityRoutePlan.PROF_CHANCE_NEEDS_DRINK_STUDYING;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;

                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 2);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromStudying(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_CLASS_PROFESSOR) {
                        if (action.getName() == UniversityAction.Name.GO_TO_CLASSROOM) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if(agentMovement.chooseClassroomGoal(ProfTable.class, agentMovement.getCurrentState().getClassroomID()))
                                {
//                                    System.out.println("Going to wait Area");
//                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() - 1, agentMovement.getRoutePlan().addWaitingRoute(agentMovement.getCurrentState().getClassroomID(),agentMovement.getCurrentState().getTickClassStart(),agent));
//                                    agentMovement.setPreviousState(agentMovement.getStateIndex());
//                                    agentMovement.setStateIndex(agentMovement.getStateIndex() -1);
//                                    agentMovement.setActionIndex(0);
//                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
//                                    if(agentMovement.getGoalAttractor() != null) {
//                                        agentMovement.getGoalAttractor().setIsReserved(false);
//                                    }
//                                    agentMovement.resetGoal();
                                }
                            }
                            else if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                    }
                                }
                                else {
                                    if (agentMovement.getCurrentPath().getPath().size() <= 2) {
                                        while (!agentMovement.getCurrentPath().getPath().isEmpty()) {
                                            agentMovement.setPosition(agentMovement.getCurrentPath().getPath().peek().getPatchCenterCoordinates());
                                            agentMovement.reachPatchInPath();
                                            if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                                agentMovement.setNextState(agentMovement.getStateIndex());
                                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                                agentMovement.setActionIndex(0);
                                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.WAIT_FOR_CLASS_PROFESSOR) {
                        if (action.getName() == UniversityAction.Name.SIT_PROFESSOR_TABLE) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.IN_CLASS_PROFESSOR) {
                        if (action.getName() == UniversityAction.Name.CLASSROOM_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if(agentMovement.getGoalAttractor()!=null){
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double MAX = UniversityRoutePlan.PROF_CHANCE_NEEDS_BATHROOM_STUDYING;
                                double MAX_DRINK = UniversityRoutePlan.PROF_CHANCE_NEEDS_DRINK_STUDYING;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 3);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromClass(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_LUNCH) {
                        if (action.getName() == UniversityAction.Name.GO_TO_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalQueueingPatchField() == null) {
                                agentMovement.chooseStall();
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.joinQueue();
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.QUEUE_VENDOR) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                }
                            }
                            else {
                                agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.CHECKOUT) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            agentMovement.setDuration(agentMovement.getDuration() - 1);
                            if (agentMovement.getDuration() <= 0) {
                                agentMovement.leaveQueue();
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.EATING_LUNCH) {
                        if (action.getName() == UniversityAction.Name.FIND_SEAT_CAFETERIA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(EatTable.class)) {
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                        if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                            agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.LUNCH_STAY_PUT) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                                double CHANCE = Simulator.roll();
                                double MAX = UniversityRoutePlan.PROF_CHANCE_NEEDS_BATHROOM_STUDYING;
                                double MAX_DRINK = UniversityRoutePlan.PROF_CHANCE_NEEDS_DRINK_STUDYING;
                                double MAX_THROW = UniversityRoutePlan.THROW_CHANCE;
                                if (CHANCE < MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("BATHROOM", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK && CHANCE >= MAX && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("DRINK", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                                else if (CHANCE < MAX+MAX_DRINK+MAX_THROW  && CHANCE >= MAX+MAX_DRINK  && agentMovement.getRoutePlan().getUrgentCtr() >= 1) {
                                    agentMovement.setReturnIndex(agentMovement.getStateIndex() - 1);
                                    agentMovement.getRoutePlan().getCurrentRoutePlan().add(agentMovement.getStateIndex() + 1, agentMovement.getRoutePlan().addUrgentRoute("TRASH", agent));
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    if (agentMovement.getGoalAttractor() != null) {
                                        agentMovement.getGoalAttractor().setIsReserved(false);
                                    }
                                    agentMovement.resetGoal();
                                    agentMovement.getRoutePlan().setFromLunch(true);
                                    agentMovement.getRoutePlan().setUrgentCtr(agentMovement.getRoutePlan().getUrgentCtr() - 3);
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_HOME) {
                        if (action.getName() == UniversityAction.Name.LEAVE_BUILDING) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.setGoalAmenity(Main.universitySimulator.getUniversity().getUniversityGates().get(0));
                                agentMovement.setGoalAttractor(agentMovement.getGoalAmenity().getAttractors().get(0));
                            }

                            if (agentMovement.chooseNextPatchInPath()) {
                                agentMovement.faceNextPosition();
                                agentMovement.moveSocialForce();
                                if (agentMovement.hasReachedNextPatchInPath()) {
                                    agentMovement.reachPatchInPath();
                                    if (agentMovement.hasAgentReachedFinalPatchInPath()) {
                                        agentMovement.despawn();
                                    }
                                }
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.GOING_TO_STAFF){
                        if (action.getName() == UniversityAction.Name.GO_TO_CABINET) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseStaffroomGoal(Cabinet.class);
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.CHECK_CABINET){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_TO_TABLE) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                agentMovement.chooseStaffroomGoal(OfficeTable.class);
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.CHECK_TABLE){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                        else if (action.getName() == UniversityAction.Name.GO_TO_STAFF) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseStaffroomGoal(Chair.class)){
                                    agentMovement.setNextState(agentMovement.getStateIndex());
                                    agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                    agentMovement.setActionIndex(0);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.resetGoal();
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.WAIT_FOR_STAFF){
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                    }
                    else if (state.getName() == UniversityState.Name.THROW_TRASH) {
                        if (action.getName() == UniversityAction.Name.GO_TO_TRASH) {
                            agentMovement.setSimultaneousInteractionAllowed(true);
                            if (agentMovement.getGoalAmenity() == null) {
                                if (!agentMovement.chooseGoal(Trash.class)) {
                                    if (agentMovement.getRoutePlan().isFromStudying()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromStudying(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromClass()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromClass(false);
                                    }
                                    else if (agentMovement.getRoutePlan().isFromLunch()) {
                                        agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                        agentMovement.setNextState(agentMovement.getReturnIndex());
                                        agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                        agentMovement.getRoutePlan().setFromLunch(false);
                                    }
                                    else {
                                        agentMovement.setNextState(agentMovement.getStateIndex());
                                        agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                        agentMovement.setActionIndex(0);
                                        agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                        agentMovement.resetGoal();
                                    }
                                }
                                else {
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else {
                                    agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                                    agentMovement.setDuration(agentMovement.getDuration() - 1);
                                    if (agentMovement.getDuration() <= 0) {
                                        if (agentMovement.getRoutePlan().isFromStudying()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromStudying(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromClass()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromClass(false);
                                        }
                                        else if (agentMovement.getRoutePlan().isFromLunch()) {
                                            agentMovement.getRoutePlan().getCurrentRoutePlan().remove(agentMovement.getStateIndex());
                                            agentMovement.setNextState(agentMovement.getReturnIndex());
                                            agentMovement.setStateIndex(agentMovement.getReturnIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                            agentMovement.getRoutePlan().setFromLunch(false);
                                        }
                                        else {
                                            agentMovement.setNextState(agentMovement.getStateIndex());
                                            agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                            agentMovement.setActionIndex(0);
                                            agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                            if (agentMovement.getGoalAttractor() != null) {
                                                agentMovement.getGoalAttractor().setIsReserved(false);
                                            }
                                            agentMovement.resetGoal();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(state.getName() == UniversityState.Name.WAIT_INFRONT_OF_CLASS){
                        if (action.getName() == UniversityAction.Name.GO_TO_WAIT_AREA) {
                            agentMovement.setSimultaneousInteractionAllowed(false);
                            if (agentMovement.getGoalAmenity() == null) {
                                if(!agentMovement.chooseWaitPatch(agentMovement.getCurrentState().getClassroomID())){
                                    System.out.println("False wait patch");
                                }
                            }
                            else {
                                if (agentMovement.chooseNextPatchInPath()) {
                                    agentMovement.faceNextPosition();
                                    agentMovement.moveSocialForce();
                                    if (agentMovement.hasReachedNextPatchInPath()) {
                                        agentMovement.reachPatchInPath();
                                    }
                                }
                                else{
                                    agentMovement.setActionIndex(agentMovement.getActionIndex() + 1);
                                    agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                    agentMovement.setDuration(agentMovement.getCurrentAction().getDuration());
                                }
                            }
                        }
                        else if(action.getName() == UniversityAction.Name.WAIT_FOR_CLASS){
                            agentMovement.setSimultaneousInteractionAllowed(true);
//                            agentMovement.setCurrentAmenity(agentMovement.getGoalAmenity());
                            if (agentMovement.getCurrentAction().getDuration() <= 0) {
                                agentMovement.setNextState(agentMovement.getStateIndex());
                                agentMovement.setStateIndex(agentMovement.getStateIndex() + 1);
                                agentMovement.setActionIndex(0);
                                agentMovement.setCurrentAction(agentMovement.getCurrentState().getActions().get(agentMovement.getActionIndex()));
                                if (agentMovement.getGoalAttractor() != null) {
                                    agentMovement.getGoalAttractor().setIsReserved(false);
                                }
                                agentMovement.resetGoal();
                            }
                            else {
                                agentMovement.getCurrentAction().setDuration(agentMovement.getCurrentAction().getDuration() - 1);
                            }
                        }
                    }

                    break;
            }
        }


        if (agentMovement.isInteracting()) {
            agentMovement.interact();
        }
        else {
            List<Patch> patches = agentMovement.get7x7Field(agentMovement.getHeading(), true, agentMovement.getFieldOfViewAngle());
            UniversityAgent agent2 = null;
            for (Patch patch: patches) {
                for (Agent otherAgent: patch.getAgents()) {
                    UniversityAgent universityAgent = (UniversityAgent) otherAgent;
                    if (!universityAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), universityAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), agentMovement.getFieldOfViewAngle()))
                            if (Coordinates.isWithinFieldOfView(universityAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), universityAgent.getAgentMovement().getProposedHeading(), universityAgent.getAgentMovement().getFieldOfViewAngle())){
                                agentMovement.rollAgentInteraction(universityAgent);
                                if (agentMovement.isInteracting()) {
                                    agent2 = universityAgent;
                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
                                    currentPatchCount[universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
                                }
                            }
                    if (agentMovement.isInteracting())
                        break;
                }
                if (agentMovement.isInteracting())
                    break;
            }
//            patches = agentMovement.get3x3Field(agentMovement.getHeading(), true, Math.toRadians(270));
//            for (Patch patch: patches) {
//                for (Agent otherAgent: patch.getAgents()) {
//                    UniversityAgent universityAgent = (UniversityAgent) otherAgent;
//                    if (!universityAgent.getAgentMovement().isInteracting() && !agentMovement.isInteracting())
//                        if (Coordinates.isWithinFieldOfView(agentMovement.getPosition(), universityAgent.getAgentMovement().getPosition(), agentMovement.getProposedHeading(), Math.toRadians(270)))
//                            if (Coordinates.isWithinFieldOfView(universityAgent.getAgentMovement().getPosition(), agentMovement.getPosition(), universityAgent.getAgentMovement().getProposedHeading(), Math.toRadians(270))){
//                                agentMovement.rollAgentInteraction(universityAgent);
//                                if (agentMovement.isInteracting()) {
//                                    agent2 = universityAgent;
//                                    currentPatchCount[agentMovement.getCurrentPatch().getMatrixPosition().getRow()][agentMovement.getCurrentPatch().getMatrixPosition().getColumn()]++;
//                                    currentPatchCount[universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getRow()][universityAgent.getAgentMovement().getCurrentPatch().getMatrixPosition().getColumn()]++;
//                                }
//                            }
//                    if (agentMovement.isInteracting())
//                        break;
//                }
//
//                if (agentMovement.isInteracting())
//                    break;
//            }
            if (agentMovement.isInteracting() && agentMovement.getInteractionDuration() == 0) {
                agentMovement.setInteracting(false);
                agentMovement.setInteractionType(null);
            }
            if (agent2 != null && agent2.getAgentMovement().isInteracting() && agent2.getAgentMovement().getInteractionDuration() == 0){
                agent2.getAgentMovement().setInteracting(false);
                agent2.getAgentMovement().setInteractionType(null);
            }
        }
        agent.getAgentGraphic().change();
    }

    private void spawnAgent(University university, long currentTick) {
        UniversityGate gate = university.getUniversityGates().get(1);
        double spawnChance;
        if (currentTick >= 540 && currentTick < 720 || currentTick >= 1800 && currentTick < 1980
                || currentTick >= 3060 && currentTick < 3240 || currentTick >= 4320 && currentTick < 4500
                || currentTick >= 5580 && currentTick < 5760 || currentTick >= 6840 && currentTick < 7020){
            spawnChance = gate.getChancePerTick();
        }
        else{
            spawnChance = gate.getChancePerTick() / 10; // 0.03
        }
//        double spawnChance = gate.getChancePerTick();
        UniversityAgent agent = null;

        for (int i = 0; i < 2; i++){
            Gate.GateBlock spawner = gate.getSpawners().get(i);
            double CHANCE = Simulator.roll();
            if (CHANCE < spawnChance && university.getUnspawnedAgents().size() > 0){
                if (university.getUnspawnedAgents().size() > 0){
                    agent = university.getUnspawnedAgents().get(Simulator.RANDOM_NUMBER_GENERATOR.nextInt(university.getUnspawnedAgents().size()));
                    if (agent.getType() == UniversityAgent.Type.STUDENT && UniversityAgent.studentCount < university.getMAX_STUDENTS() && currentStudentCount < university.getMAX_CURRENT_STUDENTS()){
                        agent.setAgentMovement(new UniversityAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick));
                        university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        currentStudentCount++;
                        UniversityAgent.studentCount++;
                        UniversityAgent.agentCount++;
                    }
                    else if (agent.getType() == UniversityAgent.Type.PROFESSOR && UniversityAgent.professorCount < university.getMAX_PROFESSORS() && currentProfessorCount < university.getMAX_CURRENT_PROFESSORS()){
                        agent.setAgentMovement(new UniversityAgentMovement(spawner.getPatch(), agent, 1.27, spawner.getPatch().getPatchCenterCoordinates(), currentTick));
                        university.getAgentPatchSet().add(agent.getAgentMovement().getCurrentPatch());
                        currentProfessorCount++;
                        UniversityAgent.professorCount++;
                        UniversityAgent.agentCount++;
                    }
                }
            }
        }
    }

    public void replenishStaticVars() {
        currentProfessorCount = 0;
        currentStudentCount = 0;
        currentNonverbalCount = 0;
        currentCooperativeCount = 0;
        currentExchangeCount = 0;
        averageNonverbalDuration = 0;
        averageCooperativeDuration = 0;
        averageExchangeDuration = 0;
        currentStudentStudentCount = 0;
        currentStudentProfCount = 0;
        currentStudentGuardCount = 0;
        currentStudentJanitorCount = 0;
        currentProfProfCount = 0;
        currentProfGuardCount = 0;
        currentProfJanitorCount = 0;
        currentGuardJanitorCount = 0;
        currentJanitorJanitorCount = 0;
        currentStudentStaffCount = 0;
        currentProfStaffCount = 0;
        currentStaffStaffCount = 0;
        currentPatchCount = new int[university.getRows()][university.getColumns()];
        compiledCurrentProfessorCount = new int[9001];
        compiledCurrentStudentCount = new int[9001];
        compiledCurrentNonverbalCount = new int[9001];
        compiledCurrentCooperativeCount = new int[9001];
        compiledCurrentExchangeCount = new int[9001];
        compiledAverageNonverbalDuration = new float[9001];
        compiledAverageCooperativeDuration = new float[9001];
        compiledAverageExchangeDuration = new float[9001];
        compiledCurrentStudentStudentCount = new int[9001];
        compiledCurrentStudentProfCount = new int[9001];
        compiledCurrentStudentGuardCount = new int[9001];
        compiledCurrentStudentJanitorCount = new int[9001];
        compiledCurrentStudentStaffCount = new int[9001];
        compiledCurrentProfProfCount = new int[9001];
        compiledCurrentProfGuardCount = new int[9001];
        compiledCurrentProfJanitorCount = new int[9001];
        compiledCurrentProfStaffCount = new int[9001];
        compiledCurrentGuardJanitorCount = new int[9001];
        compiledCurrentJanitorJanitorCount = new int[9001];
        compiledCurrentStaffStaffCount = new int[9001];
    }

    public static void exportToCSV() throws Exception{
        PrintWriter writer = new PrintWriter("University SocialSim Statistics.csv");
        StringBuilder sb = new StringBuilder();
        sb.append("Current Professor Count");
        sb.append(",");
        sb.append("Current Student Count");
        sb.append(",");
        sb.append("Current Nonverbal Count");
        sb.append(",");
        sb.append("Current Cooperative Count");
        sb.append(",");
        sb.append("Current Exchange Count");
        sb.append(",");
        sb.append("Average Nonverbal Duration");
        sb.append(",");
        sb.append("Average Cooperative Duration");
        sb.append(",");
        sb.append("Average Exchange Duration");
        sb.append(",");
        sb.append("Current Student Student Count");
        sb.append(",");
        sb.append("Current Student Prof Count");
        sb.append(",");
        sb.append("Current Student Guard Count");
        sb.append(",");
        sb.append("Current Student Janitor Count");
        sb.append(",");
        sb.append("Current Prof Prof Count");
        sb.append(",");
        sb.append("Current Prof Guard Count");
        sb.append(",");
        sb.append("Current Prof Janitor Count");
        sb.append(",");
        sb.append("Current Guard Janitor Count");
        sb.append(",");
        sb.append("Current Janitor Janitor Count");
        sb.append(",");
        sb.append("Current Student Staff Count");
        sb.append(",");
        sb.append("Current Prof Staff Count");
        sb.append(",");
        sb.append("Current Staff Staff Count");
        sb.append("\n");
        for (int i = 0; i < 9001; i++){
            sb.append(compiledCurrentProfessorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStudentCount[i]);
            sb.append(",");
            sb.append(compiledCurrentNonverbalCount[i]);
            sb.append(",");
            sb.append(compiledCurrentCooperativeCount[i]);
            sb.append(",");
            sb.append(compiledCurrentExchangeCount[i]);
            sb.append(",");
            sb.append(compiledAverageNonverbalDuration[i]);
            sb.append(",");
            sb.append(compiledAverageCooperativeDuration[i]);
            sb.append(",");
            sb.append(compiledAverageExchangeDuration[i]);
            sb.append(",");
            sb.append(compiledCurrentStudentStudentCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStudentProfCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStudentGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStudentJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStudentStaffCount[i]);
            sb.append(",");
            sb.append(compiledCurrentProfProfCount[i]);
            sb.append(",");
            sb.append(compiledCurrentProfGuardCount[i]);
            sb.append(",");
            sb.append(compiledCurrentProfJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentProfStaffCount[i]);
            sb.append(",");
            sb.append(compiledCurrentGuardJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentJanitorJanitorCount[i]);
            sb.append(",");
            sb.append(compiledCurrentStaffStaffCount[i]);
            sb.append("\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    public static void exportHeatMap() throws Exception {
        PrintWriter writer = new PrintWriter("University SocialSim Heat Map.csv");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < currentPatchCount.length; i++){
            for (int j = 0 ; j < currentPatchCount[i].length; j++){
                sb.append(currentPatchCount[i][j]);
                if (j != currentPatchCount[i].length - 1)
                    sb.append(",");
            }
            sb.append("\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }
}