package com.socialsim.controller.university.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.university.graphics.UniversityGraphicsController;
import com.socialsim.controller.university.graphics.amenity.mapper.*;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchfield.Classroom;
import com.socialsim.model.core.environment.university.patchfield.Laboratory;
import com.socialsim.model.core.environment.university.patchfield.Wall;
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
        int length = 120; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 120 columns
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
                    else if (i > 35 && j >= 30) {
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
        for (int i = 0; i < 8; i++) {
            for (int j = 72; j < 82; j++) {
                mBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> classroom1Patches = new ArrayList<>();
        for (int i = 4; i < 24; i++) {
            for (int j = 5; j < 20; j++) {
                classroom1Patches.add(university.getPatch(i, j));
            }
        };
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom1Patches, 1));

        List<Patch> classroom2Patches = new ArrayList<>();
        for (int i = 4; i < 24; i++) {
            for (int j = 30; j < 45; j++) {
                classroom2Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom2Patches, 2));

        List<Patch> classroom3Patches = new ArrayList<>();
        for (int i = 9; i < 24; i++) {
            for (int j = 72; j < 92; j++) {
                classroom3Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom3Patches, 3));

        List<Patch> classroom4Patches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 40; j < 55; j++) {
                classroom4Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom4Patches, 4));

        List<Patch> classroom5Patches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 65; j < 80; j++) {
                classroom5Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom5Patches, 5));

        List<Patch> laboratoryPatches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 90; j < 110; j++) {
                laboratoryPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getLaboratories().add(Laboratory.laboratoryFactory.create(laboratoryPatches, 1));

        List<Patch> benchRightPatches = new ArrayList<>();
        benchRightPatches.add(university.getPatch(43,0));
        benchRightPatches.add(university.getPatch(48,0));
        benchRightPatches.add(university.getPatch(53,0));
        BenchMapper.draw(benchRightPatches, "RIGHT");

        List<Patch> benchLeftPatches = new ArrayList<>();
        benchLeftPatches.add(university.getPatch(38,29));
        benchLeftPatches.add(university.getPatch(43,29));
        benchLeftPatches.add(university.getPatch(48,29));
        benchLeftPatches.add(university.getPatch(53,29));
        BenchMapper.draw(benchLeftPatches, "LEFT");

        List<Patch> benchDownPatches = new ArrayList<>();
        benchDownPatches.add(university.getPatch(24,95));
        benchDownPatches.add(university.getPatch(24,113));
        BenchMapper.draw(benchDownPatches, "DOWN");

        List<Patch> benchUpPatches = new ArrayList<>();
        benchUpPatches.add(university.getPatch(35,83));
        BenchMapper.draw(benchUpPatches, "UP");

        List<Patch> boardRightPatches = new ArrayList<>();
        boardRightPatches.add(university.getPatch(7,5)); // Classroom 1
        boardRightPatches.add(university.getPatch(15,5)); // Classroom 1
        boardRightPatches.add(university.getPatch(7,30)); // Classroom 2
        boardRightPatches.add(university.getPatch(15,30)); // Classroom 2
        BoardMapper.draw(boardRightPatches, "RIGHT");

        List<Patch> boardDownPatches = new ArrayList<>();
        boardDownPatches.add(university.getPatch(9,75)); // Classroom 3
        boardDownPatches.add(university.getPatch(9,83)); // Classroom 3
        BoardMapper.draw(boardDownPatches, "DOWN");

        List<Patch> boardLeftPatches = new ArrayList<>();
        boardLeftPatches.add(university.getPatch(39,54)); // Classroom 4
        boardLeftPatches.add(university.getPatch(47,54)); // Classroom 4
        boardLeftPatches.add(university.getPatch(39,79)); // Classroom 4
        boardLeftPatches.add(university.getPatch(47,79)); // Classroom 4
        BoardMapper.draw(boardLeftPatches, "LEFT");

        List<Patch> boardUpPatches = new ArrayList<>();
        boardUpPatches.add(university.getPatch(55,93)); // Laboratory
        boardUpPatches.add(university.getPatch(55,101)); // Laboratory
        BoardMapper.draw(boardUpPatches, "UP");

        List<Patch> bulletinRightPatches = new ArrayList<>();
        bulletinRightPatches.add(university.getPatch(36,0));
        BulletinMapper.draw(bulletinRightPatches, "RIGHT");

        List<Patch> bulletinDownPatches = new ArrayList<>();
        bulletinDownPatches.add(university.getPatch(23,45));
        bulletinDownPatches.add(university.getPatch(23,54));
        bulletinDownPatches.add(university.getPatch(23,103));
        BulletinMapper.draw(bulletinDownPatches, "DOWN");

        List<Patch> bulletinUpPatches = new ArrayList<>();
        bulletinUpPatches.add(university.getPatch(36,57));
        BulletinMapper.draw(bulletinUpPatches, "UP");

        List<Patch> chairPatches = new ArrayList<>();
        for (int i = 5; i < 23; i++) { // Classroom 1
            if (i == 5 || i == 7 || i == 9 || i == 11 || i == 16 || i == 18 || i == 20 || i == 22) {
                for (int j = 10; j < 19; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 5; i < 23; i++) { // Classroom 2
            if (i == 5 || i == 7 || i == 9 || i == 11 || i == 16 || i == 18 || i == 20 || i == 22) {
                for (int j = 35; j < 44; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 14; i < 23; i++) { // Classroom 3
            if (i % 2 == 0) {
                for (int j = 73; j < 91; j++) {
                    if (j == 73 || j == 75 || j == 77 || j == 79 || j == 84 || j == 86 || j == 88 || j == 90) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 37; i < 55; i++) { // Classroom 4
            if (i == 37 || i == 39 || i == 41 || i == 43 || i == 48 || i == 50 || i == 52 || i == 54) {
                for (int j = 41; j < 50; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 37; i < 55; i++) { // Classroom 5
            if (i == 37 || i == 39 || i == 41 || i == 43 || i == 48 || i == 50 || i == 52 || i == 54) {
                for (int j = 66; j < 75; j++) {
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
        DoorMapper.draw(doorDownPatches, "DOWN");

        List<Patch> doorLeftPatches = new ArrayList<>();
        doorLeftPatches.add(university.getPatch(9,72)); // Classroom 3
        doorLeftPatches.add(university.getPatch(22,72)); // Classroom 3
        DoorMapper.draw(doorLeftPatches, "LEFT");

        List<Patch> doorUpPatches = new ArrayList<>();
        doorUpPatches.add(university.getPatch(36,40)); // Classroom 4
        doorUpPatches.add(university.getPatch(36,53)); // Classroom 4
        doorUpPatches.add(university.getPatch(36,65)); // Classroom 5
        doorUpPatches.add(university.getPatch(36,78)); // Classroom 5
        doorUpPatches.add(university.getPatch(36,99)); // Laboratory
        DoorMapper.draw(doorUpPatches, "UP");

        List<Patch> fountainPatches = new ArrayList<>();
        fountainPatches.add(university.getPatch(11,60));
        FountainMapper.draw(fountainPatches);

        List<Patch> labTablePatches = new ArrayList<>();
        labTablePatches.add(university.getPatch(41,93));
        labTablePatches.add(university.getPatch(41,103));
        labTablePatches.add(university.getPatch(43,93));
        labTablePatches.add(university.getPatch(43,103));
        labTablePatches.add(university.getPatch(45,93));
        labTablePatches.add(university.getPatch(45,103));
        labTablePatches.add(university.getPatch(47,93));
        labTablePatches.add(university.getPatch(47,103));
        labTablePatches.add(university.getPatch(49,93));
        labTablePatches.add(university.getPatch(49,103));
        LabTableMapper.draw(labTablePatches);

        List<Patch> profTableRightPatches = new ArrayList<>();
        profTableRightPatches.add(university.getPatch(13,7)); // Classroom 1
        profTableRightPatches.add(university.getPatch(13,32)); // Classroom 2
        ProfTableMapper.draw(profTableRightPatches, "RIGHT");

        List<Patch> profTableDownPatches = new ArrayList<>();
        profTableDownPatches.add(university.getPatch(11,81)); // Classroom 3
        ProfTableMapper.draw(profTableDownPatches, "DOWN");

        List<Patch> profTableLeftPatches = new ArrayList<>();
        profTableLeftPatches.add(university.getPatch(45,52)); // Classroom 4
        profTableLeftPatches.add(university.getPatch(45,77)); // Classroom 5
        ProfTableMapper.draw(profTableLeftPatches, "LEFT");

        List<Patch> profTableUpPatches = new ArrayList<>();
        profTableUpPatches.add(university.getPatch(52,99)); // Laboratory
        ProfTableMapper.draw(profTableUpPatches, "UP");

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(university.getPatch(56,18));
        SecurityMapper.draw(securityPatches);

        List<Patch> staircasePatches = new ArrayList<>();
        staircasePatches.add(university.getPatch(28,119));
        staircasePatches.add(university.getPatch(30,119));
        StaircaseMapper.draw(staircasePatches);

        List<Patch> trashPatches = new ArrayList<>();
        trashPatches.add(university.getPatch(36,29));
        trashPatches.add(university.getPatch(27,0));
        trashPatches.add(university.getPatch(29,0));
        trashPatches.add(university.getPatch(31,0));
        trashPatches.add(university.getPatch(24,101));
        trashPatches.add(university.getPatch(24,110));
        TrashMapper.draw(trashPatches);

        List<Patch> universityGateExitPatches = new ArrayList<>();
        universityGateExitPatches.add(university.getPatch(59,9));
        UniversityGateMapper.draw(universityGateExitPatches, UniversityGate.UniversityGateMode.EXIT);

        List<Patch> universityGateEntrancePatches = new ArrayList<>();
        universityGateEntrancePatches.add(university.getPatch(59,17));
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