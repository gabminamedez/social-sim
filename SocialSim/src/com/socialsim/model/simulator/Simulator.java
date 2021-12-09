package com.socialsim.model.simulator;

import java.util.Random;

public abstract class Simulator {

    public static final Random RANDOM_NUMBER_GENERATOR; // Random number generator for all purposes in the simulation

    static {
        RANDOM_NUMBER_GENERATOR = new Random();
    }

}