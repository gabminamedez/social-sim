package com.socialsim.model.core.environment.generic.patchobject.passable.goal;

import com.socialsim.model.core.environment.generic.patchfield.headful.QueueObject;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.Queueable;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.simulator.Simulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class QueueableGoal extends NonObstacle implements Queueable, Drawable {

    private int waitingTime;
    private int waitingTimeLeft;
    private final Map<QueueObject, AmenityBlock> queueObjectAmenityBlockMap;

    protected QueueableGoal(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled);

        this.waitingTime = waitingTime;
        this.queueObjectAmenityBlockMap = new HashMap<>();
        resetWaitingTime();
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Map<QueueObject, AmenityBlock> getQueueObjectAmenityBlockMap() {
        return queueObjectAmenityBlockMap;
    }

    public boolean allowPass() { // Check if this goal will now allow an agent to pass
        this.waitingTimeLeft--;

        return this.waitingTimeLeft <= 0;
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