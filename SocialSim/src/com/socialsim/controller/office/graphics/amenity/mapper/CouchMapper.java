package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Couch;

import java.util.ArrayList;
import java.util.List;

public class CouchMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, String facing) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Couch.CouchBlock.couchBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            if (facing.equals("DOWN")) {
                for (int i = 1; i < 8; i++) {
                    Patch patchBack = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + i);
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
                    Patch patchFront = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol + i);
                    Amenity.AmenityBlock amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                    amenityBlocks.add(amenityBlockFront);
                    patchFront.setAmenityBlock(amenityBlockFront);
                }
            }
            else {
                for (int i = 1; i < 8; i++) {
                    Patch patchBack = Main.officeSimulator.getOffice().getPatch(origPatchRow + i, origPatchCol);
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
                    Patch patchFront = Main.officeSimulator.getOffice().getPatch(origPatchRow + i, origPatchCol + 1);
                    Amenity.AmenityBlock amenityBlockFront = null;
                    amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                    amenityBlocks.add(amenityBlockFront);
                    patchFront.setAmenityBlock(amenityBlockFront);
                }
            }

            Couch couchToAdd = Couch.CouchFactory.create(amenityBlocks, true, facing);
            Main.officeSimulator.getOffice().getCouches().add(couchToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}