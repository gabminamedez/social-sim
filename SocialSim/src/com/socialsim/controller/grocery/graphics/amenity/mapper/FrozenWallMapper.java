package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.FrozenWall;

import java.util.ArrayList;
import java.util.List;

public class FrozenWallMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = FrozenWall.FrozenWallBlock.frozenWallBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            for (int i = 1; i < 8; i++) {
                Patch patchBack = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol);
                Amenity.AmenityBlock amenityBlockBack = null;
                if (i % 2 == 0) {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, false, true);
                }
                else {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, false, false);
                }
                amenityBlocks.add(amenityBlockBack);
                patchBack.setAmenityBlock(amenityBlockBack);
            }

            for (int i = 0; i < 8; i++) {
                Patch patchFront = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                amenityBlocks.add(amenityBlockFront);
                patchFront.setAmenityBlock(amenityBlockFront);
            }

            FrozenWall frozenWallToAdd = FrozenWall.FrozenWallFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getFrozenWalls().add(frozenWallToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}