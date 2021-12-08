package com.socialsim.model.core.environment.university.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;

import java.util.List;

public class Wall extends PatchField {

    protected Wall(List<Patch> patches) {
        super(patches);

        for(Patch patch : patches) {
            patch.setPatchField(this);
        }
    }

    public static WallFactory wallFactory;

    static {
        wallFactory = new WallFactory();
    }

    public static class WallFactory extends PatchFieldFactory {
        public Wall create(List<Patch> patches) {
            return new Wall(patches);
        }
    }

}