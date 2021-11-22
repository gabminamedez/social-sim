package com.socialsim.controller.graphics;

import com.socialsim.model.core.environment.Environment;

public abstract class GraphicLocation implements Environment {

    protected final int graphicRow;
    protected final int graphicColumn;
    protected int graphicWidth;
    protected int graphicHeight;

    public GraphicLocation(int graphicRow, int graphicColumn) {
        this.graphicRow = graphicRow;
        this.graphicColumn = graphicColumn;
    }

    public int getGraphicRow() {
        return graphicRow;
    }

    public int getGraphicColumn() {
        return graphicColumn;
    }

    public void setGraphicWidth(int graphicWidth) {
        this.graphicWidth = graphicWidth;
    }

    public void setGraphicHeight(int graphicHeight) {
        this.graphicHeight = graphicHeight;
    }

    public abstract int getSourceY();

    public abstract int getSourceX();

    public abstract int getSourceWidth();

    public abstract int getSourceHeight();

}