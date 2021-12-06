package com.socialsim.controller.graphics.amenity.University.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Board;

import java.util.ArrayList;
import java.util.List;

public class BoardMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, String facing) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Board.BoardBlock.boardBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            if(facing.equals("UP") || facing.equals("DOWN")) {
                Patch rightPatch = Main.simulator.getUniversity().getPatch(origPatchRow, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(rightPatch, true, false);
                amenityBlocks.add(amenityBlock2);
                rightPatch.setAmenityBlock(amenityBlock2);
            }
            else {
                Patch lowerPatch = Main.simulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
                amenityBlocks.add(amenityBlock2);
                lowerPatch.setAmenityBlock(amenityBlock2);
            }

            Board boardToAdd = Board.BoardFactory.create(amenityBlocks, true, facing);
            Main.simulator.getUniversity().getBoards().add(boardToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}