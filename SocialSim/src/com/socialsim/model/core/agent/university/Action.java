package com.socialsim.model.core.agent.university;

import com.socialsim.model.core.environment.generic.Patch;

public class Action {

    public enum Name{
        GO_TO_CAFETERIA
    }

    private Name name;
    private int duration;
    private Patch destination;

    public Action(Patch destination, int duration){
        this.destination = destination;
        this.duration = duration;
    }
}
