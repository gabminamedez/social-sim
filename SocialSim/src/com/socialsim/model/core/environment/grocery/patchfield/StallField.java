package com.socialsim.model.core.environment.grocery.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Stall;
import javafx.util.Pair;

import java.util.List;

public class StallField extends QueueingPatchField {

    protected StallField(List<Patch> patches, Stall target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static StallField.StallFieldFactory stallFieldFactory;

    static {
        stallFieldFactory = new StallField.StallFieldFactory();
    }

    public static class StallFieldFactory extends PatchField.PatchFieldFactory {
        public StallField create(List<Patch> patches, Stall target, int num) {
            return new StallField(patches, target, num);
        }
    }

}