package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Toilet;

import java.util.ArrayList;
import java.util.List;

public class ToiletMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Toilet.ToiletBlock.toiletBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Toilet toiletToAdd = Toilet.ToiletFactory.create(amenityBlocks, true);
            Main.universitySimulator.getUniversity().getToilets().add(toiletToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}