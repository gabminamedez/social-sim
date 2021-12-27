package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.CartRepo;

import java.util.ArrayList;
import java.util.List;

public class CartRepoMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = CartRepo.CartRepoBlock.cartRepoBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            for (int i = 1; i < 4; i++) {
                Patch patchBack = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol);
                Amenity.AmenityBlock amenityBlockBack = null;
                if (i % 2 == 0) {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, false, true);
                }
                else {
                    if (i == 3) {
                        amenityBlockBack = amenityBlockFactory.create(patchBack, true, false);
                    }
                    else {
                        amenityBlockBack = amenityBlockFactory.create(patchBack, false, false);
                    }
                }
                amenityBlocks.add(amenityBlockBack);
                patchBack.setAmenityBlock(amenityBlockBack);
            }

            for (int i = 0; i < 4; i++) {
                Patch patchFront = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlockFront = null;
                if (i == 3) {
                    amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                }
                else {
                    amenityBlockFront = amenityBlockFactory.create(patchFront, false, false);
                }
                amenityBlocks.add(amenityBlockFront);
                patchFront.setAmenityBlock(amenityBlockFront);
            }

            CartRepo cartRepoToAdd = CartRepo.CartRepoFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getCartRepos().add(cartRepoToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}