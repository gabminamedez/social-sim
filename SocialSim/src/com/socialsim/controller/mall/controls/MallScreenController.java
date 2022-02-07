package com.socialsim.controller.mall.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.mall.graphics.MallGraphicsController;
import com.socialsim.controller.mall.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.agent.mall.MallAgentMovement;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchfield.*;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.mall.MallSimulator;
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

public class MallScreenController extends ScreenController {

    @FXML
    private ScrollPane scrollPane;
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
    @FXML private TextField maxFamily;
    @FXML private TextField maxFriend;
    @FXML private TextField maxCouple;
    @FXML private TextField maxAlone;
    @FXML private TextField fieldOfView;
    @FXML private Button configureIOSButton;
    @FXML private Button editInteractionButton;

    @FXML private Label currentPatronCount;
    @FXML private Label currentNonverbalCount;
    @FXML private Label currentCooperativeCount;
    @FXML private Label currentExchangeCount;
    @FXML private Label totalFamilyCount;
    @FXML private Label totalFriendsCount;
    @FXML private Label totalAloneCount;
    @FXML private Label totalCoupleCount;
    @FXML private Label averageNonverbalDuration;
    @FXML private Label averageCooperativeDuration;
    @FXML private Label averageExchangeDuration;
    @FXML private Label currentPatronPatronCount;
    @FXML private Label currentPatronStaffStoreCount;
    @FXML private Label currentPatronStaffRestoCount;
    @FXML private Label currentPatronStaffKioskCount;
    @FXML private Label currentPatronGuardCount;
    @FXML private Label currentStaffStoreStaffStoreCount;
    @FXML private Label currentStaffRestoStaffRestoCount;

    private final double CANVAS_SCALE = 0.5;

    public MallScreenController() {
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
        Mall mall = Mall.MallFactory.create(rows, columns);
        Main.mallSimulator.resetToDefaultConfiguration(mall);
        Mall.configureDefaultIOS();
        mall.copyDefaultToIOS();
        Mall.configureDefaultInteractionTypeChances();
        mall.copyDefaultToInteractionTypeChances();
    }

    @FXML
    public void initializeAction() {
        if (Main.mallSimulator.isRunning()) {
            playAction();
            playButton.setSelected(false);
        }
        if (validateParameters()){
            Mall mall = Main.mallSimulator.getMall();
            this.configureParameters(mall);
            initializeMall(mall);
            mall.convertIOSToChances();
            setElements();
            playButton.setDisable(false);
            disableEdits();
        }
    }

    public void initializeMall(Mall mall) {
        MallGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.mallSimulator.getMall().getRows();
        mapMall();
        Main.mallSimulator.spawnInitialAgents(mall);
        drawInterface();
    }

    public void mapMall() {
        Mall mall = Main.mallSimulator.getMall();
        int rows = mall.getRows();
        int cols = mall.getColumns();

        List<Patch> wallPatches = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            for (int j = 10; j < 50; j++) {
                wallPatches.add(mall.getPatch(i, j));
            }
        }
        for (int i = 40; i < 60; i++) {
            for (int j = 10; j < 50; j++) {
                wallPatches.add(mall.getPatch(i, j));
            }
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 50; j < 120; j++) {
                wallPatches.add(mall.getPatch(i, j));
            }
        }
        for (int i = 45; i < 60; i++) {
            for (int j = 50; j < 120; j++) {
                wallPatches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 10; j < 30; j++) {
                fBathroomPatches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getBathrooms().add(Bathroom.bathroomFactory.create(fBathroomPatches, 1));

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 53; i < 60; i++) {
            for (int j = 10; j < 30; j++) {
                mBathroomPatches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> resto1Patches = new ArrayList<>();
        for (int i = 40; i < 60; i++) {
            for (int j = 52; j < 72; j++) {
                resto1Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getRestaurants().add(Restaurant.restaurantFactory.create(resto1Patches, 1));

        List<Patch> resto2Patches = new ArrayList<>();
        for (int i = 40; i < 60; i++) {
            for (int j = 74; j < 94; j++) {
                resto2Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getRestaurants().add(Restaurant.restaurantFactory.create(resto2Patches, 2));

        List<Patch> dining1Patches = new ArrayList<>();
        for (int i = 25; i < 35; i++) {
            for (int j = 96; j < 116; j++) {
                dining1Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getDinings().add(Dining.diningFactory.create(dining1Patches, 1));

        List<Patch> store1Patches = new ArrayList<>();
        for (int i = 10; i < 20; i++) {
            for (int j = 10; j < 30; j++) {
                store1Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store1Patches, 1));

        List<Patch> store2Patches = new ArrayList<>();
        for (int i = 5; i < 20; i++) {
            for (int j = 35; j < 50; j++) {
                store2Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store2Patches, 2));

        List<Patch> store3Patches = new ArrayList<>();
        for (int i = 40; i < 50; i++) {
            for (int j = 10; j < 30; j++) {
                store3Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store3Patches, 3));

        List<Patch> store4Patches = new ArrayList<>();
        for (int i = 40; i < 55; i++) {
            for (int j = 35; j < 50; j++) {
                store4Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store4Patches, 4));

        List<Patch> store5Patches = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 51; j < 61; j++) {
                store5Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store5Patches, 5));

        List<Patch> store6Patches = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 62; j < 72; j++) {
                store6Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store6Patches, 6));

        List<Patch> store7Patches = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 75; j < 95; j++) {
                store7Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store7Patches, 7));

        List<Patch> store8Patches = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 98; j < 108; j++) {
                store8Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store8Patches, 8));

        List<Patch> store9Patches = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 109; j < 119; j++) {
                store9Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store9Patches, 9));

        List<Patch> store10Patches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 96; j < 106; j++) {
                store10Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store10Patches, 10));

        List<Patch> store11Patches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 108; j < 118; j++) {
                store11Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store11Patches, 11));

        List<Patch> showcasePatches = new ArrayList<>();
        for (int i = 18; i < 37; i++) {
            for (int j = 52; j < 92; j++) {
                showcasePatches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getShowcases().add(Showcase.showcaseFactory.create(showcasePatches, 1));

        List<Patch> storeCounterPatches = new ArrayList<>();
        storeCounterPatches.add(mall.getPatch(11,16)); // Store 1
        storeCounterPatches.add(mall.getPatch(6,38)); // Store 2
        storeCounterPatches.add(mall.getPatch(47,16)); // Store 3
        storeCounterPatches.add(mall.getPatch(52,38)); // Store 4
        storeCounterPatches.add(mall.getPatch(1,52)); // Store 5
        storeCounterPatches.add(mall.getPatch(1,63)); // Store 6
        storeCounterPatches.add(mall.getPatch(1,81)); // Store 7
        storeCounterPatches.add(mall.getPatch(1,99)); // Store 8
        storeCounterPatches.add(mall.getPatch(1,110)); // Store 9
        storeCounterPatches.add(mall.getPatch(57,97)); // Store 10
        storeCounterPatches.add(mall.getPatch(57,109)); // Store 11
        StoreCounterMapper.draw(storeCounterPatches);

        List<Patch> aisleDownPatches = new ArrayList<>();
        aisleDownPatches.add(mall.getPatch(16,19)); // Store 1; 0
        aisleDownPatches.add(mall.getPatch(19,19)); // Store 1; 1
        aisleDownPatches.add(mall.getPatch(11,38)); // Store 2; 2
        aisleDownPatches.add(mall.getPatch(11,43)); // Store 2; 3
        aisleDownPatches.add(mall.getPatch(17,38)); // Store 2; 4
        aisleDownPatches.add(mall.getPatch(17,43)); // Store 2; 5
        aisleDownPatches.add(mall.getPatch(40,12)); // Store 3; 6
        aisleDownPatches.add(mall.getPatch(40,18)); // Store 3; 7
        aisleDownPatches.add(mall.getPatch(40,24)); // Store 3; 8
        aisleDownPatches.add(mall.getPatch(43,12)); // Store 3; 9
        aisleDownPatches.add(mall.getPatch(43,18)); // Store 3; 10
        aisleDownPatches.add(mall.getPatch(43,24)); // Store 3; 11
        aisleDownPatches.add(mall.getPatch(43,38)); // Store 4; 12
        aisleDownPatches.add(mall.getPatch(46,38)); // Store 4; 13
        aisleDownPatches.add(mall.getPatch(43,43)); // Store 4; 14
        aisleDownPatches.add(mall.getPatch(46,43)); // Store 4; 15
        aisleDownPatches.add(mall.getPatch(6,54)); // Store 5; 16
        aisleDownPatches.add(mall.getPatch(12,54)); // Store 5; 17
        aisleDownPatches.add(mall.getPatch(6,65)); // Store 6; 18
        aisleDownPatches.add(mall.getPatch(12,65)); // Store 6; 19
        aisleDownPatches.add(mall.getPatch(6,83)); // Store 7; 20
        aisleDownPatches.add(mall.getPatch(9,83)); // Store 7; 21
        aisleDownPatches.add(mall.getPatch(12,83)); // Store 7; 22
        aisleDownPatches.add(mall.getPatch(6,101)); // Store 8; 23
        aisleDownPatches.add(mall.getPatch(12,101)); // Store 8; 24
        aisleDownPatches.add(mall.getPatch(6,112)); // Store 9; 25
        aisleDownPatches.add(mall.getPatch(12,112)); // Store 9; 26
        aisleDownPatches.add(mall.getPatch(47,99)); // Store 10; 27
        aisleDownPatches.add(mall.getPatch(53,99)); // Store 10; 28
        aisleDownPatches.add(mall.getPatch(47,111)); // Store 11; 29
        aisleDownPatches.add(mall.getPatch(53,111)); // Store 11; 30
        StoreAisleMapper.draw(aisleDownPatches, "DOWN");

        List<Patch> aisleRightPatches = new ArrayList<>();
        aisleRightPatches.add(mall.getPatch(16,10)); // Store 1; 31
        aisleRightPatches.add(mall.getPatch(16,13)); // Store 1; 32
        aisleRightPatches.add(mall.getPatch(16,16)); // Store 1; 33
        aisleRightPatches.add(mall.getPatch(16,26)); // Store 1; 34
        aisleRightPatches.add(mall.getPatch(16,29)); // Store 1; 35
        aisleRightPatches.add(mall.getPatch(12,35)); // Store 2; 36
        aisleRightPatches.add(mall.getPatch(12,49)); // Store 2; 37
        aisleRightPatches.add(mall.getPatch(43,35)); // Store 4; 38
        aisleRightPatches.add(mall.getPatch(45,49)); // Store 4; 39
        aisleRightPatches.add(mall.getPatch(8,51)); // Store 5; 40
        aisleRightPatches.add(mall.getPatch(8,60)); // Store 5; 41
        aisleRightPatches.add(mall.getPatch(8,62)); // Store 6; 42
        aisleRightPatches.add(mall.getPatch(8,71)); // Store 6; 43
        aisleRightPatches.add(mall.getPatch(8,75)); // Store 7; 44
        aisleRightPatches.add(mall.getPatch(8,94)); // Store 7; 45
        aisleRightPatches.add(mall.getPatch(8,79)); // Store 7; 46
        aisleRightPatches.add(mall.getPatch(8,90)); // Store 7; 47
        aisleRightPatches.add(mall.getPatch(8,98)); // Store 8; 48
        aisleRightPatches.add(mall.getPatch(8,107)); // Store 8; 49
        aisleRightPatches.add(mall.getPatch(8,109)); // Store 9; 50
        aisleRightPatches.add(mall.getPatch(8,118)); // Store 9; 51
        aisleRightPatches.add(mall.getPatch(47,96)); // Store 10; 52
        aisleRightPatches.add(mall.getPatch(47,105)); // Store 10; 53
        aisleRightPatches.add(mall.getPatch(47,108)); // Store 11; 54
        aisleRightPatches.add(mall.getPatch(47,117)); // Store 11; 55
        StoreAisleMapper.draw(aisleRightPatches, "RIGHT");

        List<Patch> mallGateExitPatches = new ArrayList<>();
        mallGateExitPatches.add(mall.getPatch(24,0));
        MallGateMapper.draw(mallGateExitPatches, MallGate.MallGateMode.EXIT);

        List<Patch> mallGateEntrancePatches = new ArrayList<>();
        mallGateEntrancePatches.add(mall.getPatch(32,0));
        MallGateMapper.draw(mallGateEntrancePatches, MallGate.MallGateMode.ENTRANCE);

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(mall.getPatch(32,2));
        SecurityMapper.draw(securityPatches);

        List<Patch> digitalPatches = new ArrayList<>();
        digitalPatches.add(mall.getPatch(27,10));
        digitalPatches.add(mall.getPatch(27,94));
        DigitalMapper.draw(digitalPatches);

        List<Patch> kioskPatches = new ArrayList<>();
        kioskPatches.add(mall.getPatch(21,53)); // 0
        kioskPatches.add(mall.getPatch(21,70)); // 1
        kioskPatches.add(mall.getPatch(21,87)); // 2
        kioskPatches.add(mall.getPatch(32,53)); // 3
        kioskPatches.add(mall.getPatch(32,70)); // 4
        kioskPatches.add(mall.getPatch(32,87)); // 5
        kioskPatches.add(mall.getPatch(26,97)); // 6
        KioskMapper.draw(kioskPatches);

        List<Patch> tableUpPatches = new ArrayList<>();
        tableUpPatches.add(mall.getPatch(47,52)); // 0
        tableUpPatches.add(mall.getPatch(47,58)); // 1
        tableUpPatches.add(mall.getPatch(47,64)); // 2
        tableUpPatches.add(mall.getPatch(47,70)); // 3
        tableUpPatches.add(mall.getPatch(50,52)); // 4
        tableUpPatches.add(mall.getPatch(50,58)); // 5
        tableUpPatches.add(mall.getPatch(50,64)); // 6
        tableUpPatches.add(mall.getPatch(50,70)); // 7
        tableUpPatches.add(mall.getPatch(53,52)); // 8
        tableUpPatches.add(mall.getPatch(53,58)); // 9
        tableUpPatches.add(mall.getPatch(53,64)); // 10
        tableUpPatches.add(mall.getPatch(53,70)); // 11
        tableUpPatches.add(mall.getPatch(56,52)); // 12
        tableUpPatches.add(mall.getPatch(56,58)); // 13
        tableUpPatches.add(mall.getPatch(56,64)); // 14
        tableUpPatches.add(mall.getPatch(56,70)); // 15
        tableUpPatches.add(mall.getPatch(47,74)); // 16
        tableUpPatches.add(mall.getPatch(47,80)); // 17
        tableUpPatches.add(mall.getPatch(47,86)); // 18
        tableUpPatches.add(mall.getPatch(47,92)); // 19
        tableUpPatches.add(mall.getPatch(50,74)); // 20
        tableUpPatches.add(mall.getPatch(50,80)); // 21
        tableUpPatches.add(mall.getPatch(50,86)); // 22
        tableUpPatches.add(mall.getPatch(50,92)); // 23
        tableUpPatches.add(mall.getPatch(53,74)); // 24
        tableUpPatches.add(mall.getPatch(53,80)); // 25
        tableUpPatches.add(mall.getPatch(53,86)); // 26
        tableUpPatches.add(mall.getPatch(53,92)); // 27
        tableUpPatches.add(mall.getPatch(56,74)); // 28
        tableUpPatches.add(mall.getPatch(56,80)); // 29
        tableUpPatches.add(mall.getPatch(56,86)); // 30
        tableUpPatches.add(mall.getPatch(56,92)); // 31
        tableUpPatches.add(mall.getPatch(27,103)); // 32
        tableUpPatches.add(mall.getPatch(27,107)); // 33
        tableUpPatches.add(mall.getPatch(27,111)); // 34
        TableMapper.draw(tableUpPatches, "UP");

        List<Patch> tableRightPatches = new ArrayList<>();
        tableRightPatches.add(mall.getPatch(42,54)); // 35
        tableRightPatches.add(mall.getPatch(42,58)); // 36
        tableRightPatches.add(mall.getPatch(42,62)); // 37
        tableRightPatches.add(mall.getPatch(42,66)); // 38
        tableRightPatches.add(mall.getPatch(42,70)); // 39

        tableRightPatches.add(mall.getPatch(42,76)); // 40
        tableRightPatches.add(mall.getPatch(42,80)); // 41
        tableRightPatches.add(mall.getPatch(42,84)); // 42
        tableRightPatches.add(mall.getPatch(42,88)); // 43
        tableRightPatches.add(mall.getPatch(42,92)); // 44

        tableRightPatches.add(mall.getPatch(31,103)); // 45
        tableRightPatches.add(mall.getPatch(31,106)); // 46
        tableRightPatches.add(mall.getPatch(31,109)); // 47
        tableRightPatches.add(mall.getPatch(31,112)); // 48
        TableMapper.draw(tableRightPatches, "RIGHT");

        List<Patch> trashPatches = new ArrayList<>();
        trashPatches.add(mall.getPatch(50,9));
        trashPatches.add(mall.getPatch(9,9));
        trashPatches.add(mall.getPatch(44,50));
        trashPatches.add(mall.getPatch(44,95));
        trashPatches.add(mall.getPatch(15,119));
        trashPatches.add(mall.getPatch(44,119));
        TrashMapper.draw(trashPatches);

        List<Patch> plantPatches = new ArrayList<>();
        plantPatches.add(mall.getPatch(8,9));
        plantPatches.add(mall.getPatch(51,9));
        plantPatches.add(mall.getPatch(20,32));
        plantPatches.add(mall.getPatch(39,32));
        plantPatches.add(mall.getPatch(18,52));
        plantPatches.add(mall.getPatch(36,52));
        plantPatches.add(mall.getPatch(18,91));
        plantPatches.add(mall.getPatch(36,91));

        plantPatches.add(mall.getPatch(29,18));
        plantPatches.add(mall.getPatch(30,18));
        plantPatches.add(mall.getPatch(29,19));
        plantPatches.add(mall.getPatch(30,19));
        plantPatches.add(mall.getPatch(29,20));
        plantPatches.add(mall.getPatch(30,20));
        plantPatches.add(mall.getPatch(29,38));
        plantPatches.add(mall.getPatch(30,38));
        plantPatches.add(mall.getPatch(29,39));
        plantPatches.add(mall.getPatch(30,39));
        plantPatches.add(mall.getPatch(29,40));
        plantPatches.add(mall.getPatch(30,40));
        plantPatches.add(mall.getPatch(20,104));
        plantPatches.add(mall.getPatch(39,104));
        PlantMapper.draw(plantPatches);

        List<Patch> benchUpPatches = new ArrayList<>();
        benchUpPatches.add(mall.getPatch(28,18));
        benchUpPatches.add(mall.getPatch(31,18));
        benchUpPatches.add(mall.getPatch(28,38));
        benchUpPatches.add(mall.getPatch(31,38));
        benchUpPatches.add(mall.getPatch(20,100));
        benchUpPatches.add(mall.getPatch(20,105));
        benchUpPatches.add(mall.getPatch(39,100));
        benchUpPatches.add(mall.getPatch(39,105));
        BenchMapper.draw(benchUpPatches, "UP");

        List<Patch> benchRightPatches = new ArrayList<>();
        benchRightPatches.add(mall.getPatch(28,17));
        benchRightPatches.add(mall.getPatch(28,21));
        benchRightPatches.add(mall.getPatch(28,37));
        benchRightPatches.add(mall.getPatch(28,41));
        BenchMapper.draw(benchRightPatches, "RIGHT");

        List<Patch> toiletPatches = new ArrayList<>();
        toiletPatches.add(mall.getPatch(0, 12));
        toiletPatches.add(mall.getPatch(0, 14));
        toiletPatches.add(mall.getPatch(0, 16));
        toiletPatches.add(mall.getPatch(0, 18));
        toiletPatches.add(mall.getPatch(0, 21));
        toiletPatches.add(mall.getPatch(0, 23));
        toiletPatches.add(mall.getPatch(0, 25));
        toiletPatches.add(mall.getPatch(0, 27));
        toiletPatches.add(mall.getPatch(59, 12));
        toiletPatches.add(mall.getPatch(59, 14));
        toiletPatches.add(mall.getPatch(59, 16));
        toiletPatches.add(mall.getPatch(59, 18));
        toiletPatches.add(mall.getPatch(59, 21));
        toiletPatches.add(mall.getPatch(59, 23));
        toiletPatches.add(mall.getPatch(59, 25));
        toiletPatches.add(mall.getPatch(59, 27));
        ToiletMapper.draw(toiletPatches);

        List<Patch> sinkPatches = new ArrayList<>();
        sinkPatches.add(mall.getPatch(53, 12));
        sinkPatches.add(mall.getPatch(53, 14));
        sinkPatches.add(mall.getPatch(53, 16));
        sinkPatches.add(mall.getPatch(53, 18));
        sinkPatches.add(mall.getPatch(53, 21));
        sinkPatches.add(mall.getPatch(53, 23));
        sinkPatches.add(mall.getPatch(53, 25));
        sinkPatches.add(mall.getPatch(53, 27));
        sinkPatches.add(mall.getPatch(6, 12));
        sinkPatches.add(mall.getPatch(6, 14));
        sinkPatches.add(mall.getPatch(6, 16));
        sinkPatches.add(mall.getPatch(6, 18));
        sinkPatches.add(mall.getPatch(6, 21));
        sinkPatches.add(mall.getPatch(6, 23));
        sinkPatches.add(mall.getPatch(6, 25));
        sinkPatches.add(mall.getPatch(6, 27));
        SinkMapper.draw(sinkPatches);
    }

    private void drawInterface() {
        drawMallViewBackground(Main.mallSimulator.getMall()); // Initially draw the Mall environment
        drawMallViewForeground(Main.mallSimulator.getMall(), false); // Then draw the agents in the Mall
    }

    public void drawMallViewBackground(Mall mall) { // Draw the mall view background
        MallGraphicsController.requestDrawMallView(stackPane, mall, MallGraphicsController.tileSize, true, false);
    }

    public void drawMallViewForeground(Mall mall, boolean speedAware) { // Draw the mall view foreground
        MallGraphicsController.requestDrawMallView(stackPane, mall, MallGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
        Platform.runLater(this::updateStatistics);
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.mallSimulator.getSimulationTime().getTime();
        long elapsedTime = Main.mallSimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS) / 5;
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
    }

    public void updateStatistics() {
        currentPatronCount.setText(String.valueOf(MallSimulator.currentPatronCount));
        currentNonverbalCount.setText(String.valueOf(MallSimulator.currentNonverbalCount));
        currentCooperativeCount.setText(String.valueOf(MallSimulator.currentCooperativeCount));
        currentExchangeCount.setText(String.valueOf(MallSimulator.currentExchangeCount));
        totalFamilyCount.setText(String.valueOf(MallSimulator.totalFamilyCount));
        totalFriendsCount.setText(String.valueOf(MallSimulator.totalFriendsCount));
        totalAloneCount.setText(String.valueOf(MallSimulator.totalAloneCount));
        totalCoupleCount.setText(String.valueOf(MallSimulator.totalCoupleCount));
        averageNonverbalDuration.setText(String.format("%.02f", MallSimulator.averageNonverbalDuration));
        averageCooperativeDuration.setText(String.format("%.02f", MallSimulator.averageCooperativeDuration));
        averageExchangeDuration.setText(String.format("%.02f", MallSimulator.averageExchangeDuration));
        currentPatronPatronCount.setText(String.valueOf(MallSimulator.currentPatronPatronCount));
        currentPatronStaffStoreCount.setText(String.valueOf(MallSimulator.currentPatronStaffStoreCount));
        currentPatronStaffRestoCount.setText(String.valueOf(MallSimulator.currentPatronStaffRestoCount));
        currentPatronStaffKioskCount.setText(String.valueOf(MallSimulator.currentPatronStaffKioskCount));
        currentPatronGuardCount.setText(String.valueOf(MallSimulator.currentPatronGuardCount));
        currentStaffStoreStaffStoreCount.setText(String.valueOf(MallSimulator.currentStaffStoreStaffStoreCount));
        currentStaffRestoStaffRestoCount.setText(String.valueOf(MallSimulator.currentStaffRestoStaffRestoCount));
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.mallSimulator.getMall().getRows() * MallGraphicsController.tileSize;
        double columnsScaled = Main.mallSimulator.getMall().getColumns() * MallGraphicsController.tileSize;

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
        if (!Main.mallSimulator.isRunning()) { // Not yet running to running (play simulation)
            Main.mallSimulator.setRunning(true);
            Main.mallSimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.mallSimulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        Main.mallSimulator.reset();
        MallAgent.clearMallAgentCounts();
        clearMall(Main.mallSimulator.getMall());
        Main.mallSimulator.spawnInitialAgents(Main.mallSimulator.getMall());
        drawMallViewForeground(Main.mallSimulator.getMall(), false); // Redraw the canvas
        if (Main.mallSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
        enableEdits();
    }

    public void disableEdits(){
        nonverbalMean.setDisable(true);
        nonverbalStdDev.setDisable(true);
        cooperativeMean.setDisable(true);
        cooperativeStdDev.setDisable(true);
        exchangeMean.setDisable(true);
        exchangeStdDev.setDisable(true);
        fieldOfView.setDisable(true);
        maxFamily.setDisable(true);
        maxFriend.setDisable(true);
        maxCouple.setDisable(true);
        maxAlone.setDisable(true);

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
        maxFamily.setDisable(false);
        maxFriend.setDisable(false);
        maxCouple.setDisable(false);
        maxAlone.setDisable(false);

        resetToDefaultButton.setDisable(false);
        configureIOSButton.setDisable(false);
        editInteractionButton.setDisable(false);
    }

    public static void clearMall(Mall mall) {
        for (MallAgent agent : mall.getAgents()) { // Remove the relationship between the patch and the agents
            agent.getAgentMovement().getCurrentPatch().getAgents().clear();
            agent.getAgentMovement().setCurrentPatch(null);
        }

        // Remove all the agents
        mall.getAgents().removeAll(mall.getAgents());
        mall.getAgents().clear();
        mall.getAgentPatchSet().clear();
    }

    @Override
    protected void closeAction() {
    }

    public void resetToDefault(){
        nonverbalMean.setText(Integer.toString(MallAgentMovement.defaultNonverbalMean));
        nonverbalStdDev.setText(Integer.toString(MallAgentMovement.defaultNonverbalStdDev));
        cooperativeMean.setText(Integer.toString(MallAgentMovement.defaultCooperativeMean));
        cooperativeStdDev.setText(Integer.toString(MallAgentMovement.defaultCooperativeStdDev));
        exchangeMean.setText(Integer.toString(MallAgentMovement.defaultExchangeMean));
        exchangeStdDev.setText(Integer.toString(MallAgentMovement.defaultExchangeStdDev));
        fieldOfView.setText(Integer.toString(MallAgentMovement.defaultFieldOfView));
        maxFamily.setText(Integer.toString(MallSimulator.defaultMaxFamily));
        maxFriend.setText(Integer.toString(MallSimulator.defaultMaxFriends));
        maxCouple.setText(Integer.toString(MallSimulator.defaultMaxCouple));
        maxAlone.setText(Integer.toString(MallSimulator.defaultMaxAlone));
    }

    public void openIOSLevels(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/MallConfigureIOS.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/MallEditInteractions.fxml"));
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
    public void configureParameters(Mall mall){
        mall.setNonverbalMean(Integer.parseInt(nonverbalMean.getText()));
        mall.setNonverbalStdDev(Integer.parseInt(nonverbalStdDev.getText()));
        mall.setCooperativeMean(Integer.parseInt(cooperativeMean.getText()));
        mall.setCooperativeStdDev(Integer.parseInt(cooperativeStdDev.getText()));
        mall.setExchangeMean(Integer.parseInt(exchangeMean.getText()));
        mall.setExchangeStdDev(Integer.parseInt(exchangeStdDev.getText()));
        mall.setFieldOfView(Integer.parseInt(fieldOfView.getText()));
        mall.setMAX_FAMILY(Integer.parseInt(maxFamily.getText()));
        mall.setMAX_FRIENDS(Integer.parseInt(maxFriend.getText()));
        mall.setMAX_COUPLE(Integer.parseInt(maxCouple.getText()));
        mall.setMAX_ALONE(Integer.parseInt(maxAlone.getText()));
    }

    public boolean validateParameters(){
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxFamily.getText()) >= 0 && Integer.parseInt(maxFriend.getText()) >= 0
                && Integer.parseInt(maxCouple.getText()) >= 0 && Integer.parseInt(maxAlone.getText()) >= 0;
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