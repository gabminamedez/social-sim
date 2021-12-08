package com.socialsim.model.core.environment.generic.patchfield.headful;

import com.socialsim.model.core.environment.generic.patchobject.passable.Queueable;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;

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