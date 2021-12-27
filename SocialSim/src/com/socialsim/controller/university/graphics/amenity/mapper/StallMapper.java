package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.university.patchfield.SecurityField;
import com.socialsim.model.core.environment.university.patchfield.StallField;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Stall;

import java.util.ArrayList;
import java.util.List;

public class StallMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        int iter = 1;

        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
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

            Stall stallToAdd = Stall.StallFactory.create(amenityBlocks, true, 20);
            Main.universitySimulator.getUniversity().getStalls().add(stallToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> stallFieldPatches = new ArrayList<>();
            stallFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 1));
            stallFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol + 1));
            stallFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol + 2));
            for (int i = 1; i < 21; i++) {
                stallFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + i, origPatchCol + 3));
            }
            Main.universitySimulator.getUniversity().getStallFields().add(StallField.stallFieldFactory.create(stallFieldPatches, stallToAdd, iter));
            iter += 1;
        }
    }

}