package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.ProductShelfGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class ProductShelf extends Goal {

    public static final ProductShelf.ProductShelfFactory productShelfFactory;
    private final ProductShelfGraphic productShelfGraphic;

    static {
        productShelfFactory = new ProductShelf.ProductShelfFactory();
    }

    protected ProductShelf(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.productShelfGraphic = new ProductShelfGraphic(this);
    }


    @Override
    public String toString() {
        return "ProductShelf" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.productShelfGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.productShelfGraphic.getGraphicLocation();
    }

    public static class ProductShelfBlock extends Amenity.AmenityBlock {
        public static ProductShelf.ProductShelfBlock.ProductShelfBlockFactory productShelfBlockFactory;

        static {
            productShelfBlockFactory = new ProductShelf.ProductShelfBlock.ProductShelfBlockFactory();
        }

        private ProductShelfBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ProductShelfBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public ProductShelf.ProductShelfBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ProductShelf.ProductShelfBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ProductShelfFactory extends Goal.GoalFactory {
        public static ProductShelf create(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled) {
            return new ProductShelf(amenityBlocks, enabled);
        }
    }

}