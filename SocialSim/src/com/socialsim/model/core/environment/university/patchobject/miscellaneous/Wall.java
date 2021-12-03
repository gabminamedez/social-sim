package com.socialsim.model.core.environment.university.patchobject.miscellaneous;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.WallGraphic;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.miscellaneous.Obstacle;

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

        private WallBlock(UniversityPatch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class WallBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Wall.WallBlock create(UniversityPatch patch, boolean attractor, boolean hasGraphic) {
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