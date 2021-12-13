package com.socialsim.model.core.environment.office.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Breakroom extends PatchField {

    protected Breakroom(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static Breakroom.BreakroomFactory breakroomFactory;

    static {
        breakroomFactory = new Breakroom.BreakroomFactory();
    }

    public static class BreakroomFactory extends PatchFieldFactory {
        public Breakroom create(List<Patch> patches, int num) {
            return new Breakroom(patches, num);
        }
    }

}