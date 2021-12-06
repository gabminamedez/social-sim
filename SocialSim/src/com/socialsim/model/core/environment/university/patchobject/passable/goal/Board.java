package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.graphics.amenity.University.BoardGraphic;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.passable.goal.Goal;

import java.util.List;

public class Board extends Goal {

    public static final Board.BoardFactory boardFactory;
    private final BoardGraphic boardGraphic;

    static {
        boardFactory = new Board.BoardFactory();
    }

    protected Board(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.boardGraphic = new BoardGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "Board" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.boardGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.boardGraphic.getGraphicLocation();
    }

    public static class BoardBlock extends Amenity.AmenityBlock {
        public static Board.BoardBlock.BoardBlockFactory boardBlockFactory;

        static {
            boardBlockFactory = new Board.BoardBlock.BoardBlockFactory();
        }

        private BoardBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class BoardBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Board.BoardBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Board.BoardBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class BoardFactory extends GoalFactory {
        public static Board create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new Board(amenityBlocks, enabled, facing);
        }
    }

}