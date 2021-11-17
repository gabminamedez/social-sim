package com.socialsim.university.controller.graphics.agent;

import com.socialsim.university.controller.graphics.GraphicLocation;

public class StudentGraphicLocation extends GraphicLocation {

    public static final int BASE_IMAGE_UNIT = 64;

    public StudentGraphicLocation(int graphicRow, int graphicColumn) {
        super(graphicRow, graphicColumn);
    }

    public int getSourceY() {
        return  this.graphicRow * BASE_IMAGE_UNIT;
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