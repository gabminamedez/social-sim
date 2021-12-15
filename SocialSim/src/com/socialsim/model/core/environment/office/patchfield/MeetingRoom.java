package com.socialsim.model.core.environment.office.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import javafx.util.Pair;

import java.util.List;

public class MeetingRoom extends PatchField {

    protected MeetingRoom(List<Patch> patches, int num) {
        super(patches);

        Pair<PatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setPatchField(pair);
        }
    }

    public static MeetingRoom.MeetingRoomFactory meetingRoomFactory;

    static {
        meetingRoomFactory = new MeetingRoom.MeetingRoomFactory();
    }

    public static class MeetingRoomFactory extends PatchFieldFactory {
        public MeetingRoom create(List<Patch> patches, int num) {
            return new MeetingRoom(patches, num);
        }
    }

}