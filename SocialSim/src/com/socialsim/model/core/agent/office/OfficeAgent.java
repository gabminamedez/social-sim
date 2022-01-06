package com.socialsim.model.core.agent.office;

import com.socialsim.controller.office.graphics.agent.OfficeAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Cubicle;
import com.socialsim.model.simulator.Simulator;

import java.util.Objects;

public class OfficeAgent extends Agent {

    public static int agentCount = 0;
    public static int bossCount = 0;
    public static int managerCount = 0;
    public static int businessCount = 0;
    public static int researcherCount = 0;
    public static int janitorCount = 0;
    public static int clientCount = 0;
    public static int driverCount = 0;
    public static int technicalCount = 0;
    public static int visitorCount = 0;
    public static int guardCount = 0;
    public static int receptionistCount = 0;
    public static int secretaryCount = 0;

    private final int id;
    private final OfficeAgent.Type type;
    private OfficeAgent.Gender gender;
    private OfficeAgent.AgeGroup ageGroup = null;
    private OfficeAgent.Persona persona = null;
    private boolean inOnStart;

    private final OfficeAgentGraphic agentGraphic;
    private final OfficeAgentMovement agentMovement;

    public static final OfficeAgent.OfficeAgentFactory agentFactory;

    static {
        agentFactory = new OfficeAgent.OfficeAgentFactory();
    }

    private OfficeAgent(OfficeAgent.Type type, Patch spawnPatch, boolean inOnStart, long currentTick, int team, Cubicle assignedCubicle) {
        this.id = agentCount;
        this.type = type;
        this.inOnStart = inOnStart;

        if (type == Type.BOSS) {
            OfficeAgent.bossCount++;
        }
        else if (type == Type.MANAGER) {
            OfficeAgent.managerCount++;
        }
        else if (type == Type.BUSINESS) {
            OfficeAgent.businessCount++;
        }
        else if (type == Type.RESEARCHER) {
            OfficeAgent.researcherCount++;
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

        if (this.type == OfficeAgent.Type.GUARD) {
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
            this.persona = OfficeAgent.Persona.GUARD;
        }
        else if(this.type == OfficeAgent.Type.JANITOR) {
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
            this.persona = OfficeAgent.Persona.JANITOR;
        }
        else if(this.type == OfficeAgent.Type.VISITOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.YOUNGER_THAN_OR_14 : AgeGroup.FROM_15_TO_24;
            this.persona = Persona.VISITOR;
        }
        else if(this.type == OfficeAgent.Type.DRIVER) {
            this.gender = Gender.MALE;
            this.ageGroup = OfficeAgent.AgeGroup.FROM_25_TO_54;
            this.persona = OfficeAgent.Persona.DRIVER;
        }
        else if(this.type == OfficeAgent.Type.CLIENT) {
            this.ageGroup = OfficeAgent.AgeGroup.FROM_55_TO_64;
            this.persona = Persona.CLIENT;
        }
        else if(this.type == Type.RECEPTIONIST) {
            this.gender = Gender.FEMALE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
            this.persona = Persona.RECEPTIONIST;
        }
        else if(this.type == Type.SECRETARY) {
            this.gender = Gender.FEMALE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
            this.persona = Persona.SECRETARY;
        }
        else if(this.type == Type.MANAGER) {
            this.ageGroup = AgeGroup.FROM_25_TO_54;
            this.persona = Persona.MANAGER;
        }
        else if (this.type == Type.BOSS) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;

            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isStrict) {
                this.persona = Persona.PROFESSIONAL_BOSS;
            }
            else {
                this.persona = Persona.APPROACHABLE_BOSS;
            }
        }
        else if (this.type == Type.BUSINESS) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isIntrovert) {
                this.persona = Persona.INT_BUSINESS;
            }
            else {
                this.persona = Persona.EXT_BUSINESS;
            }
        }
        else if (this.type == Type.RESEARCHER) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isIntrovert) {
                this.persona = Persona.INT_RESEARCHER;
            }
            else {
                this.persona = Persona.EXT_RESEARCHER;
            }
        }
        else if (this.type == Type.TECHNICAL) {
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isIntrovert) {
                this.persona = Persona.INT_TECHNICAL;
            }
            else {
                this.persona = Persona.EXT_TECHNICAL;
            }
        }

        this.agentGraphic = new OfficeAgentGraphic(this);
        this.agentMovement = new OfficeAgentMovement(spawnPatch, this, 1.27, spawnPatch.getPatchCenterCoordinates(), currentTick, team, assignedCubicle);
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

    public boolean getInOnStart() {
        return inOnStart;
    }

    public OfficeAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

    public OfficeAgentMovement getAgentMovement() {
        return agentMovement;
    }

    public static class OfficeAgentFactory extends Agent.AgentFactory {
        public static OfficeAgent create(OfficeAgent.Type type, Patch spawnPatch, boolean inOnStart, long currentTick, int team, Cubicle assignedCubicle) {
            return new OfficeAgent(type, spawnPatch, inOnStart, currentTick, team, assignedCubicle);
        }
    }

    public static void clearOfficeAgentCounts() {
        agentCount = 0;
        bossCount = 0;
        managerCount = 0;
        businessCount = 0;
        researcherCount = 0;
        janitorCount = 0;
        clientCount = 0;
        driverCount = 0;
        technicalCount = 0;
        visitorCount = 0;
        guardCount = 0;
        receptionistCount = 0;
        secretaryCount = 0;
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
        BOSS, MANAGER, BUSINESS, RESEARCHER, JANITOR, CLIENT, DRIVER, TECHNICAL, VISITOR, GUARD, RECEPTIONIST, SECRETARY
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        PROFESSIONAL_BOSS, APPROACHABLE_BOSS,
        MANAGER,
        INT_BUSINESS, EXT_BUSINESS,
        INT_RESEARCHER, EXT_RESEARCHER,
        INT_TECHNICAL, EXT_TECHNICAL,
        JANITOR, CLIENT, DRIVER, VISITOR, GUARD, RECEPTIONIST, SECRETARY
    }

}