package com.socialsim.controller.grocery.graphics.agent;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.model.core.agent.grocery.GroceryAgent;

import java.util.ArrayList;
import java.util.List;

public class GroceryAgentGraphic extends Graphic {

    public static final String AGENTS_URL_1 = "com/socialsim/view/image/Grocery/customer_alone.png";
    public static final String AGENTS_URL_2 = "com/socialsim/view/image/Grocery/customer_family.png";
    public static final String AGENTS_URL_3 = "com/socialsim/view/image/Grocery/staff.png";

    public static final List<AgentGraphicLocation> maleSttpAlone;
    public static final List<AgentGraphicLocation> femaleSttpAlone;
    public static final List<AgentGraphicLocation> maleModerateAlone;
    public static final List<AgentGraphicLocation> femaleModerateAlone;
    public static final List<AgentGraphicLocation> maleComplete1;
    public static final List<AgentGraphicLocation> femaleComplete1;
    public static final List<AgentGraphicLocation> maleComplete2;
    public static final List<AgentGraphicLocation> femaleComplete2;
    public static final List<AgentGraphicLocation> maleHelp1;
    public static final List<AgentGraphicLocation> femaleHelp1;
    public static final List<AgentGraphicLocation> maleHelp2;
    public static final List<AgentGraphicLocation> femaleHelp2;
    public static final List<AgentGraphicLocation> maleDuo1;
    public static final List<AgentGraphicLocation> femaleDuo1;
    public static final List<AgentGraphicLocation> maleDuo2;
    public static final List<AgentGraphicLocation> femaleDuo2;
    public static final List<AgentGraphicLocation> maleStaffAisle;
    public static final List<AgentGraphicLocation> femaleStaffAisle;
    public static final List<AgentGraphicLocation> cashier;
    public static final List<AgentGraphicLocation> bagger;
    public static final List<AgentGraphicLocation> butcher;
    public static final List<AgentGraphicLocation> maleStaffFood;
    public static final List<AgentGraphicLocation> femaleStaffFood;
    public static final List<AgentGraphicLocation> maleCustomerService;
    public static final List<AgentGraphicLocation> femaleCustomerService;
    public static final List<AgentGraphicLocation> maleGuard;
    public static final List<AgentGraphicLocation> femaleGuard;

    static {
        maleSttpAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleSttpAlone.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            maleSttpAlone.add(new AgentGraphicLocation(4, i));
        femaleSttpAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleSttpAlone.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            femaleSttpAlone.add(new AgentGraphicLocation(5, i));
        maleModerateAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleModerateAlone.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            maleModerateAlone.add(new AgentGraphicLocation(6, i));
        femaleModerateAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleModerateAlone.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            femaleModerateAlone.add(new AgentGraphicLocation(7, i));

        maleComplete1 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleComplete1.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            maleComplete1.add(new AgentGraphicLocation(12, i));
        femaleComplete1 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleComplete1.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            femaleComplete1.add(new AgentGraphicLocation(13, i));
        maleComplete2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleComplete2.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            maleComplete2.add(new AgentGraphicLocation(14, i));
        femaleComplete2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleComplete2.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            femaleComplete2.add(new AgentGraphicLocation(15, i));
        maleHelp1 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleHelp1.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            maleHelp1.add(new AgentGraphicLocation(16, i));
        femaleHelp1 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleHelp1.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            femaleHelp1.add(new AgentGraphicLocation(17, i));
        maleHelp2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleHelp2.add(new AgentGraphicLocation(6, i));
        for (int i = 0; i < 4; i++)
            maleHelp2.add(new AgentGraphicLocation(18, i));
        femaleHelp2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleHelp2.add(new AgentGraphicLocation(7, i));
        for (int i = 0; i < 4; i++)
            femaleHelp2.add(new AgentGraphicLocation(19, i));
        maleDuo1 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleDuo1.add(new AgentGraphicLocation(8, i));
        for (int i = 0; i < 4; i++)
            maleDuo1.add(new AgentGraphicLocation(20, i));
        femaleDuo1 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleDuo1.add(new AgentGraphicLocation(9, i));
        for (int i = 0; i < 4; i++)
            femaleDuo1.add(new AgentGraphicLocation(21, i));
        maleDuo2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleDuo2.add(new AgentGraphicLocation(10, i));
        for (int i = 0; i < 4; i++)
            maleDuo2.add(new AgentGraphicLocation(22, i));
        femaleDuo2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleDuo2.add(new AgentGraphicLocation(11, i));
        for (int i = 0; i < 4; i++)
            femaleDuo2.add(new AgentGraphicLocation(23, i));

        maleStaffAisle = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleStaffAisle.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            maleStaffAisle.add(new AgentGraphicLocation(11, i));
        femaleStaffAisle = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleStaffAisle.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            femaleStaffAisle.add(new AgentGraphicLocation(12, i));
        cashier = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            cashier.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            cashier.add(new AgentGraphicLocation(13, i));
        bagger = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            bagger.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            bagger.add(new AgentGraphicLocation(14, i));
        butcher = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            butcher.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            butcher.add(new AgentGraphicLocation(15, i));
        maleStaffFood = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleStaffFood.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            maleStaffFood.add(new AgentGraphicLocation(16, i));
        femaleStaffFood = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleStaffFood.add(new AgentGraphicLocation(6, i));
        for (int i = 0; i < 4; i++)
            femaleStaffFood.add(new AgentGraphicLocation(17, i));
        maleCustomerService = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleCustomerService.add(new AgentGraphicLocation(7, i));
        for (int i = 0; i < 4; i++)
            maleCustomerService.add(new AgentGraphicLocation(18, i));
        femaleCustomerService = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleCustomerService.add(new AgentGraphicLocation(8, i));
        for (int i = 0; i < 4; i++)
            femaleCustomerService.add(new AgentGraphicLocation(19, i));
        maleGuard = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleGuard.add(new AgentGraphicLocation(9, i));
        for (int i = 0; i < 4; i++)
            maleGuard.add(new AgentGraphicLocation(20, i));
        femaleGuard = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleGuard.add(new AgentGraphicLocation(10, i));
        for (int i = 0; i < 4; i++)
            femaleGuard.add(new AgentGraphicLocation(21, i));
    }

    private final GroceryAgent agent;
    protected final List<AgentGraphicLocation> graphics;
    protected int graphicIndex;

    public GroceryAgentGraphic(GroceryAgent agent) {
        this.agent = agent;
        this.graphics = new ArrayList<>();

        List<AgentGraphicLocation> agentGraphics = null;

        if (agent.getType() == GroceryAgent.Type.GUARD && agent.getGender() == GroceryAgent.Gender.MALE) {
            agentGraphics = maleGuard;
        }
        else if (agent.getType() == GroceryAgent.Type.GUARD && agent.getGender() == GroceryAgent.Gender.FEMALE) {
            agentGraphics = femaleGuard;
        }
        else if (agent.getType() == GroceryAgent.Type.STAFF_AISLE && agent.getGender() == GroceryAgent.Gender.MALE) {
            agentGraphics = maleStaffAisle;
        }
        else if (agent.getType() == GroceryAgent.Type.STAFF_AISLE && agent.getGender() == GroceryAgent.Gender.FEMALE) {
            agentGraphics = femaleStaffAisle;
        }
        else if (agent.getType() == GroceryAgent.Type.STAFF_FOOD && agent.getGender() == GroceryAgent.Gender.MALE) {
            agentGraphics = maleStaffFood;
        }
        else if (agent.getType() == GroceryAgent.Type.STAFF_FOOD && agent.getGender() == GroceryAgent.Gender.FEMALE) {
            agentGraphics = femaleStaffFood;
        }
        else if (agent.getType() == GroceryAgent.Type.CUSTOMER_SERVICE && agent.getGender() == GroceryAgent.Gender.MALE) {
            agentGraphics = maleCustomerService;
        }
        else if (agent.getType() == GroceryAgent.Type.CUSTOMER_SERVICE && agent.getGender() == GroceryAgent.Gender.FEMALE) {
            agentGraphics = femaleCustomerService;
        }
        else if (agent.getType() == GroceryAgent.Type.CASHIER) {
            agentGraphics = cashier;
        }
        else if (agent.getType() == GroceryAgent.Type.BAGGER) {
            agentGraphics = bagger;
        }
        else if (agent.getType() == GroceryAgent.Type.BUTCHER) {
            agentGraphics = butcher;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.STTP_ALONE_CUSTOMER) {
            agentGraphics = maleSttpAlone;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.STTP_ALONE_CUSTOMER) {
            agentGraphics = femaleSttpAlone;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
            agentGraphics = maleModerateAlone;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
            agentGraphics = femaleModerateAlone;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_25_TO_54) {
            agentGraphics = maleComplete1;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_25_TO_54) {
            agentGraphics = femaleComplete1;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_15_TO_24) {
            agentGraphics = maleComplete2;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_15_TO_24) {
            agentGraphics = femaleComplete2;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_25_TO_54) {
            agentGraphics = maleHelp1;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_25_TO_54) {
            agentGraphics = femaleHelp1;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_15_TO_24) {
            agentGraphics = maleHelp2;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_15_TO_24) {
            agentGraphics = femaleHelp2;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_25_TO_54) {
            agentGraphics = maleDuo1;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_25_TO_54) {
            agentGraphics = femaleDuo1;
        }
        else if (agent.getGender() == GroceryAgent.Gender.MALE && agent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_15_TO_24) {
            agentGraphics = maleDuo2;
        }
        else if (agent.getGender() == GroceryAgent.Gender.FEMALE && agent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER && agent.getAgeGroup() == GroceryAgent.AgeGroup.FROM_15_TO_24) {
            agentGraphics = femaleDuo2;
        }

        for (AgentGraphicLocation agentGraphicLocations : agentGraphics) {
            AgentGraphicLocation newAgentGraphicLocation = new AgentGraphicLocation(agentGraphicLocations.getGraphicRow(), agentGraphicLocations.getGraphicColumn());

            newAgentGraphicLocation.setGraphicWidth(1);
            newAgentGraphicLocation.setGraphicHeight(1);
            this.graphics.add(newAgentGraphicLocation);
        }

        this.graphicIndex = 2;
    }

    public GroceryAgent getGroceryAgent() {
        return agent;
    }

    public AgentGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    public void change() {
        GroceryAgent agent = this.agent;

        double agentHeading = agent.getAgentMovement().getHeading();
        double agentHeadingDegrees = Math.toDegrees(agentHeading);

        if (agentHeadingDegrees >= 315 && agentHeadingDegrees < 360 || agentHeadingDegrees >= 0 && agentHeadingDegrees < 45) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 5;
            }
            else {
                this.graphicIndex = 1;
            }
        }
        else if (agentHeadingDegrees >= 45 && agentHeadingDegrees < 135) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 4;
            }
            else{
                this.graphicIndex = 0;
            }
        }
        else if (agentHeadingDegrees >= 135 && agentHeadingDegrees < 225) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 7;
            }
            else {
                this.graphicIndex = 3;
            }
        }
        else if (agentHeadingDegrees >= 225 && agentHeadingDegrees < 315) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 6;
            }
            else {
                this.graphicIndex = 2;
            }
        }
    }

}