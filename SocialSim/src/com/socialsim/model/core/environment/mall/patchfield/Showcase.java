package com.socialsim.model.core.environment.mall.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class Showcase extends PatchField {

    protected Showcase(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static Showcase.ShowcaseFactory showcaseFactory;

    static {
        showcaseFactory = new Showcase.ShowcaseFactory();
    }

    public static class ShowcaseFactory extends PatchFieldFactory {
        public Showcase create(List<Patch> patches, int num) {
            return new Showcase(patches, num);
        }
    }

}