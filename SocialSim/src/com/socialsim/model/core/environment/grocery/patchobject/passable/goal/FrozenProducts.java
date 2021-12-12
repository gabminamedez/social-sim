package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.FrozenProductsGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class FrozenProducts extends Goal {

    public static final FrozenProducts.FrozenProductsFactory frozenProductsFactory;
    private final FrozenProductsGraphic frozenProductsGraphic;

    static {
        frozenProductsFactory = new FrozenProducts.FrozenProductsFactory();
    }

    protected FrozenProducts(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.frozenProductsGraphic = new FrozenProductsGraphic(this);
    }


    @Override
    public String toString() {
        return "FrozenProducts" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.frozenProductsGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.frozenProductsGraphic.getGraphicLocation();
    }

    public static class FrozenProductsBlock extends Amenity.AmenityBlock {
        public static FrozenProducts.FrozenProductsBlock.FrozenProductsBlockFactory frozenProductsBlockFactory;

        static {
            frozenProductsBlockFactory = new FrozenProducts.FrozenProductsBlock.FrozenProductsBlockFactory();
        }

        private FrozenProductsBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class FrozenProductsBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public FrozenProducts.FrozenProductsBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new FrozenProducts.FrozenProductsBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class FrozenProductsFactory extends Goal.GoalFactory {
        public static FrozenProducts create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new FrozenProducts(amenityBlocks, enabled);
        }
    }

}