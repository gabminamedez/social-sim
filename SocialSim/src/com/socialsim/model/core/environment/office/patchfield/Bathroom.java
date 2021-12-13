package com.socialsim.model.core.environment.office.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Bathroom extends PatchField {

    protected Bathroom(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static BathroomFactory bathroomFactory;

    static {
        bathroomFactory = new BathroomFactory();
    }

    public static class BathroomFactory extends PatchFieldFactory {
        public Bathroom create(List<Patch> patches, int num) {
            return new Bathroom(patches, num);
        }
    }

}