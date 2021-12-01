package com.socialsim.controller.graphics.amenity.University;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Trash;

public class TrashGraphic extends AmenityGraphic {

    private static final int ROW_SPAN = 1;
    private static final int COLUMN_SPAN = 1;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public TrashGraphic(Trash trash) {
        super(trash, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}