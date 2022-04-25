package com.socialsim.controller.grocery.graphics.amenity;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroceryAmenityGraphic extends Graphic {

    public static final String AMENITY_SPRITE_SHEET_URL = "com/socialsim/view/image/Grocery/amenity_spritesheet.png";
    public static final String FOOD_AMENITY_SPRITE_URL = "com/socialsim/view/image/Grocery/food_amenity.png";
    public static final HashMap<Class<?>, List<AmenityGraphicLocation>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        final List<AmenityGraphicLocation> groceryGateGraphic = new ArrayList<>();
        groceryGateGraphic.add(new AmenityGraphicLocation(10, 2));
        AMENITY_GRAPHICS.put(GroceryGate.class, groceryGateGraphic);

        final List<AmenityGraphicLocation> cartRepoGraphic = new ArrayList<>();
        cartRepoGraphic.add(new AmenityGraphicLocation(0, 2));
        AMENITY_GRAPHICS.put(CartRepo.class, cartRepoGraphic);

        final List<AmenityGraphicLocation> cashierCounterGraphic = new ArrayList<>();
        cashierCounterGraphic.add(new AmenityGraphicLocation(0, 1));
        AMENITY_GRAPHICS.put(CashierCounter.class, cashierCounterGraphic);

        final List<AmenityGraphicLocation> freshProductsGraphic = new ArrayList<>();
        freshProductsGraphic.add(new AmenityGraphicLocation(4, 0));
        AMENITY_GRAPHICS.put(FreshProducts.class, freshProductsGraphic);

        final List<AmenityGraphicLocation> frozenProductsGraphic = new ArrayList<>();
        frozenProductsGraphic.add(new AmenityGraphicLocation(2, 2));
        AMENITY_GRAPHICS.put(FrozenProducts.class, frozenProductsGraphic);

        final List<AmenityGraphicLocation> frozenWallGraphic = new ArrayList<>();
        frozenWallGraphic.add(new AmenityGraphicLocation(8, 0));
        AMENITY_GRAPHICS.put(FrozenWall.class, frozenWallGraphic);

        final List<AmenityGraphicLocation> meatSectionGraphic = new ArrayList<>();
        meatSectionGraphic.add(new AmenityGraphicLocation(8, 2));
        AMENITY_GRAPHICS.put(MeatSection.class, meatSectionGraphic);

        final List<AmenityGraphicLocation> productAisleGraphic = new ArrayList<>();
        productAisleGraphic.add(new AmenityGraphicLocation(2, 0));
        AMENITY_GRAPHICS.put(ProductAisle.class, productAisleGraphic);

        final List<AmenityGraphicLocation> productShelfGraphic = new ArrayList<>();
        productShelfGraphic.add(new AmenityGraphicLocation(4, 2));
        AMENITY_GRAPHICS.put(ProductShelf.class, productShelfGraphic);

        final List<AmenityGraphicLocation> productWallGraphic = new ArrayList<>();
        productWallGraphic.add(new AmenityGraphicLocation(6, 0));
        productWallGraphic.add(new AmenityGraphicLocation(6, 2));
        AMENITY_GRAPHICS.put(ProductWall.class, productWallGraphic);

        final List<AmenityGraphicLocation> securityGraphic = new ArrayList<>();
        securityGraphic.add(new AmenityGraphicLocation(0, 0));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<AmenityGraphicLocation> serviceCounterGraphic = new ArrayList<>();
        serviceCounterGraphic.add(new AmenityGraphicLocation(11, 2));
        AMENITY_GRAPHICS.put(ServiceCounter.class, serviceCounterGraphic);

        final List<AmenityGraphicLocation> stallGraphic = new ArrayList<>();
        stallGraphic.add(new AmenityGraphicLocation(0, 0));
        AMENITY_GRAPHICS.put(Stall.class, stallGraphic);

        final List<AmenityGraphicLocation> tableGraphic = new ArrayList<>();
        tableGraphic.add(new AmenityGraphicLocation(10, 0));
        AMENITY_GRAPHICS.put(Table.class, tableGraphic);

        final List<AmenityGraphicLocation> sinkGraphic = new ArrayList<>();
        sinkGraphic.add(new AmenityGraphicLocation(12, 1));
        AMENITY_GRAPHICS.put(Sink.class, sinkGraphic);

        final List<AmenityGraphicLocation> toiletGraphic = new ArrayList<>();
        toiletGraphic.add(new AmenityGraphicLocation(12, 0));
        AMENITY_GRAPHICS.put(Toilet.class, toiletGraphic);
    }

    protected final Amenity amenity;
    protected final List<AmenityGraphicLocation> graphics;
    protected int graphicIndex;

    private final AmenityGraphicScale amenityGraphicScale;
    private final AmenityGraphicOffset amenityGraphicOffset;

    public GroceryAmenityGraphic(Amenity amenity, int rowSpan, int columnSpan, int rowOffset, int columnOffset) {
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