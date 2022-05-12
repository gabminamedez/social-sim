package com.socialsim.controller.office.controls;

import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.Main;
import com.socialsim.controller.office.graphics.OfficeGraphicsController;
import com.socialsim.controller.office.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.office.OfficeAgentMovement;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.office.OfficeSimulator;
import com.socialsim.model.simulator.university.UniversitySimulator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    @FXML private StackPane stackPane;
    @FXML private Canvas backgroundCanvas;
    @FXML private Canvas foregroundCanvas;
    @FXML private Canvas markingsCanvas;
    @FXML private Text elapsedTimeText;
    @FXML private ToggleButton playButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;
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
    @FXML private Label currentManagerCount;
    @FXML private Label currentBusinessCount;
    @FXML private Label currentResearchCount;
    @FXML private Label currentTechnicalCount;
    @FXML private Label currentSecretaryCount;
    @FXML private Label currentClientCount;
    @FXML private Label currentDriverCount;
    @FXML private Label currentVisitorCount;
    @FXML private Label currentNonverbalCount;
    @FXML private Label currentCooperativeCount;
    @FXML private Label currentExchangeCount;
    @FXML private Label averageNonverbalDuration;
    @FXML private Label averageCooperativeDuration;
    @FXML private Label averageExchangeDuration;
    @FXML private Label currentTeam1Count;
    @FXML private Label currentTeam2Count;
    @FXML private Label currentTeam3Count;
    @FXML private Label currentTeam4Count;
    @FXML private Label currentBossManagerCount;
    @FXML private Label currentBossBusinessCount;
    @FXML private Label currentBossResearcherCount;
    @FXML private Label currentBossTechnicalCount;
    @FXML private Label currentBossJanitorCount;
    @FXML private Label currentBossClientCount;
    @FXML private Label currentBossDriverCount;
    @FXML private Label currentBossVisitorCount;
    @FXML private Label currentBossGuardCount;
    @FXML private Label currentBossReceptionistCount;
    @FXML private Label currentBossSecretaryCount;
    @FXML private Label currentManagerManagerCount;
    @FXML private Label currentManagerBusinessCount;
    @FXML private Label currentManagerResearcherCount;
    @FXML private Label currentManagerTechnicalCount;
    @FXML private Label currentManagerJanitorCount;
    @FXML private Label currentManagerClientCount;
    @FXML private Label currentManagerDriverCount;
    @FXML private Label currentManagerVisitorCount;
    @FXML private Label currentManagerGuardCount;
    @FXML private Label currentManagerReceptionistCount;
    @FXML private Label currentManagerSecretaryCount;
    @FXML private Label currentBusinessBusinessCount;
    @FXML private Label currentBusinessResearcherCount;
    @FXML private Label currentBusinessTechnicalCount;
    @FXML private Label currentBusinessJanitorCount;
    @FXML private Label currentBusinessClientCount;
    @FXML private Label currentBusinessDriverCount;
    @FXML private Label currentBusinessVisitorCount;
    @FXML private Label currentBusinessGuardCount;
    @FXML private Label currentBusinessReceptionistCount;
    @FXML private Label currentBusinessSecretaryCount;
    @FXML private Label currentResearcherResearcherCount;
    @FXML private Label currentResearcherTechnicalCount;
    @FXML private Label currentResearcherJanitorCount;
    @FXML private Label currentResearcherClientCount;
    @FXML private Label currentResearcherDriverCount;
    @FXML private Label currentResearcherVisitorCount;
    @FXML private Label currentResearcherGuardCount;
    @FXML private Label currentResearcherReceptionistCount;
    @FXML private Label currentResearcherSecretaryCount;
    @FXML private Label currentTechnicalTechnicalCount;
    @FXML private Label currentTechnicalJanitorCount;
    @FXML private Label currentTechnicalClientCount;
    @FXML private Label currentTechnicalDriverCount;
    @FXML private Label currentTechnicalVisitorCount;
    @FXML private Label currentTechnicalGuardCount;
    @FXML private Label currentTechnicalReceptionistCount;
    @FXML private Label currentTechnicalSecretaryCount;
    @FXML private Label currentJanitorJanitorCount;
    @FXML private Label currentJanitorClientCount;
    @FXML private Label currentJanitorDriverCount;
    @FXML private Label currentJanitorVisitorCount;
    @FXML private Label currentJanitorSecretaryCount;
    @FXML private Label currentClientClientCount;
    @FXML private Label currentClientDriverCount;
    @FXML private Label currentClientVisitorCount;
    @FXML private Label currentClientGuardCount;
    @FXML private Label currentClientReceptionistCount;
    @FXML private Label currentClientSecretaryCount;
    @FXML private Label currentDriverDriverCount;
    @FXML private Label currentDriverVisitorCount;
    @FXML private Label currentDriverGuardCount;
    @FXML private Label currentDriverReceptionistCount;
    @FXML private Label currentDriverSecretaryCount;
    @FXML private Label currentVisitorVisitorCount;
    @FXML private Label currentVisitorGuardCount;
    @FXML private Label currentVisitorReceptionistCount;
    @FXML private Label currentVisitorSecretaryCount;
    @FXML private Label currentGuardSecretaryCount;
    @FXML private Label currentReceptionistSecretaryCount;
    @FXML private Label currentSecretarySecretaryCount;
    @FXML private Button exportToCSVButton;
    @FXML private Button exportHeatMapButton;

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
        exportToCSVButton.setDisable(true);
        exportHeatMapButton.setDisable(true);

        int width = 60;
        int length = 100;
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        Office office = Office.OfficeFactory.create(rows, columns);
        Main.officeSimulator.resetToDefaultConfiguration(office);
        Office.configureDefaultIOS();
        office.copyDefaultToIOS();
        Office.configureDefaultInteractionTypeChances();
        office.copyDefaultToInteractionTypeChances();
    }

    @FXML
    public void initializeAction() {
        if (Main.officeSimulator.isRunning()) {
            playAction();
            playButton.setSelected(false);
        }
        if (validateParameters()) {
            Office office = Main.officeSimulator.getOffice();
            this.configureParameters(office);
            initializeOffice(office);
            office.convertIOSToChances();
            setElements();
            playButton.setDisable(false);
            exportToCSVButton.setDisable(true);
            exportHeatMapButton.setDisable(true);
            Main.officeSimulator.replenishStaticVars();
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
            for (int j = 35; j < 71; j++) {
                wallPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 3; i < 9; i++) {
            for (int j = 8; j < 19; j++) {
                fBathroomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getBathrooms().add(Bathroom.bathroomFactory.create(fBathroomPatches, 1));

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 12; i < 18; i++) {
            for (int j = 8; j < 19; j++) {
                mBathroomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> breakroomPatches = new ArrayList<>();
        for (int i = 21; i < 44; i++) {
            for (int j = 0; j < 19; j++) {
                breakroomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getBreakrooms().add(Breakroom.breakroomFactory.create(breakroomPatches, 1));

        List<Patch> meetingRoom1Patches = new ArrayList<>();
        for (int i = 4; i < 20; i++) {
            for (int j = 81; j < 100; j++) {
                meetingRoom1Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom1Patches, 1));

        List<Patch> meetingRoom2Patches = new ArrayList<>();
        for (int i = 22; i < 38; i++) {
            for (int j = 81; j < 100; j++) {
                meetingRoom2Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom2Patches, 2));

        List<Patch> meetingRoom3Patches = new ArrayList<>();
        for (int i = 40; i < 56; i++) {
            for (int j = 81; j < 100; j++) {
                meetingRoom3Patches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getMeetingRooms().add(MeetingRoom.meetingRoomFactory.create(meetingRoom3Patches, 3));

        List<Patch> officeRoomPatches = new ArrayList<>();
        for (int i = 46; i < 60; i++) {
            for (int j = 37; j < 69; j++) {
                officeRoomPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getOfficeRooms().add(OfficeRoom.officeRoomFactory.create(officeRoomPatches, 1));

        List<Patch> receptionPatches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 0; j < 35; j++) {
                receptionPatches.add(office.getPatch(i, j));
            }
        }
        Main.officeSimulator.getOffice().getReceptions().add(Reception.receptionFactory.create(receptionPatches, 1));

        List<Patch> cabinetDownPatches = new ArrayList<>();
        cabinetDownPatches.add(office.getPatch(0,74));
        CabinetMapper.draw(cabinetDownPatches, "DOWN");

        List<Patch> cabinetUpPatches = new ArrayList<>();
        cabinetUpPatches.add(office.getPatch(43,44));
        cabinetUpPatches.add(office.getPatch(43,46));
        cabinetUpPatches.add(office.getPatch(58,39));
        cabinetUpPatches.add(office.getPatch(58,42));
        cabinetUpPatches.add(office.getPatch(58,50));
        cabinetUpPatches.add(office.getPatch(58,53));
        CabinetMapper.draw(cabinetUpPatches, "UP");

        List<Patch> chairPatches = new ArrayList<>();
        chairPatches.add(office.getPatch(49,45));
        chairPatches.add(office.getPatch(49,48));
        chairPatches.add(office.getPatch(46,31));
        chairPatches.add(office.getPatch(49,65));
        chairPatches.add(office.getPatch(53,47));
        chairPatches.add(office.getPatch(53,50));
        for (int i = 87; i < 95; i++) {
            chairPatches.add(office.getPatch(9, i));
            chairPatches.add(office.getPatch(14, i));
            chairPatches.add(office.getPatch(27, i));
            chairPatches.add(office.getPatch(32, i));
            chairPatches.add(office.getPatch(45, i));
            chairPatches.add(office.getPatch(50, i));
        }
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
        ChairMapper.draw(chairPatches);

        List<Patch> collabDeskPatches = new ArrayList<>();
        collabDeskPatches.add(office.getPatch(8,64));
        collabDeskPatches.add(office.getPatch(15,64));
        collabDeskPatches.add(office.getPatch(22,64));
        collabDeskPatches.add(office.getPatch(29,64));
        collabDeskPatches.add(office.getPatch(36,64));
        CollabDeskMapper.draw(collabDeskPatches);

        List<Patch> couchDownPatches = new ArrayList<>();
        couchDownPatches.add(office.getPatch(22,7));
        couchDownPatches.add(office.getPatch(47,7));
        CouchMapper.draw(couchDownPatches, "DOWN");

        List<Patch> couchRightPatches = new ArrayList<>();
        couchRightPatches.add(office.getPatch(49,3));
        couchRightPatches.add(office.getPatch(46,56));
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

        List<Patch> whiteboardPatches = new ArrayList<>();
        whiteboardPatches.add(office.getPatch(9,99));
        whiteboardPatches.add(office.getPatch(11,99));
        whiteboardPatches.add(office.getPatch(13,99));

        whiteboardPatches.add(office.getPatch(27,99));
        whiteboardPatches.add(office.getPatch(29,99));
        whiteboardPatches.add(office.getPatch(31,99));

        whiteboardPatches.add(office.getPatch(45,99));
        whiteboardPatches.add(office.getPatch(47,99));
        whiteboardPatches.add(office.getPatch(49,99));
        WhiteboardMapper.draw(whiteboardPatches);

        List<Patch> doorRightPatches = new ArrayList<>();
        doorRightPatches.add(office.getPatch(4,19));
        doorRightPatches.add(office.getPatch(13,19));
        doorRightPatches.add(office.getPatch(27,19));
        DoorMapper.draw(doorRightPatches, "RIGHT");

        List<Patch> doorLeftPatches = new ArrayList<>();
        doorLeftPatches.add(office.getPatch(10,80));
        doorLeftPatches.add(office.getPatch(28,80));
        doorLeftPatches.add(office.getPatch(46,80));
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
        officeDeskPatches.add(office.getPatch(52,44));
        officeDeskPatches.add(office.getPatch(50,63));
        OfficeDeskMapper.draw(officeDeskPatches);

        List<Patch> officeGateExitPatches = new ArrayList<>();
        officeGateExitPatches.add(office.getPatch(59,17));
        OfficeGateMapper.draw(officeGateExitPatches, OfficeGate.OfficeGateMode.EXIT);

        List<Patch> officeGateEntrancePatches = new ArrayList<>();
        officeGateEntrancePatches.add(office.getPatch(59,25));
        OfficeGateMapper.draw(officeGateEntrancePatches, OfficeGate.OfficeGateMode.ENTRANCE);

        List<Patch> plantPatches = new ArrayList<>();
        plantPatches.add(office.getPatch(0,20));
        plantPatches.add(office.getPatch(44,37));
        plantPatches.add(office.getPatch(44,42));
        plantPatches.add(office.getPatch(0,79));
        plantPatches.add(office.getPatch(44,68));
        plantPatches.add(office.getPatch(20,79));
        plantPatches.add(office.getPatch(21,79));
        plantPatches.add(office.getPatch(38,79));
        plantPatches.add(office.getPatch(39,79));
        PlantMapper.draw(plantPatches);

        List<Patch> printerPatches = new ArrayList<>();
        printerPatches.add(office.getPatch(44,39));
        PrinterMapper.draw(printerPatches);

        List<Patch> receptionTablePatches = new ArrayList<>();
        receptionTablePatches.add(office.getPatch(47,29));
        ReceptionTableMapper.draw(receptionTablePatches);

        List<Patch> tableUpPatches = new ArrayList<>();
        tableUpPatches.add(office.getPatch(28,5));
        tableUpPatches.add(office.getPatch(28,9));
        tableUpPatches.add(office.getPatch(28,13));
        tableUpPatches.add(office.getPatch(31,5));
        tableUpPatches.add(office.getPatch(31,9));
        tableUpPatches.add(office.getPatch(31,13));
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
        tableRightPatches.add(office.getPatch(36,2));
        tableRightPatches.add(office.getPatch(36,6));
        tableRightPatches.add(office.getPatch(36,10));
        tableRightPatches.add(office.getPatch(36,14));
        tableRightPatches.add(office.getPatch(40,2));
        tableRightPatches.add(office.getPatch(40,6));
        tableRightPatches.add(office.getPatch(40,10));
        tableRightPatches.add(office.getPatch(40,14));
        TableMapper.draw(tableRightPatches, "RIGHT");

        List<Patch> toiletPatches = new ArrayList<>();
        toiletPatches.add(office.getPatch(3,8));
        toiletPatches.add(office.getPatch(3,10));
        toiletPatches.add(office.getPatch(3,12));
        toiletPatches.add(office.getPatch(3,14));
        toiletPatches.add(office.getPatch(3,16));
        toiletPatches.add(office.getPatch(3,18));
        toiletPatches.add(office.getPatch(12,8));
        toiletPatches.add(office.getPatch(12,10));
        toiletPatches.add(office.getPatch(12,12));
        toiletPatches.add(office.getPatch(12,14));
        toiletPatches.add(office.getPatch(12,16));
        toiletPatches.add(office.getPatch(12,18));
        ToiletMapper.draw(toiletPatches);

        List<Patch> sinkPatches = new ArrayList<>();
        sinkPatches.add(office.getPatch(8,8));
        sinkPatches.add(office.getPatch(8,10));
        sinkPatches.add(office.getPatch(8,12));
        sinkPatches.add(office.getPatch(8,14));
        sinkPatches.add(office.getPatch(8,16));
        sinkPatches.add(office.getPatch(8,18));
        sinkPatches.add(office.getPatch(17,8));
        sinkPatches.add(office.getPatch(17,10));
        sinkPatches.add(office.getPatch(17,12));
        sinkPatches.add(office.getPatch(17,14));
        sinkPatches.add(office.getPatch(17,16));
        sinkPatches.add(office.getPatch(17,18));
        SinkMapper.draw(sinkPatches);

        List<Patch> fridgePatches = new ArrayList<>();
        fridgePatches.add(office.getPatch(21,2));
        FridgeMapper.draw(fridgePatches);

        List<Patch> waterDispenserPatches = new ArrayList<>();
        waterDispenserPatches.add(office.getPatch(21,4));
        WaterDispenserMapper.draw(waterDispenserPatches);

//        List<Patch> couchRightPatches = new ArrayList<>();
//        couchRightPatches.add(office.getPatch(49,3));
//        CouchMapper.draw(couchRightPatches, "RIGHT");

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(office.getPatch(56,26));
        SecurityMapper.draw(securityPatches);
    }

    private void drawInterface() {
        drawOfficeViewBackground(Main.officeSimulator.getOffice());
        drawOfficeViewForeground(Main.officeSimulator.getOffice(), false);
    }

    public void drawOfficeViewBackground(Office office) {
        OfficeGraphicsController.requestDrawOfficeView(stackPane, office, OfficeGraphicsController.tileSize, true, false);
    }

    public void drawOfficeViewForeground(Office office, boolean speedAware) {
        OfficeGraphicsController.requestDrawOfficeView(stackPane, office, OfficeGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    public void exportHeatMap() {
        try {
            OfficeSimulator.exportHeatMap();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void requestUpdateInterfaceSimulationElements() {
        Platform.runLater(this::updateSimulationTime);
        Platform.runLater(this::updateStatistics);
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.officeSimulator.getSimulationTime().getTime();
        long elapsedTime = Main.officeSimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS) / 5;
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
    }
    public void updateStatistics() {
        currentManagerCount.setText(String.valueOf(OfficeSimulator.currentManagerCount));
        currentBusinessCount.setText(String.valueOf(OfficeSimulator.currentBusinessCount));
        currentResearchCount.setText(String.valueOf(OfficeSimulator.currentResearchCount));
        currentTechnicalCount.setText(String.valueOf(OfficeSimulator.currentTechnicalCount));
        currentSecretaryCount.setText(String.valueOf(OfficeSimulator.currentSecretaryCount));
        currentClientCount.setText(String.valueOf(OfficeSimulator.currentClientCount));
        currentDriverCount.setText(String.valueOf(OfficeSimulator.currentDriverCount));
        currentVisitorCount.setText(String.valueOf(OfficeSimulator.currentVisitorCount));
        currentNonverbalCount.setText(String.valueOf(OfficeSimulator.currentNonverbalCount));
        currentCooperativeCount.setText(String.valueOf(OfficeSimulator.currentCooperativeCount));
        currentExchangeCount.setText(String.valueOf(OfficeSimulator.currentExchangeCount));
        averageNonverbalDuration.setText(String.format("%.02f", OfficeSimulator.averageNonverbalDuration));
        averageCooperativeDuration.setText(String.format("%.02f", OfficeSimulator.averageCooperativeDuration));
        averageExchangeDuration.setText(String.format("%.02f", OfficeSimulator.averageExchangeDuration));
        currentTeam1Count.setText(String.valueOf(OfficeSimulator.currentTeam1Count));
        currentTeam2Count.setText(String.valueOf(OfficeSimulator.currentTeam2Count));
        currentTeam3Count.setText(String.valueOf(OfficeSimulator.currentTeam3Count));
        currentTeam4Count.setText(String.valueOf(OfficeSimulator.currentTeam4Count));
        currentBossManagerCount.setText(String.valueOf(OfficeSimulator.currentBossManagerCount));
        currentBossBusinessCount.setText(String.valueOf(OfficeSimulator.currentBossBusinessCount));
        currentBossResearcherCount.setText(String.valueOf(OfficeSimulator.currentBossResearcherCount));
        currentBossTechnicalCount.setText(String.valueOf(OfficeSimulator.currentBossTechnicalCount));
        currentBossJanitorCount.setText(String.valueOf(OfficeSimulator.currentBossJanitorCount));
        currentBossClientCount.setText(String.valueOf(OfficeSimulator.currentBossClientCount));
        currentBossDriverCount.setText(String.valueOf(OfficeSimulator.currentBossDriverCount));
        currentBossVisitorCount.setText(String.valueOf(OfficeSimulator.currentBossVisitorCount));
        currentBossGuardCount.setText(String.valueOf(OfficeSimulator.currentBossGuardCount));
        currentBossReceptionistCount.setText(String.valueOf(OfficeSimulator.currentBossReceptionistCount));
        currentBossSecretaryCount.setText(String.valueOf(OfficeSimulator.currentBossSecretaryCount));
        currentManagerManagerCount.setText(String.valueOf(OfficeSimulator.currentManagerManagerCount));
        currentManagerBusinessCount.setText(String.valueOf(OfficeSimulator.currentManagerBusinessCount));
        currentManagerResearcherCount.setText(String.valueOf(OfficeSimulator.currentManagerResearcherCount));
        currentManagerTechnicalCount.setText(String.valueOf(OfficeSimulator.currentManagerTechnicalCount));
        currentManagerJanitorCount.setText(String.valueOf(OfficeSimulator.currentManagerJanitorCount));
        currentManagerClientCount.setText(String.valueOf(OfficeSimulator.currentManagerClientCount));
        currentManagerDriverCount.setText(String.valueOf(OfficeSimulator.currentManagerDriverCount));
        currentManagerVisitorCount.setText(String.valueOf(OfficeSimulator.currentManagerVisitorCount));
        currentManagerGuardCount.setText(String.valueOf(OfficeSimulator.currentManagerGuardCount));
        currentManagerReceptionistCount.setText(String.valueOf(OfficeSimulator.currentManagerReceptionistCount));
        currentManagerSecretaryCount.setText(String.valueOf(OfficeSimulator.currentManagerSecretaryCount));
        currentBusinessBusinessCount.setText(String.valueOf(OfficeSimulator.currentBusinessBusinessCount));
        currentBusinessResearcherCount.setText(String.valueOf(OfficeSimulator.currentBusinessResearcherCount));
        currentBusinessTechnicalCount.setText(String.valueOf(OfficeSimulator.currentBusinessTechnicalCount));
        currentBusinessJanitorCount.setText(String.valueOf(OfficeSimulator.currentBusinessJanitorCount));
        currentBusinessClientCount.setText(String.valueOf(OfficeSimulator.currentBusinessClientCount));
        currentBusinessDriverCount.setText(String.valueOf(OfficeSimulator.currentBusinessDriverCount));
        currentBusinessVisitorCount.setText(String.valueOf(OfficeSimulator.currentBusinessVisitorCount));
        currentBusinessGuardCount.setText(String.valueOf(OfficeSimulator.currentBusinessGuardCount));
        currentBusinessReceptionistCount.setText(String.valueOf(OfficeSimulator.currentBusinessReceptionistCount));
        currentBusinessSecretaryCount.setText(String.valueOf(OfficeSimulator.currentBusinessSecretaryCount));
        currentResearcherResearcherCount.setText(String.valueOf(OfficeSimulator.currentResearcherResearcherCount));
        currentResearcherTechnicalCount.setText(String.valueOf(OfficeSimulator.currentResearcherTechnicalCount));
        currentResearcherJanitorCount.setText(String.valueOf(OfficeSimulator.currentResearcherJanitorCount));
        currentResearcherClientCount.setText(String.valueOf(OfficeSimulator.currentResearcherClientCount));
        currentResearcherDriverCount.setText(String.valueOf(OfficeSimulator.currentResearcherDriverCount));
        currentResearcherVisitorCount.setText(String.valueOf(OfficeSimulator.currentResearcherVisitorCount));
        currentResearcherGuardCount.setText(String.valueOf(OfficeSimulator.currentResearcherGuardCount));
        currentResearcherReceptionistCount.setText(String.valueOf(OfficeSimulator.currentResearcherReceptionistCount));
        currentResearcherSecretaryCount.setText(String.valueOf(OfficeSimulator.currentResearcherSecretaryCount));
        currentTechnicalTechnicalCount.setText(String.valueOf(OfficeSimulator.currentTechnicalTechnicalCount));
        currentTechnicalJanitorCount.setText(String.valueOf(OfficeSimulator.currentTechnicalJanitorCount));
        currentTechnicalClientCount.setText(String.valueOf(OfficeSimulator.currentTechnicalClientCount));
        currentTechnicalDriverCount.setText(String.valueOf(OfficeSimulator.currentTechnicalDriverCount));
        currentTechnicalVisitorCount.setText(String.valueOf(OfficeSimulator.currentTechnicalVisitorCount));
        currentTechnicalGuardCount.setText(String.valueOf(OfficeSimulator.currentTechnicalGuardCount));
        currentTechnicalReceptionistCount.setText(String.valueOf(OfficeSimulator.currentTechnicalReceptionistCount));
        currentTechnicalSecretaryCount.setText(String.valueOf(OfficeSimulator.currentTechnicalSecretaryCount));
        currentJanitorJanitorCount.setText(String.valueOf(OfficeSimulator.currentJanitorJanitorCount));
        currentJanitorClientCount.setText(String.valueOf(OfficeSimulator.currentJanitorClientCount));
        currentJanitorDriverCount.setText(String.valueOf(OfficeSimulator.currentJanitorDriverCount));
        currentJanitorVisitorCount.setText(String.valueOf(OfficeSimulator.currentJanitorVisitorCount));
        currentJanitorSecretaryCount.setText(String.valueOf(OfficeSimulator.currentJanitorSecretaryCount));
        currentClientClientCount.setText(String.valueOf(OfficeSimulator.currentClientClientCount));
        currentClientDriverCount.setText(String.valueOf(OfficeSimulator.currentClientDriverCount));
        currentClientVisitorCount.setText(String.valueOf(OfficeSimulator.currentClientVisitorCount));
        currentClientGuardCount.setText(String.valueOf(OfficeSimulator.currentClientGuardCount));
        currentClientReceptionistCount.setText(String.valueOf(OfficeSimulator.currentClientReceptionistCount));
        currentClientSecretaryCount.setText(String.valueOf(OfficeSimulator.currentClientSecretaryCount));
        currentDriverDriverCount.setText(String.valueOf(OfficeSimulator.currentDriverDriverCount));
        currentDriverVisitorCount.setText(String.valueOf(OfficeSimulator.currentDriverVisitorCount));
        currentDriverGuardCount.setText(String.valueOf(OfficeSimulator.currentDriverGuardCount));
        currentDriverReceptionistCount.setText(String.valueOf(OfficeSimulator.currentDriverReceptionistCount));
        currentDriverSecretaryCount.setText(String.valueOf(OfficeSimulator.currentDriverSecretaryCount));
        currentVisitorVisitorCount.setText(String.valueOf(OfficeSimulator.currentVisitorVisitorCount));
        currentVisitorGuardCount.setText(String.valueOf(OfficeSimulator.currentVisitorGuardCount));
        currentVisitorReceptionistCount.setText(String.valueOf(OfficeSimulator.currentVisitorReceptionistCount));
        currentVisitorSecretaryCount.setText(String.valueOf(OfficeSimulator.currentVisitorSecretaryCount));
        currentGuardSecretaryCount.setText(String.valueOf(OfficeSimulator.currentGuardSecretaryCount));
        currentReceptionistSecretaryCount.setText(String.valueOf(OfficeSimulator.currentReceptionistSecretaryCount));
        currentSecretarySecretaryCount.setText(String.valueOf(OfficeSimulator.currentSecretarySecretaryCount));
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
        if (!Main.officeSimulator.isRunning()) {
            Main.officeSimulator.setRunning(true);
            Main.officeSimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
            exportToCSVButton.setDisable(true);
            exportHeatMapButton.setDisable(true);
        }
        else {
            Main.officeSimulator.setRunning(false);
            playButton.setText("Play");
            exportToCSVButton.setDisable(false);
            exportHeatMapButton.setDisable(false);
        }
    }

    @Override
    protected void closeAction() {
    }

    public void disableEdits() {
        nonverbalMean.setDisable(true);
        nonverbalStdDev.setDisable(true);
        cooperativeMean.setDisable(true);
        cooperativeStdDev.setDisable(true);
        exchangeMean.setDisable(true);
        exchangeStdDev.setDisable(true);
        fieldOfView.setDisable(true);
        maxClients.setDisable(true);
        maxCurrentClients.setDisable(true);
        maxDrivers.setDisable(true);
        maxCurrentDrivers.setDisable(true);
        maxVisitors.setDisable(true);
        maxCurrentVisitors.setDisable(true);
        resetToDefaultButton.setDisable(true);
        configureIOSButton.setDisable(true);
        editInteractionButton.setDisable(true);
    }

    public void resetToDefault() {
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
        maxCurrentClients.setText(Integer.toString(OfficeSimulator.defaultMaxCurrentClients));
        maxCurrentDrivers.setText(Integer.toString(OfficeSimulator.defaultMaxCurrentDrivers));
        maxCurrentVisitors.setText(Integer.toString(OfficeSimulator.defaultMaxCurrentVisitors));
    }

    public void openIOSLevels() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/OfficeConfigureIOS.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Configure IOS Levels");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void openEditInteractions() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/OfficeEditInteractions.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Interaction Type Chances");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToCSV(){
        try {
            OfficeSimulator.exportToCSV();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void configureParameters(Office office) {
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

        currentManagerCount.setText(String.valueOf(OfficeSimulator.currentManagerCount));
        currentBusinessCount.setText(String.valueOf(OfficeSimulator.currentBusinessCount));
        currentResearchCount.setText(String.valueOf(OfficeSimulator.currentResearchCount));
        currentTechnicalCount.setText(String.valueOf(OfficeSimulator.currentTechnicalCount));
        currentSecretaryCount.setText(String.valueOf(OfficeSimulator.currentSecretaryCount));
        currentClientCount.setText(String.valueOf(OfficeSimulator.currentClientCount));
        currentDriverCount.setText(String.valueOf(OfficeSimulator.currentDriverCount));
        currentVisitorCount.setText(String.valueOf(OfficeSimulator.currentVisitorCount));
        currentNonverbalCount.setText(String.valueOf(OfficeSimulator.currentNonverbalCount));
        currentCooperativeCount.setText(String.valueOf(OfficeSimulator.currentCooperativeCount));
        currentExchangeCount.setText(String.valueOf(OfficeSimulator.currentExchangeCount));
        averageNonverbalDuration.setText(String.valueOf(OfficeSimulator.averageNonverbalDuration));
        averageCooperativeDuration.setText(String.valueOf(OfficeSimulator.averageCooperativeDuration));
        averageExchangeDuration.setText(String.valueOf(OfficeSimulator.averageExchangeDuration));
        currentTeam1Count.setText(String.valueOf(OfficeSimulator.currentTeam1Count));
        currentTeam2Count.setText(String.valueOf(OfficeSimulator.currentTeam2Count));
        currentTeam3Count.setText(String.valueOf(OfficeSimulator.currentTeam3Count));
        currentTeam4Count.setText(String.valueOf(OfficeSimulator.currentTeam4Count));
        currentBossManagerCount.setText(String.valueOf(OfficeSimulator.currentBossManagerCount));
        currentBossBusinessCount.setText(String.valueOf(OfficeSimulator.currentBossBusinessCount));
        currentBossResearcherCount.setText(String.valueOf(OfficeSimulator.currentBossResearcherCount));
        currentBossTechnicalCount.setText(String.valueOf(OfficeSimulator.currentBossTechnicalCount));
        currentBossJanitorCount.setText(String.valueOf(OfficeSimulator.currentBossJanitorCount));
        currentBossClientCount.setText(String.valueOf(OfficeSimulator.currentBossClientCount));
        currentBossDriverCount.setText(String.valueOf(OfficeSimulator.currentBossDriverCount));
        currentBossVisitorCount.setText(String.valueOf(OfficeSimulator.currentBossVisitorCount));
        currentBossGuardCount.setText(String.valueOf(OfficeSimulator.currentBossGuardCount));
        currentBossReceptionistCount.setText(String.valueOf(OfficeSimulator.currentBossReceptionistCount));
        currentBossSecretaryCount.setText(String.valueOf(OfficeSimulator.currentBossSecretaryCount));
        currentManagerManagerCount.setText(String.valueOf(OfficeSimulator.currentManagerManagerCount));
        currentManagerBusinessCount.setText(String.valueOf(OfficeSimulator.currentManagerBusinessCount));
        currentManagerResearcherCount.setText(String.valueOf(OfficeSimulator.currentManagerResearcherCount));
        currentManagerTechnicalCount.setText(String.valueOf(OfficeSimulator.currentManagerTechnicalCount));
        currentManagerJanitorCount.setText(String.valueOf(OfficeSimulator.currentManagerJanitorCount));
        currentManagerClientCount.setText(String.valueOf(OfficeSimulator.currentManagerClientCount));
        currentManagerDriverCount.setText(String.valueOf(OfficeSimulator.currentManagerDriverCount));
        currentManagerVisitorCount.setText(String.valueOf(OfficeSimulator.currentManagerVisitorCount));
        currentManagerGuardCount.setText(String.valueOf(OfficeSimulator.currentManagerGuardCount));
        currentManagerReceptionistCount.setText(String.valueOf(OfficeSimulator.currentManagerReceptionistCount));
        currentManagerSecretaryCount.setText(String.valueOf(OfficeSimulator.currentManagerSecretaryCount));
        currentBusinessBusinessCount.setText(String.valueOf(OfficeSimulator.currentBusinessBusinessCount));
        currentBusinessResearcherCount.setText(String.valueOf(OfficeSimulator.currentBusinessResearcherCount));
        currentBusinessTechnicalCount.setText(String.valueOf(OfficeSimulator.currentBusinessTechnicalCount));
        currentBusinessJanitorCount.setText(String.valueOf(OfficeSimulator.currentBusinessJanitorCount));
        currentBusinessClientCount.setText(String.valueOf(OfficeSimulator.currentBusinessClientCount));
        currentBusinessDriverCount.setText(String.valueOf(OfficeSimulator.currentBusinessDriverCount));
        currentBusinessVisitorCount.setText(String.valueOf(OfficeSimulator.currentBusinessVisitorCount));
        currentBusinessGuardCount.setText(String.valueOf(OfficeSimulator.currentBusinessGuardCount));
        currentBusinessReceptionistCount.setText(String.valueOf(OfficeSimulator.currentBusinessReceptionistCount));
        currentBusinessSecretaryCount.setText(String.valueOf(OfficeSimulator.currentBusinessSecretaryCount));
        currentResearcherResearcherCount.setText(String.valueOf(OfficeSimulator.currentResearcherResearcherCount));
        currentResearcherTechnicalCount.setText(String.valueOf(OfficeSimulator.currentResearcherTechnicalCount));
        currentResearcherJanitorCount.setText(String.valueOf(OfficeSimulator.currentResearcherJanitorCount));
        currentResearcherClientCount.setText(String.valueOf(OfficeSimulator.currentResearcherClientCount));
        currentResearcherDriverCount.setText(String.valueOf(OfficeSimulator.currentResearcherDriverCount));
        currentResearcherVisitorCount.setText(String.valueOf(OfficeSimulator.currentResearcherVisitorCount));
        currentResearcherGuardCount.setText(String.valueOf(OfficeSimulator.currentResearcherGuardCount));
        currentResearcherReceptionistCount.setText(String.valueOf(OfficeSimulator.currentResearcherReceptionistCount));
        currentResearcherSecretaryCount.setText(String.valueOf(OfficeSimulator.currentResearcherSecretaryCount));
        currentTechnicalTechnicalCount.setText(String.valueOf(OfficeSimulator.currentTechnicalTechnicalCount));
        currentTechnicalJanitorCount.setText(String.valueOf(OfficeSimulator.currentTechnicalJanitorCount));
        currentTechnicalClientCount.setText(String.valueOf(OfficeSimulator.currentTechnicalClientCount));
        currentTechnicalDriverCount.setText(String.valueOf(OfficeSimulator.currentTechnicalDriverCount));
        currentTechnicalVisitorCount.setText(String.valueOf(OfficeSimulator.currentTechnicalVisitorCount));
        currentTechnicalGuardCount.setText(String.valueOf(OfficeSimulator.currentTechnicalGuardCount));
        currentTechnicalReceptionistCount.setText(String.valueOf(OfficeSimulator.currentTechnicalReceptionistCount));
        currentTechnicalSecretaryCount.setText(String.valueOf(OfficeSimulator.currentTechnicalSecretaryCount));
        currentJanitorJanitorCount.setText(String.valueOf(OfficeSimulator.currentJanitorJanitorCount));
        currentJanitorClientCount.setText(String.valueOf(OfficeSimulator.currentJanitorClientCount));
        currentJanitorDriverCount.setText(String.valueOf(OfficeSimulator.currentJanitorDriverCount));
        currentJanitorVisitorCount.setText(String.valueOf(OfficeSimulator.currentJanitorVisitorCount));
        currentJanitorSecretaryCount.setText(String.valueOf(OfficeSimulator.currentJanitorSecretaryCount));
        currentClientClientCount.setText(String.valueOf(OfficeSimulator.currentClientClientCount));
        currentClientDriverCount.setText(String.valueOf(OfficeSimulator.currentClientDriverCount));
        currentClientVisitorCount.setText(String.valueOf(OfficeSimulator.currentClientVisitorCount));
        currentClientGuardCount.setText(String.valueOf(OfficeSimulator.currentClientGuardCount));
        currentClientReceptionistCount.setText(String.valueOf(OfficeSimulator.currentClientReceptionistCount));
        currentClientSecretaryCount.setText(String.valueOf(OfficeSimulator.currentClientSecretaryCount));
        currentDriverDriverCount.setText(String.valueOf(OfficeSimulator.currentDriverDriverCount));
        currentDriverVisitorCount.setText(String.valueOf(OfficeSimulator.currentDriverVisitorCount));
        currentDriverGuardCount.setText(String.valueOf(OfficeSimulator.currentDriverGuardCount));
        currentDriverReceptionistCount.setText(String.valueOf(OfficeSimulator.currentDriverReceptionistCount));
        currentDriverSecretaryCount.setText(String.valueOf(OfficeSimulator.currentDriverSecretaryCount));
        currentVisitorVisitorCount.setText(String.valueOf(OfficeSimulator.currentVisitorVisitorCount));
        currentVisitorGuardCount.setText(String.valueOf(OfficeSimulator.currentVisitorGuardCount));
        currentVisitorReceptionistCount.setText(String.valueOf(OfficeSimulator.currentVisitorReceptionistCount));
        currentVisitorSecretaryCount.setText(String.valueOf(OfficeSimulator.currentVisitorSecretaryCount));
        currentGuardSecretaryCount.setText(String.valueOf(OfficeSimulator.currentGuardSecretaryCount));
        currentReceptionistSecretaryCount.setText(String.valueOf(OfficeSimulator.currentReceptionistSecretaryCount));
        currentSecretarySecretaryCount.setText(String.valueOf(OfficeSimulator.currentSecretarySecretaryCount));
    }

    public boolean validateParameters() {
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxClients.getText()) >= 0 && Integer.parseInt(maxDrivers.getText()) >= 0
                && Integer.parseInt(maxVisitors.getText()) >= 0;
        if (!validParameters) {
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

}