package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.ReceptionTableGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class ReceptionTable extends Goal {

    public static final ReceptionTable.ReceptionTableFactory receptionTableFactory;
    private final ReceptionTableGraphic receptionTableGraphic;

    static {
        receptionTableFactory = new ReceptionTable.ReceptionTableFactory();
    }

    protected ReceptionTable(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.receptionTableGraphic = new ReceptionTableGraphic(this);
    }


    @Override
    public String toString() {
        return "ReceptionTable" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.receptionTableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.receptionTableGraphic.getGraphicLocation();
    }

    public static class ReceptionTableBlock extends Amenity.AmenityBlock {
        public static ReceptionTable.ReceptionTableBlock.ReceptionTableBlockFactory receptionTableBlockFactory;

        static {
            receptionTableBlockFactory = new ReceptionTable.ReceptionTableBlock.ReceptionTableBlockFactory();
        }

        private ReceptionTableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ReceptionTableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public ReceptionTable.ReceptionTableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ReceptionTable.ReceptionTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ReceptionTableFactory extends GoalFactory {
        public static ReceptionTable create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new ReceptionTable(amenityBlocks, enabled);
        }
    }

}