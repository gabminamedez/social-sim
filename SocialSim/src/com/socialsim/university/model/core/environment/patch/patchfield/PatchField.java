package com.socialsim.university.model.core.environment.patch.patchfield;

import com.socialsim.university.model.core.environment.BaseUniversityObject;
import com.socialsim.university.model.core.environment.Environment;
import com.socialsim.university.model.core.environment.patch.Patch;

import java.util.ArrayList;
import java.util.List;

public abstract class PatchField extends BaseUniversityObject implements Environment {

    private final List<Patch> associatedPatches;

    protected PatchField() {
        super();

        this.associatedPatches = new ArrayList<>();
    }

    public List<Patch> getAssociatedPatches() {
        return associatedPatches;
    }

    public static abstract class FloorFieldFactory extends UniversityObjectFactory {
    }

}