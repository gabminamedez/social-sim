package com.socialsim.university.model.core.environment.patch.patchfield.headful;

import com.socialsim.university.model.core.environment.patch.Patch;
import com.socialsim.university.model.core.environment.patch.patchfield.PatchField;
import com.socialsim.university.model.core.environment.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.List;

public abstract class HeadfulPatchField extends PatchField {

    private final List<Patch> apices;
    private final Queueable target;

    protected HeadfulPatchField(Queueable target) {
        this.apices = new ArrayList<>();
        this.target = target;
    }

    public List<Patch> getApices() {
        return apices;
    }

    public Queueable getTarget() {
        return target;
    }

    public static abstract class HeadfulPatchFieldFactory {
    }

}