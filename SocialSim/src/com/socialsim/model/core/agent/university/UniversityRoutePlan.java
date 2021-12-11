package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Fountain;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Security;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Staircase;

import java.util.*;

public class UniversityRoutePlan {

    public static final Map<UniversityAgent.Persona, List<Class<? extends BaseObject>>> PATTERN_PLANS; // Contains the list of pattern plans
    private Iterator<Class<? extends BaseObject>> currentRoutePlan; // Denotes the current route plan of the agent which owns this
    private Class<? extends BaseObject> currentClass; // Denotes the current class of the amenity/patchfield in the route plan

    static {
        PATTERN_PLANS = new HashMap<>();

        final List<Class<? extends BaseObject>> guardPlanList = new ArrayList<>();
        guardPlanList.add(Security.class);

        final List<Class<? extends BaseObject>> janitorPlanList = new ArrayList<>();
        janitorPlanList.add(Bathroom.class);
        janitorPlanList.add(Fountain.class);

        final List<Class<? extends BaseObject>> officerPlanList = new ArrayList<>();
        officerPlanList.add(Staircase.class);

        PATTERN_PLANS.put(UniversityAgent.Persona.GUARD, guardPlanList);
        PATTERN_PLANS.put(UniversityAgent.Persona.JANITOR, janitorPlanList);
        PATTERN_PLANS.put(UniversityAgent.Persona.OFFICER, officerPlanList);
    }

    public UniversityRoutePlan(UniversityAgent agent) {
        List<Class<? extends BaseObject>> routePlan = new ArrayList<>(PATTERN_PLANS.get(agent.getPersona()));

        this.currentRoutePlan = routePlan.iterator();

        if (agent.getPersona() == UniversityAgent.Persona.GUARD){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.JANITOR){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.OFFICER){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y2_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y3_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y4_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y1_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y2_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y3_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.INT_Y4_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y2_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y3_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y4_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y1_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y2_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y3_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.EXT_Y4_ORG_STUDENT){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.STRICT_PROFESSOR){

        }
        else if (agent.getPersona() == UniversityAgent.Persona.APPROACHABLE_PROFESSOR){

        }
        // Burn off the first class in the route plan, as the agent will have already spawned there
        setNextClass();
        setNextClass();
    }

    public void setNextClass() { // Set the next class in the route plan
        this.currentClass = this.currentRoutePlan.next();
    }

    public Iterator<Class<? extends BaseObject>> getCurrentRoutePlan() {
        return currentRoutePlan;
    }

    public Class<? extends BaseObject> getCurrentClass() {
        return currentClass;
    }

}