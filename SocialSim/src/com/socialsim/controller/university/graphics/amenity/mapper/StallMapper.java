package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Stall;

import java.util.ArrayList;
import java.util.List;

public class StallMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Stall.StallBlock.stallBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch rightPatch = Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(rightPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            rightPatch.setAmenityBlock(amenityBlock2);

            Stall stallToAdd = Stall.StallFactory.create(amenityBlocks, true);
            Main.universitySimulator.getUniversity().getStalls().add(stallToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}