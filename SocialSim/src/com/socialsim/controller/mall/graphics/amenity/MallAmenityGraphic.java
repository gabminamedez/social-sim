package com.socialsim.controller.mall.graphics.amenity;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MallAmenityGraphic extends Graphic {

    public static final String AMENITY_SPRITE_SHEET_URL = "com/socialsim/view/image/Mall/amenity_spritesheet.png";
    public static final String AMENITY_SPRITE_SHEET_URL2 = "com/socialsim/view/image/University/amenity_spritesheet.png";
    public static final HashMap<Class<?>, List<AmenityGraphicLocation>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        final List<AmenityGraphicLocation> benchGraphic = new ArrayList<>();
        benchGraphic.add(new AmenityGraphicLocation(3, 0));
        benchGraphic.add(new AmenityGraphicLocation(2, 2));
        AMENITY_GRAPHICS.put(Bench.class, benchGraphic);

        final List<AmenityGraphicLocation> digitalGraphic = new ArrayList<>();
        digitalGraphic.add(new AmenityGraphicLocation(0, 2));
        AMENITY_GRAPHICS.put(Digital.class, digitalGraphic);

        final List<AmenityGraphicLocation> kioskGraphic = new ArrayList<>();
        kioskGraphic.add(new AmenityGraphicLocation(4, 2));
        AMENITY_GRAPHICS.put(Kiosk.class, kioskGraphic);

        final List<AmenityGraphicLocation> mallGateGraphic = new ArrayList<>();
        mallGateGraphic.add(new AmenityGraphicLocation(0, 1));
        AMENITY_GRAPHICS.put(MallGate.class, mallGateGraphic);

        final List<AmenityGraphicLocation> plantGraphic = new ArrayList<>();
        plantGraphic.add(new AmenityGraphicLocation(0, 3));
        AMENITY_GRAPHICS.put(Plant.class, plantGraphic);

        final List<AmenityGraphicLocation> securityGraphic = new ArrayList<>();
        securityGraphic.add(new AmenityGraphicLocation(0, 0));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<AmenityGraphicLocation> storeCounterGraphic = new ArrayList<>();
        storeCounterGraphic.add(new AmenityGraphicLocation(4, 0));
        AMENITY_GRAPHICS.put(StoreCounter.class, storeCounterGraphic);

        final List<AmenityGraphicLocation> tableGraphic = new ArrayList<>();
        tableGraphic.add(new AmenityGraphicLocation(2, 0));
        tableGraphic.add(new AmenityGraphicLocation(2, 3));
        AMENITY_GRAPHICS.put(Table.class, tableGraphic);

        final List<AmenityGraphicLocation> trashGraphic = new ArrayList<>();
        trashGraphic.add(new AmenityGraphicLocation(1, 3));
        AMENITY_GRAPHICS.put(Trash.class, trashGraphic);

        final List<AmenityGraphicLocation> toiletGraphic = new ArrayList<>();
        toiletGraphic.add(new AmenityGraphicLocation(16, 0));
        AMENITY_GRAPHICS.put(Toilet.class, toiletGraphic);

        final List<AmenityGraphicLocation> sinkGraphic = new ArrayList<>();
        sinkGraphic.add(new AmenityGraphicLocation(16, 1));
        AMENITY_GRAPHICS.put(Sink.class, sinkGraphic);

        final List<AmenityGraphicLocation> storeAisleGraphic = new ArrayList<>();
        storeAisleGraphic.add(new AmenityGraphicLocation(13, 2));
        storeAisleGraphic.add(new AmenityGraphicLocation(14, 0));
        AMENITY_GRAPHICS.put(StoreAisle.class, storeAisleGraphic);

        final List<AmenityGraphicLocation> conciergeGraphic = new ArrayList<>();
        conciergeGraphic.add(new AmenityGraphicLocation(0, 2));
        AMENITY_GRAPHICS.put(Concierge.class, conciergeGraphic);

        final List<AmenityGraphicLocation> restoCounterGraphic = new ArrayList<>();
        restoCounterGraphic.add(new AmenityGraphicLocation(4, 0));
        AMENITY_GRAPHICS.put(RestoCounter.class, restoCounterGraphic);
    }

    protected final Amenity amenity;
    protected final List<AmenityGraphicLocation> graphics;
    protected int graphicIndex;

    private final MallAmenityGraphic.AmenityGraphicScale amenityGraphicScale;
    private final MallAmenityGraphic.AmenityGraphicOffset amenityGraphicOffset;

    public MallAmenityGraphic(Amenity amenity, int rowSpan, int columnSpan, int rowOffset, int columnOffset) {
        this.amenity = amenity;

        this.amenityGraphicScale = new MallAmenityGraphic.AmenityGraphicScale(rowSpan, columnSpan);
        this.amenityGraphicOffset = new MallAmenityGraphic.AmenityGraphicOffset(rowOffset, columnOffset);

        this.graphics = new ArrayList<>();

        for (AmenityGraphicLocation amenityGraphicLocation : AMENITY_GRAPHICS.get(amenity.getClass())) {
            AmenityGraphicLocation newAmenityGraphicLocation = new AmenityGraphicLocation(amenityGraphicLocation.getGraphicRow(), amenityGraphicLocation.getGraphicColumn());

            newAmenityGraphicLocation.setGraphicWidth(columnSpan);
            newAmenityGraphicLocation.setGraphicHeight(rowSpan);
            this.graphics.add(newAmenityGraphicLocation);
        }

        this.graphicIndex = 0;
    }

    public MallAmenityGraphic.AmenityGraphicScale getAmenityGraphicScale() {
        return amenityGraphicScale;
    }

    public MallAmenityGraphic.AmenityGraphicOffset getAmenityGraphicOffset() {
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