package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.MeatSection;

import java.util.ArrayList;
import java.util.List;

public class MeatSectionMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = MeatSection.MeatSectionBlock.meatSectionBlockFactory;
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

            MeatSection meatSectionToAdd = MeatSection.MeatSectionFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getMeatSections().add(meatSectionToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}