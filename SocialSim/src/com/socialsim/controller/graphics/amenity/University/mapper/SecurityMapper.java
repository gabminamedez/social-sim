package com.socialsim.controller.graphics.amenity.University.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Security;

import java.util.ArrayList;
import java.util.List;

public class SecurityMapper extends AmenityMapper {

    public static void draw(List<UniversityPatch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (UniversityPatch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Security.SecurityBlock.securityBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            UniversityPatch lowerPatch = Main.simulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            lowerPatch.setAmenityBlock(amenityBlock2);

            Security securityToAdd = Security.SecurityFactory.create(amenityBlocks, true, 20, false);
            Main.simulator.getUniversity().getSecurities().add(securityToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getUniversity().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}