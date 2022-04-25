package com.socialsim.controller.mall.graphics.amenity.graphic;

import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Concierge;

public class ConciergeGraphic extends MallAmenityGraphic {

    private static final int ROW_SPAN = 2;
    private static final int COLUMN_SPAN = 1;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public ConciergeGraphic(Concierge digital) {
        super(digital, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}