package com.socialsim.model.core.agent.mall;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

import java.util.Objects;

public class MallAgent extends Agent {

    private static int agentCount = 0;
    private static int patronCount = 0;
    private static int staffStoreCount = 0;
    private static int staffRestoCount = 0;
    private static int staffKioskCount = 0;
    private static int guardCount = 0;

    private final int id;
    private final MallAgent.Type type;
    private final MallAgent.Gender gender;
    private MallAgent.AgeGroup ageGroup = null;
    private MallAgent.Persona persona = null;

//    private final MallAgentGraphic agentGraphic;
//    private final MallAgentMovement agentMovement;

    public static final MallAgent.MallAgentFactory agentFactory;

    static {
        agentFactory = new MallAgent.MallAgentFactory();
    }

    private MallAgent(MallAgent.Type type, Patch spawnPatch, boolean inOnStart) {
        this.id = agentCount;
        this.type = type;

        if (type == Type.PATRON) {
            MallAgent.patronCount++;
        }
        else if (type == Type.STAFF_STORE) {
            MallAgent.staffStoreCount++;
        }
        else if (type == Type.STAFF_RESTO) {
            MallAgent.staffStoreCount++;
        }
        else if (type == Type.STAFF_KIOSK) {
            MallAgent.staffStoreCount++;
        }
        else if (type == MallAgent.Type.GUARD) {
            MallAgent.guardCount++;
        }
        MallAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.FEMALE : MallAgent.Gender.MALE;

//        if (this.type == MallAgent.Type.GUARD) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_25_TO_54 : MallAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = MallAgent.Persona.GUARD;
//        }
//        else if(this.type == MallAgent.Type.JANITOR) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_25_TO_54 : MallAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = MallAgent.Persona.JANITOR;
//        }
//        else if(this.type == MallAgent.Type.OFFICER) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_25_TO_54 : MallAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = MallAgent.Persona.OFFICER;
//        }
//        else if (this.type == MallAgent.Type.PROFESSOR) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.AgeGroup.FROM_25_TO_54 : MallAgent.AgeGroup.FROM_55_TO_64;
//
//            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//            if (isStrict) {
//                this.persona = MallAgent.Persona.STRICT_PROFESSOR;
//            }
//            else {
//                this.persona = MallAgent.Persona.APPROACHABLE_PROFESSOR;
//            }
//        }
//        else if (this.type == MallAgent.Type.STUDENT) {
//            this.ageGroup = MallAgent.AgeGroup.FROM_15_TO_24;
//
//            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//            int yearLevel = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
//            boolean isOrg = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//
//            if (isIntrovert && yearLevel == 1 && !isOrg) {
//                this.persona = MallAgent.Persona.INT_Y1_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 2 && !isOrg) {
//                this.persona = MallAgent.Persona.INT_Y2_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 3 && !isOrg) {
//                this.persona = MallAgent.Persona.INT_Y3_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 4 && !isOrg) {
//                this.persona = MallAgent.Persona.INT_Y4_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 1 && !isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y1_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 2 && !isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y2_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 3 && !isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y3_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 4 && !isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y4_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 1 && isOrg) {
//                this.persona = MallAgent.Persona.INT_Y1_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 2 && isOrg) {
//                this.persona = MallAgent.Persona.INT_Y2_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 3 && isOrg) {
//                this.persona = MallAgent.Persona.INT_Y3_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 4 && isOrg) {
//                this.persona = MallAgent.Persona.INT_Y4_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 1 && isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y1_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 2 && isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y2_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 3 && isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y3_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 4 && isOrg) {
//                this.persona = MallAgent.Persona.EXT_Y4_ORG_STUDENT;
//            }
//        }
//
//        this.agentGraphic = new MallAgentGraphic(this);
//        if (inOnStart) { // If the agent is already inside the environment on initialization
//            // this.agentMovement = new MallAgentMovement(spawnPatch, this, 1.27, spawnPatch.getPatchCenterCoordinates());
//        }
//        else {
//            MallGate mallGate = (MallGate) spawnPatch.getAmenityBlock().getParent();
//            // this.agentMovement = new MallAgentMovement(mallGate, this, 1.27, spawnPatch.getPatchCenterCoordinates());
//        }
    }

    public int getId() {
        return id;
    }

    public MallAgent.Type getType() {
        return type;
    }

    public MallAgent.Gender getGender() {
        return gender;
    }

    public MallAgent.AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public MallAgent.Persona getPersona() {
        return persona;
    }

//    public MallAgentGraphic getAgentGraphic() {
//        return agentGraphic;
//    }

//    public MallAgentMovement getAgentMovement() {
//        return agentMovement;
//    }

    public static class MallAgentFactory extends Agent.AgentFactory {
        public static MallAgent create(MallAgent.Type type, Patch spawnPatch, boolean inOnStart) {
            return new MallAgent(type, spawnPatch, inOnStart);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MallAgent agent = (MallAgent) o;

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
        PATRON, STAFF_STORE, STAFF_RESTO, STAFF_KIOSK, GUARD
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        STAFF_AISLE, CASHIER, BAGGER, GUARD, BUTCHER, CUSTOMER_SERVICE, STAFF_FOOD,
        STTP_ALONE_CUSTOMER, MODERATE_ALONE_CUSTOMER,
        COMPLETE_FAMILY_CUSTOMER, HELP_FAMILY_CUSTOMER, DUO_FAMILY_CUSTOMER
    }

}