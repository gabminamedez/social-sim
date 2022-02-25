package com.socialsim.controller.mall.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.StoreAisle;

import java.util.ArrayList;
import java.util.List;

public class StoreAisleMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, String facing) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = StoreAisle.StoreAisleBlock.storeAisleBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            if(facing.equals("UP") || facing.equals("DOWN")) {
                Patch patch2 = Main.mallSimulator.getMall().getPatch(origPatchRow, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
                amenityBlocks.add(amenityBlock2);
                patch2.setAmenityBlock(amenityBlock2);

                Patch patch3 = Main.mallSimulator.getMall().getPatch(origPatchRow, origPatchCol + 2);
                Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, true);
                amenityBlocks.add(amenityBlock3);
                patch3.setAmenityBlock(amenityBlock3);

                Patch patch4 = Main.mallSimulator.getMall().getPatch(origPatchRow, origPatchCol + 3);
                Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
                amenityBlocks.add(amenityBlock4);
                patch4.setAmenityBlock(amenityBlock4);
            }
            else {
                Patch patch2 = Main.mallSimulator.getMall().getPatch(origPatchRow + 1, origPatchCol);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
                amenityBlocks.add(amenityBlock2);
                patch2.setAmenityBlock(amenityBlock2);

                Patch patch3 = Main.mallSimulator.getMall().getPatch(origPatchRow + 2, origPatchCol);
                Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, true);
                amenityBlocks.add(amenityBlock3);
                patch3.setAmenityBlock(amenityBlock3);

                Patch patch4 = Main.mallSimulator.getMall().getPatch(origPatchRow + 3, origPatchCol);
                Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
                amenityBlocks.add(amenityBlock4);
                patch4.setAmenityBlock(amenityBlock4);
            }

            StoreAisle storeAisleToAdd = StoreAisle.StoreAisleFactory.create(amenityBlocks, true, facing);
            Main.mallSimulator.getMall().getStoreAisles().add(storeAisleToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}