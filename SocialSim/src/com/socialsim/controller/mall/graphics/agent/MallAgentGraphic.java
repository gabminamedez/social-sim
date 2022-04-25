package com.socialsim.controller.mall.graphics.agent;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.model.core.agent.mall.MallAgent;

import java.util.ArrayList;
import java.util.List;

public class MallAgentGraphic extends Graphic {

    public static final String AGENTS_URL = "com/socialsim/view/image/Mall/agent_sprites.png";
    public static final String AGENTS_URL1 = "com/socialsim/view/image/Mall/agent_sprites_1.png";

    public static final List<AgentGraphicLocation> maleErrandFamily;
    public static final List<AgentGraphicLocation> femaleErrandFamily;
    public static final List<AgentGraphicLocation> maleLoiterFamily;
    public static final List<AgentGraphicLocation> femaleLoiterFamily;
    public static final List<AgentGraphicLocation> maleErrandFriends;
    public static final List<AgentGraphicLocation> femaleErrandFriends;
    public static final List<AgentGraphicLocation> maleLoiterFriends;
    public static final List<AgentGraphicLocation> femaleLoiterFriends;
    public static final List<AgentGraphicLocation> maleErrandCouple;
    public static final List<AgentGraphicLocation> femaleErrandCouple;
    public static final List<AgentGraphicLocation> maleLoiterCouple;
    public static final List<AgentGraphicLocation> femaleLoiterCouple;
    public static final List<AgentGraphicLocation> maleErrandAlone;
    public static final List<AgentGraphicLocation> femaleErrandAlone;
    public static final List<AgentGraphicLocation> maleLoiterAlone;
    public static final List<AgentGraphicLocation> femaleLoiterAlone;
    public static final List<AgentGraphicLocation> maleStaffStore;
    public static final List<AgentGraphicLocation> femaleStaffStore;
    public static final List<AgentGraphicLocation> maleStaffResto;
    public static final List<AgentGraphicLocation> femaleStaffResto;
    public static final List<AgentGraphicLocation> maleStaffKiosk;
    public static final List<AgentGraphicLocation> femaleStaffKiosk;
    public static final List<AgentGraphicLocation> maleGuard;
    public static final List<AgentGraphicLocation> femaleGuard;
    public static final List<AgentGraphicLocation> maleJanitor;
    public static final List<AgentGraphicLocation> femaleJanitor;
    public static final List<AgentGraphicLocation> maleConcierger;
    public static final List<AgentGraphicLocation> femaleConcierger;

    static {
        maleErrandFamily = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleErrandFamily.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            maleErrandFamily.add(new AgentGraphicLocation(24, i));
        femaleErrandFamily = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleErrandFamily.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            femaleErrandFamily.add(new AgentGraphicLocation(25, i));
        maleLoiterFamily = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleLoiterFamily.add(new AgentGraphicLocation(2, i));
        for (int i = 0; i < 4; i++)
            maleLoiterFamily.add(new AgentGraphicLocation(26, i));
        femaleLoiterFamily = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleLoiterFamily.add(new AgentGraphicLocation(3, i));
        for (int i = 0; i < 4; i++)
            femaleLoiterFamily.add(new AgentGraphicLocation(27, i));
        maleErrandFriends = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleErrandFriends.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            maleErrandFriends.add(new AgentGraphicLocation(28, i));
        femaleErrandFriends = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleErrandFriends.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            femaleErrandFriends.add(new AgentGraphicLocation(29, i));
        maleLoiterFriends = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleLoiterFriends.add(new AgentGraphicLocation(6, i));
        for (int i = 0; i < 4; i++)
            maleLoiterFriends.add(new AgentGraphicLocation(30, i));
        femaleLoiterFriends = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleLoiterFriends.add(new AgentGraphicLocation(7, i));
        for (int i = 0; i < 4; i++)
            femaleLoiterFriends.add(new AgentGraphicLocation(31, i));
        maleErrandCouple = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleErrandCouple.add(new AgentGraphicLocation(8, i));
        for (int i = 0; i < 4; i++)
            maleErrandCouple.add(new AgentGraphicLocation(32, i));
        femaleErrandCouple = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleErrandCouple.add(new AgentGraphicLocation(9, i));
        for (int i = 0; i < 4; i++)
            femaleErrandCouple.add(new AgentGraphicLocation(33, i));
        maleLoiterCouple = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleLoiterCouple.add(new AgentGraphicLocation(10, i));
        for (int i = 0; i < 4; i++)
            maleLoiterCouple.add(new AgentGraphicLocation(34, i));
        femaleLoiterCouple = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleLoiterCouple.add(new AgentGraphicLocation(11, i));
        for (int i = 0; i < 4; i++)
            femaleLoiterCouple.add(new AgentGraphicLocation(35, i));
        maleErrandAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleErrandAlone.add(new AgentGraphicLocation(12, i));
        for (int i = 0; i < 4; i++)
            maleErrandAlone.add(new AgentGraphicLocation(36, i));
        femaleErrandAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleErrandAlone.add(new AgentGraphicLocation(13, i));
        for (int i = 0; i < 4; i++)
            femaleErrandAlone.add(new AgentGraphicLocation(37, i));
        maleLoiterAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleLoiterAlone.add(new AgentGraphicLocation(14, i));
        for (int i = 0; i < 4; i++)
            maleLoiterAlone.add(new AgentGraphicLocation(38, i));
        femaleLoiterAlone = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleLoiterAlone.add(new AgentGraphicLocation(15, i));
        for (int i = 0; i < 4; i++)
            femaleLoiterAlone.add(new AgentGraphicLocation(39, i));
        maleStaffStore = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleStaffStore.add(new AgentGraphicLocation(16, i));
        for (int i = 0; i < 4; i++)
            maleStaffStore.add(new AgentGraphicLocation(40, i));
        femaleStaffStore = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleStaffStore.add(new AgentGraphicLocation(17, i));
        for (int i = 0; i < 4; i++)
            femaleStaffStore.add(new AgentGraphicLocation(41, i));
        maleStaffKiosk = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleStaffKiosk.add(new AgentGraphicLocation(18, i));
        for (int i = 0; i < 4; i++)
            maleStaffKiosk.add(new AgentGraphicLocation(42, i));
        femaleStaffKiosk = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleStaffKiosk.add(new AgentGraphicLocation(19, i));
        for (int i = 0; i < 4; i++)
            femaleStaffKiosk.add(new AgentGraphicLocation(43, i));
        maleStaffResto = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleStaffResto.add(new AgentGraphicLocation(20, i));
        for (int i = 0; i < 4; i++)
            maleStaffResto.add(new AgentGraphicLocation(44, i));
        femaleStaffResto = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleStaffResto.add(new AgentGraphicLocation(21, i));
        for (int i = 0; i < 4; i++)
            femaleStaffResto.add(new AgentGraphicLocation(45, i));
        maleGuard = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleGuard.add(new AgentGraphicLocation(22, i));
        for (int i = 0; i < 4; i++)
            maleGuard.add(new AgentGraphicLocation(46, i));
        femaleGuard = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleGuard.add(new AgentGraphicLocation(23, i));
        for (int i = 0; i < 4; i++)
            femaleGuard.add(new AgentGraphicLocation(47, i));

        maleJanitor = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleJanitor.add(new AgentGraphicLocation(0, i));
        for (int i = 0; i < 4; i++)
            maleJanitor.add(new AgentGraphicLocation(6, i));

        femaleJanitor = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleJanitor.add(new AgentGraphicLocation(1, i));
        for (int i = 0; i < 4; i++)
            femaleJanitor.add(new AgentGraphicLocation(7, i));

        maleConcierger = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            maleConcierger.add(new AgentGraphicLocation(4, i));
        for (int i = 0; i < 4; i++)
            maleConcierger.add(new AgentGraphicLocation(10, i));

        femaleConcierger = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            femaleConcierger.add(new AgentGraphicLocation(5, i));
        for (int i = 0; i < 4; i++)
            femaleConcierger.add(new AgentGraphicLocation(11, i));
    }

    private final MallAgent agent;
    protected final List<AgentGraphicLocation> graphics;
    protected int graphicIndex;

    public MallAgentGraphic(MallAgent agent) {
        this.agent = agent;
        this.graphics = new ArrayList<>();

        List<AgentGraphicLocation> agentGraphics = null;

        if (agent.getType() == MallAgent.Type.GUARD && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleGuard;
        }
        else if (agent.getType() == MallAgent.Type.GUARD && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleGuard;
        }
        else if ((agent.getType() == MallAgent.Type.STAFF_STORE_SALES || agent.getType() == MallAgent.Type.STAFF_STORE_CASHIER) && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleStaffStore;
        }
        else if ((agent.getType() == MallAgent.Type.STAFF_STORE_SALES || agent.getType() == MallAgent.Type.STAFF_STORE_CASHIER) && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleStaffStore;
        }
        else if (agent.getType() == MallAgent.Type.STAFF_KIOSK && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleStaffKiosk;
        }
        else if (agent.getType() == MallAgent.Type.STAFF_KIOSK && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleStaffKiosk;
        }
        else if (agent.getType() == MallAgent.Type.STAFF_RESTO && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleStaffResto;
        }
        else if (agent.getType() == MallAgent.Type.STAFF_RESTO && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleStaffResto;
        }
        else if (agent.getType() == MallAgent.Type.JANITOR && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleJanitor;
        }
        else if (agent.getType() == MallAgent.Type.JANITOR && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleJanitor;
        }
        else if (agent.getType() == MallAgent.Type.CONCIERGER && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleConcierger;
        }
        else if (agent.getType() == MallAgent.Type.CONCIERGER && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleConcierger;
        }
        else if (agent.getPersona() == MallAgent.Persona.ERRAND_FAMILY && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleErrandFamily;
        }
        else if (agent.getPersona() == MallAgent.Persona.ERRAND_FAMILY && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleErrandFamily;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_FAMILY && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleLoiterFamily;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_FAMILY && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleLoiterFamily;
        }
        else if (agent.getPersona() == MallAgent.Persona.ERRAND_FRIENDS && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleErrandFriends;
        }
        else if (agent.getPersona() == MallAgent.Persona.ERRAND_FRIENDS && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleErrandFriends;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_FRIENDS && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleLoiterFriends;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_FRIENDS && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleLoiterFriends;
        }
        else if (agent.getPersona() == MallAgent.Persona.ERRAND_ALONE && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleErrandAlone;
        }
        else if (agent.getPersona() == MallAgent.Persona.ERRAND_ALONE && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleErrandAlone;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_ALONE && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleLoiterAlone;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_ALONE && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleLoiterAlone;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_COUPLE && agent.getGender() == MallAgent.Gender.MALE) {
            agentGraphics = maleLoiterCouple;
        }
        else if (agent.getPersona() == MallAgent.Persona.LOITER_COUPLE && agent.getGender() == MallAgent.Gender.FEMALE) {
            agentGraphics = femaleLoiterCouple;
        }

        for (AgentGraphicLocation agentGraphicLocations : agentGraphics) {
            AgentGraphicLocation newAgentGraphicLocation = new AgentGraphicLocation(agentGraphicLocations.getGraphicRow(), agentGraphicLocations.getGraphicColumn());
            newAgentGraphicLocation.setGraphicWidth(1);
            newAgentGraphicLocation.setGraphicHeight(1);
            this.graphics.add(newAgentGraphicLocation);
        }

        this.graphicIndex = 2;
    }

    public MallAgent getMallAgent() {
        return agent;
    }

    public AgentGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    public void change() {
        MallAgent agent = this.agent;

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
            else {
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
            else{
                this.graphicIndex = 2;
            }
        }
    }

}