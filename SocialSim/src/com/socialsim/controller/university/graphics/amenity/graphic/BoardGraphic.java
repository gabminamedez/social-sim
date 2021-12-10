package com.socialsim.controller.university.graphics.amenity.graphic;

import com.socialsim.controller.university.graphics.UniversityAmenityGraphic;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Board;

import java.util.Objects;

public class BoardGraphic extends UniversityAmenityGraphic {

    private static final int ROW_SPAN_VERTICAL = 2;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public BoardGraphic(Board board, String facing) {
        super(board,
                Objects.equals(facing, "RIGHT") || Objects.equals(facing, "LEFT") ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                Objects.equals(facing, "RIGHT") || Objects.equals(facing, "LEFT") ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL,
                NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);

        switch (facing) {
            case "UP" -> this.graphicIndex = 1;
            case "RIGHT" -> this.graphicIndex = 2;
            case "DOWN" -> this.graphicIndex = 0;
            case "LEFT" -> this.graphicIndex = 3;
        }
    }

}