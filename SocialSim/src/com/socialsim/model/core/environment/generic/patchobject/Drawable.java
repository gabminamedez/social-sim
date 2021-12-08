package com.socialsim.model.core.environment.generic.patchobject;

import com.socialsim.controller.university.graphics.UniversityAmenityGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;

public interface Drawable {

    UniversityAmenityGraphic getGraphicObject();
    AmenityGraphicLocation getGraphicLocation();

}