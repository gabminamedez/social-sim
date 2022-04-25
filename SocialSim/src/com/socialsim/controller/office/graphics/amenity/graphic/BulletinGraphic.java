package com.socialsim.controller.office.graphics.amenity.graphic;

import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Bulletin;

public class BulletinGraphic extends OfficeAmenityGraphic {

    private static final int ROW_SPAN = 1;
    private static final int COLUMN_SPAN = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public BulletinGraphic(Bulletin bulletin) {
        super(bulletin, ROW_SPAN, COLUMN_SPAN, NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);
    }

}