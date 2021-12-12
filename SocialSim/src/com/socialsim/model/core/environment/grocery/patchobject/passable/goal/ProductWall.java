package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.ProductWallGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class ProductWall extends Goal {

    public static final ProductWall.ProductWallFactory productWallFactory;
    private final ProductWallGraphic productWallGraphic;

    static {
        productWallFactory = new ProductWall.ProductWallFactory();
    }

    protected ProductWall(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.productWallGraphic = new ProductWallGraphic(this, facing);
    }


    @Override
    public String toString() {
        return "ProductWall" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.productWallGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.productWallGraphic.getGraphicLocation();
    }

    public static class ProductWallBlock extends Amenity.AmenityBlock {
        public static ProductWall.ProductWallBlock.ProductWallBlockFactory productWallBlockFactory;

        static {
            productWallBlockFactory = new ProductWall.ProductWallBlock.ProductWallBlockFactory();
        }

        private ProductWallBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class ProductWallBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public ProductWall.ProductWallBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new ProductWall.ProductWallBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class ProductWallFactory extends Goal.GoalFactory {
        public static ProductWall create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new ProductWall(amenityBlocks, enabled, facing);
        }
    }

}