package com.socialsim.model.core.agent;

import com.socialsim.controller.graphics.agent.AgentGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.PatchObject;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;

import java.util.Objects;

public class Agent extends PatchObject {

    private static int agentCount = 0;
    private static int guardCount = 0;
    private static int janitorCount = 0;
    private static int officerCount = 0;
    private static int professorCount = 0;
    private static int studentCount = 0;

    private final int id;
    private final Agent.Type type;
    private final Agent.Gender gender;
    private final int age;

    private final AgentGraphic agentGraphic;
    // private final AgentMovement agentMovement;

    public static final Agent.AgentFactory agentFactory;

    static {
        agentFactory = new Agent.AgentFactory();
    }

    private Agent(Agent.Type type, Agent.Gender gender, int age, Patch spawnPatch) {
        this.id = agentCount;
        if (type == Agent.Type.GUARD) {
            Agent.guardCount++;
        }
        else if (type == Agent.Type.JANITOR) {
            Agent.janitorCount++;
        }
        else if (type == Type.OFFICER) {
            Agent.officerCount++;
        }
        else if (type == Agent.Type.PROFESSOR) {
            Agent.professorCount++;
        }
        else if (type == Agent.Type.STUDENT) {
            Agent.studentCount++;
        }
        Agent.agentCount++;

        this.type = type;
        this.gender = gender;
        this.age = age;

        this.agentGraphic = new AgentGraphic(this);
        UniversityGate universityGate = (UniversityGate) spawnPatch.getAmenityBlock().getParent();
        // this.agentMovement = new AgentMovement(universityGate, this, spawnPatch.getPatchCenterCoordinates());
    }

    public int getId() {
        return id;
    }

    public Agent.Type getType() {
        return type;
    }

    public Agent.Gender getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public AgentGraphic getAgentGraphic() {
        return agentGraphic;
    }

//    public AgentMovement getAgentMovement() {
//        return agentMovement;
//    }

    public static class AgentFactory extends ObjectFactory {
        public Agent create(Agent.Type type, Agent.Gender gender, int age, Patch spawnPatch) {
            return new Agent(type, gender, age, spawnPatch);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;

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