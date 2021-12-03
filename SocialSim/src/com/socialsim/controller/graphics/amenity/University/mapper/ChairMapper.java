package com.socialsim.controller.graphics.amenity.University.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Chair;

import java.util.ArrayList;
import java.util.List;

public class ChairMapper extends AmenityMapper {

    public static void draw(List<UniversityPatch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (UniversityPatch patch : patches) {
            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Chair.ChairBlock.chairBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Chair chairToAdd = Chair.ChairFactory.create(amenityBlocks, true);
            Main.simulator.getUniversity().getChairs().add(chairToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getUniversity().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}