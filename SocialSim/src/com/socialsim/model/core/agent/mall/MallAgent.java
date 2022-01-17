package com.socialsim.model.core.agent.mall;

import com.socialsim.controller.mall.graphics.agent.MallAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.simulator.Simulator;

import java.util.Objects;

public class MallAgent extends Agent {

    public static int agentCount = 0;
    public static int patronCount = 0;
    public static int staffStoreSalesCount = 0;
    public static int staffStoreCashierCount = 0;
    public static int staffRestoCount = 0;
    public static int staffKioskCount = 0;
    public static int guardCount = 0;

    private final int id;
    private final MallAgent.Type type;
    private MallAgent.Gender gender;
    private MallAgent.AgeGroup ageGroup = null;
    private MallAgent.Persona persona = null;
    private final boolean inOnStart;

    private final MallAgentGraphic agentGraphic;
    private final MallAgentMovement agentMovement;

    public static final MallAgent.MallAgentFactory agentFactory;

    static {
        agentFactory = new MallAgent.MallAgentFactory();
    }

    private MallAgent(MallAgent.Type type, MallAgent.Persona persona, MallAgent.Gender gender, MallAgent.AgeGroup ageGroup, Patch spawnPatch, boolean inOnStart, MallAgent leaderAgent, int currentTick) {
        this.id = agentCount;
        this.type = type;
        this.inOnStart = inOnStart;

        if (type == Type.PATRON) {
            MallAgent.patronCount++;
        }
        else if (type == Type.STAFF_STORE_SALES) {
            MallAgent.staffStoreSalesCount++;
        }
        else if (type == Type.STAFF_STORE_CASHIER) {
            MallAgent.staffStoreCashierCount++;
        }
        else if (type == Type.STAFF_RESTO) {
            MallAgent.staffRestoCount++;
        }
        else if (type == Type.STAFF_KIOSK) {
            MallAgent.staffKioskCount++;
        }
        else if (type == MallAgent.Type.GUARD) {
            MallAgent.guardCount++;
        }
        MallAgent.agentCount++;

        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? MallAgent.Gender.FEMALE : MallAgent.Gender.MALE;

        if (type == Type.GUARD) {
            this.persona = Persona.GUARD;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_25_TO_54 : AgeGroup.FROM_55_TO_64;
        }
        else if (type == Type.STAFF_STORE_SALES) {
            this.persona = Persona.STAFF_STORE_SALES;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.STAFF_STORE_CASHIER) {
            this.persona = Persona.STAFF_STORE_CASHIER;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.STAFF_RESTO) {
            this.persona = Persona.STAFF_RESTO;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.STAFF_KIOSK) {
            this.persona = Persona.STAFF_KIOSK;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;
        }
        else if (type == Type.PATRON) {
            this.persona = persona;
            this.gender = gender;
            this.ageGroup = ageGroup;
        }

        this.agentGraphic = new MallAgentGraphic(this);
        this.agentMovement = new MallAgentMovement(spawnPatch, this, leaderAgent, 1.27, spawnPatch.getPatchCenterCoordinates(), currentTick);
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

    public boolean getInOnStart() {
        return inOnStart;
    }

    public MallAgent.AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public MallAgent.Persona getPersona() {
        return persona;
    }

    public MallAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

    public MallAgentMovement getAgentMovement() {
        return agentMovement;
    }

    public static class MallAgentFactory extends Agent.AgentFactory {
        public static MallAgent create(MallAgent.Type type, MallAgent.Persona persona, MallAgent.Gender gender, MallAgent.AgeGroup ageGroup, Patch spawnPatch, boolean inOnStart, MallAgent leaderAgent, int currentTick) {
            return new MallAgent(type, persona, gender, ageGroup, spawnPatch, inOnStart, leaderAgent, currentTick);
        }
    }

    public static void clearMallAgentCounts() {
        agentCount = 0;
        patronCount = 0;
        staffStoreSalesCount = 0;
        staffStoreCashierCount = 0;
        staffRestoCount = 0;
        staffKioskCount = 0;
        guardCount = 0;
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
        PATRON, STAFF_STORE_SALES, STAFF_STORE_CASHIER, STAFF_RESTO, STAFF_KIOSK, GUARD
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        STAFF_STORE_SALES, STAFF_STORE_CASHIER, STAFF_RESTO, STAFF_KIOSK, GUARD,
        ERRAND_FAMILY, LOITER_FAMILY,
        ERRAND_FRIENDS, LOITER_FRIENDS,
        ERRAND_ALONE, LOITER_ALONE,
        ERRAND_COUPLE, LOITER_COUPLE
    }

}