package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.ProductWall;

import java.util.ArrayList;
import java.util.List;

public class ProductWallMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, String facing) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = ProductWall.ProductWallBlock.productWallBlockFactory;
            Amenity.AmenityBlock amenityBlock = null;
            if (facing.equals("DOWN")) {
                amenityBlock = amenityBlockFactory.create(patch, false, true);
            }
            else {
                amenityBlock = amenityBlockFactory.create(patch, true, true);
            }
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            if (facing.equals("DOWN")) { // Horizontal
                for (int i = 1; i < 8; i++) {
                    Patch patchBack = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + i);
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
                    Patch patchFront = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + i);
                    Amenity.AmenityBlock amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                    amenityBlocks.add(amenityBlockFront);
                    patchFront.setAmenityBlock(amenityBlockFront);
                }
            }
            else {
                for (int i = 1; i < 8; i++) {
                    Patch patchFront = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol);
                    Amenity.AmenityBlock amenityBlockFront = null;
                    if (i % 2 == 0) {
                        amenityBlockFront = amenityBlockFactory.create(patchFront, true, true);
                    }
                    else {
                        amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                    }
                    amenityBlocks.add(amenityBlockFront);
                    patchFront.setAmenityBlock(amenityBlockFront);
                }

                for (int i = 0; i < 8; i++) {
                    Patch patchBack = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol + 1);
                    Amenity.AmenityBlock amenityBlockBack = null;
                    amenityBlockBack = amenityBlockFactory.create(patchBack, false, false);
                    amenityBlocks.add(amenityBlockBack);
                    patchBack.setAmenityBlock(amenityBlockBack);
                }
            }

            ProductWall productWallToAdd = ProductWall.ProductWallFactory.create(amenityBlocks, true, facing);
            Main.grocerySimulator.getGrocery().getProductWalls().add(productWallToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}