package com.socialsim.controller.grocery.graphics.amenity.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Table;

import java.util.ArrayList;
import java.util.List;

public class TableMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        for (Patch patch : patches) {
            List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();
            int origPatchRow = patch.getMatrixPosition().getRow();
            int origPatchCol = patch.getMatrixPosition().getColumn();

            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Table.TableBlock.tableBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, true, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Patch patch2 = Main.grocerySimulator.getGrocery().getPatch(origPatchRow, origPatchCol + 1);
            Amenity.AmenityBlock amenityBlock2 = amenityBlockFactory.create(patch2, true, false);
            amenityBlocks.add(amenityBlock2);
            patch2.setAmenityBlock(amenityBlock2);

            Table tableToAdd = Table.TableFactory.create(amenityBlocks, true);
            Main.grocerySimulator.getGrocery().getTables().add(tableToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
        }
    }

}