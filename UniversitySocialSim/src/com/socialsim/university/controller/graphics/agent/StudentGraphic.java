package com.socialsim.university.controller.graphics.agent;

import com.socialsim.university.controller.graphics.Graphic;
import com.socialsim.university.controller.graphics.amenity.Changeable;
import com.socialsim.university.model.core.agent.student.Student;
import com.socialsim.university.model.core.environment.Environment;
import com.socialsim.university.model.simulator.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentGraphic extends Graphic implements Changeable {

    public static final String STUDENT_SPRITE_SHEET_URL = "com/socialsim/university/view/image/agent/generic_spritesheet.png";
    public static final HashMap<Student.Gender, List<List<StudentGraphicLocation>>> STUDENT_GRAPHICS = new HashMap<>();

    static {
        List<List<StudentGraphicLocation>> femaleGraphics = new ArrayList<>();
        List<List<StudentGraphicLocation>> maleGraphics = new ArrayList<>();

        final List<StudentGraphicLocation> female1Graphic = new ArrayList<>();
        female1Graphic.add(new StudentGraphicLocation(0, 0));
        female1Graphic.add(new StudentGraphicLocation(0, 1));
        female1Graphic.add(new StudentGraphicLocation(0, 2));
        female1Graphic.add(new StudentGraphicLocation(0, 3));
        femaleGraphics.add(female1Graphic);

        final List<StudentGraphicLocation> female2Graphic = new ArrayList<>();
        female2Graphic.add(new StudentGraphicLocation(1, 0));
        female2Graphic.add(new StudentGraphicLocation(1, 1));
        female2Graphic.add(new StudentGraphicLocation(1, 2));
        female2Graphic.add(new StudentGraphicLocation(1, 3));
        femaleGraphics.add(female2Graphic);

        final List<StudentGraphicLocation> female3Graphic = new ArrayList<>();
        female3Graphic.add(new StudentGraphicLocation(2, 0));
        female3Graphic.add(new StudentGraphicLocation(2, 1));
        female3Graphic.add(new StudentGraphicLocation(2, 2));
        female3Graphic.add(new StudentGraphicLocation(2, 3));
        femaleGraphics.add(female3Graphic);

        final List<StudentGraphicLocation> female4Graphic = new ArrayList<>();
        female4Graphic.add(new StudentGraphicLocation(3, 0));
        female4Graphic.add(new StudentGraphicLocation(3, 1));
        female4Graphic.add(new StudentGraphicLocation(3, 2));
        female4Graphic.add(new StudentGraphicLocation(3, 3));
        femaleGraphics.add(female4Graphic);

        final List<StudentGraphicLocation> female5Graphic = new ArrayList<>();
        female5Graphic.add(new StudentGraphicLocation(4, 0));
        female5Graphic.add(new StudentGraphicLocation(4, 1));
        female5Graphic.add(new StudentGraphicLocation(4, 2));
        female5Graphic.add(new StudentGraphicLocation(4, 3));
        femaleGraphics.add(female5Graphic);

        final List<StudentGraphicLocation> female6Graphic = new ArrayList<>();
        female6Graphic.add(new StudentGraphicLocation(5, 0));
        female6Graphic.add(new StudentGraphicLocation(5, 1));
        female6Graphic.add(new StudentGraphicLocation(5, 2));
        female6Graphic.add(new StudentGraphicLocation(5, 3));
        femaleGraphics.add(female6Graphic);

        final List<StudentGraphicLocation> female7Graphic = new ArrayList<>();
        female7Graphic.add(new StudentGraphicLocation(6, 0));
        female7Graphic.add(new StudentGraphicLocation(6, 1));
        female7Graphic.add(new StudentGraphicLocation(6, 2));
        female7Graphic.add(new StudentGraphicLocation(6, 3));
        femaleGraphics.add(female7Graphic);

        final List<StudentGraphicLocation> female8Graphic = new ArrayList<>();
        female8Graphic.add(new StudentGraphicLocation(7, 0));
        female8Graphic.add(new StudentGraphicLocation(7, 1));
        female8Graphic.add(new StudentGraphicLocation(7, 2));
        female8Graphic.add(new StudentGraphicLocation(7, 3));
        femaleGraphics.add(female8Graphic);

        final List<StudentGraphicLocation> female9Graphic = new ArrayList<>();
        female9Graphic.add(new StudentGraphicLocation(8, 0));
        female9Graphic.add(new StudentGraphicLocation(8, 1));
        female9Graphic.add(new StudentGraphicLocation(8, 2));
        female9Graphic.add(new StudentGraphicLocation(8, 3));
        femaleGraphics.add(female9Graphic);

        final List<StudentGraphicLocation> female10Graphic = new ArrayList<>();
        female10Graphic.add(new StudentGraphicLocation(9, 0));
        female10Graphic.add(new StudentGraphicLocation(9, 1));
        female10Graphic.add(new StudentGraphicLocation(9, 2));
        female10Graphic.add(new StudentGraphicLocation(9, 3));
        femaleGraphics.add(female10Graphic);

        final List<StudentGraphicLocation> male1Graphic = new ArrayList<>();
        male1Graphic.add(new StudentGraphicLocation(10, 0));
        male1Graphic.add(new StudentGraphicLocation(10, 1));
        male1Graphic.add(new StudentGraphicLocation(10, 2));
        male1Graphic.add(new StudentGraphicLocation(10, 3));
        maleGraphics.add(male1Graphic);

        final List<StudentGraphicLocation> male2Graphic = new ArrayList<>();
        male2Graphic.add(new StudentGraphicLocation(11, 0));
        male2Graphic.add(new StudentGraphicLocation(11, 1));
        male2Graphic.add(new StudentGraphicLocation(11, 2));
        male2Graphic.add(new StudentGraphicLocation(11, 3));
        maleGraphics.add(male2Graphic);

        final List<StudentGraphicLocation> male3Graphic = new ArrayList<>();
        male3Graphic.add(new StudentGraphicLocation(12, 0));
        male3Graphic.add(new StudentGraphicLocation(12, 1));
        male3Graphic.add(new StudentGraphicLocation(12, 2));
        male3Graphic.add(new StudentGraphicLocation(12, 3));
        maleGraphics.add(male3Graphic);

        final List<StudentGraphicLocation> male4Graphic = new ArrayList<>();
        male4Graphic.add(new StudentGraphicLocation(13, 0));
        male4Graphic.add(new StudentGraphicLocation(13, 1));
        male4Graphic.add(new StudentGraphicLocation(13, 2));
        male4Graphic.add(new StudentGraphicLocation(13, 3));
        maleGraphics.add(male4Graphic);

        final List<StudentGraphicLocation> male5Graphic = new ArrayList<>();
        male5Graphic.add(new StudentGraphicLocation(14, 0));
        male5Graphic.add(new StudentGraphicLocation(14, 1));
        male5Graphic.add(new StudentGraphicLocation(14, 2));
        male5Graphic.add(new StudentGraphicLocation(14, 3));
        maleGraphics.add(male5Graphic);

        final List<StudentGraphicLocation> male6Graphic = new ArrayList<>();
        male6Graphic.add(new StudentGraphicLocation(15, 0));
        male6Graphic.add(new StudentGraphicLocation(15, 1));
        male6Graphic.add(new StudentGraphicLocation(15, 2));
        male6Graphic.add(new StudentGraphicLocation(15, 3));
        maleGraphics.add(male6Graphic);

        final List<StudentGraphicLocation> male7Graphic = new ArrayList<>();
        male7Graphic.add(new StudentGraphicLocation(16, 0));
        male7Graphic.add(new StudentGraphicLocation(16, 1));
        male7Graphic.add(new StudentGraphicLocation(16, 2));
        male7Graphic.add(new StudentGraphicLocation(16, 3));
        maleGraphics.add(male7Graphic);

        final List<StudentGraphicLocation> male8Graphic = new ArrayList<>();
        male8Graphic.add(new StudentGraphicLocation(17, 0));
        male8Graphic.add(new StudentGraphicLocation(17, 1));
        male8Graphic.add(new StudentGraphicLocation(17, 2));
        male8Graphic.add(new StudentGraphicLocation(17, 3));
        maleGraphics.add(male8Graphic);

        final List<StudentGraphicLocation> male9Graphic = new ArrayList<>();
        male9Graphic.add(new StudentGraphicLocation(18, 0));
        male9Graphic.add(new StudentGraphicLocation(18, 1));
        male9Graphic.add(new StudentGraphicLocation(18, 2));
        male9Graphic.add(new StudentGraphicLocation(18, 3));
        maleGraphics.add(male9Graphic);

        final List<StudentGraphicLocation> male10Graphic = new ArrayList<>();
        male10Graphic.add(new StudentGraphicLocation(19, 0));
        male10Graphic.add(new StudentGraphicLocation(19, 1));
        male10Graphic.add(new StudentGraphicLocation(19, 2));
        male10Graphic.add(new StudentGraphicLocation(19, 3));
        maleGraphics.add(male10Graphic);

        STUDENT_GRAPHICS.put(Student.Gender.FEMALE, femaleGraphics);
        STUDENT_GRAPHICS.put(Student.Gender.MALE, maleGraphics);
    }

    private final Student student;

    protected final List<StudentGraphicLocation> graphics;
    protected int graphicIndex;

    public StudentGraphic(Student student) {
        final int typesPerGender = 10;

        this.student = student;
        this.graphics = new ArrayList<>();

        int graphicType = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(typesPerGender);

        List<StudentGraphicLocation> studentGraphics = STUDENT_GRAPHICS.get(student.getGender()).get(graphicType);

        for (StudentGraphicLocation studentGraphicLocations : studentGraphics) {
            StudentGraphicLocation newPassengerGraphicLocation = new StudentGraphicLocation(
                    studentGraphicLocations.getGraphicRow(),
                    studentGraphicLocations.getGraphicColumn()
            );

            newPassengerGraphicLocation.setGraphicWidth(1);
            newPassengerGraphicLocation.setGraphicHeight(1);

            this.graphics.add(newPassengerGraphicLocation);
        }

        this.graphicIndex = 2;
    }

    public Student getStudent() {
        return student;
    }

    public StudentGraphicLocation getGraphicLocation() {
        return this.graphics.get(this.graphicIndex);
    }

    @Override
    public void change() {
        Student student = this.student;

        double studentHeading = student.getPassengerMovement().getHeading();
        double studentHeadingDegrees = Math.toDegrees(studentHeading);

        if (studentHeadingDegrees >= 315 && studentHeadingDegrees < 360 || studentHeadingDegrees >= 0 && studentHeadingDegrees < 45) {
            this.graphicIndex = 1;
        }
        else if (studentHeadingDegrees >= 45 && studentHeadingDegrees < 135) {
            this.graphicIndex = 0;
        }
        else if (studentHeadingDegrees >= 135 && studentHeadingDegrees < 225) {
            this.graphicIndex = 3;
        }
        else if (studentHeadingDegrees >= 225 && studentHeadingDegrees < 315) {
            this.graphicIndex = 2;
        }
    }

    public static class AmenityGraphicScale implements Environment {
        private int rowSpan;
        private int columnSpan;

        public AmenityGraphicScale(int rowSpan, int columnSpan) {
            this.rowSpan = rowSpan;
            this.columnSpan = columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public void setColumnSpan(int columnSpan) {
            this.columnSpan = columnSpan;
        }
    }

    public static class AmenityGraphicOffset implements Environment {
        private int rowOffset;
        private int columnOffset;

        public AmenityGraphicOffset(int rowOffset, int columnOffset) {
            this.rowOffset = rowOffset;
            this.columnOffset = columnOffset;
        }

        public int getRowOffset() {
            return rowOffset;
        }

        public void setRowOffset(int rowOffset) {
            this.rowOffset = rowOffset;
        }

        public int getColumnOffset() {
            return columnOffset;
        }

        public void setColumnOffset(int columnOffset) {
            this.columnOffset = columnOffset;
        }
    }

}