package com.socialsim.controller.graphics.amenity.university.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.AmenityMapper;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Trash;

import java.util.ArrayList;
import java.util.List;

public class TrashMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Trash.TrashBlock.trashBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Trash trashToAdd = Trash.TrashFactory.create(amenityBlocks, true);
            Main.simulator.getUniversity().getTrashes().add(trashToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}