package com.socialsim.model.core.environment.office.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class SecretaryRoom extends PatchField {

    protected SecretaryRoom(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static SecretaryRoom.SecretaryRoomFactory secretaryRoomFactory;

    static {
        secretaryRoomFactory = new SecretaryRoom.SecretaryRoomFactory();
    }

    public static class SecretaryRoomFactory extends PatchFieldFactory {
        public SecretaryRoom create(List<Patch> patches, int num) {
            return new SecretaryRoom(patches, num);
        }
    }

}