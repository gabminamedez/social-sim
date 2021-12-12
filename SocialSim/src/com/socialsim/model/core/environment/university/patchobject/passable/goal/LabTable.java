package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.graphic.LabTableGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class LabTable extends Goal {

    public static final LabTable.LabTableFactory labTableFactory;
    private final LabTableGraphic labTableGraphic;

    static {
        labTableFactory = new LabTable.LabTableFactory();
    }

    protected LabTable(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.labTableGraphic = new LabTableGraphic(this);
    }


    @Override
    public String toString() {
        return "LabTable" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.labTableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.labTableGraphic.getGraphicLocation();
    }

    public static class LabTableBlock extends Amenity.AmenityBlock {
        public static LabTable.LabTableBlock.LabTableBlockFactory labTableBlockFactory;

        static {
            labTableBlockFactory = new LabTable.LabTableBlock.LabTableBlockFactory();
        }

        private LabTableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class LabTableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public LabTable.LabTableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new LabTable.LabTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class LabTableFactory extends GoalFactory {
        public static LabTable create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new LabTable(amenityBlocks, enabled);
        }
    }

}