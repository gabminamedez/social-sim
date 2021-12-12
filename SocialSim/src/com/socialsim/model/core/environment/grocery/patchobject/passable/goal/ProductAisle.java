package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.ProductAisleGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class ProductAisle extends Goal {

    public static final ProductAisle.ProductAisleFactory productAisleFactory;
    private final ProductAisleGraphic productAisleGraphic;

    static {
        productAisleFactory = new ProductAisle.ProductAisleFactory();
    }

    protected ProductAisle(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.productAisleGraphic = new ProductAisleGraphic(this);
    }


    @Override
    public String toString() {
        return "ProductAisle" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.productAisleGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.productAisleGraphic.getGraphicLocation();
    }

    public static class ProductAisleBlock extends Amenity.AmenityBlock {
        public static ProductAisle.ProductAisleBlock.ProductAisleBlockFactory productAisleBlockFactory;

        static {
            productAisleBlockFactory = new ProductAisle.ProductAisleBlock.ProductAisleBlockFactory();
        }

        private ProductAisleBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ProductAisleBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public ProductAisle.ProductAisleBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ProductAisle.ProductAisleBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ProductAisleFactory extends Goal.GoalFactory {
        public static ProductAisle create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new ProductAisle(amenityBlocks, enabled);
        }
    }

}