package com.socialsim.controller.mall.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;

import java.util.ArrayList;
import java.util.List;

public class MallGateMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, MallGate.MallGateMode ugMode) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = MallGate.MallGateBlock.mallGateBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

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

            MallGate mallGateToAdd = MallGate.MallGateFactory.create(amenityBlocks, true, 95, ugMode);
            Main.mallSimulator.getMall().getMallGates().add(mallGateToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}