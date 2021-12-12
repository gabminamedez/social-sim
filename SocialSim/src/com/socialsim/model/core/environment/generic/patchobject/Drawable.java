package com.socialsim.model.core.environment.generic.patchobject;

import com.socialsim.controller.generic.graphics.Graphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;

public interface Drawable {

    Graphic getGraphicObject();
    AmenityGraphicLocation getGraphicLocation();

}