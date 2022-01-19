package com.socialsim.controller.mall.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Kiosk;
import com.socialsim.model.core.environment.mall.patchfield.KioskField;

import java.util.ArrayList;
import java.util.List;

public class KioskMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Kiosk.KioskBlock.kioskBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            for (int i = 1; i < 4; i++) {
                Patch patchBack = Main.mallSimulator.getMall().getPatch(origPatchRow, origPatchCol + i);
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

            for (int i = 0; i < 4; i++) {
                Patch patchFront = Main.mallSimulator.getMall().getPatch(origPatchRow + 1, origPatchCol + i);
                Amenity.AmenityBlock amenityBlockFront = amenityBlockFactory.create(patchFront, true, false);
                amenityBlocks.add(amenityBlockFront);
                patchFront.setAmenityBlock(amenityBlockFront);
            }

            Kiosk kioskToAdd = Kiosk.KioskFactory.create(amenityBlocks, true, 15);
            Main.mallSimulator.getMall().getKiosks().add(kioskToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> kioskFieldPatches = new ArrayList<>();
            kioskFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 1, origPatchCol + 1));
            kioskFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 2, origPatchCol + 1));
            kioskFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 3, origPatchCol + 1));
            kioskFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 4, origPatchCol + 1));
            kioskFieldPatches.add(Main.mallSimulator.getMall().getPatch(origPatchRow + 4, origPatchCol + 1));
            Main.mallSimulator.getMall().getKioskFields().add(KioskField.kioskFieldFactory.create(kioskFieldPatches, kioskToAdd, 1));
        }
    }

}