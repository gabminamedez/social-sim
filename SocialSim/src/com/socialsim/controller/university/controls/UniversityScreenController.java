package com.socialsim.controller.university.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.university.graphics.UniversityGraphicsController;
import com.socialsim.controller.university.graphics.amenity.mapper.*;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.*;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class UniversityScreenController extends ScreenController {

    @FXML private ScrollPane scrollPane;
    @FXML private Group canvasGroup;
    @FXML private StackPane stackPane;
    @FXML private Canvas backgroundCanvas;
    @FXML private Canvas foregroundCanvas;
    @FXML private Canvas markingsCanvas;
    @FXML private TabPane sidebar;
    @FXML private Spinner guardsSpinner;
    @FXML private Spinner janitorsSpinner;
    @FXML private Spinner professorsSpinner;
    @FXML private Spinner studentsSpinner;
    @FXML private Slider spawnsSlider;
    @FXML private Slider classroomsSlider;
    @FXML private ChoiceBox entranceChoice;
    @FXML private Text elapsedTimeText;
    @FXML private Button initializeButton;
    @FXML private ToggleButton playButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;

    private final double CANVAS_SCALE = 0.3;

    public UniversityScreenController() {
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    @FXML
    public void initializeAction() {
        int width = 60; // Value may be from 25-100
        int length = 130; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 130 columns
        University university = University.UniversityFactory.create(rows, columns);
        initializeUniversity(university);
        setElements();
    }

    public void initializeUniversity(University university) {
        Main.universitySimulator.resetToDefaultConfiguration(university);
        UniversityGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.universitySimulator.getUniversity().getRows();
        mapUniversity();
        drawInterface();
    }

    public void mapUniversity() {
        University university = Main.universitySimulator.getUniversity();
        int rows = university.getRows();
        int cols = university.getColumns();

        List<Patch> wallPatches = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            if (i < 24 || i > 35) {
                for (int j = 0; j < cols; j++) {
                    if (i < 24 && (j < 60 || j > 71)) {
                        wallPatches.add(university.getPatch(i, j));
                    }
                    else if (i > 35 && (j >= 20 && j <= 119 )) {
                        wallPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        Main.universitySimulator.getUniversity().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 50; j < 60; j++) {
                fBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(fBathroomPatches, 1));

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 9; i < 17; i++) {
            for (int j = 50; j < 60; j++) {
                mBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> cafeteriaPatches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 100; j < 130; j++) {
                cafeteriaPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getCafeterias().add(Cafeteria.cafeteriaFactory.create(cafeteriaPatches, 1));

        List<Patch> classroom1Patches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 5; j < 20; j++) {
                if (i != 23 || j <= 6 || j >= 18) {
                    classroom1Patches.add(university.getPatch(i, j));
                }
            }
        };
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom1Patches, 1));

        List<Patch> classroom2Patches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 30; j < 45; j++) {
                if (i != 23 || j <= 31 || j >= 43) {
                    classroom2Patches.add(university.getPatch(i, j));
                }
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom2Patches, 2));

        List<Patch> classroom3Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 24; j < 39; j++) {
                if (i != 36 || j <= 25 || j >= 37)
                    classroom3Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom3Patches, 3));

        List<Patch> classroom4Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 43; j < 58; j++) {
                if (i != 36 || j <= 44 || j >= 56)
                    classroom4Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom4Patches, 4));

        List<Patch> classroom5Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 62; j < 77; j++) {
                if (i != 36 || j <= 63 || j >= 75)
                    classroom5Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom5Patches, 5));

        List<Patch> classroom6Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 81; j < 96; j++) {
                if (i != 36 || j <= 82 || j >= 94)
                    classroom6Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom6Patches, 6));

        List<Patch> laboratoryPatches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 99; j < 119; j++) {
                if (i != 36 || (j >= 108 && j <= 109))
                    laboratoryPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getLaboratories().add(Laboratory.laboratoryFactory.create(laboratoryPatches, 1));

        List<Patch> studyAreaPatches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 75; j < 95; j++) {
                if (i != 23 || (j >= 84 && j <= 85)) {
                    studyAreaPatches.add(university.getPatch(i, j));
                }
            }
        }
        Main.universitySimulator.getUniversity().getStudyAreas().add(StudyArea.studyAreaFactory.create(studyAreaPatches, 1));

        List<Patch> benchRightPatches = new ArrayList<>();
        benchRightPatches.add(university.getPatch(43,0));
        benchRightPatches.add(university.getPatch(48,0));
        benchRightPatches.add(university.getPatch(53,0));
        benchRightPatches.add(university.getPatch(38,120));
        benchRightPatches.add(university.getPatch(43,120));
        benchRightPatches.add(university.getPatch(48,120));
        BenchMapper.draw(benchRightPatches, "RIGHT");

        List<Patch> benchLeftPatches = new ArrayList<>();
        benchLeftPatches.add(university.getPatch(38,19));
        benchLeftPatches.add(university.getPatch(43,19));
        benchLeftPatches.add(university.getPatch(48,19));
        benchLeftPatches.add(university.getPatch(53,19));
        benchLeftPatches.add(university.getPatch(38,129));
        benchLeftPatches.add(university.getPatch(43,129));
        benchLeftPatches.add(university.getPatch(48,129));
        BenchMapper.draw(benchLeftPatches, "LEFT");

        List<Patch> benchDownPatches = new ArrayList<>();
        benchDownPatches.add(university.getPatch(24,47));
        benchDownPatches.add(university.getPatch(24,55));
        BenchMapper.draw(benchDownPatches, "DOWN");

        List<Patch> benchUpPatches = new ArrayList<>();
        benchUpPatches.add(university.getPatch(35,98));
        BenchMapper.draw(benchUpPatches, "UP");

        List<Patch> boardRightPatches = new ArrayList<>();
        boardRightPatches.add(university.getPatch(6,5)); // Classroom 1
        boardRightPatches.add(university.getPatch(14,5)); // Classroom 1
        boardRightPatches.add(university.getPatch(6,30)); // Classroom 2
        boardRightPatches.add(university.getPatch(14,30)); // Classroom 2
        BoardMapper.draw(boardRightPatches, "RIGHT");

        List<Patch> boardLeftPatches = new ArrayList<>();
        boardLeftPatches.add(university.getPatch(40,38)); // Classroom 3
        boardLeftPatches.add(university.getPatch(48,38)); // Classroom 3
        boardLeftPatches.add(university.getPatch(40,57)); // Classroom 4
        boardLeftPatches.add(university.getPatch(48,57)); // Classroom 4
        boardLeftPatches.add(university.getPatch(40,76)); // Classroom 5
        boardLeftPatches.add(university.getPatch(48,76)); // Classroom 5
        boardLeftPatches.add(university.getPatch(40,95)); // Classroom 6
        boardLeftPatches.add(university.getPatch(48,95)); // Classroom 6
        BoardMapper.draw(boardLeftPatches, "LEFT");

        List<Patch> boardUpPatches = new ArrayList<>();
        boardUpPatches.add(university.getPatch(55,103)); // Laboratory
        boardUpPatches.add(university.getPatch(55,111)); // Laboratory
        BoardMapper.draw(boardUpPatches, "UP");

        List<Patch> bulletinRightPatches = new ArrayList<>();
        bulletinRightPatches.add(university.getPatch(36,0));
        BulletinMapper.draw(bulletinRightPatches, "RIGHT");

        List<Patch> bulletinDownPatches = new ArrayList<>();
        bulletinDownPatches.add(university.getPatch(23,9));
        bulletinDownPatches.add(university.getPatch(23,34));
        BulletinMapper.draw(bulletinDownPatches, "DOWN");

        List<Patch> bulletinUpPatches = new ArrayList<>();
        bulletinUpPatches.add(university.getPatch(36,28));
        bulletinUpPatches.add(university.getPatch(36,47));
        bulletinUpPatches.add(university.getPatch(36,66));
        bulletinUpPatches.add(university.getPatch(36,85));
        BulletinMapper.draw(bulletinUpPatches, "UP");

        List<Patch> chairPatches = new ArrayList<>();
        for (int i = 4; i < 23; i++) { // Classroom 1
            if (i == 4 || i == 6 || i == 8 || i == 10 || i == 15 || i == 17 || i == 19 || i == 21) {
                for (int j = 10; j < 19; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 4; i < 23; i++) { // Classroom 2
            if (i == 4 || i == 6 || i == 8 || i == 10 || i == 15 || i == 17 || i == 19 || i == 21) {
                for (int j = 35; j < 44; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 38; i < 56; i++) { // Classroom 3
            if (i == 38 || i == 40 || i == 42 || i == 44 || i == 49 || i == 51 || i == 53 || i == 55) {
                for (int j = 25; j < 34; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 38; i < 56; i++) { // Classroom 4
            if (i == 38 || i == 40 || i == 42 || i == 44 || i == 49 || i == 51 || i == 53 || i == 55) {
                for (int j = 44; j < 53; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 38; i < 56; i++) { // Classroom 5
            if (i == 38 || i == 40 || i == 42 || i == 44 || i == 49 || i == 51 || i == 53 || i == 55) {
                for (int j = 63; j < 72; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 38; i < 56; i++) { // Classroom 6
            if (i == 38 || i == 40 || i == 42 || i == 44 || i == 49 || i == 51 || i == 53 || i == 55) {
                for (int j = 82; j < 91; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        ChairMapper.draw(chairPatches);

        List<Patch> doorDownPatches = new ArrayList<>();
        doorDownPatches.add(university.getPatch(23,5)); // Classroom 1
        doorDownPatches.add(university.getPatch(23,18)); // Classroom 1
        doorDownPatches.add(university.getPatch(23,30)); // Classroom 2
        doorDownPatches.add(university.getPatch(23,43)); // Classroom 2
        doorDownPatches.add(university.getPatch(23,84)); // Study Room
        DoorMapper.draw(doorDownPatches, "DOWN");

        List<Patch> doorUpPatches = new ArrayList<>();
        doorUpPatches.add(university.getPatch(36,24)); // Classroom 3
        doorUpPatches.add(university.getPatch(36,37)); // Classroom 3
        doorUpPatches.add(university.getPatch(36,43)); // Classroom 4
        doorUpPatches.add(university.getPatch(36,56)); // Classroom 4
        doorUpPatches.add(university.getPatch(36,62)); // Classroom 5
        doorUpPatches.add(university.getPatch(36,75)); // Classroom 5
        doorUpPatches.add(university.getPatch(36,81)); // Classroom 6
        doorUpPatches.add(university.getPatch(36,94)); // Classroom 6
        doorUpPatches.add(university.getPatch(36,108)); // Laboratory
        DoorMapper.draw(doorUpPatches, "UP");

        List<Patch> eatTablePatches = new ArrayList<>();
        for (int i = 6; i < 22; i++) {
            if (i == 6 || i == 9 || i == 12 || i == 15 || i == 18 || i == 21) {
                for (int j = 102; j < 127; j++) {
                    if (j == 102 || j == 108 || j == 114|| j == 120 || j == 126) {
                        eatTablePatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        EatTableMapper.draw(eatTablePatches);

        List<Patch> fountainPatches = new ArrayList<>();
        fountainPatches.add(university.getPatch(20,60));
        FountainMapper.draw(fountainPatches);

        List<Patch> labTablePatches = new ArrayList<>();
        labTablePatches.add(university.getPatch(41,102));
        labTablePatches.add(university.getPatch(41,112));
        labTablePatches.add(university.getPatch(43,102));
        labTablePatches.add(university.getPatch(43,112));
        labTablePatches.add(university.getPatch(45,102));
        labTablePatches.add(university.getPatch(45,112));
        labTablePatches.add(university.getPatch(47,102));
        labTablePatches.add(university.getPatch(47,112));
        labTablePatches.add(university.getPatch(49,102));
        labTablePatches.add(university.getPatch(49,112));
        LabTableMapper.draw(labTablePatches);

        List<Patch> profTableRightPatches = new ArrayList<>();
        profTableRightPatches.add(university.getPatch(12,7)); // Classroom 1
        profTableRightPatches.add(university.getPatch(12,32)); // Classroom 2
        ProfTableMapper.draw(profTableRightPatches, "RIGHT");

        List<Patch> profTableLeftPatches = new ArrayList<>();
        profTableLeftPatches.add(university.getPatch(46,36)); // Classroom 3
        profTableLeftPatches.add(university.getPatch(46,55)); // Classroom 4
        profTableLeftPatches.add(university.getPatch(46,74)); // Classroom 5
        profTableLeftPatches.add(university.getPatch(46,93)); // Classroom 6
        ProfTableMapper.draw(profTableLeftPatches, "LEFT");

        List<Patch> profTableUpPatches = new ArrayList<>();
        profTableUpPatches.add(university.getPatch(52,108)); // Laboratory
        ProfTableMapper.draw(profTableUpPatches, "UP");

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(university.getPatch(56,13));
        SecurityMapper.draw(securityPatches);

        List<Patch> staircasePatches = new ArrayList<>();
        staircasePatches.add(university.getPatch(55,129));
        staircasePatches.add(university.getPatch(58,129));
        StaircaseMapper.draw(staircasePatches);

        List<Patch> stallPatches = new ArrayList<>();
        stallPatches.add(university.getPatch(3,102));
        stallPatches.add(university.getPatch(3,108));
        stallPatches.add(university.getPatch(3,114));
        stallPatches.add(university.getPatch(3,120));
        stallPatches.add(university.getPatch(3,126));
        StallMapper.draw(stallPatches);

        List<Patch> studyTablePatches = new ArrayList<>();
        for (int i = 5; i < 21; i++) {
            if (i == 5 || i == 8 || i == 11 || i == 14 || i == 17 || i == 20) {
                for (int j = 78; j < 92; j++) {
                    if (j == 78 || j == 82 || j == 86 || j == 90) {
                        studyTablePatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        StudyTableMapper.draw(studyTablePatches);

        List<Patch> trashPatches = new ArrayList<>();
        trashPatches.add(university.getPatch(36,19));
        trashPatches.add(university.getPatch(27,0));
        trashPatches.add(university.getPatch(29,0));
        trashPatches.add(university.getPatch(31,0));
        trashPatches.add(university.getPatch(18,71));
        trashPatches.add(university.getPatch(20,71));
        trashPatches.add(university.getPatch(22,71));
        trashPatches.add(university.getPatch(24,99));
        TrashMapper.draw(trashPatches);

        List<Patch> universityGateExitPatches = new ArrayList<>();
        universityGateExitPatches.add(university.getPatch(59,4));
        UniversityGateMapper.draw(universityGateExitPatches, UniversityGate.UniversityGateMode.EXIT);

        List<Patch> universityGateEntrancePatches = new ArrayList<>();
        universityGateEntrancePatches.add(university.getPatch(59,12));
        UniversityGateMapper.draw(universityGateEntrancePatches, UniversityGate.UniversityGateMode.ENTRANCE);
    }

    private void drawInterface() {
        drawUniversityViewBackground(Main.universitySimulator.getUniversity()); // Initially draw the University environment
        drawUniversityViewForeground(Main.universitySimulator.getUniversity(), false); // Then draw the agents in the University
    }

    public void drawUniversityViewBackground(University university) { // Draw the university view background
        UniversityGraphicsController.requestDrawUniversityView(stackPane, university, UniversityGraphicsController.tileSize, true, false);
    }

    public void drawUniversityViewForeground(University university, boolean speedAware) { // Draw the university view foreground
        UniversityGraphicsController.requestDrawUniversityView(stackPane, university, UniversityGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.universitySimulator.getSimulationTime().getTime();
        long elapsedTime = Main.universitySimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS);
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Elapsed time: " + timeString + " (" + elapsedTime + " s)");
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.universitySimulator.getUniversity().getRows() * UniversityGraphicsController.tileSize;
        double columnsScaled = Main.universitySimulator.getUniversity().getColumns() * UniversityGraphicsController.tileSize;

        stackPane.setPrefWidth(columnsScaled);
        stackPane.setPrefHeight(rowsScaled);

        backgroundCanvas.setWidth(columnsScaled);
        backgroundCanvas.setHeight(rowsScaled);

        foregroundCanvas.setWidth(columnsScaled);
        foregroundCanvas.setHeight(rowsScaled);

        markingsCanvas.setWidth(columnsScaled);
        markingsCanvas.setHeight(rowsScaled);
    }

    @FXML
    public void playAction() {
        if (!Main.universitySimulator.isRunning()) { // Not yet running to running (play simulation)
            Main.universitySimulator.setRunning(true);
            Main.universitySimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.universitySimulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        Main.universitySimulator.reset();

        // Clear all passengers
//        clearUniversity(Main.simulator.getUniversity());

        drawUniversityViewForeground(Main.universitySimulator.getUniversity(), false); // Redraw the canvas

        if (Main.universitySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    @Override
    protected void closeAction() {
    }

}