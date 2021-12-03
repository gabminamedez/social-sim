package com.socialsim.controller.graphics.amenity.University.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;

import java.util.ArrayList;
import java.util.List;

public class UniversityGateMapper extends AmenityMapper {

    public static void draw(List<UniversityPatch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (UniversityPatch patch : patches) {
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = UniversityGate.UniversityGateBlock.universityGateBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            UniversityPatch rightPatch = Main.simulator.getUniversity().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(rightPatch, true, false);
            amenityBlocks.add(amenityBlock2);
            rightPatch.setAmenityBlock(amenityBlock2);

            UniversityGate universityGateToAdd = UniversityGate.UniversityGateFactory.create(amenityBlocks, true, 20.0, UniversityGate.UniversityGateMode.ENTRANCE_AND_EXIT);
            Main.simulator.getUniversity().getUniversityGates().add(universityGateToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getUniversity().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}