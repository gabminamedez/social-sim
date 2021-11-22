package com.socialsim.controller.graphics.agent;

import com.socialsim.controller.graphics.GraphicLocation;

public class AgentGraphicLocation extends GraphicLocation {

    public static final int BASE_IMAGE_UNIT = 64;

    public AgentGraphicLocation(int graphicRow, int graphicColumn) {
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