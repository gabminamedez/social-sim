package com.socialsim.model.core.agent.office;

import com.socialsim.controller.office.graphics.agent.OfficeAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.simulator.Simulator;
import java.util.Objects;

public class OfficeAgent extends Agent {

    private static int idCtr = 0;
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
    public static final double[][][] chancePerActionInteractionType = new double[][][]
    {
            {{0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.60, 0, 0.40}, {0, 0, 0}, {0.60, 0, 0.40}, {0.60, 0, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0, 0.50, 0.50}, {0.20, 0.30, 0.50}},
            {{0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.50, 0, 0.50}, {0, 0, 0}, {0.50, 0, 0.50}, {0.50, 0, 0.50}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0, 0.50, 0.50}, {0.10, 0.40, 0.50}},
            {{0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.50, 0, 0.50}, {0, 0, 0}, {0.50, 0, 0.50}, {0.50, 0, 0.50}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0.10, 0.40, 0.50}, {0, 0.50, 0.50}, {0.10, 0.40, 0.50}},
            {{0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.60, 0, 0.40}, {0, 0, 0}, {0.60, 0, 0.40}, {0.60, 0, 0.40}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0.50, 0.50}, {0.40, 0.30, 0.30}},
            {{0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.50, 0, 0.50}, {0, 0, 0}, {0.50, 0, 0.50}, {0.50, 0, 0.50}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0.50, 0.50}, {0.20, 0.40, 0.40}},
            {{0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.60, 0, 0.40}, {0, 0, 0}, {0.60, 0, 0.40}, {0.60, 0, 0.40}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0.60, 0.40}, {0.40, 0.30, 0.30}, {0, 0.60, 0.40}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0.40, 0.30, 0.30}, {0, 0.50, 0.50}, {0.40, 0.30, 0.30}},
            {{0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.50, 0, 0.50}, {0, 0, 0}, {0.50, 0, 0.50}, {0.50, 0, 0.50}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0.60, 0.40}, {0.20, 0.40, 0.40}, {0, 0.60, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0.20, 0.40, 0.40}, {0, 0.50, 0.50}, {0.20, 0.40, 0.40}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.10, 0.90, 0}, {0.10, 0.90, 0}, {0.10, 0.90, 0}, {0.10, 0.90, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.80, 0, 0.20}, {0.80, 0, 0.20}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1.00}, {0.20, 0, 0.80}, {0.20, 0, 0.80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1.00}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.80, 0, 0.20}, {0.80, 0, 0.20}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1.00}, {0.20, 0, 0.80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.80, 0, 0.20}, {0.80, 0, 0.20}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1.00}, {0.20, 0, 0.80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0, 0.80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 1.00, 0}, {0, 0.10, 0.90}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0},{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0, 0.80}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.80, 0, 0.20}, {0.80, 0, 0.20}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0.20, 0, 0.80}, {0.20, 0, 0.80}, {0.20, 0, 0.80}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.20, 0.30, 0.50}, {0.60, 0, 0.40}, {0, 0, 0}, {0.60, 0, 0.40}, {0.60, 0, 0.40}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},

    };

    private final int id;
    private final OfficeAgent.Type type;
    private OfficeAgent.Gender gender;
    private int team;
    private OfficeAgent.AgeGroup ageGroup = null;
    private OfficeAgent.Persona persona = null;
    private PersonaActionGroup personaActionGroup = null;
    private boolean inOnStart;

    private final OfficeAgentGraphic agentGraphic;
    private OfficeAgentMovement agentMovement;

    public static final OfficeAgent.OfficeAgentFactory agentFactory;

    static {
        agentFactory = new OfficeAgent.OfficeAgentFactory();
    }

    private OfficeAgent(OfficeAgent.Type type, boolean inOnStart, int team) {
        this.id = idCtr++;
        this.type = type;
        this.team = team;
        this.inOnStart = inOnStart;
        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.Gender.FEMALE : OfficeAgent.Gender.MALE;

        if (this.type == OfficeAgent.Type.GUARD) {
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
            this.persona = OfficeAgent.Persona.GUARD;
            this.personaActionGroup = PersonaActionGroup.GUARD;
        }
        else if(this.type == OfficeAgent.Type.JANITOR) {
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;
            this.persona = OfficeAgent.Persona.JANITOR;
            this.personaActionGroup = PersonaActionGroup.JANITOR;
        }
        else if(this.type == OfficeAgent.Type.VISITOR) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.YOUNGER_THAN_OR_14 : AgeGroup.FROM_15_TO_24;
            this.persona = Persona.VISITOR;
            this.personaActionGroup = PersonaActionGroup.VISITOR;
        }
        else if(this.type == OfficeAgent.Type.DRIVER) {
            this.gender = Gender.MALE;
            this.ageGroup = OfficeAgent.AgeGroup.FROM_25_TO_54;
            this.persona = OfficeAgent.Persona.DRIVER;
            this.personaActionGroup = PersonaActionGroup.DRIVER;
        }
        else if(this.type == OfficeAgent.Type.CLIENT) {
            this.ageGroup = OfficeAgent.AgeGroup.FROM_55_TO_64;
            this.persona = Persona.CLIENT;
            this.personaActionGroup = PersonaActionGroup.CLIENT;
        }
        else if(this.type == Type.RECEPTIONIST) {
            this.gender = Gender.FEMALE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
            this.persona = Persona.RECEPTIONIST;
            this.personaActionGroup = PersonaActionGroup.RECEPTIONIST;
        }
        else if(this.type == Type.SECRETARY) {
            this.gender = Gender.FEMALE;
            this.ageGroup = AgeGroup.FROM_25_TO_54;
            this.persona = Persona.SECRETARY;
            this.personaActionGroup = PersonaActionGroup.SECRETARY;
        }
        else if(this.type == Type.MANAGER) {
            this.ageGroup = AgeGroup.FROM_25_TO_54;
            this.persona = Persona.MANAGER;
            this.personaActionGroup = PersonaActionGroup.MANAGER;
        }
        else if (this.type == Type.BOSS) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? OfficeAgent.AgeGroup.FROM_25_TO_54 : OfficeAgent.AgeGroup.FROM_55_TO_64;

            boolean isStrict = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isStrict) {
                this.persona = Persona.PROFESSIONAL_BOSS;
                this.personaActionGroup = PersonaActionGroup.PROFESSIONAL_BOSS;
            }
            else {
                this.persona = Persona.APPROACHABLE_BOSS;
                this.personaActionGroup = PersonaActionGroup.APPROACHABLE_BOSS;
            }
        }
        else if (this.type == Type.BUSINESS) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isIntrovert) {
                this.persona = Persona.INT_BUSINESS;
                this.personaActionGroup = PersonaActionGroup.INT_WORKER;
            }
            else {
                this.persona = Persona.EXT_BUSINESS;
                this.personaActionGroup = PersonaActionGroup.EXT_WORKER;
            }
        }
        else if (this.type == Type.RESEARCHER) {
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isIntrovert) {
                this.persona = Persona.INT_RESEARCHER;
                this.personaActionGroup = PersonaActionGroup.INT_WORKER;
            }
            else {
                this.persona = Persona.EXT_RESEARCHER;
                this.personaActionGroup = PersonaActionGroup.EXT_WORKER;
            }
        }
        else if (this.type == Type.TECHNICAL) {
            this.gender = Gender.MALE;
            this.ageGroup = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? AgeGroup.FROM_15_TO_24 : AgeGroup.FROM_25_TO_54;

            boolean isIntrovert = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean();
            if (isIntrovert) {
                this.persona = Persona.INT_TECHNICAL;
                this.personaActionGroup = PersonaActionGroup.INT_TECHNICAL;
            }
            else {
                this.persona = Persona.EXT_TECHNICAL;
                this.personaActionGroup = PersonaActionGroup.EXT_TECHNICAL;
            }
        }

        this.agentGraphic = new OfficeAgentGraphic(this);
        this.agentMovement = null;
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

    public OfficeAgent.PersonaActionGroup getPersonaActionGroup() {
        return personaActionGroup;
    }

    public boolean getInOnStart() {
        return inOnStart;
    }

    public int getTeam() {
        return team;
    }

    public OfficeAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

    public OfficeAgentMovement getAgentMovement() {
        return agentMovement;
    }

    public void setAgentMovement(OfficeAgentMovement agentMovement) {
        this.agentMovement = agentMovement;
    }

    public static class OfficeAgentFactory extends Agent.AgentFactory {
        public static OfficeAgent create(OfficeAgent.Type type, boolean inOnStart, int team) {
            return new OfficeAgent(type, inOnStart, team);
        }
    }

    public static void clearOfficeAgentCounts() {
        idCtr = 0;
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
        BOSS, MANAGER, BUSINESS, RESEARCHER, TECHNICAL, JANITOR, CLIENT, DRIVER, VISITOR, GUARD, RECEPTIONIST, SECRETARY
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum AgeGroup {
        YOUNGER_THAN_OR_14, FROM_15_TO_24, FROM_25_TO_54, FROM_55_TO_64, OLDER_THAN_OR_65
    }

    public enum Persona {
        PROFESSIONAL_BOSS(0), APPROACHABLE_BOSS(1),
        MANAGER(2),
        INT_BUSINESS(3), EXT_BUSINESS(4),
        INT_RESEARCHER(3), EXT_RESEARCHER(4),
        INT_TECHNICAL(5), EXT_TECHNICAL(6),
        JANITOR(7), CLIENT(8), DRIVER(9), VISITOR(10), GUARD(11), RECEPTIONIST(12), SECRETARY(13);

        private final int ID;

        Persona(int ID){
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }
    }

    public enum PersonaActionGroup {
        PROFESSIONAL_BOSS(),
        APPROACHABLE_BOSS(),
        MANAGER(),
        INT_WORKER(),
        EXT_WORKER(),
        INT_TECHNICAL(),
        EXT_TECHNICAL(),
        JANITOR(),
        CLIENT(),
        DRIVER(),
        VISITOR(),
        GUARD(),
        RECEPTIONIST(),
        SECRETARY();

        final int ID;
        PersonaActionGroup(){
            this.ID = this.ordinal();
        }
        public int getID() {
            return ID;
        }
    }

}