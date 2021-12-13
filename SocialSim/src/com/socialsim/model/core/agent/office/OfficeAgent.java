package com.socialsim.model.core.agent.office;

import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

import java.util.Objects;

public class OfficeAgent extends Agent {

    private static int agentCount = 0;
    private static int bossCount = 0;
    private static int managerCount = 0;
    private static int businessCount = 0;
    private static int janitorCount = 0;
    private static int clientCount = 0;
    private static int driverCount = 0;
    private static int technicalCount = 0;
    private static int visitorCount = 0;
    private static int guardCount = 0;
    private static int receptionistCount = 0;
    private static int secretaryCount = 0;

    private final int id;
    private final OfficeAgent.Type type;
    private final OfficeAgent.Gender gender;
    private OfficeAgent.AgeGroup ageGroup = null;
    private OfficeAgent.Persona persona = null;

//    private final OfficeAgentGraphic agentGraphic;
//    private final OfficeAgentMovement agentMovement;

    public static final OfficeAgent.OfficeAgentFactory agentFactory;

    static {
        agentFactory = new OfficeAgent.OfficeAgentFactory();
    }

    private OfficeAgent(OfficeAgent.Type type, Patch spawnPatch, boolean inOnStart) {
        this.id = agentCount;
        this.type = type;

        if (type == Type.BOSS) {
            OfficeAgent.bossCount++;
        }
        else if (type == Type.MANAGER) {
            OfficeAgent.managerCount++;
        }
        else if (type == Type.BUSINESS) {
            OfficeAgent.businessCount++;
        }
        else if (type == Type.JANITOR) {
            OfficeAgent.janitorCount++;
        }
        else if (type == Type.CLIENT) {
            OfficeAgent.clientCount++;
        }
        else if (type == Type.DRIVER) {
            OfficeAgent.driverCount++;
        }
        else if (type == Type.TECHNICAL) {
            OfficeAgent.technicalCount++;
        }
        else if (type == Type.VISITOR) {
            OfficeAgent.visitorCount++;
        }
        else if (type == Type.GUARD) {
            OfficeAgent.guardCount++;
        }
        else if (type == Type.RECEPTIONIST) {
            OfficeAgent.receptionistCount++;
        }
        else if (type == Type.SECRETARY) {
            OfficeAgent.secretaryCount++;
        }
        OfficeAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.Gender.FEMALE : OfficeAgent.Gender.MALE;

//        if (this.type == OfficeAgent.Type.GUARD) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = OfficeAgent.Persona.GUARD;
//        }
//        else if(this.type == OfficeAgent.Type.JANITOR) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = OfficeAgent.Persona.JANITOR;
//        }
//        else if(this.type == OfficeAgent.Type.OFFICER) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = OfficeAgent.Persona.OFFICER;
//        }
//        else if (this.type == OfficeAgent.Type.PROFESSOR) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
//
//            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//            if (isStrict) {
//                this.persona = OfficeAgent.Persona.STRICT_PROFESSOR;
//            }
//            else {
//                this.persona = OfficeAgent.Persona.APPROACHABLE_PROFESSOR;
//            }
//        }
//        else if (this.type == OfficeAgent.Type.STUDENT) {
//            this.ageGroup = OfficeAgent.AgeGroup.FROM_15_TO_24;
//
//            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//            int yearLevel = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
//            boolean isOrg = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//
//            if (isIntrovert && yearLevel == 1 && !isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y1_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 2 && !isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y2_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 3 && !isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y3_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 4 && !isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y4_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 1 && !isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y1_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 2 && !isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y2_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 3 && !isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y3_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 4 && !isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y4_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 1 && isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y1_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 2 && isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y2_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 3 && isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y3_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 4 && isOrg) {
//                this.persona = OfficeAgent.Persona.INT_Y4_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 1 && isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y1_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 2 && isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y2_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 3 && isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y3_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 4 && isOrg) {
//                this.persona = OfficeAgent.Persona.EXT_Y4_ORG_STUDENT;
//            }
//        }
//
//        this.agentGraphic = new OfficeAgentGraphic(this);
//        if (inOnStart) { // If the agent is already inside the environment on initialization
//            // this.agentMovement = new OfficeAgentMovement(spawnPatch, this, 1.27, spawnPatch.getPatchCenterCoordinates());
//        }
//        else {
//            OfficeGate officeGate = (OfficeGate) spawnPatch.getAmenityBlock().getParent();
//            // this.agentMovement = new OfficeAgentMovement(officeGate, this, 1.27, spawnPatch.getPatchCenterCoordinates());
//        }
    }

    public int getId() {
        return id;
    }

    public OfficeAgent.Type getType() {
        return type;
    }

    public OfficeAgent.Gender getGender() {
        return gender;
    }

    public OfficeAgent.AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public OfficeAgent.Persona getPersona() {
        return persona;
    }

//    public OfficeAgentGraphic getAgentGraphic() {
//        return agentGraphic;
//    }

//    public OfficeAgentMovement getAgentMovement() {
//        return agentMovement;
//    }

    public static class OfficeAgentFactory extends Agent.AgentFactory {
        public static OfficeAgent create(OfficeAgent.Type type, Patch spawnPatch, boolean inOnStart) {
            return new OfficeAgent(type, spawnPatch, inOnStart);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficeAgent agent = (OfficeAgent) o;

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
        BOSS, MANAGER, BUSINESS, JANITOR, CLIENT, DRIVER, TECHNICAL, VISITOR, GUARD, RECEPTIONIST, SECRETARY
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