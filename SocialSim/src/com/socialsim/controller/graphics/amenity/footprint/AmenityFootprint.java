package com.socialsim.controller.graphics.amenity.footprint;

import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;

import java.util.ArrayList;
import java.util.List;

public class AmenityFootprint {

    private final List<Rotation> rotations; // Lists all available rotations of this amenity when being drawn
    private int rotationIndex; // The index of the current rotation

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

    public static class Rotation { // Denotes a rotation of an amenity when being drawn, along with its necessary properties
        private final Orientation orientation;
        private final List<AmenityBlockTemplate> amenityBlockTemplates; // Lists all the amenity block templates in this rotation view

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

        public boolean isHorizontal() {
            return this.orientation == Orientation.UP || this.orientation == Orientation.DOWN;
        }

        public boolean isVertical() {
            return this.orientation == Orientation.RIGHT || this.orientation == Orientation.LEFT;
        }

        public static class AmenityBlockTemplate {
            private final Orientation orientation;
            private final UniversityPatch.Offset offset;
            private final Class<? extends Amenity> amenityClass;
            private final boolean attractor;
            private final boolean hasGraphic;

            public AmenityBlockTemplate(Orientation orientation, int rowOffset, int columnOffset, Class<? extends Amenity> amenityClass, boolean attractor, boolean hasGraphic) {
                this.orientation = orientation;
                this.offset = new UniversityPatch.Offset(rowOffset, columnOffset);
                this.amenityClass = amenityClass;
                this.attractor = attractor;
                this.hasGraphic = hasGraphic;
            }

            public Orientation getOrientation() {
                return orientation;
            }

            public UniversityPatch.Offset getOffset() {
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
            public static List<Amenity.AmenityBlock> realizeAmenityBlockTemplates(UniversityPatch cursorPatch, List<AmenityBlockTemplate> amenityBlockTemplates) {
                return Amenity.AmenityBlock.convertToAmenityBlocks(cursorPatch, amenityBlockTemplates);
            }
        }

        public enum Orientation { // Denotes the possible orientations of an amenity being drawn
            UP, RIGHT, DOWN, LEFT
        }
    }

}