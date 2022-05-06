package com.socialsim.model.core.environment.generic.patchobject;

import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.core.environment.university.patchobject.passable.goal.*;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import java.util.ArrayList;
import java.util.List;

public abstract class Amenity extends PatchObject {

    private final List<AmenityBlock> amenityBlocks;
    private final List<AmenityBlock> attractors;

    protected Amenity(List<AmenityBlock> amenityBlocks) {
        this.amenityBlocks = amenityBlocks;

        if (this.amenityBlocks != null) {
            this.attractors = new ArrayList<>();

            for (AmenityBlock amenityBlock : this.amenityBlocks) {
                amenityBlock.setParent(this);
                amenityBlock.getPatch().setAmenityBlock(amenityBlock);
                if (amenityBlock.getParent().getClass() != Door.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.Door.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.Chair.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.Table.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.MeetingDesk.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.Toilet.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.Sink.class
                        && amenityBlock.getParent().getClass() != Security.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.office.patchobject.passable.goal.Security.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Security.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Toilet.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Sink.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.mall.patchobject.passable.goal.Security.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.mall.patchobject.passable.goal.StoreAisle.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.mall.patchobject.passable.goal.StoreCounter.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.mall.patchobject.passable.goal.Table.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.university.patchobject.passable.goal.StudyTable.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.university.patchobject.passable.goal.EatTable.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.university.patchobject.passable.goal.LabTable.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.university.patchobject.passable.goal.Chair.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.university.patchobject.passable.goal.Toilet.class
                        && amenityBlock.getParent().getClass() != com.socialsim.model.core.environment.university.patchobject.passable.goal.Sink.class) {
                    amenityBlock.getPatch().signalAddAmenityBlock();
                }

                if (amenityBlock.isAttractor()) {
                    this.attractors.add(amenityBlock);
                }
            }
        }
        else {
            this.attractors = null;
        }
    }

    public List<AmenityBlock> getAmenityBlocks() {
        return amenityBlocks;
    }

    public List<AmenityBlock> getAttractors() {
        return attractors;
    }

    public abstract static class AmenityBlock {
        private Amenity parent;
        private final Patch patch;
        private final boolean attractor;
        private final boolean hasGraphic;
        private boolean isReserved;

        protected AmenityBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            this.patch = patch;
            this.attractor = attractor;
            this.hasGraphic = hasGraphic;
            this.isReserved = false;
        }

        public Amenity getParent() {
            return parent;
        }

        public void setParent(Amenity parent) {
            this.parent = parent;
        }

        public Patch getPatch() {
            return patch;
        }

        public boolean isAttractor() {
            return attractor;
        }

        public boolean hasGraphic() {
            return hasGraphic;
        }

        public boolean getIsReserved() {
            return isReserved;
        }

        public void setIsReserved(boolean isReserved) {
            this.isReserved = isReserved;
        }

        private static AmenityBlockFactory getAmenityBlockFactory(Class<? extends Amenity> amenityClass) {
            if (amenityClass == UniversityGate.class) {
                return UniversityGate.UniversityGateBlock.universityGateBlockFactory;
            }
            if (amenityClass == Bench.class) {
                return Bench.BenchBlock.benchBlockFactory;
            }
            else if (amenityClass == Board.class) {
                return Board.BoardBlock.boardBlockFactory;
            }
            else if (amenityClass == Bulletin.class) {
                return Bulletin.BulletinBlock.bulletinBlockFactory;
            }
            else if (amenityClass == Chair.class) {
                return Chair.ChairBlock.chairBlockFactory;
            }
            else if (amenityClass == Door.class) {
                return Door.DoorBlock.doorBlockFactory;
            }
            else if (amenityClass == Fountain.class) {
                return Fountain.FountainBlock.fountainBlockFactory;
            }
            else if (amenityClass == LabTable.class) {
                return LabTable.LabTableBlock.labTableBlockFactory;
            }
            else if (amenityClass == ProfTable.class) {
                return ProfTable.ProfTableBlock.profTableBlockFactory;
            }
            else if (amenityClass == Security.class) {
                return Security.SecurityBlock.securityBlockFactory;
            }
            else if (amenityClass == Staircase.class) {
                return Staircase.StaircaseBlock.staircaseBlockFactory;
            }
            else if (amenityClass == Trash.class) {
                return Trash.TrashBlock.trashBlockFactory;
            }
            else {
                return null;
            }
        }

        public abstract static class AmenityBlockFactory extends BaseObject.ObjectFactory {
            public abstract AmenityBlock create(Patch patch, boolean attractor, boolean hasGraphic);
        }
    }

    public abstract static class AmenityFactory extends BaseObject.ObjectFactory {
    }

}