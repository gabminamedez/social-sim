package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.FountainField;
import com.socialsim.model.core.environment.university.patchfield.SecurityField;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Security;

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

            Patch lowerPatch = Main.universitySimulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            lowerPatch.setAmenityBlock(amenityBlock2);

            Security securityToAdd = Security.SecurityFactory.create(amenityBlocks, true, 5);
            Main.universitySimulator.getUniversity().getSecurities().add(securityToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> securityFieldPatches = new ArrayList<>();
            securityFieldPatches.add(Main.universitySimulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol));
            for (int i = origPatchRow + 1; i < Main.universitySimulator.getUniversity().getRows(); i++) {
                Patch currentPatch = Main.universitySimulator.getUniversity().getPatch(i, origPatchCol);
                if (currentPatch.getPatchField() == null && currentPatch.getQueueingPatchField() == null && currentPatch.getAmenityBlock() == null) {
                    securityFieldPatches.add(currentPatch);
                }
            }
            Main.universitySimulator.getUniversity().getSecurityFields().add(SecurityField.securityFieldFactory.create(securityFieldPatches, securityToAdd, 1));
        }
    }

}