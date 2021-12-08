package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.Patch;

import java.util.List;

public class Classroom extends PatchField {

    protected Classroom(List<Patch> patches) {
        super(patches);

        for(Patch patch : patches) {
            patch.setPatchField(this);
        }
    }

    public static ClassroomFactory classroomFactory;

    static {
        classroomFactory = new ClassroomFactory();
    }

    public static class ClassroomFactory extends PatchFieldFactory {
        public Classroom create(List<Patch> patches) {
            return new Classroom(patches);
        }
    }

}