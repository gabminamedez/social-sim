package com.socialsim.university.controller.graphics.amenity;

import com.socialsim.university.model.core.environment.patch.patchobject.passable.gate.UniversityGate;

public class UniversityGateGraphic extends AmenityGraphic {

    private static final int ROW_SPAN = 2;
    private static final int COLUMN_SPAN = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public UniversityGateGraphic(UniversityGate universityGate) {
        super(universityGate, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}