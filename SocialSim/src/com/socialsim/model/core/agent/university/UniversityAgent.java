package com.socialsim.model.core.agent.university;

import com.socialsim.controller.graphics.agent.university.UniversityAgentGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;

import java.util.Objects;

public class UniversityAgent extends Agent {

    private static int agentCount = 0;
    private static int guardCount = 0;
    private static int janitorCount = 0;
    private static int officerCount = 0;
    private static int professorCount = 0;
    private static int studentCount = 0;

    private final int id;
    private final UniversityAgent.Type type;
    private final UniversityAgent.Gender gender;
    private final int age;

    private final UniversityAgentGraphic agentGraphic;
    // private final AgentMovement agentMovement;

    public static final UniversityAgent.UniversityAgentFactory agentFactory;

    static {
        agentFactory = new UniversityAgent.UniversityAgentFactory();
    }

    private UniversityAgent(UniversityAgent.Type type, UniversityAgent.Gender gender, int age, Patch spawnPatch) {
        this.id = agentCount;
        if (type == UniversityAgent.Type.GUARD) {
            UniversityAgent.guardCount++;
        }
        else if (type == UniversityAgent.Type.JANITOR) {
            UniversityAgent.janitorCount++;
        }
        else if (type == Type.OFFICER) {
            UniversityAgent.officerCount++;
        }
        else if (type == UniversityAgent.Type.PROFESSOR) {
            UniversityAgent.professorCount++;
        }
        else if (type == UniversityAgent.Type.STUDENT) {
            UniversityAgent.studentCount++;
        }
        UniversityAgent.agentCount++;

        this.type = type;
        this.gender = gender;
        this.age = age;

        this.agentGraphic = new UniversityAgentGraphic(this);
        UniversityGate universityGate = (UniversityGate) spawnPatch.getAmenityBlock().getParent();
        // this.agentMovement = new AgentMovement(universityGate, this, spawnPatch.getPatchCenterCoordinates());
    }

    public int getId() {
        return id;
    }

    public UniversityAgent.Type getType() {
        return type;
    }

    public UniversityAgent.Gender getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public UniversityAgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

//    public AgentMovement getAgentMovement() {
//        return agentMovement;
//    }

    public static class UniversityAgentFactory extends Agent.AgentFactory {
        public static UniversityAgent create(UniversityAgent.Type type, UniversityAgent.Gender gender, int age, Patch spawnPatch) {
            return new UniversityAgent(type, gender, age, spawnPatch);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniversityAgent agent = (UniversityAgent) o;

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
        GUARD, JANITOR, OFFICER, PROFESSOR, STUDENT
    }

    public enum Gender {
        FEMALE, MALE
    }

}