package com.socialsim.model.core.environment.office.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.controller.office.graphics.amenity.graphic.BulletinGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Bulletin extends Goal {

    public static final Bulletin.BulletinFactory bulletinFactory;
    private final BulletinGraphic bulletinGraphic;

    static {
        bulletinFactory = new Bulletin.BulletinFactory();
    }

    protected Bulletin(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.bulletinGraphic = new BulletinGraphic(this);
    }


    @Override
    public String toString() {
        return "Bulletin" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public OfficeAmenityGraphic getGraphicObject() {
        return this.bulletinGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.bulletinGraphic.getGraphicLocation();
    }

    public static class BulletinBlock extends Amenity.AmenityBlock {
        public static Bulletin.BulletinBlock.BulletinBlockFactory bulletinBlockFactory;

        static {
            bulletinBlockFactory = new Bulletin.BulletinBlock.BulletinBlockFactory();
        }

        private BulletinBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class BulletinBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Bulletin.BulletinBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Bulletin.BulletinBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class BulletinFactory extends GoalFactory {
        public static Bulletin create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Bulletin(amenityBlocks, enabled);
        }
    }

}