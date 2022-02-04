package com.socialsim.controller.university.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.university.graphics.UniversityGraphicsController;
import com.socialsim.controller.university.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.university.UniversityAgent;
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
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.plugins.tiff.BaselineTIFFTagSet;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    @FXML private Tab parameters;
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
    @FXML private Button configureIOSButton;
    @FXML private Button editInteractionButton;

    private final double CANVAS_SCALE = 0.7;

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

        int width = 60; // Value may be from 25-100
        int length = 120; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 130 columns
        University university = University.UniversityFactory.create(rows, columns);
        Main.universitySimulator.resetToDefaultConfiguration(university);
        University.configureDefaultIOS();
        university.copyDefaultToIOS();
        University.configureDefaultInteractionTypeChances();
        university.copyDefaultToInteractionTypeChances();
    }

    @FXML
    public void initializeAction() {
        if (Main.universitySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
        University university = Main.universitySimulator.getUniversity();
        this.configureParameters(university);
        university.convertIOSToChances();
        initializeUniversity(university);
        setElements();
        playButton.setDisable(false);
        disableEdits();
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
            if (i < 24 || i > 35) {
                for (int j = 0; j < cols; j++) {
                    if (i < 24 && (j < 60 || j > 71)) {
                        wallPatches.add(university.getPatch(i, j));
                    }
                    else if (i > 35 && (j >= 20/* && j <= 119 */)) {
                        wallPatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        Main.universitySimulator.getUniversity().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 50; j < 60; j++) {
                if (j != 59 || i >= 2 && i <= 5)
                    fBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(fBathroomPatches, 1));

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 9; i < 17; i++) {
            for (int j = 50; j < 60; j++) {
                if (j != 59 || i >= 11  && i <= 14)
                    mBathroomPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> cafeteriaPatches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 96; j < 120; j++) {
                cafeteriaPatches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getCafeterias().add(Cafeteria.cafeteriaFactory.create(cafeteriaPatches, 1));

        List<Patch> classroom1Patches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 5; j < 21; j++) {
                if (i != 23 || j <= 8 || j >= 17) {
                    classroom1Patches.add(university.getPatch(i, j));
                }
            }
        };
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom1Patches, 0));

        List<Patch> classroom2Patches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 25; j < 45; j++) {
                if (i != 23 || j <= 28 || j >= 41) {
                    classroom2Patches.add(university.getPatch(i, j));
                }
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom2Patches, 1));

        List<Patch> classroom3Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 23; j < 39; j++) {
                if (i != 36 || j <= 26 || j >= 35)
                    classroom3Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom3Patches, 2));

        List<Patch> classroom4Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 42; j < 58; j++) {
                if (i != 36 || j <= 45 || j >= 54)
                    classroom4Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom4Patches, 3));

        List<Patch> classroom5Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 61; j < 77; j++) {
                if (i != 36 || j <= 64 || j >= 73)
                    classroom5Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom5Patches, 4));

        List<Patch> classroom6Patches = new ArrayList<>();
        for (int i = 36; i < 57; i++) {
            for (int j = 80; j < 96; j++) {
                if (i != 36 || j <= 83 || j >= 92)
                    classroom6Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getClassrooms().add(Classroom.classroomFactory.create(classroom6Patches, 5));

//        List<Patch> laboratoryPatches = new ArrayList<>();
//        for (int i = 36; i < 56; i++) {
//            for (int j = 99; j < 119; j++) {
//                if (i != 36 || (j >= 107 && j <= 110))
//                    laboratoryPatches.add(university.getPatch(i, j));
//            }
//        }
//        Main.universitySimulator.getUniversity().getLaboratories().add(Laboratory.laboratoryFactory.create(laboratoryPatches, 1));

        List<Patch> studyAreaPatches = new ArrayList<>();
        for (int i = 3; i < 24; i++) {
            for (int j = 74; j < 94; j++) {
                if (i != 23 || (j >= 82 && j <= 85)) {
                    studyAreaPatches.add(university.getPatch(i, j));
                }
            }
        }
        Main.universitySimulator.getUniversity().getStudyAreas().add(StudyArea.studyAreaFactory.create(studyAreaPatches, 1));

        List<Patch> studyArea2Patches = new ArrayList<>();
        for (int i = 36; i < 56; i++) {
            for (int j = 99; j < 119; j++) {
                if (i != 36 || (j >= 107 && j <= 110))
                    studyArea2Patches.add(university.getPatch(i, j));
            }
        }
        Main.universitySimulator.getUniversity().getStudyAreas().add(StudyArea.studyAreaFactory.create(studyArea2Patches, 2));

        List<Patch> benchRightPatches = new ArrayList<>();
        benchRightPatches.add(university.getPatch(43,0));
        benchRightPatches.add(university.getPatch(48,0));
        benchRightPatches.add(university.getPatch(53,0));
        BenchMapper.draw(benchRightPatches, "RIGHT");

        List<Patch> benchLeftPatches = new ArrayList<>();
        benchLeftPatches.add(university.getPatch(38,19));
        benchLeftPatches.add(university.getPatch(43,19));
        benchLeftPatches.add(university.getPatch(48,19));
        benchLeftPatches.add(university.getPatch(53,19));
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
        boardRightPatches.add(university.getPatch(6,25)); // Classroom 2
        boardRightPatches.add(university.getPatch(14,25)); // Classroom 2
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

//        List<Patch> boardUpPatches = new ArrayList<>();
//        boardUpPatches.add(university.getPatch(55,102)); // Laboratory
//        boardUpPatches.add(university.getPatch(55,110)); // Laboratory
//        BoardMapper.draw(boardUpPatches, "UP");

        List<Patch> bulletinRightPatches = new ArrayList<>();
        bulletinRightPatches.add(university.getPatch(36,0));
        BulletinMapper.draw(bulletinRightPatches, "RIGHT");

        List<Patch> bulletinDownPatches = new ArrayList<>();
        bulletinDownPatches.add(university.getPatch(23,10));
        bulletinDownPatches.add(university.getPatch(23,32));
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
                for (int j = 30; j < 44; j++) {
                    if (j % 2 == 0) {
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
        doorDownPatches.add(university.getPatch(23,17)); // Classroom 1
        doorDownPatches.add(university.getPatch(23,25)); // Classroom 2
        doorDownPatches.add(university.getPatch(23,41)); // Classroom 2
        doorDownPatches.add(university.getPatch(23,82)); // Study Room
        DoorMapper.draw(doorDownPatches, "DOWN");

        List<Patch> doorUpPatches = new ArrayList<>();
        doorUpPatches.add(university.getPatch(36,23)); // Classroom 3
        doorUpPatches.add(university.getPatch(36,35)); // Classroom 3
        doorUpPatches.add(university.getPatch(36,42)); // Classroom 4
        doorUpPatches.add(university.getPatch(36,54)); // Classroom 4
        doorUpPatches.add(university.getPatch(36,61)); // Classroom 5
        doorUpPatches.add(university.getPatch(36,73)); // Classroom 5
        doorUpPatches.add(university.getPatch(36,80)); // Classroom 6
        doorUpPatches.add(university.getPatch(36,92)); // Classroom 6
        doorUpPatches.add(university.getPatch(36,107)); // Laboratory
        DoorMapper.draw(doorUpPatches, "UP");

        List<Patch> doorRightPatches = new ArrayList<>();
        doorRightPatches.add(university.getPatch(11, 59)); // mBathroom
        doorRightPatches.add(university.getPatch(2, 59)); // fBathroom
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
        toiletPatches.add(university.getPatch(0,50));
        toiletPatches.add(university.getPatch(0,52));
        toiletPatches.add(university.getPatch(0,54));
        toiletPatches.add(university.getPatch(7,50));
        toiletPatches.add(university.getPatch(7,52));
        toiletPatches.add(university.getPatch(7,54));
        toiletPatches.add(university.getPatch(9,50));
        toiletPatches.add(university.getPatch(9,52));
        toiletPatches.add(university.getPatch(9,54));
        toiletPatches.add(university.getPatch(16,50));
        toiletPatches.add(university.getPatch(16,52));
        toiletPatches.add(university.getPatch(16,54));
        ToiletMapper.draw(toiletPatches);
      
        List<Patch> sinkPatches = new ArrayList<>();
        sinkPatches.add(university.getPatch(0,56));
        sinkPatches.add(university.getPatch(0,57));
        sinkPatches.add(university.getPatch(0,58));
        sinkPatches.add(university.getPatch(9,56));
        sinkPatches.add(university.getPatch(9,57));
        sinkPatches.add(university.getPatch(9,58));
        SinkMapper.draw(sinkPatches);

//        List<Patch> labTablePatches = new ArrayList<>();
//        labTablePatches.add(university.getPatch(39,102));
//        labTablePatches.add(university.getPatch(39,112));
//        labTablePatches.add(university.getPatch(42,102));
//        labTablePatches.add(university.getPatch(42,112));
//        labTablePatches.add(university.getPatch(45,102));
//        labTablePatches.add(university.getPatch(45,112));
//        labTablePatches.add(university.getPatch(48,102));
//        labTablePatches.add(university.getPatch(48,112));
//        labTablePatches.add(university.getPatch(51,102));
//        labTablePatches.add(university.getPatch(51,112));
//        LabTableMapper.draw(labTablePatches);

        List<Patch> profTableRightPatches = new ArrayList<>();
        profTableRightPatches.add(university.getPatch(12,7)); // Classroom 1
        profTableRightPatches.add(university.getPatch(12,27)); // Classroom 2
        ProfTableMapper.draw(profTableRightPatches, "RIGHT");

        List<Patch> profTableLeftPatches = new ArrayList<>();
        profTableLeftPatches.add(university.getPatch(46,36)); // Classroom 3
        profTableLeftPatches.add(university.getPatch(46,55)); // Classroom 4
        profTableLeftPatches.add(university.getPatch(46,74)); // Classroom 5
        profTableLeftPatches.add(university.getPatch(46,93)); // Classroom 6
        ProfTableMapper.draw(profTableLeftPatches, "LEFT");

//        List<Patch> profTableUpPatches = new ArrayList<>();
//        profTableUpPatches.add(university.getPatch(52,108)); // Laboratory
//        ProfTableMapper.draw(profTableUpPatches, "UP");

        List<Patch> studyTablePatches = new ArrayList<>();
        for (int i = 5; i < 17; i++) {
            if (i == 5 || i == 8 || i == 13 || i == 16) {
                for (int j = 77; j < 82; j++) {
                    if (j == 77 || j == 81) {
                        studyTablePatches.add(university.getPatch(i, j));
                    }
                }
            }
        }
        StudyTableMapper.draw(studyTablePatches, "LEFT");
        studyTablePatches = new ArrayList<>();
        studyTablePatches.add(university.getPatch(12, 86));
        studyTablePatches.add(university.getPatch(20, 86));
        studyTablePatches.add(university.getPatch(12, 90));
        studyTablePatches.add(university.getPatch(20, 90));
        studyTablePatches.add(university.getPatch(4, 86));
        studyTablePatches.add(university.getPatch(8, 86));
        studyTablePatches.add(university.getPatch(4, 90));
        studyTablePatches.add(university.getPatch(8, 90));
        studyTablePatches.add(university.getPatch(16, 86));
        studyTablePatches.add(university.getPatch(16, 90));
        studyTablePatches.add(university.getPatch(20, 76));
        studyTablePatches.add(university.getPatch(20, 80));
        StudyTableMapper.draw(studyTablePatches, "UP");

        List<Patch> studyTable2Patches = new ArrayList<>();
//        for (int i = 5; i < 17; i++) {
//            if (i == 5 || i == 8 || i == 13 || i == 16) {
//                for (int j = 77; j < 82; j++) {
//                    if (j == 77 || j == 81) {
//                        studyTable2Patches.add(university.getPatch(i, j));
//                    }
//                }
//            }
//        }
//        StudyTableMapper.draw(studyTablePatches, "LEFT");
//        studyTable2Patches = new ArrayList<>();
        studyTable2Patches.add(university.getPatch(40, 101));
        studyTable2Patches.add(university.getPatch(44, 101));
        studyTable2Patches.add(university.getPatch(48, 101));
        studyTable2Patches.add(university.getPatch(52, 101));

        studyTable2Patches.add(university.getPatch(40, 105));
        studyTable2Patches.add(university.getPatch(44, 105));
        studyTable2Patches.add(university.getPatch(48, 105));
        studyTable2Patches.add(university.getPatch(52, 105));

        studyTable2Patches.add(university.getPatch(40, 111));
        studyTable2Patches.add(university.getPatch(44, 111));
        studyTable2Patches.add(university.getPatch(48, 111));
        studyTable2Patches.add(university.getPatch(52, 111));

        studyTable2Patches.add(university.getPatch(40, 115));
        studyTable2Patches.add(university.getPatch(44, 115));
        studyTable2Patches.add(university.getPatch(48, 115));
        studyTable2Patches.add(university.getPatch(52, 115));
        StudyTableMapper.draw(studyTable2Patches, "UP");

        List<Patch> trashPatches = new ArrayList<>();
        trashPatches.add(university.getPatch(36,19));
        trashPatches.add(university.getPatch(27,0));
        trashPatches.add(university.getPatch(29,0));
        trashPatches.add(university.getPatch(31,0));
        trashPatches.add(university.getPatch(18,71));
        trashPatches.add(university.getPatch(20,71));
        trashPatches.add(university.getPatch(22,71));
        trashPatches.add(university.getPatch(24,95));
        TrashMapper.draw(trashPatches);

        List<Patch> universityGateExitPatches = new ArrayList<>();
        universityGateExitPatches.add(university.getPatch(59,4));
        UniversityGateMapper.draw(universityGateExitPatches, UniversityGate.UniversityGateMode.EXIT);

        List<Patch> universityGateEntrancePatches = new ArrayList<>();
        universityGateEntrancePatches.add(university.getPatch(59,12));
        UniversityGateMapper.draw(universityGateEntrancePatches, UniversityGate.UniversityGateMode.ENTRANCE);

        List<Patch> fountainPatches = new ArrayList<>();
        fountainPatches.add(university.getPatch(20,60));
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

    public void generateHeatMap() {
        int[][] currentPatchCount = UniversitySimulator.currentPatchCount;
        int maxPatchCount = Arrays.stream(currentPatchCount).flatMapToInt(Arrays::stream).max().getAsInt();
        for(int i = 0; i < currentPatchCount.length; i++){
            for (int j = 0; j < currentPatchCount[0].length; j++){
                System.out.println(currentPatchCount.length);
                System.out.println(currentPatchCount[0].length);
                int patchRGBCount = 255 - currentPatchCount[i][j] * 255 / maxPatchCount;
                foregroundCanvas.getGraphicsContext2D().setFill(Color.rgb(255, patchRGBCount, patchRGBCount));
                foregroundCanvas.getGraphicsContext2D().fillRect(j * UniversityGraphicsController.tileSize, i * UniversityGraphicsController.tileSize, UniversityGraphicsController.tileSize, UniversityGraphicsController.tileSize);
            }
        }
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.universitySimulator.getSimulationTime().getTime();
        long elapsedTime = Main.universitySimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS) / 5;
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
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
        UniversityAgent.clearUniversityAgentCounts();
        clearUniversity(Main.universitySimulator.getUniversity());
        Main.universitySimulator.spawnInitialAgents(Main.universitySimulator.getUniversity());
        drawUniversityViewForeground(Main.universitySimulator.getUniversity(), false); // Redraw the canvas
        if (Main.universitySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
        enableEdits();
    }

    public static void clearUniversity(University university) {
        for (UniversityAgent agent : university.getAgents()) { // Remove the relationship between the patch and the agents
            agent.getAgentMovement().getCurrentPatch().getAgents().clear();
            agent.getAgentMovement().setCurrentPatch(null);
        }

        // Remove all the agents
        university.getAgents().removeAll(university.getAgents());
        university.getAgents().clear();
        university.getAgentPatchSet().clear();
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
    public void enableEdits(){
        nonverbalMean.setDisable(false);
        nonverbalStdDev.setDisable(false);
        cooperativeMean.setDisable(false);
        cooperativeStdDev.setDisable(false);
        exchangeMean.setDisable(false);
        exchangeStdDev.setDisable(false);
        fieldOfView.setDisable(false);
        maxStudents.setDisable(false);
        maxProfessors.setDisable(false);
        maxCurrentStudents.setDisable(false);
        maxCurrentProfessors.setDisable(false);

        resetToDefaultButton.setDisable(false);
        configureIOSButton.setDisable(false);
        editInteractionButton.setDisable(false);
    }

    public void resetToDefault(){
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

    public void openIOSLevels(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/ConfigureIOS.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/EditInteractions.fxml"));
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
    public void configureParameters(University university){
        university.setNonverbalMean(Integer.parseInt(nonverbalMean.getText()));
        university.setNonverbalStdDev(Integer.parseInt(nonverbalStdDev.getText()));
        university.setCooperativeMean(Integer.parseInt(cooperativeMean.getText()));
        university.setCooperativeStdDev(Integer.parseInt(cooperativeStdDev.getText()));
        university.setExchangeMean(Integer.parseInt(exchangeMean.getText()));
        university.setExchangeStdDev(Integer.parseInt(exchangeStdDev.getText()));
        university.setFieldOfView(Integer.parseInt(fieldOfView.getText()));
        UniversitySimulator.MAX_STUDENTS = Integer.parseInt(maxStudents.getText());
        UniversitySimulator.MAX_PROFESSORS = Integer.parseInt(maxProfessors.getText());
        UniversitySimulator.MAX_CURRENT_STUDENTS = Integer.parseInt(maxCurrentStudents.getText());
        UniversitySimulator.MAX_CURRENT_PROFESSORS = Integer.parseInt(maxCurrentProfessors.getText());
        System.out.println(university.getNonverbalMean());
        System.out.println(university.getNonverbalStdDev());
        System.out.println(university.getCooperativeMean());
        System.out.println(university.getCooperativeStdDev());
        System.out.println(university.getExchangeMean());
        System.out.println(university.getExchangeStdDev());
        System.out.println(university.getFieldOfView());
        System.out.println(UniversitySimulator.MAX_STUDENTS);
        System.out.println(UniversitySimulator.MAX_PROFESSORS);
        System.out.println(UniversitySimulator.MAX_CURRENT_STUDENTS);
        System.out.println(UniversitySimulator.MAX_CURRENT_PROFESSORS);
    }
}