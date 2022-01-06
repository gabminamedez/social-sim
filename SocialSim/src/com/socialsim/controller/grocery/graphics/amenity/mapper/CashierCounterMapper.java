package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchfield.CashierCounterField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.CashierCounter;

import java.util.ArrayList;
import java.util.List;

public class CashierCounterMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = CashierCounter.CashierCounterBlock.cashierCounterBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            for (int i = 1; i < 4; i++) {
                Patch patchBack = Main.grocerySimulator.getGrocery().getPatch(origPatchRow + i, origPatchCol);
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

            CashierCounter cashierCounterToAdd = CashierCounter.CashierCounterFactory.create(amenityBlocks, true, 20);
            Main.grocerySimulator.getGrocery().getCashierCounters().add(cashierCounterToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));

            List<Patch> cashierCounterFieldPatches = new ArrayList<>();
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 2, origPatchCol));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 2, origPatchCol - 1));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow + 1, origPatchCol - 1));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol - 1));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 1, origPatchCol - 1));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 2, origPatchCol - 1));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 3, origPatchCol - 1));
            cashierCounterFieldPatches.add(Main.grocerySimulator.getGrocery().getPatch(origPatchRow - 4, origPatchCol - 1));
            Main.grocerySimulator.getGrocery().getCashierCounterFields().add(CashierCounterField.cashierCounterFieldFactory.create(cashierCounterFieldPatches, cashierCounterToAdd, 1));
        }
    }

}