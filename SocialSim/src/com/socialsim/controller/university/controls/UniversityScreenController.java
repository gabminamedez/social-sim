package com.socialsim.controller.university.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.university.graphics.UniversityGraphicsController;
import com.socialsim.controller.university.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.university.UniversityAgentMovement;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.*;
import com.socialsim.model.core.environment.university.patchobject.passable.gate.UniversityGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.university.UniversitySimulator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class UniversityScreenController extends ScreenController {

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
    @FXML private TextField maxStudents;
    @FXML private TextField maxProfessors;
    @FXML private TextField maxCurrentStudents;
    @FXML private TextField maxCurrentProfessors;
    @FXML private TextField fieldOfView;
    @FXML private Label currentProfessorCount;
    @FXML private Label currentStudentCount;
    @FXML private Label currentNonverbalCount;
    @FXML private Label currentCooperativeCount;
    @FXML private Label currentExchangeCount;
    @FXML private Label averageNonverbalDuration;
    @FXML private Label averageCooperativeDuration;
    @FXML private Label averageExchangeDuration;
    @FXML private Label currentStudentStudentCount;
    @FXML private Label currentStudentProfCount;
    @FXML private Label currentStudentGuardCount;
    @FXML private Label currentStudentJanitorCount;
    @FXML private Label currentStudentStaffCount;
    @FXML private Label currentProfProfCount;
    @FXML private Label currentProfGuardCount;
    @FXML private Label currentProfJanitorCount;
    @FXML private Label currentProfStaffCount;
    @FXML private Label currentJanitorJanitorCount;
    @FXML private Label currentStaffStaffCount;
    @FXML private Button configureIOSButton;
    @FXML private Button editInteractionButton;
    @FXML private Button exportToCSVButton;
    @FXML private Button exportHeatMapButton;

    private final double CANVAS_SCALE = 0.5;

    public UniversityScreenController() {
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
        int length = 120;
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        University university = University.UniversityFactory.create(rows, columns);
        Main.universitySimulator.resetToDefaultConfiguration(university);
        University.configureDefaultIOS();
        university.copyDefaultToIOS();
        University.configureDefaultInteractionTypeChances();
        university.copyDefaultToInteractionTypeChances();
    }

    @FXML
    public void initializeAction() {
        if (Main.universitySimulator.isRunning()) {
            playAction();
            playButton.setSelected(false);
        }
        if (validateParameters()) {
            University university = Main.universitySimulator.getUniversity();
            this.configureParameters(university);
            initializeUniversity(university);
            university.convertIOSToChances();
            setElements();
            playButton.setDisable(false);
            exportToCSVButton.setDisable(true);
            exportHeatMapButton.setDisable(true);
            Main.universitySimulator.replenishStaticVars();
            disableEdits();
        }
    }

    public void initializeUniversity(University university) {
        UniversityGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.universitySimulator.getUniversity().getRows();
        mapUniversity();
        Main.universitySimulator.spawnInitialAgents(university);
        drawInterface();
    }

    public void mapUniversity() {
        University university = Main.universitySimulator.getUniversity();
        int rows = university.getRows();
        int cols = university.getColumns();

        List<Patch> wallPatches = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            if (i < 26 || i > 33) {
                for (int j = 0; j < cols; j++) {
                    if (i < 26 && (j < 62 || j > 69)) {
                        wallPatches.add(university.getPatch(i, j));
                    }
                    else if (i > 33 && j >= 17) {
                        wallPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        Main.universitySimulator.getUniversity().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 50; j < 62; j++) {
                if (j != 61 || i >= 3 && i <= 6)
                    fBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(fBathroomPatches, 1));

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 10; i < 18; i++) {
            for (int j = 50; j < 62; j++) {
                if (j != 61 || i >= 12 && i <= 15)
                    mBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> cafeteriaPatches = new ArrayList<>();
        for (int i = 3; i < 26; i++) {
            for (int j = 96; j < 120; j++) {
                cafeteriaPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getCafeterias().add(Cafeteria.cafeteriaFactory.create(cafeteriaPatches, 1));

        List<Patch> classroom1Patches = new ArrayList<>();
        for (int i = 3; i < 26; i++) {
            for (int j = 5; j < 21; j++) {
                if (i != 25 || j <= 8 || j >= 17) {
                    classroom1Patches.add(university.getPatch(i, j));
                }
            }
        };
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom1Patches, 0));

        List<Patch> classroom2Patches = new ArrayList<>();
        for (int i = 3; i < 26; i++) {
            for (int j = 25; j < 45; j++) {
                if (i != 25 || j <= 28 || j >= 41) {
                    classroom2Patches.add(university.getPatch(i, j));
                }
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom2Patches, 1));

        List<Patch> classroom3Patches = new ArrayList<>();
        for (int i = 34; i < 57; i++) {
            for (int j = 23; j < 39; j++) {
                if (i != 34 || j <= 26 || j >= 35)
                    classroom3Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom3Patches, 2));

        List<Patch> classroom4Patches = new ArrayList<>();
        for (int i = 34; i < 57; i++) {
            for (int j = 42; j < 58; j++) {
                if (i != 34 || j <= 45 || j >= 54)
                    classroom4Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom4Patches, 3));

        List<Patch> classroom5Patches = new ArrayList<>();
        for (int i = 34; i < 57; i++) {
            for (int j = 61; j < 77; j++) {
                if (i != 34 || j <= 64 || j >= 73)
                    classroom5Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom5Patches, 4));

        List<Patch> classroom6Patches = new ArrayList<>();
        for (int i = 34; i < 57; i++) {
            for (int j = 80; j < 96; j++) {
                if (i != 34 || j <= 83 || j >= 92)
                    classroom6Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom6Patches, 5));

        List<Patch> staffOfficePatches = new ArrayList<>();
        for (int i = 3; i < 26; i++) {
            for (int j = 74; j < 94; j++) {
                if (i != 25 || (j >= 82 && j <= 85)) {
                    staffOfficePatches.add(university.getPatch(i, j));
                }
            }
        }
        Main.universitySimulator.getUniversity().getStaffOffices().add(StaffOffice.staffOfficeFactory.create(staffOfficePatches, 1));

        List<Patch> studyAreaPatches = new ArrayList<>();
        for (int i = 34; i < 56; i++) {
            for (int j = 99; j < 119; j++) {
                if (i != 34 || (j >= 107 && j <= 110))
                    studyAreaPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getStudyAreas().add(StudyArea.studyAreaFactory.create(studyAreaPatches, 2));

        List<Patch> benchRightPatches = new ArrayList<>();
        benchRightPatches.add(university.getPatch(43,0));
        benchRightPatches.add(university.getPatch(48,0));
        benchRightPatches.add(university.getPatch(53,0));
        BenchMapper.draw(benchRightPatches, "RIGHT");

        List<Patch> benchLeftPatches = new ArrayList<>();
        benchLeftPatches.add(university.getPatch(38,16));
        benchLeftPatches.add(university.getPatch(43,16));
        benchLeftPatches.add(university.getPatch(48,16));
        benchLeftPatches.add(university.getPatch(53,16));
        BenchMapper.draw(benchLeftPatches, "LEFT");

        List<Patch> benchDownPatches = new ArrayList<>();
        benchDownPatches.add(university.getPatch(26,47));
        benchDownPatches.add(university.getPatch(26,55));
        benchDownPatches.add(university.getPatch(26,11));
        BenchMapper.draw(benchDownPatches, "DOWN");

        List<Patch> benchUpPatches = new ArrayList<>();
        benchUpPatches.add(university.getPatch(33,98));
        benchUpPatches.add(university.getPatch(33,29));
        benchUpPatches.add(university.getPatch(33,67));
        BenchMapper.draw(benchUpPatches, "UP");

        List<Patch> boardRightPatches = new ArrayList<>();
        boardRightPatches.add(university.getPatch(7,5));
        boardRightPatches.add(university.getPatch(15,5));
        boardRightPatches.add(university.getPatch(7,25));
        boardRightPatches.add(university.getPatch(15,25));
        BoardMapper.draw(boardRightPatches, "RIGHT");

        List<Patch> boardLeftPatches = new ArrayList<>();
        boardLeftPatches.add(university.getPatch(39,38));
        boardLeftPatches.add(university.getPatch(47,38));
        boardLeftPatches.add(university.getPatch(39,57));
        boardLeftPatches.add(university.getPatch(47,57));
        boardLeftPatches.add(university.getPatch(39,76));
        boardLeftPatches.add(university.getPatch(47,76));
        boardLeftPatches.add(university.getPatch(39,95));
        boardLeftPatches.add(university.getPatch(47,95));
        BoardMapper.draw(boardLeftPatches, "LEFT");

        List<Patch> bulletinRightPatches = new ArrayList<>();
        bulletinRightPatches.add(university.getPatch(36,0));
        BulletinMapper.draw(bulletinRightPatches, "RIGHT");

        List<Patch> bulletinDownPatches = new ArrayList<>();
//        bulletinDownPatches.add(university.getPatch(23,10));
        bulletinDownPatches.add(university.getPatch(25,32));
        BulletinMapper.draw(bulletinDownPatches, "DOWN");

        List<Patch> bulletinUpPatches = new ArrayList<>();
//        bulletinUpPatches.add(university.getPatch(36,28));
//        bulletinUpPatches.add(university.getPatch(36,47));
//        bulletinUpPatches.add(university.getPatch(36,66));
        bulletinUpPatches.add(university.getPatch(34,85));
        BulletinMapper.draw(bulletinUpPatches, "UP");

        List<Patch> chairPatches = new ArrayList<>();
        for (int i = 5; i < 24; i++) {
            if (i == 5 || i == 7 || i == 9 || i == 11 || i == 16 || i == 18 || i == 20 || i == 22) {
                for (int j = 10; j < 19; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 5; i < 24; i++) {
            if (i == 5 || i == 7 || i == 9 || i == 11 || i == 16 || i == 18 || i == 20 || i == 22) {
                for (int j = 30; j < 44; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 37; i < 55; i++) {
            if (i == 37 || i == 39 || i == 41 || i == 43 || i == 48 || i == 50 || i == 52 || i == 54) {
                for (int j = 25; j < 34; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 37; i < 55; i++) {
            if (i == 37 || i == 39 || i == 41 || i == 43 || i == 48 || i == 50 || i == 52 || i == 54) {
                for (int j = 44; j < 53; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 37; i < 55; i++) {
            if (i == 37 || i == 39 || i == 41 || i == 43 || i == 48 || i == 50 || i == 52 || i == 54) {
                for (int j = 63; j < 72; j++) {
                    if (j % 2 == 1) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        for (int i = 37; i < 55; i++) {
            if (i == 37 || i == 39 || i == 41 || i == 43 || i == 48 || i == 50 || i == 52 || i == 54) {
                for (int j = 82; j < 91; j++) {
                    if (j % 2 == 0) {
                        chairPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }

        chairPatches.add(university.getPatch(12, 77));
        chairPatches.add(university.getPatch(12, 82));
        chairPatches.add(university.getPatch(12, 87));
        chairPatches.add(university.getPatch(16, 74));
        chairPatches.add(university.getPatch(19, 74));
        chairPatches.add(university.getPatch(22, 74));
        chairPatches.add(university.getPatch(16, 93));
        chairPatches.add(university.getPatch(19, 93));
        chairPatches.add(university.getPatch(22, 93));
        ChairMapper.draw(chairPatches);

        List<Patch> doorDownPatches = new ArrayList<>();
        doorDownPatches.add(university.getPatch(25,5));
        doorDownPatches.add(university.getPatch(25,17));
        doorDownPatches.add(university.getPatch(25,25));
        doorDownPatches.add(university.getPatch(25,41));
        doorDownPatches.add(university.getPatch(25,82));
        DoorMapper.draw(doorDownPatches, "DOWN");

        List<Patch> doorUpPatches = new ArrayList<>();
        doorUpPatches.add(university.getPatch(34,23));
        doorUpPatches.add(university.getPatch(34,35));
        doorUpPatches.add(university.getPatch(34,42));
        doorUpPatches.add(university.getPatch(34,54));
        doorUpPatches.add(university.getPatch(34,61));
        doorUpPatches.add(university.getPatch(34,73));
        doorUpPatches.add(university.getPatch(34,80));
        doorUpPatches.add(university.getPatch(34,92));
        doorUpPatches.add(university.getPatch(34,107));
        DoorMapper.draw(doorUpPatches, "UP");

        List<Patch> doorRightPatches = new ArrayList<>();
        doorRightPatches.add(university.getPatch(12, 61));
        doorRightPatches.add(university.getPatch(3, 61));
        DoorMapper.draw(doorRightPatches, "RIGHT");

        List<Patch> eatTablePatches = new ArrayList<>();
        for (int i = 6; i < 24; i++) {
            if (i == 7 || i == 11 || i == 15 || i == 19 || i == 23) {
                for (int j = 97; j < 117; j++) {
                    if (j == 97 || j == 103 || j == 109 || j == 115) {
                        eatTablePatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        EatTableMapper.draw(eatTablePatches);    

        List<Patch> toiletPatches = new ArrayList<>();
        toiletPatches.add(university.getPatch(1,50));
        toiletPatches.add(university.getPatch(1,52));
        toiletPatches.add(university.getPatch(1,54));
        toiletPatches.add(university.getPatch(8,50));
        toiletPatches.add(university.getPatch(8,52));
        toiletPatches.add(university.getPatch(8,54));
        toiletPatches.add(university.getPatch(10,50));
        toiletPatches.add(university.getPatch(10,52));
        toiletPatches.add(university.getPatch(10,54));
        toiletPatches.add(university.getPatch(17,50));
        toiletPatches.add(university.getPatch(17,52));
        toiletPatches.add(university.getPatch(17,54));
        ToiletMapper.draw(toiletPatches);
      
        List<Patch> sinkPatches = new ArrayList<>();
        sinkPatches.add(university.getPatch(1,56));
        sinkPatches.add(university.getPatch(1,58));
        sinkPatches.add(university.getPatch(1,60));
        sinkPatches.add(university.getPatch(10,56));
        sinkPatches.add(university.getPatch(10,58));
        sinkPatches.add(university.getPatch(10,60));
        SinkMapper.draw(sinkPatches);

        List<Patch> profTableRightPatches = new ArrayList<>();
        profTableRightPatches.add(university.getPatch(13,7));
        profTableRightPatches.add(university.getPatch(13,27));
        ProfTableMapper.draw(profTableRightPatches, "RIGHT");

        List<Patch> profTableLeftPatches = new ArrayList<>();
        profTableLeftPatches.add(university.getPatch(45,36));
        profTableLeftPatches.add(university.getPatch(45,55));
        profTableLeftPatches.add(university.getPatch(45,74));
        profTableLeftPatches.add(university.getPatch(45,93));
        ProfTableMapper.draw(profTableLeftPatches, "LEFT");

//        List<Patch> studyTablePatches = new ArrayList<>();
//        for (int i = 5; i < 17; i++) {
//            if (i == 5 || i == 8 || i == 13 || i == 16) {
//                for (int j = 77; j < 82; j++) {
//                    if (j == 77 || j == 81) {
//                        studyTablePatches.add(university.getPatch(i, j));
//                    }
//                }
//            }
//        }
//        StudyTableMapper.draw(studyTablePatches, "LEFT");
//        studyTablePatches = new ArrayList<>();
//        studyTablePatches.add(university.getPatch(12, 86));
//        studyTablePatches.add(university.getPatch(20, 86));
//        studyTablePatches.add(university.getPatch(12, 90));
//        studyTablePatches.add(university.getPatch(20, 90));
//        studyTablePatches.add(university.getPatch(4, 86));
//        studyTablePatches.add(university.getPatch(8, 86));
//        studyTablePatches.add(university.getPatch(4, 90));
//        studyTablePatches.add(university.getPatch(8, 90));
//        studyTablePatches.add(university.getPatch(16, 86));
//        studyTablePatches.add(university.getPatch(16, 90));
//        studyTablePatches.add(university.getPatch(20, 76));
//        studyTablePatches.add(university.getPatch(20, 80));
//        StudyTableMapper.draw(studyTablePatches, "UP");

        List<Patch> studyTablePatches = new ArrayList<>();
        studyTablePatches.add(university.getPatch(37, 101));
        studyTablePatches.add(university.getPatch(41, 101));
        studyTablePatches.add(university.getPatch(45, 101));
        studyTablePatches.add(university.getPatch(49, 101));
        studyTablePatches.add(university.getPatch(53, 101));
        studyTablePatches.add(university.getPatch(37, 105));
        studyTablePatches.add(university.getPatch(41, 105));
        studyTablePatches.add(university.getPatch(45, 105));
        studyTablePatches.add(university.getPatch(49, 105));
        studyTablePatches.add(university.getPatch(53, 105));
        studyTablePatches.add(university.getPatch(37, 111));
        studyTablePatches.add(university.getPatch(41, 111));
        studyTablePatches.add(university.getPatch(45, 111));
        studyTablePatches.add(university.getPatch(49, 111));
        studyTablePatches.add(university.getPatch(53, 111));
        studyTablePatches.add(university.getPatch(37, 115));
        studyTablePatches.add(university.getPatch(41, 115));
        studyTablePatches.add(university.getPatch(45, 115));
        studyTablePatches.add(university.getPatch(49, 115));
        studyTablePatches.add(university.getPatch(53, 115));
        StudyTableMapper.draw(studyTablePatches, "UP");

        List<Patch> officeTablePatches = new ArrayList<>();
        officeTablePatches.add(university.getPatch(11, 74));
        officeTablePatches.add(university.getPatch(11, 76));
        officeTablePatches.add(university.getPatch(11, 78));
        officeTablePatches.add(university.getPatch(11, 80));
        officeTablePatches.add(university.getPatch(11, 82));
        officeTablePatches.add(university.getPatch(11, 84));
        officeTablePatches.add(university.getPatch(11, 86));
        officeTablePatches.add(university.getPatch(11, 88));

        OfficeTableMapper.draw(officeTablePatches, "DOWN");

        List<Patch> cabinetPatches = new ArrayList<>();
        cabinetPatches.add(university.getPatch(3, 74));
        cabinetPatches.add(university.getPatch(3, 76));
        cabinetPatches.add(university.getPatch(3, 78));
        cabinetPatches.add(university.getPatch(3, 80));
        cabinetPatches.add(university.getPatch(3, 82));
        cabinetPatches.add(university.getPatch(3, 84));
        cabinetPatches.add(university.getPatch(3, 86));
        cabinetPatches.add(university.getPatch(3, 88));
        cabinetPatches.add(university.getPatch(3, 90));
        cabinetPatches.add(university.getPatch(3, 92));
        cabinetPatches.add(university.getPatch(5, 74));
        cabinetPatches.add(university.getPatch(5, 92));
        CabinetMapper.draw(cabinetPatches, "DOWN");

        List<Patch> trashPatches = new ArrayList<>();
        trashPatches.add(university.getPatch(36,16));
        trashPatches.add(university.getPatch(27,0));
        trashPatches.add(university.getPatch(29,0));
        trashPatches.add(university.getPatch(31,0));
        trashPatches.add(university.getPatch(18,69));
        trashPatches.add(university.getPatch(20,69));
        trashPatches.add(university.getPatch(22,69));
        trashPatches.add(university.getPatch(26,95));
        trashPatches.add(university.getPatch(33,47));
        trashPatches.add(university.getPatch(33,49));
        trashPatches.add(university.getPatch(33,51));
        TrashMapper.draw(trashPatches);

        List<Patch> universityGateExitPatches = new ArrayList<>();
        universityGateExitPatches.add(university.getPatch(59,4));
        UniversityGateMapper.draw(universityGateExitPatches, UniversityGate.UniversityGateMode.EXIT);

        List<Patch> universityGateEntrancePatches = new ArrayList<>();
        universityGateEntrancePatches.add(university.getPatch(59,12));
        UniversityGateMapper.draw(universityGateEntrancePatches, UniversityGate.UniversityGateMode.ENTRANCE);

        List<Patch> fountainPatches = new ArrayList<>();
        fountainPatches.add(university.getPatch(20,62));
        fountainPatches.add(university.getPatch(26,119));
        FountainMapper.draw(fountainPatches);

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(university.getPatch(56,13));
        SecurityMapper.draw(securityPatches);

        List<Patch> stallPatches = new ArrayList<>();
        stallPatches.add(university.getPatch(3,97));
        stallPatches.add(university.getPatch(3,103));
        stallPatches.add(university.getPatch(3,109));
        stallPatches.add(university.getPatch(3,115));
        StallMapper.draw(stallPatches);
    }

    private void drawInterface() {
        drawUniversityViewBackground(Main.universitySimulator.getUniversity());
        drawUniversityViewForeground(Main.universitySimulator.getUniversity(), false);
    }

    public void drawUniversityViewBackground(University university) {
        UniversityGraphicsController.requestDrawUniversityView(stackPane, university, UniversityGraphicsController.tileSize, true, false);
    }

    public void drawUniversityViewForeground(University university, boolean speedAware) {
        UniversityGraphicsController.requestDrawUniversityView(stackPane, university, UniversityGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    public void generateHeatMap() {
        int[][] currentPatchCount = UniversitySimulator.currentPatchCount;
        int maxPatchCount = Arrays.stream(currentPatchCount).flatMapToInt(Arrays::stream).max().getAsInt();
        for(int i = 0; i < currentPatchCount.length; i++) {
            for (int j = 0; j < currentPatchCount[0].length; j++) {
                int patchRGBCount = 255 - currentPatchCount[i][j] * 255 / maxPatchCount;
                foregroundCanvas.getGraphicsContext2D().setFill(Color.rgb(255, patchRGBCount, patchRGBCount));
                foregroundCanvas.getGraphicsContext2D().fillRect(j * UniversityGraphicsController.tileSize, i * UniversityGraphicsController.tileSize, UniversityGraphicsController.tileSize, UniversityGraphicsController.tileSize);
            }
        }
    }

    public void exportHeatMap() {
        try {
            UniversitySimulator.exportHeatMap();
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
        LocalTime currentTime = Main.universitySimulator.getSimulationTime().getTime();
        long elapsedTime = Main.universitySimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS) / 5;
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
    }

    public void updateStatistics() {
        currentProfessorCount.setText(String.valueOf(UniversitySimulator.currentProfessorCount));
        currentStudentCount.setText(String.valueOf(UniversitySimulator.currentStudentCount));
        currentNonverbalCount.setText(String.valueOf(UniversitySimulator.currentNonverbalCount));
        currentCooperativeCount.setText(String.valueOf(UniversitySimulator.currentCooperativeCount));
        currentExchangeCount.setText(String.valueOf(UniversitySimulator.currentExchangeCount));
        averageNonverbalDuration.setText(String.format("%.02f", UniversitySimulator.averageNonverbalDuration));
        averageCooperativeDuration.setText(String.format("%.02f", UniversitySimulator.averageCooperativeDuration));
        averageExchangeDuration.setText(String.format("%.02f", UniversitySimulator.averageExchangeDuration));
        currentStudentStudentCount.setText(String.valueOf(UniversitySimulator.currentStudentStudentCount));
        currentStudentProfCount.setText(String.valueOf(UniversitySimulator.currentStudentProfCount));
        currentStudentGuardCount.setText(String.valueOf(UniversitySimulator.currentStudentGuardCount));
        currentStudentJanitorCount.setText(String.valueOf(UniversitySimulator.currentStudentJanitorCount));
        currentStudentStaffCount.setText(String.valueOf(UniversitySimulator.currentStudentStaffCount));
        currentProfProfCount.setText(String.valueOf(UniversitySimulator.currentProfProfCount));
        currentProfGuardCount.setText(String.valueOf(UniversitySimulator.currentProfGuardCount));
        currentProfJanitorCount.setText(String.valueOf(UniversitySimulator.currentProfJanitorCount));
        currentProfStaffCount.setText(String.valueOf(UniversitySimulator.currentProfStaffCount));
        currentJanitorJanitorCount.setText(String.valueOf(UniversitySimulator.currentJanitorJanitorCount));
        currentStaffStaffCount.setText(String.valueOf(UniversitySimulator.currentStaffStaffCount));
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
        if (!Main.universitySimulator.isRunning()) {
            Main.universitySimulator.setRunning(true);
            Main.universitySimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
            exportToCSVButton.setDisable(true);
            exportHeatMapButton.setDisable(true);
        }
        else {
            Main.universitySimulator.setRunning(false);
            playButton.setText("Play");
            exportToCSVButton.setDisable(false);
            exportHeatMapButton.setDisable(false);
        }
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
        maxStudents.setDisable(true);
        maxProfessors.setDisable(true);
        maxCurrentStudents.setDisable(true);
        maxCurrentProfessors.setDisable(true);
        resetToDefaultButton.setDisable(true);
        configureIOSButton.setDisable(true);
        editInteractionButton.setDisable(true);
    }

    public void resetToDefault() {
        nonverbalMean.setText(Integer.toString(UniversityAgentMovement.defaultNonverbalMean));
        nonverbalStdDev.setText(Integer.toString(UniversityAgentMovement.defaultNonverbalStdDev));
        cooperativeMean.setText(Integer.toString(UniversityAgentMovement.defaultCooperativeMean));
        cooperativeStdDev.setText(Integer.toString(UniversityAgentMovement.defaultCooperativeStdDev));
        exchangeMean.setText(Integer.toString(UniversityAgentMovement.defaultExchangeMean));
        exchangeStdDev.setText(Integer.toString(UniversityAgentMovement.defaultExchangeStdDev));
        fieldOfView.setText(Integer.toString(UniversityAgentMovement.defaultFieldOfView));
        maxStudents.setText(Integer.toString(UniversitySimulator.defaultMaxStudents));
        maxProfessors.setText(Integer.toString(UniversitySimulator.defaultMaxProfessors));
        maxCurrentStudents.setText(Integer.toString(UniversitySimulator.defaultMaxCurrentStudents));
        maxCurrentProfessors.setText(Integer.toString(UniversitySimulator.defaultMaxCurrentProfessors));
    }

    public void openIOSLevels() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/UniversityConfigureIOS.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/UniversityEditInteractions.fxml"));
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
            UniversitySimulator.exportToCSV();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void configureParameters(University university) {
        university.setNonverbalMean(Integer.parseInt(nonverbalMean.getText()));
        university.setNonverbalStdDev(Integer.parseInt(nonverbalStdDev.getText()));
        university.setCooperativeMean(Integer.parseInt(cooperativeMean.getText()));
        university.setCooperativeStdDev(Integer.parseInt(cooperativeStdDev.getText()));
        university.setExchangeMean(Integer.parseInt(exchangeMean.getText()));
        university.setExchangeStdDev(Integer.parseInt(exchangeStdDev.getText()));
        university.setFieldOfView(Integer.parseInt(fieldOfView.getText()));
        university.setMAX_STUDENTS(Integer.parseInt(maxStudents.getText()));
        university.setMAX_PROFESSORS(Integer.parseInt(maxProfessors.getText()));
        university.setMAX_CURRENT_STUDENTS(Integer.parseInt(maxCurrentStudents.getText()));
        university.setMAX_CURRENT_PROFESSORS(Integer.parseInt(maxCurrentProfessors.getText()));

        currentProfessorCount.setText(String.valueOf(UniversitySimulator.currentProfessorCount));
        currentStudentCount.setText(String.valueOf(UniversitySimulator.currentStudentCount));
        currentNonverbalCount.setText(String.valueOf(UniversitySimulator.currentNonverbalCount));
        currentCooperativeCount.setText(String.valueOf(UniversitySimulator.currentCooperativeCount));
        currentExchangeCount.setText(String.valueOf(UniversitySimulator.currentExchangeCount));
        averageNonverbalDuration.setText(String.valueOf(UniversitySimulator.averageNonverbalDuration));
        averageCooperativeDuration.setText(String.valueOf(UniversitySimulator.averageCooperativeDuration));
        averageExchangeDuration.setText(String.valueOf(UniversitySimulator.averageExchangeDuration));
        currentStudentStudentCount.setText(String.valueOf(UniversitySimulator.currentStudentStudentCount));
        currentStudentProfCount.setText(String.valueOf(UniversitySimulator.currentStudentProfCount));
        currentStudentGuardCount.setText(String.valueOf(UniversitySimulator.currentStudentGuardCount));
        currentStudentJanitorCount.setText(String.valueOf(UniversitySimulator.currentStudentJanitorCount));
        currentStudentStaffCount.setText(String.valueOf(UniversitySimulator.currentStudentStaffCount));
        currentProfProfCount.setText(String.valueOf(UniversitySimulator.currentProfProfCount));
        currentProfGuardCount.setText(String.valueOf(UniversitySimulator.currentProfGuardCount));
        currentProfJanitorCount.setText(String.valueOf(UniversitySimulator.currentProfJanitorCount));
        currentProfStaffCount.setText(String.valueOf(UniversitySimulator.currentProfStaffCount));
        currentJanitorJanitorCount.setText(String.valueOf(UniversitySimulator.currentJanitorJanitorCount));
        currentStaffStaffCount.setText(String.valueOf(UniversitySimulator.currentStaffStaffCount));
    }

    public boolean validateParameters() {
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxStudents.getText()) >= 0 && Integer.parseInt(maxProfessors.getText()) >= 0
                && Integer.parseInt(maxCurrentStudents.getText()) >= 0 && Integer.parseInt(maxCurrentProfessors.getText()) >= 0;

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