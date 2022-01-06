package com.socialsim.controller.mall.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.mall.graphics.MallGraphicsController;
import com.socialsim.controller.mall.graphics.amenity.mapper.*;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchfield.*;
import com.socialsim.model.core.environment.mall.patchobject.passable.gate.MallGate;
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

    private final double CANVAS_SCALE = 0.3;

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
    }

    @FXML
    public void initializeAction() {
        int width = 60; // Value may be from 25-100
        int length = 130; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 130 columns
        Mall mall = Mall.MallFactory.create(rows, columns);
        initializeMall(mall);
        setElements();
    }

    public void initializeMall(Mall mall) {
        Main.mallSimulator.resetToDefaultConfiguration(mall);
        MallGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.mallSimulator.getMall().getRows();
        mapMall();
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
        for (int i = 0; i < rows; i++) {
            for (int j = 120; j < 130; j++) {
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
        for (int i = 45; i < 60; i++) {
            for (int j = 52; j < 72; j++) {
                resto1Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getRestaurants().add(Restaurant.restaurantFactory.create(resto1Patches, 1));

        List<Patch> dining1Patches = new ArrayList<>();
        for (int i = 40; i < 45; i++) {
            for (int j = 52; j < 72; j++) {
                dining1Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getDinings().add(Dining.diningFactory.create(dining1Patches, 1));

        List<Patch> resto2Patches = new ArrayList<>();
        for (int i = 45; i < 60; i++) {
            for (int j = 74; j < 94; j++) {
                resto2Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getRestaurants().add(Restaurant.restaurantFactory.create(resto2Patches, 2));

        List<Patch> dining2Patches = new ArrayList<>();
        for (int i = 40; i < 45; i++) {
            for (int j = 74; j < 94; j++) {
                dining2Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getDinings().add(Dining.diningFactory.create(dining2Patches, 2));

        List<Patch> dining3Patches = new ArrayList<>();
        for (int i = 25; i < 35; i++) {
            for (int j = 96; j < 116; j++) {
                dining3Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getDinings().add(Dining.diningFactory.create(dining3Patches, 3));

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

        List<Patch> store12Patches = new ArrayList<>();
        for (int i = 17; i < 29; i++) {
            for (int j = 120; j < 130; j++) {
                store12Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store12Patches, 12));

        List<Patch> store13Patches = new ArrayList<>();
        for (int i = 31; i < 43; i++) {
            for (int j = 120; j < 130; j++) {
                store13Patches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getStores().add(Store.storeFactory.create(store13Patches, 13));

        List<Patch> showcasePatches = new ArrayList<>();
        for (int i = 18; i < 37; i++) {
            for (int j = 52; j < 92; j++) {
                showcasePatches.add(mall.getPatch(i, j));
            }
        }
        Main.mallSimulator.getMall().getShowcases().add(Showcase.showcaseFactory.create(showcasePatches, 1));

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
        kioskPatches.add(mall.getPatch(21,53));
        kioskPatches.add(mall.getPatch(21,70));
        kioskPatches.add(mall.getPatch(21,87));
        kioskPatches.add(mall.getPatch(32,53));
        kioskPatches.add(mall.getPatch(32,70));
        kioskPatches.add(mall.getPatch(32,87));
        kioskPatches.add(mall.getPatch(26,97));
        KioskMapper.draw(kioskPatches);

        List<Patch> storeCounterPatches = new ArrayList<>();
        storeCounterPatches.add(mall.getPatch(19,121));
        storeCounterPatches.add(mall.getPatch(33,121));
        storeCounterPatches.add(mall.getPatch(2,52));
        storeCounterPatches.add(mall.getPatch(2,63));
        storeCounterPatches.add(mall.getPatch(2,81));
        storeCounterPatches.add(mall.getPatch(2,99));
        storeCounterPatches.add(mall.getPatch(2,110));
        storeCounterPatches.add(mall.getPatch(57,97));
        storeCounterPatches.add(mall.getPatch(57,109));
        storeCounterPatches.add(mall.getPatch(12,16));
        storeCounterPatches.add(mall.getPatch(7,38));
        storeCounterPatches.add(mall.getPatch(47,16));
        storeCounterPatches.add(mall.getPatch(52,38));
        StoreCounterMapper.draw(storeCounterPatches);

        List<Patch> tableUpPatches = new ArrayList<>();
        tableUpPatches.add(mall.getPatch(47,52));
        tableUpPatches.add(mall.getPatch(47,58));
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
        tableUpPatches.add(mall.getPatch(56,70));
        tableUpPatches.add(mall.getPatch(47,74));
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
        tableUpPatches.add(mall.getPatch(56,92));
        tableUpPatches.add(mall.getPatch(27,103));
        tableUpPatches.add(mall.getPatch(27,107));
        tableUpPatches.add(mall.getPatch(27,111));
        TableMapper.draw(tableUpPatches, "UP");

        List<Patch> tableRightPatches = new ArrayList<>();
        tableRightPatches.add(mall.getPatch(42,56));
        tableRightPatches.add(mall.getPatch(42,59));
        tableRightPatches.add(mall.getPatch(42,62));
        tableRightPatches.add(mall.getPatch(42,65));
        tableRightPatches.add(mall.getPatch(42,68));
        tableRightPatches.add(mall.getPatch(42,78));
        tableRightPatches.add(mall.getPatch(42,81));
        tableRightPatches.add(mall.getPatch(42,84));
        tableRightPatches.add(mall.getPatch(42,87));
        tableRightPatches.add(mall.getPatch(42,90));
        tableRightPatches.add(mall.getPatch(31,97));
        tableRightPatches.add(mall.getPatch(31,100));
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
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.mallSimulator.getSimulationTime().getTime();
        long elapsedTime = Main.mallSimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS);
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Elapsed time: " + timeString + " (" + elapsedTime + " s)");
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

        // Clear all agents
//        clearMall(Main.simulator.getMall());

        drawMallViewForeground(Main.mallSimulator.getMall(), false); // Redraw the canvas

        if (Main.mallSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    @Override
    protected void closeAction() {
    }

}