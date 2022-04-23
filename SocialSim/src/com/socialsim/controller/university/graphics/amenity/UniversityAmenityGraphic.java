package com.socialsim.controller.university.graphics.amenity;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;
import com.socialsim.controller.generic.graphics.Graphic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class UniversityAmenityGraphic extends Graphic {

    public static final String AMENITY_SPRITE_SHEET_URL = "com/socialsim/view/image/University/amenity_spritesheet.png";
    public static final HashMap<Class<?>, List<AmenityGraphicLocation>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        final List<AmenityGraphicLocation> universityGateGraphic = new ArrayList<>();
        universityGateGraphic.add(new AmenityGraphicLocation(12, 2));
        AMENITY_GRAPHICS.put(UniversityGate.class, universityGateGraphic);

        final List<AmenityGraphicLocation> benchGraphic = new ArrayList<>();
        benchGraphic.add(new AmenityGraphicLocation(1, 2));
        benchGraphic.add(new AmenityGraphicLocation(2, 0));
        AMENITY_GRAPHICS.put(Bench.class, benchGraphic);

        final List<AmenityGraphicLocation> boardGraphic = new ArrayList<>();
        boardGraphic.add(new AmenityGraphicLocation(4, 0));
        boardGraphic.add(new AmenityGraphicLocation(4, 2));
        boardGraphic.add(new AmenityGraphicLocation(6, 0));
        boardGraphic.add(new AmenityGraphicLocation(6, 2));
        AMENITY_GRAPHICS.put(Board.class, boardGraphic);

        final List<AmenityGraphicLocation> bulletinGraphic = new ArrayList<>();
        bulletinGraphic.add(new AmenityGraphicLocation(8, 0));
        bulletinGraphic.add(new AmenityGraphicLocation(8, 2));
        bulletinGraphic.add(new AmenityGraphicLocation(10, 0));
        bulletinGraphic.add(new AmenityGraphicLocation(10, 2));
        AMENITY_GRAPHICS.put(Bulletin.class, bulletinGraphic);

        final List<AmenityGraphicLocation> chairGraphic = new ArrayList<>();
        chairGraphic.add(new AmenityGraphicLocation(0, 0));
        AMENITY_GRAPHICS.put(Chair.class, chairGraphic);

        final List<AmenityGraphicLocation> doorGraphic = new ArrayList<>();
        doorGraphic.add(new AmenityGraphicLocation(13, 2));
        doorGraphic.add(new AmenityGraphicLocation(14, 0));
        AMENITY_GRAPHICS.put(Door.class, doorGraphic);

        final List<AmenityGraphicLocation> eatTableGraphic = new ArrayList<>();
        eatTableGraphic.add(new AmenityGraphicLocation(14, 2));
        AMENITY_GRAPHICS.put(EatTable.class, eatTableGraphic);

        final List<AmenityGraphicLocation> fountainGraphic = new ArrayList<>();
        fountainGraphic.add(new AmenityGraphicLocation(1, 0));
        AMENITY_GRAPHICS.put(Fountain.class, fountainGraphic);

        final List<AmenityGraphicLocation> toiletGraphic = new ArrayList<>();
        toiletGraphic.add(new AmenityGraphicLocation(16, 0));
        AMENITY_GRAPHICS.put(Toilet.class, toiletGraphic);

        final List<AmenityGraphicLocation> sinkGraphic = new ArrayList<>();
        sinkGraphic.add(new AmenityGraphicLocation(16, 1));
        AMENITY_GRAPHICS.put(Sink.class, sinkGraphic);

        final List<AmenityGraphicLocation> labTableGraphic = new ArrayList<>();
        labTableGraphic.add(new AmenityGraphicLocation(12, 0));
        AMENITY_GRAPHICS.put(LabTable.class, labTableGraphic);

        final List<AmenityGraphicLocation> profTableGraphic = new ArrayList<>();
        profTableGraphic.add(new AmenityGraphicLocation(0, 2));
        profTableGraphic.add(new AmenityGraphicLocation(2, 1));
        AMENITY_GRAPHICS.put(ProfTable.class, profTableGraphic);

        final List<AmenityGraphicLocation> securityGraphic = new ArrayList<>();
        securityGraphic.add(new AmenityGraphicLocation(2, 2));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<AmenityGraphicLocation> staircaseGraphic = new ArrayList<>();
        staircaseGraphic.add(new AmenityGraphicLocation(2, 3));
        AMENITY_GRAPHICS.put(Staircase.class, staircaseGraphic);

        final List<AmenityGraphicLocation> stallGraphic = new ArrayList<>();
        stallGraphic.add(new AmenityGraphicLocation(15, 2));
        AMENITY_GRAPHICS.put(Stall.class, stallGraphic);

        final List<AmenityGraphicLocation> studyTableGraphic = new ArrayList<>();
        studyTableGraphic.add(new AmenityGraphicLocation(0, 2));
        studyTableGraphic.add(new AmenityGraphicLocation(2, 1));
        AMENITY_GRAPHICS.put(StudyTable.class, studyTableGraphic);

        final List<AmenityGraphicLocation> trashGraphic = new ArrayList<>();
        trashGraphic.add(new AmenityGraphicLocation(1, 1));
        AMENITY_GRAPHICS.put(Trash.class, trashGraphic);

        final List<AmenityGraphicLocation> officeTableGraphic = new ArrayList<>();
        officeTableGraphic.add(new AmenityGraphicLocation(0, 2));
        officeTableGraphic.add(new AmenityGraphicLocation(2, 1));
        AMENITY_GRAPHICS.put(OfficeTable.class, officeTableGraphic);

        final List<AmenityGraphicLocation> cabinetGraphic = new ArrayList<>();
        cabinetGraphic.add(new AmenityGraphicLocation(16, 2));
        AMENITY_GRAPHICS.put(Cabinet.class, cabinetGraphic);
    }

    protected final Amenity amenity;
    protected final List<AmenityGraphicLocation> graphics;
    protected int graphicIndex;

    private final AmenityGraphicScale amenityGraphicScale;
    private final AmenityGraphicOffset amenityGraphicOffset;

    public UniversityAmenityGraphic(Amenity amenity, int rowSpan, int columnSpan, int rowOffset, int columnOffset) {
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

    public static class AmenityGraphicScale {
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

    public static class AmenityGraphicOffset {
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