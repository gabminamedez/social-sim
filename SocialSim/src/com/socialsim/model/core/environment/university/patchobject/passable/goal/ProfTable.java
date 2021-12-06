package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.ProfTableGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.List;

public class ProfTable extends Goal {

    public static final ProfTable.ProfTableFactory profTableFactory;
    private final ProfTableGraphic profTableGraphic;

    static {
        profTableFactory = new ProfTable.ProfTableFactory();
    }

    protected ProfTable(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.profTableGraphic = new ProfTableGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "ProfTable" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.profTableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.profTableGraphic.getGraphicLocation();
    }

    public static class ProfTableBlock extends Amenity.AmenityBlock {
        public static ProfTable.ProfTableBlock.ProfTableBlockFactory profTableBlockFactory;

        static {
            profTableBlockFactory = new ProfTable.ProfTableBlock.ProfTableBlockFactory();
        }

        private ProfTableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ProfTableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public ProfTable.ProfTableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ProfTable.ProfTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ProfTableFactory extends GoalFactory {
        public static ProfTable create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new ProfTable(amenityBlocks, enabled, facing);
        }
    }

}