package com.socialsim.model.core.environment.generic.patchobject;

import com.socialsim.controller.graphics.amenity.university.UniversityAmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;

public interface Drawable {

    UniversityAmenityGraphic getGraphicObject();
    AmenityGraphicLocation getGraphicLocation();

}