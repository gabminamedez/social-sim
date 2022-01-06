package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchfield.ServiceCounterField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.ServiceCounter;

import java.util.ArrayList;
import java.util.List;

public class ServiceCounterMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = ServiceCounter.ServiceCounterBlock.serviceCounterBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch patch2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
            amenityBlocks.add(amenityBlock2);
            patch2.setAmenityBlock(amenityBlock2);

            ServiceCounter serviceCounterToAdd = ServiceCounter.ServiceCounterFactory.create(amenityBlocks, true, 20);
            Main.grocerySimulator.getGrocery().getServiceCounters().add(serviceCounterToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> serviceCounterFieldPatches = new ArrayList<>();
            serviceCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 1));
            serviceCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol + 1));
            serviceCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 2, origPatchCol + 1));
            serviceCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 3, origPatchCol + 1));
            serviceCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 4, origPatchCol + 1));
            Main.grocerySimulator.getGrocery().getServiceCounterFields().add(ServiceCounterField.serviceCounterFieldFactory.create(serviceCounterFieldPatches, serviceCounterToAdd, 1));
        }
    }

}