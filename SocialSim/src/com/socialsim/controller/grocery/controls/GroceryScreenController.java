package com.socialsim.controller.grocery.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.grocery.graphics.GroceryGraphicsController;
import com.socialsim.controller.grocery.graphics.amenity.mapper.*;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
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

public class GroceryScreenController extends ScreenController {

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

    private final double CANVAS_SCALE = 0.3;

    public GroceryScreenController() {
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
        int length = 100; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 130 columns
        Grocery grocery = Grocery.GroceryFactory.create(rows, columns);
        initializeGrocery(grocery);
        setElements();
    }

    public void initializeGrocery(Grocery grocery) {
        Main.grocerySimulator.resetToDefaultConfiguration(grocery);
        GroceryGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.grocerySimulator.getGrocery().getRows();
        mapGrocery();
        drawInterface();
    }

    public void mapGrocery() {
        Grocery grocery = Main.grocerySimulator.getGrocery();
        int rows = grocery.getRows();
        int cols = grocery.getColumns();

        List<Patch> wallPatches = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            wallPatches.add(grocery.getPatch(i, 0));
        }
        for (int i = 0; i < rows; i++) {
            wallPatches.add(grocery.getPatch(i, 99));
        }
        for (int j = 0; j < cols; j++) {
            wallPatches.add(grocery.getPatch(0, j));
        }
        for (int j = 0; j < cols; j++) {
            if (j < 44 || j > 55) {
                wallPatches.add(grocery.getPatch(59, j));
            }
        }
        for (int j = 0; j < 16; j++) {
            wallPatches.add(grocery.getPatch(43, j));
        }
        for (int i = 43; i < 47; i++) {
            for (int j = 15; j < 64; j ++) {
                if (j == 15 || j == 21 || j == 27 || j == 33 || j == 39 || j == 45 || j == 51 || j == 57 || j == 63) {
                    wallPatches.add(grocery.getPatch(i, j));
                }
            }
        }
        Main.grocerySimulator.getGrocery().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> cashierCounterPatches = new ArrayList<>();
        cashierCounterPatches.add(grocery.getPatch(43,19));
        cashierCounterPatches.add(grocery.getPatch(43,25));
        cashierCounterPatches.add(grocery.getPatch(43,31));
        cashierCounterPatches.add(grocery.getPatch(43,37));
        cashierCounterPatches.add(grocery.getPatch(43,43));
        cashierCounterPatches.add(grocery.getPatch(43,49));
        cashierCounterPatches.add(grocery.getPatch(43,55));
        cashierCounterPatches.add(grocery.getPatch(43,61));
        CashierCounterMapper.draw(cashierCounterPatches);

        List<Patch> cartRepoPatches = new ArrayList<>();
        cartRepoPatches.add(grocery.getPatch(43,65));
        cartRepoPatches.add(grocery.getPatch(43,68));
        cartRepoPatches.add(grocery.getPatch(43,71));
        CartRepoMapper.draw(cartRepoPatches);

        List<Patch> freshProductsPatches = new ArrayList<>();
        freshProductsPatches.add(grocery.getPatch(22,9));
        freshProductsPatches.add(grocery.getPatch(28,9));
        freshProductsPatches.add(grocery.getPatch(22,20));
        freshProductsPatches.add(grocery.getPatch(28,20));
        FreshProductsMapper.draw(freshProductsPatches);

        List<Patch> frozenProductsPatches = new ArrayList<>();
        frozenProductsPatches.add(grocery.getPatch(10,9));
        frozenProductsPatches.add(grocery.getPatch(16,9));
        frozenProductsPatches.add(grocery.getPatch(10,20));
        frozenProductsPatches.add(grocery.getPatch(16,20));
        FrozenProductsMapper.draw(frozenProductsPatches);

        List<Patch> frozenWallPatches = new ArrayList<>();
        frozenWallPatches.add(grocery.getPatch(6,1));
        frozenWallPatches.add(grocery.getPatch(15,1));
        FrozenWallMapper.draw(frozenWallPatches);

        List<Patch> groceryGateExitPatches = new ArrayList<>();
        groceryGateExitPatches.add(grocery.getPatch(59,44));
        GroceryGateMapper.draw(groceryGateExitPatches, GroceryGate.GroceryGateMode.EXIT);

        List<Patch> groceryGateEntrancePatches = new ArrayList<>();
        groceryGateEntrancePatches.add(grocery.getPatch(59,52));
        GroceryGateMapper.draw(groceryGateEntrancePatches, GroceryGate.GroceryGateMode.ENTRANCE);

        List<Patch> meatSectionPatches = new ArrayList<>();
        meatSectionPatches.add(grocery.getPatch(25,1));
        meatSectionPatches.add(grocery.getPatch(34,1));
        MeatSectionMapper.draw(meatSectionPatches);

        List<Patch> productAislePatches = new ArrayList<>();
        productAislePatches.add(grocery.getPatch(10,31));
        productAislePatches.add(grocery.getPatch(16,31));
        productAislePatches.add(grocery.getPatch(22,31));
        productAislePatches.add(grocery.getPatch(28,31));
        productAislePatches.add(grocery.getPatch(10,58));
        productAislePatches.add(grocery.getPatch(16,58));
        productAislePatches.add(grocery.getPatch(22,58));
        productAislePatches.add(grocery.getPatch(28,58));
        productAislePatches.add(grocery.getPatch(10,83));
        productAislePatches.add(grocery.getPatch(16,83));
        productAislePatches.add(grocery.getPatch(22,83));
        productAislePatches.add(grocery.getPatch(28,83));
        ProductAisleMapper.draw(productAislePatches);

        List<Patch> productShelfPatches = new ArrayList<>();
        productShelfPatches.add(grocery.getPatch(10,47));
        productShelfPatches.add(grocery.getPatch(16,47));
        productShelfPatches.add(grocery.getPatch(22,47));
        productShelfPatches.add(grocery.getPatch(28,47));
        productShelfPatches.add(grocery.getPatch(10,73));
        productShelfPatches.add(grocery.getPatch(16,73));
        productShelfPatches.add(grocery.getPatch(22,73));
        productShelfPatches.add(grocery.getPatch(28,73));
        productShelfPatches.add(grocery.getPatch(34,9));
        productShelfPatches.add(grocery.getPatch(34,20));
        productShelfPatches.add(grocery.getPatch(34,31));
        productShelfPatches.add(grocery.getPatch(34,42));
        productShelfPatches.add(grocery.getPatch(34,53));
        productShelfPatches.add(grocery.getPatch(34,64));
        productShelfPatches.add(grocery.getPatch(34,75));
        productShelfPatches.add(grocery.getPatch(34,86));
        ProductShelfMapper.draw(productShelfPatches);

        List<Patch> productWallDownPatches = new ArrayList<>();
        productWallDownPatches.add(grocery.getPatch(0,5));
        productWallDownPatches.add(grocery.getPatch(0,14));
        productWallDownPatches.add(grocery.getPatch(0,23));
        productWallDownPatches.add(grocery.getPatch(0,32));
        productWallDownPatches.add(grocery.getPatch(0,41));
        productWallDownPatches.add(grocery.getPatch(0,51));
        productWallDownPatches.add(grocery.getPatch(0,60));
        productWallDownPatches.add(grocery.getPatch(0,69));
        productWallDownPatches.add(grocery.getPatch(0,78));
        productWallDownPatches.add(grocery.getPatch(0,87));
        ProductWallMapper.draw(productWallDownPatches, "DOWN");

        List<Patch> productWallLeftPatches = new ArrayList<>();
        productWallLeftPatches.add(grocery.getPatch(6,98));
        productWallLeftPatches.add(grocery.getPatch(15,98));
        productWallLeftPatches.add(grocery.getPatch(25,98));
        productWallLeftPatches.add(grocery.getPatch(34,98));
        ProductWallMapper.draw(productWallLeftPatches, "LEFT");

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(grocery.getPatch(56,53));
        SecurityMapper.draw(securityPatches);

        List<Patch> serviceCounterPatches = new ArrayList<>();
        serviceCounterPatches.add(grocery.getPatch(44,4));
        serviceCounterPatches.add(grocery.getPatch(44,8));
        serviceCounterPatches.add(grocery.getPatch(44,12));
        ServiceCounterMapper.draw(serviceCounterPatches);

        List<Patch> stallPatches = new ArrayList<>();
        stallPatches.add(grocery.getPatch(58,8));
        stallPatches.add(grocery.getPatch(58,17));
        stallPatches.add(grocery.getPatch(58,26));
        stallPatches.add(grocery.getPatch(58,35));
        stallPatches.add(grocery.getPatch(58,63));
        stallPatches.add(grocery.getPatch(58,72));
        stallPatches.add(grocery.getPatch(58,81));
        stallPatches.add(grocery.getPatch(58,90));
        StallMapper.draw(stallPatches);

        List<Patch> tablePatches = new ArrayList<>();
        tablePatches.add(grocery.getPatch(52,5));
        tablePatches.add(grocery.getPatch(55,8));
        tablePatches.add(grocery.getPatch(52,11));
        tablePatches.add(grocery.getPatch(55,14));
        tablePatches.add(grocery.getPatch(52,17));
        tablePatches.add(grocery.getPatch(55,20));
        tablePatches.add(grocery.getPatch(52,23));
        tablePatches.add(grocery.getPatch(55,26));
        tablePatches.add(grocery.getPatch(52,29));
        tablePatches.add(grocery.getPatch(55,32));
        tablePatches.add(grocery.getPatch(52,35));
        tablePatches.add(grocery.getPatch(52,60));
        tablePatches.add(grocery.getPatch(55,63));
        tablePatches.add(grocery.getPatch(52,66));
        tablePatches.add(grocery.getPatch(55,69));
        tablePatches.add(grocery.getPatch(52,72));
        tablePatches.add(grocery.getPatch(55,75));
        tablePatches.add(grocery.getPatch(52,78));
        tablePatches.add(grocery.getPatch(55,81));
        tablePatches.add(grocery.getPatch(52,84));
        tablePatches.add(grocery.getPatch(55,87));
        tablePatches.add(grocery.getPatch(52,90));
        TableMapper.draw(tablePatches);
    }

    private void drawInterface() {
        drawGroceryViewBackground(Main.grocerySimulator.getGrocery()); // Initially draw the Grocery environment
        drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), false); // Then draw the agents in the Grocery
    }

    public void drawGroceryViewBackground(Grocery grocery) { // Draw the grocery view background
        GroceryGraphicsController.requestDrawGroceryView(stackPane, grocery, GroceryGraphicsController.tileSize, true, false);
    }

    public void drawGroceryViewForeground(Grocery grocery, boolean speedAware) { // Draw the grocery view foreground
        GroceryGraphicsController.requestDrawGroceryView(stackPane, grocery, GroceryGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.grocerySimulator.getSimulationTime().getTime();
        long elapsedTime = Main.grocerySimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS);
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Elapsed time: " + timeString + " (" + elapsedTime + " s)");
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.grocerySimulator.getGrocery().getRows() * GroceryGraphicsController.tileSize;
        double columnsScaled = Main.grocerySimulator.getGrocery().getColumns() * GroceryGraphicsController.tileSize;

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
        if (!Main.grocerySimulator.isRunning()) { // Not yet running to running (play simulation)
            Main.grocerySimulator.setRunning(true);
            Main.grocerySimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.grocerySimulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        Main.grocerySimulator.reset();

        // Clear all agents
//        clearGrocery(Main.simulator.getGrocery());

        drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), false); // Redraw the canvas

        if (Main.grocerySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    @Override
    protected void closeAction() {
    }

}