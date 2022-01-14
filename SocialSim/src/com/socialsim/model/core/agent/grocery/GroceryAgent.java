package com.socialsim.model.core.agent.grocery;

import com.socialsim.controller.grocery.graphics.agent.GroceryAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

import java.util.Objects;

public class GroceryAgent extends Agent {

    public static int agentCount = 0;
    public static int customerCount = 0;
    public static int staffAisleCount = 0;
    public static int cashierCount = 0;
    public static int baggerCount = 0;
    public static int guardCount = 0;
    public static int butcherCount = 0;
    public static int customerServiceCount = 0;
    public static int staffFoodCount = 0;
    public static final double[][][] chancePerActionInteractionType = new double[][][]
            {
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {10, 0, 90}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 100, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {20, 80, 0}, {0, 100, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {20, 80, 0}, {0, 100, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {20, 80, 0}, {0, 100, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {20, 80, 0}, {0, 100, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {20, 80, 0}, {0, 100, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {20, 80, 0}, {0, 100, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {0, 0, 100}, {0, 100, 0}, {0, 100, 0}, {0, 0, 100}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {0, 0, 100}, {90, 0, 10}, {90, 0, 10}, {0, 50, 50}, {90, 0, 10}, {10, 90, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    {{90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {40, 30, 30}, {40, 30, 30}, {40, 30, 30}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {0, 0, 100}, {0, 100, 0}, {0, 100, 0}, {0, 0, 100}, {90, 0, 10}, {90, 0, 10}, {90, 0, 10}, {0, 0, 100}, {90, 0, 10}, {90, 0, 10}, {0, 50, 50}, {90, 0, 10}, {10, 90, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            };

    private final int id;
    private final GroceryAgent.Type type;
    private GroceryAgent.Gender gender;
    private GroceryAgent.AgeGroup ageGroup = null;
    private GroceryAgent.Persona persona = null;
    private boolean leader;
    private final boolean inOnStart;

    private final GroceryAgentGraphic agentGraphic;
    private GroceryAgentMovement agentMovement;

    public static final GroceryAgent.GroceryAgentFactory agentFactory;

    static {
        agentFactory = new GroceryAgent.GroceryAgentFactory();
    }

    private GroceryAgent(GroceryAgent.Type type, GroceryAgent.Persona persona, GroceryAgent.Gender gender, GroceryAgent.AgeGroup ageGroup, Patch spawnPatch, boolean leader, boolean inOnStart, GroceryAgent leaderAgent, long currentTick) {
        this.id = agentCount;
        this.type = type;
        this.leader = leader;
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

        if (type == Type.STAFF_AISLE) {
            this.persona = Persona.STAFF_AISLE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.CASHIER) {
            this.persona = Persona.CASHIER;
            this.gender = Gender.FEMALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.BAGGER) {
            this.persona = Persona.BAGGER;
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.BUTCHER) {
            this.persona = Persona.BUTCHER;
            this.gender = Gender.MALE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.CUSTOMER_SERVICE) {
            this.persona = Persona.CUSTOMER_SERVICE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.STAFF_FOOD) {
            this.persona = Persona.STAFF_FOOD;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == GroceryAgent.Type.GUARD) {
            this.persona = persona;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
        }
        else if (type == Type.CUSTOMER) {
            this.persona = persona;
            this.gender = gender;
            this.ageGroup = ageGroup;
        }

        this.agentGraphic = new GroceryAgentGraphic(this);
        this.agentMovement = new GroceryAgentMovement(spawnPatch, this, leaderAgent, 1.27, spawnPatch.getPatchCenterCoordinates(), currentTick);
    }

    private GroceryAgent(GroceryAgent.Type type, GroceryAgent.Persona persona, GroceryAgent.Gender gender, GroceryAgent.AgeGroup ageGroup, boolean leader, boolean inOnStart) {
        this.id = agentCount;
        this.type = type;
        this.leader = leader;
        this.inOnStart = inOnStart;

//        if (type == Type.CUSTOMER) {
//            GroceryAgent.customerCount++;
//        }
//        else if (type == Type.STAFF_AISLE) {
//            GroceryAgent.staffAisleCount++;
//        }
//        else if (type == Type.CASHIER) {
//            GroceryAgent.cashierCount++;
//        }
//        else if (type == Type.BAGGER) {
//            GroceryAgent.baggerCount++;
//        }
//        else if (type == GroceryAgent.Type.GUARD) {
//            GroceryAgent.guardCount++;
//        }
//        else if (type == Type.BUTCHER) {
//            GroceryAgent.butcherCount++;
//        }
//        else if (type == Type.CUSTOMER_SERVICE) {
//            GroceryAgent.customerServiceCount++;
//        }
//        else if (type == Type.STAFF_FOOD) {
//            GroceryAgent.staffFoodCount++;
//        }
//        GroceryAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? GroceryAgent.Gender.FEMALE : GroceryAgent.Gender.MALE;

        if (type == Type.STAFF_AISLE) {
            this.persona = Persona.STAFF_AISLE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.CASHIER) {
            this.persona = Persona.CASHIER;
            this.gender = Gender.FEMALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.BAGGER) {
            this.persona = Persona.BAGGER;
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.BUTCHER) {
            this.persona = Persona.BUTCHER;
            this.gender = Gender.MALE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.CUSTOMER_SERVICE) {
            this.persona = Persona.CUSTOMER_SERVICE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.STAFF_FOOD) {
            this.persona = Persona.STAFF_FOOD;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == GroceryAgent.Type.GUARD) {
            this.persona = persona;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
        }
        else if (type == Type.CUSTOMER) {
            this.persona = persona;
            this.gender = gender;
            this.ageGroup = ageGroup;
        }

        this.agentGraphic = new GroceryAgentGraphic(this);
        this.agentMovement = null;
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

    public boolean isLeader() {
        return leader;
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

    public void setAgentMovement(GroceryAgentMovement agentMovement) {
        this.agentMovement = agentMovement;
    }

    public static class GroceryAgentFactory extends Agent.AgentFactory {
//        public static GroceryAgent create(GroceryAgent.Type type, GroceryAgent.Persona persona, GroceryAgent.Gender gender, GroceryAgent.AgeGroup ageGroup, Patch spawnPatch, boolean leader, boolean inOnStart, GroceryAgent leaderAgent, long currentTick) {
//            return new GroceryAgent(type, persona, gender, ageGroup, spawnPatch, leader, inOnStart, leaderAgent, currentTick);
//        }
        public static GroceryAgent create(GroceryAgent.Type type, GroceryAgent.Persona persona, GroceryAgent.Gender gender, GroceryAgent.AgeGroup ageGroup, boolean leader, boolean inOnStart) {
            return new GroceryAgent(type, persona, gender, ageGroup, leader, inOnStart);
        }
    }

    public static void clearGroceryAgentCounts() {
        agentCount = 0;
        customerCount = 0;
        staffAisleCount = 0;
        cashierCount = 0;
        baggerCount = 0;
        guardCount = 0;
        butcherCount = 0;
        customerServiceCount = 0;
        staffFoodCount = 0;
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
        GUARD_ENTRANCE(0), GUARD_EXIT(1), STAFF_AISLE(2), BUTCHER(3), CASHIER(4), BAGGER(5), CUSTOMER_SERVICE(6), STAFF_FOOD(7),
        STTP_ALONE_CUSTOMER(8), MODERATE_ALONE_CUSTOMER(8),
        COMPLETE_FAMILY_CUSTOMER(9), HELP_FAMILY_CUSTOMER(9), DUO_FAMILY_CUSTOMER(9);

        private final int ID;

        Persona(int ID){
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }
    }

}