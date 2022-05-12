package com.socialsim.model.core.agent.university;

import com.socialsim.controller.university.graphics.agent.UniversityAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.simulator.Simulator;
import java.util.Objects;

public class UniversityAgent extends Agent {

    private static int idCtr = 0;
    public static int agentCount = 0;
    public static int guardCount = 0;
    public static int janitorCount = 0;
    public static int professorCount = 0;
    public static int studentCount = 0;
    public static int staffCount = 0;
    private final int id;
    private final UniversityAgent.Type type;
    private final UniversityAgent.Gender gender;
    private UniversityAgent.AgeGroup ageGroup = null;
    private UniversityAgent.Persona persona = null;
    private PersonaActionGroup personaActionGroup = null;
    private final boolean inOnStart;
    private final UniversityAgentGraphic agentGraphic;
    private UniversityAgentMovement agentMovement;

    public static final UniversityAgent.UniversityAgentFactory agentFactory;

    static {
        agentFactory = new UniversityAgent.UniversityAgentFactory();
    }

    private UniversityAgent(UniversityAgent.Type type, boolean inOnStart) {
        this.id = idCtr++;
        this.type = type;
        this.inOnStart = inOnStart;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        if (this.type == Type.GUARD) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.GUARD;
            this.personaActionGroup = PersonaActionGroup.GUARD;
        }
        else if(this.type == Type.JANITOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.JANITOR;
            this.personaActionGroup = PersonaActionGroup.JANITOR;
        }
        else if (this.type == Type.PROFESSOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;

            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isStrict) {
                this.persona = Persona.STRICT_PROFESSOR;
                this.personaActionGroup = PersonaActionGroup.STRICT_PROFESSOR;
            }
            else {
                this.persona = Persona.APPROACHABLE_PROFESSOR;
                this.personaActionGroup = PersonaActionGroup.APPROACHABLE_PROFESSOR;
            }
        }
        else if (this.type == Type.STUDENT) {
            this.ageGroup = AgeGroup.FROM_15_TO_24;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            int yearLevel = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
            boolean isOrg = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();

            if (isIntrovert && yearLevel == 1 && !isOrg) {
                this.persona = Persona.INT_Y1_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_STUDENT;
            }
            else if (isIntrovert && yearLevel == 2 && !isOrg) {
                this.persona = Persona.INT_Y2_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_STUDENT;
            }
            else if (isIntrovert && yearLevel == 3 && !isOrg) {
                this.persona = Persona.INT_Y3_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_STUDENT;
            }
            else if (isIntrovert && yearLevel == 4 && !isOrg) {
                this.persona = Persona.INT_Y4_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 1 && !isOrg) {
                this.persona = Persona.EXT_Y1_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 2 && !isOrg) {
                this.persona = Persona.EXT_Y2_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 3 && !isOrg) {
                this.persona = Persona.EXT_Y3_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 4 && !isOrg) {
                this.persona = Persona.EXT_Y4_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_STUDENT;
            }
            else if (isIntrovert && yearLevel == 1 && isOrg) {
                this.persona = Persona.INT_Y1_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 2 && isOrg) {
                this.persona = Persona.INT_Y2_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 3 && isOrg) {
                this.persona = Persona.INT_Y3_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_ORG_STUDENT;
            }
            else if (isIntrovert && yearLevel == 4 && isOrg) {
                this.persona = Persona.INT_Y4_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.INT_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 1 && isOrg) {
                this.persona = Persona.EXT_Y1_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 2 && isOrg) {
                this.persona = Persona.EXT_Y2_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 3 && isOrg) {
                this.persona = Persona.EXT_Y3_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_ORG_STUDENT;
            }
            else if (!isIntrovert && yearLevel == 4 && isOrg) {
                this.persona = Persona.EXT_Y4_ORG_STUDENT;
                this.personaActionGroup = PersonaActionGroup.EXT_ORG_STUDENT;
            }
        }
        else if (this.type == Type.STAFF){
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
            this.persona = Persona.STAFF;
            this.personaActionGroup = PersonaActionGroup.STAFF;
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

    public UniversityAgent.PersonaActionGroup getPersonaActionGroup() {
        return personaActionGroup;
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
        public static UniversityAgent create(UniversityAgent.Type type, boolean inOnStart) {
            return new UniversityAgent(type, inOnStart);
        }
    }

    public static void clearUniversityAgentCounts() {
        idCtr = 0;
        agentCount = 0;
        guardCount = 0;
        janitorCount = 0;
        professorCount = 0;
        studentCount = 0;
        staffCount = 0;
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
        GUARD, JANITOR, PROFESSOR, STUDENT, STAFF
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
        STRICT_PROFESSOR(6), APPROACHABLE_PROFESSOR(7),
        STAFF(8);

        final int ID;
        Persona(int ID){
            this.ID = ID;
        }
        public int getID() {
            return ID;
        }
    }

    public enum PersonaActionGroup {
        GUARD(),
        JANITOR(),
        INT_STUDENT(),
        INT_ORG_STUDENT(),
        EXT_STUDENT(),
        EXT_ORG_STUDENT(),
        STRICT_PROFESSOR(),
        APPROACHABLE_PROFESSOR(),
        STAFF();

        final int ID;
        PersonaActionGroup(){
            this.ID = this.ordinal();
        }
        public int getID() {
            return ID;
        }
    }

}