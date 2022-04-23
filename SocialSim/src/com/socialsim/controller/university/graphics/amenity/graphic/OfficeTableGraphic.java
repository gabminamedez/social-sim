package com.socialsim.controller.university.graphics.amenity.graphic;

import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.OfficeTable;

import java.util.Objects;

public class OfficeTableGraphic extends UniversityAmenityGraphic {

    private static final int ROW_SPAN_VERTICAL = 2;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public OfficeTableGraphic(OfficeTable officeTable, String facing) {
        super(officeTable,
                Objects.equals(facing, "RIGHT") || Objects.equals(facing, "LEFT") ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                Objects.equals(facing, "RIGHT") || Objects.equals(facing, "LEFT") ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL,
                NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);

        switch (facing) {
            case "UP", "DOWN" -> this.graphicIndex = 0;
            case "RIGHT", "LEFT" -> this.graphicIndex = 1;
        }
    }

}