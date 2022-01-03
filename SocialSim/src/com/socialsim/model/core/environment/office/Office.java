package com.socialsim.model.core.environment.office;

import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Office extends Environment {

    private final CopyOnWriteArrayList<OfficeAgent> agents;
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> agentPatchSet;

    private final List<OfficeGate> officeGates;
    private final List<Cabinet> cabinets;
    private final List<Chair> chairs;
    private final List<CollabDesk> collabDesks;
    private final List<Couch> couches;
    private final List<Cubicle> cubicles;
    private final List<Door> doors;
    private final List<MeetingDesk> meetingDesks;
    private final List<OfficeDesk> officeDesks;
    private final List<Plant> plants;
    private final List<Printer> printers;
    private final List<ReceptionTable> receptionTables;
    private final List<Security> securities;
    private final List<Table> tables;
    private final List<Sink> sinks;
    private final List<Toilet> toilets;

    private final List<Bathroom> bathrooms;
    private final List<Breakroom> breakrooms;
    private final List<MeetingRoom> meetingRooms;
    private final List<OfficeRoom> officeRooms;
    private final List<Reception> receptions;
    private final List<Wall> walls;
    private final List<SecurityField> securityFields;

    public static final Office.OfficeFactory officeFactory;

    static {
        officeFactory = new Office.OfficeFactory();
    }

    public Office(int rows, int columns) {
        super(rows, columns);

        this.agents = new CopyOnWriteArrayList<>();

        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.agentPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        this.officeGates = Collections.synchronizedList(new ArrayList<>());
        this.cabinets = Collections.synchronizedList(new ArrayList<>());
        this.chairs = Collections.synchronizedList(new ArrayList<>());
        this.collabDesks = Collections.synchronizedList(new ArrayList<>());
        this.couches = Collections.synchronizedList(new ArrayList<>());
        this.cubicles = Collections.synchronizedList(new ArrayList<>());
        this.doors = Collections.synchronizedList(new ArrayList<>());
        this.meetingDesks = Collections.synchronizedList(new ArrayList<>());
        this.officeDesks = Collections.synchronizedList(new ArrayList<>());
        this.plants = Collections.synchronizedList(new ArrayList<>());
        this.printers = Collections.synchronizedList(new ArrayList<>());
        this.receptionTables = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());
        this.tables = Collections.synchronizedList(new ArrayList<>());
        this.sinks = Collections.synchronizedList(new ArrayList<>());
        this.toilets = Collections.synchronizedList(new ArrayList<>());

        this.bathrooms = Collections.synchronizedList(new ArrayList<>());
        this.breakrooms = Collections.synchronizedList(new ArrayList<>());
        this.meetingRooms = Collections.synchronizedList(new ArrayList<>());
        this.officeRooms = Collections.synchronizedList(new ArrayList<>());
        this.receptions = Collections.synchronizedList(new ArrayList<>());
        this.walls = Collections.synchronizedList(new ArrayList<>());
        this.securityFields = Collections.synchronizedList(new ArrayList<>());
    }

    public CopyOnWriteArrayList<OfficeAgent> getAgents() {
        return agents;
    }

    @Override
    public SortedSet<Patch> getAmenityPatchSet() {
        return amenityPatchSet;
    }

    @Override
    public SortedSet<Patch> getAgentPatchSet() {
        return agentPatchSet;
    }

    public List<OfficeGate> getOfficeGates() {
        return officeGates;
    }

    public List<Cabinet> getCabinets() {
        return cabinets;
    }

    public List<Chair> getChairs() {
        return chairs;
    }

    public List<CollabDesk> getCollabDesks() {
        return collabDesks;
    }

    public List<Couch> getCouches() {
        return couches;
    }

    public List<Cubicle> getCubicles() {
        return cubicles;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<MeetingDesk> getMeetingDesks() {
        return meetingDesks;
    }

    public List<OfficeDesk> getOfficeDesks() {
        return officeDesks;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public List<ReceptionTable> getReceptionTables() {
        return receptionTables;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public List<Toilet> getToilets() {
        return toilets;
    }

    public List<Bathroom> getBathrooms() {
        return bathrooms;
    }

    public List<Breakroom> getBreakrooms() {
        return breakrooms;
    }

    public List<MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }

    public List<OfficeRoom> getOfficeRooms() {
        return officeRooms;
    }

    public List<Reception> getReceptions() {
        return receptions;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<SecurityField> getSecurityFields() {
        return securityFields;
    }

    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == OfficeGate.class) {
            return this.getOfficeGates();
        }
        else if (amenityClass == Cabinet.class) {
            return this.getCabinets();
        }
        else if (amenityClass == Chair.class) {
            return this.getChairs();
        }
        else if (amenityClass == CollabDesk.class) {
            return this.getCollabDesks();
        }
        else if (amenityClass == Couch.class) {
            return this.getCouches();
        }
        else if (amenityClass == Cubicle.class) {
            return this.getCubicles();
        }
        else if (amenityClass == Door.class) {
            return this.getDoors();
        }
        else if (amenityClass == MeetingDesk.class) {
            return this.getMeetingDesks();
        }
        else if (amenityClass == OfficeDesk.class) {
            return this.getOfficeDesks();
        }
        else if (amenityClass == Plant.class) {
            return this.getPlants();
        }
        else if (amenityClass == Printer.class) {
            return this.getPrinters();
        }
        else if (amenityClass == ReceptionTable.class) {
            return this.getReceptionTables();
        }
        else if (amenityClass == Security.class) {
            return this.getSecurities();
        }
        else if (amenityClass == Table.class) {
            return this.getTables();
        }
        else if (amenityClass == Sink.class) {
            return this.getSinks();
        }
        else if (amenityClass == Toilet.class) {
            return this.getToilets();
        }
        else {
            return null;
        }
    }

    public static class OfficeFactory extends BaseObject.ObjectFactory {
        public static Office create(int rows, int columns) {
            return new Office(rows, columns);
        }
    }

}