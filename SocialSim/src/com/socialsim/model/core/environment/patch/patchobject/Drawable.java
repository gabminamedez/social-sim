package com.socialsim.model.core.environment.patch.patchobject;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;

public interface Drawable {

    AmenityGraphic getGraphicObject();
    AmenityGraphicLocation getGraphicLocation();

}