package com.socialsim.controller.office.graphics.agent;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.model.core.agent.office.OfficeAgent;

import java.util.ArrayList;
import java.util.List;

public class OfficeAgentGraphic extends Graphic {

    public static final String AGENTS_URL_1 = "com/socialsim/view/image/Office/office_agents_1.png";
    public static final String AGENTS_URL_2 = "com/socialsim/view/image/Office/office_agents_2.png";
    public static final String AGENTS_URL_3 = "com/socialsim/view/image/Office/office_agents_3.png";
    public static final String AGENTS_URL_4 = "com/socialsim/view/image/Office/office_agents_4.png";

    public static final List<AgentGraphicLocation> maleGuardGraphics;
    public static final List<AgentGraphicLocation> receptionistGraphics;
    public static final List<AgentGraphicLocation> maleJanitorGraphics;
    public static final List<AgentGraphicLocation> maleVisitorGraphics;
    public static final List<AgentGraphicLocation> femaleVisitorGraphics;
    public static final List<AgentGraphicLocation> femaleSecretaryGraphics;
    public static final List<AgentGraphicLocation> maleIntBusinessGraphics;
    public static final List<AgentGraphicLocation> femaleIntBusinessGraphics;
    public static final List<AgentGraphicLocation> maleExtBusinessGraphics;
    public static final List<AgentGraphicLocation> femaleExtBusinessGraphics;
    public static final List<AgentGraphicLocation> maleIntResearcherGraphics;
    public static final List<AgentGraphicLocation> femaleIntResearcherGraphics;
    public static final List<AgentGraphicLocation> maleExtResearcherGraphics;
    public static final List<AgentGraphicLocation> femaleExtResearcherGraphics;
    public static final List<AgentGraphicLocation> maleIntTechnicalGraphics;
    public static final List<AgentGraphicLocation> maleExtTechnicalGraphics;
    public static final List<AgentGraphicLocation> maleProBossGraphics;
    public static final List<AgentGraphicLocation> femaleProBossGraphics;
    public static final List<AgentGraphicLocation> maleAppBossGraphics;
    public static final List<AgentGraphicLocation> femaleAppBossGraphics;
    public static final List<AgentGraphicLocation> maleManagerGraphics;
    public static final List<AgentGraphicLocation> femaleManagerGraphics;
    public static final List<AgentGraphicLocation> driverGraphics;
    public static final List<AgentGraphicLocation> maleClientGraphics;
    public static final List<AgentGraphicLocation> femaleClientGraphics;

    static {
        maleGuardGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleGuardGraphics.add(new AgentGraphicLocation(0, i)); }
        receptionistGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { receptionistGraphics.add(new AgentGraphicLocation(1, i)); }
        maleJanitorGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleJanitorGraphics.add(new AgentGraphicLocation(2, i)); }
        maleVisitorGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleVisitorGraphics.add(new AgentGraphicLocation(3, i)); }
        femaleVisitorGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleVisitorGraphics.add(new AgentGraphicLocation(4, i)); }
        femaleSecretaryGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleSecretaryGraphics.add(new AgentGraphicLocation(5, i)); }
        driverGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { driverGraphics.add(new AgentGraphicLocation(6, i)); }

        maleIntBusinessGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleIntBusinessGraphics.add(new AgentGraphicLocation(0, i)); }
        femaleIntBusinessGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleIntBusinessGraphics.add(new AgentGraphicLocation(1, i)); }
        maleExtBusinessGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleExtBusinessGraphics.add(new AgentGraphicLocation(2, i)); }
        femaleExtBusinessGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleExtBusinessGraphics.add(new AgentGraphicLocation(3, i)); }
        maleIntResearcherGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleIntResearcherGraphics.add(new AgentGraphicLocation(4, i)); }
        femaleIntResearcherGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleIntResearcherGraphics.add(new AgentGraphicLocation(5, i)); }
        maleExtResearcherGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleExtResearcherGraphics.add(new AgentGraphicLocation(6, i)); }
        femaleExtResearcherGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleExtResearcherGraphics.add(new AgentGraphicLocation(7, i)); }

        maleIntTechnicalGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleIntTechnicalGraphics.add(new AgentGraphicLocation(0, i)); }
        maleExtTechnicalGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleExtTechnicalGraphics.add(new AgentGraphicLocation(1, i)); }
        maleProBossGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleProBossGraphics.add(new AgentGraphicLocation(2, i)); }
        femaleProBossGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleProBossGraphics.add(new AgentGraphicLocation(3, i)); }
        maleAppBossGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleAppBossGraphics.add(new AgentGraphicLocation(4, i)); }
        femaleAppBossGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleAppBossGraphics.add(new AgentGraphicLocation(5, i)); }
        maleManagerGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleManagerGraphics.add(new AgentGraphicLocation(6, i)); }
        femaleManagerGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleManagerGraphics.add(new AgentGraphicLocation(7, i)); }

        maleClientGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { maleClientGraphics.add(new AgentGraphicLocation(0, i)); }
        femaleClientGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++) { femaleClientGraphics.add(new AgentGraphicLocation(1, i)); }
    }

    private final OfficeAgent agent;
    protected final List<AgentGraphicLocation> graphics;
    protected int graphicIndex;

    public OfficeAgentGraphic(OfficeAgent agent) {
        this.agent = agent;
        this.graphics = new ArrayList<>();

        List<AgentGraphicLocation> agentGraphics = null;

        if (agent.getType() == OfficeAgent.Type.GUARD && agent.getGender() == OfficeAgent.Gender.MALE) {
            agentGraphics = maleGuardGraphics;
        }
        else if (agent.getType() == OfficeAgent.Type.RECEPTIONIST) {
            agentGraphics = receptionistGraphics;
        }
        else if (agent.getType() == OfficeAgent.Type.JANITOR && agent.getGender() == OfficeAgent.Gender.MALE) {
            agentGraphics = maleJanitorGraphics;
        }
        else if (agent.getType() == OfficeAgent.Type.VISITOR && agent.getGender() == OfficeAgent.Gender.MALE) {
            agentGraphics = maleVisitorGraphics;
        }
        else if (agent.getType() == OfficeAgent.Type.VISITOR && agent.getGender() == OfficeAgent.Gender.FEMALE) {
            agentGraphics = femaleVisitorGraphics;
        }
        else if (agent.getType() == OfficeAgent.Type.SECRETARY && agent.getGender() == OfficeAgent.Gender.FEMALE) {
            agentGraphics = femaleSecretaryGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.INT_BUSINESS) {
            agentGraphics = maleIntBusinessGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.INT_BUSINESS) {
            agentGraphics = femaleIntBusinessGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.EXT_BUSINESS) {
            agentGraphics = maleExtBusinessGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.EXT_BUSINESS) {
            agentGraphics = femaleExtBusinessGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.INT_RESEARCHER) {
            agentGraphics = maleIntResearcherGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.INT_RESEARCHER) {
            agentGraphics = femaleIntResearcherGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.EXT_RESEARCHER) {
            agentGraphics = maleExtResearcherGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.EXT_RESEARCHER) {
            agentGraphics = femaleExtResearcherGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.INT_TECHNICAL) {
            agentGraphics = maleIntTechnicalGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.EXT_TECHNICAL) {
            agentGraphics = maleExtTechnicalGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.PROFESSIONAL_BOSS) {
            agentGraphics = maleProBossGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.PROFESSIONAL_BOSS) {
            agentGraphics = femaleProBossGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.APPROACHABLE_BOSS) {
            agentGraphics = maleAppBossGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.APPROACHABLE_BOSS) {
            agentGraphics = femaleAppBossGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.MANAGER) {
            agentGraphics = maleManagerGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.MANAGER) {
            agentGraphics = femaleManagerGraphics;
        }
        else if (agent.getPersona() == OfficeAgent.Persona.DRIVER) {
            agentGraphics = driverGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.MALE && agent.getPersona() == OfficeAgent.Persona.CLIENT) {
            agentGraphics = maleClientGraphics;
        }
        else if (agent.getGender() == OfficeAgent.Gender.FEMALE && agent.getPersona() == OfficeAgent.Persona.CLIENT) {
            agentGraphics = femaleClientGraphics;
        }

        for (AgentGraphicLocation agentGraphicLocations : agentGraphics) {
            AgentGraphicLocation newAgentGraphicLocation = new AgentGraphicLocation(agentGraphicLocations.getGraphicRow(), agentGraphicLocations.getGraphicColumn());

            newAgentGraphicLocation.setGraphicWidth(1);
            newAgentGraphicLocation.setGraphicHeight(1);
            this.graphics.add(newAgentGraphicLocation);
        }

        this.graphicIndex = 2;
    }

    public OfficeAgent getOfficeAgent() {
        return agent;
    }

    public AgentGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    public void change() {
        OfficeAgent agent = this.agent;

        double agentHeading = agent.getAgentMovement().getHeading();
        double agentHeadingDegrees = Math.toDegrees(agentHeading);

        if (agentHeadingDegrees >= 315 && agentHeadingDegrees < 360 || agentHeadingDegrees >= 0 && agentHeadingDegrees < 45) {
            this.graphicIndex = 1;
        }
        else if (agentHeadingDegrees >= 45 && agentHeadingDegrees < 135) {
            this.graphicIndex = 0;
        }
        else if (agentHeadingDegrees >= 135 && agentHeadingDegrees < 225) {
            this.graphicIndex = 3;
        }
        else if (agentHeadingDegrees >= 225 && agentHeadingDegrees < 315) {
            this.graphicIndex = 2;
        }
    }

    public static class AmenityGraphicScale {
        private int rowSpan;
        private int columnSpan;

        public AmenityGraphicScale(int rowSpan, int columnSpan) {
            this.rowSpan = rowSpan;
            this.columnSpan = columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public void setColumnSpan(int columnSpan) {
            this.columnSpan = columnSpan;
        }
    }

    public static class AmenityGraphicOffset {
        private int rowOffset;
        private int columnOffset;

        public AmenityGraphicOffset(int rowOffset, int columnOffset) {
            this.rowOffset = rowOffset;
            this.columnOffset = columnOffset;
        }

        public int getRowOffset() {
            return rowOffset;
        }

        public void setRowOffset(int rowOffset) {
            this.rowOffset = rowOffset;
        }

        public int getColumnOffset() {
            return columnOffset;
        }

        public void setColumnOffset(int columnOffset) {
            this.columnOffset = columnOffset;
        }
    }

}