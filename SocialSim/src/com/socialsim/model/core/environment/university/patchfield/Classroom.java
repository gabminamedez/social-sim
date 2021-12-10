package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.Patch;
import javafx.util.Pair;

import java.util.*;

public class Classroom extends PatchField {

    protected Classroom(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static ClassroomFactory classroomFactory;

    static {
        classroomFactory = new ClassroomFactory();
    }

    public static class ClassroomFactory extends PatchFieldFactory {
        public Classroom create(List<Patch> patches, int num) {
            return new Classroom(patches, num);
        }
    }

}