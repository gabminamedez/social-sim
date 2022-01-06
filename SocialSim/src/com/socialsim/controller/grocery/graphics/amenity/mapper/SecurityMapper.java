package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Security;
import com.socialsim.model.core.environment.grocery.patchfield.SecurityField;

import java.util.ArrayList;
import java.util.List;

public class SecurityMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Security.SecurityBlock.securityBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch lowerPatch = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            lowerPatch.setAmenityBlock(amenityBlock2);

            com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Security securityToAdd = com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Security.SecurityFactory.create(amenityBlocks, true, 5);
            Main.grocerySimulator.getGrocery().getSecurities().add(securityToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> securityFieldPatches = new ArrayList<>();
            securityFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol));
            for (int i = origPatchRow + 1; i < Main.grocerySimulator.getGrocery().getRows(); i++) {
                Patch currentPatch = Main.grocerySimulator.getGrocery().getPatch(i, origPatchCol);
                if (currentPatch.getPatchField() == null && currentPatch.getQueueingPatchField() == null && currentPatch.getAmenityBlock() == null) {
                    securityFieldPatches.add(currentPatch);
                }
            }
            Main.grocerySimulator.getGrocery().getSecurityFields().add(SecurityField.securityFieldFactory.create(securityFieldPatches, securityToAdd, 1));
        }
    }

}