package com.socialsim.model.core.environment.mall.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Kiosk;
import javafx.util.Pair;

import java.util.List;

public class KioskField extends QueueingPatchField {

    protected KioskField(List<Patch> patches, Kiosk target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static KioskField.KioskFieldFactory kioskFieldFactory;

    static {
        kioskFieldFactory = new KioskField.KioskFieldFactory();
    }

    public static class KioskFieldFactory extends PatchField.PatchFieldFactory {
        public KioskField create(List<Patch> patches, Kiosk target, int num) {
            return new KioskField(patches, target, num);
        }
    }

}