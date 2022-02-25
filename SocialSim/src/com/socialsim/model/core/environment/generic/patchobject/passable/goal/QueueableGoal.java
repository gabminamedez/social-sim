package com.socialsim.model.core.environment.generic.patchobject.passable.goal;

import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.simulator.Simulator;
import java.util.List;

public abstract class QueueableGoal extends NonObstacle implements Drawable {

    private int waitingTime;
    private int waitingTimeLeft;

    protected QueueableGoal(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled);

        this.waitingTime = waitingTime;
        resetWaitingTime();
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void resetWaitingTime() {
        final int minimumWaitingTime = 1;
        double standardDeviation = 15.2;
        double computedWaitingTime = Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * standardDeviation + this.waitingTime;

        int waitingTimeLeft = (int) Math.round(computedWaitingTime);

        if (waitingTimeLeft <= minimumWaitingTime) {
            waitingTimeLeft = minimumWaitingTime;
        }

        this.waitingTimeLeft = waitingTimeLeft;
    }

    public static boolean isQueueableGoal(Amenity amenity) {
        return amenity instanceof Goal;
    }

    public static QueueableGoal toQueueableGoal(Amenity amenity) {
        if (isQueueableGoal(amenity)) {
            return (QueueableGoal) amenity;
        }
        else {
            return null;
        }
    }

    public static abstract class QueueableGoalFactory extends AmenityFactory {
    }

}