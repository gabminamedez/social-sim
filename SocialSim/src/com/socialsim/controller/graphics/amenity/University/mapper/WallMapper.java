package com.socialsim.controller.graphics.amenity.University.mapper;

import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.amenity.AmenityMapper;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.university.patchobject.miscellaneous.Wall;

import java.util.*;

public class WallMapper extends AmenityMapper {

    public static void draw(List<Patch> patches) {
        List<Amenity.AmenityBlock> amenityBlocks = new ArrayList<>();

        for (Patch patch : patches) {
            Amenity.AmenityBlock.AmenityBlockFactory amenityBlockFactory = Wall.WallBlock.wallBlockFactory;
            Amenity.AmenityBlock amenityBlock = amenityBlockFactory.create(patch, false, true);
            amenityBlocks.add(amenityBlock);
            patch.setAmenityBlock(amenityBlock);

            Wall wallToAdd = Wall.wallFactory.create(amenityBlocks);
            Main.simulator.getUniversity().getWalls().add(wallToAdd);
            amenityBlocks.forEach(ab -> ab.getPatch().getEnvironment().getAmenityPatchSet().add(ab.getPatch()));
            amenityBlocks.clear();
        }
    }

}