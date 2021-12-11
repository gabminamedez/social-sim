package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class StudyArea extends PatchField {

    protected StudyArea(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static StudyArea.StudyAreaFactory studyAreaFactory;

    static {
        studyAreaFactory = new StudyArea.StudyAreaFactory();
    }

    public static class StudyAreaFactory extends PatchField.PatchFieldFactory {
        public StudyArea create(List<Patch> patches, int num) {
            return new StudyArea(patches, num);
        }
    }

}