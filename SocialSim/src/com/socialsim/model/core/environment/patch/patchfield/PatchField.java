package com.socialsim.model.core.environment.patch.patchfield;

import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.patch.BaseObject;
import com.socialsim.model.core.environment.university.UniversityPatch;

import java.util.ArrayList;
import java.util.List;

public abstract class PatchField extends BaseObject implements Environment {

    private final List<UniversityPatch> associatedPatches;

    protected PatchField() {
        super();

        this.associatedPatches = new ArrayList<>();
    }

    public List<UniversityPatch> getAssociatedPatches() {
        return associatedPatches;
    }

    public static abstract class PatchFieldFactory extends ObjectFactory {
    }

}