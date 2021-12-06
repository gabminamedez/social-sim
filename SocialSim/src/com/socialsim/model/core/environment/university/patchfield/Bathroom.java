package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.patch.patchfield.PatchField;
import com.socialsim.model.core.environment.patch.Patch;

import java.util.List;

public class Bathroom extends PatchField {

    private final boolean isFemale;

    protected Bathroom(boolean isFemale, List<Patch> patches) {
        super(patches);

        this.isFemale = isFemale;

        for(Patch patch : patches) {
            patch.setPatchField(this);
        }
    }

    public static BathroomFactory bathroomFactory;

    static {
        bathroomFactory = new BathroomFactory();
    }

    public boolean getIsFemale() {
        return this.isFemale;
    }

    public static class BathroomFactory extends PatchFieldFactory {
        public Bathroom create(boolean isFemale, List<Patch> patches) {
            return new Bathroom(isFemale, patches);
        }
    }

}