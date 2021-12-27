package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Trash;

import java.util.ArrayList;
import java.util.List;

public class TrashMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Trash.TrashBlock.trashBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Trash trashToAdd = Trash.TrashFactory.create(amenityBlocks, true);
            Main.universitySimulator.getUniversity().getTrashes().add(trashToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}