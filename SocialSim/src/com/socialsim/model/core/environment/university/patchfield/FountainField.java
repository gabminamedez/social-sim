package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.Fountain;
import javafx.util.Pair;

import java.util.List;

public class FountainField extends QueueingPatchField {

    protected FountainField(List<Patch> patches, Fountain target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static FountainField.FountainFieldFactory fountainFieldFactory;

    static {
        fountainFieldFactory = new FountainField.FountainFieldFactory();
    }

    public static class FountainFieldFactory extends PatchField.PatchFieldFactory {
        public FountainField create(List<Patch> patches, Fountain target, int num) {
            return new FountainField(patches, target, num);
        }
    }

}