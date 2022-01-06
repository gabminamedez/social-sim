package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchfield.SecurityField;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Security;

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

            Patch lowerPatch = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            lowerPatch.setAmenityBlock(amenityBlock2);

            Security securityToAdd = Security.SecurityFactory.create(amenityBlocks, true, 5);
            Main.officeSimulator.getOffice().getSecurities().add(securityToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> securityFieldPatches = new ArrayList<>();
            securityFieldPatches.add(Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol));
            for (int i = origPatchRow + 1; i < Main.officeSimulator.getOffice().getRows(); i++) {
                Patch currentPatch = Main.officeSimulator.getOffice().getPatch(i, origPatchCol);
                if (currentPatch.getQueueingPatchField() == null && currentPatch.getAmenityBlock() == null) {
                    securityFieldPatches.add(currentPatch);
                }
            }
            Main.officeSimulator.getOffice().getSecurityFields().add(SecurityField.securityFieldFactory.create(securityFieldPatches, securityToAdd, 1));
        }
    }

}