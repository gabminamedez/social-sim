package com.socialsim.university.controller.graphics.amenity;

import com.socialsim.university.model.core.environment.patch.patchobject.passable.goal.Board;

public class BoardGraphic {

    private static final int ROW_SPAN = 1;
    private static final int COLUMN_SPAN = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public BoardGraphic(Board board) {
        super(board, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}