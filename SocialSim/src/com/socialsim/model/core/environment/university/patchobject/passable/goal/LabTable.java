package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.LabTableGraphic;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

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
    public AmenityGraphic getGraphicObject() {
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

        private LabTableBlock(UniversityPatch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class LabTableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public LabTable.LabTableBlock create(UniversityPatch patch, boolean attractor, boolean hasGraphic) {
                return new LabTable.LabTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class LabTableFactory extends GoalFactory {
        public LabTable create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new LabTable(amenityBlocks, enabled);
        }
    }

}