package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.university.graphics.amenity.graphic.OfficeTableGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class OfficeTable extends Goal {

    public static final OfficeTable.OfficeTableFactory officeTableFactory;
    private final OfficeTableGraphic officeTableGraphic;

    static {
        officeTableFactory = new OfficeTable.OfficeTableFactory();
    }

    protected OfficeTable(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.officeTableGraphic = new OfficeTableGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "ProfTable" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.officeTableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.officeTableGraphic.getGraphicLocation();
    }

    public static class OfficeTableBlock extends AmenityBlock {
        public static OfficeTable.OfficeTableBlock.OfficeTableBlockFactory officeTableBlockFactory;

        static {
            officeTableBlockFactory = new OfficeTable.OfficeTableBlock.OfficeTableBlockFactory();
        }

        private OfficeTableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class OfficeTableBlockFactory extends AmenityBlockFactory {
            @Override
            public OfficeTable.OfficeTableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new OfficeTable.OfficeTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class OfficeTableFactory extends GoalFactory {
        public static OfficeTable create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new OfficeTable(amenityBlocks, enabled, facing);
        }
    }

}