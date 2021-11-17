package com.socialsim.university.model.core.environment.patch.patchobject;

import com.socialsim.university.controller.Main;
import com.socialsim.university.controller.graphics.amenity.AmenityFootprint;
import com.socialsim.university.model.core.environment.BaseUniversityObject;
import com.socialsim.university.model.core.environment.Environment;
import com.socialsim.university.model.core.environment.patch.Patch;
import com.socialsim.university.model.core.environment.patch.patchobject.miscellaneous.Wall;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Board;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Chair;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Fountain;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.blockable.Security;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.portal.Door;
import com.socialsim.university.model.core.environment.patch.position.MatrixPosition;

import java.util.ArrayList;
import java.util.List;

public abstract class Amenity extends PatchObject implements Environment {

    private final List<AmenityBlock> amenityBlocks;
    private final List<AmenityBlock> attractors;

    protected Amenity(List<AmenityBlock> amenityBlocks) {
        this.amenityBlocks = amenityBlocks;

        if (this.amenityBlocks != null) {
            this.attractors = new ArrayList<>();

            for (AmenityBlock amenityBlock : this.amenityBlocks) {
                amenityBlock.setParent(this);
                amenityBlock.getPatch().setAmenityBlock(amenityBlock);

                if (amenityBlock.isAttractor()) {
                    this.attractors.add(amenityBlock);
                }
            }
        }
        else {
            this.attractors = null;
        }
    }

    public List<AmenityBlock> getAmenityBlocks() {
        return amenityBlocks;
    }

    public List<AmenityBlock> getAttractors() {
        return attractors;
    }

    public abstract static class AmenityBlock implements Environment {
        private Amenity parent;
        private final Patch patch;
        private final boolean attractor;
        private final boolean hasGraphic;

        protected AmenityBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            this.patch = patch;
            this.attractor = attractor;
            this.hasGraphic = hasGraphic;
        }

        public Amenity getParent() {
            return parent;
        }

        public void setParent(Amenity parent) {
            this.parent = parent;
        }

        public Patch getPatch() {
            return patch;
        }

        public boolean isAttractor() {
            return attractor;
        }

        public boolean hasGraphic() {
            return hasGraphic;
        }

        public static List<AmenityBlock> convertToAmenityBlocks(Patch referencePatch, List<AmenityFootprint.Rotation.AmenityBlockTemplate> amenityBlockTemplates) {
            List<AmenityBlock> amenityBlocks = new ArrayList<>();

            for (AmenityFootprint.Rotation.AmenityBlockTemplate amenityBlockTemplate : amenityBlockTemplates) {
                // Compute for the position of the patch using the offset data
                int row = referencePatch.getMatrixPosition().getRow() + amenityBlockTemplate.getOffset().getRowOffset();
                int column = referencePatch.getMatrixPosition().getColumn() + amenityBlockTemplate.getOffset().getColumnOffset();

                MatrixPosition patchPosition = new MatrixPosition(row, column);

                if (!MatrixPosition.inBounds(patchPosition, Main.simulator.getStation())) {
                    return null;
                }

                Patch patch = Main.simulator.getCurrentFloor().getPatch(row, column);

                assert getAmenityBlockFactory(amenityBlockTemplate.getAmenityClass()) != null;

                AmenityBlock amenityBlock = getAmenityBlockFactory(amenityBlockTemplate.getAmenityClass()).create(
                        patch,
                        amenityBlockTemplate.isAttractor(),
                        amenityBlockTemplate.hasGraphic(),
                        amenityBlockTemplate.getOrientation()
                );

                amenityBlocks.add(amenityBlock);
            }

            return amenityBlocks;
        }

        private static AmenityBlockFactory getAmenityBlockFactory(Class<? extends Amenity> amenityClass) {
            if (amenityClass == Wall.class) {
                return Wall.WallBlock.wallBlockFactory;
            }
            else if (amenityClass == Security.class) {
                return Security.SecurityBlock.securityBlockFactory;
            }
            else if (amenityClass == Board.class) {
                return Board.BoardBlock.boardBlockFactory;
            }
            else if (amenityClass == Chair.class) {
                return Chair.ChairBlock.chairBlockFactory;
            }
            else if (amenityClass == Fountain.class) {
                return Fountain.FountainBlock.fountainBlockFactory;
            }
            else if (amenityClass == Door.class) {
                return Door.DoorBlock.doorBlockFactory;
            }
            else {
                return null;
            }
        }

        public abstract static class AmenityBlockFactory extends BaseUniversityObject.UniversityObjectFactory {
            public abstract AmenityBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            );
        }
    }

    public abstract static class AmenityFactory extends BaseUniversityObject.UniversityObjectFactory {
    }

}