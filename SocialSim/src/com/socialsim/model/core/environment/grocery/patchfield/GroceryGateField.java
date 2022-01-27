package com.socialsim.model.core.environment.grocery.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import javafx.util.Pair;

import java.util.List;

public class GroceryGateField extends QueueingPatchField {

    protected GroceryGateField(List<Patch> patches, GroceryGate target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static GroceryGateField.GroceryGateFieldFactory groceryGateFieldFactory;

    static {
        groceryGateFieldFactory = new GroceryGateField.GroceryGateFieldFactory();
    }

    public static class GroceryGateFieldFactory extends PatchField.PatchFieldFactory {
        public GroceryGateField create(List<Patch> patches, GroceryGate target, int num) {
            return new GroceryGateField(patches, target, num);
        }
    }

}