package com.socialsim.controller.graphics.agent;

import com.socialsim.controller.graphics.Graphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.Environment;

import java.util.ArrayList;
import java.util.List;

public class AgentGraphic extends Graphic {

    public static final String AGENT_SPRITE_SHEET_URL = "com/socialsim/view/image/agent_spritesheet.png";
    public static final List<AgentGraphicLocation> maleGuardGraphics;
    public static final List<AgentGraphicLocation> femaleGuardGraphics;
    public static final List<AgentGraphicLocation> maleJanitorGraphics;
    public static final List<AgentGraphicLocation> femaleJanitorGraphics;
    public static final List<AgentGraphicLocation> maleProfessorGraphics;
    public static final List<AgentGraphicLocation> femaleProfessorGraphics;
    public static final List<AgentGraphicLocation> maleStudentGraphics;
    public static final List<AgentGraphicLocation> femaleStudentGraphics;

    static {
        maleGuardGraphics = new ArrayList<>();
        maleGuardGraphics.add(new AgentGraphicLocation(0, 0));
        maleGuardGraphics.add(new AgentGraphicLocation(0, 1));
        maleGuardGraphics.add(new AgentGraphicLocation(0, 2));
        maleGuardGraphics.add(new AgentGraphicLocation(0, 3));

        femaleGuardGraphics = new ArrayList<>();
        femaleGuardGraphics.add(new AgentGraphicLocation(1, 0));
        femaleGuardGraphics.add(new AgentGraphicLocation(1, 1));
        femaleGuardGraphics.add(new AgentGraphicLocation(1, 2));
        femaleGuardGraphics.add(new AgentGraphicLocation(1, 3));

        maleJanitorGraphics = new ArrayList<>();
        maleJanitorGraphics.add(new AgentGraphicLocation(2, 0));
        maleJanitorGraphics.add(new AgentGraphicLocation(2, 1));
        maleJanitorGraphics.add(new AgentGraphicLocation(2, 2));
        maleJanitorGraphics.add(new AgentGraphicLocation(2, 3));

        femaleJanitorGraphics = new ArrayList<>();
        femaleJanitorGraphics.add(new AgentGraphicLocation(3, 0));
        femaleJanitorGraphics.add(new AgentGraphicLocation(3, 1));
        femaleJanitorGraphics.add(new AgentGraphicLocation(3, 2));
        femaleJanitorGraphics.add(new AgentGraphicLocation(3, 3));

        maleProfessorGraphics = new ArrayList<>();
        maleProfessorGraphics.add(new AgentGraphicLocation(4, 0));
        maleProfessorGraphics.add(new AgentGraphicLocation(4, 1));
        maleProfessorGraphics.add(new AgentGraphicLocation(4, 2));
        maleProfessorGraphics.add(new AgentGraphicLocation(4, 3));

        femaleProfessorGraphics = new ArrayList<>();
        femaleProfessorGraphics.add(new AgentGraphicLocation(5, 0));
        femaleProfessorGraphics.add(new AgentGraphicLocation(5, 1));
        femaleProfessorGraphics.add(new AgentGraphicLocation(5, 2));
        femaleProfessorGraphics.add(new AgentGraphicLocation(5, 3));

        maleStudentGraphics = new ArrayList<>();
        maleStudentGraphics.add(new AgentGraphicLocation(6, 0));
        maleStudentGraphics.add(new AgentGraphicLocation(6, 1));
        maleStudentGraphics.add(new AgentGraphicLocation(6, 2));
        maleStudentGraphics.add(new AgentGraphicLocation(6, 3));

        femaleStudentGraphics = new ArrayList<>();
        femaleStudentGraphics.add(new AgentGraphicLocation(7, 0));
        femaleStudentGraphics.add(new AgentGraphicLocation(7, 1));
        femaleStudentGraphics.add(new AgentGraphicLocation(7, 2));
        femaleStudentGraphics.add(new AgentGraphicLocation(7, 3));
    }

    private final Agent agent;
    protected final List<AgentGraphicLocation> graphics;
    protected int graphicIndex;

    public AgentGraphic(Agent agent) {
        this.agent = agent;
        this.graphics = new ArrayList<>();
        
        List<AgentGraphicLocation> agentGraphics = null;

        if (agent.getType() == Agent.Type.GUARD && agent.getGender() == Agent.Gender.MALE) {
            agentGraphics = maleGuardGraphics;
        }
        else if (agent.getType() == Agent.Type.GUARD && agent.getGender() == Agent.Gender.FEMALE) {
            agentGraphics = femaleGuardGraphics;
        }
        else if (agent.getType() == Agent.Type.JANITOR && agent.getGender() == Agent.Gender.MALE) {
            agentGraphics = maleJanitorGraphics;
        }
        else if (agent.getType() == Agent.Type.JANITOR && agent.getGender() == Agent.Gender.FEMALE) {
            agentGraphics = femaleJanitorGraphics;
        }
        else if (agent.getType() == Agent.Type.PROFESSOR && agent.getGender() == Agent.Gender.MALE) {
            agentGraphics = maleProfessorGraphics;
        }
        else if (agent.getType() == Agent.Type.PROFESSOR && agent.getGender() == Agent.Gender.FEMALE) {
            agentGraphics = femaleProfessorGraphics;
        }
        else if (agent.getType() == Agent.Type.STUDENT && agent.getGender() == Agent.Gender.MALE) {
            agentGraphics = maleStudentGraphics;
        }
        else if (agent.getType() == Agent.Type.STUDENT && agent.getGender() == Agent.Gender.FEMALE) {
            agentGraphics = femaleStudentGraphics;
        }

        for (AgentGraphicLocation agentGraphicLocations : agentGraphics) {
            AgentGraphicLocation newAgentGraphicLocation = new AgentGraphicLocation(agentGraphicLocations.getGraphicRow(), agentGraphicLocations.getGraphicColumn());

            newAgentGraphicLocation.setGraphicWidth(1);
            newAgentGraphicLocation.setGraphicHeight(1);
            this.graphics.add(newAgentGraphicLocation);
        }

        this.graphicIndex = 2;
    }

    public Agent getAgent() {
        return agent;
    }

    public AgentGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    public void change() {
        Agent agent = this.agent;

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

    public static class AmenityGraphicScale implements Environment {
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

    public static class AmenityGraphicOffset implements Environment {
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