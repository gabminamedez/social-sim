package com.socialsim.university.controller.graphics.amenity;

import com.socialsim.university.controller.graphics.Graphic;
import com.socialsim.university.model.core.environment.Environment;
import com.socialsim.university.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.university.model.core.environment.patch.patchobject.miscellaneous.Wall;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Board;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Chair;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Fountain;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.blockable.Security;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.portal.Door;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AmenityGraphic extends Graphic {

    public static final String AMENITY_SPRITE_SHEET_URL = "com/crowdsimulation/view/image/amenity_spritesheet.png";
    public static final HashMap<Class<?>, List<AmenityGraphicLocation>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        final List<AmenityGraphicLocation> boardGraphic = new ArrayList<>();
        boardGraphic.add(new AmenityGraphicLocation(6, 0));
        AMENITY_GRAPHICS.put(Board.class, boardGraphic);

        final List<AmenityGraphicLocation> chairGraphic = new ArrayList<>();
        chairGraphic.add(new AmenityGraphicLocation(0, 0));
        AMENITY_GRAPHICS.put(Chair.class, chairGraphic);

        final List<AmenityGraphicLocation> doorGraphic = new ArrayList<>();
        doorGraphic.add(new AmenityGraphicLocation(2, 0));
        doorGraphic.add(new AmenityGraphicLocation(2, 1));
        AMENITY_GRAPHICS.put(Door.class, doorGraphic);

        final List<AmenityGraphicLocation> fountainGraphic = new ArrayList<>();
        fountainGraphic.add(new AmenityGraphicLocation(1, 0));
        AMENITY_GRAPHICS.put(Fountain.class, fountainGraphic);

        final List<AmenityGraphicLocation> securityGraphic = new ArrayList<>();
        securityGraphic.add(new AmenityGraphicLocation(4, 0));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<AmenityGraphicLocation> wallGraphic = new ArrayList<>();
        wallGraphic.add(new AmenityGraphicLocation(0, 1));
        AMENITY_GRAPHICS.put(Wall.class, wallGraphic);
    }

    protected final Amenity amenity;
    protected final List<AmenityGraphicLocation> graphics;
    protected int graphicIndex;

    private final AmenityGraphicScale amenityGraphicScale; // Denotes the rows and columns spanned by this graphic
    private final AmenityGraphicOffset amenityGraphicOffset; // Denotes the offset of this graphic

    public AmenityGraphic(Amenity amenity, int rowSpan, int columnSpan, int rowOffset, int columnOffset) {
        this.amenity = amenity;

        this.amenityGraphicScale = new AmenityGraphicScale(rowSpan, columnSpan);
        this.amenityGraphicOffset = new AmenityGraphicOffset(rowOffset, columnOffset);

        this.graphics = new ArrayList<>();

        for (AmenityGraphicLocation amenityGraphicLocation : AMENITY_GRAPHICS.get(amenity.getClass())) {
            AmenityGraphicLocation newAmenityGraphicLocation = new AmenityGraphicLocation(amenityGraphicLocation.getGraphicRow(), amenityGraphicLocation.getGraphicColumn());

            newAmenityGraphicLocation.setGraphicWidth(columnSpan);
            newAmenityGraphicLocation.setGraphicHeight(rowSpan);
            this.graphics.add(newAmenityGraphicLocation);
        }

        this.graphicIndex = 0;
    }

    public AmenityGraphicScale getAmenityGraphicScale() {
        return amenityGraphicScale;
    }

    public AmenityGraphicOffset getAmenityGraphicOffset() {
        return amenityGraphicOffset;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public AmenityGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    public static class AmenityGraphicScale implements Environment {
        private int rowSpan;
        private int columnSpan;

        public AmenityGraphicScale(int rowSpan, int columnSpan) {
            this.rowSpan = rowSpan;
            this.columnSpan = columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public void setColumnSpan(int columnSpan) {
            this.columnSpan = columnSpan;
        }
    }

    public static class AmenityGraphicOffset implements Environment {
        private int rowOffset;
        private int columnOffset;

        public AmenityGraphicOffset(int rowOffset, int columnOffset) {
            this.rowOffset = rowOffset;
            this.columnOffset = columnOffset;
        }

        public int getRowOffset() {
            return rowOffset;
        }

        public void setRowOffset(int rowOffset) {
            this.rowOffset = rowOffset;
        }

        public int getColumnOffset() {
            return columnOffset;
        }

        public void setColumnOffset(int columnOffset) {
            this.columnOffset = columnOffset;
        }
    }

}