package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.ProductAisle;

import java.util.ArrayList;
import java.util.List;

public class ProductAisleMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = ProductAisle.ProductAisleBlock.productAisleBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

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

            Patch patchLeft6 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 6);
            Amenity.AmenityBlock amenityBlockLeft6 = amenityBlockFactory.create(patchLeft6, true, true);
            amenityBlocks.add(amenityBlockLeft6);
            patchLeft6.setAmenityBlock(amenityBlockLeft6);

            Patch patchLeft7 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 7);
            Amenity.AmenityBlock amenityBlockLeft7 = amenityBlockFactory.create(patchLeft7, true, false);
            amenityBlocks.add(amenityBlockLeft7);
            patchLeft7.setAmenityBlock(amenityBlockLeft7);

            Patch patchLeft8 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 8);
            Amenity.AmenityBlock amenityBlockLeft8 = amenityBlockFactory.create(patchLeft8, true, true);
            amenityBlocks.add(amenityBlockLeft8);
            patchLeft8.setAmenityBlock(amenityBlockLeft8);

            Patch patchLeft9 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 9);
            Amenity.AmenityBlock amenityBlockLeft9 = amenityBlockFactory.create(patchLeft9, true, false);
            amenityBlocks.add(amenityBlockLeft9);
            patchLeft9.setAmenityBlock(amenityBlockLeft9);

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

            Patch patchRight6 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 6);
            Amenity.AmenityBlock amenityBlockRight6 = amenityBlockFactory.create(patchRight6, true, false);
            amenityBlocks.add(amenityBlockRight6);
            patchRight6.setAmenityBlock(amenityBlockRight6);

            Patch patchRight7 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 7);
            Amenity.AmenityBlock amenityBlockRight7 = amenityBlockFactory.create(patchRight7, true, false);
            amenityBlocks.add(amenityBlockRight7);
            patchRight7.setAmenityBlock(amenityBlockRight7);

            Patch patchRight8 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 8);
            Amenity.AmenityBlock amenityBlockRight8 = amenityBlockFactory.create(patchRight8, true, false);
            amenityBlocks.add(amenityBlockRight8);
            patchRight8.setAmenityBlock(amenityBlockRight8);

            Patch patchRight9 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 9);
            Amenity.AmenityBlock amenityBlockRight9 = amenityBlockFactory.create(patchRight9, true, false);
            amenityBlocks.add(amenityBlockRight9);
            patchRight9.setAmenityBlock(amenityBlockRight9);

            ProductAisle productAisleToAdd = ProductAisle.ProductAisleFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getProductAisles().add(productAisleToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}