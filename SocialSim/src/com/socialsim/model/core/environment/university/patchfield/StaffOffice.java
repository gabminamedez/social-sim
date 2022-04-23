package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class StaffOffice extends PatchField {

    protected StaffOffice(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static StaffOffice.StaffOfficeFactory staffOfficeFactory;

    static {
        staffOfficeFactory = new StaffOffice.StaffOfficeFactory();
    }

    public static class StaffOfficeFactory extends PatchFieldFactory {
        public StaffOffice create(List<Patch> patches, int num) {
            return new StaffOffice(patches, num);
        }
    }

}