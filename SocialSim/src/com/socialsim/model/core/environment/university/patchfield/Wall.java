package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Wall extends PatchField {

    protected Wall(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static WallFactory wallFactory;

    static {
        wallFactory = new WallFactory();
    }

    public static class WallFactory extends PatchFieldFactory {
        public Wall create(List<Patch> patches, int num) {
            return new Wall(patches, num);
        }
    }

}