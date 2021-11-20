package com.socialsim.university.model.core.environment.patch.patchobject.miscellaneous;

import com.socialsim.university.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.university.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.university.controller.graphics.amenity.WallGraphic;
import com.socialsim.university.model.core.environment.patch.Patch;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public class Wall extends Obstacle {

    public static final WallFactory wallFactory;
    private final WallGraphic wallGraphic;

    static {
        wallFactory = new WallFactory();
    }

    protected Wall(List<AmenityBlock> amenityBlocks) {
        super(amenityBlocks);

        this.wallGraphic = new WallGraphic(this);
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.wallGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.wallGraphic.getGraphicLocation();
    }

    public static class WallBlock extends Amenity.AmenityBlock {
        public static Wall.WallBlock.WallBlockFactory wallBlockFactory;

        static {
            wallBlockFactory = new Wall.WallBlock.WallBlockFactory();
        }

        private WallBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class WallBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Wall.WallBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Wall.WallBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class WallFactory extends ObstacleFactory {
        public Wall create(List<AmenityBlock> amenityBlocks) {
            return new Wall(amenityBlocks);
        }
    }

}