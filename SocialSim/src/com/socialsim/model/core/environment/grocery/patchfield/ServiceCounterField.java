package com.socialsim.model.core.environment.grocery.patchfield;

import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.QueueingPatchField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.ServiceCounter;
import javafx.util.Pair;

import java.util.List;

public class ServiceCounterField extends QueueingPatchField {

    protected ServiceCounterField(List<Patch> patches, ServiceCounter target, int num) {
        super(patches, target);

        Pair<QueueingPatchField, Integer> pair = new Pair<>(this, num);
        for(Patch patch : patches) {
            patch.setQueueingPatchField(pair);
        }
    }

    public static ServiceCounterField.ServiceCounterFieldFactory serviceCounterFieldFactory;

    static {
        serviceCounterFieldFactory = new ServiceCounterField.ServiceCounterFieldFactory();
    }

    public static class ServiceCounterFieldFactory extends PatchField.PatchFieldFactory {
        public ServiceCounterField create(List<Patch> patches, ServiceCounter target, int num) {
            return new ServiceCounterField(patches, target, num);
        }
    }

}