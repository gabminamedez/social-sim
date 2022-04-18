package com.socialsim.controller.office.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Whiteboard;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Whiteboard.WhiteboardBlock.whiteboardBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch lowerPatch = Main.officeSimulator.getOffice().getPatch(origPatchRow + 1, origPatchCol);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(lowerPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            lowerPatch.setAmenityBlock(amenityBlock2);

            Whiteboard whiteboardToAdd = Whiteboard.WhiteboardFactory.create(amenityBlocks, true);
            Main.officeSimulator.getOffice().getWhiteboards().add(whiteboardToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}