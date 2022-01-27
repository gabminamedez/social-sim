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
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = FrozenWall.FrozenWallBlock.frozenWallBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch patchLeft1 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft1 = amenityBlockFactory.create(patchLeft1, false, false);
            amenityBlocks.add(amenityBlockLeft1);
            patchLeft1.setAmenityBlock(amenityBlockLeft1);

            Patch patchLeft2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 2, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft2 = amenityBlockFactory.create(patchLeft2, false, true);
            amenityBlocks.add(amenityBlockLeft2);
            patchLeft2.setAmenityBlock(amenityBlockLeft2);

            Patch patchLeft3 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 3, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft3 = amenityBlockFactory.create(patchLeft3, false, false);
            amenityBlocks.add(amenityBlockLeft3);
            patchLeft3.setAmenityBlock(amenityBlockLeft3);

            Patch patchLeft4 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 4, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft4 = amenityBlockFactory.create(patchLeft4, false, true);
            amenityBlocks.add(amenityBlockLeft4);
            patchLeft4.setAmenityBlock(amenityBlockLeft4);

            Patch patchLeft5 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 5, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft5 = amenityBlockFactory.create(patchLeft5, false, false);
            amenityBlocks.add(amenityBlockLeft5);
            patchLeft5.setAmenityBlock(amenityBlockLeft5);

            Patch patchLeft6 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 6, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft6 = amenityBlockFactory.create(patchLeft6, false, true);
            amenityBlocks.add(amenityBlockLeft6);
            patchLeft6.setAmenityBlock(amenityBlockLeft6);

            Patch patchLeft7 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 7, origPatchCol);
            Amenity.AmenityBlock amenityBlockLeft7 = amenityBlockFactory.create(patchLeft7, false, false);
            amenityBlocks.add(amenityBlockLeft7);
            patchLeft7.setAmenityBlock(amenityBlockLeft7);

            Patch patchRight0 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight0 = amenityBlockFactory.create(patchRight0, true, false);
            amenityBlocks.add(amenityBlockRight0);
            patchRight0.setAmenityBlock(amenityBlockRight0);

            Patch patchRight1 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight1 = amenityBlockFactory.create(patchRight1, true, false);
            amenityBlocks.add(amenityBlockRight1);
            patchRight1.setAmenityBlock(amenityBlockRight1);

            Patch patchRight2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 2, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight2 = amenityBlockFactory.create(patchRight2, true, false);
            amenityBlocks.add(amenityBlockRight2);
            patchRight2.setAmenityBlock(amenityBlockRight2);

            Patch patchRight3 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 3, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight3 = amenityBlockFactory.create(patchRight3, true, false);
            amenityBlocks.add(amenityBlockRight3);
            patchRight3.setAmenityBlock(amenityBlockRight3);

            Patch patchRight4 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 4, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight4 = amenityBlockFactory.create(patchRight4, true, false);
            amenityBlocks.add(amenityBlockRight4);
            patchRight4.setAmenityBlock(amenityBlockRight4);

            Patch patchRight5 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 5, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight5 = amenityBlockFactory.create(patchRight5, true, false);
            amenityBlocks.add(amenityBlockRight5);
            patchRight5.setAmenityBlock(amenityBlockRight5);

            Patch patchRight6 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 6, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight6 = amenityBlockFactory.create(patchRight6, true, false);
            amenityBlocks.add(amenityBlockRight6);
            patchRight6.setAmenityBlock(amenityBlockRight6);

            Patch patchRight7 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 7, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockRight7 = amenityBlockFactory.create(patchRight7, true, false);
            amenityBlocks.add(amenityBlockRight7);
            patchRight7.setAmenityBlock(amenityBlockRight7);

            Patch patchExtra11 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 1, origPatchCol);
            Amenity.AmenityBlock amenityBlockExtra11 = amenityBlockFactory.create(patchExtra11, false, false);
            amenityBlocks.add(amenityBlockExtra11);
            patchExtra11.setAmenityBlock(amenityBlockExtra11);

            Patch patchExtra12 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 1, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockExtra12 = amenityBlockFactory.create(patchExtra12, false, false);
            amenityBlocks.add(amenityBlockExtra12);
            patchExtra12.setAmenityBlock(amenityBlockExtra12);

            Patch patchExtra21 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 8, origPatchCol);
            Amenity.AmenityBlock amenityBlockExtra21 = amenityBlockFactory.create(patchExtra21, false, false);
            amenityBlocks.add(amenityBlockExtra21);
            patchExtra21.setAmenityBlock(amenityBlockExtra21);

            Patch patchExtra22 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 8, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlockExtra22 = amenityBlockFactory.create(patchExtra22, false, false);
            amenityBlocks.add(amenityBlockExtra22);
            patchExtra22.setAmenityBlock(amenityBlockExtra22);

            FrozenWall frozenWallToAdd = FrozenWall.FrozenWallFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getFrozenWalls().add(frozenWallToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}