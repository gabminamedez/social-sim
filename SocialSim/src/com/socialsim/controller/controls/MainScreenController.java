package com.socialsim.controller.controls;

import com.socialsim.controller.Controller;
import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.GraphicsController;
import com.socialsim.controller.graphics.amenity.University.mapper.*;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.UniversityPatch;
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

    public MainScreenController() {
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    @FXML
    public void initialize() {
        int width = 40; // Value may be from 25-100
        int length = 100; // Value may be from 106-220
        int rows = (int) Math.ceil(width / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS); // 67 rows
        int columns = (int) Math.ceil(length / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS); // 167 cols
        University university = new University(rows, columns);
        initializeUniversity(university);
        setElements();
    }

    public void initializeUniversity(University university) {
        Main.simulator.resetToDefaultConfiguration(university);
        GraphicsController.tileSize = backgroundCanvas.getHeight() / Main.simulator.getUniversity().getRows();
        mapUniversityAmenities();
        drawInterface();
    }

    public void mapUniversityAmenities() {
        University university = Main.simulator.getUniversity();
        List<UniversityPatch> patches = new ArrayList<>();
        patches.add(university.getPatch(0,0));
        patches.add(university.getPatch(1,0));
        patches.add(university.getPatch(0,1));
        WallMapper.draw(patches);

        List<UniversityPatch> patches2 = new ArrayList<>();
        patches2.add(university.getPatch(2,2));
        patches2.add(university.getPatch(2,0));
        patches2.add(university.getPatch(0,2));
        ChairMapper.draw(patches2);

        List<UniversityPatch> patches3 = new ArrayList<>();
        patches3.add(university.getPatch(3,3));
        patches3.add(university.getPatch(3,0));
        patches3.add(university.getPatch(0,3));
        FountainMapper.draw(patches3);

        List<UniversityPatch> patches4 = new ArrayList<>();
        patches4.add(university.getPatch(4,4));
        patches4.add(university.getPatch(4,0));
        patches4.add(university.getPatch(0,4));
        TrashMapper.draw(patches4);

        List<UniversityPatch> patches5 = new ArrayList<>();
        patches5.add(university.getPatch(5,5));
        SecurityMapper.draw(patches5);

        List<UniversityPatch> patches6 = new ArrayList<>();
        patches6.add(university.getPatch(6,6));
        StaircaseMapper.draw(patches6);

        List<UniversityPatch> patches7 = new ArrayList<>();
        patches7.add(university.getPatch(7,7));
        LabTableMapper.draw(patches7);

        List<UniversityPatch> patches8 = new ArrayList<>();
        patches8.add(university.getPatch(8,8));
        BenchMapper.draw(patches8, "RIGHT");

        List<UniversityPatch> patches9 = new ArrayList<>();
        patches9.add(university.getPatch(9,9));
        BenchMapper.draw(patches9, "DOWN");

        List<UniversityPatch> patches10 = new ArrayList<>();
        patches10.add(university.getPatch(10,10));
        ProfTableMapper.draw(patches10, "LEFT");

        List<UniversityPatch> patches11 = new ArrayList<>();
        patches11.add(university.getPatch(11,11));
        ProfTableMapper.draw(patches11, "UP");

        List<UniversityPatch> patches12 = new ArrayList<>();
        patches12.add(university.getPatch(12,12));
        UniversityGateMapper.draw(patches12);

        List<UniversityPatch> patches13 = new ArrayList<>();
        patches13.add(university.getPatch(13,13));
        DoorMapper.draw(patches13, "RIGHT");

        List<UniversityPatch> patches14 = new ArrayList<>();
        patches14.add(university.getPatch(14,14));
        DoorMapper.draw(patches14, "DOWN");

        List<UniversityPatch> patches15 = new ArrayList<>();
        patches15.add(university.getPatch(15,15));
        BoardMapper.draw(patches15, "LEFT");

        List<UniversityPatch> patches16 = new ArrayList<>();
        patches16.add(university.getPatch(16,16));
        BoardMapper.draw(patches16, "UP");

        List<UniversityPatch> patches17 = new ArrayList<>();
        patches17.add(university.getPatch(17,17));
        BoardMapper.draw(patches17, "RIGHT");

        List<UniversityPatch> patches18 = new ArrayList<>();
        patches18.add(university.getPatch(18,18));
        BoardMapper.draw(patches18, "DOWN");

        List<UniversityPatch> patches19 = new ArrayList<>();
        patches19.add(university.getPatch(19,19));
        BulletinMapper.draw(patches19, "LEFT");

        List<UniversityPatch> patches20 = new ArrayList<>();
        patches20.add(university.getPatch(20,20));
        BulletinMapper.draw(patches20, "UP");

        List<UniversityPatch> patches21 = new ArrayList<>();
        patches21.add(university.getPatch(21,21));
        BulletinMapper.draw(patches21, "RIGHT");

        List<UniversityPatch> patches22 = new ArrayList<>();
        patches22.add(university.getPatch(22,22));
        BulletinMapper.draw(patches22, "DOWN");
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
        stackPane.setScaleX(1.0);
        stackPane.setScaleY(1.0);

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