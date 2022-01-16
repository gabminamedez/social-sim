package com.socialsim.controller.mall.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Security;
import com.socialsim.model.core.environment.mall.patchfield.SecurityField;

import java.util.ArrayList;
import java.util.List;

public class SecurityMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Security.SecurityBlock.securityBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch patch2 = Main.mallSimulator.getMall().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
            amenityBlocks.add(amenityBlock2);
            patch2.setAmenityBlock(amenityBlock2);

            Security securityToAdd = Security.SecurityFactory.create(amenityBlocks, true, 5);
            Main.mallSimulator.getMall().getSecurities().add(securityToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> securityFieldPatches = new ArrayList<>();
            securityFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 1, origPatchCol));
            securityFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 1, origPatchCol - 1));
            Main.mallSimulator.getMall().getSecurityFields().add(SecurityField.securityFieldFactory.create(securityFieldPatches, securityToAdd, 1));
        }
    }

}