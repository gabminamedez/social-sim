package com.socialsim.controller.university.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Bulletin;

import java.util.ArrayList;
import java.util.List;

public class BulletinMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, String facing) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Bulletin.BulletinBlock.bulletinBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            if(facing.equals("UP") || facing.equals("DOWN")) {
                Patch patch2 = Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 1);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
                amenityBlocks.add(amenityBlock2);
                patch2.setAmenityBlock(amenityBlock2);

                Patch patch3 = Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 2);
                Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, true);
                amenityBlocks.add(amenityBlock3);
                patch3.setAmenityBlock(amenityBlock3);

                Patch patch4 = Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 3);
                Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
                amenityBlocks.add(amenityBlock4);
                patch4.setAmenityBlock(amenityBlock4);

                Patch patch5 = Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 4);
                Amenity.AmenityBlock amenityBlock5 = amenityBlockFactory.create(patch5, true, true);
                amenityBlocks.add(amenityBlock5);
                patch3.setAmenityBlock(amenityBlock5);

                Patch patch6 = Main.universitySimulator.getUniversity().getPatch(origPatchRow, origPatchCol + 5);
                Amenity.AmenityBlock amenityBlock6 = amenityBlockFactory.create(patch6, true, false);
                amenityBlocks.add(amenityBlock6);
                patch4.setAmenityBlock(amenityBlock6);
            }
            else {
                Patch patch2 = Main.universitySimulator.getUniversity().getPatch(origPatchRow + 1, origPatchCol);
                Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
                amenityBlocks.add(amenityBlock2);
                patch2.setAmenityBlock(amenityBlock2);

                Patch patch3 = Main.universitySimulator.getUniversity().getPatch(origPatchRow + 2, origPatchCol);
                Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, true);
                amenityBlocks.add(amenityBlock3);
                patch3.setAmenityBlock(amenityBlock3);

                Patch patch4 = Main.universitySimulator.getUniversity().getPatch(origPatchRow + 3, origPatchCol);
                Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
                amenityBlocks.add(amenityBlock4);
                patch4.setAmenityBlock(amenityBlock4);

                Patch patch5 = Main.universitySimulator.getUniversity().getPatch(origPatchRow + 4, origPatchCol);
                Amenity.AmenityBlock amenityBlock5 = amenityBlockFactory.create(patch5, true, true);
                amenityBlocks.add(amenityBlock5);
                patch3.setAmenityBlock(amenityBlock5);

                Patch patch6 = Main.universitySimulator.getUniversity().getPatch(origPatchRow + 5, origPatchCol);
                Amenity.AmenityBlock amenityBlock6 = amenityBlockFactory.create(patch6, true, false);
                amenityBlocks.add(amenityBlock6);
                patch4.setAmenityBlock(amenityBlock6);
            }

            Bulletin bulletinToAdd = Bulletin.BulletinFactory.create(amenityBlocks, true, facing);
            Main.universitySimulator.getUniversity().getBulletins().add(bulletinToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}