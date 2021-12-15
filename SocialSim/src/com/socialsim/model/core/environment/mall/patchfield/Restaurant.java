package com.socialsim.model.core.environment.mall.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Restaurant extends PatchField {

    protected Restaurant(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static Restaurant.RestaurantFactory restaurantFactory;

    static {
        restaurantFactory = new Restaurant.RestaurantFactory();
    }

    public static class RestaurantFactory extends PatchFieldFactory {
        public Restaurant create(List<Patch> patches, int num) {
            return new Restaurant(patches, num);
        }
    }

}