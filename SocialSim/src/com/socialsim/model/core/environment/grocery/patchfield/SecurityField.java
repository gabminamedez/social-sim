package com.socialsim.model.core.environment.grocery.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Security;
import javafx.util.Pair;

import java.util.List;

public class SecurityField extends QueueingPatchField {

    protected SecurityField(List<Patch> patches, Security target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static SecurityFieldFactory securityFieldFactory;

    static {
        securityFieldFactory = new SecurityFieldFactory();
    }

    public static class SecurityFieldFactory extends PatchField.PatchFieldFactory {
        public SecurityField create(List<Patch> patches, Security target, int num) {
            return new SecurityField(patches, target, num);
        }
    }

}