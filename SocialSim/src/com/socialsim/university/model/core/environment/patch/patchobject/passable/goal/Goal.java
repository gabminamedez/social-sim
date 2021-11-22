package com.socialsim.university.model.core.environment.patch.patchobject.passable.goal;

import com.socialsim.university.model.core.environment.patch.patchfield.headful.QueueObject;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.university.model.core.environment.patch.patchobject.Drawable;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.NonObstacle;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.Queueable;

import java.util.List;

public abstract class Goal extends NonObstacle implements Queueable, Drawable {

    private int waitingTime;
    private int waitingTimeLeft;
    private final QueueObject queueObject;

    protected Goal(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, QueueObject queueObject) {
        super(amenityBlocks, enabled);

        this.waitingTime = waitingTime;
        this.waitingTimeLeft = this.waitingTime;
        this.queueObject = queueObject;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public boolean allowPass() {
        this.waitingTimeLeft--;

        if (this.waitingTimeLeft <= 0) {
            this.waitingTimeLeft = this.waitingTime;

            return true;
        }

        return false;
    }

    public static boolean isGoal(Amenity amenity) {
        return amenity instanceof Goal;
    }

    public static Goal toGoal(Amenity amenity) {
        if (isGoal(amenity)) {
            return (Goal) amenity;
        }
        else {
            return null;
        }
    }


    public QueueObject getQueueObject() {
        return this.queueObject;
    }

    public static abstract class GoalFactory extends AmenityFactory {
    }

}