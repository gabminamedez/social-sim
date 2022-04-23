package com.socialsim.controller.university.graphics.amenity.graphic;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Cabinet;

public class CabinetGraphic extends UniversityAmenityGraphic {

    private static final int ROW_SPAN = 2;
    private static final int COLUMN_SPAN = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public CabinetGraphic(Cabinet cabinet, String facing) {
        super(cabinet, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);

        this.graphicIndex = 0;
    }

}