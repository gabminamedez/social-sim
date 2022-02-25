package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Cabinet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CabinetMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, String facing) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            if (Objects.equals(facing, "UP")) {
                Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Cabinet.CabinetBlock.cabinetBlockFactory;
                Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
                amenityBlocks.add(amenityBlock);
                patch.setAmenityBlock(amenityBlock);

                Patch patch2 = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
                amenityBlocks.add(amenityBlock2);
                patch2.setAmenityBlock(amenityBlock2);

                Patch patch3 = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol);
                Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, false, false);
                amenityBlocks.add(amenityBlock3);
                patch3.setAmenityBlock(amenityBlock3);

                Patch patch4 = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, false, false);
                amenityBlocks.add(amenityBlock4);
                patch4.setAmenityBlock(amenityBlock4);

            }
            else {
                Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Cabinet.CabinetBlock.cabinetBlockFactory;
                Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
                amenityBlocks.add(amenityBlock);
                patch.setAmenityBlock(amenityBlock);

                Patch patch2 = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, false, false);
                amenityBlocks.add(amenityBlock2);
                patch2.setAmenityBlock(amenityBlock2);

                Patch patch3 = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol);
                Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, false);
                amenityBlocks.add(amenityBlock3);
                patch3.setAmenityBlock(amenityBlock3);

                Patch patch4 = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
                amenityBlocks.add(amenityBlock4);
                patch4.setAmenityBlock(amenityBlock4);
            }

            Cabinet cabinetToAdd = Cabinet.CabinetFactory.create(amenityBlocks, true, facing);
            Main.officeSimulator.getOffice().getCabinets().add(cabinetToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}