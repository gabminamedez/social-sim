package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.FreshProductsGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class FreshProducts extends Goal {

    public static final FreshProducts.FreshProductsFactory freshProductsFactory;
    private final FreshProductsGraphic freshProductsGraphic;

    static {
        freshProductsFactory = new FreshProducts.FreshProductsFactory();
    }

    protected FreshProducts(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.freshProductsGraphic = new FreshProductsGraphic(this);
    }


    @Override
    public String toString() {
        return "FreshProducts" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.freshProductsGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.freshProductsGraphic.getGraphicLocation();
    }

    public static class FreshProductsBlock extends Amenity.AmenityBlock {
        public static FreshProducts.FreshProductsBlock.FreshProductsBlockFactory freshProductsBlockFactory;

        static {
            freshProductsBlockFactory = new FreshProducts.FreshProductsBlock.FreshProductsBlockFactory();
        }

        private FreshProductsBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class FreshProductsBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public FreshProducts.FreshProductsBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new FreshProducts.FreshProductsBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class FreshProductsFactory extends Goal.GoalFactory {
        public static FreshProducts create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new FreshProducts(amenityBlocks, enabled);
        }
    }

}