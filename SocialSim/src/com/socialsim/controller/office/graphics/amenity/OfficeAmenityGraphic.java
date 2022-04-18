package com.socialsim.controller.office.graphics.amenity;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfficeAmenityGraphic extends Graphic {

    public static final String AMENITY_SPRITE_SHEET_URL = "com/socialsim/view/image/Office/amenity_spritesheet.png";
    public static final String AMENITY_SPRITE_SHEET_URL2 = "com/socialsim/view/image/University/amenity_spritesheet.png";
    public static final HashMap<Class<?>, List<AmenityGraphicLocation>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        final List<AmenityGraphicLocation> chairGraphic = new ArrayList<>();
        chairGraphic.add(new AmenityGraphicLocation(0, 0));
        AMENITY_GRAPHICS.put(Chair.class, chairGraphic);

        final List<AmenityGraphicLocation> cabinetGraphic = new ArrayList<>();
        cabinetGraphic.add(new AmenityGraphicLocation(10, 0));
        cabinetGraphic.add(new AmenityGraphicLocation(8, 2));
        AMENITY_GRAPHICS.put(Cabinet.class, cabinetGraphic);

        final List<AmenityGraphicLocation> collabDeskGraphic = new ArrayList<>();
        collabDeskGraphic.add(new AmenityGraphicLocation(6, 2));
        AMENITY_GRAPHICS.put(CollabDesk.class, collabDeskGraphic);

        final List<AmenityGraphicLocation> couchGraphic = new ArrayList<>();
        couchGraphic.add(new AmenityGraphicLocation(4, 0));
        couchGraphic.add(new AmenityGraphicLocation(4, 2));
        AMENITY_GRAPHICS.put(Couch.class, couchGraphic);

        final List<AmenityGraphicLocation> cubicleGraphic = new ArrayList<>();
        cubicleGraphic.add(new AmenityGraphicLocation(2, 0));
        cubicleGraphic.add(new AmenityGraphicLocation(2, 2));
        AMENITY_GRAPHICS.put(Cubicle.class, cubicleGraphic);

        final List<AmenityGraphicLocation> doorGraphic = new ArrayList<>();
        doorGraphic.add(new AmenityGraphicLocation(9, 0));
        doorGraphic.add(new AmenityGraphicLocation(0, 3));
        AMENITY_GRAPHICS.put(Door.class, doorGraphic);

        final List<AmenityGraphicLocation> meetingDeskGraphic = new ArrayList<>();
        meetingDeskGraphic.add(new AmenityGraphicLocation(6, 2));
        AMENITY_GRAPHICS.put(MeetingDesk.class, meetingDeskGraphic);

        final List<AmenityGraphicLocation> officeGateGraphic = new ArrayList<>();
        officeGateGraphic.add(new AmenityGraphicLocation(8, 0));
        AMENITY_GRAPHICS.put(OfficeGate.class, officeGateGraphic);

        final List<AmenityGraphicLocation> officeDeskGraphic = new ArrayList<>();
        officeDeskGraphic.add(new AmenityGraphicLocation(12, 0));
        AMENITY_GRAPHICS.put(OfficeDesk.class, officeDeskGraphic);

        final List<AmenityGraphicLocation> plantGraphic = new ArrayList<>();
        plantGraphic.add(new AmenityGraphicLocation(0, 1));
        AMENITY_GRAPHICS.put(Plant.class, plantGraphic);

        final List<AmenityGraphicLocation> printerGraphic = new ArrayList<>();
        printerGraphic.add(new AmenityGraphicLocation(1, 0));
        AMENITY_GRAPHICS.put(Printer.class, printerGraphic);

        final List<AmenityGraphicLocation> receptionTable = new ArrayList<>();
        receptionTable.add(new AmenityGraphicLocation(7, 0));
        AMENITY_GRAPHICS.put(ReceptionTable.class, receptionTable);

        final List<AmenityGraphicLocation> securityGraphic = new ArrayList<>();
        securityGraphic.add(new AmenityGraphicLocation(0, 2));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<AmenityGraphicLocation> tableGraphic = new ArrayList<>();
        tableGraphic.add(new AmenityGraphicLocation(6, 0));
        tableGraphic.add(new AmenityGraphicLocation(10, 3));
        AMENITY_GRAPHICS.put(Table.class, tableGraphic);

        final List<AmenityGraphicLocation> sinkGraphic = new ArrayList<>();
        sinkGraphic.add(new AmenityGraphicLocation(16, 1));
        AMENITY_GRAPHICS.put(Sink.class, sinkGraphic);

        final List<AmenityGraphicLocation> toiletGraphic = new ArrayList<>();
        toiletGraphic.add(new AmenityGraphicLocation(16, 0));
        AMENITY_GRAPHICS.put(Toilet.class, toiletGraphic);

        final List<AmenityGraphicLocation> bulletinGraphic = new ArrayList<>();
        bulletinGraphic.add(new AmenityGraphicLocation(13, 0));
        AMENITY_GRAPHICS.put(Bulletin.class, bulletinGraphic);

        final List<AmenityGraphicLocation> whiteboardGraphic = new ArrayList<>();
        whiteboardGraphic.add(new AmenityGraphicLocation(10, 2));
        AMENITY_GRAPHICS.put(Whiteboard.class, whiteboardGraphic);

        final List<AmenityGraphicLocation> waterDispenserGraphic = new ArrayList<>();
        waterDispenserGraphic.add(new AmenityGraphicLocation(12, 2));
        AMENITY_GRAPHICS.put(WaterDispenser.class, waterDispenserGraphic);

        final List<AmenityGraphicLocation> fridgeGraphic = new ArrayList<>();
        fridgeGraphic.add(new AmenityGraphicLocation(12, 3));
        AMENITY_GRAPHICS.put(Fridge.class, fridgeGraphic);
    }

    protected final Amenity amenity;
    protected final List<AmenityGraphicLocation> graphics;
    protected int graphicIndex;

    private final OfficeAmenityGraphic.AmenityGraphicScale amenityGraphicScale;
    private final OfficeAmenityGraphic.AmenityGraphicOffset amenityGraphicOffset;

    public OfficeAmenityGraphic(Amenity amenity, int rowSpan, int columnSpan, int rowOffset, int columnOffset) {
        this.amenity = amenity;

        this.amenityGraphicScale = new OfficeAmenityGraphic.AmenityGraphicScale(rowSpan, columnSpan);
        this.amenityGraphicOffset = new OfficeAmenityGraphic.AmenityGraphicOffset(rowOffset, columnOffset);

        this.graphics = new ArrayList<>();

        for (AmenityGraphicLocation amenityGraphicLocation : AMENITY_GRAPHICS.get(amenity.getClass())) {
            AmenityGraphicLocation newAmenityGraphicLocation = new AmenityGraphicLocation(amenityGraphicLocation.getGraphicRow(), amenityGraphicLocation.getGraphicColumn());

            newAmenityGraphicLocation.setGraphicWidth(columnSpan);
            newAmenityGraphicLocation.setGraphicHeight(rowSpan);
            this.graphics.add(newAmenityGraphicLocation);
        }

        this.graphicIndex = 0;
    }

    public OfficeAmenityGraphic.AmenityGraphicScale getAmenityGraphicScale() {
        return amenityGraphicScale;
    }

    public OfficeAmenityGraphic.AmenityGraphicOffset getAmenityGraphicOffset() {
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