package com.socialsim.controller.graphics.amenity.University.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Door;

import java.util.ArrayList;
import java.util.List;

public class DoorMapper extends AmenityMapper {

    public static void draw(List<UniversityPatch> patches, String facing) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (UniversityPatch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Door.DoorBlock.doorBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            if(facing.equals("UP") || facing.equals("DOWN")) {
                UniversityPatch rightPatch = Main.simulator.getUniversity().getPatch(origPatchRow, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(rightPatch, true, false);
                amenityBlocks.add(amenityBlock2);
                rightPatch.setAmenityBlock(amenityBlock2);
            }
            else {
                UniversityPatch lowerPatch = Main.simulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
                amenityBlocks.add(amenityBlock2);
                lowerPatch.setAmenityBlock(amenityBlock2);
            }

            Door doorToAdd = Door.DoorFactory.create(amenityBlocks, true, facing);
            Main.simulator.getUniversity().getDoors().add(doorToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getUniversity().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}