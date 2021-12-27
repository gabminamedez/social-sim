package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.CollabDesk;

import java.util.ArrayList;
import java.util.List;

public class CollabDeskMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = CollabDesk.CollabDeskBlock.collabDeskBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            for (int i = 1; i < 6; i++) {
                Patch patchBack = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + i);
                Amenity.AmenityBlock amenityBlockBack = null;
                if (i % 2 == 0) {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, true, true);
                }
                else {
                    amenityBlockBack = amenityBlockFactory.create(patchBack, true, false);
                }
                amenityBlocks.add(amenityBlockBack);
                patchBack.setAmenityBlock(amenityBlockBack);
            }

            for (int i = 0; i < 6; i++) {
                Patch patchFront = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol + i);
                Amenity.AmenityBlock amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                amenityBlocks.add(amenityBlockFront);
                patchFront.setAmenityBlock(amenityBlockFront);
            }

            CollabDesk collabDeskToAdd = CollabDesk.CollabDeskFactory.create(amenityBlocks, true);
            Main.officeSimulator.getOffice().getCollabDesks().add(collabDeskToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}