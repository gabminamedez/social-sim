package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.FreshProducts;

import java.util.ArrayList;
import java.util.List;

public class FreshProductsMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = FreshProducts.FreshProductsBlock.freshProductsBlockFactory;
            Amenity.AmenityBlock amenityBlockLeft0 = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlockLeft0);
            patch.setAmenityBlock(amenityBlockLeft0);

            Patch patchLeft1 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockLeft1 = amenityBlockFactory.create(patchLeft1, true, false);
            amenityBlocks.add(amenityBlockLeft1);
            patchLeft1.setAmenityBlock(amenityBlockLeft1);

            Patch patchLeft2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 2);
            Amenity.AmenityBlock amenityBlockLeft2 = amenityBlockFactory.create(patchLeft2, true, true);
            amenityBlocks.add(amenityBlockLeft2);
            patchLeft2.setAmenityBlock(amenityBlockLeft2);

            Patch patchLeft3 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 3);
            Amenity.AmenityBlock amenityBlockLeft3 = amenityBlockFactory.create(patchLeft3, true, false);
            amenityBlocks.add(amenityBlockLeft3);
            patchLeft3.setAmenityBlock(amenityBlockLeft3);

            Patch patchLeft4 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 4);
            Amenity.AmenityBlock amenityBlockLeft4 = amenityBlockFactory.create(patchLeft4, true, true);
            amenityBlocks.add(amenityBlockLeft4);
            patchLeft4.setAmenityBlock(amenityBlockLeft4);

            Patch patchLeft5 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 5);
            Amenity.AmenityBlock amenityBlockLeft5 = amenityBlockFactory.create(patchLeft5, true, false);
            amenityBlocks.add(amenityBlockLeft5);
            patchLeft5.setAmenityBlock(amenityBlockLeft5);

            Patch patchRight0 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlockRight0 = amenityBlockFactory.create(patchRight0, true, false);
            amenityBlocks.add(amenityBlockRight0);
            patchRight0.setAmenityBlock(amenityBlockRight0);

            Patch patchRight1 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight1 = amenityBlockFactory.create(patchRight1, true, false);
            amenityBlocks.add(amenityBlockRight1);
            patchRight1.setAmenityBlock(amenityBlockRight1);

            Patch patchRight2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 2);
            Amenity.AmenityBlock amenityBlockRight2 = amenityBlockFactory.create(patchRight2, true, false);
            amenityBlocks.add(amenityBlockRight2);
            patchRight2.setAmenityBlock(amenityBlockRight2);

            Patch patchRight3 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 3);
            Amenity.AmenityBlock amenityBlockRight3 = amenityBlockFactory.create(patchRight3, true, false);
            amenityBlocks.add(amenityBlockRight3);
            patchRight3.setAmenityBlock(amenityBlockRight3);

            Patch patchRight4 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 4);
            Amenity.AmenityBlock amenityBlockRight4 = amenityBlockFactory.create(patchRight4, true, false);
            amenityBlocks.add(amenityBlockRight4);
            patchRight4.setAmenityBlock(amenityBlockRight4);

            Patch patchRight5 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 5);
            Amenity.AmenityBlock amenityBlockRight5 = amenityBlockFactory.create(patchRight5, true, false);
            amenityBlocks.add(amenityBlockRight5);
            patchRight5.setAmenityBlock(amenityBlockRight5);

            for (int i = 1; i < 6; i++) {
                Patch patchBack = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + i);
                Amenity.AmenityBlock amenityBlockBack = null;
                if (i % 2 == 0) {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, true, true);
                }
                else {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, true, false);
                }
                amenityBlocks.add(amenityBlockBack);
                patchBack.setAmenityBlock(amenityBlockBack);
            }

            for (int i = 0; i < 6; i++) {
                Patch patchFront = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + i);
                Amenity.AmenityBlock amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                amenityBlocks.add(amenityBlockFront);
                patchFront.setAmenityBlock(amenityBlockFront);
            }

            FreshProducts freshProductsToAdd = FreshProducts.FreshProductsFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getFreshProducts().add(freshProductsToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}