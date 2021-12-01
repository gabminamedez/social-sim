package com.socialsim.controller.graphics.amenity.University;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Staircase;

public class StaircaseGraphic extends AmenityGraphic {

    private static final int ROW_SPAN = 2;
    private static final int COLUMN_SPAN = 1;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public StaircaseGraphic(Staircase staircase) {
        super(staircase, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}