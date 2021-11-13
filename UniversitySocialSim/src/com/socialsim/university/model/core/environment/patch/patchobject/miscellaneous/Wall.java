package com.socialsim.university.model.core.environment.patch.patchobject.miscellaneous;

import com.socialsim.university.model.core.environment.patch.Patch;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;

import java.util.List;

public class Wall extends Obstacle {

    private WallType wallType;
    public static final WallFactory wallFactory;
    private final WallGraphic wallGraphic;
    public static final AmenityFootprint wallFootprint;

    static {
        wallFactory = new WallFactory();

        wallFootprint = new AmenityFootprint();
        AmenityFootprint.Rotation upView = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        AmenityFootprint.Rotation.AmenityBlockTemplate block00
                = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                Wall.class,
                false,
                true
        );

        upView.getAmenityBlockTemplates().add(block00);
        wallFootprint.addRotation(upView);
    }

    protected Wall(List<AmenityBlock> amenityBlocks, WallType wallType) {
        super(amenityBlocks);

        this.wallType = wallType;
        this.wallGraphic = new WallGraphic(this);
    }

    public WallType getWallType() {
        return wallType;
    }

    public void setWallType(WallType wallType) {
        this.wallType = wallType;
    }

    @Override
    public String toString() {
        return this.wallType.toString();
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.wallGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.wallGraphic.getGraphicLocation();
    }

    public enum WallType {
        WALL("Wall"),
        BUILDING_COLUMN("Building column"),
        BELT_BARRIER("Belt barrier"),
        METAL_BARRIER("Metal barrier");

        private final String name;

        WallType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
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
            public Wall.WallBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new Wall.WallBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class WallFactory extends ObstacleFactory {
        public Wall create(List<AmenityBlock> amenityBlocks, WallType wallType) {
            return new Wall(amenityBlocks, wallType);
        }
    }

}