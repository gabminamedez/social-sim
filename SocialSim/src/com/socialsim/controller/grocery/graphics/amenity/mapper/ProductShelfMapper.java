package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.ProductShelf;

import java.util.ArrayList;
import java.util.List;

public class ProductShelfMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = ProductShelf.ProductShelfBlock.productShelfBlockFactory;
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

            ProductShelf productShelfToAdd = ProductShelf.ProductShelfFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getProductShelves().add(productShelfToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}