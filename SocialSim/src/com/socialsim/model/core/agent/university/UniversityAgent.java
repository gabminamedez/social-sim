package com.socialsim.model.core.agent.university;

import com.socialsim.controller.university.graphics.agent.UniversityAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.simulator.UniversitySimulator;

import java.util.Objects;

public class UniversityAgent extends Agent {

    private static int agentCount = 0;
    private static int guardCount = 0;
    private static int janitorCount = 0;
    private static int officerCount = 0;
    private static int professorCount = 0;
    private static int studentCount = 0;

    private final int id;
    private final UniversityAgent.Type type;
    private final UniversityAgent.Gender gender;
    private UniversityAgent.AgeGroup ageGroup = null;
    private UniversityAgent.Persona persona = null;

    private final UniversityAgentGraphic agentGraphic;
    // private final AgentMovement agentMovement;

    public static final UniversityAgent.UniversityAgentFactory agentFactory;

    static {
        agentFactory = new UniversityAgent.UniversityAgentFactory();
    }

    private UniversityAgent(UniversityAgent.Type type, Patch spawnPatch, boolean inOnStart) {
        this.id = agentCount;
        this.type = type;

        if (type == UniversityAgent.Type.GUARD) {
            UniversityAgent.guardCount++;
        }
        else if (type == UniversityAgent.Type.JANITOR) {
            UniversityAgent.janitorCount++;
        }
        else if (type == Type.OFFICER) {
            UniversityAgent.officerCount++;
        }
        else if (type == UniversityAgent.Type.PROFESSOR) {
            UniversityAgent.professorCount++;
        }
        else if (type == UniversityAgent.Type.STUDENT) {
            UniversityAgent.studentCount++;
        }
        UniversityAgent.agentCount++;

        this.gender = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        if (this.type == Type.GUARD || this.type == Type.JANITOR || this.type == Type.OFFICER) {
            this.ageGroup = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = null;
        }
        else if (this.type == Type.PROFESSOR) {
            this.ageGroup = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;

            boolean isStrict = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isStrict) {
                this.persona = Persona.STRICT_PROFESSOR;
            }
            else {
                this.persona = Persona.APPROACHABLE_PROFESSOR;
            }
        }
        else if (this.type == Type.STUDENT) {
            this.ageGroup = AgeGroup.FROM_15_TO_24;

            boolean isIntrovert = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            int yearLevel = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
            boolean isOrg = UniversitySimulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

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
        if (inOnStart) { // If the agent is already inside the environment on initialization
            spawnPatch.addAgent(this);
        }
        else {
            UniversityGate universityGate = (UniversityGate) spawnPatch.getAmenityBlock().getParent();
        }
        // this.agentMovement = new AgentMovement(universityGate, this, spawnPatch.getPatchCenterCoordinates());
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

    public UniversityAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

//    public AgentMovement getAgentMovement() {
//        return agentMovement;
//    }

    public static class UniversityAgentFactory extends Agent.AgentFactory {
        public static UniversityAgent create(UniversityAgent.Type type, Patch spawnPatch, boolean inOnStart) {
            return new UniversityAgent(type, spawnPatch, inOnStart);
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
        GUARD, JANITOR, OFFICER, PROFESSOR, STUDENT
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        INT_Y1_STUDENT, INT_Y2_STUDENT, INT_Y3_STUDENT, INT_Y4_STUDENT,
        INT_Y1_ORG_STUDENT, INT_Y2_ORG_STUDENT, INT_Y3_ORG_STUDENT, INT_Y4_ORG_STUDENT,
        EXT_Y1_STUDENT, EXT_Y2_STUDENT, EXT_Y3_STUDENT, EXT_Y4_STUDENT,
        EXT_Y1_ORG_STUDENT, EXT_Y2_ORG_STUDENT, EXT_Y3_ORG_STUDENT, EXT_Y4_ORG_STUDENT,
        STRICT_PROFESSOR, APPROACHABLE_PROFESSOR
    }

}