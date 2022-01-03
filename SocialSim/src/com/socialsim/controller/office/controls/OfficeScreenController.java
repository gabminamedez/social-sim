package com.socialsim.controller.office.controls;

import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.Main;
import com.socialsim.controller.office.graphics.OfficeGraphicsController;
import com.socialsim.controller.office.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.simulator.SimulationTime;
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

public class OfficeScreenController extends ScreenController {

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
    @FXML private Text elapsedTimeText;
    @FXML private Button initializeButton;
    @FXML private ToggleButton playButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;

    private final double CANVAS_SCALE = 0.5;

    public OfficeScreenController() {
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    @FXML
    private void initialize() {
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SimulationTime.SLEEP_TIME_MILLISECONDS.set((int) (1.0 / newVal.intValue() * 1000));
        });
    }

    @FXML
    public void initializeAction() {
        if (Main.officeSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }

        int width = 60; // Value may be from 25-100
        int length = 100; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 100 columns
        Office office = Office.OfficeFactory.create(rows, columns);
        initializeOffice(office);
        setElements();
    }

    public void initializeOffice(Office office) {
        Main.officeSimulator.resetToDefaultConfiguration(office);
        OfficeGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.officeSimulator.getOffice().getRows();
        mapOffice();
        Main.officeSimulator.spawnInitialAgents(office);
        drawInterface();
    }

    public void mapOffice() {
        Office office = Main.officeSimulator.getOffice();
        int rows = office.getRows();
        int cols = office.getColumns();

        List<Patch> wallPatches = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 20; j++) {
                wallPatches.add(office.getPatch(i, j));
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 80; j < 100; j++) {
                wallPatches.add(office.getPatch(i, j));
            }
        }
        for (int i = 45; i < 60; i++) {
            for (int j = 45; j < 75; j++) {
                wallPatches.add(office.getPatch(i, j));
            }
        }
        for (int i = 41; i < 45; i++) {
            for (int j = 35; j < 50; j++) {
                wallPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> bathroomPatches = new ArrayList<>();
        for (int i = 2; i < 12; i++) {
            for (int j = 10; j < 20; j++) {
                bathroomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getBathrooms().add(Bathroom.bathroomFactory.create(bathroomPatches, 1));

        List<Patch> breakroomPatches = new ArrayList<>();
        for (int i = 15; i < 41; i++) {
            for (int j = 0; j < 20; j++) {
                breakroomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getBreakrooms().add(Breakroom.breakroomFactory.create(breakroomPatches, 1));

        List<Patch> meetingRoom1Patches = new ArrayList<>();
        for (int i = 6; i < 18; i++) {
            for (int j = 80; j < 100; j++) {
                meetingRoom1Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom1Patches, 1));

        List<Patch> meetingRoom2Patches = new ArrayList<>();
        for (int i = 24; i < 36; i++) {
            for (int j = 80; j < 100; j++) {
                meetingRoom2Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom2Patches, 2));

        List<Patch> meetingRoom3Patches = new ArrayList<>();
        for (int i = 42; i < 54; i++) {
            for (int j = 80; j < 100; j++) {
                meetingRoom3Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom3Patches, 3));

        List<Patch> officeRoomPatches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 50; j < 70; j++) {
                officeRoomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getOfficeRooms().add(OfficeRoom.officeRoomFactory.create(officeRoomPatches, 1));

        List<Patch> receptionPatches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 0; j < 45; j++) {
                receptionPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getReceptions().add(Reception.receptionFactory.create(receptionPatches, 1));

        List<Patch> wall2Patches = new ArrayList<>();
        for (int i = 15; i < 42; i++) {
            if (i != 27 && i != 28) {
                wall2Patches.add(office.getPatch(i, 19));
            }
        }
        for (int j = 50; j < 70; j++) {
            if (j != 59 && j != 60) {
                wall2Patches.add(office.getPatch(45, j));
            }
        }
        for (int i = 6; i < 55; i++) {
            if (i != 11 && i != 12 && i != 29 && i != 30 && i != 47 && i != 48) {
                wall2Patches.add(office.getPatch(i, 80));
            }
        }
        for (int i = 2; i < 6; i++) {
            wall2Patches.add(office.getPatch(i, 19));
        }
        for (int i = 8; i < 12; i++) {
            wall2Patches.add(office.getPatch(i, 19));
        }
        Main.officeSimulator.getOffice().getWalls().add(Wall.wallFactory.create(wall2Patches, 1));

        List<Patch> cabinetDownPatches = new ArrayList<>();
        cabinetDownPatches.add(office.getPatch(0,74));
        CabinetMapper.draw(cabinetDownPatches, "DOWN");

        List<Patch> cabinetUpPatches = new ArrayList<>();
        cabinetUpPatches.add(office.getPatch(39,45));
        cabinetUpPatches.add(office.getPatch(39,47));
        cabinetUpPatches.add(office.getPatch(58,51));
        cabinetUpPatches.add(office.getPatch(58,54));
        cabinetUpPatches.add(office.getPatch(58,64));
        cabinetUpPatches.add(office.getPatch(58,67));
        CabinetMapper.draw(cabinetUpPatches, "UP");

        List<Patch> chairPatches = new ArrayList<>();
        chairPatches.add(office.getPatch(51,58)); // 0
        chairPatches.add(office.getPatch(51,61)); // 1
        chairPatches.add(office.getPatch(46,37));  // 2
        for (int i = 87; i < 95; i++) { // 3-56
            chairPatches.add(office.getPatch(9, i));
            chairPatches.add(office.getPatch(14, i));
            chairPatches.add(office.getPatch(27, i));
            chairPatches.add(office.getPatch(32, i));
            chairPatches.add(office.getPatch(45, i));
            chairPatches.add(office.getPatch(50, i));
        }
        chairPatches.add(office.getPatch(11,85)); // 57
        chairPatches.add(office.getPatch(12,85)); // 58
        chairPatches.add(office.getPatch(11,96)); // 59
        chairPatches.add(office.getPatch(12,96)); // 60
        chairPatches.add(office.getPatch(29,85)); // 61
        chairPatches.add(office.getPatch(30,85)); // 62
        chairPatches.add(office.getPatch(29,96)); // 63
        chairPatches.add(office.getPatch(30,96)); // 64
        chairPatches.add(office.getPatch(47,85)); // 65
        chairPatches.add(office.getPatch(48,85)); // 66
        chairPatches.add(office.getPatch(47,96)); // 67
        chairPatches.add(office.getPatch(48,96)); // 68
        chairPatches.add(office.getPatch(55,60)); // 69
        chairPatches.add(office.getPatch(55,63)); // 70
        for (int i = 64; i < 70; i++) {
            chairPatches.add(office.getPatch(7, i));
            chairPatches.add(office.getPatch(10, i));
            chairPatches.add(office.getPatch(14, i));
            chairPatches.add(office.getPatch(17, i));
            chairPatches.add(office.getPatch(21, i));
            chairPatches.add(office.getPatch(24, i));
            chairPatches.add(office.getPatch(28, i));
            chairPatches.add(office.getPatch(31, i));
            chairPatches.add(office.getPatch(35, i));
            chairPatches.add(office.getPatch(38, i));
        }
        chairPatches.add(office.getPatch(32,1)); chairPatches.add(office.getPatch(32,3));
        chairPatches.add(office.getPatch(33,1)); chairPatches.add(office.getPatch(33,3));
        chairPatches.add(office.getPatch(32,5)); chairPatches.add(office.getPatch(32,7));
        chairPatches.add(office.getPatch(33,5)); chairPatches.add(office.getPatch(33,7));
        chairPatches.add(office.getPatch(32,9)); chairPatches.add(office.getPatch(32,11));
        chairPatches.add(office.getPatch(33,9)); chairPatches.add(office.getPatch(33,11));
        chairPatches.add(office.getPatch(32,13)); chairPatches.add(office.getPatch(32,15));
        chairPatches.add(office.getPatch(33,13)); chairPatches.add(office.getPatch(33,15));
        chairPatches.add(office.getPatch(36,1)); chairPatches.add(office.getPatch(36,3));
        chairPatches.add(office.getPatch(37,1)); chairPatches.add(office.getPatch(37,3));
        chairPatches.add(office.getPatch(36,5)); chairPatches.add(office.getPatch(36,7));
        chairPatches.add(office.getPatch(37,5)); chairPatches.add(office.getPatch(37,7));
        chairPatches.add(office.getPatch(36,9)); chairPatches.add(office.getPatch(36,11));
        chairPatches.add(office.getPatch(37,9)); chairPatches.add(office.getPatch(37,11));
        chairPatches.add(office.getPatch(36,13)); chairPatches.add(office.getPatch(36,15));
        chairPatches.add(office.getPatch(37,13)); chairPatches.add(office.getPatch(37,15));
        ChairMapper.draw(chairPatches);

        List<Patch> collabDeskPatches = new ArrayList<>();
        collabDeskPatches.add(office.getPatch(8,64));
        collabDeskPatches.add(office.getPatch(15,64));
        collabDeskPatches.add(office.getPatch(22,64));
        collabDeskPatches.add(office.getPatch(29,64));
        collabDeskPatches.add(office.getPatch(36,64));
        CollabDeskMapper.draw(collabDeskPatches);

        List<Patch> couchDownPatches = new ArrayList<>();
        couchDownPatches.add(office.getPatch(17,7));
        couchDownPatches.add(office.getPatch(47,7));
        CouchMapper.draw(couchDownPatches, "DOWN");

        List<Patch> couchRightPatches = new ArrayList<>();
        couchRightPatches.add(office.getPatch(19,3));
        couchRightPatches.add(office.getPatch(49,3));
        CouchMapper.draw(couchRightPatches, "RIGHT");

        List<Patch> cubicleUpPatches = new ArrayList<>();
        cubicleUpPatches.add(office.getPatch(0,26));
        cubicleUpPatches.add(office.getPatch(0,30));
        cubicleUpPatches.add(office.getPatch(0,34));
        cubicleUpPatches.add(office.getPatch(0,38));
        cubicleUpPatches.add(office.getPatch(0,42));
        cubicleUpPatches.add(office.getPatch(0,46));
        cubicleUpPatches.add(office.getPatch(0,50));
        cubicleUpPatches.add(office.getPatch(0,54));
        cubicleUpPatches.add(office.getPatch(0,58));
        cubicleUpPatches.add(office.getPatch(0,62));
        cubicleUpPatches.add(office.getPatch(0,66));
        cubicleUpPatches.add(office.getPatch(0,70));
        cubicleUpPatches.add(office.getPatch(9,26));
        cubicleUpPatches.add(office.getPatch(9,30));
        cubicleUpPatches.add(office.getPatch(9,34));
        cubicleUpPatches.add(office.getPatch(9,38));
        cubicleUpPatches.add(office.getPatch(9,42));
        cubicleUpPatches.add(office.getPatch(9,46));
        cubicleUpPatches.add(office.getPatch(9,50));
        cubicleUpPatches.add(office.getPatch(9,54));
        cubicleUpPatches.add(office.getPatch(19,26));
        cubicleUpPatches.add(office.getPatch(19,30));
        cubicleUpPatches.add(office.getPatch(19,34));
        cubicleUpPatches.add(office.getPatch(19,38));
        cubicleUpPatches.add(office.getPatch(19,42));
        cubicleUpPatches.add(office.getPatch(19,46));
        cubicleUpPatches.add(office.getPatch(19,50));
        cubicleUpPatches.add(office.getPatch(19,54));
        cubicleUpPatches.add(office.getPatch(29,26));
        cubicleUpPatches.add(office.getPatch(29,30));
        cubicleUpPatches.add(office.getPatch(29,34));
        cubicleUpPatches.add(office.getPatch(29,38));
        cubicleUpPatches.add(office.getPatch(29,42));
        cubicleUpPatches.add(office.getPatch(29,46));
        cubicleUpPatches.add(office.getPatch(29,50));
        cubicleUpPatches.add(office.getPatch(29,54));
        CubicleMapper.draw(cubicleUpPatches, "UP");

        List<Patch> cubicleDownPatches = new ArrayList<>();
        cubicleDownPatches.add(office.getPatch(6,26));
        cubicleDownPatches.add(office.getPatch(6,30));
        cubicleDownPatches.add(office.getPatch(6,34));
        cubicleDownPatches.add(office.getPatch(6,38));
        cubicleDownPatches.add(office.getPatch(6,42));
        cubicleDownPatches.add(office.getPatch(6,46));
        cubicleDownPatches.add(office.getPatch(6,50));
        cubicleDownPatches.add(office.getPatch(6,54));
        cubicleDownPatches.add(office.getPatch(16,26));
        cubicleDownPatches.add(office.getPatch(16,30));
        cubicleDownPatches.add(office.getPatch(16,34));
        cubicleDownPatches.add(office.getPatch(16,38));
        cubicleDownPatches.add(office.getPatch(16,42));
        cubicleDownPatches.add(office.getPatch(16,46));
        cubicleDownPatches.add(office.getPatch(16,50));
        cubicleDownPatches.add(office.getPatch(16,54));
        cubicleDownPatches.add(office.getPatch(26,26));
        cubicleDownPatches.add(office.getPatch(26,30));
        cubicleDownPatches.add(office.getPatch(26,34));
        cubicleDownPatches.add(office.getPatch(26,38));
        cubicleDownPatches.add(office.getPatch(26,42));
        cubicleDownPatches.add(office.getPatch(26,46));
        cubicleDownPatches.add(office.getPatch(26,50));
        cubicleDownPatches.add(office.getPatch(26,54));
        CubicleMapper.draw(cubicleDownPatches, "DOWN");

        List<Patch> doorRightPatches = new ArrayList<>();
        doorRightPatches.add(office.getPatch(6,19));
        doorRightPatches.add(office.getPatch(27,19));
        DoorMapper.draw(doorRightPatches, "RIGHT");

        List<Patch> doorLeftPatches = new ArrayList<>();
        doorLeftPatches.add(office.getPatch(11,80));
        doorLeftPatches.add(office.getPatch(29,80));
        doorLeftPatches.add(office.getPatch(47,80));
        DoorMapper.draw(doorLeftPatches, "LEFT");

        List<Patch> doorUpPatches = new ArrayList<>();
        doorUpPatches.add(office.getPatch(45,59));
        DoorMapper.draw(doorUpPatches, "UP");

        List<Patch> meetingDeskPatches = new ArrayList<>();
        meetingDeskPatches.add(office.getPatch(10,86));
        meetingDeskPatches.add(office.getPatch(12,86));
        meetingDeskPatches.add(office.getPatch(28,86));
        meetingDeskPatches.add(office.getPatch(30,86));
        meetingDeskPatches.add(office.getPatch(46,86));
        meetingDeskPatches.add(office.getPatch(48,86));
        MeetingDeskMapper.draw(meetingDeskPatches);

        List<Patch> officeDeskPatches = new ArrayList<>();
        officeDeskPatches.add(office.getPatch(54,57));
        OfficeDeskMapper.draw(officeDeskPatches);

        List<Patch> officeGateExitPatches = new ArrayList<>();
        officeGateExitPatches.add(office.getPatch(59,27));
        OfficeGateMapper.draw(officeGateExitPatches, OfficeGate.OfficeGateMode.EXIT);

        List<Patch> officeGateEntrancePatches = new ArrayList<>();
        officeGateEntrancePatches.add(office.getPatch(59,35));
        OfficeGateMapper.draw(officeGateEntrancePatches, OfficeGate.OfficeGateMode.ENTRANCE);

        List<Patch> plantPatches = new ArrayList<>();
        plantPatches.add(office.getPatch(0,20));
        plantPatches.add(office.getPatch(40,38));
        plantPatches.add(office.getPatch(40,43));
        plantPatches.add(office.getPatch(0,79));
        plantPatches.add(office.getPatch(44,73));
        plantPatches.add(office.getPatch(20,79));
        plantPatches.add(office.getPatch(21,79));
        plantPatches.add(office.getPatch(38,79));
        plantPatches.add(office.getPatch(39,79));
        PlantMapper.draw(plantPatches);

        List<Patch> printerPatches = new ArrayList<>();
        printerPatches.add(office.getPatch(40,40));
        PrinterMapper.draw(printerPatches);

        List<Patch> receptionTablePatches = new ArrayList<>();
        receptionTablePatches.add(office.getPatch(47,35));
        ReceptionTableMapper.draw(receptionTablePatches);

        List<Patch> tableUpPatches = new ArrayList<>();
        tableUpPatches.add(office.getPatch(21,9));
        tableUpPatches.add(office.getPatch(22,9));
        tableUpPatches.add(office.getPatch(23,9));
        tableUpPatches.add(office.getPatch(24,9));
        tableUpPatches.add(office.getPatch(21,11));
        tableUpPatches.add(office.getPatch(22,11));
        tableUpPatches.add(office.getPatch(23,11));
        tableUpPatches.add(office.getPatch(24,11));
        tableUpPatches.add(office.getPatch(51,9));
        tableUpPatches.add(office.getPatch(52,9));
        tableUpPatches.add(office.getPatch(53,9));
        tableUpPatches.add(office.getPatch(54,9));
        tableUpPatches.add(office.getPatch(51,11));
        tableUpPatches.add(office.getPatch(52,11));
        tableUpPatches.add(office.getPatch(53,11));
        tableUpPatches.add(office.getPatch(54,11));
        TableMapper.draw(tableUpPatches, "UP");

        List<Patch> tableRightPatches = new ArrayList<>();
        tableRightPatches.add(office.getPatch(32,2));
        tableRightPatches.add(office.getPatch(32,6));
        tableRightPatches.add(office.getPatch(32,10));
        tableRightPatches.add(office.getPatch(32,14));
        tableRightPatches.add(office.getPatch(36,2));
        tableRightPatches.add(office.getPatch(36,6));
        tableRightPatches.add(office.getPatch(36,10));
        tableRightPatches.add(office.getPatch(36,14));
        TableMapper.draw(tableRightPatches, "RIGHT");

        List<Patch> toiletPatches = new ArrayList<>();
        toiletPatches.add(office.getPatch(2,10));
        toiletPatches.add(office.getPatch(2,13));
        toiletPatches.add(office.getPatch(2,16));
        ToiletMapper.draw(toiletPatches);

        List<Patch> sinkPatches = new ArrayList<>();
        sinkPatches.add(office.getPatch(11,10));
        sinkPatches.add(office.getPatch(11,13));
        sinkPatches.add(office.getPatch(11,16));
        SinkMapper.draw(sinkPatches);

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(office.getPatch(56,36));
        SecurityMapper.draw(securityPatches);
    }

    private void drawInterface() {
        drawOfficeViewBackground(Main.officeSimulator.getOffice()); // Initially draw the Office environment
        drawOfficeViewForeground(Main.officeSimulator.getOffice(), false); // Then draw the agents in the Office
    }

    public void drawOfficeViewBackground(Office office) { // Draw the office view background
        OfficeGraphicsController.requestDrawOfficeView(stackPane, office, OfficeGraphicsController.tileSize, true, false);
    }

    public void drawOfficeViewForeground(Office office, boolean speedAware) { // Draw the office view foreground
        OfficeGraphicsController.requestDrawOfficeView(stackPane, office, OfficeGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.officeSimulator.getSimulationTime().getTime();
        long elapsedTime = Main.officeSimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS) / 5;
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.officeSimulator.getOffice().getRows() * OfficeGraphicsController.tileSize;
        double columnsScaled = Main.officeSimulator.getOffice().getColumns() * OfficeGraphicsController.tileSize;

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
        if (!Main.officeSimulator.isRunning()) { // Not yet running to running (play simulation)
            Main.officeSimulator.setRunning(true);
            Main.officeSimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.officeSimulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        initializeAction();
        Main.officeSimulator.reset();
        clearOffice(Main.officeSimulator.getOffice());
        Main.officeSimulator.spawnInitialAgents(Main.officeSimulator.getOffice());
        drawOfficeViewForeground(Main.officeSimulator.getOffice(), false); // Redraw the canvas
        if (Main.officeSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    public static void clearOffice(Office office) {
        for (OfficeAgent agent : office.getAgents()) { // Remove the relationship between the patch and the agents
            agent.getAgentMovement().getCurrentPatch().getAgents().clear();
            agent.getAgentMovement().setCurrentPatch(null);
        }

        // Remove all the agents
        office.getAgents().removeAll(office.getAgents());
        office.getAgents().clear();
        office.getAgentPatchSet().clear();
    }

    @Override
    protected void closeAction() {
    }

}