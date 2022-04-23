package com.socialsim.controller.grocery.graphics.amenity.graphic;

import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Toilet;

public class ToiletGraphic extends GroceryAmenityGraphic {

    private static final int ROW_SPAN = 1;
    private static final int COLUMN_SPAN = 1;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public ToiletGraphic(Toilet toilet) {
        super(toilet, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}