package com.socialsim.model.core.agent.university;

import com.socialsim.controller.university.graphics.agent.UniversityAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

public class UniversityAgent extends Agent {

    public static int agentCount = 0;
    public static int guardCount = 0;
    public static int janitorCount = 0;
    public static int professorCount = 0;
    public static int studentCount = 0;
    public static final double[][][] chancePerActionInteractionType = new double[][][]
    {
        {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 100}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
        {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 100, 0}, {0, 100, 0}, {0, 100, 0}, {0, 100, 0}},
        {{90, 0, 10}, {20, 0, 80}, {0, 0, 0}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {5, 20, 75}, {15, 30, 55}, {15, 30, 55}, {20, 0, 80}, {5, 20, 75}, {20, 0, 80}, {15, 30, 55}, {20, 0, 80}, {20, 0, 80}, {60, 0, 40}, {0, 0, 0}, {50, 0, 50}, {20, 0, 80}, {0, 0, 0}, {60, 0, 40}, {20, 0, 80}, {0, 0, 0}, {0, 30, 70}, {0, 30, 70}, {0, 0, 0}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {0, 20, 80}, {20, 0, 80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
        {{85, 0, 15}, {20, 0, 80}, {0, 0, 0}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {5, 20, 75}, {15, 30, 55}, {15, 30, 55}, {20, 0, 80}, {5, 20, 75}, {20, 0, 80}, {15, 30, 55}, {20, 0, 80}, {20, 0, 80}, {60, 0, 40}, {0, 0, 0}, {50, 0, 50}, {20, 0, 80}, {0, 0, 0}, {60, 0, 40}, {20, 0, 80}, {0, 0, 0}, {0, 30, 70}, {0, 30, 70}, {0, 0, 0}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {20, 0, 80}, {0, 20, 80}, {20, 0, 80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
        {{80, 0, 20}, {10, 0, 90}, {0, 0, 0}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {5, 20, 75}, {10, 30, 60}, {10, 30, 60}, {10, 0, 90}, {0, 20, 80}, {10, 0, 90}, {10, 30, 60}, {10, 0, 90}, {10, 0, 90}, {60, 0, 40}, {0, 0, 0}, {50, 0, 50}, {10, 0, 90}, {0, 0, 0}, {60, 0, 40}, {10, 0, 90}, {0, 0, 0}, {0, 30, 70}, {0, 30, 70}, {0, 0, 0}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {0, 20, 80}, {10, 0, 90}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
        {{80, 0, 20}, {10, 0, 90}, {0, 0, 0}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {5, 20, 75}, {10, 30, 60}, {10, 30, 60}, {10, 0, 90}, {0, 20, 80}, {10, 0, 90}, {10, 30, 60}, {10, 0, 90}, {10, 0, 90}, {60, 0, 40}, {0, 0, 0}, {50, 0, 50}, {10, 0, 90}, {0, 0, 0}, {60, 0, 40}, {10, 0, 90}, {0, 0, 0}, {0, 30, 70}, {0, 30, 70}, {0, 0, 0}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {10, 0, 90}, {0, 20, 80}, {10, 0, 90}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
        {{90, 0, 10}, {75, 0, 25}, {0, 0, 0}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {0, 0, 0}, {0, 0, 0}, {15, 30, 55}, {75, 0, 25}, {5, 20, 75}, {75, 0, 25}, {15, 30, 55}, {75, 0, 25}, {75, 0, 25}, {60, 0, 40}, {0, 0, 0}, {50, 0, 50}, {75, 0, 25}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 40, 60}, {0, 50, 50}, {0, 0, 0}, {0, 40, 60}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {75, 0, 25}, {0, 20, 80}, {75, 0, 25}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
        {{80, 0, 20}, {65, 0, 35}, {0, 0, 0}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {0, 0, 0}, {0, 0, 0}, {10, 30, 60}, {65, 0, 35}, {0, 20, 80}, {65, 0, 35}, {10, 30, 60}, {65, 0, 35}, {65, 0, 35}, {60, 0, 40}, {0, 0, 0}, {50, 0, 50}, {65, 0, 35}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 40, 60}, {0, 50, 50}, {0, 0, 0}, {0, 40, 60}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {65, 0, 35}, {0, 20, 80}, {65, 0, 35}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
    };
    private final int id;
    private final UniversityAgent.Type type;
    private final UniversityAgent.Gender gender;
    private UniversityAgent.AgeGroup ageGroup = null;
    private UniversityAgent.Persona persona = null;
    private final boolean inOnStart;

    private final UniversityAgentGraphic agentGraphic;
    private UniversityAgentMovement agentMovement;

    public static final UniversityAgent.UniversityAgentFactory agentFactory;

    public static final double INT_CHANCE_SPAWN = 0.10, INT_ORG_CHANCE_SPAWN = 0.10,
            EXT_CHANCE_SPAWN = 0.10, EXT_ORG_CHANCE_SPAWN = 0.10,
            STRICT_PROF_CHANCE_SPAWN = 0.10, APPROACHABLE_PROF_CHANCE_SPAWN = 0.10;

    static {
        agentFactory = new UniversityAgent.UniversityAgentFactory();
    }

    private UniversityAgent(UniversityAgent.Type type, Patch spawnPatch, boolean inOnStart, long currentTick) {
        this.id = agentCount;
        this.type = type;
        this.inOnStart = inOnStart;

        if (type == UniversityAgent.Type.GUARD) {
            UniversityAgent.guardCount++;
        }
        else if (type == UniversityAgent.Type.JANITOR) {
            UniversityAgent.janitorCount++;
        }
        else if (type == UniversityAgent.Type.PROFESSOR) {
            UniversityAgent.professorCount++;
        }
        else if (type == UniversityAgent.Type.STUDENT) {
            UniversityAgent.studentCount++;
        }
        UniversityAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        if (this.type == Type.GUARD) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.GUARD;
        }
        else if(this.type == Type.JANITOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.JANITOR;
        }
        else if (this.type == Type.PROFESSOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;

            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isStrict) {
                this.persona = Persona.STRICT_PROFESSOR;
            }
            else {
                this.persona = Persona.APPROACHABLE_PROFESSOR;
            }
        }
        else if (this.type == Type.STUDENT) {
            this.ageGroup = AgeGroup.FROM_15_TO_24;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            int yearLevel = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
            boolean isOrg = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

            if (isIntrovert && yearLevel == 1 && !isOrg) {
                this.persona = Persona.INT_Y1_STUDENT;
            }
            else if (isIntrovert && yearLevel == 2 && !isOrg) {
                this.persona = Persona.INT_Y2_STUDENT;
            }
            else if (isIntrovert && yearLevel == 3 && !isOrg) {
                this.persona = Persona.INT_Y3_STUDENT;
            }
            else if (isIntrovert && yearLevel == 4 && !isOrg) {
                this.persona = Persona.INT_Y4_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 1 && !isOrg) {
                this.persona = Persona.EXT_Y1_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 2 && !isOrg) {
                this.persona = Persona.EXT_Y2_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 3 && !isOrg) {
                this.persona = Persona.EXT_Y3_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 4 && !isOrg) {
                this.persona = Persona.EXT_Y4_STUDENT;
            }
            else if (isIntrovert && yearLevel == 1 && isOrg) {
                this.persona = Persona.INT_Y1_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 2 && isOrg) {
                this.persona = Persona.INT_Y2_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 3 && isOrg) {
                this.persona = Persona.INT_Y3_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 4 && isOrg) {
                this.persona = Persona.INT_Y4_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 1 && isOrg) {
                this.persona = Persona.EXT_Y1_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 2 && isOrg) {
                this.persona = Persona.EXT_Y2_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 3 && isOrg) {
                this.persona = Persona.EXT_Y3_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 4 && isOrg) {
                this.persona = Persona.EXT_Y4_ORG_STUDENT;
            }
        }

        this.agentGraphic = new UniversityAgentGraphic(this);
        this.agentMovement = new UniversityAgentMovement(spawnPatch, this, 1.27, spawnPatch.getPatchCenterCoordinates(), currentTick);
    }


    private UniversityAgent(UniversityAgent.Type type, boolean inOnStart) {
        this.id = agentCount;
        this.type = type;
        this.inOnStart = inOnStart;

        if (type == UniversityAgent.Type.GUARD) {
            UniversityAgent.guardCount++;
        }
        else if (type == UniversityAgent.Type.JANITOR) {
            UniversityAgent.janitorCount++;
        }
        else if (type == UniversityAgent.Type.PROFESSOR) {
            UniversityAgent.professorCount++;
        }
        else if (type == UniversityAgent.Type.STUDENT) {
            UniversityAgent.studentCount++;
        }
        UniversityAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        if (this.type == Type.GUARD) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.GUARD;
        }
        else if(this.type == Type.JANITOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.JANITOR;
        }
        else if (this.type == Type.PROFESSOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;

            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isStrict) {
                this.persona = Persona.STRICT_PROFESSOR;
            }
            else {
                this.persona = Persona.APPROACHABLE_PROFESSOR;
            }
        }
        else if (this.type == Type.STUDENT) {
            this.ageGroup = AgeGroup.FROM_15_TO_24;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            int yearLevel = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
            boolean isOrg = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

            if (isIntrovert && yearLevel == 1 && !isOrg) {
                this.persona = Persona.INT_Y1_STUDENT;
            }
            else if (isIntrovert && yearLevel == 2 && !isOrg) {
                this.persona = Persona.INT_Y2_STUDENT;
            }
            else if (isIntrovert && yearLevel == 3 && !isOrg) {
                this.persona = Persona.INT_Y3_STUDENT;
            }
            else if (isIntrovert && yearLevel == 4 && !isOrg) {
                this.persona = Persona.INT_Y4_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 1 && !isOrg) {
                this.persona = Persona.EXT_Y1_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 2 && !isOrg) {
                this.persona = Persona.EXT_Y2_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 3 && !isOrg) {
                this.persona = Persona.EXT_Y3_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 4 && !isOrg) {
                this.persona = Persona.EXT_Y4_STUDENT;
            }
            else if (isIntrovert && yearLevel == 1 && isOrg) {
                this.persona = Persona.INT_Y1_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 2 && isOrg) {
                this.persona = Persona.INT_Y2_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 3 && isOrg) {
                this.persona = Persona.INT_Y3_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 4 && isOrg) {
                this.persona = Persona.INT_Y4_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 1 && isOrg) {
                this.persona = Persona.EXT_Y1_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 2 && isOrg) {
                this.persona = Persona.EXT_Y2_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 3 && isOrg) {
                this.persona = Persona.EXT_Y3_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 4 && isOrg) {
                this.persona = Persona.EXT_Y4_ORG_STUDENT;
            }
        }
        this.agentGraphic = new UniversityAgentGraphic(this);
        this.agentMovement = null;
    }

    public int getId() {
        return id;
    }

    public UniversityAgent.Type getType() {
        return type;
    }

    public UniversityAgent.Gender getGender() {
        return gender;
    }

    public UniversityAgent.AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public UniversityAgent.Persona getPersona() {
        return persona;
    }

    public boolean getInOnStart() {
        return inOnStart;
    }

    public UniversityAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

    public UniversityAgentMovement getAgentMovement() {
        return agentMovement;
    }

    public void setAgentMovement(UniversityAgentMovement agentMovement) {
        this.agentMovement = agentMovement;
    }

    public static class UniversityAgentFactory extends Agent.AgentFactory {
        public static UniversityAgent create(UniversityAgent.Type type, Patch spawnPatch, boolean inOnStart, long currentTick) {
            return new UniversityAgent(type, spawnPatch, inOnStart, currentTick);
        }
        public static UniversityAgent create(UniversityAgent.Type type, boolean inOnStart) {
            return new UniversityAgent(type, inOnStart);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniversityAgent agent = (UniversityAgent) o;

        return id == agent.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public enum Type {
        GUARD, JANITOR, PROFESSOR, STUDENT
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        GUARD(0), JANITOR(1),
        INT_Y1_STUDENT(2), INT_Y2_STUDENT(2), INT_Y3_STUDENT(2), INT_Y4_STUDENT(2),
        INT_Y1_ORG_STUDENT(3), INT_Y2_ORG_STUDENT(3), INT_Y3_ORG_STUDENT(3), INT_Y4_ORG_STUDENT(3),
        EXT_Y1_STUDENT(4), EXT_Y2_STUDENT(4), EXT_Y3_STUDENT(4), EXT_Y4_STUDENT(4),
        EXT_Y1_ORG_STUDENT(5), EXT_Y2_ORG_STUDENT(5), EXT_Y3_ORG_STUDENT(5), EXT_Y4_ORG_STUDENT(5),
        STRICT_PROFESSOR(6), APPROACHABLE_PROFESSOR(7);

        final int ID;
        Persona(int ID){
            this.ID = ID;
        }
        public int getID() {
            return ID;
        }
    }

}