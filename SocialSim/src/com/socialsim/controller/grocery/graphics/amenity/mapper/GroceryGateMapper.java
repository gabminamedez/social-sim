package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchfield.GroceryGateField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;

import java.util.ArrayList;
import java.util.List;

public class GroceryGateMapper extends AmenityMapper {

    public static void draw(List<Patch> patches, GroceryGate.GroceryGateMode ugMode) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = GroceryGate.GroceryGateBlock.groceryGateBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch patch2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
            amenityBlocks.add(amenityBlock2);
            patch2.setAmenityBlock(amenityBlock2);

            Patch patch3 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 2);
            Amenity.AmenityBlock amenityBlock3 = amenityBlockFactory.create(patch3, true, true);
            amenityBlocks.add(amenityBlock3);
            patch3.setAmenityBlock(amenityBlock3);

            Patch patch4 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 3);
            Amenity.AmenityBlock amenityBlock4 = amenityBlockFactory.create(patch4, true, false);
            amenityBlocks.add(amenityBlock4);
            patch4.setAmenityBlock(amenityBlock4);

            GroceryGate groceryGateToAdd = GroceryGate.GroceryGateFactory.create(amenityBlocks, true, 95, ugMode);
            Main.grocerySimulator.getGrocery().getGroceryGates().add(groceryGateToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            if (ugMode == GroceryGate.GroceryGateMode.EXIT) {
                List<Patch> groceryGateFieldPatches = new ArrayList<>();
                groceryGateFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 1, origPatchCol + 2));
                groceryGateFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 2, origPatchCol + 2));
                Main.grocerySimulator.getGrocery().getGroceryGateFields().add(GroceryGateField.groceryGateFieldFactory.create(groceryGateFieldPatches, groceryGateToAdd, 1));
            }
        }
    }

}