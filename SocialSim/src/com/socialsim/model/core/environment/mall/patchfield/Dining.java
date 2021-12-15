package com.socialsim.model.core.environment.mall.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Dining extends PatchField {

    protected Dining(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static Dining.DiningFactory diningFactory;

    static {
        diningFactory = new Dining.DiningFactory();
    }

    public static class DiningFactory extends PatchFieldFactory {
        public Dining create(List<Patch> patches, int num) {
            return new Dining(patches, num);
        }
    }

}