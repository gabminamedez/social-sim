package com.socialsim.controller.mall.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.mall.graphics.MallGraphicsController;
import com.socialsim.controller.mall.graphics.amenity.mapper.*;
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

    @FXML private StackPane stackPane;
    @FXML private Canvas backgroundCanvas;
    @FXML private Canvas foregroundCanvas;
    @FXML private Canvas markingsCanvas;
    @FXML private Text elapsedTimeText;
    @FXML private ToggleButton playButton;
    @FXML private Slider speedSlider;
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

        int width = 60;
        int length = 120;
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS);
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
        if (validateParameters()) {
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

        List<Patch> store10Patches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 96; j < 106; j++) {
                store10Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store10Patches, 10));

        List<Patch> fBathroomPatches2 = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 109; j < 119; j++) {
                fBathroomPatches2.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getBathrooms().add(Bathroom.bathroomFactory.create(fBathroomPatches2, 1));

        List<Patch> mBathroomPatches2 = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 108; j < 118; j++) {
                mBathroomPatches2.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getBathrooms().add(Bathroom.bathroomFactory.create(mBathroomPatches2, 2));

        List<Patch> showcasePatches = new ArrayList<>();
        for (int i = 18; i < 37; i++) {
            for (int j = 52; j < 92; j++) {
                showcasePatches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getShowcases().add(Showcase.showcaseFactory.create(showcasePatches, 1));

        List<Patch> storeCounterPatches = new ArrayList<>();
        storeCounterPatches.add(mall.getPatch(11,16));
        storeCounterPatches.add(mall.getPatch(6,38));
        storeCounterPatches.add(mall.getPatch(47,16));
        storeCounterPatches.add(mall.getPatch(52,38));
        storeCounterPatches.add(mall.getPatch(1,52));
        storeCounterPatches.add(mall.getPatch(1,63));
        storeCounterPatches.add(mall.getPatch(1,81));
        storeCounterPatches.add(mall.getPatch(1,99));
        storeCounterPatches.add(mall.getPatch(57,97));
        StoreCounterMapper.draw(storeCounterPatches);

        List<Patch> aisleDownPatches = new ArrayList<>(); // 0 - 26
        aisleDownPatches.add(mall.getPatch(16,19));
        aisleDownPatches.add(mall.getPatch(19,19));
        aisleDownPatches.add(mall.getPatch(11,38));
        aisleDownPatches.add(mall.getPatch(11,43));
        aisleDownPatches.add(mall.getPatch(17,38));
        aisleDownPatches.add(mall.getPatch(17,43));
        aisleDownPatches.add(mall.getPatch(40,12));
        aisleDownPatches.add(mall.getPatch(40,18));
        aisleDownPatches.add(mall.getPatch(40,24));
        aisleDownPatches.add(mall.getPatch(43,12));
        aisleDownPatches.add(mall.getPatch(43,18));
        aisleDownPatches.add(mall.getPatch(43,24));
        aisleDownPatches.add(mall.getPatch(43,38));
        aisleDownPatches.add(mall.getPatch(46,38));
        aisleDownPatches.add(mall.getPatch(43,43));
        aisleDownPatches.add(mall.getPatch(46,43));
        aisleDownPatches.add(mall.getPatch(6,54));
        aisleDownPatches.add(mall.getPatch(12,54));
        aisleDownPatches.add(mall.getPatch(6,65));
        aisleDownPatches.add(mall.getPatch(12,65));
        aisleDownPatches.add(mall.getPatch(6,83));
        aisleDownPatches.add(mall.getPatch(9,83));
        aisleDownPatches.add(mall.getPatch(12,83));
        aisleDownPatches.add(mall.getPatch(6,101));
        aisleDownPatches.add(mall.getPatch(12,101));
        aisleDownPatches.add(mall.getPatch(47,99));
        aisleDownPatches.add(mall.getPatch(53,99));
        StoreAisleMapper.draw(aisleDownPatches, "DOWN");

        List<Patch> aisleRightPatches = new ArrayList<>(); // 27 - 47
        aisleRightPatches.add(mall.getPatch(16,10));
        aisleRightPatches.add(mall.getPatch(16,13));
        aisleRightPatches.add(mall.getPatch(16,16));
        aisleRightPatches.add(mall.getPatch(16,26));
        aisleRightPatches.add(mall.getPatch(16,29));
        aisleRightPatches.add(mall.getPatch(12,35));
        aisleRightPatches.add(mall.getPatch(12,49));
        aisleRightPatches.add(mall.getPatch(43,35));
        aisleRightPatches.add(mall.getPatch(45,49));
        aisleRightPatches.add(mall.getPatch(8,51));
        aisleRightPatches.add(mall.getPatch(8,60));
        aisleRightPatches.add(mall.getPatch(8,62));
        aisleRightPatches.add(mall.getPatch(8,71));
        aisleRightPatches.add(mall.getPatch(8,75));
        aisleRightPatches.add(mall.getPatch(8,94));
        aisleRightPatches.add(mall.getPatch(8,79));
        aisleRightPatches.add(mall.getPatch(8,90));
        aisleRightPatches.add(mall.getPatch(8,98));
        aisleRightPatches.add(mall.getPatch(8,107));
        aisleRightPatches.add(mall.getPatch(47,96));
        aisleRightPatches.add(mall.getPatch(47,105));
        StoreAisleMapper.draw(aisleRightPatches, "RIGHT");

        List<Patch> mallGateExitPatches = new ArrayList<>();
        mallGateExitPatches.add(mall.getPatch(24,0));
        mallGateExitPatches.add(mall.getPatch(24,119));
        MallGateMapper.draw(mallGateExitPatches, MallGate.MallGateMode.EXIT);

        List<Patch> mallGateEntrancePatches = new ArrayList<>();
        mallGateEntrancePatches.add(mall.getPatch(32,0));
        mallGateEntrancePatches.add(mall.getPatch(32,119));
        MallGateMapper.draw(mallGateEntrancePatches, MallGate.MallGateMode.ENTRANCE);

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(mall.getPatch(32,2));
        SecurityMapper.draw(securityPatches);

        List<Patch> digitalPatches = new ArrayList<>();
        digitalPatches.add(mall.getPatch(27,10));
        digitalPatches.add(mall.getPatch(27,94));
        DigitalMapper.draw(digitalPatches);

        List<Patch> kioskPatches = new ArrayList<>();
        kioskPatches.add(mall.getPatch(26,97));
        kioskPatches.add(mall.getPatch(28,28));
        kioskPatches.add(mall.getPatch(21,53));
        kioskPatches.add(mall.getPatch(21,70));
        kioskPatches.add(mall.getPatch(21,87));
        kioskPatches.add(mall.getPatch(32,53));
        kioskPatches.add(mall.getPatch(32,70));
        kioskPatches.add(mall.getPatch(32,87));
        KioskMapper.draw(kioskPatches);

        List<Patch> tableUpPatches = new ArrayList<>();
        tableUpPatches.add(mall.getPatch(47,52)); // 0
        tableUpPatches.add(mall.getPatch(47,58)); // 1
        tableUpPatches.add(mall.getPatch(47,64));
        tableUpPatches.add(mall.getPatch(47,70));
        tableUpPatches.add(mall.getPatch(50,52));
        tableUpPatches.add(mall.getPatch(50,58));
        tableUpPatches.add(mall.getPatch(50,64));
        tableUpPatches.add(mall.getPatch(50,70));
        tableUpPatches.add(mall.getPatch(53,52));
        tableUpPatches.add(mall.getPatch(53,58));
        tableUpPatches.add(mall.getPatch(53,64));
        tableUpPatches.add(mall.getPatch(53,70));
        tableUpPatches.add(mall.getPatch(56,52));
        tableUpPatches.add(mall.getPatch(56,58));
        tableUpPatches.add(mall.getPatch(56,64));
        tableUpPatches.add(mall.getPatch(56,70)); // 15

        tableUpPatches.add(mall.getPatch(47,74)); // 16
        tableUpPatches.add(mall.getPatch(47,80));
        tableUpPatches.add(mall.getPatch(47,86));
        tableUpPatches.add(mall.getPatch(47,92));
        tableUpPatches.add(mall.getPatch(50,74));
        tableUpPatches.add(mall.getPatch(50,80));
        tableUpPatches.add(mall.getPatch(50,86));
        tableUpPatches.add(mall.getPatch(50,92));
        tableUpPatches.add(mall.getPatch(53,74));
        tableUpPatches.add(mall.getPatch(53,80));
        tableUpPatches.add(mall.getPatch(53,86));
        tableUpPatches.add(mall.getPatch(53,92));
        tableUpPatches.add(mall.getPatch(56,74));
        tableUpPatches.add(mall.getPatch(56,80));
        tableUpPatches.add(mall.getPatch(56,86));
        tableUpPatches.add(mall.getPatch(56,92)); // 31

        tableUpPatches.add(mall.getPatch(27,103)); // 32
        tableUpPatches.add(mall.getPatch(27,107));
        tableUpPatches.add(mall.getPatch(27,111)); // 34
        TableMapper.draw(tableUpPatches, "UP");

        List<Patch> tableRightPatches = new ArrayList<>();
        tableRightPatches.add(mall.getPatch(42,54));
        tableRightPatches.add(mall.getPatch(42,58));
        tableRightPatches.add(mall.getPatch(42,62));
        tableRightPatches.add(mall.getPatch(42,66));
        tableRightPatches.add(mall.getPatch(42,70));

        tableRightPatches.add(mall.getPatch(42,76));
        tableRightPatches.add(mall.getPatch(42,80));
        tableRightPatches.add(mall.getPatch(42,84));
        tableRightPatches.add(mall.getPatch(42,88));
        tableRightPatches.add(mall.getPatch(42,92));

        tableRightPatches.add(mall.getPatch(31,103));
        tableRightPatches.add(mall.getPatch(31,106));
        tableRightPatches.add(mall.getPatch(31,109));
        tableRightPatches.add(mall.getPatch(31,112));
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
        plantPatches.add(mall.getPatch(27,61));
        plantPatches.add(mall.getPatch(28,61));
        plantPatches.add(mall.getPatch(27,62));
        plantPatches.add(mall.getPatch(28,62));
        plantPatches.add(mall.getPatch(27,63));
        plantPatches.add(mall.getPatch(28,63));
        plantPatches.add(mall.getPatch(27,79));
        plantPatches.add(mall.getPatch(28,79));
        plantPatches.add(mall.getPatch(27,80));
        plantPatches.add(mall.getPatch(28,80));
        plantPatches.add(mall.getPatch(27,81));
        plantPatches.add(mall.getPatch(28,81));
        PlantMapper.draw(plantPatches);

        List<Patch> benchUpPatches = new ArrayList<>();
        benchUpPatches.add(mall.getPatch(28,18));
        benchUpPatches.add(mall.getPatch(31,18));
        benchUpPatches.add(mall.getPatch(28,38));
        benchUpPatches.add(mall.getPatch(31,38));
        benchUpPatches.add(mall.getPatch(20,100));
        benchUpPatches.add(mall.getPatch(20,105));
        benchUpPatches.add(mall.getPatch(26,61));
        benchUpPatches.add(mall.getPatch(26,79));
        benchUpPatches.add(mall.getPatch(29,61));
        benchUpPatches.add(mall.getPatch(29,79));
        benchUpPatches.add(mall.getPatch(39,100));
        benchUpPatches.add(mall.getPatch(39,105));
        BenchMapper.draw(benchUpPatches, "UP");

        List<Patch> benchRightPatches = new ArrayList<>();
        benchRightPatches.add(mall.getPatch(28,17));
        benchRightPatches.add(mall.getPatch(28,21));
        benchRightPatches.add(mall.getPatch(28,37));
        benchRightPatches.add(mall.getPatch(28,41));
        benchRightPatches.add(mall.getPatch(26,60));
        benchRightPatches.add(mall.getPatch(26,64));
        benchRightPatches.add(mall.getPatch(26,78));
        benchRightPatches.add(mall.getPatch(26,82));
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
        toiletPatches.add(mall.getPatch(2, 109));
        toiletPatches.add(mall.getPatch(4, 109));
        toiletPatches.add(mall.getPatch(6, 109));
        toiletPatches.add(mall.getPatch(8, 109));
        toiletPatches.add(mall.getPatch(10, 109));
        toiletPatches.add(mall.getPatch(12, 109));
        toiletPatches.add(mall.getPatch(47, 108));
        toiletPatches.add(mall.getPatch(49, 108));
        toiletPatches.add(mall.getPatch(51, 108));
        toiletPatches.add(mall.getPatch(53, 108));
        toiletPatches.add(mall.getPatch(55, 108));
        toiletPatches.add(mall.getPatch(57, 108));
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
        sinkPatches.add(mall.getPatch(2, 118));
        sinkPatches.add(mall.getPatch(4, 118));
        sinkPatches.add(mall.getPatch(6, 118));
        sinkPatches.add(mall.getPatch(8, 118));
        sinkPatches.add(mall.getPatch(10, 118));
        sinkPatches.add(mall.getPatch(12, 118));
        sinkPatches.add(mall.getPatch(47, 117));
        sinkPatches.add(mall.getPatch(49, 117));
        sinkPatches.add(mall.getPatch(51, 117));
        sinkPatches.add(mall.getPatch(53, 117));
        sinkPatches.add(mall.getPatch(55, 117));
        sinkPatches.add(mall.getPatch(57, 117));
        SinkMapper.draw(sinkPatches);
    }

    private void drawInterface() {
        drawMallViewBackground(Main.mallSimulator.getMall());
        drawMallViewForeground(Main.mallSimulator.getMall(), false);
    }

    public void drawMallViewBackground(Mall mall) {
        MallGraphicsController.requestDrawMallView(stackPane, mall, MallGraphicsController.tileSize, true, false);
    }

    public void drawMallViewForeground(Mall mall, boolean speedAware) {
        MallGraphicsController.requestDrawMallView(stackPane, mall, MallGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() {
        Platform.runLater(this::updateSimulationTime);
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
        if (!Main.mallSimulator.isRunning()) {
            Main.mallSimulator.setRunning(true);
            Main.mallSimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.mallSimulator.setRunning(false);
            playButton.setText("Play");
        }
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

    @Override
    protected void closeAction() {
    }

    public void resetToDefault() {
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

    public void openIOSLevels() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/MallConfigureIOS.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/MallEditInteractions.fxml"));
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
    public void configureParameters(Mall mall) {
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

    public boolean validateParameters() {
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxFamily.getText()) >= 0 && Integer.parseInt(maxFriend.getText()) >= 0
                && Integer.parseInt(maxCouple.getText()) >= 0 && Integer.parseInt(maxAlone.getText()) >= 0;

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