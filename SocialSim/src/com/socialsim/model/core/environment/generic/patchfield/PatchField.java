package com.socialsim.model.core.environment.generic.patchfield;

import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import java.util.ArrayList;
import java.util.List;

public abstract class PatchField extends BaseObject {

    private final List<Patch> associatedPatches;

    protected PatchField() {
        super();

        this.associatedPatches = new ArrayList<>();
    }

    protected PatchField(List<Patch> patches) {
        super();

        this.associatedPatches = new ArrayList<>();
        associatedPatches.addAll(patches);
    }

    public List<Patch> getAssociatedPatches() {
        return associatedPatches;
    }

    public static abstract class PatchFieldFactory extends ObjectFactory {
    }

}