package com.socialsim.model.core.environment.office.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Reception extends PatchField {

    protected Reception(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static Reception.ReceptionFactory receptionFactory;

    static {
        receptionFactory = new Reception.ReceptionFactory();
    }

    public static class ReceptionFactory extends PatchFieldFactory {
        public Reception create(List<Patch> patches, int num) {
            return new Reception(patches, num);
        }
    }

}