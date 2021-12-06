package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.patch.patchfield.PatchField;
import com.socialsim.model.core.environment.patch.Patch;

import java.util.List;

public class Laboratory extends PatchField {

    protected Laboratory(List<Patch> patches) {
        super(patches);

        for(Patch patch : patches) {
            patch.setPatchField(Laboratory.class);
        }
    }

    public static LaboratoryFactory laboratoryFactory;

    static {
        laboratoryFactory = new LaboratoryFactory();
    }

    public static class LaboratoryFactory extends PatchFieldFactory {
        public Laboratory create(List<Patch> patches) {
            return new Laboratory(patches);
        }
    }

}