package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Plant.PlantBlock.plantBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Plant plantToAdd = Plant.PlantFactory.create(amenityBlocks, true);
            Main.officeSimulator.getOffice().getPlants().add(plantToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}