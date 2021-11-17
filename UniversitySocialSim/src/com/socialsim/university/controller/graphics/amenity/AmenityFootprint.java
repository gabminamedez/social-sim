package com.socialsim.university.controller.graphics.amenity;

import com.socialsim.university.model.core.environment.patch.Patch;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.university.model.core.environment.patch.position.MatrixPosition;

import java.util.ArrayList;
import java.util.List;

public class AmenityFootprint {

    private final List<Rotation> rotations;
    private int rotationIndex;

    public AmenityFootprint() {
        this.rotations = new ArrayList<>();
        this.rotationIndex = 0;
    }

    public void addRotation(Rotation rotation) {
        this.rotations.add(rotation);
    }

    public void rotateClockwise() {
        if (this.rotationIndex == rotations.size() - 1) {
            this.rotationIndex = 0;
        }
        else {
            this.rotationIndex++;
        }
    }

    public void rotateCounterclockwise() {
        if (this.rotationIndex == 0) {
            this.rotationIndex = rotations.size() - 1;
        }
        else {
            this.rotationIndex--;
        }
    }

    public Rotation getCurrentRotation() {
        return this.rotations.get(this.rotationIndex);
    }

    // Denotes a rotation of an amenity when being drawn, along with its necessary properties
    public static class Rotation {
        private final Orientation orientation;
        private final List<AmenityBlockTemplate> amenityBlockTemplates;

        public Rotation(Orientation orientation) {
            this.orientation = orientation;
            this.amenityBlockTemplates = new ArrayList<>();
        }

        public List<AmenityBlockTemplate> getAmenityBlockTemplates() {
            return amenityBlockTemplates;
        }

        public Orientation getOrientation() {
            return orientation;
        }

        public boolean isVertical() {
            return this.orientation == Orientation.UP || this.orientation == Orientation.DOWN;
        }

        public boolean isHorizontal() {
            return this.orientation == Orientation.RIGHT || this.orientation == Orientation.LEFT;
        }

        // Denotes the offset of a specific amenity block being drawn from the cursor
        public static class Offset {
            private final MatrixPosition offset;

            public Offset(int rowOffset, int columnOffset) {
                this.offset = new MatrixPosition(rowOffset, columnOffset);
            }

            public MatrixPosition getMatrixPosition() {
                return this.offset;
            }

            public int getRowOffset() {
                return this.offset.getRow();
            }

            public int getColumnOffset() {
                return this.offset.getColumn();
            }
        }

        public static class AmenityBlockTemplate {
            private final Orientation orientation;
            private final Offset offset;
            private final Class<? extends Amenity> amenityClass;
            private final boolean attractor;
            private final boolean hasGraphic;

            public AmenityBlockTemplate(Orientation orientation, int rowOffset, int columnOffset, Class<? extends Amenity> amenityClass, boolean attractor, boolean hasGraphic) {
                this.orientation = orientation;
                this.offset = new Offset(rowOffset, columnOffset);
                this.amenityClass = amenityClass;
                this.attractor = attractor;
                this.hasGraphic = hasGraphic;
            }

            public Orientation getOrientation() {
                return orientation;
            }

            public Offset getOffset() {
                return offset;
            }

            public Class<? extends Amenity> getAmenityClass() {
                return amenityClass;
            }

            public boolean isAttractor() {
                return attractor;
            }

            public boolean hasGraphic() {
                return hasGraphic;
            }

            // Convert the list of amenity block templates to a list of amenity blocks
            public static List<Amenity.AmenityBlock> realizeAmenityBlockTemplates(Patch cursorPatch, List<AmenityBlockTemplate> amenityBlockTemplates) {
                return Amenity.AmenityBlock.convertToAmenityBlocks(cursorPatch, amenityBlockTemplates);
            }
        }

        public enum Orientation {
            UP, RIGHT, DOWN, LEFT
        }
    }

}