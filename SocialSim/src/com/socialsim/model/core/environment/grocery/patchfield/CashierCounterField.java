package com.socialsim.model.core.environment.grocery.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.CashierCounter;
import javafx.util.Pair;

import java.util.List;

public class CashierCounterField extends QueueingPatchField {

    protected CashierCounterField(List<Patch> patches, CashierCounter target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static CashierCounterField.CashierCounterFieldFactory cashierCounterFieldFactory;

    static {
        cashierCounterFieldFactory = new CashierCounterField.CashierCounterFieldFactory();
    }

    public static class CashierCounterFieldFactory extends PatchField.PatchFieldFactory {
        public CashierCounterField create(List<Patch> patches, CashierCounter target, int num) {
            return new CashierCounterField(patches, target, num);
        }
    }

}