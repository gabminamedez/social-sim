package com.socialsim.controller.graphics.amenity.university.graphic;

import com.socialsim.controller.graphics.amenity.university.UniversityAmenityGraphic;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Security;

public class SecurityGraphic extends UniversityAmenityGraphic {

    private static final int ROW_SPAN = 2;
    private static final int COLUMN_SPAN = 1;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public SecurityGraphic(Security security) {
        super(security, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}