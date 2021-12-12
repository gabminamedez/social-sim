package com.socialsim.model.core.environment.grocery.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.controller.grocery.graphics.amenity.graphic.CartRepoGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class CartRepo extends Goal {

    public static final CartRepo.CartRepoFactory cartRepoFactory;
    private final CartRepoGraphic cartRepoGraphic;

    static {
        cartRepoFactory = new CartRepo.CartRepoFactory();
    }

    protected CartRepo(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);

        this.cartRepoGraphic = new CartRepoGraphic(this);
    }


    @Override
    public String toString() {
        return "CartRepo" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public GroceryAmenityGraphic getGraphicObject() {
        return this.cartRepoGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.cartRepoGraphic.getGraphicLocation();
    }

    public static class CartRepoBlock extends Amenity.AmenityBlock {
        public static CartRepo.CartRepoBlock.CartRepoBlockFactory cartRepoBlockFactory;

        static {
            cartRepoBlockFactory = new CartRepo.CartRepoBlock.CartRepoBlockFactory();
        }

        private CartRepoBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class CartRepoBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public CartRepo.CartRepoBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new CartRepo.CartRepoBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class CartRepoFactory extends GoalFactory {
        public static CartRepo create(List<AmenityBlock> amenityBlocks, boolean enabled) {
            return new CartRepo(amenityBlocks, enabled);
        }
    }

}