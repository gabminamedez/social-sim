package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchfield.FountainField;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Fountain;
import com.socialsim.model.simulator.university.UniversitySimulator;

import java.util.ArrayList;
import java.util.List;

public class FountainMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Fountain.FountainBlock.fountainBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Fountain fountainToAdd = Fountain.FountainFactory.create(amenityBlocks, true, 20);
            Main.universitySimulator.getUniversity().getFountains().add(fountainToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> fountainFieldPatches = new ArrayList<>();
            if (origPatchCol == 62){
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 1, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 1));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 2));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 3));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 4));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 5));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 6));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 7));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow - 2, origPatchCol + 8));
            }
            else{
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 2, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 3, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 4, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 5, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 6, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 7, origPatchCol));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 7, origPatchCol - 1));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 7, origPatchCol - 2));
                fountainFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 7, origPatchCol - 3));
            }

            Main.universitySimulator.getUniversity().getFountainFields().add(FountainField.fountainFieldFactory.create(fountainFieldPatches, fountainToAdd, 1));
        }
    }

}