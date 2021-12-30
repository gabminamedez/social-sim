package com.socialsim.model.core.agent.grocery;

import com.socialsim.controller.grocery.graphics.agent.GroceryAgentGraphic;
import com.socialsim.controller.university.graphics.agent.UniversityAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.university.UniversityAgentMovement;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.simulator.Simulator;

import java.util.Objects;

public class GroceryAgent extends Agent {

    private static int agentCount = 0;
    private static int customerCount = 0;
    private static int staffAisleCount = 0;
    private static int cashierCount = 0;
    private static int baggerCount = 0;
    private static int guardCount = 0;
    private static int butcherCount = 0;
    private static int customerServiceCount = 0;
    private static int staffFoodCount = 0;

    private final int id;
    private final GroceryAgent.Type type;
    private final GroceryAgent.Gender gender;
    private GroceryAgent.AgeGroup ageGroup = null;
    private GroceryAgent.Persona persona = null;
    private final boolean inOnStart;

    private final GroceryAgentGraphic agentGraphic;
    private final GroceryAgentMovement agentMovement;

    public static final GroceryAgent.GroceryAgentFactory agentFactory;

    static {
        agentFactory = new GroceryAgent.GroceryAgentFactory();
    }

    private GroceryAgent(GroceryAgent.Type type, Patch spawnPatch, boolean inOnStart) {
        this.id = agentCount;
        this.type = type;
        this.inOnStart = inOnStart;

        if (type == Type.CUSTOMER) {
            GroceryAgent.customerCount++;
        }
        else if (type == Type.STAFF_AISLE) {
            GroceryAgent.staffAisleCount++;
        }
        else if (type == Type.CASHIER) {
            GroceryAgent.cashierCount++;
        }
        else if (type == Type.BAGGER) {
            GroceryAgent.baggerCount++;
        }
        else if (type == GroceryAgent.Type.GUARD) {
            GroceryAgent.guardCount++;
        }
        else if (type == Type.BUTCHER) {
            GroceryAgent.butcherCount++;
        }
        else if (type == Type.CUSTOMER_SERVICE) {
            GroceryAgent.customerServiceCount++;
        }
        else if (type == Type.STAFF_FOOD) {
            GroceryAgent.staffFoodCount++;
        }
        GroceryAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.FEMALE : GroceryAgent.Gender.MALE;

//        if (this.type == GroceryAgent.Type.GUARD) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.AgeGroup.FROM_25_TO_54 : GroceryAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = GroceryAgent.Persona.GUARD;
//        }
//        else if(this.type == GroceryAgent.Type.JANITOR) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.AgeGroup.FROM_25_TO_54 : GroceryAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = GroceryAgent.Persona.JANITOR;
//        }
//        else if(this.type == GroceryAgent.Type.OFFICER) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.AgeGroup.FROM_25_TO_54 : GroceryAgent.AgeGroup.FROM_55_TO_64;
//            this.persona = GroceryAgent.Persona.OFFICER;
//        }
//        else if (this.type == GroceryAgent.Type.PROFESSOR) {
//            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.AgeGroup.FROM_25_TO_54 : GroceryAgent.AgeGroup.FROM_55_TO_64;
//
//            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//            if (isStrict) {
//                this.persona = GroceryAgent.Persona.STRICT_PROFESSOR;
//            }
//            else {
//                this.persona = GroceryAgent.Persona.APPROACHABLE_PROFESSOR;
//            }
//        }
//        else if (this.type == GroceryAgent.Type.STUDENT) {
//            this.ageGroup = GroceryAgent.AgeGroup.FROM_15_TO_24;
//
//            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//            int yearLevel = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(4) + 1;
//            boolean isOrg = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
//
//            if (isIntrovert && yearLevel == 1 && !isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y1_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 2 && !isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y2_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 3 && !isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y3_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 4 && !isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y4_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 1 && !isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y1_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 2 && !isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y2_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 3 && !isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y3_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 4 && !isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y4_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 1 && isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y1_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 2 && isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y2_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 3 && isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y3_ORG_STUDENT;
//            }
//            else if (isIntrovert && yearLevel == 4 && isOrg) {
//                this.persona = GroceryAgent.Persona.INT_Y4_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 1 && isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y1_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 2 && isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y2_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 3 && isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y3_ORG_STUDENT;
//            }
//            else if (!isIntrovert && yearLevel == 4 && isOrg) {
//                this.persona = GroceryAgent.Persona.EXT_Y4_ORG_STUDENT;
//            }
//        }
//
//        this.agentGraphic = new GroceryAgentGraphic(this);
//        if (inOnStart) { // If the agent is already inside the environment on initialization
//            // this.agentMovement = new GroceryAgentMovement(spawnPatch, this, 1.27, spawnPatch.getPatchCenterCoordinates());
//        }
//        else {
//            GroceryGate groceryGate = (GroceryGate) spawnPatch.getAmenityBlock().getParent();
//            // this.agentMovement = new GroceryAgentMovement(groceryGate, this, 1.27, spawnPatch.getPatchCenterCoordinates());
//        }

        this.agentGraphic = new GroceryAgentGraphic();
        this.agentMovement = new GroceryAgentMovement(spawnPatch, this, 1.27, spawnPatch.getPatchCenterCoordinates());
    }

    public int getId() {
        return id;
    }

    public GroceryAgent.Type getType() {
        return type;
    }

    public GroceryAgent.Gender getGender() {
        return gender;
    }

    public boolean getInOnStart() {
        return inOnStart;
    }

    public GroceryAgent.AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public GroceryAgent.Persona getPersona() {
        return persona;
    }

    public GroceryAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

    public GroceryAgentMovement getAgentMovement() {
        return agentMovement;
    }

    public static class GroceryAgentFactory extends Agent.AgentFactory {
        public static GroceryAgent create(GroceryAgent.Type type, Patch spawnPatch, boolean inOnStart) {
            return new GroceryAgent(type, spawnPatch, inOnStart);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroceryAgent agent = (GroceryAgent) o;

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
        CUSTOMER, STAFF_AISLE, CASHIER, BAGGER, GUARD, BUTCHER, CUSTOMER_SERVICE, STAFF_FOOD
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        GUARD_ENTRANCE, GUARD_EXIT, STAFF_AISLE, BUTCHER, CASHIER, BAGGER, CUSTOMER_SERVICE, STAFF_FOOD,
        STTP_ALONE_CUSTOMER, MODERATE_ALONE_CUSTOMER,
        COMPLETE_FAMILY_CUSTOMER, HELP_FAMILY_CUSTOMER, DUO_FAMILY_CUSTOMER
    }

}