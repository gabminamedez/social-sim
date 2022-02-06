package com.socialsim.controller.office.controls;

import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.Main;
import com.socialsim.controller.office.graphics.OfficeGraphicsController;
import com.socialsim.controller.office.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.agent.office.OfficeAgentMovement;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.office.OfficeSimulator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    @FXML private Tab parameters;
    @FXML private Button resetToDefaultButton;
    @FXML private TextField nonverbalMean;
    @FXML private TextField nonverbalStdDev;
    @FXML private TextField cooperativeMean;
    @FXML private TextField cooperativeStdDev;
    @FXML private TextField exchangeMean;
    @FXML private TextField exchangeStdDev;
    @FXML private TextField maxClients;
    @FXML private TextField maxDrivers;
    @FXML private TextField maxVisitors;
    @FXML private TextField maxCurrentClients;
    @FXML private TextField maxCurrentDrivers;
    @FXML private TextField maxCurrentVisitors;
    @FXML private TextField fieldOfView;
    @FXML private Button configureIOSButton;
    @FXML private Button editInteractionButton;

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
        resetToDefault();
        playButton.setDisable(true);

        int width = 60; // Value may be from 25-100
        int length = 100; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 100 columns
        Office office = Office.OfficeFactory.create(rows, columns);
        Main.officeSimulator.resetToDefaultConfiguration(office);
        Office.configureDefaultIOS();
        office.copyDefaultToIOS();
        Office.configureDefaultInteractionTypeChances();
        office.copyDefaultToInteractionTypeChances();

    }

    @FXML
    public void initializeAction() {
        if (Main.officeSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
        if (validateParameters()){
            Office office = Main.officeSimulator.getOffice();
            this.configureParameters(office);
            office.convertIOSToChances();
            initializeOffice(office);
            setElements();
            playButton.setDisable(false);
            disableEdits();
        }
    }

    public void initializeOffice(Office office) {
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
        for (int i = 4; i < 20; i++) {
            for (int j = 80; j < 100; j++) {
                meetingRoom1Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom1Patches, 1));

        List<Patch> meetingRoom2Patches = new ArrayList<>();
        for (int i = 22; i < 38; i++) {
            for (int j = 80; j < 100; j++) {
                meetingRoom2Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom2Patches, 2));

        List<Patch> meetingRoom3Patches = new ArrayList<>();
        for (int i = 40; i < 56; i++) {
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
        for (int i = 4; i < 57; i++) {
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
        chairPatches.add(office.getPatch(49,50)); // 3
        chairPatches.add(office.getPatch(55,60)); // 4
        chairPatches.add(office.getPatch(55,63)); // 5
        // meeting room chairs
        for (int i = 87; i < 95; i++) { //
            chairPatches.add(office.getPatch(9, i));// 6, 12, 18, 24, 30, 36, 42, 48
            chairPatches.add(office.getPatch(14, i));// 7, 13, 19, 25, 31, 37, 43, 49
            chairPatches.add(office.getPatch(27, i));// 8, 14, 20, 26, 32, 38, 44, 50
            chairPatches.add(office.getPatch(32, i));// 9, 15, 21, 27, 33, 39, 45, 51
            chairPatches.add(office.getPatch(45, i));// 10, 16, 22, 28, 34, 40, 46, 52
            chairPatches.add(office.getPatch(50, i));// 11, 17, 23, 29, 35, 41, 47, 53
        }//54, 58, 62
        chairPatches.add(office.getPatch(11,85));
        chairPatches.add(office.getPatch(12,85));
        chairPatches.add(office.getPatch(11,96));
        chairPatches.add(office.getPatch(12,96));

        chairPatches.add(office.getPatch(29,85));
        chairPatches.add(office.getPatch(30,85));
        chairPatches.add(office.getPatch(29,96));
        chairPatches.add(office.getPatch(30,96));

        chairPatches.add(office.getPatch(47,85));
        chairPatches.add(office.getPatch(48,85));
        chairPatches.add(office.getPatch(47,96));
        chairPatches.add(office.getPatch(48,96));
        // collaboration desk chairs
        for (int i = 64; i < 70; i++) {
            chairPatches.add(office.getPatch(7, i)); // 6, 16, 26, 36, 46, 56
            chairPatches.add(office.getPatch(10, i)); // 7
            chairPatches.add(office.getPatch(14, i)); // 8
            chairPatches.add(office.getPatch(17, i)); // 9
            chairPatches.add(office.getPatch(21, i)); // 10
            chairPatches.add(office.getPatch(24, i)); // 11
            chairPatches.add(office.getPatch(28, i)); // 12
            chairPatches.add(office.getPatch(31, i)); // 13
            chairPatches.add(office.getPatch(35, i)); // 14
            chairPatches.add(office.getPatch(38, i)); // 15
        }
        // Break room chairs
        /*chairPatches.add(office.getPatch(32,1)); chairPatches.add(office.getPatch(32,3));
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
        chairPatches.add(office.getPatch(37,13)); chairPatches.add(office.getPatch(37,15));*/
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
        doorRightPatches.add(office.getPatch(6,19)); // 0
        doorRightPatches.add(office.getPatch(27,19)); // 1
        DoorMapper.draw(doorRightPatches, "RIGHT");

        List<Patch> doorLeftPatches = new ArrayList<>();
        doorLeftPatches.add(office.getPatch(10,80)); // 2
        doorLeftPatches.add(office.getPatch(28,80)); // 3
        doorLeftPatches.add(office.getPatch(46,80)); // 4
        DoorMapper.draw(doorLeftPatches, "LEFT");

        List<Patch> doorUpPatches = new ArrayList<>();
        doorUpPatches.add(office.getPatch(45,59)); // 5
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
        //tableUpPatches.add(office.getPatch(21,9));
        tableUpPatches.add(office.getPatch(23,9));
        //tableUpPatches.add(office.getPatch(23,9));
        tableUpPatches.add(office.getPatch(23,13));
        //tableUpPatches.add(office.getPatch(21,11));
        tableUpPatches.add(office.getPatch(26,9));
        //tableUpPatches.add(office.getPatch(23,11));
        tableUpPatches.add(office.getPatch(26,13)); // couch tables
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
        Main.officeSimulator.reset();
        Main.officeSimulator.replenishSeats();
        OfficeAgent.clearOfficeAgentCounts();
        clearOffice(Main.officeSimulator.getOffice());
        Main.officeSimulator.spawnInitialAgents(Main.officeSimulator.getOffice());
        drawOfficeViewForeground(Main.officeSimulator.getOffice(), false); // Redraw the canvas
        if (Main.officeSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
        enableEdits();
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

    public void disableEdits(){
        nonverbalMean.setDisable(true);
        nonverbalStdDev.setDisable(true);
        cooperativeMean.setDisable(true);
        cooperativeStdDev.setDisable(true);
        exchangeMean.setDisable(true);
        exchangeStdDev.setDisable(true);
        fieldOfView.setDisable(true);
        maxClients.setDisable(true);
        maxDrivers.setDisable(true);
        maxVisitors.setDisable(true);

        resetToDefaultButton.setDisable(true);
        configureIOSButton.setDisable(true);
        editInteractionButton.setDisable(true);
    }
    public void enableEdits(){
        nonverbalMean.setDisable(false);
        nonverbalStdDev.setDisable(false);
        cooperativeMean.setDisable(false);
        cooperativeStdDev.setDisable(false);
        exchangeMean.setDisable(false);
        exchangeStdDev.setDisable(false);
        fieldOfView.setDisable(false);
        maxClients.setDisable(false);
        maxDrivers.setDisable(false);
        maxVisitors.setDisable(false);

        resetToDefaultButton.setDisable(false);
        configureIOSButton.setDisable(false);
        editInteractionButton.setDisable(false);
    }

    public void resetToDefault(){
        nonverbalMean.setText(Integer.toString(OfficeAgentMovement.defaultNonverbalMean));
        nonverbalStdDev.setText(Integer.toString(OfficeAgentMovement.defaultNonverbalStdDev));
        cooperativeMean.setText(Integer.toString(OfficeAgentMovement.defaultCooperativeMean));
        cooperativeStdDev.setText(Integer.toString(OfficeAgentMovement.defaultCooperativeStdDev));
        exchangeMean.setText(Integer.toString(OfficeAgentMovement.defaultExchangeMean));
        exchangeStdDev.setText(Integer.toString(OfficeAgentMovement.defaultExchangeStdDev));
        fieldOfView.setText(Integer.toString(OfficeAgentMovement.defaultFieldOfView));
        maxClients.setText(Integer.toString(OfficeSimulator.defaultMaxClients));
        maxDrivers.setText(Integer.toString(OfficeSimulator.defaultMaxDrivers));
        maxVisitors.setText(Integer.toString(OfficeSimulator.defaultMaxVisitors));
    }

    public void openIOSLevels(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/OfficeConfigureIOS.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Configure IOS Levels");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void openEditInteractions(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/OfficeEditInteractions.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Interaction Type Chances");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void configureParameters(Office office){
        office.setNonverbalMean(Integer.parseInt(nonverbalMean.getText()));
        office.setNonverbalStdDev(Integer.parseInt(nonverbalStdDev.getText()));
        office.setCooperativeMean(Integer.parseInt(cooperativeMean.getText()));
        office.setCooperativeStdDev(Integer.parseInt(cooperativeStdDev.getText()));
        office.setExchangeMean(Integer.parseInt(exchangeMean.getText()));
        office.setExchangeStdDev(Integer.parseInt(exchangeStdDev.getText()));
        office.setFieldOfView(Integer.parseInt(fieldOfView.getText()));
        office.setMAX_CLIENTS(Integer.parseInt(maxClients.getText()));
        office.setMAX_CURRENT_CLIENTS(Integer.parseInt(maxCurrentClients.getText()));
        office.setMAX_DRIVERS(Integer.parseInt(maxDrivers.getText()));
        office.setMAX_CURRENT_DRIVERS(Integer.parseInt(maxCurrentDrivers.getText()));
        office.setMAX_VISITORS(Integer.parseInt(maxVisitors.getText()));
        office.setMAX_CURRENT_VISITORS(Integer.parseInt(maxCurrentVisitors.getText()));
    }

    public boolean validateParameters(){
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxClients.getText()) >= 0 && Integer.parseInt(maxDrivers.getText()) >= 0
                && Integer.parseInt(maxVisitors.getText()) >= 0;
        if (!validParameters){
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            Label label = new Label("Failed to initialize. Please make sure all values are greater than 0, and field of view is not greater than 360 degrees");
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }
        return validParameters;
    }
    public void generateHeatMap(){

    }
}