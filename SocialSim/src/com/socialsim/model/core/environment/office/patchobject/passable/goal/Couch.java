package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.CouchGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Couch extends Goal {

    public static final Couch.CouchFactory couchFactory;
    private final CouchGraphic couchGraphic;

    static {
        couchFactory = new Couch.CouchFactory();
    }

    protected Couch(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.couchGraphic = new CouchGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "Couch" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.couchGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.couchGraphic.getGraphicLocation();
    }

    public static class CouchBlock extends Amenity.AmenityBlock {
        public static Couch.CouchBlock.CouchBlockFactory couchBlockFactory;

        static {
            couchBlockFactory = new Couch.CouchBlock.CouchBlockFactory();
        }

        private CouchBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class CouchBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Couch.CouchBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Couch.CouchBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class CouchFactory extends GoalFactory {
        public static Couch create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new Couch(amenityBlocks, enabled, facing);
        }
    }

}