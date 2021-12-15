package com.socialsim.model.core.environment.office.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class OfficeRoom extends PatchField {

    protected OfficeRoom(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static OfficeRoom.OfficeRoomFactory officeRoomFactory;

    static {
        officeRoomFactory = new OfficeRoom.OfficeRoomFactory();
    }

    public static class OfficeRoomFactory extends PatchFieldFactory {
        public OfficeRoom create(List<Patch> patches, int num) {
            return new OfficeRoom(patches, num);
        }
    }

}