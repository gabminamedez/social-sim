package com.socialsim.controller.generic.graphics.amenity;

import com.socialsim.controller.generic.graphics.GraphicLocation;

public class AmenityGraphicLocation extends GraphicLocation {

    public static final int BASE_IMAGE_UNIT = 200;

    public AmenityGraphicLocation(int graphicRow, int graphicColumn) {
        super(graphicRow, graphicColumn);
    }

    public int getSourceY() {
        return this.graphicRow * BASE_IMAGE_UNIT;
    }

    public int getSourceX() {
        return  this.graphicColumn * BASE_IMAGE_UNIT;
    }

    public int getSourceWidth() {
        return  this.graphicWidth * BASE_IMAGE_UNIT;
    }

    public int getSourceHeight() {
        return  this.graphicHeight * BASE_IMAGE_UNIT;
    }

}