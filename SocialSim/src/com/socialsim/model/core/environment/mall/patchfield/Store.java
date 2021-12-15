package com.socialsim.model.core.environment.mall.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Store extends PatchField {

    protected Store(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static StoreFactory storeFactory;

    static {
        storeFactory = new StoreFactory();
    }

    public static class StoreFactory extends PatchFieldFactory {
        public Store create(List<Patch> patches, int num) {
            return new Store(patches, num);
        }
    }

}