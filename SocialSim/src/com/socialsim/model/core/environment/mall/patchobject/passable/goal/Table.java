package com.socialsim.model.core.environment.mall.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.controller.mall.graphics.amenity.graphic.TableGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class Table extends Goal {

    public static final Table.TableFactory tableFactory;
    private final TableGraphic tableGraphic;

    static {
        tableFactory = new Table.TableFactory();
    }

    protected Table(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.tableGraphic = new TableGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "Table" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public MallAmenityGraphic getGraphicObject() {
        return this.tableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.tableGraphic.getGraphicLocation();
    }

    public static class TableBlock extends Amenity.AmenityBlock {
        public static Table.TableBlock.TableBlockFactory tableBlockFactory;

        static {
            tableBlockFactory = new Table.TableBlock.TableBlockFactory();
        }

        private TableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class TableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Table.TableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new Table.TableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class TableFactory extends GoalFactory {
        public static com.socialsim.model.core.environment.mall.patchobject.passable.goal.Table create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new com.socialsim.model.core.environment.mall.patchobject.passable.goal.Table(amenityBlocks, enabled, facing);
        }
    }

}