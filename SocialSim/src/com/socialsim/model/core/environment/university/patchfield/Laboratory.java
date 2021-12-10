package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.Patch;
import javafx.util.Pair;

import java.util.List;

public class Laboratory extends PatchField {

    protected Laboratory(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static LaboratoryFactory laboratoryFactory;

    static {
        laboratoryFactory = new LaboratoryFactory();
    }

    public static class LaboratoryFactory extends PatchFieldFactory {
        public Laboratory create(List<Patch> patches, int num) {
            return new Laboratory(patches, num);
        }
    }

}