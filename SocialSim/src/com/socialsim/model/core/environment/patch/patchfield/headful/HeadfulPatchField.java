package com.socialsim.model.core.environment.patch.patchfield.headful;

import com.socialsim.model.core.environment.patch.patchobject.passable.Queueable;
import com.socialsim.model.core.environment.university.UniversityPatch;
import com.socialsim.model.core.environment.patch.patchfield.PatchField;

import java.util.ArrayList;
import java.util.List;

public abstract class HeadfulPatchField extends PatchField {

    private final List<UniversityPatch> apices;
    private final Queueable target;

    protected HeadfulPatchField(Queueable target) {
        this.apices = new ArrayList<>();
        this.target = target;
    }

    public List<UniversityPatch> getApices() {
        return apices;
    }

    public Queueable getTarget() {
        return target;
    }

    public static abstract class HeadfulPatchFieldFactory {
    }

}