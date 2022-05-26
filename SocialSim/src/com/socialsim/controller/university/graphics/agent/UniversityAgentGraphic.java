package com.socialsim.controller.university.graphics.agent;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.model.core.agent.university.UniversityAgent;
import java.util.ArrayList;
import java.util.List;

public class UniversityAgentGraphic extends Graphic {

    public static final String AGENTS_URL_1 = "com/socialsim/view/image/University/male_students.png";
    public static final String AGENTS_URL_2 = "com/socialsim/view/image/University/female_students.png";
    public static final String AGENTS_URL_3 = "com/socialsim/view/image/University/professors.png";
    public static final String AGENTS_URL_4 = "com/socialsim/view/image/University/guard_janitor_officer.png";

    public static final List<AgentGraphicLocation> intY1MaleStudents;
    public static final List<AgentGraphicLocation> extY1MaleStudents;
    public static final List<AgentGraphicLocation> intY2MaleStudents;
    public static final List<AgentGraphicLocation> extY2MaleStudents;
    public static final List<AgentGraphicLocation> intY3MaleStudents;
    public static final List<AgentGraphicLocation> extY3MaleStudents;
    public static final List<AgentGraphicLocation> intY4MaleStudents;
    public static final List<AgentGraphicLocation> extY4MaleStudents;

    public static final List<AgentGraphicLocation> intY1FemaleStudents;
    public static final List<AgentGraphicLocation> extY1FemaleStudents;
    public static final List<AgentGraphicLocation> intY2FemaleStudents;
    public static final List<AgentGraphicLocation> extY2FemaleStudents;
    public static final List<AgentGraphicLocation> intY3FemaleStudents;
    public static final List<AgentGraphicLocation> extY3FemaleStudents;
    public static final List<AgentGraphicLocation> intY4FemaleStudents;
    public static final List<AgentGraphicLocation> extY4FemaleStudents;

    public static final List<AgentGraphicLocation> strictMaleProfessor;
    public static final List<AgentGraphicLocation> strictFemaleProfessor;
    public static final List<AgentGraphicLocation> approachableMaleProfessor;
    public static final List<AgentGraphicLocation> approachableFemaleProfessor;

    public static final List<AgentGraphicLocation> maleGuardGraphics;
    public static final List<AgentGraphicLocation> femaleGuardGraphics;
    public static final List<AgentGraphicLocation> maleJanitorGraphics;
    public static final List<AgentGraphicLocation> femaleJanitorGraphics;
    public static final List<AgentGraphicLocation> maleOfficerGraphics;
    public static final List<AgentGraphicLocation> femaleOfficerGraphics;

    static {
        intY1MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY1MaleStudents.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            intY1MaleStudents.add(new AgentGraphicLocation(8, i));
        extY1MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY1MaleStudents.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            extY1MaleStudents.add(new AgentGraphicLocation(9, i));
        intY2MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY2MaleStudents.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            intY2MaleStudents.add(new AgentGraphicLocation(10, i));
        extY2MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY2MaleStudents.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            extY2MaleStudents.add(new AgentGraphicLocation(11, i));
        intY3MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY3MaleStudents.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            intY3MaleStudents.add(new AgentGraphicLocation(12, i));
        extY3MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY3MaleStudents.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            extY3MaleStudents.add(new AgentGraphicLocation(13, i));
        intY4MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY4MaleStudents.add(new AgentGraphicLocation(6, i));
        for (int i = 0; i < 4; i++)
            intY4MaleStudents.add(new AgentGraphicLocation(14, i));
        extY4MaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY4MaleStudents.add(new AgentGraphicLocation(7, i));
        for (int i = 0; i < 4; i++)
            extY4MaleStudents.add(new AgentGraphicLocation(15, i));

        intY1FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY1FemaleStudents.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            intY1FemaleStudents.add(new AgentGraphicLocation(8, i));
        extY1FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY1FemaleStudents.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            extY1FemaleStudents.add(new AgentGraphicLocation(9, i));
        intY2FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY2FemaleStudents.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            intY2FemaleStudents.add(new AgentGraphicLocation(10, i));
        extY2FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY2FemaleStudents.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            extY2FemaleStudents.add(new AgentGraphicLocation(11, i));
        intY3FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY3FemaleStudents.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            intY3FemaleStudents.add(new AgentGraphicLocation(12, i));
        extY3FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY3FemaleStudents.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            extY3FemaleStudents.add(new AgentGraphicLocation(13, i));
        intY4FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            intY4FemaleStudents.add(new AgentGraphicLocation(6, i));
        for (int i = 0; i < 4; i++)
            intY4FemaleStudents.add(new AgentGraphicLocation(14, i));
        extY4FemaleStudents = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            extY4FemaleStudents.add(new AgentGraphicLocation(7, i));
        for (int i = 0; i < 4; i++)
            extY4FemaleStudents.add(new AgentGraphicLocation(15, i));

        strictMaleProfessor = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            strictMaleProfessor.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            strictMaleProfessor.add(new AgentGraphicLocation(4, i));
        strictFemaleProfessor = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            strictFemaleProfessor.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            strictFemaleProfessor.add(new AgentGraphicLocation(5, i));
        approachableMaleProfessor = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            approachableMaleProfessor.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            approachableMaleProfessor.add(new AgentGraphicLocation(6, i));
        approachableFemaleProfessor = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            approachableFemaleProfessor.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            approachableFemaleProfessor.add(new AgentGraphicLocation(7, i));

        maleGuardGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleGuardGraphics.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            maleGuardGraphics.add(new AgentGraphicLocation(6, i));
        femaleGuardGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleGuardGraphics.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            femaleGuardGraphics.add(new AgentGraphicLocation(7, i));
        maleJanitorGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleJanitorGraphics.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            maleJanitorGraphics.add(new AgentGraphicLocation(8, i));
        femaleJanitorGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleJanitorGraphics.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            femaleJanitorGraphics.add(new AgentGraphicLocation(9, i));
        maleOfficerGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleOfficerGraphics.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            maleOfficerGraphics.add(new AgentGraphicLocation(10, i));
        femaleOfficerGraphics = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleOfficerGraphics.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            femaleOfficerGraphics.add(new AgentGraphicLocation(11, i));
    }

    private final UniversityAgent agent;
    protected final List<AgentGraphicLocation> graphics;
    protected int graphicIndex;

    public UniversityAgentGraphic(UniversityAgent agent) {
        this.agent = agent;
        this.graphics = new ArrayList<>();
        
        List<AgentGraphicLocation> agentGraphics = null;

        if (agent.getType() == UniversityAgent.Type.GUARD && agent.getGender() == UniversityAgent.Gender.MALE) {
            agentGraphics = maleGuardGraphics;
        }
        else if (agent.getType() == UniversityAgent.Type.GUARD && agent.getGender() == UniversityAgent.Gender.FEMALE) {
            agentGraphics = femaleGuardGraphics;
        }
        else if (agent.getType() == UniversityAgent.Type.JANITOR && agent.getGender() == UniversityAgent.Gender.MALE) {
            agentGraphics = maleJanitorGraphics;
        }
        else if (agent.getType() == UniversityAgent.Type.JANITOR && agent.getGender() == UniversityAgent.Gender.FEMALE) {
            agentGraphics = femaleJanitorGraphics;
        }
        else if (agent.getType() == UniversityAgent.Type.STAFF && agent.getGender() == UniversityAgent.Gender.MALE) {
            agentGraphics = maleOfficerGraphics;
        }
        else if (agent.getType() == UniversityAgent.Type.STAFF && agent.getGender() == UniversityAgent.Gender.FEMALE) {
            agentGraphics = femaleOfficerGraphics;
        }
        else if (agent.getGender() == UniversityAgent.Gender.MALE && agent.getPersona() == UniversityAgent.Persona.STRICT_PROFESSOR) {
            agentGraphics = strictMaleProfessor;
        }
        else if (agent.getGender() == UniversityAgent.Gender.FEMALE && agent.getPersona() == UniversityAgent.Persona.STRICT_PROFESSOR) {
            agentGraphics = strictFemaleProfessor;
        }
        else if (agent.getGender() == UniversityAgent.Gender.MALE && agent.getPersona() == UniversityAgent.Persona.APPROACHABLE_PROFESSOR) {
            agentGraphics = approachableMaleProfessor;
        }
        else if (agent.getGender() == UniversityAgent.Gender.FEMALE && agent.getPersona() == UniversityAgent.Persona.APPROACHABLE_PROFESSOR) {
            agentGraphics = approachableFemaleProfessor;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT)) {
            agentGraphics = intY1MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT)) {
            agentGraphics = intY1FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT)) {
            agentGraphics = intY2MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT)) {
            agentGraphics = intY2FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT)) {
            agentGraphics = intY3MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT)) {
            agentGraphics = intY3FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT)) {
            agentGraphics = intY4MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT || agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT)) {
            agentGraphics = intY4FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT)) {
            agentGraphics = extY1MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT)) {
            agentGraphics = extY1FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT)) {
            agentGraphics = extY2MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT)) {
            agentGraphics = extY2FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT)) {
            agentGraphics = extY3MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT)) {
            agentGraphics = extY3FemaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.MALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT)) {
            agentGraphics = extY4MaleStudents;
        }
        else if(agent.getGender() == UniversityAgent.Gender.FEMALE && (agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT || agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT)) {
            agentGraphics = extY4FemaleStudents;
        }

        for (AgentGraphicLocation agentGraphicLocations : agentGraphics) {
            AgentGraphicLocation newAgentGraphicLocation = new AgentGraphicLocation(agentGraphicLocations.getGraphicRow(), agentGraphicLocations.getGraphicColumn());

            newAgentGraphicLocation.setGraphicWidth(1);
            newAgentGraphicLocation.setGraphicHeight(1);
            this.graphics.add(newAgentGraphicLocation);
        }

        this.graphicIndex = 2;
    }

    public UniversityAgent getUniversityAgent() {
        return agent;
    }

    public AgentGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    public void change() {
        UniversityAgent agent = this.agent;

        double agentHeading = agent.getAgentMovement().getHeading();
        double agentHeadingDegrees = Math.toDegrees(agentHeading);

        if (agentHeadingDegrees > 360){
            agentHeadingDegrees -= 360;
        } else if (agentHeadingDegrees < 0) {
            agentHeadingDegrees += 360;
        }

        if (agentHeadingDegrees >= 315 && agentHeadingDegrees < 360 || agentHeadingDegrees >= 0 && agentHeadingDegrees < 45) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 5;
//                System.out.println("Interacting");
            }
            else {
                this.graphicIndex = 1;
//                System.out.println("Not Interacting");
            }
        }
        else if (agentHeadingDegrees >= 45 && agentHeadingDegrees < 135) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 4;
//                System.out.println("Interacting");
            }
            else {
                this.graphicIndex = 0;
//                System.out.println("Not Interacting");
            }
        }
        else if (agentHeadingDegrees >= 135 && agentHeadingDegrees < 225) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 7;
//                System.out.println("Interacting");
            }
            else {
                this.graphicIndex = 3;
//                System.out.println("Not Interacting");
            }
        }
        else if (agentHeadingDegrees >= 225 && agentHeadingDegrees < 315) {
            if (this.agent.getAgentMovement().isInteracting()) {
                this.graphicIndex = 6;
//                System.out.println("Interacting");
            }
            else {
                this.graphicIndex = 2;
//                System.out.println("Not Interacting");
            }
        }
    }

}