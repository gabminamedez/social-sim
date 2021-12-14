package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.PrinterGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Printer extends Goal {

    public static final Printer.PrinterFactory printerFactory;
    private final PrinterGraphic printerGraphic;

    static {
        printerFactory = new Printer.PrinterFactory();
    }

    protected Printer(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.printerGraphic = new PrinterGraphic(this);
    }


    @Override
    public String toString() {
        return "Printer" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.printerGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.printerGraphic.getGraphicLocation();
    }

    public static class PrinterBlock extends Amenity.AmenityBlock {
        public static Printer.PrinterBlock.PrinterBlockFactory printerBlockFactory;

        static {
            printerBlockFactory = new Printer.PrinterBlock.PrinterBlockFactory();
        }

        private PrinterBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class PrinterBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Printer.PrinterBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Printer.PrinterBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class PrinterFactory extends GoalFactory {
        public static Printer create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Printer(amenityBlocks, enabled);
        }
    }

}