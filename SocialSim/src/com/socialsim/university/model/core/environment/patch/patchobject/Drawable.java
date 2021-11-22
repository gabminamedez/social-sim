package com.socialsim.university.model.core.environment.patch.patchobject;

import com.socialsim.university.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.university.controller.graphics.amenity.AmenityGraphicLocation;

public interface Drawable {

    AmenityGraphic getGraphicObject();
    AmenityGraphicLocation getGraphicLocation();

}