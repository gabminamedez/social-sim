package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.BulletinGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

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
    public AmenityGraphic getGraphicObject() {
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
        public Bulletin create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new Bulletin(amenityBlocks, enabled);
        }
    }

}