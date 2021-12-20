package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.FountainGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.QueueableGoal;

import java.util.List;

public class Fountain extends QueueableGoal {

    public static final long serialVersionUID = -4576236425454267953L;
    public static final FountainFactory fountainFactory;
    private final FountainGraphic fountainGraphic;

    static {
        fountainFactory = new FountainFactory();
    }

    protected Fountain(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled, waitingTime);

        this.fountainGraphic = new FountainGraphic(this);
    }

    @Override
    public String toString() {
        return "Fountain" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.fountainGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.fountainGraphic.getGraphicLocation();
    }

    public static class FountainBlock extends AmenityBlock {
        public static FountainBlockFactory fountainBlockFactory;

        static {
            fountainBlockFactory = new FountainBlockFactory();
        }

        private FountainBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class FountainBlockFactory extends AmenityBlockFactory {
            @Override
            public FountainBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new FountainBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class FountainFactory extends Goal.GoalFactory {
        public static Fountain create(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
            return new Fountain(amenityBlocks, enabled, waitingTime);
        }
    }

}