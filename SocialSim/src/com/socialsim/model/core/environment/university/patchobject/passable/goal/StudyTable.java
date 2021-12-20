package com.socialsim.model.core.environment.university.patchobject.passable.goal;

import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.controller.university.graphics.amenity.graphic.ProfTableGraphic;
import com.socialsim.controller.university.graphics.amenity.graphic.StudyTableGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.passable.goal.Goal;

import java.util.List;

public class StudyTable extends Goal {

    public static final StudyTable.StudyTableFactory studyTableFactory;
    private final StudyTableGraphic studyTableGraphic;

    static {
        studyTableFactory = new StudyTable.StudyTableFactory();
    }

    protected StudyTable(List<Amenity.AmenityBlock> amenityBlocks, boolean enabled, String facing) {
        super(amenityBlocks, enabled);

        this.studyTableGraphic = new StudyTableGraphic(this, facing);
    }

    @Override
    public String toString() {
        return "StudyTable" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public UniversityAmenityGraphic getGraphicObject() {
        return this.studyTableGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.studyTableGraphic.getGraphicLocation();
    }

    public static class StudyTableBlock extends Amenity.AmenityBlock {
        public static StudyTable.StudyTableBlock.StudyTableBlockFactory studyTableBlockFactory;

        static {
            studyTableBlockFactory = new StudyTable.StudyTableBlock.StudyTableBlockFactory();
        }

        private StudyTableBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        public static class StudyTableBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public StudyTable.StudyTableBlock create(Patch patch, boolean attractor, boolean hasGraphic) {
                return new StudyTable.StudyTableBlock(patch, attractor, hasGraphic);
            }
        }
    }

    public static class StudyTableFactory extends Goal.GoalFactory {
        public static StudyTable create(List<AmenityBlock> amenityBlocks, boolean enabled, String facing) {
            return new StudyTable(amenityBlocks, enabled, facing);
        }
    }

}