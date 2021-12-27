package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.ReceptionTable;

import java.util.ArrayList;
import java.util.List;

public class ReceptionTableMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = ReceptionTable.ReceptionTableBlock.receptionTableBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch patch2 = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
            amenityBlocks.add(amenityBlock2);
            patch2.setAmenityBlock(amenityBlock2);

            Patch patch3 = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + 2);
            Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, true);
            amenityBlocks.add(amenityBlock3);
            patch3.setAmenityBlock(amenityBlock3);

            Patch patch4 = Main.officeSimulator.getOffice().getPatch(origPatchRow, origPatchCol + 3);
            Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
            amenityBlocks.add(amenityBlock4);
            patch4.setAmenityBlock(amenityBlock4);

            ReceptionTable receptionTableToAdd = ReceptionTable.ReceptionTableFactory.create(amenityBlocks, true);
            Main.officeSimulator.getOffice().getReceptionTables().add(receptionTableToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}