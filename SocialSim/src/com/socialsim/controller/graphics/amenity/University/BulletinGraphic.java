package com.socialsim.controller.graphics.amenity.University;

import com.socialsim.controller.graphics.GraphicsController;
import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.footprint.AmenityFootprint;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Bulletin;

public class BulletinGraphic extends AmenityGraphic {

    private static final int ROW_SPAN_VERTICAL = 2;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public BulletinGraphic(Bulletin bulletin) {
        super(bulletin,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical() ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical() ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL,
                NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);

        AmenityFootprint.Rotation.Orientation orientation = GraphicsController.currentAmenityFootprint.getCurrentRotation().getOrientation();

        switch (orientation) {
            case UP: this.graphicIndex = 3; break;
            case RIGHT: this.graphicIndex = 1; break;
            case DOWN: this.graphicIndex = 0; break;
            case LEFT: this.graphicIndex = 2; break;
        }
    }

}