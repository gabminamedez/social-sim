package com.socialsim.controller.controls;

import com.socialsim.controller.Controller;
import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.GraphicsController;
import com.socialsim.controller.graphics.amenity.University.mapper.*;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.patch.Patch;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchfield.Classroom;
import com.socialsim.model.core.environment.university.patchfield.Laboratory;
import com.socialsim.model.core.environment.university.patchfield.Wall;
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

public class MainScreenController extends Controller {

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

    private final double CANVAS_SCALE = 0.5;

    public MainScreenController() {
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    @FXML
    public void initialize() {
        int width = 60; // Value may be from 25-100
        int length = 120; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        University university = new University(rows, columns);
        initializeUniversity(university);
        setElements();
    }

    public void initializeUniversity(University university) {
        Main.simulator.resetToDefaultConfiguration(university);
        GraphicsController.tileSize = backgroundCanvas.getHeight() / Main.simulator.getUniversity().getRows();
        mapUniversity();
        drawInterface();
    }

    public void mapUniversity() {
        University university = Main.simulator.getUniversity();
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
        Wall.wallFactory.create(wallPatches);

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 50; j < 60; j++) {
                fBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Bathroom.bathroomFactory.create(true, fBathroomPatches);

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 72; j < 82; j++) {
                mBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Bathroom.bathroomFactory.create(false, mBathroomPatches);

        List<Patch> classroom1Patches = new ArrayList<>();
        for (int i = 4; i < 24; i++) {
            for (int j = 5; j < 20; j++) {
                classroom1Patches.add(university.getPatch(i, j));
            }
        }
        Classroom.classroomFactory.create(classroom1Patches);

        List<Patch> classroom2Patches = new ArrayList<>();
        for (int i = 4; i < 24; i++) {
            for (int j = 30; j < 45; j++) {
                classroom2Patches.add(university.getPatch(i, j));
            }
        }
        Classroom.classroomFactory.create(classroom2Patches);

        List<Patch> classroom3Patches = new ArrayList<>();
        for (int i = 9; i < 24; i++) {
            for (int j = 72; j < 92; j++) {
                classroom3Patches.add(university.getPatch(i, j));
            }
        }
        Classroom.classroomFactory.create(classroom3Patches);

        List<Patch> classroom4Patches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 40; j < 55; j++) {
                classroom4Patches.add(university.getPatch(i, j));
            }
        }
        Classroom.classroomFactory.create(classroom4Patches);

        List<Patch> classroom5Patches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 65; j < 79; j++) {
                classroom5Patches.add(university.getPatch(i, j));
            }
        }
        Classroom.classroomFactory.create(classroom5Patches);

        List<Patch> laboratoryPatches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 90; j < 110; j++) {
                laboratoryPatches.add(university.getPatch(i, j));
            }
        }
        Laboratory.laboratoryFactory.create(laboratoryPatches);

        List<Patch> fountainPatches = new ArrayList<>();
        fountainPatches.add(university.getPatch(11,60));
        FountainMapper.draw(fountainPatches);

        List<Patch> staircasePatches = new ArrayList<>();
        staircasePatches.add(university.getPatch(28,119));
        staircasePatches.add(university.getPatch(30,119));
        StaircaseMapper.draw(staircasePatches);

        List<Patch> universityGatePatches = new ArrayList<>();
        universityGatePatches.add(university.getPatch(59,9));
        universityGatePatches.add(university.getPatch(59,17));
        UniversityGateMapper.draw(universityGatePatches);
    }

    private void drawInterface() {
        drawUniversityViewBackground(Main.simulator.getUniversity()); // Initially draw the University environment
        drawUniversityViewForeground(Main.simulator.getUniversity(), false); // Then draw the agents in the University
    }

    public void drawUniversityViewBackground(University university) { // Draw the university view background
        GraphicsController.requestDrawUniversityView(stackPane, university, GraphicsController.tileSize, true, false);
    }

    public void drawUniversityViewForeground(University university, boolean speedAware) { // Draw the university view foreground
        GraphicsController.requestDrawUniversityView(stackPane, university, GraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.simulator.getSimulationTime().getTime();
        long elapsedTime = Main.simulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS);
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Elapsed time: " + timeString + " (" + elapsedTime + " s)");
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.simulator.getUniversity().getRows() * GraphicsController.tileSize;
        double columnsScaled = Main.simulator.getUniversity().getColumns() * GraphicsController.tileSize;

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
        if (!Main.simulator.isRunning()) { // Not yet running to running (play simulation)
            Main.simulator.setRunning(true);
            Main.simulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.simulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        Main.simulator.reset();

        // Clear all passengers
//        clearUniversity(Main.simulator.getUniversity());

        drawUniversityViewForeground(Main.simulator.getUniversity(), false); // Redraw the canvas

        if (Main.simulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

}